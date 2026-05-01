package com.seprocom.protesto.chat.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferência de dados dos detalhes de NFSe.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotasDetDTO {

    private Long id;
    private Long notaId;
    private Integer item;

    // Dados do Serviço
    private String codigoServico;
    private String descricao;
    private BigDecimal quantidade;
    private String unidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorServico;

    // Cálculo ISS
    private BigDecimal baseCalculo;
    private BigDecimal aliquotaIss;
    private BigDecimal valorIss;
    private BigDecimal valorDeducao;
    private BigDecimal valorDescontoIncond;
    private BigDecimal valorOutrasRetencoes;

    // Campos complementares
    private String codigoMunicipio;
    private String codigoIncidencia;
    private String codigoPais;
    private String exigibilidadeIss;
    private String descricaoExigibilidade;
    private String documentoOrigem;
    private String tipoDocumentoOrigem;

    // Controle
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String usuarioCriacao;
    private String usuarioAtualizacao;

    /**
     * Indica se é um novo item
     */
    private boolean novo;

    /**
     * Indica se o item foi removido
     */
    private boolean removido;
}

