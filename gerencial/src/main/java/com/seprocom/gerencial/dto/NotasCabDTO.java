package com.seprocom.gerencial.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para Cabeçalho de Nota Fiscal.
 * 
 * @author Seprocom
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotasCabDTO {

    private Long id;
    private String numeroNota;
    private String serie;
    private LocalDate dataEmissao;
    private LocalDate dataCompetencia;
    private LocalDate dataVencimento;
    private String situacao;
    
    // Tomador
    private String tomadorCnpjCpf;
    private String tomadorNome;
    private String tomadorEndereco;
    private String tomadorBairro;
    private String tomadorCidade;
    private String tomadorUf;
    private String tomadorCep;
    private String tomadorEmail;
    private String tomadorTelefone;
    
    // Valores
    private BigDecimal valorServico;
    private BigDecimal valorIss;
    private BigDecimal valorDesc;
    private BigDecimal valorTotal;
    
    // Serviço
    private String discriminacao;
    private String codigoMunicipio;
    private String codigoServico;
    private BigDecimal aliquota;
    
    // Emitente
    private String emitenteCnpj;
    private String emitenteInscricaoMunicipal;
    private String emitenteRazaoSocial;
    
    // NFS-e
    private String chaveNfse;
    private String numeroRps;
    private String linkConsulta;
    private String xmlNfse;
    private String protocolo;
    private String mensagemErro;
    
    // Controle
    private String observacoes;
    private Integer codigoUsu;
    private LocalDateTime dtcadastro;
    private LocalDateTime dtalteracao;
    
    // Itens
    private List<NotasDetDTO> itens;
}