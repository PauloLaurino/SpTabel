package com.selador.model;

import com.selador.enums.TipoSelo;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Model que representa um selo da tabela selos.
 */
public class Selo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ========== IDENTIFICAÇÃO ==========
    private Long id;                    // ID_sel - INT(11) - AUTO_INCREMENT
    private String numeroSelo;          // selo_sel - CHAR(50) - NOT NULL
    private TipoSelo tipoSelo;          // tiposelo_sel - CHAR(4) → Enum
    private String tipo;                // tipo_sel - CHAR(4) - '0000'=disponível, '0701'=utilizado
    private String validador;           // validador_sel - CHAR(10)
    
    // ========== VALORES ==========
    private BigDecimal valor;           // valorselo_sel - DECIMAL(9,2)
    private BigDecimal emolumentos;     // emolumentos_sel - DECIMAL(9,2)
    private BigDecimal frj;             // frj_sel - DECIMAL(9,2)
    
    // ========== INFORMAÇÕES DO PEDIDO ==========
    private String pedido;              // pedido_sel - CHAR(30)
    private Date dataPedido;            // dataped_sel - DATE
    private String numeroRecibo;        // numrec_sel - CHAR(10)
    private Date dataSelo;              // dataselo_sel - DATE
    private String numeroRequisicao;    // numreq_sel - CHAR(10)
    
    // ========== CHAVES DE RELACIONAMENTO ==========
    private String chave1;              // chave1_sel - CHAR(6) → numapo1_001
    private String chave2;              // chave2_sel - CHAR(10) → numapo2_001
    private String controle;            // controle_sel - CHAR(2)
    
    // ========== INFORMAÇÕES DO ATO ==========
    private Integer codigoCartorio;     // codcartorio - INT(9)
    private Integer codigoTipoAto;      // codtipoato - INT(9) → 701, 702, etc.
    private String numeroPedido;        // numpedido - CHAR(50)
    
    // ========== INFORMAÇÕES DO CONTRIBUINTE ==========
    private String nome;                // nome - CHAR(100)
    private String cpfCnpj;             // cpfcnpj - CHAR(20)
    private String folha;               // folha - CHAR(10)
    private String livro;               // livro - CHAR(10)
    private String quantidadeProtestos; // qtdeprotestos - CHAR(2)
    
    // ========== INFORMAÇÕES DE PROTOCOLO ==========
    private String protocolo;           // protocolo - CHAR(20)
    private Integer tipoSeloNum;        // tiposelo - INT(9)
    private Integer tipoAcao;           // tipoacao - INT(9)
    
    // ========== DATAS E HORAS ==========
    private Date dataHora;              // datahora - CHAR(30) → Date
    private String chaveDigital;        // chavedigital - CHAR(50)
    
    // ========== INFORMAÇÕES DE VEÍCULO (para transferências) ==========
    private String placa;               // Placa - CHAR(15)
    private String crv;                 // CRV - CHAR(20)
    private String renavam;             // Renavam - CHAR(20)
    private String dataRegistro;        // data_registro - CHAR(10)
    private String identificacaoAto;    // identificacao_ato - CHAR(2)
    
    // ========== STATUS E CONTROLE ==========
    private boolean cancelado;          // cancelado - CHAR(1) - 'S'/'N'
    private String idap;                // IDAP - VARCHAR(40)
    private String filler1;             // filler1 - VARCHAR(60)
    private String versao;              // versao - CHAR(3)
    private String filler;              // filler - VARCHAR(24)
    
    // ========== CAMPOS DE CONTROLE DO SISTEMA ==========
    private Date dataCriacao;
    private Date dataAtualizacao;
    private String usuarioCriacao;
    private String usuarioAtualizacao;
    
    // ========== CONSTRUTORES ==========
    public Selo() {
        this.tipo = "0000"; // Disponível por padrão
        this.cancelado = false;
        this.dataCriacao = new Date();
    }
    
    public Selo(String numeroSelo, TipoSelo tipoSelo) {
        this();
        this.numeroSelo = numeroSelo;
        this.tipoSelo = tipoSelo;
    }
    
    // ========== MÉTODOS DE NEGÓCIO ==========
    
    /**
     * Verifica se o selo está disponível para uso
     * Baseado na estrutura real da tabela
     */
    public boolean isDisponivel() {
        return "0000".equals(tipo) && 
            dataSelo == null &&  // ← ADICIONAR ESTA VERIFICAÇÃO
            (chave1 == null || chave1.trim().isEmpty()) &&
            !cancelado;
    }
    
    /**
     * Verifica se o selo foi utilizado
     */
    public boolean isUtilizado() {
        return "0701".equals(tipo) || 
               (chave1 != null && !chave1.trim().isEmpty());
    }
    
    /**
     * Verifica se o selo é do tipo padrão para intimação (0004)
     */
    public boolean isTipoPadraoIntimacao() {
        return tipoSelo != null && tipoSelo.getCodigo().equals("0004");
    }
    
    /**
     * Retorna o valor total (valor + emolumentos + frj)
     */
    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        
        if (valor != null) total = total.add(valor);
        if (emolumentos != null) total = total.add(emolumentos);
        if (frj != null) total = total.add(frj);
        
        return total;
    }
    
    /**
     * Retorna a descrição completa do tipo de selo
     */
    public String getDescricaoCompleta() {
        if (tipoSelo != null) {
            return tipoSelo.getSigla() + " - " + tipoSelo.getDescricao();
        }
        return "Selo " + numeroSelo;
    }
    
    /**
     * Formata o número do selo para exibição
     */
    public String getNumeroFormatado() {
        if (numeroSelo == null || numeroSelo.length() < 10) {
            return numeroSelo;
        }
        
        // Formata como XXXXX-XXXXX-XXXXX...
        StringBuilder formatado = new StringBuilder();
        for (int i = 0; i < numeroSelo.length(); i++) {
            if (i > 0 && i % 5 == 0) {
                formatado.append("-");
            }
            formatado.append(numeroSelo.charAt(i));
        }
        
        return formatado.toString();
    }
    
    /**
     * Retorna a URL do QR Code para consulta
     */
    public String getQrcodeUrl() {
        if (numeroSelo == null) return "";
        return "https://selo.funarpen.com.br/consulta/" + numeroSelo;
    }
    
    /**
     * Marca o selo como utilizado
     */
    public void marcarComoUtilizado(String chave1, String chave2, String protocolo, 
                                   String nome, String cpfCnpj, String idap) {
        this.tipo = "0701";
        this.chave1 = chave1;
        this.chave2 = chave2;
        this.protocolo = protocolo;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.idap = idap;
        this.dataSelo = new Date();
        this.dataHora = new Date();
        this.chaveDigital = this.numeroSelo; // Chave digital é o próprio número do selo
        this.dataAtualizacao = new Date();
    }
    
    // ========== GETTERS E SETTERS ==========
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroSelo() { return numeroSelo; }
    public void setNumeroSelo(String numeroSelo) { this.numeroSelo = numeroSelo; }
    
    public TipoSelo getTipoSelo() { return tipoSelo; }
    public void setTipoSelo(TipoSelo tipoSelo) { this.tipoSelo = tipoSelo; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public String getChave1() { return chave1; }
    public void setChave1(String chave1) { this.chave1 = chave1; }
    
    public String getChave2() { return chave2; }
    public void setChave2(String chave2) { this.chave2 = chave2; }
    
    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    
    public Date getDataSelo() { return dataSelo; }
    public void setDataSelo(Date dataSelo) { this.dataSelo = dataSelo; }
    
    public Date getDataHora() { return dataHora; }
    public void setDataHora(Date dataHora) { this.dataHora = dataHora; }
    
    public String getIdap() { return idap; }
    public void setIdap(String idap) { this.idap = idap; }
    
    public boolean isCancelado() { return cancelado; }
    public void setCancelado(boolean cancelado) { this.cancelado = cancelado; }
    
    public Date getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Date dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public Date getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(Date dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public String getValidador() { return validador; }
    public void setValidador(String validador) { this.validador = validador; }

    public BigDecimal getEmolumentos() { return emolumentos; }
    public void setEmolumentos(BigDecimal emolumentos) { this.emolumentos = emolumentos; }

    public BigDecimal getFrj() { return frj; }
    public void setFrj(BigDecimal frj) { this.frj = frj; }

    public String getPedido() { return pedido; }
    public void setPedido(String pedido) { this.pedido = pedido; }

    public Date getDataPedido() { return dataPedido; }
    public void setDataPedido(Date dataPedido) { this.dataPedido = dataPedido; }

    public String getNumeroRecibo() { return numeroRecibo; }
    public void setNumeroRecibo(String numeroRecibo) { this.numeroRecibo = numeroRecibo; }

    public String getNumeroRequisicao() { return numeroRequisicao; }
    public void setNumeroRequisicao(String numeroRequisicao) { this.numeroRequisicao = numeroRequisicao; }

    public String getControle() { return controle; }
    public void setControle(String controle) { this.controle = controle; }

    public Integer getCodigoCartorio() { return codigoCartorio; }
    public void setCodigoCartorio(Integer codigoCartorio) { this.codigoCartorio = codigoCartorio; }

    public Integer getCodigoTipoAto() { return codigoTipoAto; }
    public void setCodigoTipoAto(Integer codigoTipoAto) { this.codigoTipoAto = codigoTipoAto; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public String getFolha() { return folha; }
    public void setFolha(String folha) { this.folha = folha; }

    public String getLivro() { return livro; }
    public void setLivro(String livro) { this.livro = livro; }

    public String getQuantidadeProtestos() { return quantidadeProtestos; }
    public void setQuantidadeProtestos(String quantidadeProtestos) { this.quantidadeProtestos = quantidadeProtestos; }

    public Integer getTipoSeloNum() { return tipoSeloNum; }
    public void setTipoSeloNum(Integer tipoSeloNum) { this.tipoSeloNum = tipoSeloNum; }

    public Integer getTipoAcao() { return tipoAcao; }
    public void setTipoAcao(Integer tipoAcao) { this.tipoAcao = tipoAcao; }

    public String getChaveDigital() { return chaveDigital; }
    public void setChaveDigital(String chaveDigital) { this.chaveDigital = chaveDigital; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getCrv() { return crv; }
    public void setCrv(String crv) { this.crv = crv; }

    public String getRenavam() { return renavam; }
    public void setRenavam(String renavam) { this.renavam = renavam; }

    public String getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(String dataRegistro) { this.dataRegistro = dataRegistro; }

    public String getIdentificacaoAto() { return identificacaoAto; }
    public void setIdentificacaoAto(String identificacaoAto) { this.identificacaoAto = identificacaoAto; }

    public String getFiller1() { return filler1; }
    public void setFiller1(String filler1) { this.filler1 = filler1; }

    public String getVersao() { return versao; }
    public void setVersao(String versao) { this.versao = versao; }

    public String getFiller() { return filler; }
    public void setFiller(String filler) { this.filler = filler; }

    public String getUsuarioCriacao() { return usuarioCriacao; }
    public void setUsuarioCriacao(String usuarioCriacao) { this.usuarioCriacao = usuarioCriacao; }

    public String getUsuarioAtualizacao() { return usuarioAtualizacao; }
    public void setUsuarioAtualizacao(String usuarioAtualizacao) { this.usuarioAtualizacao = usuarioAtualizacao; }
    
    // ========== EQUALS E HASHCODE ==========
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Selo selo = (Selo) o;
        return numeroSelo != null ? numeroSelo.equals(selo.numeroSelo) : selo.numeroSelo == null;
    }
    
    @Override
    public int hashCode() {
        return numeroSelo != null ? numeroSelo.hashCode() : 0;
    }
    
    // ========== TO STRING ==========
    @Override
    public String toString() {
        return "Selo{" +
                "numeroSelo='" + getNumeroFormatado() + '\'' +
                ", tipoSelo=" + tipoSelo +
                ", tipo='" + tipo + '\'' +
                ", disponivel=" + isDisponivel() +
                '}';
    }
}