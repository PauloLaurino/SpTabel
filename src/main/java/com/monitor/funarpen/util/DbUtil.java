package com.monitor.funarpen.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {
    // Caminhos de configuração por aplicação
    private static final String BASE_PATH = System.getProperty("os.name").toLowerCase().contains("win") 
        ? "C:\\ProgramData\\spr" 
        : "/etc/spr";
    private static final String NOTAS_CONFIG_PATH = BASE_PATH + "/notas/db.properties";
    private static final String PROTESTO_CONFIG_PATH = BASE_PATH + "/protesto/db.properties";
    private static final String DEFAULT_CONFIG_PATH = BASE_PATH + "/db.properties";
    private static final String DEFAULT_MASTER_KEY_PATH = BASE_PATH + "/master.key";
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/sptabel?serverTimezone=UTC&useSSL=false";

    public static Connection getConnection() throws SQLException {
        Properties p = loadProperties();

        String url = firstNonNull(p.getProperty("DB_URL"), System.getenv("DB_URL"));
        if (url == null) {
            url = DEFAULT_DB_URL;
        }
        String user = firstNonNull(p.getProperty("DB_USER"), System.getenv("DB_USER"));
        String pass = firstNonNull(p.getProperty("DB_PASS"), System.getenv("DB_PASS"));

        if (url == null || user == null) {
            throw new SQLException("DB_URL or DB_USER not defined in configuration");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver com.mysql.cj.jdbc.Driver not found on classpath", e);
        }

        return DriverManager.getConnection(url, user, pass == null ? "" : pass);
    }

    private static Properties loadProperties() {
        // Tentar caminho específico da aplicação primeiro
        String configPath = getApplicationConfigPath();
        
        Properties props = loadPropertiesFromFile(configPath);
        
        // Fallback para db.properties padrão
        if (props.isEmpty()) {
            props = loadPropertiesFromFile(DEFAULT_CONFIG_PATH);
        }
        
        return props;
    }
    
    /**
     * Detecta qual aplicação está em execução e retorna o caminho de configuração
     */
    private static String getApplicationConfigPath() {
        // Detectar pela URL do contexto ou pelo nome da aplicação
        String contextPath = System.getProperty("catalina.base", "");
        
        // Se tem "notas" no caminho, usa configuração do Notas
        if (contextPath.toLowerCase().contains("notas")) {
            return NOTAS_CONFIG_PATH;
        }
        // Se tem "protesto" ou "funarpen" no caminho, usa configuração do Protesto
        if (contextPath.toLowerCase().contains("protesto") || 
            contextPath.toLowerCase().contains("funarpen")) {
            return PROTESTO_CONFIG_PATH;
        }
        
        // Fallback: verificar variável de ambiente
        String appName = System.getenv("APP_NAME");
        if ("notas".equalsIgnoreCase(appName)) {
            return NOTAS_CONFIG_PATH;
        }
        if ("protesto".equalsIgnoreCase(appName) || "funarpen".equalsIgnoreCase(appName)) {
            return PROTESTO_CONFIG_PATH;
        }
        
        // Padrão: usar Notas (sptabel)
        return NOTAS_CONFIG_PATH;
    }
    
    /**
     * Carrega propriedades de um arquivo específico
     */
    private static Properties loadPropertiesFromFile(String configPath) {
        Properties props = new Properties();
        File f = new File(configPath);
        if (!f.exists()) {
            return props;
        }
        
        try (FileInputStream in = new FileInputStream(f)) {
            props.load(in);
        } catch (IOException e) {
            return props;
        }

        boolean needDecrypt = false;
        for (String name : props.stringPropertyNames()) {
            String v = props.getProperty(name);
            if (v != null && v.startsWith("ENC(") && v.endsWith(")")) {
                needDecrypt = true;
                break;
            }
        }

        if (needDecrypt) {
            String master = readMasterKey();
            if (master != null) {
                StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
                enc.setPassword(master);
                enc.setAlgorithm("PBEWithMD5AndDES");
                for (String name : props.stringPropertyNames()) {
                    String v = props.getProperty(name);
                    if (v != null && v.startsWith("ENC(") && v.endsWith(")")) {
                        String inner = v.substring(4, v.length() - 1);
                        try {
                            String dec = enc.decrypt(inner);
                            props.setProperty(name, dec);
                        } catch (Exception ex) {
                            props.setProperty(name, "");
                        }
                    }
                }
            }
        }

        return props;
    }

    private static String readMasterKey() {
        File f = new File(DEFAULT_MASTER_KEY_PATH);
        if (!f.exists()) return null;
        try {
            byte[] b = Files.readAllBytes(f.toPath());
            String s = new String(b, StandardCharsets.UTF_8).trim();
            return s.isEmpty() ? null : s;
        } catch (IOException e) {
            return null;
        }
    }

    private static String firstNonNull(String a, String b) {
        return a != null && !a.isEmpty() ? a : (b != null && !b.isEmpty() ? b : null);
    }

    public static String debugDiagnostics() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        
        // Mostrar caminhos de configuração
        sb.append("\"paths\":{");
        sb.append("\"notas\":\"").append(NOTAS_CONFIG_PATH).append("\",");
        sb.append("\"protesto\":\"").append(PROTESTO_CONFIG_PATH).append("\",");
        sb.append("\"default\":\"").append(DEFAULT_CONFIG_PATH).append("\",");
        sb.append("\"master\":\"").append(DEFAULT_MASTER_KEY_PATH).append("\"");
        sb.append('}').append(',');
        
        // Detectar qual aplicação está em uso
        String appConfig = getApplicationConfigPath();
        sb.append("\"activeConfig\":\"").append(appConfig).append("\",");
        
        File cfg = new File(appConfig);
        File m = new File(DEFAULT_MASTER_KEY_PATH);
        sb.append("\"configExists\":").append(cfg.exists()).append(',');
        sb.append("\"masterExists\":").append(m.exists()).append(',');
        sb.append("\"database\":\"").append(appConfig.contains("notas") ? "sptabel" : "spprot").append("\",");
        try {
            Properties p = new Properties();
            if (cfg.exists()) {
                try (FileInputStream in = new FileInputStream(cfg)) { p.load(in); }
            } else {
                // Tentar caminho padrão
                File defaultCfg = new File(DEFAULT_CONFIG_PATH);
                if (defaultCfg.exists()) {
                    try (FileInputStream in = new FileInputStream(defaultCfg)) { p.load(in); }
                }
            }
            sb.append("\"properties\":{");
            boolean first=true;
            String master = null;
            if (m.exists()) {
                try {
                    byte[] b = Files.readAllBytes(m.toPath());
                    master = new String(b, StandardCharsets.UTF_8).trim();
                } catch (Exception ex) {
                    master = null;
                }
            }
            org.jasypt.encryption.pbe.StandardPBEStringEncryptor enc = null;
            if (master != null) {
                enc = new org.jasypt.encryption.pbe.StandardPBEStringEncryptor();
                enc.setPassword(master);
                enc.setAlgorithm("PBEWithMD5AndDES");
            }
            for (String name : p.stringPropertyNames()) {
                if (!first) sb.append(','); first=false;
                String v = p.getProperty(name);
                boolean isEnc = v != null && v.startsWith("ENC(") && v.endsWith(")");
                sb.append('"').append(name).append('"').append(':');
                sb.append('{');
                sb.append("\"isEnc\":").append(isEnc).append(',');
                sb.append("\"length\":").append(v == null ? 0 : v.length()).append(',');
                if (isEnc) {
                    String inner = v.substring(4, v.length() - 1);
                    if (enc == null) {
                        sb.append("\"decryptAttempt\":\"no-master\"");
                    } else {
                        try {
                            String dec = enc.decrypt(inner);
                            sb.append("\"decryptAttempt\":\"ok\",");
                            sb.append("\"decodedLength\":").append(dec == null ? 0 : dec.length());
                        } catch (Exception ex) {
                            sb.append("\"decryptAttempt\":\"fail\",");
                            sb.append("\"decryptError\":\"").append(ex.getMessage().replaceAll("[\\n\\r\"]"," ")).append("\"");
                        }
                    }
                }
                sb.append('}');
            }
            sb.append('}');
        } catch (Exception ex) {
            sb.append("\"propertiesError\":\"").append(ex.getMessage()).append("\"");
        }
        sb.append('}');
        return sb.toString();
    }
}
