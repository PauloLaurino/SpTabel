package com.seprocom.gerencial.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA para Cabecalho de Nota Fiscal de Servicos (NFSe).
 * Mapeia a tabela notascab do banco de dados sptabel.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "notascab")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotasCab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "numero_nota", length = 20)
    private String numeroNota;

    @Column(name = "serie", length = 10)
    @Builder.Default
    private String serie = "001";

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_competencia")
    private LocalDate dataCompetencia;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(name = "situacao", length = 1)
    @Builder.Default
    private String situacao = "P"; // P=Pendente, E=Emitida, C=Cancelada, X=Erro

    // Tomador (Cliente)
    @Column(name = "tomador_cnpj_cpf", length = 20)
    private String tomadorCnpjCpf;

    @Column(name = "tomador_nome", length = 200)
    private String tomadorNome;

    @Column(name = "tomador_endereco", length = 500)
    private String tomadorEndereco;

    @Column(name = "tomador_bairro", length = 100)
    private String tomadorBairro;

    @Column(name = "tomador_cidade", length = 100)
    private String tomadorCidade;

    @Column(name = "tomador_uf", length = 2)
    private String tomadorUf;

    @Column(name = "tomador_cep", length = 10)
    private String tomadorCep;

    @Column(name = "tomador_email", length = 200)
    private String tomadorEmail;

    @Column(name = "tomador_telefone", length = 20)
    private String tomadorTelefone;

    // Valores
    @Column(name = "valor_servico", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorServico = BigDecimal.ZERO;

    @Column(name = "valor_iss", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorIss = BigDecimal.ZERO;

    @Column(name = "valor_desc", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorDesc = BigDecimal.ZERO;

    @Column(name = "valor_total", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    // Informacoes do servico
    @Column(name = "discriminacao", columnDefinition = "TEXT")
    private String discriminacao;

    @Column(name = "codigo_municipio", length = 10)
    private String codigoMunicipio;

    @Column(name = "codigo_servico", length = 10)
    private String codigoServico;

    @Column(name = "aliquota", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal aliquota = BigDecimal.ZERO;

    // Informacoes do emitente
    @Column(name = "emitente_cnpj", length = 20)
    private String emitenteCnpj;

    @Column(name = "emitente_inscricao_municipal", length = 20)
    private String emitenteInscricaoMunicipal;

    @Column(name = "emitente_razao_social", length = 200)
    private String emitenteRazaoSocial;

    // Dados do NFSe (preenchidos apos emissao)
    @Column(name = "chave_nfse", length = 50)
    private String chaveNfse;

    @Column(name = "numero_rps", length = 20)
    private String numeroRps;

    @Column(name = "link_consulta", length = 500)
    private String linkConsulta;

    @Column(name = "xml_nfse", columnDefinition = "TEXT")
    private String xmlNfse;

    @Column(name = "protocolo", length = 50)
    private String protocolo;

    @Column(name = "mensagem_erro", length = 500)
    private String mensagemErro;

    // Observacoes
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    // Controle
    @Column(name = "codigo_usu")
    private Integer codigoUsu;

    @Column(name = "dtcadastro")
    private LocalDateTime dtcadastro;

    @Column(name = "dtalteracao")
    private LocalDateTime dtalteracao;

    /**
     * Adiciona um item a nota e atualiza os totais.
     */
    public void addItem(NotasDet item) {
        item.setNotaCabId(this.id);
        this.valorServico = this.valorServico.add(item.getValorTotal());
        recalcularTotais();
    }

    /**
     * Remove um item da nota e atualiza os totais.
     */
    public void removeItem(NotasDet item) {
        this.valorServico = this.valorServico.subtract(item.getValorTotal());
        recalcularTotais();
    }

    /**
     * Recalcula os totais da nota com base nos valores.
     */
    public void recalcularTotais() {
        // Valor total = Valor servico - Desconto
        this.valorTotal = this.valorServico.subtract(this.valorDesc != null ? this.valorDesc : BigDecimal.ZERO);
        
        // ISS calculado automaticamente se aliquota estiver definida
        if (this.aliquota != null && this.aliquota.compareTo(BigDecimal.ZERO) > 0) {
            this.valorIss = this.valorServico.multiply(this.aliquota).divide(BigDecimal.valueOf(100));
        }
    }

    /**
     * Metodo chamado antes de persistir
     */
    @PrePersist
    protected void onCreate() {
        if (dtcadastro == null) dtcadastro = LocalDateTime.now();
        if (dtalteracao == null) dtalteracao = LocalDateTime.now();
        if (dataEmissao == null) dataEmissao = LocalDate.now();
        if (situacao == null) situacao = "P";
    }

    /**
     * Metodo chamado antes de atualizar
     */
    @PreUpdate
    protected void onUpdate() {
        dtalteracao = LocalDateTime.now();
    }
}