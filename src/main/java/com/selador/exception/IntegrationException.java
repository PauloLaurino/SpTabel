package com.selador.exception;

/**
 * Exceção lançada quando ocorre um erro de integração com o Maker 5
 */
public class IntegrationException extends Exception {
    
    private String integrationPoint;
    private String makerVersion;
    private String operation;
    private Object requestData;
    private Object responseData;
    
    public IntegrationException(String message) {
        super(message);
    }
    
    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IntegrationException(String message, String integrationPoint) {
        super(message);
        this.integrationPoint = integrationPoint;
    }
    
    public IntegrationException(String message, String integrationPoint, Throwable cause) {
        super(message, cause);
        this.integrationPoint = integrationPoint;
    }
    
    public IntegrationException(String message, String integrationPoint, String operation) {
        super(message);
        this.integrationPoint = integrationPoint;
        this.operation = operation;
    }
    
    // Getters e Setters
    public String getIntegrationPoint() {
        return integrationPoint;
    }
    
    public void setIntegrationPoint(String integrationPoint) {
        this.integrationPoint = integrationPoint;
    }
    
    public String getMakerVersion() {
        return makerVersion;
    }
    
    public void setMakerVersion(String makerVersion) {
        this.makerVersion = makerVersion;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public Object getRequestData() {
        return requestData;
    }
    
    public void setRequestData(Object requestData) {
        this.requestData = requestData;
    }
    
    public Object getResponseData() {
        return responseData;
    }
    
    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }
    
    /**
     * Cria IntegrationException para erro de comunicação com Maker 5
     */
    public static IntegrationException comunicacaoError(String endpoint, Throwable cause) {
        IntegrationException ex = new IntegrationException(
            String.format("Erro de comunicação com Maker 5 no endpoint: %s", endpoint),
            cause
        );
        ex.setIntegrationPoint(endpoint);
        ex.setOperation("HTTP_COMMUNICATION");
        return ex;
    }
    
    /**
     * Cria IntegrationException para resposta inválida do Maker 5
     */
    public static IntegrationException respostaInvalida(String endpoint, String resposta) {
        IntegrationException ex = new IntegrationException(
            String.format("Resposta inválida do Maker 5 no endpoint: %s. Resposta: %s", 
                endpoint, resposta)
        );
        ex.setIntegrationPoint(endpoint);
        ex.setOperation("RESPONSE_PARSING");
        ex.setResponseData(resposta);
        return ex;
    }
    
    /**
     * Cria IntegrationException para timeout na integração
     */
    public static IntegrationException timeoutError(String endpoint, long timeoutMs) {
        IntegrationException ex = new IntegrationException(
            String.format("Timeout na comunicação com Maker 5 no endpoint: %s. Timeout: %d ms", 
                endpoint, timeoutMs)
        );
        ex.setIntegrationPoint(endpoint);
        ex.setOperation("TIMEOUT");
        return ex;
    }
    
    /**
     * Cria IntegrationException para configuração de integração ausente
     */
    public static IntegrationException configuracaoAusente(String configKey) {
        IntegrationException ex = new IntegrationException(
            String.format("Configuração de integração ausente: %s", configKey)
        );
        ex.setIntegrationPoint("CONFIGURATION");
        ex.setOperation("VALIDATION");
        return ex;
    }
    
    /**
     * Cria IntegrationException para integração desativada
     */
    public static IntegrationException integracaoDesativada() {
        IntegrationException ex = new IntegrationException(
            "Integração com Maker 5 está desativada"
        );
        ex.setIntegrationPoint("SYSTEM");
        ex.setOperation("CHECK_STATUS");
        return ex;
    }
    
    /**
     * Cria IntegrationException para formato de dados incompatível
     */
    public static IntegrationException formatoIncompativel(String expected, String actual) {
        IntegrationException ex = new IntegrationException(
            String.format("Formato de dados incompatível. Esperado: %s, Recebido: %s", 
                expected, actual)
        );
        ex.setIntegrationPoint("DATA_FORMAT");
        ex.setOperation("DATA_VALIDATION");
        return ex;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IntegrationException{");
        sb.append("message='").append(getMessage()).append('\'');
        
        if (integrationPoint != null) {
            sb.append(", integrationPoint='").append(integrationPoint).append('\'');
        }
        
        if (makerVersion != null) {
            sb.append(", makerVersion='").append(makerVersion).append('\'');
        }
        
        if (operation != null) {
            sb.append(", operation='").append(operation).append('\'');
        }
        
        if (requestData != null) {
            sb.append(", requestData=").append(requestData);
        }
        
        if (responseData != null) {
            sb.append(", responseData=").append(responseData);
        }
        
        sb.append('}');
        return sb.toString();
    }
}