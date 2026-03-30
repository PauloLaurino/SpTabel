package com.selador.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Utilitário para segurança, criptografia e geração de tokens
 */
public class SecurityUtil {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "SeladorMaker5Key!@"; // 16 caracteres para AES-128
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    private SecurityUtil() {
        // Classe utilitária - não instanciável
    }
    
    /**
     * Criptografa texto usando AES
     */
    public static String encrypt(String texto) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
            
        } catch (Exception e) {
            LogUtil.log("SECURITY", "ERROR", "Erro ao criptografar: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Descriptografa texto usando AES
     */
    public static String decrypt(String textoCriptografado) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decodedBytes = Base64.getDecoder().decode(textoCriptografado);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            LogUtil.log("SECURITY", "ERROR", "Erro ao descriptografar: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gera hash MD5
     */
    public static String hashMD5(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            LogUtil.log("SECURITY", "ERROR", "MD5 não disponível: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gera hash SHA-256
     */
    public static String hashSHA256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            LogUtil.log("SECURITY", "ERROR", "SHA-256 não disponível: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gera token de segurança único
     */
    public static String generateToken() {
        // Combinação de UUID e timestamp
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf(SECURE_RANDOM.nextInt(999999));
        
        return hashSHA256(uuid + timestamp + random).substring(0, 32);
    }
    
    /**
     * Gera senha aleatória
     */
    public static String generatePassword(int length) {
        if (length < 8) length = 8;
        if (length > 32) length = 32;
        
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                          "abcdefghijklmnopqrstuvwxyz" +
                          "0123456789" +
                          "!@#$%^&*()-_=+";
        
        StringBuilder senha = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(caracteres.length());
            senha.append(caracteres.charAt(index));
        }
        
        return senha.toString();
    }
    
    /**
     * Valida força da senha
     */
    public static boolean isPasswordStrong(String senha) {
        if (senha == null || senha.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : senha.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Ofusca dados sensíveis (para logs) - Compatível com Java 8
     */
    public static String obfuscateSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        
        // Manter primeiros 2 e últimos 2 caracteres, ofuscar o resto
        int keepStart = 2;
        int keepEnd = 2;
        
        if (data.length() <= keepStart + keepEnd) {
            return "****";
        }
        
        String start = data.substring(0, keepStart);
        String end = data.substring(data.length() - keepEnd);
        
        // Criar string de asteriscos do tamanho do meio (Java 8 compatível)
        int middleLength = data.length() - keepStart - keepEnd;
        StringBuilder middle = new StringBuilder();
        for (int i = 0; i < middleLength; i++) {
            middle.append("*");
        }
        
        return start + middle.toString() + end;
    }
    
    /**
     * Ofusca CPF/CNPJ
     */
    public static String obfuscateCpfCnpj(String documento) {
        if (documento == null) return "****";
        
        String numeros = documento.replaceAll("[^0-9]", "");
        if (numeros.length() == 11) { // CPF
            return "***." + numeros.substring(3, 6) + ".***-**";
        } else if (numeros.length() == 14) { // CNPJ
            return "**." + numeros.substring(2, 5) + "." + 
                   numeros.substring(5, 8) + "/****-**";
        }
        
        return obfuscateSensitiveData(documento);
    }
    
    /**
     * Valida se token não expirou
     */
    public static boolean isTokenValid(String token, long maxAgeMinutes) {
        try {
            // Token contém timestamp no formato: token_timestamp
            String[] parts = token.split("_");
            if (parts.length != 2) return false;
            
            long tokenTime = Long.parseLong(parts[1]);
            long currentTime = System.currentTimeMillis();
            long ageMinutes = (currentTime - tokenTime) / (1000 * 60);
            
            return ageMinutes <= maxAgeMinutes;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gera token com timestamp
     */
    public static String generateTimestampedToken() {
        String token = generateToken();
        long timestamp = System.currentTimeMillis();
        return token + "_" + timestamp;
    }
    
    /**
     * Calcula CRC32 (para verificação de integridade)
     */
    public static long calculateCRC32(String data) {
        java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        crc32.update(data.getBytes(StandardCharsets.UTF_8));
        return crc32.getValue();
    }
    
    /**
     * Codifica em Base64
     */
    public static String encodeBase64(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Decodifica de Base64
     */
    public static String decodeBase64(String textoCodificado) {
        byte[] decodedBytes = Base64.getDecoder().decode(textoCodificado);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Sanitiza entrada contra XSS
     */
    public static String sanitizeForXSS(String input) {
        if (input == null) return "";
        
        return input.replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;")
                   .replaceAll("\\(", "&#40;")
                   .replaceAll("\\)", "&#41;")
                   .replaceAll("&", "&amp;");
    }
    
    /**
     * Valida se string não contém SQL injection
     */
    public static boolean isSqlInjectionSafe(String input) {
        if (input == null) return true;
        
        String lowerInput = input.toLowerCase();
        String[] dangerousPatterns = {
            "select.*from", "insert.*into", "update.*set", "delete.*from",
            "drop.*table", "drop.*database", "truncate.*table",
            "union.*select", "exec.*", "execute.*", "xp_cmdshell",
            "--", ";", "/*", "*/", "' or '1'='1", "\" or \"1\"=\"1"
        };
        
        for (String pattern : dangerousPatterns) {
            if (lowerInput.matches(".*" + pattern + ".*")) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Valida assinatura HMAC (simplificada para Java 8)
     */
    public static boolean validateHMAC(String data, String signature, String secret) {
        try {
            String expectedSignature = hashSHA256(data + secret);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gera assinatura HMAC (simplificada)
     */
    public static String generateHMAC(String data, String secret) {
        return hashSHA256(data + secret);
    }
    
    /**
     * Limpa dados sensíveis de objetos (para logs)
     */
    public static String cleanSensitiveDataForLog(String logData) {
        if (logData == null) return "";
        
        // Padrões para dados sensíveis
        String[] sensitivePatterns = {
            "senha", "password", "token", "chave", "key", 
            "cpf", "cnpj", "rg", "cartao", "credito"
        };
        
        String result = logData;
        for (String pattern : sensitivePatterns) {
            // Substituir valores após os padrões sensíveis
            result = result.replaceAll(
                "(?i)(" + pattern + "\\s*[:=]\\s*)[^,\\s\\]]+", 
                "$1[PROTEGIDO]"
            );
        }
        
        return result;
    }
    
    /**
     * Versão alternativa usando array de char para ofuscar
     */
    public static String obfuscateSensitiveDataAlt(String data) {
        if (data == null) return "****";
        
        int len = data.length();
        if (len <= 4) return "****";
        
        // Manter 2 primeiros e 2 últimos caracteres
        String start = data.substring(0, 2);
        String end = data.substring(len - 2);
        
        // Criar array de asteriscos
        char[] asterisks = new char[len - 4];
        java.util.Arrays.fill(asterisks, '*');
        
        return start + new String(asterisks) + end;
    }
}