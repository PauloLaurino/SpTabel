package com.seprocom.protesto.chat.service;

import com.seprocom.protesto.chat.dto.request.MensagemRequest;
import com.seprocom.protesto.chat.dto.request.VincularIntimacaoRequest;
import com.seprocom.protesto.chat.dto.response.AtendimentoResponse;
import com.seprocom.protesto.chat.dto.response.MensagemResponse;
import com.seprocom.protesto.chat.entity.Atendimento;
import com.seprocom.protesto.chat.entity.AtendimentoIntimacao;
import com.seprocom.protesto.chat.entity.Contato;
import com.seprocom.protesto.chat.entity.Mensagem;
import com.seprocom.protesto.chat.entity.Operador;
import com.seprocom.protesto.chat.exception.BusinessException;
import com.seprocom.protesto.chat.repository.AtendimentoIntimacaoRepository;
import com.seprocom.protesto.chat.repository.AtendimentoRepository;
import com.seprocom.protesto.chat.repository.ContatoRepository;
import com.seprocom.protesto.chat.repository.MensagemRepository;
import com.seprocom.protesto.chat.repository.OperadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciamento de atendimentos.
 * 
 * Integra com a tabela ctp001 através de AtendimentoIntimacao.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final AtendimentoIntimacaoRepository atendimentoIntimacaoRepository;
    private final MensagemRepository mensagemRepository;
    private final ContatoRepository contatoRepository;
    private final OperadorRepository operadorRepository;
    private final SincronizacaoCtp001Service sincronizacaoService;

    /**
     * Cria um novo atendimento para um contato.
     *
     * @param contatoId ID do contato
     * @param operadorId ID do operador
     * @return Atendimento criado
     */
    public AtendimentoResponse criarAtendimento(Long contatoId, Long operadorId) {
        log.info("Criando atendimento para contato {} pelo operador {}", contatoId, operadorId);

        Contato contato = contatoRepository.findById(contatoId)
                .orElseThrow(() -> new EntityNotFoundException("Contato não encontrado: " + contatoId));

        Operador operador = operadorRepository.findById(operadorId)
                .orElseThrow(() -> new EntityNotFoundException("Operador não encontrado: " + operadorId));

        // Verifica se já existe atendimento em aberto para o contato
        Optional<Atendimento> atendimentoExistente = atendimentoRepository
                .findByContatoIdAndStatusNot(contatoId, Atendimento.StatusAtendimento.ENCERRADO.getCodigo());

        if (atendimentoExistente.isPresent()) {
            log.warn("Já existe atendimento em aberto para o contato: {}", contatoId);
            return mapToResponse(atendimentoExistente.get());
        }

        Atendimento atendimento = new Atendimento();
        atendimento.setContato(contato);
        atendimento.setOperador(operador);
        atendimento.setStatus(Atendimento.StatusAtendimento.EM_ATENDIMENTO.getCodigo());
        atendimento.setDataEntrada(LocalDateTime.now());
        atendimento.setDataAbertura(LocalDateTime.now());

        atendimento = atendimentoRepository.save(atendimento);
        log.info("Atendimento criado com sucesso: {}", atendimento.getId());

        return mapToResponse(atendimento);
    }

    /**
     * Envia uma mensagem no atendimento.
     *
     * @param atendimentoId ID do atendimento
     * @param request Dados da mensagem
     * @return Mensagem enviada
     */
    public MensagemResponse enviarMensagem(Long atendimentoId, MensagemRequest request) {
        log.info("Enviando mensagem para atendimento {}", atendimentoId);

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado: " + atendimentoId));

        Mensagem mensagem = new Mensagem();
        mensagem.setAtendimento(atendimento);
        mensagem.setTipoMensagem(request.getTipoMensagem());
        mensagem.setMensagem(request.getMensagem());
        mensagem.setEntradaSaida(request.isOperador() ? "E" : "R");
        mensagem.setDataHora(LocalDateTime.now());
        mensagem.setStatus(1);

        mensagem = mensagemRepository.save(mensagem);

        // Atualiza data de última mensagem do atendimento
        atendimento.setDataUltimaMensagem(LocalDateTime.now());
        atendimentoRepository.save(atendimento);

        return mapToMensagemResponse(mensagem);
    }

    /**
     * Vincula uma intimação/boleto ao atendimento.
     *
     * @param atendimentoId ID do atendimento
     * @param request Dados do vínculo
     * @return Atendimento atualizado
     */
    public AtendimentoResponse vincularIntimacao(Long atendimentoId, VincularIntimacaoRequest request) {
        log.info("Vinculando intimação ao atendimento {}: numapo1={}, numapo2={}, controle={}",
                atendimentoId, request.getNumapo1_001(), request.getNumapo2_001(), request.getControle_001());

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado: " + atendimentoId));

        // Verifica se já existe vínculo com esta intimação
        Optional<AtendimentoIntimacao> vinculoExistente = atendimentoIntimacaoRepository
                .findByNumapo1_001AndNumapo2_001AndControle_001(
                        request.getNumapo1_001(),
                        request.getNumapo2_001(),
                        request.getControle_001()
                );

        if (vinculoExistente.isPresent()) {
            throw new BusinessException("Esta intimação já está vinculada a outro atendimento");
        }

        AtendimentoIntimacao vinculo = new AtendimentoIntimacao();
        vinculo.setAtendimento(atendimento);
        vinculo.setNumapo1_001(request.getNumapo1_001());
        vinculo.setNumapo2_001(request.getNumapo2_001());
        vinculo.setControle_001(request.getControle_001());
        vinculo.setStatusIntimacao("P");

        atendimentoIntimacaoRepository.save(vinculo);

        return mapToResponse(atendimento);
    }

    /**
     * Envia PDF de intimação para o devedor.
     *
     * @param atendimentoId ID do atendimento
     * @param vinculoId ID do vínculo com a intimação
     * @param arquivo Arquivo PDF
     * @return Mensagem enviada
     */
    public MensagemResponse enviarPdfIntimacao(Long atendimentoId, Long vinculoId, MultipartFile arquivo) {
        log.info("Enviando PDF de intimação para atendimento {}, vínculo {}", atendimentoId, vinculoId);

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado: " + atendimentoId));

        AtendimentoIntimacao vinculo = atendimentoIntimacaoRepository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + vinculoId));

        if (!vinculo.getAtendimento().getId().equals(atendimentoId)) {
            throw new BusinessException("Vínculo não pertence a este atendimento");
        }

        try {
            // Salva o arquivo
            String nomeArquivo = salvarArquivo(arquivo, "intimacoes");

            // Cria mensagem com o arquivo
            Mensagem mensagem = new Mensagem();
            mensagem.setAtendimento(atendimento);
            mensagem.setTipoMensagem(5); // Anexo
            mensagem.setMensagem("Intimação enviada");
            mensagem.setAnexo(nomeArquivo);
            mensagem.setEntradaSaida("E");
            mensagem.setDataHora(LocalDateTime.now());
            mensagem.setStatus(1);

            mensagem = mensagemRepository.save(mensagem);

            // Marca o vínculo como PDF enviado e atualiza datas na ctp001
            vinculo.marcarPdfEnviado(nomeArquivo);
            atendimentoIntimacaoRepository.save(vinculo);
            
            // Sincroniza automaticamente com a tabela ctp001
            try {
                sincronizacaoService.sincronizarIntimacao(vinculo);
            } catch (Exception e) {
                log.warn("Sincronização automática falhou, será feita posteriormente: {}", e.getMessage());
            }

            // Atualiza atendimento
            atendimento.setDataUltimaMensagem(LocalDateTime.now());
            atendimentoRepository.save(atendimento);

            return mapToMensagemResponse(mensagem);

        } catch (IOException e) {
            log.error("Erro ao salvar arquivo: {}", e.getMessage());
            throw new BusinessException("Erro ao salvar arquivo de intimação");
        }
    }

    /**
     * Registra comprovante de pagamento recebido.
     *
     * @param atendimentoId ID do atendimento
     * @param vinculoId ID do vínculo com a intimação
     * @param arquivo Arquivo do comprovante
     * @return Mensagem enviada
     */
    public MensagemResponse registrarComprovantePagamento(Long atendimentoId, Long vinculoId, MultipartFile arquivo) {
        log.info("Registrando comprovante de pagamento para atendimento {}, vínculo {}", atendimentoId, vinculoId);

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado: " + atendimentoId));

        AtendimentoIntimacao vinculo = atendimentoIntimacaoRepository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + vinculoId));

        if (!vinculo.getAtendimento().getId().equals(atendimentoId)) {
            throw new BusinessException("Vínculo não pertence a este atendimento");
        }

        try {
            // Salva o arquivo
            String nomeArquivo = salvarArquivo(arquivo, "comprovantes");

            // Cria mensagem com o arquivo
            Mensagem mensagem = new Mensagem();
            mensagem.setAtendimento(atendimento);
            mensagem.setTipoMensagem(5); // Anexo
            mensagem.setMensagem("Comprovante de pagamento");
            mensagem.setAnexo(nomeArquivo);
            mensagem.setEntradaSaida("R");
            mensagem.setDataHora(LocalDateTime.now());
            mensagem.setStatus(1);

            mensagem = mensagemRepository.save(mensagem);

            // Marca o vínculo como comprovante recebido e atualiza data de pagamento
            vinculo.marcarComprovanteRecebido(nomeArquivo);
            atendimentoIntimacaoRepository.save(vinculo);
            
            // Sincroniza automaticamente com a tabela ctp001
            try {
                sincronizacaoService.sincronizarIntimacao(vinculo);
            } catch (Exception e) {
                log.warn("Sincronização automática falhou, será feita posteriormente: {}", e.getMessage());
            }

            // Atualiza atendimento
            atendimento.setDataUltimaMensagem(LocalDateTime.now());
            atendimentoRepository.save(atendimento);

            log.info("Comprovante registrado com sucesso. Data de pagamento: {}", vinculo.getDatapag_001());

            return mapToMensagemResponse(mensagem);

        } catch (IOException e) {
            log.error("Erro ao salvar arquivo: {}", e.getMessage());
            throw new BusinessException("Erro ao salvar comprovante de pagamento");
        }
    }

    /**
     * Lista atendimentos por operador.
     *
     * @param operadorId ID do operador
     * @param pageable Paginação
     * @return Página de atendimentos
     */
    @Transactional(readOnly = true)
    public Page<AtendimentoResponse> listarAtendimentosPorOperador(Long operadorId, Pageable pageable) {
        return atendimentoRepository.findByOperadorId(operadorId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Lista atendimentos por status.
     *
     * @param status Status do atendimento
     * @param pageable Paginação
     * @return Página de atendimentos
     */
    @Transactional(readOnly = true)
    public Page<AtendimentoResponse> listarAtendimentosPorStatus(Atendimento.StatusAtendimento status, Pageable pageable) {
        return atendimentoRepository.findByStatus(status.getCodigo(), pageable)
                .map(this::mapToResponse);
    }

    /**
     * Busca atendimento por ID.
     *
     * @param id ID do atendimento
     * @return Atendimento encontrado
     */
    @Transactional(readOnly = true)
    public AtendimentoResponse buscarPorId(Long id) {
        Atendimento atendimento = atendimentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado: " + id));
        return mapToResponse(atendimento);
    }

    /**
     * Lista mensagens de um atendimento.
     *
     * @param atendimentoId ID do atendimento
     * @return Lista de mensagens
     */
    @Transactional(readOnly = true)
    public List<MensagemResponse> listarMensagens(Long atendimentoId) {
        return mensagemRepository.findByAtendimentoIdOrderByDataHoraAsc(atendimentoId)
                .stream()
                .map(this::mapToMensagemResponse)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza status do atendimento.
     *
     * @param atendimentoId ID do atendimento
     * @param status Novo status
     * @return Atendimento atualizado
     */
    public AtendimentoResponse atualizarStatus(Long atendimentoId, Atendimento.StatusAtendimento status) {
        log.info("Atualizando status do atendimento {} para {}", atendimentoId, status);

        Atendimento atendimento = atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new EntityNotFoundException("Atendimento não encontrado: " + atendimentoId));

        atendimento.setStatus(status.getCodigo());

        if (status == Atendimento.StatusAtendimento.ENCERRADO) {
            atendimento.setDataFechamento(LocalDateTime.now());
        }

        atendimento = atendimentoRepository.save(atendimento);

        return mapToResponse(atendimento);
    }

    /**
     * Salva arquivo no sistema de arquivos.
     *
     * @param arquivo Arquivo a ser salvo
     * @param diretorio Diretório de destino
     * @return Nome do arquivo salvo
     */
    private String salvarArquivo(MultipartFile arquivo, String diretorio) throws IOException {
        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String nomeArquivo = UUID.randomUUID().toString() + extensao;

        Path diretorioPath = Paths.get("uploads", diretorio);
        Files.createDirectories(diretorioPath);

        Path arquivoPath = diretorioPath.resolve(nomeArquivo);
        Files.copy(arquivo.getInputStream(), arquivoPath);

        return nomeArquivo;
    }

    /**
     * Converte entidade para DTO de resposta.
     */
    private AtendimentoResponse mapToResponse(Atendimento atendimento) {
        List<AtendimentoIntimacao> vinculos = atendimentoIntimacaoRepository.findByAtendimentoId(atendimento.getId());

        return AtendimentoResponse.builder()
                .id(atendimento.getId())
                .contatoId(atendimento.getContato() != null ? atendimento.getContato().getId() : null)
                .contatoNome(atendimento.getContato() != null ? atendimento.getContato().getNome() : null)
                .contatoTelefone(atendimento.getContato() != null ? atendimento.getContato().getTelefone() : null)
                .operadorId(atendimento.getOperador() != null ? atendimento.getOperador().getId() : null)
                .operadorNome(atendimento.getOperador() != null ? atendimento.getOperador().getNome() : null)
                .departamentoId(null)
                .departamentoNome(null)
                .status(atendimento.getStatus())
                .dataCriacao(atendimento.getDataCriacao())
                .dataUltimaMensagem(atendimento.getDataUltimaMensagem())
                .dataFechamento(atendimento.getDataFechamento())
                .build();
    }

    /**
     * Converte mensagem para DTO de resposta.
     */
    private MensagemResponse mapToMensagemResponse(Mensagem mensagem) {
        return MensagemResponse.builder()
                .id(mensagem.getId())
                .atendimentoId(mensagem.getAtendimento().getId())
                .tipoMensagem(mensagem.getTipoMensagem())
                .mensagem(mensagem.getMensagem())
                .anexo(mensagem.getAnexo())
                .entradaSaida(mensagem.getEntradaSaida())
                .dataHora(mensagem.getDataHora())
                .status(mensagem.getStatus())
                .build();
    }
}
