package com.seprocom.gerencial.controller;

import com.seprocom.gerencial.dto.NotasCabDTO;
import com.seprocom.gerencial.dto.NotasDetDTO;
import com.seprocom.gerencial.dto.NfseResponseDTO;
import com.seprocom.gerencial.entity.NotasCab;
import com.seprocom.gerencial.entity.NotasDet;
import com.seprocom.gerencial.repository.NotasCabRepository;
import com.seprocom.gerencial.repository.NotasDetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para operações de NFSe.
 * 
 * @author Seprocom
 */
@RestController
@RequestMapping("/api/nfse")
@RequiredArgsConstructor
@Slf4j
public class NfseController {

    private final NotasCabRepository notasCabRepository;
    private final NotasDetRepository notasDetRepository;

    /**
     * Lista todas as notas com paginação
     */
    @GetMapping("/notas")
    public ResponseEntity<Page<NotasCabDTO>> listarNotas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(defaultValue = "dataEmissao") String ordenacao,
            @RequestParam(defaultValue = "DESC") String direcao) {
        
        Sort.Direction direction = "ASC".equalsIgnoreCase(direcao) 
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(direction, ordenacao));
        
        Page<NotasCab> notas = notasCabRepository.findAll(pageable);
        Page<NotasCabDTO> dtoPage = notas.map(this::toDTO);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Lista notas pendentes de emissão
     */
    @GetMapping("/pendentes")
    public ResponseEntity<Page<NotasCabDTO>> listarPendentes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "dataEmissao"));
        Page<NotasCab> notas = notasCabRepository.findPendentes(pageable);
        Page<NotasCabDTO> dtoPage = notas.map(this::toDTO);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Busca nota por ID
     */
    @GetMapping("/notas/{id}")
    public ResponseEntity<NotasCabDTO> buscarPorId(@PathVariable Long id) {
        return notasCabRepository.findById(id)
                .map(nota -> ResponseEntity.ok(toDTOComItens(nota)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca notas por período
     */
    @GetMapping("/notas/periodo")
    public ResponseEntity<List<NotasCabDTO>> buscarPorPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        
        List<NotasCab> notas = notasCabRepository.findByDataEmissaoBetween(dataInicio, dataFim);
        List<NotasCabDTO> dtos = notas.stream().map(this::toDTO).collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Busca notas por tomador
     */
    @GetMapping("/notas/tomador")
    public ResponseEntity<List<NotasCabDTO>> buscarPorTomador(@RequestParam String termo) {
        List<NotasCab> notas = notasCabRepository.buscarPorTomador(termo);
        List<NotasCabDTO> dtos = notas.stream().map(this::toDTO).collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Pesquisa global em notas
     */
    @GetMapping("/notas/busca")
    public ResponseEntity<Page<NotasCabDTO>> buscarGlobal(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        
        Pageable pageable = PageRequest.of(pagina, tamanho);
        Page<NotasCab> notas = notasCabRepository.buscarGlobal(termo, pageable);
        Page<NotasCabDTO> dtoPage = notas.map(this::toDTO);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Cria nova nota
     */
    @PostMapping("/notas")
    public ResponseEntity<NotasCabDTO> criarNota(@RequestBody NotasCabDTO dto) {
        NotasCab nota = toEntity(dto);
        nota.setSituacao("P");
        nota.setDataEmissao(LocalDate.now());
        
        NotasCab salva = notasCabRepository.save(nota);
        
        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            for (NotasDetDTO itemDTO : dto.getItens()) {
                NotasDet item = toEntityItem(itemDTO);
                item.setNotaCabId(salva.getId());
                notasDetRepository.save(item);
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(salva));
    }

    /**
     * Atualiza nota existente
     */
    @PutMapping("/notas/{id}")
    public ResponseEntity<NotasCabDTO> atualizarNota(@PathVariable Long id, @RequestBody NotasCabDTO dto) {
        return notasCabRepository.findById(id)
                .map(nota -> {
                    atualizarDados(nota, dto);
                    NotasCab atualizada = notasCabRepository.save(nota);
                    return ResponseEntity.ok(toDTO(atualizada));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Exclui nota
     */
    @DeleteMapping("/notas/{id}")
    public ResponseEntity<Void> excluirNota(@PathVariable Long id) {
        return notasCabRepository.findById(id)
                .map(nota -> {
                    if (!"P".equals(nota.getSituacao())) {
                        return ResponseEntity.badRequest().<Void>build();
                    }
                    notasDetRepository.deleteByNotaCabId(id);
                    notasCabRepository.delete(nota);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lista itens de uma nota
     */
    @GetMapping("/notas/{id}/itens")
    public ResponseEntity<List<NotasDetDTO>> listarItens(@PathVariable Long id) {
        List<NotasDet> itens = notasDetRepository.findByNotaCabId(id);
        List<NotasDetDTO> dtos = itens.stream().map(this::toItemDTO).collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Emite nota fiscal
     */
    @PostMapping("/notas/{id}/emitir")
    public ResponseEntity<NfseResponseDTO> emitirNota(@PathVariable Long id) {
        log.info("Emitindo NFS-e para nota ID: {}", id);
        
        return notasCabRepository.findById(id)
                .map(nota -> {
                    // Simulação de emissão
                    String numeroNfse = String.valueOf(System.currentTimeMillis());
                    nota.setSituacao("E");
                    nota.setChaveNfse(numeroNfse);
                    nota.setNumeroRps("RPS-" + numeroNfse);
                    nota.setLinkConsulta("https://nfse.prefeitura.sp.gov.br/" + numeroNfse);
                    notasCabRepository.save(nota);
                    
                    NfseResponseDTO response = NfseResponseDTO.builder()
                            .sucesso(true)
                            .mensagem("NFS-e emitida com sucesso")
                            .numeroNfse(numeroNfse)
                            .chaveNfse(numeroNfse)
                            .dataEmissao(LocalDate.now())
                            .linkConsulta(nota.getLinkConsulta())
                            .build();
                    
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cancela nota fiscal
     */
    @PostMapping("/notas/{id}/cancelar")
    public ResponseEntity<NfseResponseDTO> cancelarNota(@PathVariable Long id, @RequestBody CancelamentoRequest request) {
        log.info("Cancelando NFS-e da nota ID: {}", id);
        
        return notasCabRepository.findById(id)
                .map(nota -> {
                    if (!"E".equals(nota.getSituacao())) {
                        NfseResponseDTO response = NfseResponseDTO.builder()
                                .sucesso(false)
                                .mensagem("Nota não está emitida")
                                .build();
                        return ResponseEntity.badRequest().body(response);
                    }
                    
                    nota.setSituacao("C");
                    nota.setObservacoes(request.getMotivo());
                    notasCabRepository.save(nota);
                    
                    NfseResponseDTO response = NfseResponseDTO.builder()
                            .sucesso(true)
                            .mensagem("NFS-e cancelada com sucesso")
                            .build();
                    
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Consulta NFS-e
     */
    @GetMapping("/nfse/consultar")
    public ResponseEntity<NfseResponseDTO> consultarNfsePorNumero(
            @RequestParam String numero,
            @RequestParam(required = false) String cnpjTomador) {
        
        // Simulação - em produção chamaria API da Prefeitura
        NfseResponseDTO response = NfseResponseDTO.builder()
                .sucesso(true)
                .mensagem("Consulta realizada com sucesso")
                .numeroNfse(numero)
                .build();
        
        return ResponseEntity.ok(response);
    }

    // Métodos de conversão
    private NotasCabDTO toDTO(NotasCab nota) {
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
            .dtcadastro(nota.getDtcadastro())
            .dtalteracao(nota.getDtalteracao())
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
        return NotasCab.builder()
            .id(dto.getId())
            .numeroNota(dto.getNumeroNota())
            .serie(dto.getSerie())
            .dataEmissao(dto.getDataEmissao())
            .dataCompetencia(dto.getDataCompetencia())
            .dataVencimento(dto.getDataVencimento())
            .situacao(dto.getSituacao())
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

    @lombok.Data
    public static class CancelamentoRequest {
        private String motivo;
    }
}