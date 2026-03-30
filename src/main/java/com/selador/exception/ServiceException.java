package com.selador.exception;

/**
 * Exceção lançada quando ocorre um erro na camada de serviço
 */
public class ServiceException extends Exception {
    
    private String errorCode;
    private String serviceName;
    private Object additionalData;
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ServiceException(String message, String errorCode, String serviceName) {
        super(message);
        this.errorCode = errorCode;
        this.serviceName = serviceName;
    }
    
    public ServiceException(String message, String errorCode, String serviceName, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.serviceName = serviceName;
    }
    
    // Getters e Setters
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public Object getAdditionalData() {
        return additionalData;
    }
    
    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }
    
    /**
     * Cria uma ServiceException para erro de banco de dados
     */
    public static ServiceException databaseError(String operation, Throwable cause) {
        return new ServiceException(
            "Erro de banco de dados na operação: " + operation,
            "DB_ERROR",
            "DatabaseService",
            cause
        );
    }
    
    /**
     * Cria uma ServiceException para erro de validação
     */
    public static ServiceException validationError(String message, String field) {
        ServiceException ex = new ServiceException(
            "Erro de validação: " + message,
            "VALIDATION_ERROR",
            "ValidationService"
        );
        ex.setAdditionalData(field);
        return ex;
    }
    
    /**
     * Cria uma ServiceException para selo não encontrado
     */
    public static ServiceException seloNotFound(String tipoSelo) {
        return new ServiceException(
            "Nenhum selo disponível do tipo: " + tipoSelo,
            "SELO_NOT_FOUND",
            "SeloService"
        );
    }
    
    /**
     * Cria uma ServiceException para selos insuficientes
     */
    public static ServiceException selosInsuficientes(int necessarios, int disponiveis) {
        ServiceException ex = new ServiceException(
            String.format("Selos insuficientes. Necessários: %d, Disponíveis: %d", 
                necessarios, disponiveis),
            "SELOS_INSUFICIENTES",
            "SelagemService"
        );
        ex.setAdditionalData(new int[]{necessarios, disponiveis});
        return ex;
    }
    
    /**
     * Cria uma ServiceException para apontamento não apto
     */
    public static ServiceException apontamentoNaoApto(String numapo1, String numapo2, String motivo) {
        ServiceException ex = new ServiceException(
            String.format("Apontamento %s/%s não está apto para selagem: %s", 
                numapo1, numapo2, motivo),
            "APONTAMENTO_NAO_APTO",
            "SelagemService"
        );
        ex.setAdditionalData(new String[]{numapo1, numapo2, motivo});
        return ex;
    }
    
    /**
     * Cria uma ServiceException para configuração inválida
     */
    public static ServiceException configuracaoInvalida(String key, String valor) {
        return new ServiceException(
            String.format("Configuração inválida: %s = %s", key, valor),
            "CONFIG_INVALID",
            "ConfigService"
        );
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServiceException{");
        sb.append("message='").append(getMessage()).append('\'');
        if (errorCode != null) {
            sb.append(", errorCode='").append(errorCode).append('\'');
        }
        if (serviceName != null) {
            sb.append(", serviceName='").append(serviceName).append('\'');
        }
        if (additionalData != null) {
            sb.append(", additionalData=").append(additionalData);
        }
        sb.append('}');
        return sb.toString();
    }
}