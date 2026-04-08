package com.seprocom.gerencial.repository;

import com.seprocom.gerencial.entity.NotasCab;
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
 * Repository para operacoes na tabela notascab.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface NotasCabRepository extends JpaRepository<NotasCab, Long> {

    Optional<NotasCab> findByNumeroNota(String numeroNota);

    List<NotasCab> findByDataEmissaoBetween(LocalDate dataInicio, LocalDate dataFim);

    Page<NotasCab> findByDataEmissaoBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    List<NotasCab> findBySituacao(String situacao);

    @Query("SELECT n FROM NotasCab n WHERE LOWER(n.tomadorNome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(n.tomadorCnpjCpf) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<NotasCab> buscarPorTomador(@Param("termo") String termo);

    @Query("SELECT n FROM NotasCab n WHERE " +
           "LOWER(n.numeroNota) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(n.tomadorNome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(n.tomadorCnpjCpf) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(n.discriminacao) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Page<NotasCab> buscarGlobal(@Param("termo") String termo, Pageable pageable);

    @Query("SELECT n FROM NotasCab n WHERE n.situacao = 'P' ORDER BY n.dataEmissao DESC")
    List<NotasCab> findPendentes();

    @Query("SELECT n FROM NotasCab n WHERE n.situacao = 'P' ORDER BY n.dataEmissao DESC")
    Page<NotasCab> findPendentes(Pageable pageable);

    long countBySituacao(String situacao);
}