package com.selador.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Model que representa um registro de selagem na tabela selados.
 */
public class Selado implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== IDENTIFICAÇÃO ==========
    private Long id;                    // ID - INT(11) UNSIGNED - AUTO_INCREMENT
    private String numTipAto;           // NUMTIPATO - CHAR(3) - NOT NULL
    private String idap;                // IDAP - VARCHAR(40)
    private String chave;               // CHAVE - VARCHAR(30)
    private String selo;                // SELO - VARCHAR(30)
    
    // ========== INFORMAÇÕES DO REGISTRO ==========
    private String qrcode;              // QRCODE - VARCHAR(1000)
    private String imagem;              // IMG - VARCHAR(30000)
    private String registro;            // REGISTRO - VARCHAR(30)
    private String json;                // JSON - VARCHAR(2000)
    
    // ========== DATAS ==========
    private String dataEnvio;           // DATAENVIO - VARCHAR(30) - ISO 8601
    private String dataRetorno;         // DATARETORNO - VARCHAR(30)
    
    // ========== STATUS E RETORNO ==========
    private String retorno;             // RETORNO - VARCHAR(100)
    private String usuario;             // USUARIO - VARCHAR(30)
    private String status;              // STATUS - VARCHAR(100)
    
    // ========== CAMPOS DE CONTROLE DO SISTEMA ==========
    private Date dataCriacao;
    private Date dataAtualizacao;
    
    // ========== CONSTRUTORES ==========
    public Selado() {
        this.dataCriacao = new Date();
        this.dataAtualizacao = new Date();
    }
    
    public Selado(String selo, String numTipAto, String registro, String idap) {
        this();
        this.selo = selo;
        this.numTipAto = numTipAto;
        this.registro = registro;
        this.idap = idap;
        this.chave = selo; // Chave é o próprio número do selo
        this.qrcode = "https://selo.funarpen.com.br/consulta/" + selo;
        this.dataEnvio = new Date().toInstant().toString(); // ISO 8601
        this.status = "ENVIADO";
    }
    
    // ========== MÉTODOS DE NEGÓCIO ==========
    
    /**
     * Verifica se o registro foi enviado
     */
    public boolean isEnviado() {
        return dataEnvio != null && !dataEnvio.trim().isEmpty();
    }
    
    /**
     * Verifica se há retorno do sistema externo
     */
    public boolean isRetornado() {
        return dataRetorno != null && !dataRetorno.trim().isEmpty();
    }
    
    /**
     * Verifica se o envio foi bem sucedido
     */
    public boolean isSucesso() {
        return "SUCESSO".equalsIgnoreCase(status) || 
               "CONCLUIDO".equalsIgnoreCase(status) ||
               "PROCESSADO".equalsIgnoreCase(status);
    }
    
    /**
     * Verifica se há erro no processamento
     */
    public boolean isErro() {
        return status != null && (
            status.toUpperCase().contains("ERRO") ||
            status.toUpperCase().contains("FALHA") ||
            status.toUpperCase().contains("INVALIDO")
        );
    }
    
    /**
     * Retorna a URL do QR Code
     */
    public String getQrcodeUrl() {
        if (qrcode != null && !qrcode.trim().isEmpty()) {
            return qrcode;
        }
        if (selo != null && !selo.trim().isEmpty()) {
            return "https://selo.funarpen.com.br/consulta/" + selo;
        }
        return "";
    }
    
    /**
     * Retorna a data de envio formatada
     */
    public String getDataEnvioFormatada() {
        if (dataEnvio == null) return "";
        
        try {
            // Tenta converter ISO 8601 para formato BR
            if (dataEnvio.contains("T")) {
                Date data = javax.xml.bind.DatatypeConverter.parseDateTime(dataEnvio).getTime();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                return sdf.format(data);
            }
        } catch (Exception e) {
            // Se não conseguir converter, retorna o original
        }
        
        return dataEnvio;
    }
    
    /**
     * Retorna a data de retorno formatada
     */
    public String getDataRetornoFormatada() {
        if (dataRetorno == null) return "";
        
        try {
            // Tenta converter ISO 8601 para formato BR
            if (dataRetorno.contains("T")) {
                Date data = javax.xml.bind.DatatypeConverter.parseDateTime(dataRetorno).getTime();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                return sdf.format(data);
            }
        } catch (Exception e) {
            // Se não conseguir converter, retorna o original
        }
        
        return dataRetorno;
    }
    
    /**
     * Marca como processado com sucesso
     */
    public void marcarComoSucesso(String retornoMensagem) {
        this.dataRetorno = new Date().toInstant().toString();
        this.retorno = retornoMensagem;
        this.status = "SUCESSO";
        this.dataAtualizacao = new Date();
    }
    
    /**
     * Marca como erro no processamento
     */
    public void marcarComoErro(String erroMensagem) {
        this.dataRetorno = new Date().toInstant().toString();
        this.retorno = erroMensagem;
        this.status = "ERRO";
        this.dataAtualizacao = new Date();
    }
    
    /**
     * Retorna o JSON como objeto (se disponível)
     */
    public Object getJsonObject() {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            return new com.google.gson.Gson().fromJson(json, Object.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Define o JSON a partir de um objeto
     */
    public void setJsonObject(Object objeto) {
        if (objeto == null) {
            this.json = null;
        } else {
            this.json = new com.google.gson.Gson().toJson(objeto);
        }
    }
    
    // ========== GETTERS E SETTERS ==========
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumTipAto() { return numTipAto; }
    public void setNumTipAto(String numTipAto) { this.numTipAto = numTipAto; }
    
    public String getIdap() { return idap; }
    public void setIdap(String idap) { this.idap = idap; }
    
    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }
    
    public String getSelo() { return selo; }
    public void setSelo(String selo) { this.selo = selo; }
    
    public String getQrcode() { return qrcode; }
    public void setQrcode(String qrcode) { this.qrcode = qrcode; }
    
    public String getRegistro() { return registro; }
    public void setRegistro(String registro) { this.registro = registro; }
    
    public String getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(String dataEnvio) { this.dataEnvio = dataEnvio; }
    
    public String getDataRetorno() { return dataRetorno; }
    public void setDataRetorno(String dataRetorno) { this.dataRetorno = dataRetorno; }
    
    public String getRetorno() { return retorno; }
    public void setRetorno(String retorno) { this.retorno = retorno; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public Date getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(Date dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    
    public String getJson() { return json; }
    public void setJson(String json) { this.json = json; }
    
    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }
    
    // ========== EQUALS E HASHCODE ==========
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Selado selado = (Selado) o;
        return id != null ? id.equals(selado.id) : selado.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    // ========== TO STRING ==========
    @Override
    public String toString() {
        return "Selado{" +
                "id=" + id +
                ", selo='" + selo + '\'' +
                ", registro='" + registro + '\'' +
                ", status='" + status + '\'' +
                ", dataEnvio='" + getDataEnvioFormatada() + '\'' +
                '}';
    }
}