package com.seprocom.protesto.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para vincular uma intimação/boleto a um atendimento.
 * 
 * Utiliza as chaves da tabela ctp001: numapo1_001, numapo2_001, controle_001
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para vincular intimação/boleto ao atendimento")
public class VincularIntimacaoRequest {

    @NotNull(message = "ID do atendimento é obrigatório")
    @Schema(description = "ID do atendimento", example = "1", required = true)
    private Long atendimentoId;

    // ========================================
    // Chaves da tabela ctp001
    // Os 3 campos juntos formam 1 chave composta única
    // ========================================

    @NotNull(message = "numapo1_001 é obrigatório")
    @Size(min = 4, max = 4, message = "numapo1_001 deve ter 4 caracteres (AAAAMM)")
    @Schema(description = "Número do apontamento 1 - Chave ctp001 (AAAAMM)", example = "202602", required = true)
    private String numapo1_001;

    @NotNull(message = "numapo2_001 é obrigatório")
    @Size(min = 10, max = 10, message = "numapo2_001 deve ter 10 caracteres")
    @Schema(description = "Número do apontamento 2 - Chave ctp001 (NNNNNNNNNN)", example = "0004567890", required = true)
    private String numapo2_001;

    @NotNull(message = "controle_001 é obrigatório")
    @Size(min = 2, max = 2, message = "controle_001 deve ter 2 caracteres")
    @Schema(description = "Controle - Chave ctp001 (NN)", example = "01", required = true)
    private String controle_001;

    // ========================================
    // Dados da Intimação/Boleto
    // ========================================

    @Size(max = 50, message = "Número do título deve ter no máximo 50 caracteres")
    @Schema(description = "Número do título/protesto", example = "123456")
    private String numeroTitulo;

    @Size(max = 100, message = "Nome do devedor deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do devedor", example = "João da Silva")
    private String nomeDevedor;

    @Size(max = 20, message = "CPF/CNPJ deve ter no máximo 20 caracteres")
    @Schema(description = "CPF/CNPJ do devedor", example = "123.456.789-00")
    private String cpfCnpjDevedor;

    @Schema(description = "Valor do título", example = "1500.00")
    private Double valorTitulo;

    @Schema(description = "Data de vencimento do título", example = "2024-12-31")
    private LocalDate dataVencimento;

    @Size(max = 100, message = "Nome do apresentante deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do apresentante", example = "Banco do Brasil")
    private String nomeApresentante;

    @Schema(description = "Observações sobre a intimação")
    private String observacoes;
}
