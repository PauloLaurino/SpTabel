package com.seprocom.protesto.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidade que representa o log de acesso dos usuários.
 * 
 * Mapeia para a tabela web_usuario_log existente.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "web_usuario_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "Nome_usuario")
    private String nomeUsuario;

    @Column(name = "Formulario_logado")
    private String formularioLogado;

    @Column(name = "Acao")
    private String acao;

    @Column(name = "Data_hora")
    private LocalDateTime dataHora;

    /**
     * Registra um log de acesso
     */
    public static UsuarioLog of(String nomeUsuario, String formulario, String acao) {
        return UsuarioLog.builder()
                .nomeUsuario(nomeUsuario)
                .formularioLogado(formulario)
                .acao(acao)
                .dataHora(LocalDateTime.now())
                .build();
    }
}
