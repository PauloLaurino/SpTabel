package com.seprocom.assinatura.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "assinatura")
public class ConfigProperties {
    
    private int sessaoMinutosValidade = 30;
    private String diretorioTemp = "/tmp/assinaturas";
    private TsaConfig tsa = new TsaConfig();
    private TokenConfig token = new TokenConfig();
    
    @Data
    public static class TsaConfig {
        private String url = "https://tsp.datalat.com.br/tsp";
        private String username;
        private String password;
        private int timeoutSegundos = 30;
    }
    
    @Data
    public static class TokenConfig {
        private String tipo = "PKCS11";
        private String biblioteca;
        private String pin;
        private String driver;
    }
}
