package com.seprocom.gerencial.repository;

import com.seprocom.gerencial.entity.LogParametros;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para Log de Auditoria de Parâmetros
 */
@Repository
public interface LogParametrosRepository extends JpaRepository<LogParametros, Long> {
    
    /**
     * Busca logs por código de parâmetro
     */
    List<LogParametros> findByCodigoParametroOrderByDataAlteracaoDesc(Integer codigoParametro);
    
    /**
     * Busca logs por código de parâmetro com paginação
     */
    Page<LogParametros> findByCodigoParametro(Integer codigoParametro, Pageable pageable);
    
    /**
     * Busca logs por usuário
     */
    List<LogParametros> findByUsuarioAlteracaoOrderByDataAlteracaoDesc(String usuario);
    
    /**
     * Busca logs por período
     */
    List<LogParametros> findByDataAlteracaoBetweenOrderByDataAlteracaoDesc(
        LocalDateTime dataInicio, LocalDateTime dataFim);
    
    /**
     * Busca logs por campo alterado
     */
    List<LogParametros> findByNomeCampoOrderByDataAlteracaoDesc(String nomeCampo);
}