package com.seprocom.gerencial.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para envio de RPS (Recibo Provisorio de Servicos) ao WebService ABRASF.
 * Segue o padrao nacional NFS-e ABRASF versao 2.02.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfseRequestDTO {

    /** Identificacao do RPS */
    private IdentificacaoRps identificacao;
    
    /** Data de emissao do RPS */
    private LocalDate dataEmissao;
    
    /** Status do RPS: N=Normal, C=Cancelado, E=Extorno */
    @Builder.Default
    private String statusRps = "N";
    
    /** Natureza da operacao:
     * 1 - Tributacao no municipio
     * 2 - Tributacao fora do municipio
     * 3 - Isenta
     * 4 - Imune
     * 5 - Exigibilidade suspensa por decisao judicial
     * 6 - Exigibilidade suspensa por procedimento administrativo
     */
    private String naturezaOperacao;
    
    /** Optante pelo Simples Nacional: S=Sim, N=Naoo */
    @Builder.Default
    private String optanteSimples = "N";
    
    /** Incentivador cultural: S=Sim, N=Não */
    @Builder.Default
    private String incentivadorCultural = "N";
    
    /** Numero do documento de origem */
    private String documentoOrigem;
    
    /** Valor total dos servicos */
    private BigDecimal valorServicos;
    
    /** Valor das deducoes */
    @Builder.Default
    private BigDecimal valorDeducoes = BigDecimal.ZERO;
    
    /** Valor do ISS calculado */
    private BigDecimal valorIss;
    
    /** Base de calculo */
    private BigDecimal baseCalculo;
    
    /** Aliquota do ISS (%) */
    private BigDecimal aliquota;
    
    /** Valor do ISS retido (pf PJ) */
    @Builder.Default
    private BigDecimal valorIssRetido = BigDecimal.ZERO;
    
    /** Valor total retido */
    @Builder.Default
    private BigDecimal valorRetencoes = BigDecimal.ZERO;
    
    /** Nome/Razao Social do tomador */
    private String tomadorNome;
    
    /** Tipo de documento do tomador: CPF, CNPJ */
    private String tomadorTipoDoc;
    
    /** Numero do documento do tomador */
    private String tomadorNumeroDoc;
    
    /** Inscricao Municipal do tomador */
    private String tomadorInscricaoMunicipal;
    
    /** Endereco do tomador */
    private Endereco tomadorEndereco;
    
    /** Telefone do tomador */
    private String tomadorTelefone;
    
    /** Email do tomador */
    private String tomadorEmail;
    
    /** Codigo do servico municipal */
    private String codigoServico;
    
    /** Codigo CNAE */
    private String codigoCnae;
    
    /** Discriminacao do servico */
    private String discriminacao;
    
    /** Codigo do municipio IBGE do servico */
    private String codigoMunicipio;
    
    /** Quantidade de items */
    private Integer quantidadeItems;
    
    /** Lista de items do servico */
    private List<ServicoItem> itens;
    
    /** Informacoes do interpolated */
    private String informacoesComplementares;
    
    /** Valores retidos na fonte */
    private List<Retencao> retencoes;
    
    /**
     * Dados de identificacao do RPS
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdentificacaoRps {
        private String numero;
        private String serie;
        private String tipo; // 1=RPS, 2=NFS-e, 3=Recibo
    }
    
    /**
     * Dados de endereco
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Endereco {
        private String endereco;
        private String numero;
        private String complemento;
        private String bairro;
        private String cidade;
        private String uf;
        private String cep;
    }
    
    /**
     * Item de servico
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServicoItem {
        private Integer numeroItem;
        private String codigoServico;
        private String discriminacao;
        private BigDecimal quantidade;
        private BigDecimal valorUnitario;
        private BigDecimal valorTotal;
        private BigDecimal valorDeducao;
        private BigDecimal aliquota;
        private BigDecimal valorIss;
        private String tributavel; // S=Sim, N=Não
    }
    
    /**
     * Retencoes na fonte
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Retencao {
        private String tipo; // IRRF, PIS, COFINS, CSLL, ISS
        private BigDecimal valorRetido;
        private BigDecimal aliquota;
        private String cnpjRetentor;
    }
    
    /**
     * Converte NotasCabDTO para NfseRequestDTO
     */
    public static NfseRequestDTO fromNotasCabDTO(NotasCabDTO nota) {
        IdentificacaoRps ident = IdentificacaoRps.builder()
                .numero(nota.getNumeroRps() != null ? nota.getNumeroRps() : nota.getNumeroNota())
                .serie(nota.getSerie() != null ? nota.getSerie() : "001")
                .tipo("1")
                .build();
        
        Endereco end = null;
        if (nota.getTomadorEndereco() != null) {
            end = Endereco.builder()
                    .endereco(nota.getTomadorEndereco())
                    .bairro(nota.getTomadorBairro())
                    .cidade(nota.getTomadorCidade())
                    .uf(nota.getTomadorUf())
                    .cep(nota.getTomadorCep())
                    .build();
        }
        
        return NfseRequestDTO.builder()
                .identificacao(ident)
                .dataEmissao(nota.getDataEmissao() != null ? nota.getDataEmissao() : LocalDate.now())
                .statusRps("N")
                .naturezaOperacao("1") // Tributação no município
                .optanteSimples("N")
                .documentoOrigem(nota.getNumeroNota())
                .valorServicos(nota.getValorServico())
                .valorDeducoes(nota.getValorDesc() != null ? nota.getValorDesc() : BigDecimal.ZERO)
                .valorIss(nota.getValorIss())
                .baseCalculo(nota.getValorServico())
                .aliquota(nota.getAliquota())
                .tomadorNome(nota.getTomadorNome())
                .tomadorTipoDoc(nota.getTomadorCnpjCpf() != null && nota.getTomadorCnpjCpf().length() == 14 ? "CNPJ" : "CPF")
                .tomadorNumeroDoc(nota.getTomadorCnpjCpf())
                .tomadorEndereco(end)
                .tomadorTelefone(nota.getTomadorTelefone())
                .tomadorEmail(nota.getTomadorEmail())
                .codigoServico(nota.getCodigoServico())
                .discriminacao(nota.getDiscriminacao())
                .codigoMunicipio(nota.getCodigoMunicipio())
                .informacoesComplementares(nota.getObservacoes())
                .build();
    }
}