package com.seprocom.protesto.chat.websocket;

import com.seprocom.protesto.chat.dto.response.MensagemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para mensagens do WebSocket.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    /**
     * Tipo da mensagem.
     * Valores: JOIN, JOINED, MESSAGE, NEW_MESSAGE, TYPING, LEAVE, ERROR
     */
    private String type;

    /**
     * ID do atendimento.
     */
    private Long atendimentoId;

    /**
     * Conteúdo da mensagem (para mensagens de texto).
     */
    private String content;

    /**
     * Tipo da mensagem (TEXTO, IMAGEM, DOCUMENTO, AUDIO).
     */
    private String messageType;

    /**
     * Se o remetente é operador.
     */
    private boolean operador;

    /**
     * Se está digitando.
     */
    private boolean typing;

    /**
     * Mensagem completa (para NEW_MESSAGE).
     */
    private MensagemResponse message;

    /**
     * Dados do vínculo de intimação (para INTIMACAO_VINCULADA, PDF_ENVIADO, COMPROVANTE_ENVIADO).
     */
    private VinculoNotificacao vinculo;

    /**
     * Dados adicionais para notificações.
     */
    private Object data;

    /**
     * Classe interna para notificações de vínculo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VinculoNotificacao {
        private Long vinculoId;
        private String numapo1_001;
        private String numapo2_001;
        private String controle_001;
        private boolean pdfEnviado;
        private boolean comprovanteRecebido;
        private String dataEnvioPdf;
        private String dataRecebimentoComprovante;
    }
}
