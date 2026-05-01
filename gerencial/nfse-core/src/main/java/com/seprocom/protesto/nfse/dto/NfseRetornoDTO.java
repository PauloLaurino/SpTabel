package com.seprocom.protesto.nfse.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para retorno de envio de NFSe no Padrão Nacional (ABRASF 2.03).
 * 
 * Representa a resposta do WebService após envio do lote de RPS.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NfseRetornoDTO {

    /**
     * Código do status de retorno
     * 0 = Sucesso
     * 1 = Erro
     * 2 = Alerta
     */
    private Integer codigoStatus;
    
    /**
     * Descrição do status
     */
    private String descricaoStatus;
    
    /**
     * Número do protocolo de retorno
     */
    private String numeroProtocolo;
    
    /**
     * Data e hora de processamento
     */
    private LocalDateTime dataProcessamento;
    
    /**
     * Lista de NFSe processadas
     */
    private List<NfseProcessadaDTO> nfseProcessadas;
    
    /**
     * Lista de erros/alertas retornados
     */
    private List<MensagemRetornoDTO> mensagens;
    
    /**
     * DTO para NFSe processada com sucesso
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NfseProcessadaDTO {
        
        private String numeroNfse;
        private String numeroRps;
        private String serieRps;
        private String chaveNfse;
        private String codigoVerificacao;
        private LocalDateTime dataEmissao;
        private BigDecimal valorServico;
        private BigDecimal valorIss;
        private String situacao; // E=Emitida, C=Cancelada
        private String linkConsulta;
        private String xmlNfse;
    }
    
    /**
     * DTO para mensagens de erro/alerta
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MensagemRetornoDTO {
        
        private String codigo;
        private String descricao;
        private String correcao;
        private String tipo; // E=Erro, A=Alerta
        private String campo; // Campo que originou o erro
    }
}
