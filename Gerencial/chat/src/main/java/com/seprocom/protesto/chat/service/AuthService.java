package com.seprocom.protesto.chat.service;

import com.seprocom.protesto.chat.dto.request.LoginRequest;
import com.seprocom.protesto.chat.dto.response.AuthResponse;
import com.seprocom.protesto.chat.entity.Usuario;
import com.seprocom.protesto.chat.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Serviço de autenticação.
 * 
 * Gerencia login, refresh token e validação de tokens.
 * Utiliza a tabela web_usuario existente.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Realiza o login do usuário.
     *
     * @param request Dados de login
     * @return Resposta com tokens e dados do usuário
     */
    public AuthResponse login(LoginRequest request) {
        // Autentica o usuário
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getSenha()
                )
        );

        // Carrega os dados do usuário
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());
        
        // Busca dados adicionais do usuário
        Usuario usuario = usuarioRepository.findByNome(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Gera os tokens
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .operadorId(usuario.getId())
                .nome(usuario.getNome())
                .login(usuario.getNome())
                .perfil(usuario.getCargo() != null ? usuario.getCargo() : "USER")
                .departamentoId(null)
                .build();
    }

    /**
     * Atualiza o token usando refresh token.
     *
     * @param refreshToken Refresh token
     * @return Novo token de acesso
     */
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        
        if (username == null) {
            throw new RuntimeException("Refresh token inválido");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token expirado ou inválido");
        }

        Usuario usuario = usuarioRepository.findByNome(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .operadorId(usuario.getId())
                .nome(usuario.getNome())
                .login(usuario.getNome())
                .perfil(usuario.getCargo() != null ? usuario.getCargo() : "USER")
                .departamentoId(null)
                .build();
    }
}
