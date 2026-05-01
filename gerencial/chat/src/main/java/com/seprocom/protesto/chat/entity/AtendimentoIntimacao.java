package com.seprocom.protesto.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa o vínculo entre um atendimento e uma intimação/boleto.
 * 
 * Esta entidade permite a integração entre o chat e a tabela ctp001 do sistema
 * de protesto, utilizando as chaves: numapo1_001, numapo2_001 e controle_001.
 * 
 * Através deste vínculo, é possível:
 * - Enviar PDF de intimação para o devedor
 * - Registrar data de intimação (dtintimacao_001)
 * - Registrar data de pagamento (datapag_001)
 * - Registrar data de ocorrência (dataocr_001)
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "cad_atendimentos_intimacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"atendimento"})
@EqualsAndHashCode(of = "id")
public class AtendimentoIntimacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_intimacao")
    private Long id;

    /**
     * Atendimento vinculado à intimação
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atendimento", nullable = false)
    private Atendimento atendimento;

    // ========================================
    // Chaves da tabela ctp001 (Sistema de Protesto)
    // Os 3 campos juntos formam 1 chave composta única
    // ========================================

    /**
     * Número do apontamento 1 (chave primária da ctp001)
     * Formato: AAAAMM (ex: 202602 para fevereiro de 2026)
     * Tamanho: char(4)
     */
    @NotNull(message = "numapo1_001 é obrigatório")
    @Size(min = 4, max = 4, message = "numapo1_001 deve ter 4 caracteres (AAAAMM)")
    @Column(name = "numapo1_001", nullable = false, length = 4)
    private String numapo1_001;

    /**
     * Número do apontamento 2 (chave primária da ctp001)
     * Formato: NNNNNNNNNN (ex: 0004567890)
     * Tamanho: char(10)
     */
    @NotNull(message = "numapo2_001 é obrigatório")
    @Size(min = 10, max = 10, message = "numapo2_001 deve ter 10 caracteres")
    @Column(name = "numapo2_001", nullable = false, length = 10)
    private String numapo2_001;

    /**
     * Controle (chave primária da ctp001)
     * Formato: NN (ex: 01)
     * Tamanho: char(2)
     */
    @NotNull(message = "controle_001 é obrigatório")
    @Size(min = 2, max = 2, message = "controle_001 deve ter 2 caracteres")
    @Column(name = "controle_001", nullable = false, length = 2)
    private String controle_001;

    // ========================================
    // Dados da Intimação/Boleto
    // ========================================

    /**
     * Número do título/protesto
     */
    @Size(max = 50, message = "Número do título deve ter no máximo 50 caracteres")
    @Column(name = "numero_titulo", length = 50)
    private String numeroTitulo;

    /**
     * Nome do devedor
     */
    @Size(max = 100, message = "Nome do devedor deve ter no máximo 100 caracteres")
    @Column(name = "nome_devedor", length = 100)
    private String nomeDevedor;

    /**
     * CPF/CNPJ do devedor
     */
    @Size(max = 20, message = "CPF/CNPJ deve ter no máximo 20 caracteres")
    @Column(name = "cpf_cnpj_devedor", length = 20)
    private String cpfCnpjDevedor;

    /**
     * Valor do título
     */
    @Column(name = "valor_titulo", precision = 15, scale = 2)
    private Double valorTitulo;

    /**
     * Data de vencimento do título
     */
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    /**
     * Nome do apresentante
     */
    @Size(max = 100, message = "Nome do apresentante deve ter no máximo 100 caracteres")
    @Column(name = "nome_apresentante", length = 100)
    private String nomeApresentante;

    // ========================================
    // Dados de Intimação (atualização ctp001)
    // ========================================

    /**
     * Data da intimação (dtintimacao_001)
     * Formato: DDMMAAAA
     * Registrado quando o devedor é intimado via WhatsApp
     */
    @Size(max = 8, message = "Data de intimação deve ter 8 caracteres (DDMMAAAA)")
    @Column(name = "dtintimacao_001", length = 8)
    private String dtintimacao_001;

    /**
     * Data de ocorrência (dataocr_001)
     * Formato: AAAAMMDD
     * Registrado quando há uma ocorrência (pagamento, intimação, etc.)
     */
    @Size(max = 8, message = "Data de ocorrência deve ter 8 caracteres (AAAAMMDD)")
    @Column(name = "dataocr_001", length = 8)
    private String dataocr_001;

    /**
     * Data do pagamento (datapag_001)
     * Registrado quando o devedor envia comprovante de pagamento
     */
    @Column(name = "datapag_001")
    private LocalDate datapag_001;

    /**
     * Hora do pagamento
     */
    @Column(name = "hora_pagamento")
    private String horaPagamento;

    /**
     * Valor pago
     */
    @Column(name = "valor_pago", precision = 15, scale = 2)
    private Double valorPago;

    // ========================================
    // Controle de Envio
    // ========================================

    /**
     * Indica se o PDF da intimação foi enviado
     */
    @Column(name = "pdf_enviado")
    @Builder.Default
    private Boolean pdfEnviado = false;

    /**
     * Data/hora do envio do PDF
     */
    @Column(name = "data_envio_pdf")
    private LocalDateTime dataEnvioPdf;

    /**
     * Nome do arquivo PDF enviado
     */
    @Size(max = 255, message = "Nome do arquivo deve ter no máximo 255 caracteres")
    @Column(name = "arquivo_pdf", length = 255)
    private String arquivoPdf;

    /**
     * Indica se o comprovante de pagamento foi recebido
     */
    @Column(name = "comprovante_recebido")
    @Builder.Default
    private Boolean comprovanteRecebido = false;

    /**
     * Data/hora do recebimento do comprovante
     */
    @Column(name = "data_recebimento_comprovante")
    private LocalDateTime dataRecebimentoComprovante;

    /**
     * Nome do arquivo do comprovante
     */
    @Size(max = 255, message = "Nome do arquivo deve ter no máximo 255 caracteres")
    @Column(name = "arquivo_comprovante", length = 255)
    private String arquivoComprovante;

    /**
     * Indica se os dados foram sincronizados com a ctp001
     */
    @Column(name = "sincronizado_ctp001")
    @Builder.Default
    private Boolean sincronizadoCtp001 = false;

    /**
     * Data/hora da última sincronização
     */
    @Column(name = "data_sincronizacao")
    private LocalDateTime dataSincronizacao;

    /**
     * Observações sobre a intimação
     */
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    /**
     * Status da intimação:
     * P = Pendente
     * E = Enviada
     * C = Confirmada (pagamento)
     * X = Cancelada
     */
    @Size(max = 1, message = "Status deve ter 1 caractere")
    @Column(name = "status_intimacao", length = 1)
    @Builder.Default
    private String statusIntimacao = "P";

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
     * Callbacks JPA para auditoria
     */
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // ========================================
    // Métodos de Negócio
    // ========================================

    /**
     * Marca o PDF como enviado
     */
    public void marcarPdfEnviado(String arquivoPdf) {
        this.pdfEnviado = true;
        this.dataEnvioPdf = LocalDateTime.now();
        this.arquivoPdf = arquivoPdf;
        this.statusIntimacao = "E";
        
        // Registra a data de intimação no formato DDMMAAAA
        LocalDate hoje = LocalDate.now();
        this.dtintimacao_001 = String.format("%02d%02d%04d", 
            hoje.getDayOfMonth(), 
            hoje.getMonthValue(), 
            hoje.getYear());
        
        // Registra a data de ocorrência no formato AAAAMMDD
        this.dataocr_001 = String.format("%04d%02d%02d", 
            hoje.getYear(), 
            hoje.getMonthValue(), 
            hoje.getDayOfMonth());
    }

    /**
     * Marca o comprovante como recebido
     */
    public void marcarComprovanteRecebido(String arquivoComprovante, Double valorPago) {
        this.comprovanteRecebido = true;
        this.dataRecebimentoComprovante = LocalDateTime.now();
        this.arquivoComprovante = arquivoComprovante;
        this.valorPago = valorPago;
        this.datapag_001 = LocalDate.now();
        this.horaPagamento = String.format("%02d:%02d", 
            LocalDateTime.now().getHour(), 
            LocalDateTime.now().getMinute());
        this.statusIntimacao = "C";
    }

    /**
     * Marca o comprovante como recebido (sem valor)
     */
    public void marcarComprovanteRecebido(String arquivoComprovante) {
        marcarComprovanteRecebido(arquivoComprovante, null);
    }

    /**
     * Marca como sincronizado com a ctp001
     */
    public void marcarSincronizado() {
        this.sincronizadoCtp001 = true;
        this.dataSincronizacao = LocalDateTime.now();
    }

    /**
     * Verifica se está pendente
     */
    public boolean isPendente() {
        return "P".equals(statusIntimacao);
    }

    /**
     * Verifica se foi enviada
     */
    public boolean isEnviada() {
        return "E".equals(statusIntimacao);
    }

    /**
     * Verifica se foi confirmada (pagamento)
     */
    public boolean isConfirmada() {
        return "C".equals(statusIntimacao);
    }

    /**
     * Verifica se foi cancelada
     */
    public boolean isCancelada() {
        return "X".equals(statusIntimacao);
    }

    /**
     * Retorna a descrição do status
     */
    public String getDescricaoStatus() {
        return switch (statusIntimacao) {
            case "P" -> "Pendente";
            case "E" -> "Enviada";
            case "C" -> "Confirmada";
            case "X" -> "Cancelada";
            default -> "Desconhecido";
        };
    }

    /**
     * Converte dtintimacao_001 (DDMMAAAA) para LocalDate
     */
    public LocalDate getDataIntimacaoAsLocalDate() {
        if (dtintimacao_001 == null || dtintimacao_001.length() != 8) {
            return null;
        }
        try {
            int dia = Integer.parseInt(dtintimacao_001.substring(0, 2));
            int mes = Integer.parseInt(dtintimacao_001.substring(2, 4));
            int ano = Integer.parseInt(dtintimacao_001.substring(4, 8));
            return LocalDate.of(ano, mes, dia);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converte dataocr_001 (AAAAMMDD) para LocalDate
     */
    public LocalDate getDataOcorrenciaAsLocalDate() {
        if (dataocr_001 == null || dataocr_001.length() != 8) {
            return null;
        }
        try {
            int ano = Integer.parseInt(dataocr_001.substring(0, 4));
            int mes = Integer.parseInt(dataocr_001.substring(4, 6));
            int dia = Integer.parseInt(dataocr_001.substring(6, 8));
            return LocalDate.of(ano, mes, dia);
        } catch (Exception e) {
            return null;
        }
    }
}
