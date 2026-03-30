package com.selador.web.dto;

// FiltroBuscaDTO.java - DTO para filtros de busca
public class FiltroBuscaDTO {
    private String tipoOperacao;
    private String dataInicial;
    private String dataFinal;
    private String status;
    private String devedor;
    private String cpfCnpj;
    private Integer pagina;
    private Integer tamanhoPagina;
    
    // Getters e Setters
    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String tipoOperacao) { this.tipoOperacao = tipoOperacao; }
    
    public String getDataInicial() { return dataInicial; }
    public void setDataInicial(String dataInicial) { this.dataInicial = dataInicial; }
    
    public String getDataFinal() { return dataFinal; }
    public void setDataFinal(String dataFinal) { this.dataFinal = dataFinal; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDevedor() { return devedor; }
    public void setDevedor(String devedor) { this.devedor = devedor; }
    
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    
    public Integer getPagina() { return pagina; }
    public void setPagina(Integer pagina) { this.pagina = pagina; }
    
    public Integer getTamanhoPagina() { return tamanhoPagina; }
    public void setTamanhoPagina(Integer tamanhoPagina) { this.tamanhoPagina = tamanhoPagina; }
}