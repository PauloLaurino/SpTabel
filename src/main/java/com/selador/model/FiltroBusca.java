package com.selador.model;

import com.selador.enums.TipoOperacao;
import java.io.Serializable;
import java.util.Date;

/**
 * Model que representa os filtros para busca de apontamentos.
 */
public class FiltroBusca implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== FILTROS PRINCIPAIS ==========
    private TipoOperacao tipoOperacao;
    private Date dataInicial;
    private Date dataFinal;
    private String dataInicialStr;  // Formato DD/MM/YYYY
    private String dataFinalStr;    // Formato DD/MM/YYYY
    
    // ========== FILTROS DE STATUS ==========
    private boolean apenasAptosSelagem;
    private boolean apenasNaoSelados;
    
    // ========== FILTROS DE DEVEDOR ==========
    private String nomeDevedor;
    private String cpfCnpj;
    private String numeroDocumento;
    
    // ========== FILTROS DE VALOR ==========
    private Double valorMinimo;
    private Double valorMaximo;
    
    // ========== FILTROS DE PROTOCOLO ==========
    private String protocolo;
    private String numeroApo1;
    private String numeroApo2;
    
    // ========== FILTROS DE PAGINAÇÃO ==========
    private int pagina;
    private int itensPorPagina;
    private String ordenarPor;
    private boolean ordemAscendente;
    
    // ========== FILTROS ADICIONAIS ==========
    private String codigoPortador;
    private String nomeCedente;
    private String cidade;
    private String uf;
    
    // ========== MÉTODOS DE NEGÓCIO ==========
    
    /**
     * Valida os filtros básicos
     */
    public boolean isValid() {
        if (tipoOperacao == null) {
            return false;
        }
        
        if (dataInicial == null && dataFinal == null && 
            (dataInicialStr == null || dataFinalStr == null)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Retorna a data inicial como string no formato do banco
     */
    public String getDataInicialFormatada() {
        if (tipoOperacao == null) return "";
        
        if (dataInicialStr != null && !dataInicialStr.trim().isEmpty()) {
            return tipoOperacao.formatarDataParaBanco(dataInicialStr);
        }
        
        if (dataInicial != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String dataStr = sdf.format(dataInicial);
            return tipoOperacao.formatarDataParaBanco(dataStr);
        }
        
        return "";
    }
    
    /**
     * Retorna a data final como string no formato do banco
     */
    public String getDataFinalFormatada() {
        if (tipoOperacao == null) return "";
        
        if (dataFinalStr != null && !dataFinalStr.trim().isEmpty()) {
            return tipoOperacao.formatarDataParaBanco(dataFinalStr);
        }
        
        if (dataFinal != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String dataStr = sdf.format(dataFinal);
            return tipoOperacao.formatarDataParaBanco(dataStr);
        }
        
        return "";
    }
    
    /**
     * Retorna se há filtro por nome do devedor
     */
    public boolean hasNomeDevedorFilter() {
        return nomeDevedor != null && !nomeDevedor.trim().isEmpty();
    }
    
    /**
     * Retorna se há filtro por CPF/CNPJ
     */
    public boolean hasCpfCnpjFilter() {
        return cpfCnpj != null && !cpfCnpj.trim().isEmpty();
    }
    
    /**
     * Retorna se há filtro por valor
     */
    public boolean hasValorFilter() {
        return valorMinimo != null || valorMaximo != null;
    }
    
    /**
     * Retorna o offset para paginação
     */
    public int getOffset() {
        return (pagina - 1) * itensPorPagina;
    }
    
    /**
     * Retorna o limite para paginação
     */
    public int getLimit() {
        return itensPorPagina;
    }
    
    /**
     * Retorna a cláusula ORDER BY para SQL
     */
    public String getOrderByClause() {
        if (ordenarPor == null || ordenarPor.trim().isEmpty()) {
            return "ORDER BY dataApontamento DESC";
        }
        
        String direcao = ordemAscendente ? "ASC" : "DESC";
        return "ORDER BY " + ordenarPor + " " + direcao;
    }
    
    // ========== GETTERS E SETTERS ==========
    
    public TipoOperacao getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(TipoOperacao tipoOperacao) { this.tipoOperacao = tipoOperacao; }
    
    public Date getDataInicial() { return dataInicial; }
    public void setDataInicial(Date dataInicial) { this.dataInicial = dataInicial; }
    
    public Date getDataFinal() { return dataFinal; }
    public void setDataFinal(Date dataFinal) { this.dataFinal = dataFinal; }
    
    public String getDataInicialStr() { return dataInicialStr; }
    public void setDataInicialStr(String dataInicialStr) { this.dataInicialStr = dataInicialStr; }
    
    public String getDataFinalStr() { return dataFinalStr; }
    public void setDataFinalStr(String dataFinalStr) { this.dataFinalStr = dataFinalStr; }
    
    public boolean isApenasAptosSelagem() { return apenasAptosSelagem; }
    public void setApenasAptosSelagem(boolean apenasAptosSelagem) { this.apenasAptosSelagem = apenasAptosSelagem; }
    
    public boolean isApenasNaoSelados() { return apenasNaoSelados; }
    public void setApenasNaoSelados(boolean apenasNaoSelados) { this.apenasNaoSelados = apenasNaoSelados; }
    
    public String getNomeDevedor() { return nomeDevedor; }
    public void setNomeDevedor(String nomeDevedor) { this.nomeDevedor = nomeDevedor; }
    
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    
    public Double getValorMinimo() { return valorMinimo; }
    public void setValorMinimo(Double valorMinimo) { this.valorMinimo = valorMinimo; }
    
    public Double getValorMaximo() { return valorMaximo; }
    public void setValorMaximo(Double valorMaximo) { this.valorMaximo = valorMaximo; }
    
    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }
    
    public String getNumeroApo1() { return numeroApo1; }
    public void setNumeroApo1(String numeroApo1) { this.numeroApo1 = numeroApo1; }
    
    public String getNumeroApo2() { return numeroApo2; }
    public void setNumeroApo2(String numeroApo2) { this.numeroApo2 = numeroApo2; }
    
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina; }
    
    public int getItensPorPagina() { return itensPorPagina; }
    public void setItensPorPagina(int itensPorPagina) { this.itensPorPagina = itensPorPagina; }
    
    public String getOrdenarPor() { return ordenarPor; }
    public void setOrdenarPor(String ordenarPor) { this.ordenarPor = ordenarPor; }
    
    public boolean isOrdemAscendente() { return ordemAscendente; }
    public void setOrdemAscendente(boolean ordemAscendente) { this.ordemAscendente = ordemAscendente; }
    
    public String getCodigoPortador() { return codigoPortador; }
    public void setCodigoPortador(String codigoPortador) { this.codigoPortador = codigoPortador; }
    
    public String getNomeCedente() { return nomeCedente; }
    public void setNomeCedente(String nomeCedente) { this.nomeCedente = nomeCedente; }
    
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    
    // ========== TO STRING ==========
    @Override
    public String toString() {
        return "FiltroBusca{" +
                "tipoOperacao=" + tipoOperacao +
                ", dataInicialStr='" + dataInicialStr + '\'' +
                ", dataFinalStr='" + dataFinalStr + '\'' +
                ", pagina=" + pagina +
                '}';
    }
}