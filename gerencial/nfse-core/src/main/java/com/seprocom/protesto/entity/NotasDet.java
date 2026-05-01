package com.seprocom.protesto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * Entidade JPA para itens das notas fiscais (notasdet).
 */
@Entity
@Table(name = "notasdet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotasDet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_not")
    private Long id;

    @NotNull(message = "ID da nota e obrigatorio")
    @Column(name = "id_not", insertable = false, updatable = false)
    private Long idNota;

    @NotNull(message = "Numero do item e obrigatorio")
    @Column(name = "item_not")
    private Integer item;

    @Size(max = 20, message = "Codigo do produto deve ter no maximo 20 caracteres")
    @Column(name = "codprod_not")
    private String codigoProduto;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
    @Column(name = "desc_not")
    private String descricao;

    @NotNull(message = "Quantidade e obrigatoria")
    @Column(name = "qtd_not")
    private BigDecimal quantidade;

    @NotNull(message = "Valor unitario e obrigatorio")
    @Column(name = "vlunit_not")
    private BigDecimal valorUnitario;

    @NotNull(message = "Valor total e obrigatorio")
    @Column(name = "vltotal_not")
    private BigDecimal valorTotal;

    @Column(name = "codigo_servico")
    private String codigoServico;

    @Size(max = 20, message = "Unidade deve ter no maximo 20 caracteres")
    @Column(name = "unidade")
    private String unidade;

    @Column(name = "aliquota_iss", precision = 5, scale = 2)
    private BigDecimal aliquotaIss;

    @Column(name = "valor_iss", precision = 15, scale = 2)
    private BigDecimal valorIss;

    @Column(name = "valor_deducao", precision = 15, scale = 2)
    private BigDecimal valorDeducao;

    @Column(name = "valor_desconto_incond", precision = 15, scale = 2)
    private BigDecimal valorDescontoIncond;

    @Column(name = "valor_outras_retencoes", precision = 15, scale = 2)
    private BigDecimal valorOutrasRetencoes;

    @Size(max = 10, message = "Codigo municipio deve ter no maximo 10 caracteres")
    @Column(name = "codigo_municipio")
    private String codigoMunicipio;

    @Size(max = 10, message = "Codigo incidencia deve ter no maximo 10 caracteres")
    @Column(name = "codigo_incidencia")
    private String codigoIncidencia;

    @Size(max = 10, message = "Codigo pais deve ter no maximo 10 caracteres")
    @Column(name = "codigo_pais")
    private String codigoPais;

    @Size(max = 10, message = "Exigibilidade ISS deve ter no maximo 10 caracteres")
    @Column(name = "exigibilidade_iss")
    private String exigibilidadeIss;

    @Size(max = 500, message = "Descricao exigibilidade deve ter no maximo 500 caracteres")
    @Column(name = "descricao_exigibilidade")
    private String descricaoExigibilidade;

    @Size(max = 50, message = "Documento origem deve ter no maximo 50 caracteres")
    @Column(name = "documento_origem")
    private String documentoOrigem;

    @Size(max = 20, message = "Tipo documento origem deve ter no maximo 20 caracteres")
    @Column(name = "tipo_documento_origem")
    private String tipoDocumentoOrigem;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Size(max = 100, message = "Usuario criacao deve ter no maximo 100 caracteres")
    @Column(name = "usuario_criacao")
    private String usuarioCriacao;

    @Size(max = 100, message = "Usuario atualizacao deve ter no maximo 100 caracteres")
    @Column(name = "usuario_atualizacao")
    private String usuarioAtualizacao;

    /** Relacionamento com cabeçalho da nota (bidirectional) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_not", insertable = false, updatable = false)
    @JsonIgnore
    private NotasCab notaCab;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public void calcularValorServico() {
        if (quantidade != null && valorUnitario != null) {
            this.valorTotal = quantidade.multiply(valorUnitario);
        }
    }

    public void calcularIss() {
        if (aliquotaIss != null && aliquotaIss.compareTo(BigDecimal.ZERO) > 0 && valorTotal != null) {
            BigDecimal baseCalc = quantidade.multiply(valorUnitario);
            this.valorIss = baseCalc.multiply(aliquotaIss).divide(BigDecimal.valueOf(100));
        }
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdNota() {
        return idNota;
    }

    public void setIdNota(Long idNota) {
        this.idNota = idNota;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getCodigoServico() {
        return codigoServico;
    }

    public void setCodigoServico(String codigoServico) {
        this.codigoServico = codigoServico;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getAliquotaIss() {
        return aliquotaIss;
    }

    public void setAliquotaIss(BigDecimal aliquotaIss) {
        this.aliquotaIss = aliquotaIss;
    }

    public BigDecimal getValorIss() {
        return valorIss;
    }

    public void setValorIss(BigDecimal valorIss) {
        this.valorIss = valorIss;
    }

    public BigDecimal getValorDeducao() {
        return valorDeducao;
    }

    public void setValorDeducao(BigDecimal valorDeducao) {
        this.valorDeducao = valorDeducao;
    }

    public BigDecimal getValorDescontoIncond() {
        return valorDescontoIncond;
    }

    public void setValorDescontoIncond(BigDecimal valorDescontoIncond) {
        this.valorDescontoIncond = valorDescontoIncond;
    }

    public BigDecimal getValorOutrasRetencoes() {
        return valorOutrasRetencoes;
    }

    public void setValorOutrasRetencoes(BigDecimal valorOutrasRetencoes) {
        this.valorOutrasRetencoes = valorOutrasRetencoes;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getCodigoIncidencia() {
        return codigoIncidencia;
    }

    public void setCodigoIncidencia(String codigoIncidencia) {
        this.codigoIncidencia = codigoIncidencia;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public String getExigibilidadeIss() {
        return exigibilidadeIss;
    }

    public void setExigibilidadeIss(String exigibilidadeIss) {
        this.exigibilidadeIss = exigibilidadeIss;
    }

    public String getDescricaoExigibilidade() {
        return descricaoExigibilidade;
    }

    public void setDescricaoExigibilidade(String descricaoExigibilidade) {
        this.descricaoExigibilidade = descricaoExigibilidade;
    }

    public String getDocumentoOrigem() {
        return documentoOrigem;
    }

    public void setDocumentoOrigem(String documentoOrigem) {
        this.documentoOrigem = documentoOrigem;
    }

    public String getTipoDocumentoOrigem() {
        return tipoDocumentoOrigem;
    }

    public void setTipoDocumentoOrigem(String tipoDocumentoOrigem) {
        this.tipoDocumentoOrigem = tipoDocumentoOrigem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getUsuarioCriacao() {
        return usuarioCriacao;
    }

    public void setUsuarioCriacao(String usuarioCriacao) {
        this.usuarioCriacao = usuarioCriacao;
    }

    public String getUsuarioAtualizacao() {
        return usuarioAtualizacao;
    }

    public void setUsuarioAtualizacao(String usuarioAtualizacao) {
        this.usuarioAtualizacao = usuarioAtualizacao;
    }

    /** Alias: valor do serviço (mapeado para valorTotal) */
    public BigDecimal getValorServico() {
        return valorTotal;
    }

    /** Alias: base de cálculo (quantidade * valorUnitario) */
    public BigDecimal getBaseCalculo() {
        if (quantidade != null && valorUnitario != null) {
            return quantidade.multiply(valorUnitario);
        }
        return valorTotal != null ? valorTotal : BigDecimal.ZERO;
    }

    public NotasCab getNotaCab() {
        return notaCab;
    }

    public void setNotaCab(NotasCab notaCab) {
        this.notaCab = notaCab;
    }
}
