package com.seprocom.protesto.chat.service;

import com.seprocom.protesto.chat.entity.Usuario;
import com.seprocom.protesto.chat.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Serviço para carregar detalhes do usuário para autenticação.
 * 
 * Implementa UserDetailsService do Spring Security.
 * Utiliza a tabela existente web_usuario.
 * Suporta senhas em BCrypt ou texto plano (para compatibilidade).
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Carregando usuário: {}", username);

        Usuario usuario = usuarioRepository.findByNome(username)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });

        // Determina a role baseada no cargo do usuário
        String role = determineRole(usuario.getCargo());

        // Retorna usuário com senha codificada
        // O PasswordEncoder vai validar tanto BCrypt quanto texto plano
        return User.builder()
                .username(usuario.getNome())
                .password(usuario.getSenha())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority(role)
                ))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Valida a senha do usuário.
     * Suporta tanto senhas em BCrypt quanto em texto plano.
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        // Se a senha armazenada já é BCrypt, usa o encoder
        if (encodedPassword.startsWith("$2") || encodedPassword.startsWith("$")) {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        }
        // Para senhas em texto plano, compara diretamente
        return rawPassword.equals(encodedPassword);
    }

    /**
     * Determina a role baseada no cargo do usuário.
     * Mapeia cargos da tabela web_usuario para roles do Spring Security.
     */
    private String determineRole(String cargo) {
        if (cargo == null || cargo.isBlank()) {
            return "ROLE_USER";
        }

        return switch (cargo.toUpperCase()) {
            case "ADMIN", "ADMINISTRADOR" -> "ROLE_ADMIN";
            case "SUPERVISOR", "GERENTE" -> "ROLE_SUPERVISOR";
            case "OPERADOR", "ATENDENTE" -> "ROLE_OPERATOR";
            default -> "ROLE_USER";
        };
    }
}
