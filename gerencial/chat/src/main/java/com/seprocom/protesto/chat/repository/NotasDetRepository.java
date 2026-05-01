package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.NotasDet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository para a entidade NotasDet (Detalhes de NFSe).
 * 
 * Fornece métodos de acesso a dados para os itens/detalhes das notas fiscais.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface NotasDetRepository extends JpaRepository<NotasDet, Long> {

    /**
     * Busca todos os itens de uma nota
     */
    List<NotasDet> findByNotaCabId(Long notaCabId);

    /**
     * Busca itens por código de serviço
     */
    List<NotasDet> findByCodigoServico(String codigoServico);

    /**
     * Busca itens por nota e ordenados por item
     */
    List<NotasDet> findByNotaCabIdOrderByItemAsc(Long notaCabId);

    /**
     * Soma o valor dos serviços de uma nota
     */
    @Query("SELECT SUM(d.valorServico) FROM NotasDet d WHERE d.notaCab.id = :notaId")
    BigDecimal somaValorServicoPorNota(@Param("notaId") Long notaId);

    /**
     * Soma a base de cálculo de uma nota
     */
    @Query("SELECT SUM(d.baseCalculo) FROM NotasDet d WHERE d.notaCab.id = :notaId")
    BigDecimal somaBaseCalculoPorNota(@Param("notaId") Long notaId);

    /**
     * Soma o ISS de uma nota
     */
    @Query("SELECT SUM(d.valorIss) FROM NotasDet d WHERE d.notaCab.id = :notaId")
    BigDecimal somaValorIssPorNota(@Param("notaId") Long notaId);

    /**
     * Conta itens de uma nota
     */
    long countByNotaCabId(Long notaCabId);

    /**
     * Busca itens por descrição (contém)
     */
    List<NotasDet> findByDescricaoContainingIgnoreCase(String descricao);

    /**
     * Deleta todos os itens de uma nota
     */
    void deleteByNotaCabId(Long notaCabId);
}


