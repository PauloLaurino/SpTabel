public class RunSingleSelo {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java RunSingleSelo <seloDigital>");
            System.exit(2);
        }
        String selo = args[0];
        boolean ok = com.selador.util.SeloJsonSanitizerNotas.sanitizarESalvar(selo);
        System.out.println("RESULT:" + ok);
    }
}
