package com.seprocom.protesto.chat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um departamento de atendimento.
 * 
 * Os departamentos organizam os atendimentos por área
 * (ex: Financeiro, Suporte, Protesto).
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "cad_departamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"operadores"})
@EqualsAndHashCode(of = "id")
public class Departamento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departamento")
    private Long id;

    /**
     * Descrição/nome do departamento
     */
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 60, message = "Descrição deve ter no máximo 60 caracteres")
    @Column(name = "descricao", length = 60)
    private String descricao;

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
     * Lista de operadores do departamento
     */
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<OperadorDepartamento> operadores = new ArrayList<>();

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
     * Adiciona um operador ao departamento
     */
    public void addOperador(Operador operador) {
        OperadorDepartamento od = new OperadorDepartamento();
        od.setDepartamento(this);
        od.setOperador(operador);
        operadores.add(od);
    }

    /**
     * Remove um operador do departamento
     */
    public void removeOperador(Operador operador) {
        operadores.removeIf(od -> od.getOperador().equals(operador));
    }
}
