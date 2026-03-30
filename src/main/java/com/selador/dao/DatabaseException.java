package com.selador.dao;

/**
 * Exceção personalizada para erros de banco de dados
 */
public class DatabaseException extends Exception {
    
    private String sql;
    private String errorCode;
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(String message, String sql, Throwable cause) {
        super(message, cause);
        this.sql = sql;
    }
    
    public DatabaseException(String message, String sql, String errorCode, Throwable cause) {
        super(message, cause);
        this.sql = sql;
        this.errorCode = errorCode;
    }
    
    public String getSql() {
        return sql;
    }
    
    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (sql != null) {
            sb.append(" [SQL: ").append(sql).append("]");
        }
        if (errorCode != null) {
            sb.append(" [Error Code: ").append(errorCode).append("]");
        }
        return sb.toString();
    }
}