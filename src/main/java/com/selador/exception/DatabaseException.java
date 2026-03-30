package com.selador.exception;

import java.sql.SQLException;

/**
 * Exceção lançada quando ocorre um erro de banco de dados
 * (Já implementado anteriormente, vamos apenas melhorar)
 */
public class DatabaseException extends Exception {
    
    private String sql;
    private String operation;
    private int errorCode;
    private String sqlState;
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        if (cause instanceof SQLException) {
            SQLException sqlEx = (SQLException) cause;
            this.errorCode = sqlEx.getErrorCode();
            this.sqlState = sqlEx.getSQLState();
        }
    }
    
    public DatabaseException(String message, String sql, Throwable cause) {
        super(message, cause);
        this.sql = sql;
        if (cause instanceof SQLException) {
            SQLException sqlEx = (SQLException) cause;
            this.errorCode = sqlEx.getErrorCode();
            this.sqlState = sqlEx.getSQLState();
        }
    }
    
    public DatabaseException(String message, String sql, String operation, Throwable cause) {
        super(message, cause);
        this.sql = sql;
        this.operation = operation;
        if (cause instanceof SQLException) {
            SQLException sqlEx = (SQLException) cause;
            this.errorCode = sqlEx.getErrorCode();
            this.sqlState = sqlEx.getSQLState();
        }
    }
    
    // Getters
    public String getSql() {
        return sql;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public String getSqlState() {
        return sqlState;
    }
    
    /**
     * Verifica se é erro de conexão
     */
    public boolean isConnectionError() {
        return sqlState != null && (
            sqlState.startsWith("08") || // Connection Exception
            errorCode == 0 || // Connection lost
            getMessage().toLowerCase().contains("connection")
        );
    }
    
    /**
     * Verifica se é erro de timeout
     */
    public boolean isTimeoutError() {
        return sqlState != null && (
            sqlState.equals("HYT00") || // Timeout expired
            getMessage().toLowerCase().contains("timeout") ||
            getMessage().toLowerCase().contains("timed out")
        );
    }
    
    /**
     * Verifica se é erro de chave duplicada
     */
    public boolean isDuplicateKeyError() {
        return sqlState != null && (
            sqlState.equals("23000") || // Integrity constraint violation
            errorCode == 1062 || // MySQL duplicate entry
            getMessage().toLowerCase().contains("duplicate") ||
            getMessage().toLowerCase().contains("unique constraint")
        );
    }
    
    /**
     * Verifica se é erro de dados não encontrados
     */
    public boolean isDataNotFoundError() {
        return sqlState != null && (
            sqlState.equals("02000") || // No data found
            errorCode == 0 || // No results
            getMessage().toLowerCase().contains("no data") ||
            getMessage().toLowerCase().contains("not found")
        );
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DatabaseException{");
        sb.append("message='").append(getMessage()).append('\'');
        
        if (operation != null) {
            sb.append(", operation='").append(operation).append('\'');
        }
        
        if (sql != null) {
            sb.append(", sql='").append(sql).append('\'');
        }
        
        if (errorCode != 0) {
            sb.append(", errorCode=").append(errorCode);
        }
        
        if (sqlState != null) {
            sb.append(", sqlState='").append(sqlState).append('\'');
        }
        
        sb.append('}');
        return sb.toString();
    }
}