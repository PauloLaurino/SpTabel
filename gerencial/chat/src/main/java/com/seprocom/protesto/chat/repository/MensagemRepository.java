package com.seprocom.protesto.chat.repository;

import com.seprocom.protesto.chat.entity.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para a entidade Mensagem.
 * 
 * Fornece métodos de acesso a dados para mensagens do chat.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    /**
     * Busca mensagens por atendimento
     */
    List<Mensagem> findByAtendimentoIdOrderByDataHoraAsc(Long atendimentoId);

    /**
     * Busca mensagens por atendimento ordenadas por data de envio (alias)
     */
    default List<Mensagem> findByAtendimentoIdOrderByDataEnvioAsc(Long atendimentoId) {
        return findByAtendimentoIdOrderByDataHoraAsc(atendimentoId);
    }

    /**
     * Busca mensagens por atendimento com paginação
     */
    Page<Mensagem> findByAtendimentoIdOrderByDataHoraDesc(Long atendimentoId, Pageable pageable);

    /**
     * Busca mensagens não lidas de um atendimento
     */
    @Query("SELECT m FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.lida = false ORDER BY m.dataHora ASC")
    List<Mensagem> findMensagensNaoLidasByAtendimento(@Param("atendimentoId") Long atendimentoId);

    /**
     * Conta mensagens não lidas de um atendimento
     */
    @Query("SELECT COUNT(m) FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.lida = false")
    long countMensagensNaoLidasByAtendimento(@Param("atendimentoId") Long atendimentoId);

    /**
     * Busca mensagens recebidas não lidas
     */
    @Query("SELECT m FROM Mensagem m WHERE m.entradaSaida = 'R' AND m.lida = false ORDER BY m.dataHora ASC")
    List<Mensagem> findMensagensRecebidasNaoLidas();

    /**
     * Marca todas as mensagens de um atendimento como lidas
     */
    @Modifying
    @Query("UPDATE Mensagem m SET m.lida = true, m.dataLeitura = :dataLeitura WHERE m.atendimento.id = :atendimentoId AND m.lida = false")
    int marcarTodasComoLidas(@Param("atendimentoId") Long atendimentoId, @Param("dataLeitura") LocalDateTime dataLeitura);

    /**
     * Busca mensagens por tipo
     */
    List<Mensagem> findByAtendimentoIdAndTipoMensagem(Long atendimentoId, Integer tipoMensagem);

    /**
     * Busca mensagens com anexo
     */
    @Query("SELECT m FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.tipoMensagem = 5")
    List<Mensagem> findMensagensComAnexo(@Param("atendimentoId") Long atendimentoId);

    /**
     * Busca mensagens por período
     */
    @Query("SELECT m FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.dataHora BETWEEN :dataInicio AND :dataFim ORDER BY m.dataHora ASC")
    List<Mensagem> findByAtendimentoAndPeriodo(
            @Param("atendimentoId") Long atendimentoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca última mensagem de um atendimento
     */
    @Query("SELECT m FROM Mensagem m WHERE m.atendimento.id = :atendimentoId ORDER BY m.dataHora DESC LIMIT 1")
    Mensagem findUltimaMensagemByAtendimento(@Param("atendimentoId") Long atendimentoId);

    /**
     * Busca mensagens por código (Message ID do WhatsApp)
     */
    Mensagem findByCodigo(String codigo);

    /**
     * Verifica se existe mensagem com o código
     */
    boolean existsByCodigo(String codigo);

    /**
     * Atualiza status da mensagem
     */
    @Modifying
    @Query("UPDATE Mensagem m SET m.status = :status WHERE m.id = :id")
    int atualizarStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * Conta mensagens por atendimento
     */
    long countByAtendimentoId(Long atendimentoId);

    /**
     * Conta mensagens enviadas por atendimento
     */
    @Query("SELECT COUNT(m) FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.entradaSaida = 'E'")
    long countEnviadasByAtendimento(@Param("atendimentoId") Long atendimentoId);

    /**
     * Conta mensagens recebidas por atendimento
     */
    @Query("SELECT COUNT(m) FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.entradaSaida = 'R'")
    long countRecebidasByAtendimento(@Param("atendimentoId") Long atendimentoId);

    /**
     * Busca mensagens por operador
     */
    @Query("SELECT m FROM Mensagem m WHERE m.operadorId = :operadorId ORDER BY m.dataHora DESC")
    List<Mensagem> findByOperadorId(@Param("operadorId") Long operadorId, Pageable pageable);

    /**
     * Busca mensagens contendo texto
     */
    @Query("SELECT m FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND LOWER(m.mensagem) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Mensagem> searchByTexto(@Param("atendimentoId") Long atendimentoId, @Param("texto") String texto);

    /**
     * Busca mensagens de áudio
     */
    @Query("SELECT m FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.tipoMensagem = 2")
    List<Mensagem> findMensagensAudio(@Param("atendimentoId") Long atendimentoId);

    /**
     * Busca mensagens de imagem
     */
    @Query("SELECT m FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.tipoMensagem = 4")
    List<Mensagem> findMensagensImagem(@Param("atendimentoId") Long atendimentoId);

    /**
     * Busca mensagens de vídeo
     */
    @Query("SELECT m FROM Mensagem m WHERE m.atendimento.id = :atendimentoId AND m.tipoMensagem = 3")
    List<Mensagem> findMensagensVideo(@Param("atendimentoId") Long atendimentoId);
}
