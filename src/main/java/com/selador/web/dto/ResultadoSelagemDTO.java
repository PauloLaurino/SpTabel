package com.selador.web.dto;

import java.util.Date;
import java.util.List;

// ResultadoSelagemDTO.java - DTO para resultado de selagem
public class ResultadoSelagemDTO {
    private String status;
    private String mensagem;
    private Integer totalProcessados;
    private Integer sucessos;
    private Integer falhas;
    private String dataProcessamento;
    private String usuario;
    private List<DetalheSelagemDTO> detalhes;
    
    public ResultadoSelagemDTO() {
        this.dataProcessamento = new Date().toString();
    }
    
    // Getters e Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    
    public Integer getTotalProcessados() { return totalProcessados; }
    public void setTotalProcessados(Integer totalProcessados) { this.totalProcessados = totalProcessados; }
    
    public Integer getSucessos() { return sucessos; }
    public void setSucessos(Integer sucessos) { this.sucessos = sucessos; }
    
    public Integer getFalhas() { return falhas; }
    public void setFalhas(Integer falhas) { this.falhas = falhas; }
    
    public String getDataProcessamento() { return dataProcessamento; }
    public void setDataProcessamento(String dataProcessamento) { this.dataProcessamento = dataProcessamento; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public List<DetalheSelagemDTO> getDetalhes() { return detalhes; }
    public void setDetalhes(List<DetalheSelagemDTO> detalhes) { this.detalhes = detalhes; }
}