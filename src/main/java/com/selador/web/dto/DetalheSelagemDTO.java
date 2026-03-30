package com.selador.web.dto;

import java.math.BigDecimal;

// DetalheSelagemDTO.java - DTO para detalhe de selagem
public class DetalheSelagemDTO {
    private String numapo1;
    private String numapo2;
    private String devedor;
    private BigDecimal valor;
    private boolean sucesso;
    private String mensagem;
    private String seloUtilizado;
    private String idap;
    
    // Getters e Setters
    public String getNumapo1() { return numapo1; }
    public void setNumapo1(String numapo1) { this.numapo1 = numapo1; }
    
    public String getNumapo2() { return numapo2; }
    public void setNumapo2(String numapo2) { this.numapo2 = numapo2; }
    
    public String getDevedor() { return devedor; }
    public void setDevedor(String devedor) { this.devedor = devedor; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }
    
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    
    public String getSeloUtilizado() { return seloUtilizado; }
    public void setSeloUtilizado(String seloUtilizado) { this.seloUtilizado = seloUtilizado; }
    
    public String getIdap() { return idap; }
    public void setIdap(String idap) { this.idap = idap; }
}