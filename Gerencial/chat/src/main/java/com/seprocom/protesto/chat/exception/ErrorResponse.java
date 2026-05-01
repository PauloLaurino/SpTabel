package com.seprocom.protesto.chat.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para resposta de erro padronizada.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta de erro padronizada")
public class ErrorResponse {

    @Schema(description = "Data e hora do erro", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código do status HTTP", example = "400")
    private int status;

    @Schema(description = "Descrição do erro", example = "Bad Request")
    private String error;

    @Schema(description = "Magem detalhada do erro", example = "Dados de entrada inválidos")
    private String message;

    @Schema(description = "Caminho da requisição", example = "/api/atendimentos")
    private String path;

    @Schema(description = "Lista de erros de validação por campo")
    private Map<String, String> errors;
}
