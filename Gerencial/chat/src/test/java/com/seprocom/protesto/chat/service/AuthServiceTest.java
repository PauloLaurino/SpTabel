package com.seprocom.protesto.chat.service;

import com.seprocom.protesto.chat.dto.request.LoginRequest;
import com.seprocom.protesto.chat.dto.response.AuthResponse;
import com.seprocom.protesto.chat.entity.Usuario;
import com.seprocom.protesto.chat.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AuthService.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("admin")
                .senha("senhaCodificada")
                .cargo("ADMIN")
                .build();

        loginRequest = LoginRequest.builder()
                .login("admin")
                .senha("senha123")
                .build();
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso")
    void deveAutenticarUsuarioComSucesso() {
        // Arrange
        when(usuarioRepository.findByNomeIgnoreCase("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "senhaCodificada")).thenReturn(true);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("token-jwt-falso");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("token-jwt-falso", response.getAccessToken());
        assertEquals("admin", response.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção com usuário inexistente")
    void deveLancarExcecaoComUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findByNomeIgnoreCase("admin")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção com senha incorreta")
    void deveLancarExcecaoComSenhaIncorreta() {
        // Arrange
        when(usuarioRepository.findByNomeIgnoreCase("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "senhaCodificada")).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });
    }
}
