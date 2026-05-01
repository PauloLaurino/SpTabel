package com.seprocom.gerencial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Classe principal da aplicação Spring Boot.
 * Emissor de Notas Fiscais de Serviços Eletrônicas (NFS-e)
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@SpringBootApplication
public class GerencialNfseApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GerencialNfseApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(GerencialNfseApplication.class);
    }
}