package com.selador.model;

import java.math.BigDecimal;

/**
 * Model para a tabela ser_custas - Custas e Serviços do Tabelionato
 */
public class SerCustas {
    
    // Campos da chave primária
    private String sistemaCus;      // SISTEMA_CUS CHAR(1)
    private int codigoCus;          // CODIGO_CUS SMALLINT(3)
    
    // Campos de dados
    private String moduloCus;       // MODULO_CUS CHAR(1)
    private int tipoatoCus;         // TIPOATO_CUS SMALLINT(3)
    private String classCus;        // CLASS_CUS CHAR(1)
    private String descrCus;        // DESCR_CUS VARCHAR(50)
    private int qtdeCus;            // QTDE_CUS DECIMAL(3,0)
    private BigDecimal valorCus;    // VALOR_CUS DECIMAL(12,4)
    private String informCus;       // INFOR_CUS CHAR(1)
    private BigDecimal qtdevrcCus;  // QTDEVRC_CUS DECIMAL(8,4)
    private BigDecimal aliqCus;     // ALIQ_CUS DECIMAL(8,4)
    private BigDecimal aliqfrjCus;  // ALIQFRJ_CUS DECIMAL(8,4)
    private String avaliadoCus;     // AVALIADO_CUS CHAR(1)
    private String baseCus;         // BASE_CUS CHAR(1)
    private String agregadoCus;     // AGREGADO_CUS CHAR(1)
    private String isentoCus;       // ISENTO_CUS CHAR(1)
    private String herculesCus;     // HERCULES_CUS CHAR(1)
    private String hercqtdeCus;     // HERCQTDE_CUS CHAR(1)
    private String classCod;        // CLASS_COD CHAR(2)
    private String tagCus;          // TAG_CUS CHAR(20)
    
    // Construtores
    public SerCustas() {
    }
    
    public SerCustas(String sistemaCus, int codigoCus) {
        this.sistemaCus = sistemaCus;
        this.codigoCus = codigoCus;
    }
    
    // Getters e Setters
    public String getSistemaCus() {
        return sistemaCus;
    }
    
    public void setSistemaCus(String sistemaCus) {
        this.sistemaCus = sistemaCus;
    }
    
    public int getCodigoCus() {
        return codigoCus;
    }
    
    public void setCodigoCus(int codigoCus) {
        this.codigoCus = codigoCus;
    }
    
    public String getModuloCus() {
        return moduloCus;
    }
    
    public void setModuloCus(String moduloCus) {
        this.moduloCus = moduloCus;
    }
    
    public int getTipoatoCus() {
        return tipoatoCus;
    }
    
    public void setTipoatoCus(int tipoatoCus) {
        this.tipoatoCus = tipoatoCus;
    }
    
    public String getClassCus() {
        return classCus;
    }
    
    public void setClassCus(String classCus) {
        this.classCus = classCus;
    }
    
    public String getDescrCus() {
        return descrCus;
    }
    
    public void setDescrCus(String descrCus) {
        this.descrCus = descrCus;
    }
    
    public int getQtdeCus() {
        return qtdeCus;
    }
    
    public void setQtdeCus(int qtdeCus) {
        this.qtdeCus = qtdeCus;
    }
    
    public BigDecimal getValorCus() {
        return valorCus;
    }
    
    public void setValorCus(BigDecimal valorCus) {
        this.valorCus = valorCus;
    }
    
    public String getInformCus() {
        return informCus;
    }
    
    public void setInformCus(String informCus) {
        this.informCus = informCus;
    }
    
    public BigDecimal getQtdevrcCus() {
        return qtdevrcCus;
    }
    
    public void setQtdevrcCus(BigDecimal qtdevrcCus) {
        this.qtdevrcCus = qtdevrcCus;
    }
    
    public BigDecimal getAliqCus() {
        return aliqCus;
    }
    
    public void setAliqCus(BigDecimal aliqCus) {
        this.aliqCus = aliqCus;
    }
    
    public BigDecimal getAliqfrjCus() {
        return aliqfrjCus;
    }
    
    public void setAliqfrjCus(BigDecimal aliqfrjCus) {
        this.aliqfrjCus = aliqfrjCus;
    }
    
    public String getAvaliadoCus() {
        return avaliadoCus;
    }
    
    public void setAvaliadoCus(String avaliadoCus) {
        this.avaliadoCus = avaliadoCus;
    }
    
    public String getBaseCus() {
        return baseCus;
    }
    
    public void setBaseCus(String baseCus) {
        this.baseCus = baseCus;
    }
    
    public String getAgregadoCus() {
        return agregadoCus;
    }
    
    public void setAgregadoCus(String agregadoCus) {
        this.agregadoCus = agregadoCus;
    }
    
    public String getIsentoCus() {
        return isentoCus;
    }
    
    public void setIsentoCus(String isentoCus) {
        this.isentoCus = isentoCus;
    }
    
    public String getHerculesCus() {
        return herculesCus;
    }
    
    public void setHerculesCus(String herculesCus) {
        this.herculesCus = herculesCus;
    }
    
    public String getHercqtdeCus() {
        return hercqtdeCus;
    }
    
    public void setHercqtdeCus(String hercqtdeCus) {
        this.hercqtdeCus = hercqtdeCus;
    }
    
    public String getClassCod() {
        return classCod;
    }
    
    public void setClassCod(String classCod) {
        this.classCod = classCod;
    }
    
    public String getTagCus() {
        return tagCus;
    }
    
    public void setTagCus(String tagCus) {
        this.tagCus = tagCus;
    }
    
    /**
     * Verifica se é um ato principal (não agregado)
     */
    public boolean isAtoPrincipal() {
        return !"S".equals(agregadoCus);
    }
    
    /**
     * Verifica se é um encargo (FUNDEP, ISS, FUNREJUS, SELO, etc.)
     */
    public boolean isEncargo() {
        return "C".equals(moduloCus);
    }
    
    /**
     * Verifica se usa valor de referência progressivo
     */
    public boolean hasValorReferencia() {
        return qtdevrcCus != null && qtdevrcCus.compareTo(BigDecimal.ZERO) > 0;
    }
    
    @Override
    public String toString() {
        return "SerCustas{" +
                "codigoCus=" + codigoCus +
                ", descrCus='" + descrCus + '\'' +
                ", valorCus=" + valorCus +
                ", moduloCus=" + moduloCus +
                '}';
    }
}
