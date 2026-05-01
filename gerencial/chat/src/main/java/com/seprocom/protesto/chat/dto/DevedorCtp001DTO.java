package com.seprocom.protesto.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para transferência de dados do devedor da tabela CTP001.
 * Usado para integração entre o sistema de chat e o sistema de protesto.
 */
@Data
@Schema(description = "Dados do devedor do protesto")
public class DevedorCtp001DTO {

    @Schema(description = "Número do apontamento 1", example = "0001")
    private String numapo1_001;

    @Schema(description = "Número do apontamento 2", example = "0001")
    private String numapo2_001;

    @Schema(description = "Número de controle", example = "000001")
    private String controle_001;

    @Schema(description = "Nome do devedor", example = "JOÃO DA SILVA")
    private String nome_001;

    @Schema(description = "CPF ou CNPJ do devedor", example = "12345678901")
    private String cpfCnpj_001;

    @Schema(description = "Endereço do devedor", example = "RUA DAS FLORES, 123")
    private String endereco_001;

    @Schema(description = "Cidade do devedor", example = "SÃO PAULO")
    private String cidade_001;

    @Schema(description = "UF do devedor", example = "SP")
    private String uf_001;

    @Schema(description = "CEP do devedor", example = "01234567")
    private String cep_001;

    @Schema(description = "Valor do protesto", example = "1500.00")
    private BigDecimal valor_001;

    @Schema(description = "Data do protesto")
    private LocalDate dataProtesto_001;

    @Schema(description = "Data da intimação")
    private LocalDate dtintimacao_001;

    @Schema(description = "Data da ocorrência/cancelamento")
    private LocalDate dataocr_001;

    @Schema(description = "Data do pagamento")
    private LocalDate datapag_001;

    @Schema(description = "Status do protesto", example = "PENDENTE", 
            allowableValues = {"PENDENTE", "INTIMADO", "PAGO", "CANCELADO"})
    private String statusProtesto;
}
