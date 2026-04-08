package com.seprocom.gerencial.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta da emissao de NFSe via WebService ABRASF.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfseResponseDTO {

    /** Status do processamento */
    private String status;
    
    /** Codigo da resposta */
    private String codigo;
    
    /** Mensagem de retorno */
    private String mensagem;
    
    /** Numero da NFS-e gerada */
    private String numeroNfse;
    
    /** Chave de acesso da NFS-e */
    private String chaveNfse;
    
    /** Data de emissao */
    private LocalDate dataEmissao;
    
    /** Link de consulta da NFS-e */
    private String linkConsulta;
    
    /** XML da NFS-e gerada */
    private String xmlNfse;
    
    /** Numero do RPS convertido */
    private String numeroRps;
    
    /** Protocolo de processamento */
    private String protocolo;
    
    /** Codigo de verificacao */
    private String codigoVerificacao;
    
    /** Numero do lote processado */
    private String numeroLote;
    
    /** Data de processamento */
    private LocalDateTime dataProcessamento;
    
    /** Codigo do municipio IBGE */
    private String codigoMunicipio;
    
    /** Sucesso na operacao */
    private boolean sucesso;
    
    /** Erros retornados pelo servico */
    private List<String> erros;
    
    /** Sucesso na operacao */
    public boolean isSucesso() {
        return sucesso || "S".equalsIgnoreCase(status) || "100".equals(codigo);
    }
    
    /**
     * Cria resposta de sucesso
     */
    public static NfseResponseDTO sucesso(String numeroNfse, String chaveNfse, String mensagem) {
        return NfseResponseDTO.builder()
                .status("S")
                .codigo("100")
                .mensagem(mensagem)
                .numeroNfse(numeroNfse)
                .chaveNfse(chaveNfse)
                .dataEmissao(LocalDate.now())
                .build();
    }
    
    /**
     * Cria resposta de erro
     */
    public static NfseResponseDTO erro(String codigo, String mensagem) {
        return NfseResponseDTO.builder()
                .status("N")
                .codigo(codigo)
                .mensagem(mensagem)
                .build();
    }
    
    /**
     * Cria resposta de erro com lista de erros
     */
    public static NfseResponseDTO erro(String codigo, String mensagem, List<String> erros) {
        return NfseResponseDTO.builder()
                .status("N")
                .codigo(codigo)
                .mensagem(mensagem)
                .erros(erros)
                .build();
    }
}