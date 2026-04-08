package com.seprocom.gerencial.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Serviço para geração e validação de tokens JWT.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    /**
     * Extrai o nome de usuário do token.
     * @param token Token JWT
     * @return Nome de usuário
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai uma claim específica do token.
     * @param token Token JWT
     * @param claimsResolver Função para extrair a claim
     * @return Claim extraída
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Gera um token para o usuário.
     * @param userDetails Detalhes do usuário
     * @return Token JWT
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Gera um token com claims extras para o usuário.
     * @param extraClaims Claims extras
     * @param userDetails Detalhes do usuário
     * @return Token JWT
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 7)) // 7 days
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Gera um token de refresh.
     * @param userDetails Detalhes do usuário
     * @return Refresh token JWT
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)) // 30 days
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Verifica se o token é válido para o usuário.
     * @param token Token JWT
     * @param userDetails Detalhes do usuário
     * @return true se o token é válido
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica se o token está expirado.
     * @param token Token JWT
     * @return true se o token está expirado
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token.
     * @param token Token JWT
     * @return Data de expiração
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrai todas as claims do token.
     * @param token Token JWT
     * @return Claims extraídas
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Erro ao extrair claims do token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtém a chave de assinatura.
     * @return Chave de assinatura
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}