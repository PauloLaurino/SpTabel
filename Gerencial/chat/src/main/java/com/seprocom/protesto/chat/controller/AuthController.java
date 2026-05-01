package com.seprocom.protesto.chat.controller;

import com.seprocom.protesto.chat.dto.request.LoginRequest;
import com.seprocom.protesto.chat.dto.response.AuthResponse;
import com.seprocom.protesto.chat.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller para autenticação.
 * 
 * Endpoints para login e refresh token.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação", description = "Endpoints para autenticação e autorização")
public class AuthController {

    private final AuthService authService;

    /**
     * Realiza o login do usuário.
     *
     * @param request Dados de login
     * @return Token JWT e dados do usuário
     */
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Realiza a autenticação do usuário e retorna o token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Tentativa de login para o usuário: {}", request.getLogin());
        AuthResponse response = authService.login(request);
        log.info("Login realizado com sucesso para o usuário: {}", request.getLogin());
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza o token JWT usando refresh token.
     *
     * @param refreshToken Refresh token
     * @return Novo token JWT
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Atualiza o token JWT usando refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    })
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        log.debug("Atualizando token JWT");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica se o token é válido.
     *
     * @return Status da validação
     */
    @GetMapping("/validate")
    @Operation(summary = "Validar Token", description = "Verifica se o token JWT é válido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    public ResponseEntity<Void> validateToken() {
        return ResponseEntity.ok().build();
    }

    /**
     * Realiza o logout do usuário.
     *
     * @return Status do logout
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Realiza o logout do usuário")
    @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso")
    public ResponseEntity<Void> logout() {
        log.info("Logout realizado");
        return ResponseEntity.ok().build();
    }
}
