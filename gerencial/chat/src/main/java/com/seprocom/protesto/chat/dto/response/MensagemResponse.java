package com.seprocom.protesto.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para mensagem.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de mensagem")
public class MensagemResponse {

    @Schema(description = "ID da mensagem", example = "1")
    private Long id;

    @Schema(description = "ID do atendimento", example = "1")
    private Long atendimentoId;

    @Schema(description = "Tipo de mensagem: 1=Texto, 2=Áudio, 3=Vídeo, 4=Imagem, 5=Anexo", example = "1")
    private Integer tipoMensagem;

    @Schema(description = "Conteúdo da mensagem")
    private String mensagem;

    @Schema(description = "Nome do arquivo anexo")
    private String anexo;

    @Schema(description = "Direção: E=Enviada, R=Recebida", example = "E")
    private String entradaSaida;

    @Schema(description = "Data/hora da mensagem", example = "2024-01-15T10:00:00")
    private LocalDateTime dataHora;

    @Schema(description = "Status: 1=Enviada, 2=Entregue, 3=Lida, 4=Erro", example = "1")
    private Integer status;
}
