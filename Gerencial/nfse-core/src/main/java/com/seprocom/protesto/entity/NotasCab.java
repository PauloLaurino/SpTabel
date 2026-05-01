package com.seprocom.protesto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa o cabeçalho de uma NFSe (Nota Fiscal de Serviços Eletrônica).
 * 
 * Armazena os dados principais da nota fiscal: tomador, valores, status, etc.
 * Relaciona-se com {@link NotasDet} para os itens detalhados dos serviços.
 * 
 * Tabela: notascab
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
@ToString(exclude = {"itens"})
@EqualsAndHashCode(of = "id")
public class NotasCab implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nota_id")
    private Long id;

    /**
     * Número da NFSe
     */
    @Size(max = 20, message = "Número da nota deve ter no máximo 20 caracteres")
    @Column(name = "numero_nota", length = 20)
    private String numeroNota;

    /**
     * Série da NFSe
     */
    @Size(max = 10, message = "Série deve ter no máximo 10 caracteres")
    @Column(name = "serie", length = 10)
    @Builder.Default
    private String serie = "001";

    /**
     * Código de verificação da NFSe (gerado pela prefeitura)
     */
    @Size(max = 50, message = "Código de verificação deve ter no máximo 50 caracteres")
    @Column(name = "codigo_verificacao", length = 50)
    private String codigoVerificacao;

    /**
     * Data de emissão da NFSe
     */
    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    /**
     * Data de competência (data do serviço)
     */
    @Column(name = "data_competencia")
    private LocalDate dataCompetencia;

    /**
     * Situação da NFSe:
     * E = Emitida/Enviada
     * P = Pendente
     * C = Cancelada
     * R = Rejeitada
     * B = Boleto gerado
     */
    @Size(max = 1, message = "Situação deve ter 1 caractere")
    @Column(name = "situacao", length = 1)
    @Builder.Default
    private String situacao = "P";

    /**
     * Tipo de NFS-e (municipal, federal, etc)
     */
    @Size(max = 20, message = "Tipo deve ter no máximo 20 caracteres")
    @Column(name = "tipo", length = 20)
    @Builder.Default
    private String tipo = "NFS-e";

    /**
     * Valor total dos serviços
     */
    @Column(name = "valor_servico", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorServico = BigDecimal.ZERO;

    /**
     * Valor do ISS (Imposto Sobre Serviços)
     */
    @Column(name = "valor_iss", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorIss = BigDecimal.ZERO;

    /**
     * Valor total da nota (serviço + iss)
     */
    @Column(name = "valor_total", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    /**
     * Alíquota do ISS (%)
     */
    @Column(name = "aliquota_iss", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal aliquotaIss = BigDecimal.ZERO;

    /**
     * Base de cálculo do ISS
     */
    @Column(name = "base_calculo", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal baseCalculo = BigDecimal.ZERO;

    /**
     * Valor deduções
     */
    @Column(name = "valor_deducao", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorDeducao = BigDecimal.ZERO;

    /**
     * Valor outras retenções
     */
    @Column(name = "valor_outras_retencoes", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorOutrasRetencoes = BigDecimal.ZERO;

    /**
     * Valor desconto incondicionado
     */
    @Column(name = "valor_desconto_incond", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorDescontoIncond = BigDecimal.ZERO;

    /**
     * Valor desconto condicionado
     */
    @Column(name = "valor_desconto_cond", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorDescontoCond = BigDecimal.ZERO;

    // ============================================
    // Dados do Tomador (Cliente)
    // Integração com tabela CTP000
    // ============================================

    /**
     * CNPJ/CPF do tomador
     */
    @Size(max = 20, message = "CNPJ/CPF do tomador deve ter no máximo 20 caracteres")
    @Column(name = "tomador_cnpj_cpf", length = 20)
    private String tomadorCnpjCpf;

    /**
     * Nome/Razão Social do tomador
     */
    @Size(max = 200, message = "Nome do tomador deve ter no máximo 200 caracteres")
    @Column(name = "tomador_nome", length = 200)
    private String tomadorNome;

    /**
     * Inscrição Municipal do tomador
     */
    @Size(max = 20, message = "IM tomador deve ter no máximo 20 caracteres")
    @Column(name = "tomador_inscricao_municipal", length = 20)
    private String tomadorInscricaoMunicipal;

    /**
     * Inscrição Estadual do tomador
     */
    @Size(max = 20, message = "IE tomador deve ter no máximo 20 caracteres")
    @Column(name = "tomador_inscricao_estadual", length = 20)
    private String tomadorInscricaoEstadual;

    /**
     * Endereço do tomador - Logradouro (integração com CTP002)
     */
    @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
    @Column(name = "tomador_endereco", length = 200)
    private String tomadorEndereco;

    /**
     * Endereço do tomador - Número
     */
    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Column(name = "tomador_numero", length = 20)
    private String tomadorNumero;

    /**
     * Endereço do tomador - Complemento
     */
    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(name = "tomador_complemento", length = 100)
    private String tomadorComplemento;

    /**
     * Endereço do tomador - Bairro
     */
    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Column(name = "tomador_bairro", length = 100)
    private String tomadorBairro;

    /**
     * Endereço do tomador - CEP
     */
    @Size(max = 10, message = "CEP deve ter no máximo 10 caracteres")
    @Column(name = "tomador_cep", length = 10)
    private String tomadorCep;

    /**
     * Endereço do tomador - Cidade (tabela cidade)
     */
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "tomador_cidade", length = 100)
    private String tomadorCidade;

    /**
     * Endereço do tomador - UF
     */
    @Size(max = 2, message = "UF deve ter 2 caracteres")
    @Column(name = "tomador_uf", length = 2)
    private String tomadorUf;

    /**
     * E-mail do tomador
     */
    @Size(max = 100, message = "E-mail deve ter no máximo 100 caracteres")
    @Column(name = "tomador_email", length = 100)
    private String tomadorEmail;

    /**
     * Telefone do tomador
     */
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "tomador_telefone", length = 20)
    private String tomadorTelefone;

    // ============================================
    // Dados do Intermediário (se houver)
    // ============================================

    /**
     * CNPJ/CPF do intermediário
     */
    @Size(max = 20, message = "CNPJ/CPF intermediário deve ter no máximo 20 caracteres")
    @Column(name = "intermediario_cnpj_cpf", length = 20)
    private String intermediarioCnpjCpf;

    /**
     * Nome/Razão Social do intermediário
     */
    @Size(max = 200, message = "Nome intermediário deve ter no máximo 200 caracteres")
    @Column(name = "intermediario_nome", length = 200)
    private String intermediarioNome;

    // ============================================
    // Discriminação dos Serviços
    // ============================================

    /**
     * Discriminação dos serviços (texto livre)
     */
    @Column(name = "discriminacao_servicos", columnDefinition = "TEXT")
    private String discriminacaoServicos;

    /**
     * Código do serviço (LC 116/2003)
     */
    @Size(max = 10, message = "Código serviço deve ter no máximo 10 caracteres")
    @Column(name = "codigo_servico", length = 10)
    private String codigoServico;

    /**
     * Descrição do código de serviço
     */
    @Size(max = 500, message = "Descrição serviço deve ter no máximo 500 caracteres")
    @Column(name = "descricao_servico", length = 500)
    private String descricaoServico;

    /**
     * Código do município IBGE (tabela cidade)
     */
    @Size(max = 10, message = "Código município deve ter no máximo 10 caracteres")
    @Column(name = "codigo_municipio", length = 10)
    private String codigoMunicipio;

    /**
     * Código da obra (se aplicável)
     */
    @Size(max = 30, message = "Código obra deve ter no máximo 30 caracteres")
    @Column(name = "codigo_obra", length = 30)
    private String codigoObra;

    /**
     * Art (Anotação de Responsabilidade Técnica)
     */
    @Size(max = 30, message = "ART deve ter no máximo 30 caracteres")
    @Column(name = "art", length = 30)
    private String art;

    // ============================================
    // Dados do Recife/ISSSinc
    // ============================================

    /**
     * Número do RPS (Recibo Provisório de Serviços)
     */
    @Size(max = 20, message = "Número RPS deve ter no máximo 20 caracteres")
    @Column(name = "numero_rps", length = 20)
    private String numeroRps;

    /**
     * Série do RPS
     */
    @Size(max = 10, message = "Série RPS deve ter no máximo 10 caracteres")
    @Column(name = "serie_rps", length = 10)
    private String serieRps;

    /**
     * Tipo do RPS
     */
    @Size(max = 2, message = "Tipo RPS deve ter no máximo 2 caracteres")
    @Column(name = "tipo_rps", length = 2)
    private String tipoRps;

    /**
     * Data de emissão do RPS
     */
    @Column(name = "data_emissao_rps")
    private LocalDate dataEmissaoRps;

    /**
     * Status da transmissão (Retorno da prefeitura)
     */
    @Size(max = 500, message = "Status transmissão deve ter no máximo 500 caracteres")
    @Column(name = "status_transmissao", length = 500)
    private String statusTransmissao;

    /**
     * Data/hora da transmissão
     */
    @Column(name = "data_transmissao")
    private LocalDateTime dataTransmissao;

    /**
     * Link de consulta da NFSe
     */
    @Size(max = 500, message = "Link consulta deve ter no máximo 500 caracteres")
    @Column(name = "link_consulta", length = 500)
    private String linkConsulta;

    /**
     * XML da NFSe (armazenado em base64 ou texto)
     */
    @Column(name = "xml_nfse", columnDefinition = "LONGTEXT")
    private String xmlNfse;

    /**
     * Chave da NFSe
     */
    @Size(max = 50, message = "Chave NFSe deve ter no máximo 50 caracteres")
    @Column(name = "chave_nfse", length = 50)
    private String chaveNfse;

    // ============================================
    // Campos de Controle
    // ============================================

    /**
     * Observações internas
     */
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    /**
     * Código da operação (para integração)
     */
    @Size(max = 50, message = "Código operação deve ter no máximo 50 caracteres")
    @Column(name = "codigo_operacao", length = 50)
    private String codigoOperacao;

    /**
     * ID externo (referência do sistema origen)
     */
    @Size(max = 50, message = "ID externo deve ter no máximo 50 caracteres")
    @Column(name = "id_externo", length = 50)
    private String idExterno;

    /**
     * Data de criação do registro
     */
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Data da última atualização
     */
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    /**
     * Usuário que criou o registro
     */
    @Size(max = 100, message = "Usuário criação deve ter no máximo 100 caracteres")
    @Column(name = "usuario_criacao", length = 100)
    private String usuarioCriacao;

    /**
     * Usuário que atualizou o registro
     */
    @Size(max = 100, message = "Usuário atualização deve ter no máximo 100 caracteres")
    @Column(name = "usuario_atualizacao", length = 100)
    private String usuarioAtualizacao;

    /**
     * Lista de itens/detalhes da NFSe
     */
    @OneToMany(mappedBy = "notaCab", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("item ASC")
    @Builder.Default
    private List<NotasDet> itens = new ArrayList<>();

    // ============================================
    // Callbacks JPA
    // ============================================

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (dataEmissao == null) {
            dataEmissao = LocalDate.now();
        }
        if (dataCompetencia == null) {
            dataCompetencia = LocalDate.now();
        }
        // Gerar número da nota se não existir
        if (numeroNota == null || numeroNota.isEmpty()) {
            numeroNota = gerarNumeroNota();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // ============================================
    // Métodos de Negócio
    // ============================================

    /**
     * Adiciona um item ao detalhe da nota
     */
    public void addItem(NotasDet item) {
        itens.add(item);
        item.setNotaCab(this);
        recalcularTotais();
    }

    /**
     * Remove um item do detalhe da nota
     */
    public void removeItem(NotasDet item) {
        itens.remove(item);
        item.setNotaCab(null);
        recalcularTotais();
    }

    /**
     * Recalcula os totais da nota com base nos itens
     */
    public void recalcularTotais() {
        BigDecimal totalServico = BigDecimal.ZERO;
        BigDecimal baseCalc = BigDecimal.ZERO;
        
        for (NotasDet det : itens) {
            if (det.getValorServico() != null) {
                totalServico = totalServico.add(det.getValorServico());
            }
            if (det.getBaseCalculo() != null) {
                baseCalc = baseCalc.add(det.getBaseCalculo());
            }
        }
        
        this.valorServico = totalServico;
        this.baseCalculo = baseCalc;
        
        // Calcula ISS baseado na aliquota
        if (this.aliquotaIss != null && this.aliquotaIss.compareTo(BigDecimal.ZERO) > 0) {
            this.valorIss = this.baseCalculo.multiply(this.aliquotaIss).divide(BigDecimal.valueOf(100));
        }
        
        // Calcula total
        this.valorTotal = this.valorServico.add(this.valorIss);
    }

    /**
     * Gera número da nota (implementação simples)
     */
    private String gerarNumeroNota() {
        // Implementação deve gerar um número único
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * Verifica se a nota pode ser editada
     */
    public boolean isEditable() {
        return "P".equals(this.situacao) || "R".equals(this.situacao);
    }

    /**
     * Verifica se a nota pode ser cancelada
     */
    public boolean isCancelable() {
        return "E".equals(this.situacao);
    }

    /**
     * Retorna a descricao da situacao da nota
     */
    public String getDescricaoSituacao() {
        if (situacao == null) return "Desconhecida";
        switch (situacao) {
            case "P": return "Pendente";
            case "E": return "Emitida/Enviada";
            case "C": return "Cancelada";
            case "R": return "Rejeitada";
            case "B": return "Boleto Gerado";
            default: return situacao;
        }
    }
}
