package com.seprocom.protesto.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Entidade que representa o relacionamento entre Operador e Departamento.
 * 
 * Esta é uma tabela associativa que permite que um operador
 * pertença a múltiplos departamentos.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "cad_operadores_deps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OperadorDepartamento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departamento")
    private Long id;

    /**
     * Operador associado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_operador", nullable = false)
    private Operador operador;

    /**
     * Departamento associado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento", nullable = false)
    private Departamento departamento;
}
