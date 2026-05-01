package com.seprocom.protesto.chat.config;

import com.seprocom.protesto.chat.websocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuração do WebSocket para comunicação em tempo real.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/chat")
                .setAllowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:5173",
                        "http://192.168.10.30:3001",
                        "http://192.168.10.70:8059",
                        "https://code.result.inf.br",
                        "http://192.168.10.30",
                        "http://192.168.10.70",
                        "http://*",
                        "https://*"
                );
    }
}
