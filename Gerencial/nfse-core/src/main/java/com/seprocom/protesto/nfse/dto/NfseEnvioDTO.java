package com.seprocom.protesto.nfse.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para envio de NFSe no Padrão Nacional (ABRASF 2.03).
 * 
 * Este DTO representa o XML de envio conforme especificação do
 * Projeto NFSe Nacional da ABRASF.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NfseEnvioDTO {

    /**
     * Identificação do Lote de RPS
     */
    private String identificador;
    
    /**
     * Código do município sesuai IBGE
     */
    private String codigoMunicipio;
    
    /**
     * Data de geração do lote
     */
    private LocalDateTime dataGeracao;
    
    /**
     * Lista de RPS a serem enviados
     */
    private List<RpsDTO> rpsList;
    
    /**
     * Dados do prestador (empresa que emite)
     */
    private PrestadorDTO prestador;
    
    /**
     * DTO para RPS (Recibo Provisório de Serviços)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RpsDTO {
        
        private String numeroRps;
        private String serieRps;
        private String tipoRps; // 1=RPS, 2=RPS-M, 3=RPS-SW
        private LocalDate dataEmissaoRps;
        private String statusRps; // 1=Normal, 2=Cancelada
        
        // Dados do serviço
        private String codigoServico;
        private String discriminacao;
        private BigDecimal valorServico;
        private BigDecimal valorDeducao;
        private BigDecimal valorPis;
        private BigDecimal valorCofins;
        private BigDecimal valorInss;
        private BigDecimal valorIr;
        private BigDecimal valorCsll;
        private BigDecimal valorIss;
        private BigDecimal aliquotaIss;
        private BigDecimal baseCalculo;
        
        // Dados do tomador
        private TomadorDTO tomador;
        
        // Dados intermediário (se houver)
        private IntermediarioDTO intermediario;
        
        // Dados da obra (se houver)
        private String codigoObra;
        private String art;
        
        // Informações complementares
        private String informacoesComplementares;
    }
    
    /**
     * DTO para Prestador (Empresa que emite a NFSe)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PrestadorDTO {
        private String cnpj;
        private String inscricaoMunicipal;
        private String nomeRazaoSocial;
        private String nomeFantasia;
        private String telefone;
        private String email;
        private EnderecoDTO endereco;
    }
    
    /**
     * DTO para Tomador (Cliente que recebe o serviço)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TomadorDTO {
        private String cpfCnpj; // CPF ou CNPJ
        private String tipoDocumento; // 1=CPF, 2=CNPJ
        private String nomeRazaoSocial;
        private String inscricaoMunicipal;
        private String inscricaoEstadual;
        private String telefone;
        private String email;
        private EnderecoDTO endereco;
    }
    
    /**
     * DTO para Intermediário (se houver)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IntermediarioDTO {
        private String cpfCnpj;
        private String nomeRazaoSocial;
        private String inscricaoMunicipal;
        private String telefone;
        private String email;
    }
    
    /**
     * DTO para Endereço
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnderecoDTO {
        private String endereco;
        private String numero;
        private String complemento;
        private String bairro;
        private String cep;
        private String codigoMunicipio;
        private String uf;
    }
}
