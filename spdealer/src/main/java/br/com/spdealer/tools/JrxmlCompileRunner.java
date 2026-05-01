package br.com.spdealer.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

/**
 * Utility to compile a JRXML file and print detailed context when XML parsing errors occur.
 * Usage: java -cp ... br.com.spdealer.tools.JrxmlCompileRunner path/to/report.jrxml
 */
public class JrxmlCompileRunner {
    public static void main(String[] args) {
        // If the environment variable JR_COMPILER_CP is set, forward it to JasperReports
        String jrCp = System.getenv("JR_COMPILER_CP");
        if (jrCp != null && !jrCp.isEmpty()) {
            System.setProperty("net.sf.jasperreports.compiler.classpath", jrCp);
            System.out.println("Set net.sf.jasperreports.compiler.classpath from JR_COMPILER_CP");
        }

        if (args.length == 0) {
            System.err.println("Usage: JrxmlCompileRunner <path-to-jrxml>");
            System.exit(1);
        }

        String jrxmlPath = args[0];
        System.out.println("Compiling JRXML: " + jrxmlPath);

        try {
            JasperCompileManager.compileReportToFile(jrxmlPath);
            System.out.println("Compilation successful: " + jrxmlPath);
            System.exit(0);
        } catch (JRException e) {
            System.err.println("JRException: " + e.getMessage());
            Throwable cause = e.getCause();
            boolean handled = false;
            while (cause != null) {
                System.err.println("Cause: " + cause.getClass().getName() + " - " + cause.getMessage());
                // Check for SAXParseException by name to avoid compile-time dependency on parser impl
                if (cause.getClass().getName().endsWith("SAXParseException")) {
                    handled = true;
                    try {
                        // reflectively attempt to get line/column
                        int line = (int) cause.getClass().getMethod("getLineNumber").invoke(cause);
                        int col = (int) cause.getClass().getMethod("getColumnNumber").invoke(cause);
                        System.err.println("SAXParseException at line=" + line + " column=" + col);
                        printFileContext(jrxmlPath, line, 6);
                    } catch (Exception ex) {
                        System.err.println("Erro ao obter detalhes do SAXParseException: " + ex.getMessage());
                    }
                }
                cause = cause.getCause();
            }

            if (!handled) {
                System.err.println("No SAXParseException found in causes; printing full stacktrace:");
                e.printStackTrace(System.err);
            }

            System.exit(2);
        } catch (Throwable t) {
            System.err.println("Unexpected error: " + t.getMessage());
            t.printStackTrace(System.err);
            System.exit(3);
        }
    }

    private static void printFileContext(String path, int errorLine, int contextLines) {
        File f = new File(path);
        if (!f.exists()) {
            System.err.println("File not found: " + path);
            return;
        }

        int start = Math.max(1, errorLine - contextLines);
        int end = errorLine + contextLines;

        System.err.println("--- File context (" + path + ") lines " + start + ".." + end + " ---");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int ln = 0;
            while ((line = br.readLine()) != null) {
                ln++;
                if (ln < start) continue;
                if (ln > end) break;
                String prefix = (ln == errorLine) ? ">>> " : "    ";
                System.err.println(prefix + String.format("%4d", ln) + ": " + line);
            }
        } catch (IOException ioe) {
            System.err.println("Erro lendo arquivo para contexto: " + ioe.getMessage());
        }
    }
}
