package com.seprocom.gerencial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.seprocom.assinatura", "com.seprocom.gerencial"})
public class AplicacaoPrincipal {

    public static void main(String[] args) {
        SpringApplication.run(AplicacaoPrincipal.class, args);
    }
}
