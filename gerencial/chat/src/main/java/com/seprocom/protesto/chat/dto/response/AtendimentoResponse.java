package com.seprocom.protesto.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de resposta para atendimento.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de atendimento")
public class AtendimentoResponse {

    @Schema(description = "ID do atendimento", example = "1")
    private Long id;

    @Schema(description = "ID do contato", example = "1")
    private Long contatoId;

    @Schema(description = "Nome do contato", example = "João Silva")
    private String contatoNome;

    @Schema(description = "Telefone do contato", example = "11999999999")
    private String contatoTelefone;

    @Schema(description = "ID do operador", example = "1")
    private Long operadorId;

    @Schema(description = "Nome do operador", example = "Maria Santos")
    private String operadorNome;

    @Schema(description = "ID do departamento", example = "1")
    private Long departamentoId;

    @Schema(description = "Nome do departamento", example = "Atendimento")
    private String departamentoNome;

    @Schema(description = "Status: P=Pendente, F=Na Fila, A=Em Atendimento, E=Encerrado, T=Transferido", example = "A")
    private String status;

    @Schema(description = "Data de entrada na fila", example = "2024-01-15T10:00:00")
    private LocalDateTime dataEntrada;

    @Schema(description = "Data de abertura do atendimento", example = "2024-01-15T10:05:00")
    private LocalDateTime dataAbertura;

    @Schema(description = "Data de fechamento", example = "2024-01-15T11:00:00")
    private LocalDateTime dataFechamento;

    @Schema(description = "Data da última mensagem", example = "2024-01-15T10:30:00")
    private LocalDateTime dataUltimaMensagem;

    @Schema(description = "Data de criação", example = "2024-01-15T10:00:00")
    private LocalDateTime dataCriacao;
}
