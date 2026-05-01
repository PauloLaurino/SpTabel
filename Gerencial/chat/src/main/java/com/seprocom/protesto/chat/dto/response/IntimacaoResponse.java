package com.seprocom.protesto.chat.dto.response;

import com.seprocom.protesto.chat.entity.AtendimentoIntimacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para intimação/boleto vinculado ao atendimento.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de intimação/boleto")
public class IntimacaoResponse {

    @Schema(description = "ID da intimação", example = "1")
    private Long id;

    @Schema(description = "ID do atendimento", example = "1")
    private Long atendimentoId;

    // Chaves da tabela ctp001
    @Schema(description = "Número do apontamento 1 - Chave ctp001 (AAAAMM)", example = "202602")
    private String numapo1_001;

    @Schema(description = "Número do apontamento 2 - Chave ctp001 (NNNNNNNNNN)", example = "0004567890")
    private String numapo2_001;

    @Schema(description = "Controle - Chave ctp001 (NN)", example = "01")
    private String controle_001;

    // Dados da Intimação/Boleto
    @Schema(description = "Número do título", example = "123456")
    private String numeroTitulo;

    @Schema(description = "Nome do devedor", example = "João da Silva")
    private String nomeDevedor;

    @Schema(description = "CPF/CNPJ do devedor", example = "123.456.789-00")
    private String cpfCnpjDevedor;

    @Schema(description = "Valor do título", example = "1500.00")
    private Double valorTitulo;

    @Schema(description = "Data de vencimento", example = "2024-12-31")
    private LocalDate dataVencimento;

    @Schema(description = "Nome do apresentante", example = "Banco do Brasil")
    private String nomeApresentante;

    // Dados de Intimação
    @Schema(description = "Data de intimação (DDMMAAAA)", example = "15012024")
    private String dtintimacao_001;

    @Schema(description = "Data de ocorrência (AAAAMMDD)", example = "20240115")
    private String dataocr_001;

    @Schema(description = "Data do pagamento", example = "2024-01-15")
    private LocalDate datapag_001;

    @Schema(description = "Hora do pagamento", example = "14:30")
    private String horaPagamento;

    @Schema(description = "Valor pago", example = "1500.00")
    private Double valorPago;

    // Controle de Envio
    @Schema(description = "PDF enviado", example = "true")
    private Boolean pdfEnviado;

    @Schema(description = "Data de envio do PDF", example = "2024-01-15T10:00:00")
    private LocalDateTime dataEnvioPdf;

    @Schema(description = "Nome do arquivo PDF", example = "intimacao_12345.pdf")
    private String arquivoPdf;

    @Schema(description = "Comprovante recebido", example = "false")
    private Boolean comprovanteRecebido;

    @Schema(description = "Data de recebimento do comprovante")
    private LocalDateTime dataRecebimentoComprovante;

    @Schema(description = "Nome do arquivo do comprovante")
    private String arquivoComprovante;

    @Schema(description = "Sincronizado com ctp001", example = "false")
    private Boolean sincronizadoCtp001;

    @Schema(description = "Data da última sincronização")
    private LocalDateTime dataSincronizacao;

    @Schema(description = "Status: P=Pendente, E=Enviada, C=Confirmada, X=Cancelada", example = "P")
    private String statusIntimacao;

    @Schema(description = "Descrição do status", example = "Pendente")
    private String descricaoStatus;

    @Schema(description = "Observações")
    private String observacoes;

    @Schema(description = "Data de criação", example = "2024-01-15T10:00:00")
    private LocalDateTime dataCriacao;

    /**
     * Converte entidade para DTO
     */
    public static IntimacaoResponse fromEntity(AtendimentoIntimacao intimacao) {
        return IntimacaoResponse.builder()
                .id(intimacao.getId())
                .atendimentoId(intimacao.getAtendimento() != null ? intimacao.getAtendimento().getId() : null)
                .numapo1_001(intimacao.getNumapo1_001())
                .numapo2_001(intimacao.getNumapo2_001())
                .controle_001(intimacao.getControle_001())
                .numeroTitulo(intimacao.getNumeroTitulo())
                .nomeDevedor(intimacao.getNomeDevedor())
                .cpfCnpjDevedor(intimacao.getCpfCnpjDevedor())
                .valorTitulo(intimacao.getValorTitulo())
                .dataVencimento(intimacao.getDataVencimento())
                .nomeApresentante(intimacao.getNomeApresentante())
                .dtintimacao_001(intimacao.getDtintimacao_001())
                .dataocr_001(intimacao.getDataocr_001())
                .datapag_001(intimacao.getDatapag_001())
                .horaPagamento(intimacao.getHoraPagamento())
                .valorPago(intimacao.getValorPago())
                .pdfEnviado(intimacao.getPdfEnviado())
                .dataEnvioPdf(intimacao.getDataEnvioPdf())
                .arquivoPdf(intimacao.getArquivoPdf())
                .comprovanteRecebido(intimacao.getComprovanteRecebido())
                .dataRecebimentoComprovante(intimacao.getDataRecebimentoComprovante())
                .arquivoComprovante(intimacao.getArquivoComprovante())
                .sincronizadoCtp001(intimacao.getSincronizadoCtp001())
                .dataSincronizacao(intimacao.getDataSincronizacao())
                .statusIntimacao(intimacao.getStatusIntimacao())
                .descricaoStatus(intimacao.getDescricaoStatus())
                .observacoes(intimacao.getObservacoes())
                .dataCriacao(intimacao.getDataCriacao())
                .build();
    }
}
