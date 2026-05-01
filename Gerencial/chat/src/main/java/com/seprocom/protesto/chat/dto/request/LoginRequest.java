package com.seprocom.protesto.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de login.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de login do usuário")
public class LoginRequest {

    @NotBlank(message = "Login é obrigatório")
    @Schema(description = "Login do usuário", example = "admin", required = true)
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "123456", required = true)
    private String senha;
}
