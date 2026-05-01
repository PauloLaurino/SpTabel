package com.seprocom.protesto.chat.dto.request;

import com.seprocom.protesto.chat.entity.Mensagem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para envio de mensagem no chat.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para envio de mensagem")
public class MensagemRequest {

    @NotNull(message = "ID do atendimento é obrigatório")
    @Schema(description = "ID do atendimento", example = "1", required = true)
    private Long atendimentoId;

    @NotNull(message = "Tipo de mensagem é obrigatório")
    @Schema(description = "Tipo de mensagem: 1=Texto, 2=Áudio, 3=Vídeo, 4=Imagem, 5=Anexo", example = "1", required = true)
    private Integer tipoMensagem;

    @NotBlank(message = "Mensagem é obrigatória")
    @Size(max = 5000, message = "Mensagem deve ter no máximo 5000 caracteres")
    @Schema(description = "Conteúdo da mensagem", example = "Olá, como posso ajudar?", required = true)
    private String mensagem;

    @Schema(description = "Nome do arquivo anexo", example = "intimacao.pdf")
    private String anexo;

    @Schema(description = "MIME type do anexo", example = "application/pdf")
    private String mimeType;

    @Schema(description = "Tamanho do anexo em bytes", example = "1024")
    private Integer tamanho;

    @Schema(description = "Duração em segundos (para áudio/vídeo)", example = "30")
    private Integer tempo;

    @Schema(description = "Conteúdo do arquivo em Base64 (para anexos)")
    private String arquivoBase64;

    @Schema(description = "Indica se a mensagem é do operador", example = "true")
    @Builder.Default
    private Boolean operador = true;

    // Métodos de compatibilidade
    public Mensagem.TipoMensagem getTipo() {
        return tipoMensagem != null ? Mensagem.TipoMensagem.fromValor(tipoMensagem) : Mensagem.TipoMensagem.TEXTO;
    }

    public String getConteudo() {
        return mensagem;
    }

    public boolean isOperador() {
        return operador != null && operador;
    }
}
