package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.Ctp001;
import com.seprocom.protesto.chat.entity.Ctp001Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para interação com a tabela ctp001.
 * 
 * Tabela principal do sistema de protesto. Chave composta:
 * numapo1_001 + numapo2_001 + controle_001.
 * 
 * Campos atualizáveis pelo chat:
 * - dtintimacao_001: Data de intimação (DDMMAAAA)
 * - dataocr_001: Data de ocorrência (AAAAMMDD)
 * - datapag_001: Data do pagamento
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface Ctp001Repository extends JpaRepository<Ctp001, Ctp001Id> {

    /**
     * Busca registro por chave composta.
     */
    @Query("SELECT c FROM Ctp001 c WHERE c.numapo1_001 = :numapo1 AND c.numapo2_001 = :numapo2 AND c.controle_001 = :controle")
    Optional<Ctp001> findByChaves(
            @Param("numapo1") String numapo1,
            @Param("numapo2") String numapo2,
            @Param("controle") String controle
    );

    /**
     * Busca devedores por CPF/CNPJ.
     */
    @Query("SELECT c FROM Ctp001 c WHERE c.cpfcnpj_001 = :cpfCnpj ORDER BY c.numapo1_001 DESC")
    List<Ctp001> findByCpfCnpj(@Param("cpfCnpj") String cpfCnpj);

    /**
     * Busca devedores por nome (LIKE).
     */
    @Query("SELECT c FROM Ctp001 c WHERE UPPER(c.devedor_001) LIKE UPPER(CONCAT('%', :nome, '%')) ORDER BY c.numapo1_001 DESC")
    List<Ctp001> findByNomeDevedor(@Param("nome") String nome);

    /**
     * Busca devedores por número do título.
     */
    @Query("SELECT c FROM Ctp001 c WHERE c.numtitulo_001 = :numTitulo")
    List<Ctp001> findByNumTitulo(@Param("numTitulo") String numTitulo);

    /**
     * Busca títulos pendentes (sem data de pagamento) de um devedor.
     */
    @Query("SELECT c FROM Ctp001 c WHERE c.cpfcnpj_001 = :cpfCnpj AND c.datapag_001 IS NULL ORDER BY c.numapo1_001 DESC")
    List<Ctp001> findPendentesByCpfCnpj(@Param("cpfCnpj") String cpfCnpj);

    /**
     * Atualiza a data de intimação e data de ocorrência na tabela ctp001.
     * Chamado quando o PDF da intimação é enviado para o devedor.
     */
    @Modifying
    @Query(value = "UPDATE ctp001 SET dtintimacao_001 = :dtintimacao, dataocr_001 = :dataocr " +
            "WHERE numapo1_001 = :numapo1 AND numapo2_001 = :numapo2 AND controle_001 = :controle",
            nativeQuery = true)
    int atualizarIntimacao(
            @Param("numapo1") String numapo1,
            @Param("numapo2") String numapo2,
            @Param("controle") String controle,
            @Param("dtintimacao") String dtintimacao,
            @Param("dataocr") String dataocr
    );

    /**
     * Atualiza a data de pagamento na tabela ctp001.
     * Chamado quando o comprovante de pagamento é recebido.
     */
    @Modifying
    @Query(value = "UPDATE ctp001 SET datapag_001 = :datapag " +
            "WHERE numapo1_001 = :numapo1 AND numapo2_001 = :numapo2 AND controle_001 = :controle",
            nativeQuery = true)
    int atualizarPagamento(
            @Param("numapo1") String numapo1,
            @Param("numapo2") String numapo2,
            @Param("controle") String controle,
            @Param("datapag") LocalDate datapag
    );

    /**
     * Atualiza todos os dados de uma vez (intimação + pagamento).
     */
    @Modifying
    @Query(value = "UPDATE ctp001 SET dtintimacao_001 = :dtintimacao, dataocr_001 = :dataocr, datapag_001 = :datapag " +
            "WHERE numapo1_001 = :numapo1 AND numapo2_001 = :numapo2 AND controle_001 = :controle",
            nativeQuery = true)
    int atualizarIntimacaoEPagamento(
            @Param("numapo1") String numapo1,
            @Param("numapo2") String numapo2,
            @Param("controle") String controle,
            @Param("dtintimacao") String dtintimacao,
            @Param("dataocr") String dataocr,
            @Param("datapag") LocalDate datapag
    );

    /**
     * Verifica se existe registro na ctp001 com as chaves informadas.
     */
    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM ctp001 " +
            "WHERE numapo1_001 = :numapo1 AND numapo2_001 = :numapo2 AND controle_001 = :controle",
            nativeQuery = true)
    int existsByChaves(
            @Param("numapo1") String numapo1,
            @Param("numapo2") String numapo2,
            @Param("controle") String controle
    );
}
