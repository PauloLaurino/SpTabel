package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Operador.
 * 
 * Fornece métodos de acesso a dados para operadores/atendentes.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface OperadorRepository extends JpaRepository<Operador, Long> {

    /**
     * Busca operador por nome
     */
    List<Operador> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca operador por email
     */
    Optional<Operador> findByEmail(String email);

    /**
     * Busca operador por telefone
     */
    Optional<Operador> findByTelefone(String telefone);

    /**
     * Busca operador por ID do usuário no sistema principal
     */
    Optional<Operador> findByUsuarioId(Long usuarioId);

    /**
     * Busca operadores por status
     */
    List<Operador> findByStatus(String status);

    /**
     * Busca operadores online
     */
    @Query("SELECT o FROM Operador o WHERE o.status = 'online'")
    List<Operador> findOperadoresOnline();

    /**
     * Busca operadores com seus departamentos
     */
    @Query("SELECT o FROM Operador o LEFT JOIN FETCH o.departamentos WHERE o.id = :id")
    Optional<Operador> findByIdWithDepartamentos(@Param("id") Long id);

    /**
     * Busca operadores por departamento
     */
    @Query("SELECT DISTINCT o FROM Operador o JOIN o.departamentos od WHERE od.departamento.id = :departamentoId")
    List<Operador> findByDepartamentoId(@Param("departamentoId") Long departamentoId);

    /**
     * Busca operadores online por departamento
     */
    @Query("SELECT DISTINCT o FROM Operador o JOIN o.departamentos od WHERE od.departamento.id = :departamentoId AND o.status = 'online'")
    List<Operador> findOnlineByDepartamento(@Param("departamentoId") Long departamentoId);

    /**
     * Conta operadores online
     */
    @Query("SELECT COUNT(o) FROM Operador o WHERE o.status = 'online'")
    long countOperadoresOnline();

    /**
     * Conta operadores por departamento
     */
    @Query("SELECT COUNT(DISTINCT o) FROM Operador o JOIN o.departamentos od WHERE od.departamento.id = :departamentoId")
    long countByDepartamento(@Param("departamentoId") Long departamentoId);

    /**
     * Verifica se existe operador com o email
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe operador com o telefone
     */
    boolean existsByTelefone(String telefone);
}
