package com.seprocom.protesto.chat.exception;

/**
 * Exceção de negócio da aplicação.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
