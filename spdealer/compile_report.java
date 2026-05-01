import net.sf.jasperreports.engine.JasperCompileManager;
public class compile_report {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java compile_report <jrxml_file>");
            System.exit(1);
        }
        String jrxml = args[0];
        String jasper = jrxml.replaceAll("\\.jrxml$", ".jasper");
        System.out.println("Compiling: " + jrxml + " -> " + jasper);
        JasperCompileManager.compileReportToFile(jrxml, jasper);
        System.out.println("Done!");
    }
}