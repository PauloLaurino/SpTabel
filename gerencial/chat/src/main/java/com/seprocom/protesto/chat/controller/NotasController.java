package com.seprocom.protesto.chat.controller;

import com.seprocom.protesto.chat.dto.NotasCabDTO;
import com.seprocom.protesto.chat.dto.NotasDetDTO;
import com.seprocom.protesto.chat.service.NotasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para gerenciamento de Notas Fiscais de Serviços (NFSe).
 * 
 * Fornece endpoints para CRUD completo de notas e seus itens.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/nfse")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "NFSe", description = "Endpoints para gerenciamento de Notas Fiscais de Serviços")
public class NotasController {

    private final NotasService notasService;

    // ============================================
    // Endpoints de Notas (Cabeçalho)
    // ============================================

    /**
     * Lista todas as notas com paginação
     */
    @GetMapping
    @Operation(summary = "Listar notas", description = "Lista todas as notas fiscais com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notas encontradas")
    })
    public ResponseEntity<Page<NotasCabDTO>> listarTodas(
            @PageableDefault(size = 20, sort = "dataEmissao") Pageable pageable
    ) {
        return ResponseEntity.ok(notasService.listarTodas(pageable));
    }

    /**
     * Busca nota por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota por ID", description = "Retorna uma nota fiscal pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<NotasCabDTO> buscarPorId(
            @Parameter(description = "ID da nota") @PathVariable Long id
    ) {
        return ResponseEntity.ok(notasService.buscarPorId(id));
    }

    /**
     * Lista notas por período de emissão
     */
    @GetMapping("/periodo")
    @Operation(summary = "Buscar notas por período", description = "Lista notas fiscais por período de emissão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notas encontradas")
    })
    public ResponseEntity<List<NotasCabDTO>> buscarPorPeriodo(
            @Parameter(description = "Data inicial (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return ResponseEntity.ok(notasService.buscarPorPeriodo(dataInicio, dataFim));
    }

    /**
     * Lista notas por período com paginação
     */
    @GetMapping("/periodo/paginado")
    @Operation(summary = "Buscar notas por período (paginado)", description = "Lista notas fiscais por período com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notas encontradas")
    })
    public ResponseEntity<Page<NotasCabDTO>> buscarPorPeriodoPaginado(
            @Parameter(description = "Data inicial (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(size = 20, sort = "dataEmissao") Pageable pageable
    ) {
        return ResponseEntity.ok(notasService.buscarPorPeriodo(dataInicio, dataFim, pageable));
    }

    /**
     * Lista notas por situação
     */
    @GetMapping("/situacao/{situacao}")
    @Operation(summary = "Buscar notas por situação", description = "Lista notas fiscais por situação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notas encontradas")
    })
    public ResponseEntity<List<NotasCabDTO>> buscarPorSituacao(
            @Parameter(description = "Situação da nota (P=Pendente, E=Emitida, C=Cancelada, R=Rejeitada)") 
            @PathVariable String situacao
    ) {
        return ResponseEntity.ok(notasService.buscarPorSituacao(situacao));
    }

    /**
     * Busca nota por número
     */
    @GetMapping("/numero/{numero}")
    @Operation(summary = "Buscar nota por número", description = "Retorna uma nota fiscal pelo número")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<NotasCabDTO> buscarPorNumero(
            @Parameter(description = "Número da nota") @PathVariable String numero
    ) {
        NotasCabDTO nota = notasService.buscarPorNumero(numero);
        if (nota == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nota);
    }

    /**
     * Busca notas por tomador
     */
    @GetMapping("/tomador")
    @Operation(summary = "Buscar notas por tomador", description = "Pesquisa notas fiscais por CNPJ/CPF ou nome do tomador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notas encontradas")
    })
    public ResponseEntity<List<NotasCabDTO>> buscarPorTomador(
            @Parameter(description = "Termo de busca (CNPJ/CPF ou nome)") @RequestParam String termo
    ) {
        return ResponseEntity.ok(notasService.buscarPorTomador(termo));
    }

    /**
     * Busca notas com filtro global
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar notas (filtro global)", description = "Pesquisa notas fiscais em todos os campos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notas encontradas")
    })
    public ResponseEntity<Page<NotasCabDTO>> buscarGlobal(
            @Parameter(description = "Termo de busca") @RequestParam String termo,
            @PageableDefault(size = 20, sort = "dataEmissao") Pageable pageable
    ) {
        return ResponseEntity.ok(notasService.buscarGlobal(termo, pageable));
    }

    /**
     * Cria uma nova nota fiscal
     */
    @PostMapping
    @Operation(summary = "Criar nota", description = "Cria uma nova nota fiscal de serviços")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<NotasCabDTO> criar(
            @Parameter(description = "Dados da nota") @Valid @RequestBody NotasCabDTO dto
    ) {
        return ResponseEntity.ok(notasService.criar(dto));
    }

    /**
     * Atualiza uma nota fiscal
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nota", description = "Atualiza uma nota fiscal existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<NotasCabDTO> atualizar(
            @Parameter(description = "ID da nota") @PathVariable Long id,
            @Parameter(description = "Dados da nota") @Valid @RequestBody NotasCabDTO dto
    ) {
        return ResponseEntity.ok(notasService.atualizar(id, dto));
    }

    /**
     * Exclui uma nota fiscal
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir nota", description = "Exclui uma nota fiscal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID da nota") @PathVariable Long id
    ) {
        notasService.excluir(id);
        return ResponseEntity.ok().build();
    }

    // ============================================
    // Endpoints de Itens (Detalhes)
    // ============================================

    /**
     * Lista todos os itens de uma nota
     */
    @GetMapping("/{notaId}/itens")
    @Operation(summary = "Listar itens da nota", description = "Lista todos os itens de uma nota fiscal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens encontrados"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<List<NotasDetDTO>> listarItens(
            @Parameter(description = "ID da nota") @PathVariable Long notaId
    ) {
        return ResponseEntity.ok(notasService.listarItensDaNota(notaId));
    }

    /**
     * Adiciona um item à nota
     */
    @PostMapping("/{notaId}/itens")
    @Operation(summary = "Adicionar item", description = "Adiciona um item à nota fiscal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada")
    })
    public ResponseEntity<NotasDetDTO> adicionarItem(
            @Parameter(description = "ID da nota") @PathVariable Long notaId,
            @Parameter(description = "Dados do item") @Valid @RequestBody NotasDetDTO dto
    ) {
        return ResponseEntity.ok(notasService.adicionarItem(notaId, dto));
    }

    /**
     * Atualiza um item da nota
     */
    @PutMapping("/{notaId}/itens/{itemId}")
    @Operation(summary = "Atualizar item", description = "Atualiza um item da nota fiscal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Item ou nota não encontrado")
    })
    public ResponseEntity<NotasDetDTO> atualizarItem(
            @Parameter(description = "ID da nota") @PathVariable Long notaId,
            @Parameter(description = "ID do item") @PathVariable Long itemId,
            @Parameter(description = "Dados do item") @Valid @RequestBody NotasDetDTO dto
    ) {
        return ResponseEntity.ok(notasService.atualizarItem(notaId, itemId, dto));
    }

    /**
     * Remove um item da nota
     */
    @DeleteMapping("/{notaId}/itens/{itemId}")
    @Operation(summary = "Remover item", description = "Remove um item da nota fiscal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item ou nota não encontrado")
    })
    public ResponseEntity<Void> removerItem(
            @Parameter(description = "ID da nota") @PathVariable Long notaId,
            @Parameter(description = "ID do item") @PathVariable Long itemId
    ) {
        notasService.removerItem(notaId, itemId);
        return ResponseEntity.ok().build();
    }
}

