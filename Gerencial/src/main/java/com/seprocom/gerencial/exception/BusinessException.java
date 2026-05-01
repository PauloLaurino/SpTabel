package com.seprocom.gerencial.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excessao de negocio para erros esperados na aplicacao.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, String code) {
        super(message);
    }

    public BusinessException(String message, String code, Throwable cause) {
        super(message, cause);
    }
}