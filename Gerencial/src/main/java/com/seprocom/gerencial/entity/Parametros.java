package com.seprocom.gerencial.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade JPA para parâmetros do sistema
 * Mapeia a tabela parametros do banco de dados
 * Inclui campos para Reforma Tributária (CBS, IBS, CPP)
 * 
 * @author Seprocom
 */
@Entity
@Table(name = "parametros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parametros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODIGO_PAR")
    private Integer codigoPar;

    @Column(name = "DOC_PAR", length = 50)
    private String docPar;

    @Column(name = "NOME_TABELIAO", length = 100)
    private String nomeTabeliao;

    @Column(name = "CODIGO_TABELIAO", length = 20)
    private String codigoTabeliao;

    @Column(name = "CODIGO_CARTORIO", length = 20)
    private String codigoCartorio;

    @Column(name = "NOME_CARTORIO", length = 200)
    private String nomeCartorio;

    @Column(name = "CNPJ_CARTORIO", length = 20)
    private String cnpjCartorio;

    @Column(name = "INSCRICAO_ESTADUAL", length = 30)
    private String inscricaoEstadual;

    @Column(name = "ENDERECO_CARTORIO", length = 500)
    private String enderecoCartorio;

    @Column(name = "TELEFONE_CARTORIO", length = 20)
    private String telefoneCartorio;

    @Column(name = "EMAIL_CARTORIO", length = 200)
    private String emailCartorio;

    @Column(name = "ATIVA", length = 1)
    private String ativa;

    @Column(name = "DTVERSAO_FUNARPEN")
    private LocalDate dtVersaoFunarpen;

    @Column(name = "OBS_FUNARPEN", length = 1000)
    private String obsFunarpen;

    // =====================================================
    // Novos campos para Reforma Tributária (EC 132/2023)
    // =====================================================
    
    // CBS (Contribuição sobre Bens e Serviços) - Federal -取代PIS/COFINS
    @Column(name = "CBS_PAR", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal cbsPar = new BigDecimal("0.10");
    
    // IBS (Imposto sobre Bens e Serviços) - Estadual/Municipal -取代ISS
    @Column(name = "IBS_PAR", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal ibsPar = new BigDecimal("0.10");
    
    // PIS (Programa de Integração Social) - Federal
    @Column(name = "PIS_PAR", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal pisPar = new BigDecimal("0.0165");
    
    // COFINS (Contribuição para Financiamento da Seguridade Social) - Federal
    @Column(name = "COFINS_PAR", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal cofinsPar = new BigDecimal("0.076");
    
    // CSLL (Contribuição Social sobre Lucro Líquido) - Federal
    @Column(name = "CSLL_PAR", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal csllPar = new BigDecimal("0.09");
    
    // IRPJ (Imposto de Renda de Pessoa Jurídica) - Federal
    @Column(name = "IRPJ_PAR", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal irpjPar = new BigDecimal("0.15");
    
    // IRPJ Adicional (2,5% sobre excedente R$20mil/mês)
    @Column(name = "IRPJ_ADIC_PAR", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal irpjAdicPar = new BigDecimal("0.025");
    
    // CPP (Contribuição Patronal) - Federal - Nova após Reforma
    @Column(name = "CPP_PAR", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal cppPar = new BigDecimal("0.12");
    
    // Fase da Reforma Tributária
    @Column(name = "FASE_REFORMA_PAR", length = 20)
    @Builder.Default
    private String faseReformaPar = "NAO_INICIADA";
    
    // Ativar CBS
    @Column(name = "CBS_ATIVO_PAR")
    @Builder.Default
    private Boolean cbsAtivoPar = false;
    
    // Ativar IBS
    @Column(name = "IBS_ATIVO_PAR")
    @Builder.Default
    private Boolean ibsAtivoPar = false;
    
    // Ativar CPP
    @Column(name = "CPP_ATIVO_PAR")
    @Builder.Default
    private Boolean cppAtivoPar = false;
    
    // Valores para dedução
    @Column(name = "CBS_DEDUZIR_PAR", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal cbsDeduzirPar = BigDecimal.ZERO;
    
    @Column(name = "IBS_DEDUZIR_PAR", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal ibsDeduzirPar = BigDecimal.ZERO;
}