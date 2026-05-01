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
 * Entidade que representa um operador/atendente no sistema de chat.
 * 
 * Os operadores são responsáveis por atender os devedores
 * e realizar as interações via WhatsApp.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "cad_operadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"atendimentos", "departamentos"})
@EqualsAndHashCode(of = "id")
public class Operador implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operador")
    private Long id;

    /**
     * Nome do operador
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 80, message = "Nome deve ter no máximo 80 caracteres")
    @Column(name = "nome", length = 80)
    private String nome;

    /**
     * Número do telefone do operador
     */
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "telefone", length = 20)
    private String telefone;

    /**
     * Email do operador
     */
    @Email(message = "Email inválido")
    @Size(max = 80, message = "Email deve ter no máximo 80 caracteres")
    @Column(name = "email", length = 80)
    private String email;

    /**
     * ID do usuário no sistema principal (Maker5)
     */
    @Column(name = "usuario")
    private Long usuarioId;

    /**
     * Status do operador (online/offline)
     */
    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "offline";

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
     * Lista de atendimentos do operador
     */
    @OneToMany(mappedBy = "operador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Atendimento> atendimentos = new ArrayList<>();

    /**
     * Lista de departamentos do operador
     */
    @OneToMany(mappedBy = "operador", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<OperadorDepartamento> departamentos = new ArrayList<>();

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
     * Adiciona um departamento ao operador
     */
    public void addDepartamento(Departamento departamento) {
        OperadorDepartamento od = new OperadorDepartamento();
        od.setOperador(this);
        od.setDepartamento(departamento);
        departamentos.add(od);
    }

    /**
     * Remove um departamento do operador
     */
    public void removeDepartamento(Departamento departamento) {
        departamentos.removeIf(od -> od.getDepartamento().equals(departamento));
    }

    /**
     * Verifica se o operador está online
     */
    public boolean isOnline() {
        return "online".equalsIgnoreCase(status);
    }

    /**
     * Marca o operador como online
     */
    public void setOnline() {
        this.status = "online";
    }

    /**
     * Marca o operador como offline
     */
    public void setOffline() {
        this.status = "offline";
    }
}
