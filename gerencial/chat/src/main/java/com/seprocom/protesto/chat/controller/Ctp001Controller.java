package com.seprocom.protesto.chat.controller;

import com.seprocom.protesto.chat.dto.DevedorCtp001DTO;
import com.seprocom.protesto.chat.entity.Ctp001;
import com.seprocom.protesto.chat.entity.Ctp001Id;
import com.seprocom.protesto.chat.repository.Ctp001Repository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller para integração com tabela CTP001 do Sistema de Protesto.
 * Permite buscar devedores por chaves, CPF/CNPJ ou nome.
 */
@RestController
@RequestMapping("/api/ctp001")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CTP001 - Integração Protesto", description = "Endpoints para integração com tabela de protestos")
@SecurityRequirement(name = "bearerAuth")
public class Ctp001Controller {

    private final Ctp001Repository ctp001Repository;

    /**
     * Busca devedor pelas chaves completas (numapo1, numapo2, controle)
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar devedor por chaves", description = "Busca um devedor pelas chaves completas do protesto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Devedor encontrado"),
        @ApiResponse(responseCode = "404", description = "Devedor não encontrado")
    })
    public ResponseEntity<DevedorCtp001DTO> buscarPorChaves(
            @Parameter(description = "Número do apontamento 1") 
            @RequestParam String numapo1_001,
            @Parameter(description = "Número do apontamento 2") 
            @RequestParam String numapo2_001,
            @Parameter(description = "Número de controle") 
            @RequestParam String controle_001) {
        
        log.info("Buscando devedor por chaves: {}-{}-{}", numapo1_001, numapo2_001, controle_001);
        
        Ctp001Id id = new Ctp001Id(numapo1_001, numapo2_001, controle_001);
        Optional<Ctp001> devedorOpt = ctp001Repository.findById(id);
        
        return devedorOpt
                .map(devedor -> ResponseEntity.ok(convertToDTO(devedor)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca devedores por CPF ou CNPJ
     */
    @GetMapping("/cpfCnpj/{cpfCnpj}")
    @Operation(summary = "Buscar devedor por CPF/CNPJ", description = "Busca um devedor pelo CPF ou CNPJ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Devedor encontrado"),
        @ApiResponse(responseCode = "404", description = "Devedor não encontrado")
    })
    public ResponseEntity<DevedorCtp001DTO> buscarPorCpfCnpj(
            @Parameter(description = "CPF ou CNPJ do devedor (apenas números)") 
            @PathVariable String cpfCnpj) {
        
        log.info("Buscando devedor por CPF/CNPJ: {}", cpfCnpj);
        
        List<Ctp001> encontrados = ctp001Repository.findByCpfCnpj(cpfCnpj);
        Optional<Ctp001> devedorOpt = encontrados.stream().findFirst();
        
        return devedorOpt
                .map(devedor -> ResponseEntity.ok(convertToDTO(devedor)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca devedores por nome (busca parcial, case insensitive)
     */
    @GetMapping("/nome")
    @Operation(summary = "Buscar devedores por nome", description = "Busca devedores por nome (busca parcial)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de devedores encontrados")
    })
    public ResponseEntity<List<DevedorCtp001DTO>> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome do devedor") 
            @RequestParam String nome) {
        
        log.info("Buscando devedores por nome: {}", nome);
        
        List<Ctp001> devedores = ctp001Repository.findByNomeDevedor(nome);
        List<DevedorCtp001DTO> dtos = devedores.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    /**
     * Atualiza a data de intimação do devedor
     */
    @PatchMapping("/intimacao")
    @Operation(summary = "Atualizar data de intimação", description = "Registra a data de intimação do protesto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Data de intimação atualizada"),
        @ApiResponse(responseCode = "404", description = "Devedor não encontrado")
    })
    public ResponseEntity<DevedorCtp001DTO> atualizarIntimacao(
            @Parameter(description = "Número do apontamento 1") 
            @RequestParam String numapo1_001,
            @Parameter(description = "Número do apontamento 2") 
            @RequestParam String numapo2_001,
            @Parameter(description = "Número de controle") 
            @RequestParam String controle_001,
            @Parameter(description = "Data da intimação") 
            @RequestParam String dataIntimacao) {
        
        log.info("Atualizando data de intimação: {}-{}-{} -> {}", numapo1_001, numapo2_001, controle_001, dataIntimacao);
        
        Ctp001Id id = new Ctp001Id(numapo1_001, numapo2_001, controle_001);
        Optional<Ctp001> devedorOpt = ctp001Repository.findById(id);
        
        if (devedorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Ctp001 devedor = devedorOpt.get();
        LocalDate dataIntimacaoDate = LocalDate.parse(dataIntimacao);
        // dtintimacao_001 stored as DDMMAAAA, dataocr_001 stored as AAAAMMDD
        java.time.format.DateTimeFormatter dtintFmt = java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy");
        java.time.format.DateTimeFormatter dataocrFmt = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
        String dtintimacaoStr = dataIntimacaoDate.format(dtintFmt);
        String dataocrStr = dataIntimacaoDate.format(dataocrFmt);
        ctp001Repository.atualizarIntimacao(numapo1_001, numapo2_001, controle_001, dtintimacaoStr, dataocrStr);

        devedor.setDtintimacao_001(dtintimacaoStr);
        return ResponseEntity.ok(convertToDTO(devedor));
    }

    /**
     * Atualiza a data de pagamento do devedor
     */
    @PatchMapping("/pagamento")
    @Operation(summary = "Atualizar data de pagamento", description = "Registra a data de pagamento do protesto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Data de pagamento atualizada"),
        @ApiResponse(responseCode = "404", description = "Devedor não encontrado")
    })
    public ResponseEntity<DevedorCtp001DTO> atualizarPagamento(
            @Parameter(description = "Número do apontamento 1") 
            @RequestParam String numapo1_001,
            @Parameter(description = "Número do apontamento 2") 
            @RequestParam String numapo2_001,
            @Parameter(description = "Número de controle") 
            @RequestParam String controle_001,
            @Parameter(description = "Data do pagamento") 
            @RequestParam String dataPagamento) {
        
        log.info("Atualizando data de pagamento: {}-{}-{} -> {}", numapo1_001, numapo2_001, controle_001, dataPagamento);
        
        Ctp001Id id = new Ctp001Id(numapo1_001, numapo2_001, controle_001);
        Optional<Ctp001> devedorOpt = ctp001Repository.findById(id);
        
        if (devedorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Ctp001 devedor = devedorOpt.get();
        LocalDate dataPagamentoDate = LocalDate.parse(dataPagamento);
        ctp001Repository.atualizarPagamento(numapo1_001, numapo2_001, controle_001, dataPagamentoDate);

        devedor.setDatapag_001(dataPagamentoDate);
        return ResponseEntity.ok(convertToDTO(devedor));
    }

    /**
     * Converte entidade Ctp001 para DTO
     */
    private DevedorCtp001DTO convertToDTO(Ctp001 entity) {
        DevedorCtp001DTO dto = new DevedorCtp001DTO();
        dto.setNumapo1_001(entity.getNumapo1_001());
        dto.setNumapo2_001(entity.getNumapo2_001());
        dto.setControle_001(entity.getControle_001());
        dto.setNome_001(entity.getDevedor_001());
        dto.setCpfCnpj_001(entity.getCpfcnpj_001());
        dto.setEndereco_001(entity.getEndereco_001());
        dto.setCidade_001(entity.getCidade_001());
        dto.setUf_001(entity.getUf_001());
        dto.setCep_001(entity.getCep_001());
        dto.setValor_001(entity.getValor_001());
        // dataProtesto: derive from datavenc_001 if available (stored as DDMMAAAA)
        try {
            if (entity.getDatavenc_001() != null && !entity.getDatavenc_001().isBlank()) {
                java.time.format.DateTimeFormatter src = java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy");
                dto.setDataProtesto_001(java.time.LocalDate.parse(entity.getDatavenc_001(), src));
            }
        } catch (Exception ex) {
            // ignore parse issues and leave null
        }
        // dtintimacao and dataocr are stored as strings; convert to LocalDate where possible
        try {
            if (entity.getDtintimacao_001() != null && !entity.getDtintimacao_001().isBlank()) {
                java.time.format.DateTimeFormatter src = java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy");
                dto.setDtintimacao_001(java.time.LocalDate.parse(entity.getDtintimacao_001(), src));
            }
        } catch (Exception ex) {
            // ignore
        }
        try {
            if (entity.getDataocr_001() != null && !entity.getDataocr_001().isBlank()) {
                java.time.format.DateTimeFormatter src = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
                dto.setDataocr_001(java.time.LocalDate.parse(entity.getDataocr_001(), src));
            }
        } catch (Exception ex) {
            // ignore
        }
        dto.setDatapag_001(entity.getDatapag_001());
        
        // Determinar status do protesto
        String status = "PENDENTE";
        if (entity.getDatapag_001() != null) {
            status = "PAGO";
        } else if (entity.getDtintimacao_001() != null) {
            status = "INTIMADO";
        } else if (entity.getDataocr_001() != null) {
            status = "CANCELADO";
        }
        dto.setStatusProtesto(status);
        
        return dto;
    }
}
