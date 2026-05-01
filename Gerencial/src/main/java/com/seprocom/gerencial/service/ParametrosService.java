package com.seprocom.gerencial.service;

import com.seprocom.gerencial.dto.ParametrosDTO;
import com.seprocom.gerencial.entity.LogParametros;
import com.seprocom.gerencial.entity.Parametros;
import com.seprocom.gerencial.repository.LogParametrosRepository;
import com.seprocom.gerencial.repository.ParametrosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service para gerenciamento de parâmetros do sistema
 * Inclui auditoria de todas as alterações
 * 
 * @author Seprocom
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParametrosService {

    private final ParametrosRepository parametrosRepository;
    private final LogParametrosRepository logParametrosRepository;

    /**
     * Busca todos os parâmetros do sistema
     */
    public ParametrosDTO buscarParametros() {
        log.info("Buscando parâmetros do sistema");
        
        Parametros parametros = parametrosRepository.findFirstByCodigoPar(1);
        if (parametros == null) {
            log.warn("Parâmetros não encontrados, criando novos...");
            parametros = Parametros.builder()
                .codigoPar(1)
                .build();
            parametros = parametrosRepository.save(parametros);
        }
        
        return toDTO(parametros);
    }

    /**
     * Salva/atualiza os parâmetros com auditoria
     */
    @Transactional
    public ParametrosDTO salvarParametros(ParametrosDTO dto, String usuario, String ipOrigem, String modulo) {
        log.info("Salvando parâmetros, usuário: {}", usuario);
        
        Parametros parametrosAtual = parametrosRepository.findFirstByCodigoPar(1);
        
        // Se não existe, cria novo
        if (parametrosAtual == null) {
            parametrosAtual = Parametros.builder()
                .codigoPar(1)
                .build();
        }
        
        // Registrar mudanças para auditoria
        List<LogParametros> logs = new ArrayList<>();
        
        // Comparar e registrar cada campo
        logs.addAll(compararCampos(parametrosAtual, dto, usuario, ipOrigem, modulo));
        
        // Atualizar entidade com novos valores
        atualizarEntidade(parametrosAtual, dto);
        
        // Salvar parâmetros
        Parametros parametrosSalvo = parametrosRepository.save(parametrosAtual);
        
        // Salvar logs de auditoria
        if (!logs.isEmpty()) {
            logParametrosRepository.saveAll(logs);
            log.info("Registrados {} logs de auditoria", logs.size());
        }
        
        return toDTO(parametrosSalvo);
    }

    /**
     * Busca histórico de alterações dos parâmetros
     */
    public Page<LogParametros> buscarHistorico(Pageable pageable) {
        return logParametrosRepository.findAll(pageable);
    }

    /**
     * Busca histórico por período
     */
    public List<LogParametros> buscarHistoricoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return logParametrosRepository.findByDataAlteracaoBetweenOrderByDataAlteracaoDesc(inicio, fim);
    }

    /**
     * Busca histórico por campo
     */
    public List<LogParametros> buscarHistoricoPorCampo(String campo) {
        return logParametrosRepository.findByNomeCampoOrderByDataAlteracaoDesc(campo);
    }

    /**
     * Compara campos e cria logs de auditoria
     */
    private List<LogParametros> compararCampos(Parametros atual, ParametrosDTO novo, 
            String usuario, String ip, String modulo) {
        
        List<LogParametros> logs = new ArrayList<>();
        
        // Cartório
        if (!Objects.equals(atual.getNomeCartorio(), novo.getNomeCartorio())) {
            logs.add(criarLog(atual.getCodigoPar(), "NOME_CARTORIO", 
                atual.getNomeCartorio(), novo.getNomeCartorio(), 
                usuario, ip, modulo, "Dados do cartório"));
        }
        
        if (!Objects.equals(atual.getCnpjCartorio(), novo.getCnpjCartorio())) {
            logs.add(criarLog(atual.getCodigoPar(), "CNPJ_CARTORIO", 
                atual.getCnpjCartorio(), novo.getCnpjCartorio(), 
                usuario, ip, modulo, "CNPJ do cartório"));
        }
        
        // Endereço
        if (!Objects.equals(atual.getEnderecoCartorio(), novo.getEnderecoCartorio())) {
            logs.add(criarLog(atual.getCodigoPar(), "ENDERECO_CARTORIO", 
                atual.getEnderecoCartorio(), novo.getEnderecoCartorio(), 
                usuario, ip, modulo, "Endereço do cartório"));
        }
        
        if (!Objects.equals(atual.getTelefoneCartorio(), novo.getTelefoneCartorio())) {
            logs.add(criarLog(atual.getCodigoPar(), "TELEFONE_CARTORIO", 
                atual.getTelefoneCartorio(), novo.getTelefoneCartorio(), 
                usuario, ip, modulo, "Telefone"));
        }
        
        if (!Objects.equals(atual.getEmailCartorio(), novo.getEmailCartorio())) {
            logs.add(criarLog(atual.getCodigoPar(), "EMAIL_CARTORIO", 
                atual.getEmailCartorio(), novo.getEmailCartorio(), 
                usuario, ip, modulo, "Email"));
        }
        
        // NFSe - Impostos da Reforma Tributária
        // Comparar CBS
        if (!Objects.equals(atual.getCbsPar(), novo.getCbsPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "CBS_PAR", 
                String.valueOf(atual.getCbsPar()), String.valueOf(novo.getCbsPar()), 
                usuario, ip, modulo, "Alíquota CBS (Reforma Tributária)"));
        }
        
        if (!Objects.equals(atual.getIbsPar(), novo.getIbsPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "IBS_PAR", 
                String.valueOf(atual.getIbsPar()), String.valueOf(novo.getIbsPar()), 
                usuario, ip, modulo, "Alíquota IBS (Reforma Tributária)"));
        }
        
        if (!Objects.equals(atual.getPisPar(), novo.getPisPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "PIS_PAR", 
                String.valueOf(atual.getPisPar()), String.valueOf(novo.getPisPar()), 
                usuario, ip, modulo, "Alíquota PIS"));
        }
        
        if (!Objects.equals(atual.getCofinsPar(), novo.getCofinsPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "COFINS_PAR", 
                String.valueOf(atual.getCofinsPar()), String.valueOf(novo.getCofinsPar()), 
                usuario, ip, modulo, "Alíquota COFINS"));
        }
        
        if (!Objects.equals(atual.getCsllPar(), novo.getCsllPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "CSLL_PAR", 
                String.valueOf(atual.getCsllPar()), String.valueOf(novo.getCsllPar()), 
                usuario, ip, modulo, "Alíquota CSLL"));
        }
        
        if (!Objects.equals(atual.getIrpjPar(), novo.getIrpjPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "IRPJ_PAR", 
                String.valueOf(atual.getIrpjPar()), String.valueOf(novo.getIrpjPar()), 
                usuario, ip, modulo, "Alíquota IRPJ"));
        }
        
        if (!Objects.equals(atual.getCppPar(), novo.getCppPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "CPP_PAR", 
                String.valueOf(atual.getCppPar()), String.valueOf(novo.getCppPar()), 
                usuario, ip, modulo, "Alíquota CPP (Reforma Tributária)"));
        }
        
        // Fase da Reforma
        if (!Objects.equals(atual.getFaseReformaPar(), novo.getFaseReformaPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "FASE_REFORMA_PAR", 
                atual.getFaseReformaPar(), novo.getFaseReformaPar(), 
                usuario, ip, modulo, "Fase da Reforma Tributária"));
        }
        
        // Ativos
        if (!Objects.equals(atual.getCbsAtivoPar(), novo.getCbsAtivoPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "CBS_ATIVO_PAR", 
                String.valueOf(atual.getCbsAtivoPar()), String.valueOf(novo.getCbsAtivoPar()), 
                usuario, ip, modulo, "CBS ativo"));
        }
        
        if (!Objects.equals(atual.getIbsAtivoPar(), novo.getIbsAtivoPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "IBS_ATIVO_PAR", 
                String.valueOf(atual.getIbsAtivoPar()), String.valueOf(novo.getIbsAtivoPar()), 
                usuario, ip, modulo, "IBS ativo"));
        }
        
        if (!Objects.equals(atual.getCppAtivoPar(), novo.getCppAtivoPar())) {
            logs.add(criarLog(atual.getCodigoPar(), "CPP_ATIVO_PAR", 
                String.valueOf(atual.getCppAtivoPar()), String.valueOf(novo.getCppAtivoPar()), 
                usuario, ip, modulo, "CPP ativo"));
        }
        
        return logs;
    }

    /**
     * Cria um registro de log
     */
    private LogParametros criarLog(Integer codigo, String campo, String anterior, String novo,
            String usuario, String ip, String modulo, String observacao) {
        return LogParametros.builder()
            .codigoParametro(codigo)
            .nomeCampo(campo)
            .valorAnterior(anterior)
            .valorNovo(novo)
            .tipoOperacao("UPDATE")
            .usuarioAlteracao(usuario)
            .ipOrigem(ip)
            .moduloOrigem(modulo)
            .observacao(observacao)
            .dataAlteracao(LocalDateTime.now())
            .build();
    }

    /**
     * Atualiza entidade com valores do DTO
     */
    private void atualizarEntidade(Parametros entity, ParametrosDTO dto) {
        // Cartório
        if (dto.getNomeCartorio() != null) entity.setNomeCartorio(dto.getNomeCartorio());
        if (dto.getCnpjCartorio() != null) entity.setCnpjCartorio(dto.getCnpjCartorio());
        if (dto.getEnderecoCartorio() != null) entity.setEnderecoCartorio(dto.getEnderecoCartorio());
        if (dto.getTelefoneCartorio() != null) entity.setTelefoneCartorio(dto.getTelefoneCartorio());
        if (dto.getEmailCartorio() != null) entity.setEmailCartorio(dto.getEmailCartorio());
        
        // NFSe - Impostos da Reforma Tributária
        if (dto.getCbsPar() != null) entity.setCbsPar(dto.getCbsPar());
        if (dto.getIbsPar() != null) entity.setIbsPar(dto.getIbsPar());
        if (dto.getPisPar() != null) entity.setPisPar(dto.getPisPar());
        if (dto.getCofinsPar() != null) entity.setCofinsPar(dto.getCofinsPar());
        if (dto.getCsllPar() != null) entity.setCsllPar(dto.getCsllPar());
        if (dto.getIrpjPar() != null) entity.setIrpjPar(dto.getIrpjPar());
        if (dto.getIrpjAdicPar() != null) entity.setIrpjAdicPar(dto.getIrpjAdicPar());
        if (dto.getCppPar() != null) entity.setCppPar(dto.getCppPar());
        
        // Fase
        if (dto.getFaseReformaPar() != null) entity.setFaseReformaPar(dto.getFaseReformaPar());
        
        // Ativos
        if (dto.getCbsAtivoPar() != null) entity.setCbsAtivoPar(dto.getCbsAtivoPar());
        if (dto.getIbsAtivoPar() != null) entity.setIbsAtivoPar(dto.getIbsAtivoPar());
        if (dto.getCppAtivoPar() != null) entity.setCppAtivoPar(dto.getCppAtivoPar());
        
        // Deduções
        if (dto.getCbsDeduzirPar() != null) entity.setCbsDeduzirPar(dto.getCbsDeduzirPar());
        if (dto.getIbsDeduzirPar() != null) entity.setIbsDeduzirPar(dto.getIbsDeduzirPar());
    }

    /**
     * Converte Entity para DTO
     */
    private ParametrosDTO toDTO(Parametros entity) {
        return ParametrosDTO.builder()
            .codigoPar(entity.getCodigoPar())
            .docPar(entity.getDocPar())
            .nomeTabeliao(entity.getNomeTabeliao())
            .codigoTabeliao(entity.getCodigoTabeliao())
            .codigoCartorio(entity.getCodigoCartorio())
            .nomeCartorio(entity.getNomeCartorio())
            .cnpjCartorio(entity.getCnpjCartorio())
            .inscricaoEstadual(entity.getInscricaoEstadual())
            .enderecoCartorio(entity.getEnderecoCartorio())
            .telefoneCartorio(entity.getTelefoneCartorio())
            .emailCartorio(entity.getEmailCartorio())
            .ativa(entity.getAtiva())
            .dtVersaoFunarpen(entity.getDtVersaoFunarpen())
            .obsFunarpen(entity.getObsFunarpen())
            // Impostos
            .cbsPar(entity.getCbsPar())
            .ibsPar(entity.getIbsPar())
            .pisPar(entity.getPisPar())
            .cofinsPar(entity.getCofinsPar())
            .csllPar(entity.getCsllPar())
            .irpjPar(entity.getIrpjPar())
            .irpjAdicPar(entity.getIrpjAdicPar())
            .cppPar(entity.getCppPar())
            // Fase
            .faseReformaPar(entity.getFaseReformaPar())
            .cbsAtivoPar(entity.getCbsAtivoPar())
            .ibsAtivoPar(entity.getIbsAtivoPar())
            .cppAtivoPar(entity.getCppAtivoPar())
            // Deduções
            .cbsDeduzirPar(entity.getCbsDeduzirPar())
            .ibsDeduzirPar(entity.getIbsDeduzirPar())
            .build();
    }
}