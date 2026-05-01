package com.seprocom.gerencial.controller;

import com.seprocom.gerencial.dto.ParametrosDTO;
import com.seprocom.gerencial.entity.LogParametros;
import com.seprocom.gerencial.service.ParametrosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de Parametros do sistema.
 * Inclui CRUD completo e histórico de alterações.
 * 
 * @author Seprocom
 */
@RestController
@RequestMapping("/api/nfse/parametros")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Parametros", description = "API para gerenciamento de parâmetros do sistema")
public class ParametrosController {

    private final ParametrosService parametrosService;

    /**
     * Busca os parâmetros atuais do sistema
     */
    @GetMapping
    @Operation(summary = "Buscar parâmetros", description = "Retorna os parâmetros atuais do sistema")
    public ResponseEntity<ParametrosDTO> buscarParametros() {
        log.info("Requisição para buscar parâmetros");
        ParametrosDTO parametros = parametrosService.buscarParametros();
        return ResponseEntity.ok(parametros);
    }

    /**
     * Salva/Atualiza os parâmetros do sistema com auditoria
     */
    @PutMapping
    @Operation(summary = "Salvar parâmetros", description = "Salva ou atualiza os parâmetros do sistema com registro de auditoria")
    public ResponseEntity<ParametrosDTO> salvarParametros(
            @RequestBody ParametrosDTO dto,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario,
            @RequestHeader(value = "X-IP-Origem", defaultValue = "localhost") String ipOrigem,
            @RequestHeader(value = "X-Modulo", defaultValue = "NFSe") String modulo) {
        
        log.info("Requisição para salvar parâmetros, usuário: {}", usuario);
        ParametrosDTO parametros = parametrosService.salvarParametros(dto, usuario, ipOrigem, modulo);
        return ResponseEntity.ok(parametros);
    }

    /**
     * Busca histórico de alterações com paginação
     */
    @GetMapping("/historico")
    @Operation(summary = "Buscar histórico", description = "Retorna o histórico de alterações dos parâmetros")
    public ResponseEntity<Page<LogParametros>> buscarHistorico(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(defaultValue = "dataAlteracao") String ordenacao,
            @RequestParam(defaultValue = "DESC") String direcao) {
        
        Sort.Direction direction = "ASC".equalsIgnoreCase(direcao) 
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageable = PageRequest.of(pagina, tamanho, Sort.by(direction, ordenacao));
        
        Page<LogParametros> historico = parametrosService.buscarHistorico(pageable);
        return ResponseEntity.ok(historico);
    }

    /**
     * Busca histórico por período
     */
    @GetMapping("/historico/periodo")
    @Operation(summary = "Buscar histórico por período", description = "Retorna o histórico de alterações em um período específico")
    public ResponseEntity<List<LogParametros>> buscarHistoricoPorPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        
        log.info("Buscando histórico de {} até {}", dataInicio, dataFim);
        List<LogParametros> historico = parametrosService.buscarHistoricoPorPeriodo(
            java.time.LocalDateTime.parse(dataInicio),
            java.time.LocalDateTime.parse(dataFim)
        );
        return ResponseEntity.ok(historico);
    }

    /**
     * Busca histórico por campo específico
     */
    @GetMapping("/historico/campo/{campo}")
    @Operation(summary = "Buscar histórico por campo", description = "Retorna o histórico de alterações de um campo específico")
    public ResponseEntity<List<LogParametros>> buscarHistoricoPorCampo(@PathVariable String campo) {
        log.info("Buscando histórico do campo: {}", campo);
        List<LogParametros> historico = parametrosService.buscarHistoricoPorCampo(campo);
        return ResponseEntity.ok(historico);
    }

    /**
     *health check para a API
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica se a API está funcionando")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}