package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.AtendimentoIntimacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade AtendimentoIntimacao.
 * 
 * Fornece métodos de acesso a dados para o vínculo entre
 * atendimentos e intimações/boletos da tabela ctp001.
 * 
 * As chaves da ctp001 são:
 * - numapo1_001: char(4) - AAAAMM
 * - numapo2_001: char(10) - NNNNNNNNNN
 * - controle_001: char(2) - NN
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface AtendimentoIntimacaoRepository extends JpaRepository<AtendimentoIntimacao, Long> {

    /**
     * Busca intimações por atendimento
     */
    List<AtendimentoIntimacao> findByAtendimentoId(Long atendimentoId);

    /**
     * Busca intimação por atendimento e chaves da ctp001
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.atendimento.id = :atendimentoId " +
           "AND ai.numapo1_001 = :numapo1 AND ai.numapo2_001 = :numapo2 AND ai.controle_001 = :controle")
    Optional<AtendimentoIntimacao> findByAtendimentoAndChaves(
            @Param("atendimentoId") Long atendimentoId,
            @Param("numapo1") String numapo1,
            @Param("numapo2") String numapo2,
            @Param("controle") String controle);

    /**
     * Busca por chaves da ctp001
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.numapo1_001 = :numapo1 " +
           "AND ai.numapo2_001 = :numapo2 AND ai.controle_001 = :controle")
    Optional<AtendimentoIntimacao> findByChaves(
            @Param("numapo1") String numapo1,
            @Param("numapo2") String numapo2,
            @Param("controle") String controle);

    /**
     * Busca por chaves da ctp001 (método derivado para compatibilidade)
     */
    Optional<AtendimentoIntimacao> findByNumapo1_001AndNumapo2_001AndControle_001(
            String numapo1_001, String numapo2_001, String controle_001);

    /**
     * Verifica se existe vínculo com as chaves
     */
    @Query("SELECT CASE WHEN COUNT(ai) > 0 THEN true ELSE false END FROM AtendimentoIntimacao ai " +
           "WHERE ai.numapo1_001 = :numapo1 AND ai.numapo2_001 = :numapo2 AND ai.controle_001 = :controle")
    boolean existsByChaves(
            @Param("numapo1") String numapo1,
            @Param("numapo2") String numapo2,
            @Param("controle") String controle);

    /**
     * Busca intimações pendentes de envio
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.pdfEnviado = false AND ai.statusIntimacao = 'P'")
    List<AtendimentoIntimacao> findPendentesEnvio();

    /**
     * Busca intimações com comprovante recebido mas não sincronizado
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.comprovanteRecebido = true AND ai.sincronizadoCtp001 = false")
    List<AtendimentoIntimacao> findComprovantesNaoSincronizados();

    /**
     * Busca intimações por status
     */
    List<AtendimentoIntimacao> findByStatusIntimacao(String statusIntimacao);

    /**
     * Busca intimações enviadas (PDF enviado)
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.pdfEnviado = true")
    List<AtendimentoIntimacao> findEnviadas();

    /**
     * Busca intimações com comprovante
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.comprovanteRecebido = true")
    List<AtendimentoIntimacao> findComComprovante();

    /**
     * Busca intimações por CPF/CNPJ do devedor
     */
    List<AtendimentoIntimacao> findByCpfCnpjDevedor(String cpfCnpjDevedor);

    /**
     * Busca intimações por nome do devedor (contendo)
     */
    List<AtendimentoIntimacao> findByNomeDevedorContainingIgnoreCase(String nomeDevedor);

    /**
     * Busca intimações por período de vencimento
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.dataVencimento BETWEEN :dataInicio AND :dataFim")
    List<AtendimentoIntimacao> findByDataVencimentoBetween(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    /**
     * Busca intimações vencidas
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.dataVencimento < :dataAtual AND ai.statusIntimacao NOT IN ('C', 'X')")
    List<AtendimentoIntimacao> findVencidas(@Param("dataAtual") LocalDate dataAtual);

    /**
     * Conta intimações por status
     */
    @Query("SELECT ai.statusIntimacao, COUNT(ai) FROM AtendimentoIntimacao ai GROUP BY ai.statusIntimacao")
    List<Object[]> countByStatusIntimacao();

    /**
     * Atualiza status de sincronização
     */
    @Modifying
    @Query("UPDATE AtendimentoIntimacao ai SET ai.sincronizadoCtp001 = true, ai.dataSincronizacao = CURRENT_TIMESTAMP WHERE ai.id = :id")
    int marcarSincronizado(@Param("id") Long id);

    /**
     * Busca intimações por atendimento com dados completos
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai " +
           "LEFT JOIN FETCH ai.atendimento a " +
           "LEFT JOIN FETCH a.contato c " +
           "WHERE ai.atendimento.id = :atendimentoId")
    List<AtendimentoIntimacao> findByAtendimentoIdWithDetails(@Param("atendimentoId") Long atendimentoId);

    /**
     * Busca última intimação de um atendimento
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE ai.atendimento.id = :atendimentoId ORDER BY ai.dataCriacao DESC LIMIT 1")
    Optional<AtendimentoIntimacao> findUltimaByAtendimento(@Param("atendimentoId") Long atendimentoId);

    /**
     * Conta intimações por atendimento
     */
    long countByAtendimentoId(Long atendimentoId);

    /**
     * Soma valor total das intimações por atendimento
     */
    @Query("SELECT SUM(ai.valorTitulo) FROM AtendimentoIntimacao ai WHERE ai.atendimento.id = :atendimentoId")
    Double sumValorTituloByAtendimento(@Param("atendimentoId") Long atendimentoId);

    /**
     * Soma valor pago por atendimento
     */
    @Query("SELECT SUM(ai.valorPago) FROM AtendimentoIntimacao ai WHERE ai.atendimento.id = :atendimentoId AND ai.comprovanteRecebido = true")
    Double sumValorPagoByAtendimento(@Param("atendimentoId") Long atendimentoId);

    /**
     * Busca intimações para sincronização com ctp001
     */
    @Query("SELECT ai FROM AtendimentoIntimacao ai WHERE " +
           "(ai.pdfEnviado = true AND ai.dtintimacao_001 IS NOT NULL AND ai.sincronizadoCtp001 = false) " +
           "OR (ai.comprovanteRecebido = true AND ai.datapag_001 IS NOT NULL AND ai.sincronizadoCtp001 = false)")
    List<AtendimentoIntimacao> findParaSincronizacaoCtp001();

    /**
     * Busca intimações por número do título
     */
    List<AtendimentoIntimacao> findByNumeroTitulo(String numeroTitulo);

    /**
     * Busca intimações por apresentante
     */
    List<AtendimentoIntimacao> findByNomeApresentanteContainingIgnoreCase(String nomeApresentante);
}
