package com.seprocom.gerencial.service;

import com.seprocom.gerencial.dto.NotasCabDTO;
import com.seprocom.gerencial.dto.NotasDetDTO;
import com.seprocom.gerencial.entity.NotasCab;
import com.seprocom.gerencial.entity.NotasDet;
import com.seprocom.gerencial.exception.BusinessException;
import com.seprocom.gerencial.repository.NotasCabRepository;
import com.seprocom.gerencial.repository.NotasDetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para gerenciar notas fiscais de servicos (NFSe).
 * 
 * Fornece metodos para CRUD de notas e itens, com integracao
 * aos dados do emitente vindos da tabela parametros.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotasService {

    private final NotasCabRepository notasCabRepository;
    private final NotasDetRepository notasDetRepository;

    // ============================================
    // Metodos de Consulta
    // ============================================

    /**
     * Lista todas as notas com paginacao
     */
    public Page<NotasCabDTO> listarTodas(Pageable pageable) {
        return notasCabRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Busca nota por ID
     */
    public NotasCabDTO buscarPorId(Long id) {
        NotasCab nota = notasCabRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Nota nao encontrada com ID: " + id));
        return toDTOComItens(nota);
    }

    /**
     * Lista notas por periodo de emissao
     */
    public List<NotasCabDTO> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return notasCabRepository.findByDataEmissaoBetween(dataInicio, dataFim)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Lista notas por periodo com paginacao
     */
    public Page<NotasCabDTO> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return notasCabRepository.findByDataEmissaoBetween(dataInicio, dataFim, pageable)
                .map(this::toDTO);
    }

    /**
     * Lista notas por situacao
     */
    public List<NotasCabDTO> buscarPorSituacao(String situacao) {
        return notasCabRepository.findBySituacao(situacao)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Busca nota por numero
     */
    public NotasCabDTO buscarPorNumero(String numeroNota) {
        return notasCabRepository.findByNumeroNota(numeroNota)
                .map(this::toDTOComItens)
                .orElse(null);
    }

    /**
     * Busca notas por tomador (CNPJ/CPF ou nome)
     */
    public List<NotasCabDTO> buscarPorTomador(String termo) {
        return notasCabRepository.buscarPorTomador(termo)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Busca notas com filtro global (pesquisa em varios campos)
     */
    public Page<NotasCabDTO> buscarGlobal(String termo, Pageable pageable) {
        return notasCabRepository.buscarGlobal(termo, pageable)
                .map(this::toDTO);
    }

    /**
     * Lista notas pendentes de emissao
     */
    public List<NotasCabDTO> listarPendentes() {
        return notasCabRepository.findPendentes()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ============================================
    // Metodos de Persistencia
    // ============================================

    /**
     * Cria nova nota
     */
    @Transactional
    public NotasCabDTO criar(NotasCabDTO dto) {
        NotasCab nota = toEntity(dto);
        
        // Gera numero da nota automaticamente
        if (nota.getNumeroNota() == null) {
            Long maxId = notasCabRepository.count();
            nota.setNumeroNota(String.valueOf(maxId + 1));
        }
        
        // Define situacao padrao como pendente
        if (nota.getSituacao() == null) {
            nota.setSituacao("P");
        }
        
        // Define data de emissao como hoje se nao informada
        if (nota.getDataEmissao() == null) {
            nota.setDataEmissao(LocalDate.now());
        }
        
        // Inicializa valores padrao
        if (nota.getValorServico() == null) nota.setValorServico(BigDecimal.ZERO);
        if (nota.getValorIss() == null) nota.setValorIss(BigDecimal.ZERO);
        if (nota.getValorDesc() == null) nota.setValorDesc(BigDecimal.ZERO);
        if (nota.getValorTotal() == null) nota.setValorTotal(BigDecimal.ZERO);
        
        nota = notasCabRepository.save(nota);
        log.info("Nota criada com ID: {}", nota.getId());
        
        return toDTO(nota);
    }

    /**
     * Atualiza nota existente
     */
    @Transactional
    public NotasCabDTO atualizar(Long id, NotasCabDTO dto) {
        NotasCab nota = notasCabRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Nota nao encontrada com ID: " + id));

        // Verifica se pode ser alterada
        if ("E".equals(nota.getSituacao()) || "C".equals(nota.getSituacao())) {
            throw new BusinessException("Nota ja emitida ou cancelada nao pode ser alterada");
        }

        atualizarDados(nota, dto);
        nota = notasCabRepository.save(nota);
        log.info("Nota atualizada com ID: {}", nota.getId());

        return toDTO(nota);
    }

    /**
     * Exclui nota (apenas pendentes)
     */
    @Transactional
    public void excluir(Long id) {
        NotasCab nota = notasCabRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Nota nao encontrada com ID: " + id));

        if (!"P".equals(nota.getSituacao())) {
            throw new BusinessException("Apenas notas pendentes podem ser excluidas");
        }

        // Remover itens primeiro
        notasDetRepository.deleteByNotaCabId(id);

        notasCabRepository.delete(nota);
        log.info("Nota excluida com ID: {}", id);
    }

    // ============================================
    // Metodos de Itens
    // ============================================

    /**
     * Lista itens de uma nota
     */
    public List<NotasDetDTO> listarItensDaNota(Long notaId) {
        NotasCab nota = notasCabRepository.findById(notaId)
                .orElseThrow(() -> new BusinessException("Nota nao encontrada com ID: " + notaId));

        return notasDetRepository.findByNotaCabId(notaId)
                .stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    /**
     * Adiciona item a nota
     */
    @Transactional
    public NotasDetDTO adicionarItem(Long notaId, NotasDetDTO dto) {
        NotasCab nota = notasCabRepository.findById(notaId)
                .orElseThrow(() -> new BusinessException("Nota nao encontrada com ID: " + notaId));

        if (!"P".equals(nota.getSituacao())) {
            throw new BusinessException("Apenas notas pendentes podem ter itens adicionados");
        }

        NotasDet item = toEntityItem(dto);
        item.setNotaCabId(notaId);
        
        // Define numero do item automaticamente
        List<NotasDet> itensExistentes = notasDetRepository.findByNotaCabId(notaId);
        item.setNumeroItem(itensExistentes.size() + 1);
        
        item = notasDetRepository.save(item);
        
        nota.addItem(item);
        notasCabRepository.save(nota);

        log.info("Item adicionado a nota {}: {}", notaId, item.getId());
        return toItemDTO(item);
    }

    /**
     * Atualiza item de nota
     */
    @Transactional
    public NotasDetDTO atualizarItem(Long notaId, Long itemId, NotasDetDTO dto) {
        NotasCab nota = notasCabRepository.findById(notaId)
                .orElseThrow(() -> new BusinessException("Nota nao encontrada com ID: " + notaId));

        NotasDet item = notasDetRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("Item nao encontrado com ID: " + itemId));

        // Atualiza dados do item
        item.setCodigoServico(dto.getCodigoServico());
        item.setDiscriminacao(dto.getDiscriminacao());
        item.setQuantidade(dto.getQuantidade() != null ? dto.getQuantidade() : BigDecimal.ONE);
        item.setValorUnitario(dto.getValorUnitario() != null ? dto.getValorUnitario() : BigDecimal.ZERO);
        
        // Recalcula total
        BigDecimal total = item.getQuantidade().multiply(item.getValorUnitario());
        item.setValorTotal(total);
        
        item.calcularIss();
        notasDetRepository.save(item);

        nota.recalcularTotais();
        notasCabRepository.save(nota);

        log.info("Item atualizado: {}", itemId);
        return toItemDTO(item);
    }

    /**
     * Remove item de nota
     */
    @Transactional
    public void removerItem(Long notaId, Long itemId) {
        NotasCab nota = notasCabRepository.findById(notaId)
                .orElseThrow(() -> new BusinessException("Nota nao encontrada com ID: " + notaId));

        NotasDet item = notasDetRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("Item nao encontrado com ID: " + itemId));

        nota.removeItem(item);
        notasDetRepository.delete(item);

        nota.recalcularTotais();
        notasCabRepository.save(nota);

        log.info("Item removido: {}", itemId);
    }

    // ============================================
    // Metodos de Conversao
    // ============================================

    private NotasCabDTO toDTO(NotasCab nota) {
        if (nota == null) return null;

        return NotasCabDTO.builder()
                .id(nota.getId())
                .numeroNota(nota.getNumeroNota())
                .serie(nota.getSerie())
                .dataEmissao(nota.getDataEmissao())
                .dataCompetencia(nota.getDataCompetencia())
                .dataVencimento(nota.getDataVencimento())
                .situacao(nota.getSituacao())
                .tomadorCnpjCpf(nota.getTomadorCnpjCpf())
                .tomadorNome(nota.getTomadorNome())
                .tomadorEndereco(nota.getTomadorEndereco())
                .tomadorBairro(nota.getTomadorBairro())
                .tomadorCidade(nota.getTomadorCidade())
                .tomadorUf(nota.getTomadorUf())
                .tomadorCep(nota.getTomadorCep())
                .tomadorEmail(nota.getTomadorEmail())
                .tomadorTelefone(nota.getTomadorTelefone())
                .valorServico(nota.getValorServico())
                .valorIss(nota.getValorIss())
                .valorDesc(nota.getValorDesc())
                .valorTotal(nota.getValorTotal())
                .discriminacao(nota.getDiscriminacao())
                .codigoMunicipio(nota.getCodigoMunicipio())
                .codigoServico(nota.getCodigoServico())
                .aliquota(nota.getAliquota())
                .emitenteCnpj(nota.getEmitenteCnpj())
                .emitenteInscricaoMunicipal(nota.getEmitenteInscricaoMunicipal())
                .emitenteRazaoSocial(nota.getEmitenteRazaoSocial())
                .chaveNfse(nota.getChaveNfse())
                .numeroRps(nota.getNumeroRps())
                .linkConsulta(nota.getLinkConsulta())
                .xmlNfse(nota.getXmlNfse())
                .protocolo(nota.getProtocolo())
                .mensagemErro(nota.getMensagemErro())
                .observacoes(nota.getObservacoes())
                .codigoUsu(nota.getCodigoUsu())
                .build();
    }

    private NotasCabDTO toDTOComItens(NotasCab nota) {
        NotasCabDTO dto = toDTO(nota);
        
        List<NotasDet> itens = notasDetRepository.findByNotaCabId(nota.getId());
        List<NotasDetDTO> itemDTOs = itens.stream().map(this::toItemDTO).collect(Collectors.toList());
        
        dto.setItens(itemDTOs);
        return dto;
    }

    private NotasDetDTO toItemDTO(NotasDet item) {
        return NotasDetDTO.builder()
                .id(item.getId())
                .notaCabId(item.getNotaCabId())
                .numeroItem(item.getNumeroItem())
                .codigoServico(item.getCodigoServico())
                .discriminacao(item.getDiscriminacao())
                .quantidade(item.getQuantidade())
                .valorUnitario(item.getValorUnitario())
                .valorTotal(item.getValorTotal())
                .valorDesc(item.getValorDesc())
                .valorIss(item.getValorIss())
                .aliquota(item.getAliquota())
                .tributavel(item.getTributavel())
                .build();
    }

    private NotasCab toEntity(NotasCabDTO dto) {
        if (dto == null) return null;

        return NotasCab.builder()
                .id(dto.getId())
                .numeroNota(dto.getNumeroNota())
                .serie(dto.getSerie() != null ? dto.getSerie() : "001")
                .dataEmissao(dto.getDataEmissao())
                .dataCompetencia(dto.getDataCompetencia())
                .dataVencimento(dto.getDataVencimento())
                .situacao(dto.getSituacao() != null ? dto.getSituacao() : "P")
                .tomadorCnpjCpf(dto.getTomadorCnpjCpf())
                .tomadorNome(dto.getTomadorNome())
                .tomadorEndereco(dto.getTomadorEndereco())
                .tomadorBairro(dto.getTomadorBairro())
                .tomadorCidade(dto.getTomadorCidade())
                .tomadorUf(dto.getTomadorUf())
                .tomadorCep(dto.getTomadorCep())
                .tomadorEmail(dto.getTomadorEmail())
                .tomadorTelefone(dto.getTomadorTelefone())
                .valorServico(dto.getValorServico() != null ? dto.getValorServico() : BigDecimal.ZERO)
                .valorIss(dto.getValorIss() != null ? dto.getValorIss() : BigDecimal.ZERO)
                .valorDesc(dto.getValorDesc() != null ? dto.getValorDesc() : BigDecimal.ZERO)
                .valorTotal(dto.getValorTotal() != null ? dto.getValorTotal() : BigDecimal.ZERO)
                .discriminacao(dto.getDiscriminacao())
                .codigoMunicipio(dto.getCodigoMunicipio())
                .codigoServico(dto.getCodigoServico())
                .aliquota(dto.getAliquota() != null ? dto.getAliquota() : BigDecimal.ZERO)
                .emitenteCnpj(dto.getEmitenteCnpj())
                .emitenteInscricaoMunicipal(dto.getEmitenteInscricaoMunicipal())
                .emitenteRazaoSocial(dto.getEmitenteRazaoSocial())
                .chaveNfse(dto.getChaveNfse())
                .numeroRps(dto.getNumeroRps())
                .linkConsulta(dto.getLinkConsulta())
                .xmlNfse(dto.getXmlNfse())
                .protocolo(dto.getProtocolo())
                .mensagemErro(dto.getMensagemErro())
                .observacoes(dto.getObservacoes())
                .codigoUsu(dto.getCodigoUsu())
                .build();
    }

    private void atualizarDados(NotasCab nota, NotasCabDTO dto) {
        if (dto.getNumeroNota() != null) nota.setNumeroNota(dto.getNumeroNota());
        if (dto.getSerie() != null) nota.setSerie(dto.getSerie());
        if (dto.getDataEmissao() != null) nota.setDataEmissao(dto.getDataEmissao());
        if (dto.getDataCompetencia() != null) nota.setDataCompetencia(dto.getDataCompetencia());
        if (dto.getDataVencimento() != null) nota.setDataVencimento(dto.getDataVencimento());
        
        nota.setTomadorCnpjCpf(dto.getTomadorCnpjCpf());
        nota.setTomadorNome(dto.getTomadorNome());
        nota.setTomadorEndereco(dto.getTomadorEndereco());
        nota.setTomadorBairro(dto.getTomadorBairro());
        nota.setTomadorCidade(dto.getTomadorCidade());
        nota.setTomadorUf(dto.getTomadorUf());
        nota.setTomadorCep(dto.getTomadorCep());
        nota.setTomadorEmail(dto.getTomadorEmail());
        nota.setTomadorTelefone(dto.getTomadorTelefone());
        
        if (dto.getValorServico() != null) nota.setValorServico(dto.getValorServico());
        if (dto.getAliquota() != null) nota.setAliquota(dto.getAliquota());
        
        nota.setDiscriminacao(dto.getDiscriminacao());
        nota.setCodigoMunicipio(dto.getCodigoMunicipio());
        nota.setCodigoServico(dto.getCodigoServico());
        nota.setObservacoes(dto.getObservacoes());
    }

    private NotasDet toEntityItem(NotasDetDTO dto) {
        return NotasDet.builder()
                .id(dto.getId())
                .notaCabId(dto.getNotaCabId())
                .numeroItem(dto.getNumeroItem())
                .codigoServico(dto.getCodigoServico())
                .discriminacao(dto.getDiscriminacao())
                .quantidade(dto.getQuantidade() != null ? dto.getQuantidade() : BigDecimal.ONE)
                .valorUnitario(dto.getValorUnitario() != null ? dto.getValorUnitario() : BigDecimal.ZERO)
                .valorTotal(dto.getValorTotal() != null ? dto.getValorTotal() : BigDecimal.ZERO)
                .valorDesc(dto.getValorDesc() != null ? dto.getValorDesc() : BigDecimal.ZERO)
                .valorIss(dto.getValorIss() != null ? dto.getValorIss() : BigDecimal.ZERO)
                .aliquota(dto.getAliquota() != null ? dto.getAliquota() : BigDecimal.ZERO)
                .tributavel(dto.getTributavel() != null ? dto.getTributavel() : "S")
                .build();
    }
}