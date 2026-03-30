package com.selador.service;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Service para gerenciar configurações do sistema e integração com Maker 5
 */
public class ConfigService {
    
    private static final Logger logger = Logger.getLogger(ConfigService.class.getName());
    
    private static ConfigService instance;
    private Properties properties;
    private Map<String, String> configCache;
    
    private ConfigService() {
        carregarConfiguracoes();
    }
    
    public static synchronized ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService();
        }
        return instance;
    }
    
    /**
     * Carrega configurações do arquivo properties
     */
    private void carregarConfiguracoes() {
        try {
            properties = new Properties();
            
            // Tentar carregar do classpath
            try (java.io.InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("config.properties")) {
                if (input != null) {
                    properties.load(input);
                }
            }
            
            // Se não encontrar no classpath, tentar arquivo externo
            if (properties.isEmpty()) {
                try (java.io.InputStream input = new java.io.FileInputStream("config.properties")) {
                    properties.load(input);
                }
            }
            
            configCache = new HashMap<>();
            
            // Cache de configurações importantes
            configCache.put("app.name", getString("app.name", "Selador"));
            configCache.put("app.version", getString("app.version", "1.0.0"));
            configCache.put("maker.integration", getString("app.maker.integration.enabled", "true"));
            configCache.put("selo.tipo.padrao", getString("selo.tipo.padrao", "0004"));
            configCache.put("selo.codigo.tipo.ato", getString("selo.codigo.tipo.ato", "701"));
            
            logger.info("Configurações carregadas: " + configCache.get("app.name") + 
                " v" + configCache.get("app.version"));
                
        } catch (Exception e) {
            logger.severe("Erro ao carregar configurações: " + e.getMessage());
            properties = new Properties();
            configCache = new HashMap<>();
            
            // Configurações padrão
            setDefaultConfigurations();
        }
    }
    
    /**
     * Define configurações padrão
     */
    private void setDefaultConfigurations() {
        configCache.put("app.name", "Selador");
        configCache.put("app.version", "1.0.0");
        configCache.put("maker.integration", "true");
        configCache.put("selo.tipo.padrao", "0004");  // TPI - Intimação/Digitalização
        configCache.put("selo.codigo.tipo.ato", "701");
        configCache.put("db.pool.max.size", "10");
        configCache.put("db.pool.min.idle", "2");
        configCache.put("db.connection.timeout", "30000");
        configCache.put("interface.pagina.tamanho", "50");
        configCache.put("interface.timeout.busca", "30000");
        
        logger.info("Usando configurações padrão");
    }
    
    /**
     * Obtém configuração como String
     */
    public String getString(String key, String defaultValue) {
        if (configCache.containsKey(key)) {
            return configCache.get(key);
        }
        
        // Tentar pegar da properties
        String value = properties.getProperty(key, defaultValue);
        
        // Cache o valor
        configCache.put(key, value);
        
        return value;
    }
    
    /**
     * Obtém configuração como inteiro
     */
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getString(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warning("Erro ao converter configuração " + key + " para inteiro: " + e.getMessage());
            return defaultValue;
        }
    }
    
    /**
     * Obtém configuração como boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }
    
    /**
     * Obtém configuração como double
     */
    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(getString(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warning("Erro ao converter configuração " + key + " para double: " + e.getMessage());
            return defaultValue;
        }
    }
    
    /**
     * Verifica se a integração com Maker 5 está ativa
     */
    public boolean isMakerIntegrationEnabled() {
        return getBoolean("maker.integration", true);
    }
    
    /**
     * Obtém tipo de selo padrão
     */
    public String getTipoSeloPadrao() {
        return getString("selo.tipo.padrao", "0004");
    }
    
    /**
     * Obtém código do tipo de ato
     */
    public String getCodigoTipoAto() {
        return getString("selo.codigo.tipo.ato", "701");
    }
    
    /**
     * Obtém configurações do banco de dados
     */
    public String getDbUrl() {
        return getString("db.url", "jdbc:mysql://localhost:3306/spprot");
    }
    
    public String getDbUsername() {
        return getString("db.username", "root");
    }
    
    public String getDbPassword() {
        return getString("db.password", "");
    }
    
    public int getDbPoolMaxSize() {
        return getInt("db.pool.max.size", 10);
    }
    
    public int getDbPoolMinIdle() {
        return getInt("db.pool.min.idle", 2);
    }
    
    /**
     * Obtém configurações da interface JavaScript
     */
    public Map<String, Object> getConfiguracoesInterface() {
        Map<String, Object> configs = new HashMap<>();
        
        configs.put("appName", getString("app.name", "Selador"));
        configs.put("appVersion", getString("app.version", "1.0.0"));
        configs.put("seloTipoPadrao", getTipoSeloPadrao());
        configs.put("codigoTipoAto", getCodigoTipoAto());
        configs.put("makerIntegration", isMakerIntegrationEnabled());
        configs.put("paginaTamanho", getInt("interface.pagina.tamanho", 50));
        configs.put("timeoutBusca", getInt("interface.timeout.busca", 30000));
        
        return configs;
    }
    
    /**
     * Atualiza configuração em tempo de execução
     */
    public boolean atualizarConfiguracao(String key, String value) {
        try {
            configCache.put(key, value);
            
            // Também atualiza na properties
            properties.setProperty(key, value);
            
            logger.info(String.format("Configuração atualizada: %s = %s", key, value));
            return true;
        } catch (Exception e) {
            logger.severe(String.format("Erro ao atualizar configuração %s: %s", key, e.getMessage()));
            return false;
        }
    }
    
    /**
     * Salva configurações em arquivo
     */
    public boolean salvarConfiguracoes() {
        try {
            try (java.io.OutputStream output = new java.io.FileOutputStream("config.properties")) {
                properties.store(output, "Configurações do Sistema Selador");
            }
            logger.info("Configurações salvas no arquivo config.properties");
            return true;
        } catch (Exception e) {
            logger.severe("Erro ao salvar configurações: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém todas as configurações (para interface administrativa)
     */
    public Map<String, String> getTodasConfiguracoes() {
        Map<String, String> todas = new HashMap<>();
        
        // Configurações do sistema
        todas.put("app.name", getString("app.name", "Selador"));
        todas.put("app.version", getString("app.version", "1.0.0"));
        todas.put("app.maker.integration.enabled", 
            String.valueOf(isMakerIntegrationEnabled()));
        
        // Configurações de selo
        todas.put("selo.tipo.padrao", getTipoSeloPadrao());
        todas.put("selo.codigo.tipo.ato", getCodigoTipoAto());
        
        // Configurações de banco
        todas.put("db.url", getDbUrl());
        todas.put("db.username", getDbUsername());
        // Não expor senhas
        todas.put("db.password", "******");
        todas.put("db.pool.max.size", String.valueOf(getDbPoolMaxSize()));
        todas.put("db.pool.min.idle", String.valueOf(getDbPoolMinIdle()));
        
        // Configurações de interface
        todas.put("interface.pagina.tamanho", String.valueOf(getInt("interface.pagina.tamanho", 50)));
        todas.put("interface.timeout.busca", String.valueOf(getInt("interface.timeout.busca", 30000)));
        
        return todas;
    }
    
    /**
     * Valida configurações necessárias para funcionamento
     */
    public Map<String, String> validarConfiguracoes() {
        Map<String, String> problemas = new HashMap<>();
        
        // Verificar configurações críticas
        if (getTipoSeloPadrao() == null || getTipoSeloPadrao().trim().isEmpty()) {
            problemas.put("selo.tipo.padrao", "Tipo de selo padrão não configurado");
        }
        
        if (getCodigoTipoAto() == null || getCodigoTipoAto().trim().isEmpty()) {
            problemas.put("selo.codigo.tipo.ato", "Código do tipo de ato não configurado");
        }
        
        // Verificar conexão com banco
        String dbUrl = getDbUrl();
        if (dbUrl == null || dbUrl.trim().isEmpty() || !dbUrl.startsWith("jdbc:")) {
            problemas.put("db.url", "URL do banco de dados inválida");
        }
        
        // Verificar integração Maker
        if (!isMakerIntegrationEnabled()) {
            problemas.put("maker.integration", "Integração com Maker 5 desativada");
        }
        
        if (problemas.isEmpty()) {
            logger.info("Configurações validadas com sucesso");
        } else {
            logger.warning("Problemas encontrados nas configurações: " + problemas);
        }
        
        return problemas;
    }
    
    /**
     * Recarrega configurações do arquivo
     */
    public void recarregarConfiguracoes() {
        logger.info("Recarregando configurações...");
        configCache.clear();
        carregarConfiguracoes();
    }
    
    /**
     * Limpa cache de configurações
     */
    public void limparCache() {
        logger.info("Limpando cache de configurações");
        configCache.clear();
    }
}