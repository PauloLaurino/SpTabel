package com.seprocom.protesto.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um contato (devedor) no sistema de chat.
 * 
 * Esta entidade armazena as informações de contato dos devedores
 * que serão atendidos via WhatsApp.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "cad_contatos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"atendimentos"})
@EqualsAndHashCode(of = "id")
public class Contato implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contato")
    private Long id;

    /**
     * Número do telefone no formato internacional (ex: 5562999999999)
     */
    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "telefone", length = 20)
    private String telefone;

    /**
     * Nome do contato/devedor
     */
    @Size(max = 80, message = "Nome deve ter no máximo 80 caracteres")
    @Column(name = "nome", length = 80, columnDefinition = "VARCHAR(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nome;

    /**
     * Email do contato
     */
    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(name = "email", length = 100)
    private String email;

    /**
     * URL da foto do contato
     */
    @Size(max = 255, message = "URL da foto deve ter no máximo 255 caracteres")
    @Column(name = "foto", length = 255)
    private String fotoUrl;

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
     * Lista de atendimentos relacionados a este contato
     */
    @OneToMany(mappedBy = "contato", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Atendimento> atendimentos = new ArrayList<>();

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

    /**
     * Adiciona um atendimento ao contato
     */
    public void addAtendimento(Atendimento atendimento) {
        atendimentos.add(atendimento);
        atendimento.setContato(this);
    }

    /**
     * Remove um atendimento do contato
     */
    public void removeAtendimento(Atendimento atendimento) {
        atendimentos.remove(atendimento);
        atendimento.setContato(null);
    }
}
