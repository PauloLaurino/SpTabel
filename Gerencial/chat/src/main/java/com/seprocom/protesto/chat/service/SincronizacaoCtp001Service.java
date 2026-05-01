package com.seprocom.protesto.chat.service;

import com.seprocom.protesto.chat.entity.AtendimentoIntimacao;
import com.seprocom.protesto.chat.repository.AtendimentoIntimacaoRepository;
import com.seprocom.protesto.chat.repository.Ctp001Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço para sincronização de dados entre o chat e a tabela ctp001.
 * 
 * Este serviço é responsável por:
 * - Sincronizar data de intimação (dtintimacao_001) e data de ocorrência (dataocr_001)
 *   quando o PDF da intimação é enviado
 * - Sincronizar data de pagamento (datapag_001) quando o comprovante é recebido
 * - Executar sincronização periódica automaticamente
 * - Permitir sincronização manual
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SincronizacaoCtp001Service {

    private final Ctp001Repository ctp001Repository;
    private final AtendimentoIntimacaoRepository atendimentoIntimacaoRepository;

    /**
     * Sincroniza uma intimação específica com a tabela ctp001.
     * 
     * @param intimacao A intimação a ser sincronizada
     * @return true se a sincronização foi bem sucedida
     */
    @Transactional
    public boolean sincronizarIntimacao(AtendimentoIntimacao intimacao) {
        log.info("Iniciando sincronização para intimação {}: numapo1={}, numapo2={}, controle={}",
                intimacao.getId(), 
                intimacao.getNumapo1_001(), 
                intimacao.getNumapo2_001(), 
                intimacao.getControle_001());

        try {
            // Verifica se o registro existe na ctp001
            int existe = ctp001Repository.existsByChaves(
                    intimacao.getNumapo1_001(),
                    intimacao.getNumapo2_001(),
                    intimacao.getControle_001()
            );

            if (existe == 0) {
                log.warn("Registro não encontrado na ctp001 para as chaves: {}/{}/{}",
                        intimacao.getNumapo1_001(),
                        intimacao.getNumapo2_001(),
                        intimacao.getControle_001());
                return false;
            }

            int registrosAtualizados = 0;

            // Se tem data de intimação e ocorrência, atualiza
            if (intimacao.getDtintimacao_001() != null && intimacao.getDataocr_001() != null) {
                if (intimacao.getDatapag_001() != null) {
                    // Atualiza tudo (intimação + pagamento)
                    registrosAtualizados = ctp001Repository.atualizarIntimacaoEPagamento(
                            intimacao.getNumapo1_001(),
                            intimacao.getNumapo2_001(),
                            intimacao.getControle_001(),
                            intimacao.getDtintimacao_001(),
                            intimacao.getDataocr_001(),
                            intimacao.getDatapag_001()
                    );
                    log.info("Sincronizados intimação e pagamento para ctp001");
                } else {
                    // Atualiza apenas intimação
                    registrosAtualizados = ctp001Repository.atualizarIntimacao(
                            intimacao.getNumapo1_001(),
                            intimacao.getNumapo2_001(),
                            intimacao.getControle_001(),
                            intimacao.getDtintimacao_001(),
                            intimacao.getDataocr_001()
                    );
                    log.info("Sincronizada intimação para ctp001: dtintimacao={}, dataocr={}",
                            intimacao.getDtintimacao_001(), intimacao.getDataocr_001());
                }
            } 
            // Se tem apenas data de pagamento, atualiza
            else if (intimacao.getDatapag_001() != null) {
                registrosAtualizados = ctp001Repository.atualizarPagamento(
                        intimacao.getNumapo1_001(),
                        intimacao.getNumapo2_001(),
                        intimacao.getControle_001(),
                        intimacao.getDatapag_001()
                );
                log.info("Sincronizado pagamento para ctp001: datapag={}", intimacao.getDatapag_001());
            }

            if (registrosAtualizados > 0) {
                // Marca como sincronizado
                intimacao.marcarSincronizado();
                atendimentoIntimacaoRepository.save(intimacao);
                log.info("Sincronização concluída com sucesso para intimacao {}", intimacao.getId());
                return true;
            } else {
                log.warn("Nenhum registro atualizado para intimacao {}", intimacao.getId());
                return false;
            }

        } catch (Exception e) {
            log.error("Erro ao sincronizar intimacao {}: {}", intimacao.getId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Sincroniza todas as intimações pendentes.
     * 
     * @return número de sincronizações bem sucedidas
     */
    @Transactional
    public int sincronizarTodasPendentes() {
        log.info("Iniciando sincronização de todas as intimações pendentes");
        
        List<AtendimentoIntimacao> pendentes = atendimentoIntimacaoRepository.findParaSincronizacaoCtp001();
        log.info("Encontradas {} intimações pendentes de sincronização", pendentes.size());

        int sucesso = 0;
        for (AtendimentoIntimacao intimacao : pendentes) {
            if (sincronizarIntimacao(intimacao)) {
                sucesso++;
            }
        }

        log.info("Sincronização concluída: {}/{} com sucesso", sucesso, pendentes.size());
        return sucesso;
    }

    /**
     * Sincroniza uma intimação específica pelo ID.
     * 
     * @param intimacaoId ID da intimação
     * @return true se a sincronização foi bem sucedida
     */
    @Transactional
    public boolean sincronizarPorId(Long intimacaoId) {
        return atendimentoIntimacaoRepository.findById(intimacaoId)
                .map(this::sincronizarIntimacao)
                .orElse(false);
    }

    /**
     * Tarefa agendada para sincronização automática.
     * Executa a cada 5 minutos.
     */
    @Scheduled(fixedRate = 300000) // 5 minutos
    @Transactional
    public void sincronizacaoAutomatica() {
        log.debug("Executando sincronização automática com ctp001");
        try {
            int sincronizadas = sincronizarTodasPendentes();
            if (sincronizadas > 0) {
                log.info("Sincronização automática: {} registros atualizados às {}", 
                        sincronizadas, LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("Erro na sincronização automática: {}", e.getMessage(), e);
        }
    }

    /**
     * Verifica se existe registro na ctp001 com as chaves informadas.
     * 
     * @param numapo1 Número do apontamento 1
     * @param numapo2 Número do apontamento 2
     * @param controle Controle
     * @return true se existir
     */
    public boolean verificarExistencia(String numapo1, String numapo2, String controle) {
        return ctp001Repository.existsByChaves(numapo1, numapo2, controle) > 0;
    }
}
