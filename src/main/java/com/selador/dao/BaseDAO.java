package com.selador.dao;

import com.selador.exception.DatabaseException;
import com.selador.web.dto.IntervaloApontamentoDTO;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * DAO base com métodos utilitários para todas as operações de banco
 */
public abstract class BaseDAO {
    
    private static final Logger logger = Logger.getLogger(BaseDAO.class.getName());
    
    // Configurações do banco
    protected static final String DB_URL = System.getProperty("db.url", "jdbc:mysql://localhost:3306/spprot");
    protected static final String DB_USER = System.getProperty("db.username", "root");
    protected static final String DB_PASSWORD = System.getProperty("db.password", "k15720");
    protected static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Pool de conexões simples
    private static Connection connection;
    
    static {
        try {
            Class.forName(DB_DRIVER);
            logger.info("Driver MySQL carregado com sucesso");
        } catch (ClassNotFoundException e) {
            logger.severe("Falha ao carregar driver MySQL: " + e.getMessage());
        }
    }
    
    /**
     * Obtém uma conexão com o banco de dados
     */
    protected Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.fine("Nova conexão criada com o banco");
        }
        return ConnectionFactory.getConnection(); 
    }
    
    /**
     * Fecha recursos de forma segura
     */
    protected void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) rs.close();
        } catch (SQLException e) {
            logger.warning("Erro ao fechar ResultSet: " + e.getMessage());
        }
        
        try {
            if (stmt != null && !stmt.isClosed()) stmt.close();
        } catch (SQLException e) {
            logger.warning("Erro ao fechar Statement: " + e.getMessage());
        }
        
        // ↓↓↓↓ CORREÇÃO: FECHAR A CONEXÃO SEMPRE!
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close(); // ← Devolve ao pool HikariCP
            }
        } catch (SQLException e) {
            logger.warning("Erro ao fechar Connection: " + e.getMessage());
        }
    }
    
    /**
     * Configura parâmetros em um PreparedStatement
     */
    protected void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            int paramIndex = i + 1;
            Object param = params[i];
            
            if (param == null) {
                stmt.setNull(paramIndex, Types.NULL);
            } else if (param instanceof String) {
                stmt.setString(paramIndex, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(paramIndex, (Integer) param);
            } else if (param instanceof Long) {
                stmt.setLong(paramIndex, (Long) param);
            } else if (param instanceof Double) {
                stmt.setDouble(paramIndex, (Double) param);
            } else if (param instanceof Float) {
                stmt.setFloat(paramIndex, (Float) param);
            } else if (param instanceof Boolean) {
                stmt.setBoolean(paramIndex, (Boolean) param);
            } else if (param instanceof java.sql.Date) {
                stmt.setDate(paramIndex, (java.sql.Date) param);
            } else if (param instanceof Timestamp) {
                stmt.setTimestamp(paramIndex, (Timestamp) param);
            } else if (param instanceof java.util.Date) {
                // Converter java.util.Date para java.sql.Timestamp
                java.util.Date utilDate = (java.util.Date) param;
                stmt.setTimestamp(paramIndex, new Timestamp(utilDate.getTime()));
            } else {
                stmt.setObject(paramIndex, param);
            }
        }
    }
    
    /**
     * Converte um ResultSet em um Map de colunas/valores
     */
    protected Map<String, Object> resultSetToMap(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object value = rs.getObject(i);
            row.put(columnName, value);
        }
        
        return row;
    }
    
    /**
     * Executa uma query e retorna lista de Maps
     */
    protected List<Map<String, Object>> executeQuery(String sql, Object... params) 
            throws DatabaseException {
        
        List<Map<String, Object>> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection(); // ← Pega do pool
            stmt = conn.prepareStatement(sql);
            setParameters(stmt, params);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                results.add(resultSetToMap(rs));
            }
            
            return results;
            
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao executar query: " + e.getMessage(), e);
        } finally {
            // ↓↓↓↓ CORREÇÃO CRÍTICA: FECHAR A CONEXÃO!
            closeResources(conn, stmt, rs); // Passar 'conn' também
        }
    }
    
    /**
     * Executa uma atualização (INSERT/UPDATE/DELETE)
     */
    protected int executeUpdate(String sql, Object... params) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            setParameters(stmt, params);
            
            logger.fine("Executando update: " + sql);
            int affectedRows = stmt.executeUpdate();
            
            logger.fine("Update afetou " + affectedRows + " linhas");
            return affectedRows;
            
        } catch (SQLException e) {
            logger.severe("Erro no update: " + e.getMessage() + " | SQL: " + sql);
            throw new DatabaseException("Erro ao executar update: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, null);
        }
    }
    
    /**
     * Executa uma transação com múltiplas operações
     */
    protected void executeTransaction(List<TransactionOperation> operations) 
            throws DatabaseException {
        
        Connection conn = null;
        Savepoint savepoint = null;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            // Criar savepoint se suportado
            try {
                savepoint = conn.setSavepoint();
            } catch (SQLException e) {
                logger.warning("Savepoint não suportado: " + e.getMessage());
            }
            
            // Executar cada operação
            for (TransactionOperation operation : operations) {
                PreparedStatement stmt = null;
                try {
                    stmt = conn.prepareStatement(operation.getSql());
                    setParameters(stmt, operation.getParams());
                    stmt.executeUpdate();
                } finally {
                    if (stmt != null) stmt.close();
                }
            }
            
            // Commit se tudo OK
            conn.commit();
            logger.fine("Transação commitada com sucesso");
            
        } catch (SQLException e) {
            try {
                if (savepoint != null) {
                    conn.rollback(savepoint);
                    logger.info("Rollback para savepoint");
                } else {
                    conn.rollback();
                    logger.info("Rollback completo");
                }
            } catch (SQLException rollbackEx) {
                logger.severe("Erro ao fazer rollback: " + rollbackEx.getMessage());
            }
            throw new DatabaseException("Erro na transação: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                logger.warning("Erro ao restaurar autoCommit: " + e.getMessage());
            }
        }
    }

    /**
     * Método CONCRETO para buscar intervalo por tipo e data
     * Retorna null por padrão - classes específicas podem sobrescrever
     */
    public IntervaloApontamentoDTO buscarIntervaloPorTipoEData(
        String tipoOuCodigo, 
        String descricaoOperacao, 
        String dataBanco
    ) throws DatabaseException {
        // Implementação padrão que retorna null
        // Classes que realmente precisam deste método devem sobrescrevê-lo
        return null;
    }
    
    /**
     * Classe interna para operações de transação
     */
    public static class TransactionOperation {
        private final String sql;
        private final Object[] params;
        
        public TransactionOperation(String sql, Object... params) {
            this.sql = sql;
            this.params = params;
        }
        
        public String getSql() { return sql; }
        public Object[] getParams() { return params; }
    }
}