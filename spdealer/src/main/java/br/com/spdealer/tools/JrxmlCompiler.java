package br.com.spdealer.tools;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintElement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * Utilitário para compilar JRXMLs recursivamente e testar preenchimento com JDBC
 */
public class JrxmlCompiler {
    public static void main(String[] args) {
        try {
            System.out.println("Compiling all JRXMLs under src/main/resources/reports...");
            File reportsRoot = new File("src/main/resources/reports");
            if (reportsRoot.exists()) {
                compileRecursively(reportsRoot);
            } else {
                System.out.println("Reports directory not found: " + reportsRoot.getAbsolutePath());
            }

            // apontar para o relatório Fluxo de Caixa (v2) compilado
            String reportJasper = "src/main/resources/reports/FluxoCaixaReport_v2.jasper";

            // DB properties (args[0]=url, args[1]=user, args[2]=pass)
            String dbUrl = null;
            String dbUser = null;
            String dbPass = null;

            if (args != null && args.length >= 3) {
                dbUrl = args[0];
                dbUser = args[1];
                dbPass = args[2];
            } else {
                dbUrl = System.getProperty("db.url");
                dbUser = System.getProperty("db.user");
                dbPass = System.getProperty("db.pass");
            }

            if (dbUrl == null || dbUser == null) {
                if (dbUrl == null) dbUrl = System.getenv("DB_URL");
                if (dbUser == null) dbUser = System.getenv("DB_USER");
                if (dbPass == null) dbPass = System.getenv("DB_PASS");
            }

            if (dbUrl != null && dbUser != null) {
                // Ajuste: converter URLs jdbc:mysql para jdbc:mariadb quando necessário
                if (dbUrl.startsWith("jdbc:mysql://")) {
                    System.out.println("Converting JDBC URL jdbc:mysql:// -> jdbc:mariadb:// for MariaDB driver compatibility.");
                    dbUrl = dbUrl.replaceFirst("jdbc:mysql://", "jdbc:mariadb://");
                }

                System.out.println("Filling report using DB: " + dbUrl + " user=" + dbUser);
                try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
                    Map<String,Object> params = new HashMap<>();
                    
                    // Parâmetros de data para teste: dia único 15/12/2025
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date testDate = sdf.parse("15/12/2025");
                    params.put("dataini", testDate); // usado por subreports (java.util.Date)
                    params.put("datafim", testDate);
                    // também enviar versões em String/param names esperadas pelo FluxoCaixaReport_v2
                    // Também enviar versões em objeto Date para subreports que esperam java.util.Date
                    params.put("DATA_INICIAL", testDate);
                    params.put("DATA_FINAL", testDate);
                    // Diretorio de subreports (relativo à resources/reports)
                    params.put("SUBREPORT_DIR", "reports/");
                    params.put("EMPRESA_NOME", "SPDealer - Sistema Integrado");
                    params.put("EMPRESA_CNPJ", "00.000.000/0001-00");
                    params.put("USUARIO", "Admin");
                    // Parâmetro de filial para subreports (usar '001' por padrão)
                    params.put("FILIAL", System.getenv().getOrDefault("REPORT_FILIAL", "001"));
                    // Permite pular execução de subreports pesados via variável de ambiente REPORT_RUN_SUBREPORTS
                    String runSub = System.getenv().get("REPORT_RUN_SUBREPORTS");
                    if (runSub != null && runSub.equalsIgnoreCase("false")) {
                        params.put("RUN_SUBREPORTS", Boolean.FALSE);
                    } else {
                        params.put("RUN_SUBREPORTS", Boolean.TRUE);
                    }

                    if (new File(reportJasper).exists()) {
                        try {
                            JasperPrint jp = JasperFillManager.fillReport(reportJasper, params, conn);
                            // Diagnostic: report page/element counts
                            int pageCount = jp.getPages() == null ? 0 : jp.getPages().size();
                            System.out.println("Diagnostic: JasperPrint pages = " + pageCount);
                            if (pageCount > 0) {
                                int pageIndex = 0;
                                for (JRPrintPage p : jp.getPages()) {
                                    int elems = p.getElements() == null ? 0 : p.getElements().size();
                                    System.out.println(" Page[" + pageIndex + "] elements = " + elems);
                                    pageIndex++;
                                }
                            }
                            String outPdf = "target/FluxoCaixa_20251215_" + System.currentTimeMillis() + ".pdf";
                            JasperExportManager.exportReportToPdfFile(jp, outPdf);
                            System.out.println("Report filled and exported to: " + outPdf);
                            System.out.println("Period: 15/12/2025 (single-day test)");
                        } catch (net.sf.jasperreports.engine.JRException jrEx) {
                            System.err.println("Filling with DB failed: " + jrEx.getMessage());
                            System.err.println("Attempting to fill report using empty data source to validate layout (skipping heavy subreports)...");
                            try {
                                // indicar aos JRXMLs que pulem subreports pesados
                                params.put("RUN_SUBREPORTS", Boolean.FALSE);
                                // keep DATA_INICIAL/DATA_FINAL as Date objects to avoid ClassCastException in subreports
                                net.sf.jasperreports.engine.JasperPrint jp = net.sf.jasperreports.engine.JasperFillManager.fillReport(reportJasper, params, new net.sf.jasperreports.engine.JREmptyDataSource());
                                String outPdf = "target/FluxoCaixa_20251215_emptyds_" + System.currentTimeMillis() + ".pdf";
                                net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jp, outPdf);
                                System.out.println("Report filled (empty datasource) and exported to: " + outPdf);
                            } catch (Exception ex2) {
                                System.err.println("Failed to fill report with empty datasource: " + ex2.getMessage());
                                ex2.printStackTrace(System.err);
                                throw ex2;
                            }
                        }
                    } else {
                        System.out.println("Main report jasper not found (skipping fill): " + reportJasper);
                    }
                }
            } else {
                System.out.println("DB properties not provided; skipping fill step.");
            }

            System.out.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void compileRecursively(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                compileRecursively(f);
            } else if (f.getName().toLowerCase().endsWith(".jrxml")) {
                String jrxmlPath = f.getPath().replace("\\", "/");
                String jasperPath = jrxmlPath.substring(0, jrxmlPath.length() - 6) + ".jasper";
                System.out.println("Compiling: " + jrxmlPath + " -> " + jasperPath);
                try {
                    JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
                    System.out.println("  OK: " + jasperPath);
                } catch (Exception ex) {
                    System.err.println("  ERROR compiling " + jrxmlPath + ": " + ex.getMessage());
                    // print stack trace short
                    ex.printStackTrace(System.err);
                    // continue compiling other files
                }
            }
        }
    }
}
