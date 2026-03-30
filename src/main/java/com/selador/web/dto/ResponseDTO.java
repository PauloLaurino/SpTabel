package com.selador.web.dto;

import java.util.List;



/**
 * DTOs para transferência de dados entre camadas
 */

// ResponseDTO.java - Resposta padrão da API
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private long timestamp;
    private String requestId;
    
    public ResponseDTO() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(true, "Operação realizada com sucesso", data);
    }
    
    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(true, message, data);
    }
    
    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(false, message, null);
    }
    
    public static <T> ResponseDTO<T> error(String message, List<String> errors) {
        ResponseDTO<T> response = new ResponseDTO<>(false, message, null);
        response.setErrors(errors);
        return response;
    }
    
    // Getters e Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}
