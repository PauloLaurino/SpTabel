package com.seprocom.protesto.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seprocom.protesto.chat.dto.request.MensagemRequest;
import com.seprocom.protesto.chat.dto.response.MensagemResponse;
import com.seprocom.protesto.chat.service.AtendimentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler do WebSocket para comunicação em tempo real.
 * 
 * Gerencia conexões e mensagens do chat.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final AtendimentoService atendimentoService;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    // Mapa de sessões ativas por ID do atendimento
    private final Map<Long, ConcurrentHashMap<String, WebSocketSession>> sessionsByAtendimento = new ConcurrentHashMap<>();

    // Mapa de sessões por ID da sessão
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Nova conexão WebSocket estabelecida: {}", session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            log.debug("Mensagem recebida: {}", payload);

            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);

            switch (wsMessage.getType()) {
                case "JOIN":
                    handleJoin(session, wsMessage);
                    break;
                case "MESSAGE":
                    handleMessage(session, wsMessage);
                    break;
                case "TYPING":
                    handleTyping(session, wsMessage);
                    break;
                case "LEAVE":
                    handleLeave(session, wsMessage);
                    break;
                default:
                    log.warn("Tipo de mensagem desconhecido: {}", wsMessage.getType());
            }
        } catch (Exception e) {
            log.error("Erro ao processar mensagem: {}", e.getMessage());
            sendError(session, "Erro ao processar mensagem");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Conexão WebSocket fechada: {} - Status: {}", session.getId(), status);
        
        // Remove a sessão de todos os mapas
        sessions.remove(session.getId());
        sessionsByAtendimento.forEach((atendimentoId, sessionsMap) -> {
            sessionsMap.remove(session.getId());
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Erro de transporte WebSocket: {}", exception.getMessage());
    }

    /**
     * Trata entrada em um atendimento.
     */
    private void handleJoin(WebSocketSession session, WebSocketMessage wsMessage) throws IOException {
        Long atendimentoId = wsMessage.getAtendimentoId();
        
        if (atendimentoId == null) {
            sendError(session, "ID do atendimento é obrigatório");
            return;
        }

        // Adiciona a sessão ao mapa do atendimento
        sessionsByAtendimento.computeIfAbsent(atendimentoId, k -> new ConcurrentHashMap<>())
                .put(session.getId(), session);

        // Envia confirmação
        sendMessage(session, WebSocketMessage.builder()
                .type("JOINED")
                .atendimentoId(atendimentoId)
                .build());

        log.info("Sessão {} entrou no atendimento {}", session.getId(), atendimentoId);
    }

    /**
     * Trata envio de mensagem.
     */
    private void handleMessage(WebSocketSession session, WebSocketMessage wsMessage) throws IOException {
        Long atendimentoId = wsMessage.getAtendimentoId();
        String content = wsMessage.getContent();
        
        if (atendimentoId == null || content == null) {
            sendError(session, "ID do atendimento e conteúdo são obrigatórios");
            return;
        }

        // Converte tipo de mensagem de String para Integer
        Integer tipoMensagem = 1; // default: TEXTO
        if (wsMessage.getMessageType() != null) {
            try {
                tipoMensagem = Integer.parseInt(wsMessage.getMessageType());
            } catch (NumberFormatException e) {
                // Se não for número, tenta converter por nome
                switch (wsMessage.getMessageType().toUpperCase()) {
                    case "TEXTO": tipoMensagem = 1; break;
                    case "IMAGEM": tipoMensagem = 2; break;
                    case "DOCUMENTO": tipoMensagem = 3; break;
                    case "AUDIO": tipoMensagem = 4; break;
                    default: tipoMensagem = 1;
                }
            }
        }

        // Cria a mensagem no banco
        MensagemRequest request = MensagemRequest.builder()
                .atendimentoId(atendimentoId)
                .tipoMensagem(tipoMensagem)
                .mensagem(content)
                .operador(wsMessage.isOperador())
                .build();

        MensagemResponse response = atendimentoService.enviarMensagem(atendimentoId, request);

        // Broadcast para todos os participantes do atendimento
        broadcastToAtendimento(atendimentoId, WebSocketMessage.builder()
                .type("NEW_MESSAGE")
                .atendimentoId(atendimentoId)
                .message(response)
                .build());

        // ==========================================
        // 🚀 INTEGRAÇÃO COM IA: IA / AJUDA
        // ==========================================
        String textLower = content.toLowerCase();
        if (textLower.startsWith("ia ") || textLower.startsWith("ajuda ")) {
            log.info("[AI] Comando IA detectado no Gerencial: {}", content);
            
            String query = content.replaceAll("(?i)^(ia|ajuda)\\s*", "");
            
            // Busca resposta da IA
            String aiReply = aiService.getAiResponse(query);
            
            // Salva resposta da IA no banco (como se fosse o operador respondendo)
            MensagemRequest aiRequest = MensagemRequest.builder()
                    .atendimentoId(atendimentoId)
                    .tipoMensagem(1) // TEXTO
                    .mensagem(aiReply)
                    .operador(true)
                    .build();
            
            MensagemResponse aiResponse = atendimentoService.enviarMensagem(atendimentoId, aiRequest);
            
            // Broadcast da resposta da IA
            broadcastToAtendimento(atendimentoId, WebSocketMessage.builder()
                    .type("NEW_MESSAGE")
                    .atendimentoId(atendimentoId)
                    .message(aiResponse)
                    .build());
        }

        log.info("Mensagem enviada no atendimento {}: {}", atendimentoId, response.getId());
    }

    /**
     * Trata indicador de digitação.
     */
    private void handleTyping(WebSocketSession session, WebSocketMessage wsMessage) throws IOException {
        Long atendimentoId = wsMessage.getAtendimentoId();
        
        if (atendimentoId == null) {
            return;
        }

        // Broadcast para outros participantes
        broadcastToAtendimentoExcept(atendimentoId, session.getId(), WebSocketMessage.builder()
                .type("TYPING")
                .atendimentoId(atendimentoId)
                .content(wsMessage.isTyping() ? "typing" : "stopped")
                .operador(wsMessage.isOperador())
                .build());
    }

    /**
     * Trata saída do atendimento.
     */
    private void handleLeave(WebSocketSession session, WebSocketMessage wsMessage) {
        Long atendimentoId = wsMessage.getAtendimentoId();
        
        if (atendimentoId != null) {
            ConcurrentHashMap<String, WebSocketSession> sessionsMap = sessionsByAtendimento.get(atendimentoId);
            if (sessionsMap != null) {
                sessionsMap.remove(session.getId());
            }
            log.info("Sessão {} saiu do atendimento {}", session.getId(), atendimentoId);
        }
    }

    /**
     * Envia mensagem para uma sessão específica.
     */
    private void sendMessage(WebSocketSession session, WebSocketMessage message) throws IOException {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }
    }

    /**
     * Envia mensagem de erro.
     */
    private void sendError(WebSocketSession session, String error) throws IOException {
        sendMessage(session, WebSocketMessage.builder()
                .type("ERROR")
                .content(error)
                .build());
    }

    /**
     * Envia mensagem para todos os participantes de um atendimento.
     */
    public void broadcastToAtendimento(Long atendimentoId, WebSocketMessage message) throws IOException {
        ConcurrentHashMap<String, WebSocketSession> sessionsMap = sessionsByAtendimento.get(atendimentoId);
        if (sessionsMap != null) {
            String json = objectMapper.writeValueAsString(message);
            for (WebSocketSession session : sessionsMap.values()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        }
    }

    /**
     * Envia mensagem para todos os participantes de um atendimento, exceto o remetente.
     */
    private void broadcastToAtendimentoExcept(Long atendimentoId, String exceptSessionId, WebSocketMessage message) throws IOException {
        ConcurrentHashMap<String, WebSocketSession> sessionsMap = sessionsByAtendimento.get(atendimentoId);
        if (sessionsMap != null) {
            String json = objectMapper.writeValueAsString(message);
            for (Map.Entry<String, WebSocketSession> entry : sessionsMap.entrySet()) {
                if (!entry.getKey().equals(exceptSessionId) && entry.getValue().isOpen()) {
                    entry.getValue().sendMessage(new TextMessage(json));
                }
            }
        }
    }

    /**
     * Notifica nova mensagem em um atendimento.
     */
    public void notificarNovaMensagem(Long atendimentoId, MensagemResponse mensagem) {
        try {
            broadcastToAtendimento(atendimentoId, WebSocketMessage.builder()
                    .type("NEW_MESSAGE")
                    .atendimentoId(atendimentoId)
                    .message(mensagem)
                    .build());
        } catch (IOException e) {
            log.error("Erro ao notificar nova mensagem: {}", e.getMessage());
        }
    }

    /**
     * Notifica vínculo de intimação em um atendimento.
     */
    public void notificarIntimacaoVinculada(Long atendimentoId, WebSocketMessage.VinculoNotificacao vinculo) {
        try {
            broadcastToAtendimento(atendimentoId, WebSocketMessage.builder()
                    .type("INTIMACAO_VINCULADA")
                    .atendimentoId(atendimentoId)
                    .vinculo(vinculo)
                    .build());
            log.info("Notificação de intimação vinculada enviada para atendimento {}", atendimentoId);
        } catch (IOException e) {
            log.error("Erro ao notificar intimação vinculada: {}", e.getMessage());
        }
    }

    /**
     * Notifica envio de PDF de intimação.
     */
    public void notificarPdfEnviado(Long atendimentoId, WebSocketMessage.VinculoNotificacao vinculo) {
        try {
            broadcastToAtendimento(atendimentoId, WebSocketMessage.builder()
                    .type("PDF_ENVIADO")
                    .atendimentoId(atendimentoId)
                    .vinculo(vinculo)
                    .build());
            log.info("Notificação de PDF enviado para atendimento {}", atendimentoId);
        } catch (IOException e) {
            log.error("Erro ao notificar PDF enviado: {}", e.getMessage());
        }
    }

    /**
     * Notifica recebimento de comprovante de pagamento.
     */
    public void notificarComprovanteRecebido(Long atendimentoId, WebSocketMessage.VinculoNotificacao vinculo) {
        try {
            broadcastToAtendimento(atendimentoId, WebSocketMessage.builder()
                    .type("COMPROVANTE_RECEBIDO")
                    .atendimentoId(atendimentoId)
                    .vinculo(vinculo)
                    .build());
            log.info("Notificação de comprovante recebido para atendimento {}", atendimentoId);
        } catch (IOException e) {
            log.error("Erro ao notificar comprovante recebido: {}", e.getMessage());
        }
    }

    /**
     * Notifica atualização de status do atendimento.
     */
    public void notificarStatusAtendimento(Long atendimentoId, String novoStatus) {
        try {
            broadcastToAtendimento(atendimentoId, WebSocketMessage.builder()
                    .type("STATUS_UPDATE")
                    .atendimentoId(atendimentoId)
                    .content(novoStatus)
                    .build());
            log.info("Notificação de status {} enviada para atendimento {}", novoStatus, atendimentoId);
        } catch (IOException e) {
            log.error("Erro ao notificar status do atendimento: {}", e.getMessage());
        }
    }

    /**
     * Notifica pagamento registrado no sistema de protesto.
     */
    public void notificarPagamentoRegistrado(Long atendimentoId, String chaves, String dataPagamento) {
        try {
            broadcastToAtendimento(atendimentoId, WebSocketMessage.builder()
                    .type("PAGAMENTO_REGISTRADO")
                    .atendimentoId(atendimentoId)
                    .content(chaves)
                    .data(dataPagamento)
                    .build());
            log.info("Notificação de pagamento registrado para atendimento {}", atendimentoId);
        } catch (IOException e) {
            log.error("Erro ao notificar pagamento registrado: {}", e.getMessage());
        }
    }
}
