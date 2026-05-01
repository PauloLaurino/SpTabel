package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Departamento.
 * 
 * Fornece métodos de acesso a dados para departamentos de atendimento.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    /**
     * Busca departamento por descrição
     */
    Optional<Departamento> findByDescricao(String descricao);

    /**
     * Busca departamentos por descrição contendo texto
     */
    List<Departamento> findByDescricaoContainingIgnoreCase(String descricao);

    /**
     * Verifica se existe departamento com a descrição
     */
    boolean existsByDescricao(String descricao);

    /**
     * Busca departamentos com operadores
     */
    @Query("SELECT d FROM Departamento d LEFT JOIN FETCH d.operadores WHERE d.id = :id")
    Optional<Departamento> findByIdWithOperadores(@Param("id") Long id);

    /**
     * Busca todos os departamentos ordenados por descrição
     */
    List<Departamento> findAllByOrderByDescricaoAsc();

    /**
     * Busca departamentos com atendimentos ativos
     */
    @Query("SELECT DISTINCT d FROM Departamento d JOIN d.operadores od JOIN od.operador o JOIN o.atendimentos a WHERE a.status IN ('P', 'F', 'A')")
    List<Departamento> findDepartamentosComAtendimentosAtivos();

    /**
     * Conta operadores por departamento
     */
    @Query("SELECT COUNT(od) FROM OperadorDepartamento od WHERE od.departamento.id = :departamentoId")
    long countOperadoresByDepartamento(@Param("departamentoId") Long departamentoId);

    /**
     * Conta atendimentos ativos por departamento
     */
    @Query("SELECT COUNT(a) FROM Atendimento a WHERE a.departamento.id = :departamentoId AND a.status IN ('P', 'F', 'A')")
    long countAtendimentosAtivosByDepartamento(@Param("departamentoId") Long departamentoId);
}
