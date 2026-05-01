package com.seprocom.protesto.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de autenticação.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de autenticação com tokens e dados do usuário")
public class AuthResponse {

    @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Refresh token para renovação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Tempo de expiração em segundos", example = "86400")
    private Long expiresIn;

    @Schema(description = "ID do operador", example = "1")
    private Long operadorId;

    @Schema(description = "Nome do operador", example = "João Silva")
    private String nome;

    @Schema(description = "Login do operador", example = "joao.silva")
    private String login;

    @Schema(description = "Perfil do operador", example = "ADMIN")
    private String perfil;

    @Schema(description = "ID do departamento", example = "1")
    private Long departamentoId;
}
