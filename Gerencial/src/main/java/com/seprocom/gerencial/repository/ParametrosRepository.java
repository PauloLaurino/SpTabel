package com.seprocom.gerencial.repository;

import com.seprocom.gerencial.entity.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para parâmetros do sistema (tabela parametros)
 */
@Repository
public interface ParametrosRepository extends JpaRepository<Parametros, Long> {
    
    /**
     * Busca parâmetros do sistema (CODIGO_PAR = 1)
     */
    Parametros findFirstByCodigoPar(Integer codigoPar);
}