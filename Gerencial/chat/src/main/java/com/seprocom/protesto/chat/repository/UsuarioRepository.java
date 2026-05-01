package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para a entidade Usuario.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário pelo nome (login)
     */
    Optional<Usuario> findByNome(String nome);

    /**
     * Busca usuário pelo nome ignorando case
     */
    Optional<Usuario> findByNomeIgnoreCase(String nome);

    /**
     * Verifica se existe usuário com o nome
     */
    boolean existsByNome(String nome);
}
