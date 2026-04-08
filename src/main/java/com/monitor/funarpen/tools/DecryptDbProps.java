package com.monitor.funarpen.tools;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Utility to read a db.properties and master.key and print decrypted DB_URL, DB_USER, DB_PASS
 * Usage: mvn exec:java -Dexec.mainClass="com.monitor.funarpen.tools.DecryptDbProps" -Dexec.args="<propsPath> <masterKeyPath>"
 */
public class DecryptDbProps {
    public static void main(String[] args) throws Exception {
        String propsPath = args != null && args.length > 0 ? args[0] : "C:/ProgramData/spr/notas/db.properties";
        String masterPath = args != null && args.length > 1 ? args[1] : "C:/ProgramData/spr/master.key";

        Properties p = new Properties();
        File f = new File(propsPath);
        if (!f.exists()) {
            System.err.println("PROP_NOT_FOUND:" + propsPath);
            System.exit(2);
        }
        try (FileInputStream in = new FileInputStream(f)) {
            p.load(in);
        }

        boolean needDecrypt = false;
        for (String name : p.stringPropertyNames()) {
            String v = p.getProperty(name);
            if (v != null && v.startsWith("ENC(") && v.endsWith(")")) { needDecrypt = true; break; }
        }

        String master = null;
        File mk = new File(masterPath);
        if (mk.exists()) {
            try {
                byte[] b = Files.readAllBytes(mk.toPath());
                master = new String(b, StandardCharsets.UTF_8).trim();
                if (master.isEmpty()) master = null;
            } catch (Exception ex) {
                master = null;
            }
        }

        if (needDecrypt && master != null) {
            StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
            enc.setPassword(master);
            enc.setAlgorithm("PBEWithMD5AndDES");
            for (String name : p.stringPropertyNames()) {
                String v = p.getProperty(name);
                if (v != null && v.startsWith("ENC(") && v.endsWith(")")) {
                    String inner = v.substring(4, v.length() - 1);
                    try { String dec = enc.decrypt(inner); p.setProperty(name, dec); } catch (Exception ex) { p.setProperty(name, ""); }
                }
            }
        }

        // Print in KEY=VALUE form for easy parsing
        String url = p.getProperty("DB_URL", "");
        String user = p.getProperty("DB_USER", "");
        String pass = p.getProperty("DB_PASS", "");

        System.out.println("DB_URL=" + url);
        System.out.println("DB_USER=" + user);
        System.out.println("DB_PASS=" + pass);
    }
}
