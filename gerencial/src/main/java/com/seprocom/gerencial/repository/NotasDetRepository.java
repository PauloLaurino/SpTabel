package com.seprocom.gerencial.repository;

import com.seprocom.gerencial.entity.NotasDet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operacoes na tabela notasdet.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface NotasDetRepository extends JpaRepository<NotasDet, Long> {

    List<NotasDet> findByNotaCabId(Long notaCabId);

    void deleteByNotaCabId(Long notaCabId);
}