package com.seprocom.assinatura.exception;

import com.seprocom.assinatura.dto.AssinaturaResponse;
import com.seprocom.assinatura.dto.DadosCertificado;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AssinaturaException.class)
    public ResponseEntity<AssinaturaResponse> handleAssinaturaException(AssinaturaException ex) {
        AssinaturaResponse response = AssinaturaResponse.builder()
                .status("erro")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AssinaturaResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Erro de validação");

        AssinaturaResponse response = AssinaturaResponse.builder()
                .status("erro")
                .mensagem(mensagem)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AssinaturaResponse> handleGenericException(Exception ex) {
        AssinaturaResponse response = AssinaturaResponse.builder()
                .status("erro")
                .mensagem("Erro interno: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
