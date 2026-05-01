package com.monitor.funarpen.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Ferramenta auxiliar para:
 * - carregar e descriptografar `db.properties` via `DbUtil`
 * - exibir valores (mascarando senhas)
 * - tentar consumir endpoints comuns de `parametros`
 */
public class ToolsFetchParametros {

    public static void main(String[] args) {
        try {
            System.out.println("[ToolsFetchParametros] Tentando carregar propriedades via DbUtil (reflexão)...");
            Properties p = null;
            try {
                Class<?> dbUtil = Class.forName("com.monitor.funarpen.util.DbUtil");
                java.lang.reflect.Method m = dbUtil.getMethod("loadProperties");
                Object r = m.invoke(null);
                if (r instanceof Properties) p = (Properties) r;
            } catch (ClassNotFoundException cnf) {
                System.out.println("[ToolsFetchParametros] DbUtil não disponível no classpath do módulo; fallback para variáveis/arquivo local.");
            }

            if (p == null || p.isEmpty()) {
                System.out.println("[ToolsFetchParametros] Nenhuma propriedade carregada (vazias).");
            } else {
                System.out.println("[ToolsFetchParametros] Propriedades carregadas:");
                for (String name : p.stringPropertyNames()) {
                    String v = p.getProperty(name);
                    if (name.toUpperCase().contains("PASS") || name.toUpperCase().contains("SENHA") || name.toUpperCase().contains("KEY")) {
                        System.out.printf("  %s = %s\n", name, mask(v));
                    } else {
                        System.out.printf("  %s = %s\n", name, v);
                    }
                }
            }

            // Candidate endpoints to try. If user provided an arg, prepend it.
            List<String> candidates = new ArrayList<>();
            if (args != null && args.length > 0 && args[0] != null && !args[0].isBlank()) {
                String base = args[0].trim();
                if (base.endsWith("/")) base = base.substring(0, base.length()-1);
                candidates.add(sanitizeConcat(base, "/maker/api/parametros"));
                candidates.add(sanitizeConcat(base, "/funarpen/maker/api/parametros"));
            }

            // Common local endpoints
            candidates.add("http://localhost:8080/funarpen/maker/api/parametros");
            candidates.add("http://localhost:8080/maker/api/parametros");
            candidates.add("http://localhost:8059/maker/api/parametros");
            candidates.add("http://localhost:5000/funarpen/maker/api/parametros");

            System.out.println("\n[ToolsFetchParametros] Tentando endpoints de parâmetros:");
            for (String url : candidates) {
                url = sanitizeUrl(url);
                try {
                    System.out.println("-> " + url);
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(5000);
                    int code = conn.getResponseCode();
                    System.out.println("   HTTP " + code);
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(
                            code >= 200 && code < 400 ? conn.getInputStream() : conn.getErrorStream()))) {
                        String line; int n = 0;
                        while ((line = br.readLine()) != null && n++ < 50) {
                            System.out.println("   " + line);
                        }
                        if (n == 50) System.out.println("   ... (truncated)");
                    }
                } catch (Exception ex) {
                    System.out.println("   erro: " + ex.getMessage());
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("[ToolsFetchParametros] Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String mask(String v) {
        if (v == null) return "null";
        if (v.length() <= 4) return "****";
        return "****" + v.substring(v.length()-4);
    }

    private static String sanitizeConcat(String base, String path) {
        if (base == null) return path;
        String b = base.trim();
        if (b.endsWith("/")) b = b.substring(0, b.length()-1);
        if (!path.startsWith("/")) path = "/" + path;
        return b + path;
    }

    private static String sanitizeUrl(String urlStr) {
        if (urlStr == null) return null;
        try {
            URL u = new URL(urlStr);
            String host = u.getHost();
            int port = u.getPort();
            String path = u.getPath();
            if (path != null) {
                String hostPort = host + (port == -1 ? "" : (":" + port));
                if (path.startsWith("/" + hostPort + "/")) {
                    path = path.substring( ("/" + hostPort).length() );
                    String newUrl = u.getProtocol() + "://" + host + (port == -1 ? "" : (":"+port)) + path + (u.getQuery() != null ? "?"+u.getQuery() : "");
                    return newUrl;
                }
            }
        } catch (Exception e) {
            // ignore and return original
        }
        return urlStr;
    }
}
