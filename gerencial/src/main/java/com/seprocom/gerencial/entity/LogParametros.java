package com.seprocom.gerencial.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade para Log de Auditoria de Parâmetros
 * Registra todas as alterações na tabela parametros
 * 
 * @author Seprocom
 */
@Entity
@Table(name = "log_parametros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogParametros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_parametro", nullable = false)
    private Integer codigoParametro;

    @Column(name = "nome_campo", nullable = false, length = 50)
    private String nomeCampo;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_novo", columnDefinition = "TEXT")
    private String valorNovo;

    @Column(name = "tipo_operacao", nullable = false, length = 10)
    private String tipoOperacao; // INSERT, UPDATE, DELETE

    @Column(name = "data_alteracao")
    @Builder.Default
    private LocalDateTime dataAlteracao = LocalDateTime.now();

    @Column(name = "usuario_alteracao", length = 50)
    private String usuarioAlteracao;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @Column(name = "modulo_origem", length = 50)
    private String moduloOrigem;

    @Column(name = "observacao", length = 500)
    private String observacao;
}