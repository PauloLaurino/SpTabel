package com.seprocom.protesto.chat.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para transferência de dados do cabeçalho de NFSe.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotasCabDTO {

    private Long id;
    private String numeroNota;
    private String serie;
    private String codigoVerificacao;
    private LocalDate dataEmissao;
    private LocalDate dataCompetencia;
    private String situacao;
    private String descricaoSituacao;
    private String tipo;

    // Valores
    private BigDecimal valorServico;
    private BigDecimal valorIss;
    private BigDecimal valorTotal;
    private BigDecimal aliquotaIss;
    private BigDecimal baseCalculo;
    private BigDecimal valorDeducao;
    private BigDecimal valorOutrasRetencoes;
    private BigDecimal valorDescontoIncond;
    private BigDecimal valorDescontoCond;

    // Dados do Tomador
    private String tomadorCnpjCpf;
    private String tomadorNome;
    private String tomadorInscricaoMunicipal;
    private String tomadorInscricaoEstadual;
    private String tomadorEndereco;
    private String tomadorNumero;
    private String tomadorComplemento;
    private String tomadorBairro;
    private String tomadorCep;
    private String tomadorCidade;
    private String tomadorUf;
    private String tomadorEmail;
    private String tomadorTelefone;

    // Dados do Intermediário
    private String intermediarioCnpjCpf;
    private String intermediarioNome;

    // Discriminação
    private String discriminacaoServicos;
    private String codigoServico;
    private String descricaoServico;
    private String codigoMunicipio;
    private String codigoObra;
    private String art;

    // Dados RPS
    private String numeroRps;
    private String serieRps;
    private String tipoRps;
    private LocalDate dataEmissaoRps;

    // Retorno prefeitura
    private String statusTransmissao;
    private LocalDateTime dataTransmissao;
    private String linkConsulta;
    private String xmlNfse;
    private String chaveNfse;

    // Campos de controle
    private String observacoes;
    private String codigoOperacao;
    private String idExterno;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String usuarioCriacao;
    private String usuarioAtualizacao;

    // Itens da nota
    private List<NotasDetDTO> itens;

    /**
     * Contagem de itens
     */
    private Integer quantidadeItens;

    /**
     * Indica se é uma nova nota (ainda não persistida)
     */
    private boolean nova;

    /**
     * Indica se a nota foi modificada
     */
    private boolean modificado;
}

