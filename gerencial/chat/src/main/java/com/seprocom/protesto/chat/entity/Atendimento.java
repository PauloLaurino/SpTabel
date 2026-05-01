package com.seprocom.protesto.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um atendimento no sistema de chat.
 * 
 * Um atendimento é uma sessão de conversa entre um operador
 * e um contato (devedor), podendo estar vinculado a uma intimação/boleto.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "cad_atendimentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"mensagens", "contato", "operador", "departamento", "intimacoesBoletos"})
@EqualsAndHashCode(of = "id")
public class Atendimento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atendimento")
    private Long id;

    /**
     * Status do atendimento:
     * P = Pendente
     * F = Na Fila
     * A = Em Atendimento
     * E = Encerrado
     * T = Transferido
     */
    @Size(max = 20, message = "Status deve ter no máximo 20 caracteres")
    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "P";

    /**
     * Data de entrada na fila
     */
    @Column(name = "data_entrada")
    private LocalDateTime dataEntrada;

    /**
     * Data de abertura do atendimento
     */
    @Column(name = "data_abertura")
    private LocalDateTime dataAbertura;

    /**
     * Data de fechamento do atendimento
     */
    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    /**
     * Data da última mensagem
     */
    @Column(name = "data_ultima_mensagem")
    private LocalDateTime dataUltimaMensagem;

    /**
     * Departamento responsável pelo atendimento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento")
    private Departamento departamento;

    /**
     * Operador responsável pelo atendimento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_operador")
    private Operador operador;

    /**
     * Contato (devedor) do atendimento
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contato")
    private Contato contato;

    /**
     * Opção digitada pelo contato no menu inicial
     */
    @Size(max = 20, message = "Opção digitada deve ter no máximo 20 caracteres")
    @Column(name = "opcao_digitada", length = 20)
    private String opcaoDigitada;

    /**
     * Observações sobre o atendimento
     */
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    /**
     * Motivo do encerramento
     */
    @Size(max = 255, message = "Motivo deve ter no máximo 255 caracteres")
    @Column(name = "motivo_encerramento", length = 255)
    private String motivoEncerramento;

    /**
     * Avaliação do atendimento (1 a 5)
     */
    @Column(name = "avaliacao")
    private Integer avaliacao;

    /**
     * Comentário da avaliação
     */
    @Column(name = "comentario_avaliacao", columnDefinition = "TEXT")
    private String comentarioAvaliacao;

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
     * Lista de mensagens do atendimento
     */
    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("dataHora ASC")
    @Builder.Default
    private List<Mensagem> mensagens = new ArrayList<>();

    /**
     * Lista de intimações/boletos vinculados ao atendimento
     */
    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AtendimentoIntimacao> intimacoesBoletos = new ArrayList<>();

    /**
     * Callbacks JPA para auditoria
     */
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (dataEntrada == null) {
            dataEntrada = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Adiciona uma mensagem ao atendimento
     */
    public void addMensagem(Mensagem mensagem) {
        mensagens.add(mensagem);
        mensagem.setAtendimento(this);
    }

    /**
     * Remove uma mensagem do atendimento
     */
    public void removeMensagem(Mensagem mensagem) {
        mensagens.remove(mensagem);
        mensagem.setAtendimento(null);
    }

    /**
     * Adiciona uma intimação/boleto ao atendimento
     */
    public void addIntimacaoBoleto(AtendimentoIntimacao intimacao) {
        intimacoesBoletos.add(intimacao);
        intimacao.setAtendimento(this);
    }

    /**
     * Verifica se o atendimento está pendente
     */
    public boolean isPendente() {
        return "P".equals(status);
    }

    /**
     * Verifica se o atendimento está na fila
     */
    public boolean isNaFila() {
        return "F".equals(status);
    }

    /**
     * Verifica se o atendimento está em andamento
     */
    public boolean isEmAtendimento() {
        return "A".equals(status);
    }

    /**
     * Verifica se o atendimento está encerrado
     */
    public boolean isEncerrado() {
        return "E".equals(status);
    }

    /**
     * Verifica se o atendimento está transferido
     */
    public boolean isTransferido() {
        return "T".equals(status);
    }

    /**
     * Coloca o atendimento na fila
     */
    public void colocarNaFila() {
        this.status = "F";
        if (dataEntrada == null) {
            dataEntrada = LocalDateTime.now();
        }
    }

    /**
     * Inicia o atendimento
     */
    public void iniciarAtendimento() {
        this.status = "A";
        this.dataAbertura = LocalDateTime.now();
    }

    /**
     * Encerra o atendimento
     */
    public void encerrarAtendimento(String motivo) {
        this.status = "E";
        this.dataFechamento = LocalDateTime.now();
        this.motivoEncerramento = motivo;
    }

    /**
     * Transfere o atendimento
     */
    public void transferirAtendimento() {
        this.status = "T";
    }

    /**
     * Retorna a descrição do status
     */
    public String getDescricaoStatus() {
        return switch (status) {
            case "P" -> "Pendente";
            case "F" -> "Na Fila";
            case "A" -> "Em Atendimento";
            case "E" -> "Encerrado";
            case "T" -> "Transferido";
            default -> "Desconhecido";
        };
    }

    /**
     * Enum para status do atendimento
     */
    public enum StatusAtendimento {
        PENDENTE("P"),
        NA_FILA("F"),
        EM_ATENDIMENTO("A"),
        ENCERRADO("E"),
        TRANSFERIDO("T"),
        FECHADO("E");

        private final String codigo;

        StatusAtendimento(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }

        public static StatusAtendimento fromCodigo(String codigo) {
            for (StatusAtendimento s : values()) {
                if (s.codigo.equals(codigo)) {
                    return s;
                }
            }
            return PENDENTE;
        }
    }
}
