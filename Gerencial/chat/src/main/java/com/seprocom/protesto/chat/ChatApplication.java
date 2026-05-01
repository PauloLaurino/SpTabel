package com.seprocom.protesto.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicação principal do Chat WhatsApp para Sistema de Protesto.
 * 
 * Esta aplicação permite o atendimento de devedores via chat,
 * integração com intimações/boletos e registro de pagamentos.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ChatApplication extends SpringBootServletInitializer {

    /**
     * Construtor padrão para deploy como WAR no Tomcat 10+.
     */
    public ChatApplication() {
        super();
    }

    /**
     * Configuração para deploy como WAR.
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ChatApplication.class);
    }

    /**
     * Método principal para execução standalone (desenvolvimento).
     */
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}
