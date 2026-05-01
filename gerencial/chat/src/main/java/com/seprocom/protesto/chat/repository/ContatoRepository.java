package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.Contato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Contato.
 * 
 * Fornece métodos de acesso a dados para contatos (devedores).
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {

    /**
     * Busca um contato pelo telefone
     */
    Optional<Contato> findByTelefone(String telefone);

    /**
     * Verifica se existe um contato com o telefone informado
     */
    boolean existsByTelefone(String telefone);

    /**
     * Busca contatos por nome (case insensitive, contendo o texto)
     */
    List<Contato> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca contatos por email
     */
    Optional<Contato> findByEmail(String email);

    /**
     * Busca contatos com atendimentos ativos
     */
    @Query("SELECT DISTINCT c FROM Contato c " +
           "JOIN c.atendimentos a " +
           "WHERE a.status IN ('P', 'F', 'A')")
    List<Contato> findContatosComAtendimentosAtivos();

    /**
     * Busca contatos com paginação e filtro
     */
    @Query("SELECT c FROM Contato c " +
           "WHERE (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "AND (:telefone IS NULL OR c.telefone LIKE CONCAT('%', :telefone, '%'))")
    Page<Contato> findByFiltro(
            @Param("nome") String nome,
            @Param("telefone") String telefone,
            Pageable pageable);

    /**
     * Busca contato com seus atendimentos
     */
    @Query("SELECT c FROM Contato c LEFT JOIN FETCH c.atendimentos WHERE c.id = :id")
    Optional<Contato> findByIdWithAtendimentos(@Param("id") Long id);

    /**
     * Conta o número de contatos cadastrados
     */
    long count();

    /**
     * Busca contatos criados em um período
     */
    @Query("SELECT c FROM Contato c WHERE c.dataCriacao BETWEEN :dataInicio AND :dataFim")
    List<Contato> findByDataCriacaoBetween(
            @Param("dataInicio") java.time.LocalDateTime dataInicio,
            @Param("dataFim") java.time.LocalDateTime dataFim);
}
