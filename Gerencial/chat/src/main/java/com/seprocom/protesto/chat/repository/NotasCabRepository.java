package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.NotasCab;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade NotasCab (Cabeçalho de NFSe).
 * 
 * Fornece métodos de acesso a dados para notas fiscais de serviços.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface NotasCabRepository extends JpaRepository<NotasCab, Long> {

    /**
     * Busca notas por situação
     */
    List<NotasCab> findBySituacao(String situacao);

    /**
     * Busca notas por situação com paginação
     */
    Page<NotasCab> findBySituacao(String situacao, Pageable pageable);

    /**
     * Busca notas por situação em uma lista
     */
    List<NotasCab> findBySituacaoIn(List<String> situacoes);

    /**
     * Busca notas por número
     */
    Optional<NotasCab> findByNumeroNota(String numeroNota);

    /**
     * Busca notas por CNPJ/CPF do tomador
     */
    List<NotasCab> findByTomadorCnpjCpf(String cnpjCpf);

    /**
     * Busca notas por nome do tomador (contém)
     */
    List<NotasCab> findByTomadorNomeContainingIgnoreCase(String nome);

    /**
     * Busca notas por período de emissão
     */
    List<NotasCab> findByDataEmissaoBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca notas por período de emissão com paginação
     */
    Page<NotasCab> findByDataEmissaoBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    /**
     * Busca notas por competência
     */
    List<NotasCab> findByDataCompetenciaBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca notas por competência com paginação
     */
    Page<NotasCab> findByDataCompetenciaBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    /**
     * Busca por código de verificação
     */
    Optional<NotasCab> findByCodigoVerificacao(String codigoVerificacao);

    /**
     * Busca por chave NFSe
     */
    Optional<NotasCab> findByChaveNfse(String chaveNfse);

    /**
     * Busca por ID externo
     */
    Optional<NotasCab> findByIdExterno(String idExterno);

    /**
     * Busca por código de operação
     */
    Optional<NotasCab> findByCodigoOperacao(String codigoOperacao);

    /**
     * Busca notas por tomador (CNPJ/CPF ou nome)
     */
    @Query("SELECT n FROM NotasCab n WHERE " +
           "n.tomadorCnpjCpf LIKE %:termo% OR " +
           "LOWER(n.tomadorNome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<NotasCab> buscarPorTomador(@Param("termo") String termo);

    /**
     * Busca notas por tomador com paginação
     */
    @Query("SELECT n FROM NotasCab n WHERE " +
           "n.tomadorCnpjCpf LIKE %:termo% OR " +
           "LOWER(n.tomadorNome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Page<NotasCab> buscarPorTomador(@Param("termo") String termo, Pageable pageable);

    /**
     * Busca notas com filtro global (pesquisa em vários campos)
     */
    @Query("SELECT n FROM NotasCab n WHERE " +
           "n.numeroNota LIKE %:termo% OR " +
           "n.tomadorCnpjCpf LIKE %:termo% OR " +
           "LOWER(n.tomadorNome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "n.codigoVerificacao LIKE %:termo% OR " +
           "n.chaveNfse LIKE %:termo% OR " +
           "LOWER(n.observacoes) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<NotasCab> buscarGlobal(@Param("termo") String termo);

    /**
     * Busca notas com filtro global com paginação
     */
    @Query("SELECT n FROM NotasCab n WHERE " +
           "n.numeroNota LIKE %:termo% OR " +
           "n.tomadorCnpjCpf LIKE %:termo% OR " +
           "LOWER(n.tomadorNome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "n.codigoVerificacao LIKE %:termo% OR " +
           "n.chaveNfse LIKE %:termo% OR " +
           "LOWER(n.observacoes) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Page<NotasCab> buscarGlobal(@Param("termo") String termo, Pageable pageable);

    /**
     * Conta notas por situação
     */
    long countBySituacao(String situacao);

    /**
     * Soma valor total das notas por situação
     */
    @Query("SELECT SUM(n.valorTotal) FROM NotasCab n WHERE n.situacao = :situacao")
    java.math.BigDecimal somaValorTotalPorSituacao(@Param("situacao") String situacao);

    /**
     * Soma valor total das notas por período
     */
    @Query("SELECT SUM(n.valorTotal) FROM NotasCab n WHERE n.dataEmissao BETWEEN :dataInicio AND :dataFim")
    java.math.BigDecimal somaValorTotalPorPeriodo(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
}

