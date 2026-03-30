package com.selador.web.dto;

import java.util.Date;

// ErrorDTO.java - DTO para erros detalhados
public class ErrorDTO {
    private String code;
    private String message;
    private String detail;
    private String field;
    private String timestamp;
    
    public ErrorDTO() {
        this.timestamp = new Date().toString();
    }
    
    public ErrorDTO(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = new Date().toString();
    }
    
    public ErrorDTO(String code, String message, String detail) {
        this.code = code;
        this.message = message;
        this.detail = detail;
        this.timestamp = new Date().toString();
    }
    
    // Getters e Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}