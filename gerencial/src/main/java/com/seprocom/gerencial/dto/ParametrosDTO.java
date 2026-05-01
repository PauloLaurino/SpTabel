package com.seprocom.gerencial.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para parâmetros do sistema (tabela parametros)
 * Inclui campos para Reforma Tributária
 * 
 * @author Seprocom
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParametrosDTO {

    private Integer codigoPar;
    private String docPar;
    private String nomeTabeliao;
    private String codigoTabeliao;
    private String codigoCartorio;
    private String nomeCartorio;
    private String cnpjCartorio;
    private String inscricaoEstadual;
    private String enderecoCartorio;
    private String telefoneCartorio;
    private String emailCartorio;
    private String ativa;
    private LocalDate dtVersaoFunarpen;
    private String obsFunarpen;
    
    // Impostos - Reforma Tributária
    private BigDecimal cbsPar;        // CBS (取代PIS/COFINS)
    private BigDecimal ibsPar;        // IBS (取代ISS)
    private BigDecimal pisPar;        // PIS
    private BigDecimal cofinsPar;     // COFINS
    private BigDecimal csllPar;       // CSLL
    private BigDecimal irpjPar;       // IRPJ
    private BigDecimal irpjAdicPar;  // IRPJ Adicional
    private BigDecimal cppPar;        // CPP (nova)
    
    // Fase da Reforma Tributária
    private String faseReformaPar;    // NAO_INICIADA, TRANSICAO, CONVERGENCIA, PLENA
    private Boolean cbsAtivoPar;      // Se CBS está ativa
    private Boolean ibsAtivoPar;      // Se IBS está ativa
    private Boolean cppAtivoPar;      // Se CPP está ativa
    
    // Valores para dedução
    private BigDecimal cbsDeduzirPar;
    private BigDecimal ibsDeduzirPar;
}