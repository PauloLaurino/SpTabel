package com.seprocom.protesto.chat.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * PasswordEncoder customizado que suporta tanto BCrypt quanto texto plano.
 * 
 * Para compatibilidade com sistemas legados, aceita senhas em texto plano
 * e também senhas em formato BCrypt.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Slf4j
public class CustomPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        // Sempre codifica em BCrypt para armazenamento
        return bcryptEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        
        // Se a senha armazenada é BCrypt
        if (encodedPassword.startsWith("$2")) {
            return bcryptEncoder.matches(rawPassword, encodedPassword);
        }
        
        // Para senhas em texto plano (compatibilidade legacy)
        // Compara diretamente
        if (rawPassword != null && rawPassword.toString().equals(encodedPassword)) {
            log.debug("Senha validada via texto plano (legacy)");
            return true;
        }
        
        // Tenta também como BCrypt (pode ter sido migrada)
        try {
            return bcryptEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            log.debug("Senha não validou: {}", e.getMessage());
            return false;
        }
    }
}
