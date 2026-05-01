package com.seprocom.protesto.nfse.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para cancelamento de NFSe no Padrão Nacional (ABRASF 2.03).
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NfseCancelamentoDTO {

    /**
     * Identificação do pedido de cancelamento
     */
    private String identificador;
    
    /**
     * Código do município (IBGE)
     */
    private String codigoMunicipio;
    
    /**
     * NFSe a ser cancelada
     */
    private InfNfseCancelamentoDTO nfse;
    
    /**
     * DTO com dados da NFSe para cancelamento
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InfNfseCancelamentoDTO {
        
        /**
         * Número da NFSe
         */
        private String numeroNfse;
        
        /**
         * Código de verificação da NFSe
         */
        private String codigoVerificacao;
        
        /**
         * Chave da NFSe
         */
        private String chaveNfse;
        
        /**
         * Data de cancelamento
         */
        @Builder.Default
        private LocalDateTime dataCancelamento = LocalDateTime.now();
        
        /**
         * Motivo do cancelamento
         */
        private String motivo;
    }
}
