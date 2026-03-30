package com.selador.util;

import com.selador.exception.*;
import com.selador.web.dto.ErrorDTO;
import com.selador.web.dto.ResponseDTO;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Utilitário para tratamento centralizado de exceções
 */
public class ExceptionHandlerUtil {
    
    private static final Logger logger = Logger.getLogger(ExceptionHandlerUtil.class.getName());
    
    private ExceptionHandlerUtil() {
        // Classe utilitária - não instanciável
    }
    
    /**
     * Converte exceção para ErrorDTO
     */
    public static ErrorDTO toErrorDTO(Exception exception) {
        ErrorDTO errorDTO = new ErrorDTO();
        
        if (exception instanceof ValidationException) {
            ValidationException valEx = (ValidationException) exception;
            errorDTO.setCode("VALIDATION_ERROR");
            errorDTO.setMessage("Erro de validação");
            errorDTO.setDetail(exception.getMessage());
            
            // Adicionar erros de campo como detalhes extras
            if (valEx.hasErrors()) {
                StringBuilder details = new StringBuilder();
                for (String error : valEx.getAllErrors()) {
                    details.append(error).append("; ");
                }
                errorDTO.setDetail(details.toString());
            }
            
        } else if (exception instanceof DatabaseException) {
            DatabaseException dbEx = (DatabaseException) exception;
            errorDTO.setCode("DATABASE_ERROR");
            errorDTO.setMessage("Erro de banco de dados");
            errorDTO.setDetail(dbEx.getMessage());
            
            if (dbEx.isConnectionError()) {
                errorDTO.setCode("DATABASE_CONNECTION_ERROR");
                errorDTO.setMessage("Erro de conexão com o banco de dados");
            } else if (dbEx.isTimeoutError()) {
                errorDTO.setCode("DATABASE_TIMEOUT_ERROR");
                errorDTO.setMessage("Timeout na operação do banco de dados");
            }
            
        } else if (exception instanceof ServiceException) {
            ServiceException svcEx = (ServiceException) exception;
            errorDTO.setCode(svcEx.getErrorCode() != null ? svcEx.getErrorCode() : "SERVICE_ERROR");
            errorDTO.setMessage("Erro no serviço: " + svcEx.getServiceName());
            errorDTO.setDetail(svcEx.getMessage());
            
        } else if (exception instanceof IntegrationException) {
            IntegrationException intEx = (IntegrationException) exception;
            errorDTO.setCode("INTEGRATION_ERROR");
            errorDTO.setMessage("Erro de integração com Maker 5");
            errorDTO.setDetail(intEx.getMessage());
            
        } else {
            errorDTO.setCode("INTERNAL_ERROR");
            errorDTO.setMessage("Erro interno do sistema");
            errorDTO.setDetail(exception.getMessage());
        }
        
        return errorDTO;
    }
    
    /**
     * Converte exceção para ResponseDTO
     */
    public static <T> ResponseDTO<T> toResponseDTO(Exception exception) {
        ErrorDTO errorDTO = toErrorDTO(exception);
        
        ResponseDTO<T> response = ResponseDTO.error(errorDTO.getMessage());
        
        // Adicionar erros de validação como lista
        if (exception instanceof ValidationException) {
            ValidationException valEx = (ValidationException) exception;
            if (valEx.hasErrors()) {
                response.setErrors(valEx.getAllErrors());
            }
        }
        
        return response;
    }
    
    /**
     * Loga exceção de forma apropriada
     */
    public static void logException(Exception exception, String context) {
        String tipo = "ERRO_GERAL";
        Level nivel = Level.SEVERE;
        
        if (exception instanceof ValidationException) {
            tipo = "ERRO_VALIDACAO";
            nivel = Level.WARNING;
        } else if (exception instanceof DatabaseException) {
            tipo = "ERRO_BANCO";
        } else if (exception instanceof ServiceException) {
            tipo = "ERRO_SERVICO";
        } else if (exception instanceof IntegrationException) {
            tipo = "ERRO_INTEGRACAO";
        }
        
        String mensagem = String.format("[%s] %s: %s", tipo, context, exception.getMessage());
        
        if (exception.getCause() != null) {
            mensagem += " - Causa: " + exception.getCause().getMessage();
        }
        
        // Log usando java.util.logging
        logger.log(nivel, mensagem);
        
        // Log detalhado para debugging se necessário
        if (nivel == Level.FINE) {
            exception.printStackTrace();
        }
    }
    
