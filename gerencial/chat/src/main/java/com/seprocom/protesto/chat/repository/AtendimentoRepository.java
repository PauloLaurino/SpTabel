package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.Atendimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Atendimento.
 * 
 * Fornece métodos de acesso a dados para atendimentos.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    /**
     * Busca atendimentos por status
     */
    List<Atendimento> findByStatus(String status);

    /**
     * Busca atendimentos por status com paginação
     */
    Page<Atendimento> findByStatus(String status, Pageable pageable);

    /**
     * Busca atendimentos por status em uma lista
     */
    List<Atendimento> findByStatusIn(List<String> statusList);

    /**
     * Busca atendimentos por operador
     */
    List<Atendimento> findByOperadorId(Long operadorId);

    /**
     * Busca atendimentos por operador com paginação
     */
    Page<Atendimento> findByOperadorId(Long operadorId, Pageable pageable);

    /**
     * Busca atendimento por contato e status diferente de
     */
    @Query("SELECT a FROM Atendimento a WHERE a.contato.id = :contatoId AND a.status <> :status")
    Optional<Atendimento> findByContatoIdAndStatusNot(@Param("contatoId") Long contatoId, @Param("status") String status);

    /**
     * Busca atendimentos por contato
     */
    List<Atendimento> findByContatoId(Long contatoId);

    /**
     * Busca atendimentos por departamento
     */
    List<Atendimento> findByDepartamentoId(Long departamentoId);

    /**
     * Busca atendimento ativo de um contato
     */
    @Query("SELECT a FROM Atendimento a WHERE a.contato.id = :contatoId AND a.status IN ('P', 'F', 'A')")
    Optional<Atendimento> findAtendimentoAtivoByContatoId(@Param("contatoId") Long contatoId);

    /**
     * Busca atendimentos na fila de um departamento
     */
    @Query("SELECT a FROM Atendimento a WHERE a.departamento.id = :departamentoId AND a.status = 'F' ORDER BY a.dataEntrada ASC")
    List<Atendimento> findFilaByDepartamento(@Param("departamentoId") Long departamentoId);

    /**
     * Busca atendimentos em andamento de um operador
     */
    @Query("SELECT a FROM Atendimento a WHERE a.operador.id = :operadorId AND a.status = 'A'")
    List<Atendimento> findEmAtendimentoByOperador(@Param("operadorId") Long operadorId);

    /**
     * Conta atendimentos na fila por departamento
     */
    @Query("SELECT COUNT(a) FROM Atendimento a WHERE a.departamento.id = :departamentoId AND a.status = 'F'")
    long countNaFilaByDepartamento(@Param("departamentoId") Long departamentoId);

    /**
     * Conta atendimentos em andamento por operador
     */
    @Query("SELECT COUNT(a) FROM Atendimento a WHERE a.operador.id = :operadorId AND a.status = 'A'")
    long countEmAtendimentoByOperador(@Param("operadorId") Long operadorId);

    /**
     * Busca atendimentos com mensagens
     */
    @Query("SELECT a FROM Atendimento a LEFT JOIN FETCH a.mensagens WHERE a.id = :id")
    Optional<Atendimento> findByIdWithMensagens(@Param("id") Long id);

    /**
     * Busca atendimentos com intimações/boletos
     */
    @Query("SELECT a FROM Atendimento a LEFT JOIN FETCH a.intimacoesBoletos WHERE a.id = :id")
    Optional<Atendimento> findByIdWithIntimacoes(@Param("id") Long id);

    /**
     * Busca atendimentos com todos os relacionamentos
     */
    @Query("SELECT a FROM Atendimento a " +
           "LEFT JOIN FETCH a.mensagens m " +
           "LEFT JOIN FETCH a.intimacoesBoletos i " +
           "LEFT JOIN FETCH a.contato c " +
           "LEFT JOIN FETCH a.operador o " +
           "LEFT JOIN FETCH a.departamento d " +
           "WHERE a.id = :id")
    Optional<Atendimento> findByIdWithAll(@Param("id") Long id);

    /**
     * Busca atendimentos por período
     */
    @Query("SELECT a FROM Atendimento a WHERE a.dataEntrada BETWEEN :dataInicio AND :dataFim")
    List<Atendimento> findByDataEntradaBetween(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca atendimentos com paginação e filtros
     */
    @Query("SELECT a FROM Atendimento a " +
           "WHERE (:status IS NULL OR a.status = :status) " +
           "AND (:operadorId IS NULL OR a.operador.id = :operadorId) " +
           "AND (:departamentoId IS NULL OR a.departamento.id = :departamentoId) " +
           "AND (:contatoId IS NULL OR a.contato.id = :contatoId)")
    Page<Atendimento> findByFiltro(
            @Param("status") String status,
            @Param("operadorId") Long operadorId,
            @Param("departamentoId") Long departamentoId,
            @Param("contatoId") Long contatoId,
            Pageable pageable);

    /**
     * Busca últimos atendimentos de um contato
     */
    @Query("SELECT a FROM Atendimento a WHERE a.contato.id = :contatoId ORDER BY a.dataEntrada DESC")
    List<Atendimento> findUltimosAtendimentosByContato(@Param("contatoId") Long contatoId, Pageable pageable);

    /**
     * Conta atendimentos por status
     */
    @Query("SELECT a.status, COUNT(a) FROM Atendimento a GROUP BY a.status")
    List<Object[]> countByStatus();

    /**
     * Busca atendimentos encerrados com avaliação
     */
    @Query("SELECT a FROM Atendimento a WHERE a.status = 'E' AND a.avaliacao IS NOT NULL")
    List<Atendimento> findEncerradosComAvaliacao();

    /**
     * Média de avaliações por operador
     */
    @Query("SELECT a.operador.id, AVG(a.avaliacao) FROM Atendimento a WHERE a.status = 'E' AND a.avaliacao IS NOT NULL GROUP BY a.operador.id")
    List<Object[]> getMediaAvaliacaoByOperador();

    /**
     * Tempo médio de atendimento por operador
     */
    @Query("SELECT a.operador.id, AVG(TIMESTAMPDIFF(MINUTE, a.dataAbertura, a.dataFechamento)) " +
           "FROM Atendimento a WHERE a.status = 'E' AND a.dataAbertura IS NOT NULL AND a.dataFechamento IS NOT NULL " +
           "GROUP BY a.operador.id")
    List<Object[]> getTempoMedioAtendimentoByOperador();
}
