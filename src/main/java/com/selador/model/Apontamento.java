package com.selador.model;

import com.selador.enums.TipoOperacao;
import com.selador.enums.CategoriaOperacao;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.math.BigDecimal;

/**
 * Model que representa um apontamento da tabela ctp001.
 * Mantém apenas os campos essenciais para o sistema de selagem.
 */
public class Apontamento implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== CHAVE PRIMÁRIA ==========
    private String numApo1;          // numapo1_001 - CHAR(6) - NOT NULL
    private String numApo2;          // numapo2_001 - CHAR(10) - NOT NULL
    private String controle;         // controle_001 - CHAR(2) - NOT NULL
    
    // ========== INFORMAÇÕES DO DEVEDOR ==========
    private String devedor;          // devedor_001 - VARCHAR(100)
    private String cpfCnpj;          // cgccpfsac_001 - CHAR(12)
    
    // ========== INFORMAÇÕES DO TÍTULO ==========
    private BigDecimal valor;        // valor_001 - DECIMAL(16,2)
    private BigDecimal juros;        // juros_001 - DECIMAL(12,2)
    
    // ========== INFORMAÇÕES DE PROTOCOLO ==========
    private String protocolo;        // protocolo_001 - DECIMAL(10,0) → String
    
    // ========== INFORMAÇÕES DE SELAGEM ==========
    private String selo;             // seloaponta_001 - CHAR(30)
    
    // ========== DATAS PRINCIPAIS ==========
    private Date dataApontamento;    // dataapo_001 - DECIMAL(8,0) → Date
    private Date dataIntimacao;      // dtintimacao_001 - DECIMAL(8,0) → Date
    private Date dataRetirada;       // dataret_001 - DECIMAL(8,0) → Date
    private Date dataPagamento;      // datapag_001 - DECIMAL(8,0) → Date
    private Date dataBaixa;          // databai_001 - DECIMAL(8,0) → Date
    
    // ========== CAMPOS DE CONTROLE DO SISTEMA ==========
    private TipoOperacao tipoOperacao;   // Não é da tabela, usado no sistema
    private boolean selecionado;         // Para UI - seleção na grade
    private Date dataCriacao;            // Para auditoria
    private Date dataAtualizacao;        // Para auditoria
    private String usuarioCriacao;       // Para auditoria
    private String usuarioAtualizacao;   // Para auditoria

    // ========== MÉTODOS DE IDENTIFICAÇÃO ==========
    
    /**
     * Retorna a chave única do apontamento (numApo1/numApo2)
     */
    public String getChave() {
        return numApo1 + "/" + numApo2;
    }
    
    /**
     * Retorna a chave completa (numApo1/numApo2/controle)
     */
    public String getChaveCompleta() {
        return numApo1 + "/" + numApo2 + "/" + controle;
    }
    
    /**
     * Verifica se já foi selado
     */
    public boolean isSelado() {
        return selo != null && !selo.trim().isEmpty();
    }
    
    /**
     * Retorna a categoria da operação baseada no tipo
     */
    public CategoriaOperacao getCategoriaOperacao() {
        return tipoOperacao != null ? tipoOperacao.getCategoria() : CategoriaOperacao.OUTROS;
    }
    
    /**
     * Retorna o valor total (valor + juros)
     */
    public BigDecimal getValorTotal() {
        BigDecimal total = valor != null ? valor : BigDecimal.ZERO;
        if (juros != null) {
            total = total.add(juros);
        }
        return total;
    }
    
    /**
     * Formata o CPF/CNPJ do devedor
     */
    public String getCpfCnpjFormatado() {
        if (cpfCnpj == null || cpfCnpj.trim().isEmpty()) {
            return "";
        }
        
        String limpo = cpfCnpj.replaceAll("[^0-9]", "");
        
        if (limpo.length() == 11) {
            return limpo.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (limpo.length() == 14) {
            return limpo.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        
        return cpfCnpj;
    }
    
    /**
     * Retorna o nome formatado (apenas primeiras palavras se muito longo)
     */
    public String getDevedorResumido(int maxPalavras) {
        if (devedor == null || devedor.trim().isEmpty()) {
            return "";
        }
        
        String[] palavras = devedor.split("\\s+");
        if (palavras.length <= maxPalavras) {
            return devedor;
        }
        
        StringBuilder resumido = new StringBuilder();
        for (int i = 0; i < maxPalavras; i++) {
            if (i > 0) resumido.append(" ");
            resumido.append(palavras[i]);
        }
        resumido.append("...");
        
        return resumido.toString();
    }
    
    // ========== GETTERS E SETTERS ==========
    
    // Chave primária
    public String getNumApo1() { return numApo1; }
    public void setNumApo1(String numApo1) { this.numApo1 = numApo1; }
    
    public String getNumApo2() { return numApo2; }
    public void setNumApo2(String numApo2) { this.numApo2 = numApo2; }
    
    public String getControle() { return controle; }
    public void setControle(String controle) { this.controle = controle; }
    
    // Informações do devedor
    public String getDevedor() { return devedor; }
    public void setDevedor(String devedor) { this.devedor = devedor; }
    
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    
    // Valores
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public void setValor(Double valor) { 
        this.valor = valor != null ? BigDecimal.valueOf(valor) : null; 
    }
    
    public BigDecimal getJuros() { return juros; }
    public void setJuros(BigDecimal juros) { this.juros = juros; }
    
    public void setJuros(Double juros) {
        this.juros = juros != null ? BigDecimal.valueOf(juros) : null;
    }
    
    // Protocolo
    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }
    
    // Selo
    public String getSelo() { return selo; }
    public void setSelo(String selo) { this.selo = selo; }
    
    // Datas
    public Date getDataApontamento() { return dataApontamento; }
    public void setDataApontamento(Date dataApontamento) { this.dataApontamento = dataApontamento; }
    
    public void setDataApontamento(Double dataApontamento) {
        this.dataApontamento = convertDoubleToDate(dataApontamento);
    }
    
    public Date getDataIntimacao() { return dataIntimacao; }
    public void setDataIntimacao(Date dataIntimacao) { this.dataIntimacao = dataIntimacao; }
    
    public Date getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(Date dataRetirada) { this.dataRetirada = dataRetirada; }
    
    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }
    
    public Date getDataBaixa() { return dataBaixa; }
    public void setDataBaixa(Date dataBaixa) { this.dataBaixa = dataBaixa; }
    
    // Tipo de operação (não é da tabela)
    public TipoOperacao getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(TipoOperacao tipoOperacao) { this.tipoOperacao = tipoOperacao; }
    
    // Seleção (para UI)
    public boolean isSelecionado() { return selecionado; }
    public void setSelecionado(boolean selecionado) { this.selecionado = selecionado; }
    
    // Auditoria
    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public Date getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(Date dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    
    public String getUsuarioCriacao() { return usuarioCriacao; }
    public void setUsuarioCriacao(String usuarioCriacao) { this.usuarioCriacao = usuarioCriacao; }
    
    public String getUsuarioAtualizacao() { return usuarioAtualizacao; }
    public void setUsuarioAtualizacao(String usuarioAtualizacao) { this.usuarioAtualizacao = usuarioAtualizacao; }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Converte Double (formato YYYYMMDD) para Date
     */
    private Date convertDoubleToDate(Double doubleValue) {
        if (doubleValue == null || doubleValue == 0.0) {
            return null;
        }
        
        try {
            int intValue = doubleValue.intValue();
            String strValue = String.valueOf(intValue);
            
            if (strValue.length() == 8) {
                int year = Integer.parseInt(strValue.substring(0, 4));
                int month = Integer.parseInt(strValue.substring(4, 6));
                int day = Integer.parseInt(strValue.substring(6, 8));
                
                // Using Calendar instead of deprecated Date constructor
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar.getTime();
            }
        } catch (Exception e) {
            // Ignora erro de conversão
        }
        
        return null;
    }
    
    // ========== EQUALS E HASHCODE ==========
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Apontamento that = (Apontamento) o;
        
        if (numApo1 != null ? !numApo1.equals(that.numApo1) : that.numApo1 != null) return false;
        if (numApo2 != null ? !numApo2.equals(that.numApo2) : that.numApo2 != null) return false;
        return controle != null ? controle.equals(that.controle) : that.controle == null;
    }
    
    @Override
    public int hashCode() {
        int result = numApo1 != null ? numApo1.hashCode() : 0;
        result = 31 * result + (numApo2 != null ? numApo2.hashCode() : 0);
        result = 31 * result + (controle != null ? controle.hashCode() : 0);
        return result;
    }
    
    // ========== TO STRING ==========
    @Override
    public String toString() {
        return "Apontamento{" +
                "numApo1='" + numApo1 + '\'' +
                ", numApo2='" + numApo2 + '\'' +
                ", controle='" + controle + '\'' +
                ", devedor='" + getDevedorResumido(3) + '\'' +
                ", valor=" + valor +
                ", selo='" + selo + '\'' +
                '}';
    }
}