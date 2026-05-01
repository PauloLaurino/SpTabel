package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.UsuarioLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para a entidade UsuarioLog.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface UsuarioLogRepository extends JpaRepository<UsuarioLog, Long> {

    /**
     * Busca logs por nome de usuário
     */
    List<UsuarioLog> findByNomeUsuarioOrderByDataHoraDesc(String nomeUsuario);

    /**
     * Busca logs por formulário
     */
    List<UsuarioLog> findByFormularioLogadoOrderByDataHoraDesc(String formulario);

    /**
     * Busca logs por período
     */
    List<UsuarioLog> findByDataHoraBetweenOrderByDataHoraDesc(
            LocalDateTime dataInicio, 
            LocalDateTime dataFim);

    /**
     * Busca logs por usuário e período
     */
    List<UsuarioLog> findByNomeUsuarioAndDataHoraBetweenOrderByDataHoraDesc(
            String nomeUsuario,
            LocalDateTime dataInicio, 
            LocalDateTime dataFim);
}
