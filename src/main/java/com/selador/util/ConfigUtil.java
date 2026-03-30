package com.selador.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

/**
 * Utilitário para leitura de arquivos de configuração .properties
 */
public class ConfigUtil {
    
    private static final String CONFIG_FILE = "config.properties";
    private static final String CONFIG_DIR = "config";
    private static Properties properties = null;
    
    private ConfigUtil() {
        // Classe utilitária - não instanciável
    }
    
    /**
     * Carrega propriedades do arquivo config.properties
     */
    public static Properties carregarProperties() throws IOException {
        if (properties != null) {
            return properties;
        }
        
        properties = new Properties();
        InputStream input = null;
        
        try {
            // 1. Tentar carregar do diretório config/ (para deploy WAR)
            File configFile = Paths.get(CONFIG_DIR, CONFIG_FILE).toFile();
            if (configFile.exists() && configFile.isFile()) {
                input = new FileInputStream(configFile);
                properties.load(input);
                LogUtil.log("CONFIG", "INFO", 
                    "Configurações carregadas do arquivo: " + configFile.getAbsolutePath());
                return properties;
            }
            
            // 2. Tentar carregar do classpath (para desenvolvimento)
            input = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (input != null) {
                properties.load(input);
                LogUtil.log("CONFIG", "INFO", 
                    "Configurações carregadas do classpath");
                return properties;
            }
            
            // 3. Tentar carregar do diretório atual
            configFile = new File(CONFIG_FILE);
            if (configFile.exists() && configFile.isFile()) {
                input = new FileInputStream(configFile);
                properties.load(input);
                LogUtil.log("CONFIG", "INFO", 
                    "Configurações carregadas do diretório atual");
                return properties;
            }
            
            throw new IOException("Arquivo config.properties não encontrado em nenhum local");
            
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LogUtil.log("CONFIG", "ERROR", 
                        "Erro ao fechar stream de configuração: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Carrega propriedades de um arquivo específico
     */
    public static Properties carregarProperties(String fileName) throws IOException {
        Properties props = new Properties();
        InputStream input = null;
        
        try {
            input = ConfigUtil.class.getClassLoader().getResourceAsStream(fileName);
            if (input == null) {
                throw new IOException("Arquivo " + fileName + " não encontrado no classpath");
            }
            
            props.load(input);
            LogUtil.log("CONFIG", "INFO", 
                "Arquivo de configuração carregado: " + fileName);
                
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LogUtil.log("CONFIG", "ERROR", 
                        "Erro ao fechar stream: " + e.getMessage());
                }
            }
        }
        
        return props;
    }
    
    /**
     * Obtém valor da propriedade como String
     */
    public static String getString(String key) throws IOException {
        return getString(key, null);
    }
    
    /**
     * Obtém valor da propriedade como String com valor padrão
     */
    public static String getString(String key, String defaultValue) throws IOException {
        if (properties == null) {
            carregarProperties();
        }
        
        String value = properties.getProperty(key);
        return (value != null) ? value.trim() : defaultValue;
    }
    
    /**
     * Obtém valor da propriedade como inteiro
     */
    public static int getInt(String key) throws IOException {
        return getInt(key, 0);
    }
    
    /**
     * Obtém valor da propriedade como inteiro com valor padrão
     */
    public static int getInt(String key, int defaultValue) throws IOException {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            LogUtil.log("CONFIG", "WARN", 
                "Valor inválido para propriedade inteira " + key + ": " + value);
            return defaultValue;
        }
    }
    
    /**
     * Obtém valor da propriedade como boolean
     */
    public static boolean getBoolean(String key) throws IOException {
        return getBoolean(key, false);
    }
    
    /**
     * Obtém valor da propriedade como boolean com valor padrão
     */
    public static boolean getBoolean(String key, boolean defaultValue) throws IOException {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        
        value = value.trim().toLowerCase();
        return "true".equals(value) || "1".equals(value) || "yes".equals(value);
    }
    
    /**
     * Obtém valor da propriedade como double
     */
    public static double getDouble(String key) throws IOException {
        return getDouble(key, 0.0);
    }
    
    /**
     * Obtém valor da propriedade como double com valor padrão
     */
    public static double getDouble(String key, double defaultValue) throws IOException {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            LogUtil.log("CONFIG", "WARN", 
                "Valor inválido para propriedade double " + key + ": " + value);
            return defaultValue;
        }
    }
    
    /**
     * Obtém configurações do banco de dados
     */
    public static Properties getDatabaseProperties() throws IOException {
        Properties dbProps = new Properties();
        
        dbProps.setProperty("url", getString("db.url", "jdbc:mysql://localhost:3306/sptabel"));
        dbProps.setProperty("username", getString("db.username", "root"));
        dbProps.setProperty("password", getString("db.password", ""));
        dbProps.setProperty("pool.max.size", String.valueOf(getInt("db.pool.max.size", 10)));
        dbProps.setProperty("pool.min.idle", String.valueOf(getInt("db.pool.min.idle", 5)));
        dbProps.setProperty("connection.timeout", String.valueOf(getInt("db.connection.timeout", 30000)));
        
        return dbProps;
    }
    
    /**
     * Obtém configurações da aplicação
     */
    public static Properties getAppProperties() throws IOException {
        Properties appProps = new Properties();
        
        appProps.setProperty("app.name", getString("app.name", "Selador"));
        appProps.setProperty("app.version", getString("app.version", "1.0.0"));
        appProps.setProperty("app.maker.integration.enabled", 
            String.valueOf(getBoolean("app.maker.integration.enabled", true)));
        
        return appProps;
    }
    
    /**
     * Obtém configurações de selo
     */
    public static Properties getSeloProperties() throws IOException {
        Properties seloProps = new Properties();
        
        seloProps.setProperty("selo.tipo.padrao", getString("selo.tipo.padrao", "0004"));
        seloProps.setProperty("selo.codigo.tipo.ato", getString("selo.codigo.tipo.ato", "701"));
        seloProps.setProperty("selo.validade.meses", String.valueOf(getInt("selo.validade.meses", 12)));
        
        return seloProps;
    }
    
    /**
     * Limpa cache de propriedades (útil para desenvolvimento)
     */
    public static void clearCache() {
        properties = null;
        LogUtil.log("CONFIG", "INFO", "Cache de configurações limpo");
    }
    
    /**
     * Verifica se todas as propriedades obrigatórias estão configuradas
     */
    public static boolean validarConfiguracoesObrigatorias() throws IOException {
        String[] obrigatorias = {
            "db.url",
            "db.username",
            "db.password",
            "app.name"
        };
        
        for (String key : obrigatorias) {
            String value = getString(key);
            if (value == null || value.trim().isEmpty()) {
                LogUtil.log("CONFIG", "ERROR", 
                    "Propriedade obrigatória não configurada: " + key);
                return false;
            }
        }
        
        return true;
    }
}