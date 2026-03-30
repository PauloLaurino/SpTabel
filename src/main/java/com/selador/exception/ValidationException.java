package com.selador.exception;

import com.selador.util.ValidationUtil;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Exceção lançada quando ocorre um erro de validação
 */
public class ValidationException extends Exception {
    
    private Map<String, List<String>> fieldErrors;
    private List<String> globalErrors;
    private String validationType;
    
    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.globalErrors = new ArrayList<>();
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldErrors = new HashMap<>();
        this.globalErrors = new ArrayList<>();
    }
    
    public ValidationException(String message, String field, String error) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.globalErrors = new ArrayList<>();
        addFieldError(field, error);
    }
    
    public ValidationException(String message, Map<String, List<String>> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
        this.globalErrors = new ArrayList<>();
    }
    
    public ValidationException(String message, List<String> globalErrors) {
        super(message);
        this.fieldErrors = new HashMap<>();
        this.globalErrors = globalErrors != null ? globalErrors : new ArrayList<>();
    }
    
    // Getters e Setters
    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }
    
    public void setFieldErrors(Map<String, List<String>> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
    
    public List<String> getGlobalErrors() {
        return globalErrors;
    }
    
    public void setGlobalErrors(List<String> globalErrors) {
        this.globalErrors = globalErrors;
    }
    
    public String getValidationType() {
        return validationType;
    }
    
    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }
    
    /**
     * Adiciona erro de campo
     */
    public void addFieldError(String field, String error) {
        fieldErrors.computeIfAbsent(field, k -> new ArrayList<>()).add(error);
    }
    
    /**
     * Adiciona erro global
     */
    public void addGlobalError(String error) {
        globalErrors.add(error);
    }
    
    /**
     * Verifica se existem erros
     */
    public boolean hasErrors() {
        return !fieldErrors.isEmpty() || !globalErrors.isEmpty();
    }
    
    /**
     * Obtém todos os erros como lista plana
     */
    public List<String> getAllErrors() {
        List<String> allErrors = new ArrayList<>(globalErrors);
        fieldErrors.forEach((field, errors) -> {
            for (String error : errors) {
                allErrors.add(field + ": " + error);
            }
        });
        return allErrors;
    }
    
    /**
     * Cria ValidationException para data inválida
     */
    public static ValidationException dataInvalida(String campo, String valor, String formatoEsperado) {
        ValidationException ex = new ValidationException(
            String.format("Data inválida no campo '%s': %s. Formato esperado: %s", 
                campo, valor, formatoEsperado)
        );
        ex.addFieldError(campo, ValidationUtil.getMensagemErro(campo, "DATE"));
        ex.setValidationType("DATE_VALIDATION");
        return ex;
    }
    
    /**
     * Cria ValidationException para CPF/CNPJ inválido
     */
    public static ValidationException cpfCnpjInvalido(String campo, String valor) {
        ValidationException ex = new ValidationException(
            String.format("CPF/CNPJ inválido no campo '%s': %s", campo, valor)
        );
        ex.addFieldError(campo, ValidationUtil.getMensagemErro(campo, "CPF_CNPJ"));
        ex.setValidationType("CPF_CNPJ_VALIDATION");
        return ex;
    }
    
    /**
     * Cria ValidationException para campo obrigatório
     */
    public static ValidationException campoObrigatorio(String campo) {
        ValidationException ex = new ValidationException(
            String.format("Campo obrigatório não preenchido: '%s'", campo)
        );
        ex.addFieldError(campo, ValidationUtil.getMensagemErro(campo, "REQUIRED"));
        ex.setValidationType("REQUIRED_VALIDATION");
        return ex;
    }
    
    /**
     * Cria ValidationException para formato inválido
     */
    public static ValidationException formatoInvalido(String campo, String valor, String formato) {
        ValidationException ex = new ValidationException(
            String.format("Formato inválido no campo '%s': %s. Formato esperado: %s", 
                campo, valor, formato)
        );
        ex.addFieldError(campo, ValidationUtil.getMensagemErro(campo, "FORMAT"));
        ex.setValidationType("FORMAT_VALIDATION");
        return ex;
    }
    
    /**
     * Cria ValidationException para valor fora do intervalo
     */
    public static ValidationException valorForaIntervalo(String campo, String valor, 
            String min, String max) {
        ValidationException ex = new ValidationException(
            String.format("Valor fora do intervalo no campo '%s': %s. Intervalo permitido: %s - %s", 
                campo, valor, min, max)
        );
        ex.addFieldError(campo, "Valor fora do intervalo permitido");
        ex.setValidationType("RANGE_VALIDATION");
        return ex;
    }
    
    /**
     * Cria ValidationException para filtro de busca inválido
     */
    public static ValidationException filtroBuscaInvalido(List<String> erros) {
        ValidationException ex = new ValidationException("Filtro de busca inválido");
        ex.setGlobalErrors(erros);
        ex.setValidationType("FILTER_VALIDATION");
        return ex;
    }
    
    /**
     * Cria ValidationException para tipo de operação inválido
     */
    public static ValidationException tipoOperacaoInvalido(String tipo) {
        ValidationException ex = new ValidationException(
            String.format("Tipo de operação inválido: %s", tipo)
        );
        ex.addGlobalError(ValidationUtil.getMensagemErro("tipoOperacao", "TIPO_OPERACAO"));
        ex.setValidationType("TIPO_OPERACAO_VALIDATION");
        return ex;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationException{");
        sb.append("message='").append(getMessage()).append('\'');
        
        if (validationType != null) {
            sb.append(", validationType='").append(validationType).append('\'');
        }
        
        if (!fieldErrors.isEmpty()) {
            sb.append(", fieldErrors=").append(fieldErrors);
        }
        
        if (!globalErrors.isEmpty()) {
            sb.append(", globalErrors=").append(globalErrors);
        }
        
        sb.append('}');
        return sb.toString();
    }
}