    /**
     * Loga exceção com mensagem customizada
     */
    public static void logException(Exception exception, String context, String customMessage) {
        String tipo = "ERRO_GERAL";
        Level nivel = Level.SEVERE;
        
        if (exception instanceof ValidationException) {
            tipo = "ERRO_VALIDACAO";
            nivel = Level.WARNING;
        } else if (exception instanceof DatabaseException) {
            tipo = "ERRO_BANCO";
        } else if (exception instanceof ServiceException) {
            tipo = "ERRO_SERVICO";
        } else if (exception instanceof IntegrationException) {
            tipo = "ERRO_INTEGRACAO";
        }
        
        String mensagem = String.format("[%s] %s: %s - %s", 
            tipo, context, customMessage, exception.getMessage());
        
        logger.log(nivel, mensagem);
    }
    
    /**
     * Extrai informações da exceção para análise
     */
    public static Map<String, Object> extractExceptionInfo(Exception exception) {
        Map<String, Object> info = new HashMap<>();
        
        info.put("exceptionType", exception.getClass().getSimpleName());
        info.put("message", exception.getMessage());
        info.put("timestamp", System.currentTimeMillis());
        
        if (exception instanceof ValidationException) {
            ValidationException valEx = (ValidationException) exception;
            info.put("fieldErrors", valEx.getFieldErrors());
            info.put("globalErrors", valEx.getGlobalErrors());
            info.put("validationType", valEx.getValidationType());
            
        } else if (exception instanceof DatabaseException) {
            DatabaseException dbEx = (DatabaseException) exception;
            info.put("sql", dbEx.getSql());
            info.put("operation", dbEx.getOperation());
            info.put("errorCode", dbEx.getErrorCode());
            info.put("sqlState", dbEx.getSqlState());
            info.put("isConnectionError", dbEx.isConnectionError());
            info.put("isTimeoutError", dbEx.isTimeoutError());
            
        } else if (exception instanceof ServiceException) {
            ServiceException svcEx = (ServiceException) exception;
            info.put("errorCode", svcEx.getErrorCode());
            info.put("serviceName", svcEx.getServiceName());
            info.put("additionalData", svcEx.getAdditionalData());
            
        } else if (exception instanceof IntegrationException) {
            IntegrationException intEx = (IntegrationException) exception;
            info.put("integrationPoint", intEx.getIntegrationPoint());
            info.put("operation", intEx.getOperation());
            info.put("makerVersion", intEx.getMakerVersion());
        }
        
        if (exception.getCause() != null) {
            info.put("cause", exception.getCause().getClass().getSimpleName());
            info.put("causeMessage", exception.getCause().getMessage());
        }
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0) {
            List<String> simplifiedTrace = new ArrayList<>();
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                simplifiedTrace.add(stackTrace[i].toString());
            }
            info.put("stackTrace", simplifiedTrace);
        }
        
        return info;
    }
    
    /**
     * Determina o código HTTP apropriado para a exceção
     */
    public static int getHttpStatusCode(Exception exception) {
        if (exception instanceof ValidationException) {
            return 400; // Bad Request
        } else if (exception instanceof DatabaseException) {
            DatabaseException dbEx = (DatabaseException) exception;
            if (dbEx.isConnectionError()) {
                return 503; // Service Unavailable
            } else if (dbEx.isDataNotFoundError()) {
                return 404; // Not Found
            }
            return 500; // Internal Server Error
        } else if (exception instanceof ServiceException) {
            ServiceException svcEx = (ServiceException) exception;
            if ("SELO_NOT_FOUND".equals(svcEx.getErrorCode()) ||
                "APONTAMENTO_NAO_APTO".equals(svcEx.getErrorCode())) {
                return 404; // Not Found
            } else if ("SELOS_INSUFICIENTES".equals(svcEx.getErrorCode())) {
                return 409; // Conflict
            }
            return 500;
        } else if (exception instanceof IntegrationException) {
            return 502; // Bad Gateway
        } else {
            return 500; // Internal Server Error
        }
    }
    
    /**
     * Trata exceção e retorna mensagem amigável para o usuário
     */
    public static String getUserFriendlyMessage(Exception exception) {
        if (exception instanceof ValidationException) {
            return "Por favor, verifique os dados informados e tente novamente.";
        } else if (exception instanceof DatabaseException) {
            DatabaseException dbEx = (DatabaseException) exception;
            if (dbEx.isConnectionError()) {
                return "Não foi possível conectar ao banco de dados. Tente novamente em alguns instantes.";
            } else if (dbEx.isTimeoutError()) {
                return "A operação está demorando mais que o esperado. Tente novamente.";
            }
            return "Ocorreu um erro ao acessar os dados. Por favor, tente novamente.";
        } else if (exception instanceof ServiceException) {
            ServiceException svcEx = (ServiceException) exception;
            if ("SELO_NOT_FOUND".equals(svcEx.getErrorCode())) {
                return "Não há selos disponíveis para esta operação.";
            } else if ("SELOS_INSUFICIENTES".equals(svcEx.getErrorCode())) {
                return "Quantidade de selos disponíveis é insuficiente para processar todos os apontamentos.";
            } else if ("APONTAMENTO_NAO_APTO".equals(svcEx.getErrorCode())) {
                return "O apontamento não está apto para selagem.";
            }
            return "Ocorreu um erro ao processar sua solicitação. Por favor, tente novamente.";
        } else if (exception instanceof IntegrationException) {
            return "Ocorreu um erro na comunicação com o sistema Maker 5. Por favor, verifique a integração.";
        } else {
            return "Ocorreu um erro inesperado. Por favor, entre em contato com o suporte.";
        }
    }
    
    /**
     * Método simplificado para logar exceção sem contexto
     */
    public static void logException(Exception exception) {
        logException(exception, "Sistema");
    }
    
    /**
     * Método para logar warning
     */
    public static void logWarning(String message, Exception exception) {
        String logMessage = String.format("[WARNING] %s: %s", message, exception.getMessage());
        logger.log(Level.WARNING, logMessage);
    }
    
    /**
     * Método para logar informação
     */
    public static void logInfo(String message) {
        logger.log(Level.INFO, message);
    }
    
    /**
     * Método para logar erro grave
     */
    public static void logSevere(String message, Exception exception) {
        String logMessage = String.format("[SEVERE] %s: %s", message, exception.getMessage());
        logger.log(Level.SEVERE, logMessage, exception);
    }
    
    /**
     * Método para verificar se é uma exceção que deve ser relançada
     */
    public static boolean shouldRethrow(Exception exception) {
        // Exceções que indicam problemas graves devem ser relançadas
        if (exception instanceof DatabaseException) {
            DatabaseException dbEx = (DatabaseException) exception;
            return dbEx.isConnectionError() || dbEx.isTimeoutError();
        }
        
        // Outras exceções que devem ser relançadas
        return exception instanceof IntegrationException ||
               exception instanceof RuntimeException;
    }
    
    /**
     * Método para formatar exceção para log detalhado
     */
    public static String formatExceptionForLog(Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception: ").append(exception.getClass().getName()).append("\n");
        sb.append("Message: ").append(exception.getMessage()).append("\n");
        sb.append("StackTrace:\n");
        
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append("  at ").append(element.toString()).append("\n");
        }
        
        if (exception.getCause() != null) {
            sb.append("Caused by: ").append(exception.getCause().getClass().getName()).append("\n");
            sb.append("Caused by Message: ").append(exception.getCause().getMessage()).append("\n");
        }
        
        return sb.toString();
    }
}