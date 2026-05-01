package com.seprocom.protesto.chat.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuração do Swagger/OpenAPI.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/chat}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(apiInfo())
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080" + contextPath).description("Desenvolvimento"),
                        new Server().url("http://192.168.10.70:8059" + contextPath).description("Produção")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, 
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT obtido no endpoint de login")
                        )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("Chat WhatsApp - Sistema de Protesto")
                .description("""
                        API RESTful para o módulo de chat estilo WhatsApp do Sistema de Gestão de Tabelionato de Protesto.
                        
                        ## Funcionalidades
                        
                        - **Autenticação**: Login com JWT
                        - **Atendimentos**: Gerenciamento de atendimentos a devedores
                        - **Mensagens**: Envio e recebimento de mensagens em tempo real
                        - **Intimações**: Envio de PDF de intimações
                        - **Comprovantes**: Recebimento de comprovantes de pagamento
                        
                        ## Integração com Sistema Existente
                        
                        O chat integra-se com a tabela `ctp001` através das chaves:
                        - `numapo1_001`
                        - `numapo2_001`
                        - `controle_001`
                        
                        ## Formatos de Data
                        
                        - `dtintimacao_001`: DDMMAAAA
                        - `dataocr_001`: AAAAMMDD
                        - `datapag_001`: DATE (SQL)
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Seprocom")
                        .email("suporte@seprocom.com.br")
                        .url("https://www.seprocom.com.br"))
                .license(new License()
                        .name("Proprietary")
                        .url("https://www.seprocom.com.br/licenca"));
    }
}
