package com.seprocom.protesto.chat.service;

import com.seprocom.protesto.chat.dto.NotasCabDTO;
import com.seprocom.protesto.chat.dto.NotasDetDTO;
import com.seprocom.protesto.chat.entity.NotasCab;
import com.seprocom.protesto.chat.entity.NotasDet;
import com.seprocom.protesto.chat.exception.BusinessException;
import com.seprocom.protesto.chat.repository.NotasCabRepository;
import com.seprocom.protesto.chat.repository.NotasDetRepository;
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
 * Service para gerenciar notas fiscais de serviços (NFSe).
 * 
 * Fornece métodos para CRUD de notas e itens, com integração
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
    // Métodos de Consulta
    // ============================================

    /**
     * Lista todas as notas com paginação
     */
    public Page<NotasCabDTO> listarTodas(Pageable pageable) {
        return notasCabRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Busca nota por ID
     */
    public NotasCabDTO buscarPorId(Long id) {
        NotasCab nota = notasCabRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Nota não encontrada com ID: " + id));
        return toDTOComItens(nota);
    }

    /**
     * Lista notas por período de emissão
     */
    public List<NotasCabDTO> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return notasCabRepository.findByDataEmissaoBetween(dataInicio, dataFim)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Lista notas por período com paginação
     */
    public Page<NotasCabDTO> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return notasCabRepository.findByDataEmissaoBetween(dataInicio, dataFim, pageable)
                .map(this::toDTO);
    }

    /**
     * Lista notas por situação
     */
    public List<NotasCabDTO> buscarPorSituacao(String situacao) {
        return notasCabRepository.findBySituacao(situacao)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Busca nota por número
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
    

    private NotasCab toEntity(NotasCabDTO dto) {
        if (dto == null) return null;
        
        return NotasCab.builder()
                .id(dto.getId())
                .numeroNota(dto.getNumeroNota())
                .serie(dto.getSerie())
                .codigoVerificacao(dto.getCodigoVerificacao())
                .dataEmissao(dto.getDataEmissao())
                .dataCompetencia(dto.getDataCompetencia())
                .situacao(dto.getSituacao())
                .tipo(dto.getTipo())
                .valorServico(dto.getValorServico())
                .valorIss(dto.getValorIss())
                .valorTotal(dto.getValorTotal())
                .aliquotaIss(dto.getAliquotaIss())
                .baseCalculo(dto.getBaseCalculo())
                .valorDeducao(dto.getValorDeducao())
                .valorOutrasRetencoes(dto.getValorOutrasRetencoes())
                .valorDescontoIncond(dto.getValorDescontoIncond())
                .valorDescontoCond(dto.getValorDescontoCond())
                .tomadorCnpjCpf(dto.getTomadorCnpjCpf())
                .tomadorNome(dto.getTomadorNome())
                .tomadorInscricaoMunicipal(dto.getTomadorInscricaoMunicipal())
                .tomadorInscricaoEstadual(dto.getTomadorInscricaoEstadual())
                .tomadorEndereco(dto.getTomadorEndereco())
                .tomadorNumero(dto.getTomadorNumero())
                .tomadorComplemento(dto.getTomadorComplemento())
                .tomadorBairro(dto.getTomadorBairro())
                .tomadorCep(dto.getTomadorCep())
                .tomadorCidade(dto.getTomadorCidade())
                .tomadorUf(dto.getTomadorUf())
                .tomadorEmail(dto.getTomadorEmail())
                .tomadorTelefone(dto.getTomadorTelefone())
                .intermediarioCnpjCpf(dto.getIntermediarioCnpjCpf())
                .intermediarioNome(dto.getIntermediarioNome())
                .discriminacaoServicos(dto.getDiscriminacaoServicos())
                .codigoServico(dto.getCodigoServico())
                .descricaoServico(dto.getDescricaoServico())
                .codigoMunicipio(dto.getCodigoMunicipio())
                .codigoObra(dto.getCodigoObra())
                .art(dto.getArt())
                .numeroRps(dto.getNumeroRps())
                .serieRps(dto.getSerieRps())
                .tipoRps(dto.getTipoRps())
                .dataEmissaoRps(dto.getDataEmissaoRps())
                .statusTransmissao(dto.getStatusTransmissao())
                .dataTransmissao(dto.getDataTransmissao())
                .linkConsulta(dto.getLinkConsulta())
                .xmlNfse(dto.getXmlNfse())
                .chaveNfse(dto.getChaveNfse())
                .observacoes(dto.getObservacoes())
                .codigoOperacao(dto.getCodigoOperacao())
                .idExterno(dto.getIdExterno())
                .build();
    }

    private NotasDet toDetEntity(NotasDetDTO dto) {
        if (dto == null) return null;
        
        return NotasDet.builder()
                .id(dto.getId())
                .item(dto.getItem())
                .codigoServico(dto.getCodigoServico())
                .descricao(dto.getDescricao())
                .quantidade(dto.getQuantidade())
                .unidade(dto.getUnidade())
                .valorUnitario(dto.getValorUnitario())
                .valorServico(dto.getValorServico())
                .baseCalculo(dto.getBaseCalculo())
                .aliquotaIss(dto.getAliquotaIss())
                .valorIss(dto.getValorIss())
                .valorDeducao(dto.getValorDeducao())
                .valorDescontoIncond(dto.getValorDescontoIncond())
                .valorOutrasRetencoes(dto.getValorOutrasRetencoes())
                .codigoMunicipio(dto.getCodigoMunicipio())
                .codigoIncidencia(dto.getCodigoIncidencia())
                .codigoPais(dto.getCodigoPais())
                .exigibilidadeIss(dto.getExigibilidadeIss())
                .documentoOrigem(dto.getDocumentoOrigem())
                .tipoDocumentoOrigem(dto.getTipoDocumentoOrigem())
                .observacoes(dto.getObservacoes())
                .build();
    }

    private void atualizarDados(NotasCab nota, NotasCabDTO dto) {
        nota.setTomadorCnpjCpf(dto.getTomadorCnpjCpf());
        nota.setTomadorNome(dto.getTomadorNome());
        nota.setTomadorInscricaoMunicipal(dto.getTomadorInscricaoMunicipal());
        nota.setTomadorInscricaoEstadual(dto.getTomadorInscricaoEstadual());
        nota.setTomadorEndereco(dto.getTomadorEndereco());
        nota.setTomadorNumero(dto.getTomadorNumero());
        nota.setTomadorComplemento(dto.getTomadorComplemento());
        nota.setTomadorBairro(dto.getTomadorBairro());
        nota.setTomadorCep(dto.getTomadorCep());
        nota.setTomadorCidade(dto.getTomadorCidade());
        nota.setTomadorUf(dto.getTomadorUf());
        nota.setTomadorEmail(dto.getTomadorEmail());
        nota.setTomadorTelefone(dto.getTomadorTelefone());
        
        nota.setIntermediarioCnpjCpf(dto.getIntermediarioCnpjCpf());
        nota.setIntermediarioNome(dto.getIntermediarioNome());
        
        nota.setDiscriminacaoServicos(dto.getDiscriminacaoServicos());
        nota.setCodigoServico(dto.getCodigoServico());
        nota.setDescricaoServico(dto.getDescricaoServico());
        nota.setCodigoMunicipio(dto.getCodigoMunicipio());
        nota.setCodigoObra(dto.getCodigoObra());
        nota.setArt(dto.getArt());
        
        nota.setDataCompetencia(dto.getDataCompetencia());
        nota.setAliquotaIss(dto.getAliquotaIss());
        
        nota.setObservacoes(dto.getObservacoes());
    }

    private void atualizarItens(NotasCab nota, List<NotasDetDTO> itensDTO) {
        if (itensDTO == null || itensDTO.isEmpty()) {
            return;
        }
        
        // Remover itens existentes
        notasDetRepository.deleteByNotaCabId(nota.getId());
        
        // Adicionar novos itens
        int seq = 1;
        for (NotasDetDTO dto : itensDTO) {
            NotasDet item = toDetEntity(dto);
            item.setNotaCab(nota);
            item.setItem(seq++);
            item.calcularValorServico();
            item.calcularIss();
            notasDetRepository.save(item);
        }
    }

    private String gerarNumeroNota() {
        // Gera um número baseado no timestamp
        return String.valueOf(System.currentTimeMillis()).substring(5);
    }

    private NotasCabDTO toDTO(NotasCab nota) {
        if (nota == null) return null;
        
        return NotasCabDTO.builder()
                .id(nota.getId())
                .numeroNota(nota.getNumeroNota())
                .serie(nota.getSerie())
                .codigoVerificacao(nota.getCodigoVerificacao())
                .dataEmissao(nota.getDataEmissao())
                .dataCompetencia(nota.getDataCompetencia())
                .situacao(nota.getSituacao())
                .descricaoSituacao(nota.getDescricaoSituacao())
                .tipo(nota.getTipo())
                .valorServico(nota.getValorServico())
                .valorIss(nota.getValorIss())
                .valorTotal(nota.getValorTotal())
                .aliquotaIss(nota.getAliquotaIss())
                .baseCalculo(nota.getBaseCalculo())
                .valorDeducao(nota.getValorDeducao())
                .valorOutrasRetencoes(nota.getValorOutrasRetencoes())
                .valorDescontoIncond(nota.getValorDescontoIncond())
                .valorDescontoCond(nota.getValorDescontoCond())
                .tomadorCnpjCpf(nota.getTomadorCnpjCpf())
                .tomadorNome(nota.getTomadorNome())
                .tomadorInscricaoMunicipal(nota.getTomadorInscricaoMunicipal())
                .tomadorInscricaoEstadual(nota.getTomadorInscricaoEstadual())
                .tomadorEndereco(nota.getTomadorEndereco())
                .tomadorNumero(nota.getTomadorNumero())
                .tomadorComplemento(nota.getTomadorComplemento())
                .tomadorBairro(nota.getTomadorBairro())
                .tomadorCep(nota.getTomadorCep())
                .tomadorCidade(nota.getTomadorCidade())
                .tomadorUf(nota.getTomadorUf())
                .tomadorEmail(nota.getTomadorEmail())
                .tomadorTelefone(nota.getTomadorTelefone())
                .intermediarioCnpjCpf(nota.getIntermediarioCnpjCpf())
                .intermediarioNome(nota.getIntermediarioNome())
                .discriminacaoServicos(nota.getDiscriminacaoServicos())
                .codigoServico(nota.getCodigoServico())
                .descricaoServico(nota.getDescricaoServico())
                .codigoMunicipio(nota.getCodigoMunicipio())
                .codigoObra(nota.getCodigoObra())
                .art(nota.getArt())
                .numeroRps(nota.getNumeroRps())
                .serieRps(nota.getSerieRps())
                .tipoRps(nota.getTipoRps())
                .dataEmissaoRps(nota.getDataEmissaoRps())
                .statusTransmissao(nota.getStatusTransmissao())
                .dataTransmissao(nota.getDataTransmissao())
                .linkConsulta(nota.getLinkConsulta())
                .xmlNfse(nota.getXmlNfse())
                .chaveNfse(nota.getChaveNfse())
                .observacoes(nota.getObservacoes())
                .codigoOperacao(nota.getCodigoOperacao())
                .idExterno(nota.getIdExterno())
                .dataCriacao(nota.getDataCriacao())
                .dataAtualizacao(nota.getDataAtualizacao())
                .usuarioCriacao(nota.getUsuarioCriacao())
                .usuarioAtualizacao(nota.getUsuarioAtualizacao())
                .quantidadeItens(nota.getItens() != null ? nota.getItens().size() : 0)
                .build();
    }

    private NotasCabDTO toDTOComItens(NotasCab nota) {
        NotasCabDTO dto = toDTO(nota);
        if (nota.getItens() != null) {
            dto.setItens(nota.getItens().stream()
                    .map(this::toDetDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private NotasDetDTO toDetDTO(NotasDet item) {
        if (item == null) return null;
        
        return NotasDetDTO.builder()
                .id(item.getId())
                .notaId(item.getNotaCab() != null ? item.getNotaCab().getId() : null)
                .item(item.getItem())
                .codigoServico(item.getCodigoServico())
                .descricao(item.getDescricao())
                .quantidade(item.getQuantidade())
                .unidade(item.getUnidade())
                .valorUnitario(item.getValorUnitario())
                .valorServico(item.getValorServico())
                .baseCalculo(item.getBaseCalculo())
                .aliquotaIss(item.getAliquotaIss())
                .valorIss(item.getValorIss())
                .valorDeducao(item.getValorDeducao())
                .valorDescontoIncond(item.getValorDescontoIncond())
                .valorOutrasRetencoes(item.getValorOutrasRetencoes())
                .codigoMunicipio(item.getCodigoMunicipio())
                .codigoIncidencia(item.getCodigoIncidencia())
                .codigoPais(item.getCodigoPais())
                .exigibilidadeIss(item.getExigibilidadeIss())
                .descricaoExigibilidade(item.getDescricaoExigibilidade())
                .documentoOrigem(item.getDocumentoOrigem())
                .tipoDocumentoOrigem(item.getTipoDocumentoOrigem())
                .observacoes(item.getObservacoes())
                .dataCriacao(item.getDataCriacao())
                .dataAtualizacao(item.getDataAtualizacao())
                .usuarioCriacao(item.getUsuarioCriacao())
                .usuarioAtualizacao(item.getUsuarioAtualizacao())
                .build();
    }
    
    // Removido bloco duplicado de conversões/atualizações (mantidas as primeiras definições acima)
}
}

