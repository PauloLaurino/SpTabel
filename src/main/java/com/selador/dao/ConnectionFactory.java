package com.selador.dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Factory para gerenciamento de conexões com pool
 */
public class ConnectionFactory {
    
    private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());
    
    private static DataSource dataSource;
    private static boolean usePool = true;
    
    // Configurações padrão de PRODUÇÃO - sptabel
    private static final String DEFAULT_DB_URL = "jdbc:mariadb://localhost:3306/sptabel?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "k15720";
    
    static {
        initDataSource();
        testConnectionOnStartup();
    }
    
    /**
     * Testa conexão na inicialização
     */
    private static void testConnectionOnStartup() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                logger.info("✅ Conexão com banco de dados estabelecida com sucesso!");
                
                // Testar consulta básica
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT DATABASE() as db, USER() as user")) {
                    if (rs.next()) {
                        logger.info("📊 Banco: " + rs.getString("db") + " | Usuário: " + rs.getString("user"));
                    }
                }
            }
        } catch (Exception e) {
            logger.severe("❌ FALHA CRÍTICA na conexão com banco de dados: " + e.getMessage());
            logger.severe("Detalhes: " + e.toString());
            
            // Tentar conexão alternativa sem senha (para teste)
            tryAlternativeConnection();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) {}
            }
        }
    }
    
    /**
     * Tenta conexão alternativa (sem senha ou localhost)
     */
    private static void tryAlternativeConnection() {
        String[] alternativeUrls = {
            "jdbc:mysql://localhost:3306/spprot",
            "jdbc:mysql://127.0.0.1:3306/spprot",
        };
        
        String[] users = {"root", ""};
        String[] passwords = {"k15720", "", "root", "admin"};
        
        for (String url : alternativeUrls) {
            for (String user : users) {
                for (String password : passwords) {
                    try {
                        Connection testConn = DriverManager.getConnection(url, user, password);
                        if (testConn != null && !testConn.isClosed()) {
                            logger.warning("⚠️ Conexão alternativa funcionou: " + url + " com usuário: " + user);
                            testConn.close();
                            break;
                        }
                    } catch (Exception e) {
                        // Ignorar, tentar próxima combinação
                    }
                }
            }
        }
    }
    
    /**
     * Obtém propriedade do sistema ou valor padrão
     */
    private static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            // Tentar ler do arquivo de configuração
            value = readFromConfigFile(key);
        }
        return value != null ? value : defaultValue;
    }
    
    /**
     * Lê propriedade do arquivo de configuração
     */
    private static String readFromConfigFile(String key) {
        try {
            Properties props = new Properties();
            props.load(ConnectionFactory.class.getClassLoader()
                .getResourceAsStream("config.properties"));
            return props.getProperty(key);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Inicializa o pool de conexões
     */
    private static void initDataSource() {
        try {
            // Registrar driver (MariaDB ou MySQL)
            try {
                String url = getProperty("db.url", DEFAULT_DB_URL);
                if (url.startsWith("jdbc:mariadb:")) {
                    Class.forName("org.mariadb.jdbc.Driver");
                    logger.info("✅ Driver MariaDB registrado com sucesso");
                } else {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    logger.info("✅ Driver MySQL registrado com sucesso");
                }
            } catch (ClassNotFoundException e) {
                logger.severe("❌ Driver de banco de dados não encontrado!");
                usePool = false;
                return;
            }
            
            String poolEnabled = getProperty("db.pool.enabled", "true");
            usePool = Boolean.parseBoolean(poolEnabled);
            
            if (usePool) {
                HikariConfig config = new HikariConfig();
                
                String url = getProperty("db.url", DEFAULT_DB_URL);
                String user = getProperty("db.username", DEFAULT_DB_USER);
                String password = getProperty("db.password", DEFAULT_DB_PASSWORD);
                
                config.setJdbcUrl(url);
                config.setUsername(user);
                config.setPassword(password);
                
                // Log das configurações (sem senha por segurança)
                logger.info("🔧 Configurando conexão:");
                logger.info("   URL: " + url);
                logger.info("   Usuário: " + user);
                logger.info("   Pool habilitado: " + usePool);
                logger.info("   Propriedade db.url do sistema: " + System.getProperty("db.url"));
                logger.info("   Propriedade db.username do sistema: " + System.getProperty("db.username"));
                
                // Configurações do pool
                config.setMaximumPoolSize(Integer.parseInt(
                    getProperty("db.pool.max.size", "10")));
                config.setMinimumIdle(Integer.parseInt(
                    getProperty("db.pool.min.idle", "2")));
                config.setConnectionTimeout(Long.parseLong(
                    getProperty("db.connection.timeout", "30000")));
                config.setIdleTimeout(Long.parseLong(
                    getProperty("db.idle.timeout", "600000")));
                config.setMaxLifetime(Long.parseLong(
                    getProperty("db.max.lifetime", "1800000")));
                config.setLeakDetectionThreshold(Long.parseLong(
                    getProperty("db.leak.detection.threshold", "60000")));
                
                // Otimizações para MySQL/MariaDB
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.addDataSourceProperty("useServerPrepStmts", "true");
                config.addDataSourceProperty("useLocalSessionState", "true");
                config.addDataSourceProperty("rewriteBatchedStatements", "true");
                config.addDataSourceProperty("cacheResultSetMetadata", "true");
                config.addDataSourceProperty("cacheServerConfiguration", "true");
                config.addDataSourceProperty("elideSetAutoCommits", "true");
                config.addDataSourceProperty("maintainTimeStats", "false");
                
                // Configurações de reconexão
                config.addDataSourceProperty("autoReconnect", "true");
                config.addDataSourceProperty("failOverReadOnly", "false");
                config.addDataSourceProperty("maxReconnects", "3");
                config.addDataSourceProperty("initialTimeout", "2");
                
                // Timeouts de rede
                config.addDataSourceProperty("socketTimeout", "30000");
                config.addDataSourceProperty("connectTimeout", "10000");
                
                dataSource = new HikariDataSource(config);
                logger.info("✅ Pool de conexões HikariCP inicializado com sucesso");
                
                // Testar primeira conexão
                try (Connection testConn = dataSource.getConnection()) {
                    logger.info("✅ Primeira conexão do pool obtida com sucesso");
                }
                
            } else {
                logger.info("ℹ️ Pool de conexões desabilitado, usando conexões diretas");
            }
            
        } catch (Exception e) {
            logger.severe("❌ ERRO ao inicializar pool de conexões: " + e.getMessage());
            e.printStackTrace();
            usePool = false;
            
            // Tentar inicialização simples sem pool
            trySimpleConnection();
        }
    }
    
    /**
     * Tenta conexão simples sem pool
     */
    private static void trySimpleConnection() {
        try {
            String url = getProperty("db.url", DEFAULT_DB_URL);
            String user = getProperty("db.username", DEFAULT_DB_USER);
            String password = getProperty("db.password", DEFAULT_DB_PASSWORD);
            
            logger.info("🔄 Tentando conexão direta sem pool...");
            Connection conn = DriverManager.getConnection(url, user, password);
            logger.info("✅ Conexão direta estabelecida");
            conn.close();
        } catch (Exception e) {
            logger.severe("❌ Conexão direta também falhou: " + e.getMessage());
        }
    }
    
    /**
     * Obtém uma conexão (Utiliza o padrão DbUtil da aplicação)
     */
    public static Connection getConnection() throws SQLException {
        return com.monitor.funarpen.util.DbUtil.getConnection();
    }
    
    /**
     * Fecha o pool de conexões
     */
    public static void closePool() {
        if (dataSource instanceof HikariDataSource) {
            try {
                ((HikariDataSource) dataSource).close();
                logger.info("✅ Pool de conexões fechado");
            } catch (Exception e) {
                logger.warning("⚠️ Erro ao fechar pool: " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifica se o pool está ativo
     */
    public static boolean isPoolActive() {
        if (dataSource instanceof HikariDataSource) {
            return !((HikariDataSource) dataSource).isClosed();
        }
        return false;
    }
    
    /**
     * Retorna estatísticas do pool (se disponível)
     */
    public static Map<String, Object> getPoolStats() {
        Map<String, Object> stats = new HashMap<>();
        
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikari = (HikariDataSource) dataSource;
            try {
                stats.put("activeConnections", hikari.getHikariPoolMXBean().getActiveConnections());
                stats.put("idleConnections", hikari.getHikariPoolMXBean().getIdleConnections());
                stats.put("totalConnections", hikari.getHikariPoolMXBean().getTotalConnections());
                stats.put("threadsAwaitingConnection", hikari.getHikariPoolMXBean().getThreadsAwaitingConnection());
                stats.put("poolStatus", "ATIVO");
            } catch (Exception e) {
                logger.warning("Erro ao obter estatísticas do pool: " + e.getMessage());
                stats.put("poolStatus", "ERRO");
            }
        } else {
            stats.put("poolStatus", "DESATIVADO");
        }
        
        return stats;
    }
    
    /**
     * Testa a conexão com o banco de forma robusta
     */
    public static Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        Connection conn = null;
        
        try {
            long startTime = System.currentTimeMillis();
            conn = getConnection();
            long endTime = System.currentTimeMillis();
            
            if (conn != null && !conn.isClosed()) {
                result.put("success", true);
                result.put("message", "Conexão estabelecida com sucesso");
                result.put("connectionTime", (endTime - startTime) + "ms");
                result.put("database", conn.getMetaData().getDatabaseProductName());
                result.put("version", conn.getMetaData().getDatabaseProductVersion());
                result.put("url", conn.getMetaData().getURL());
                result.put("user", conn.getMetaData().getUserName());
                
                // Testar consulta
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT NOW() as hora_atual")) {
                    if (rs.next()) {
                        result.put("serverTime", rs.getString("hora_atual"));
                    }
                }
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Falha na conexão: " + e.getMessage());
            result.put("error", e.toString());
            result.put("timestamp", new java.util.Date());
            
            logger.severe("❌ Teste de conexão falhou: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) {
                    logger.fine("Erro ao fechar conexão de teste: " + e.getMessage());
                }
            }
        }
        
        return result;
    }
    
    /**
     * Método para diagnóstico rápido
     */
    public static void runDiagnostics() {
        logger.info("🔍 DIAGNÓSTICO DE CONEXÃO:");
        logger.info("   Use Pool: " + usePool);
        logger.info("   DataSource: " + (dataSource != null ? "INICIALIZADO" : "NULO"));
        logger.info("   Pool Ativo: " + isPoolActive());
        
        Map<String, Object> testResult = testConnection();
        logger.info("   Teste Conexão: " + (Boolean.TRUE.equals(testResult.get("success")) ? "✅ OK" : "❌ FALHA"));
        
        if (Boolean.FALSE.equals(testResult.get("success"))) {
            logger.severe("   Erro detalhado: " + testResult.get("message"));
        }
    }
}