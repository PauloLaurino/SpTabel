package com.seprocom.gerencial.assinatura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.seprocom.assinatura.config.ConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class AplicacaoAssinatura {

    public static void main(String[] args) {
        SpringApplication.run(AplicacaoAssinatura.class, args);
    }
}
