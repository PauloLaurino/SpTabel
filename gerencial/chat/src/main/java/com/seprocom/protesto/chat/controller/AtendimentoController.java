package com.seprocom.protesto.chat.controller;

import com.seprocom.protesto.chat.dto.request.MensagemRequest;
import com.seprocom.protesto.chat.dto.request.VincularIntimacaoRequest;
import com.seprocom.protesto.chat.dto.response.AtendimentoResponse;
import com.seprocom.protesto.chat.dto.response.MensagemResponse;
import com.seprocom.protesto.chat.entity.Atendimento;
import com.seprocom.protesto.chat.service.AtendimentoService;
import com.seprocom.protesto.chat.service.SincronizacaoCtp001Service;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de atendimentos.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Atendimentos", description = "Endpoints para gerenciamento de atendimentos")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;
    private final SincronizacaoCtp001Service sincronizacaoService;

    /**
     * Cria um novo atendimento.
     *
     * @param contatoId ID do contato
     * @param operadorId ID do operador
     * @return Atendimento criado
     */
    @PostMapping
    @Operation(summary = "Criar atendimento", description = "Cria um novo atendimento para um contato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Contato ou operador não encontrado")
    })
    public ResponseEntity<AtendimentoResponse> criarAtendimento(
            @Parameter(description = "ID do contato") @RequestParam Long contatoId,
            @Parameter(description = "ID do operador") @RequestParam Long operadorId
    ) {
        AtendimentoResponse response = atendimentoService.criarAtendimento(contatoId, operadorId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca atendimento por ID.
     *
     * @param id ID do atendimento
     * @return Atendimento encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar atendimento", description = "Busca um atendimento pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    public ResponseEntity<AtendimentoResponse> buscarPorId(
            @Parameter(description = "ID do atendimento") @PathVariable Long id
    ) {
        AtendimentoResponse response = atendimentoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista atendimentos por operador.
     *
     * @param operadorId ID do operador
     * @param pageable Paginação
     * @return Página de atendimentos
     */
    @GetMapping("/operador/{operadorId}")
    @Operation(summary = "Listar por operador", description = "Lista atendimentos de um operador")
    @ApiResponse(responseCode = "200", description = "Lista de atendimentos")
    public ResponseEntity<Page<AtendimentoResponse>> listarPorOperador(
            @Parameter(description = "ID do operador") @PathVariable Long operadorId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<AtendimentoResponse> response = atendimentoService.listarAtendimentosPorOperador(operadorId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista atendimentos por status.
     *
     * @param status Status do atendimento
     * @param pageable Paginação
     * @return Página de atendimentos
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Listar por status", description = "Lista atendimentos por status")
    @ApiResponse(responseCode = "200", description = "Lista de atendimentos")
    public ResponseEntity<Page<AtendimentoResponse>> listarPorStatus(
            @Parameter(description = "Status do atendimento") @PathVariable Atendimento.StatusAtendimento status,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<AtendimentoResponse> response = atendimentoService.listarAtendimentosPorStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza status do atendimento.
     *
     * @param id ID do atendimento
     * @param status Novo status
     * @return Atendimento atualizado
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status", description = "Atualiza o status de um atendimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    public ResponseEntity<AtendimentoResponse> atualizarStatus(
            @Parameter(description = "ID do atendimento") @PathVariable Long id,
            @Parameter(description = "Novo status") @RequestParam Atendimento.StatusAtendimento status
    ) {
        AtendimentoResponse response = atendimentoService.atualizarStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Envia uma mensagem no atendimento.
     *
     * @param id ID do atendimento
     * @param request Dados da mensagem
     * @return Mensagem enviada
     */
    @PostMapping("/{id}/mensagens")
    @Operation(summary = "Enviar mensagem", description = "Envia uma mensagem no atendimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagem enviada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    public ResponseEntity<MensagemResponse> enviarMensagem(
            @Parameter(description = "ID do atendimento") @PathVariable Long id,
            @Valid @RequestBody MensagemRequest request
    ) {
        MensagemResponse response = atendimentoService.enviarMensagem(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista mensagens de um atendimento.
     *
     * @param id ID do atendimento
     * @return Lista de mensagens
     */
    @GetMapping("/{id}/mensagens")
    @Operation(summary = "Listar mensagens", description = "Lista todas as mensagens de um atendimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mensagens"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    public ResponseEntity<List<MensagemResponse>> listarMensagens(
            @Parameter(description = "ID do atendimento") @PathVariable Long id
    ) {
        List<MensagemResponse> response = atendimentoService.listarMensagens(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Vincula uma intimação ao atendimento.
     *
     * @param id ID do atendimento
     * @param request Dados do vínculo
     * @return Atendimento atualizado
     */
    @PostMapping("/{id}/vinculos")
    @Operation(summary = "Vincular intimação", description = "Vincula uma intimação/boleto ao atendimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vínculo criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou vínculo já existe"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    public ResponseEntity<AtendimentoResponse> vincularIntimacao(
            @Parameter(description = "ID do atendimento") @PathVariable Long id,
            @Valid @RequestBody VincularIntimacaoRequest request
    ) {
        AtendimentoResponse response = atendimentoService.vincularIntimacao(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Envia PDF de intimação.
     *
     * @param id ID do atendimento
     * @param vinculoId ID do vínculo
     * @param arquivo Arquivo PDF
     * @return Mensagem enviada
     */
    @PostMapping(value = "/{id}/vinculos/{vinculoId}/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar PDF de intimação", description = "Envia o PDF da intimação para o devedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF enviado"),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
            @ApiResponse(responseCode = "404", description = "Atendimento ou vínculo não encontrado")
    })
    public ResponseEntity<MensagemResponse> enviarPdfIntimacao(
            @Parameter(description = "ID do atendimento") @PathVariable Long id,
            @Parameter(description = "ID do vínculo") @PathVariable Long vinculoId,
            @Parameter(description = "Arquivo PDF") @RequestParam("arquivo") MultipartFile arquivo
    ) {
        MensagemResponse response = atendimentoService.enviarPdfIntimacao(id, vinculoId, arquivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra comprovante de pagamento.
     *
     * @param id ID do atendimento
     * @param vinculoId ID do vínculo
     * @param arquivo Arquivo do comprovante
     * @return Mensagem enviada
     */
    @PostMapping(value = "/{id}/vinculos/{vinculoId}/comprovante", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Registrar comprovante", description = "Registra o comprovante de pagamento recebido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comprovante registrado"),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
            @ApiResponse(responseCode = "404", description = "Atendimento ou vínculo não encontrado")
    })
    public ResponseEntity<MensagemResponse> registrarComprovante(
            @Parameter(description = "ID do atendimento") @PathVariable Long id,
            @Parameter(description = "ID do vínculo") @PathVariable Long vinculoId,
            @Parameter(description = "Arquivo do comprovante") @RequestParam("arquivo") MultipartFile arquivo
    ) {
        MensagemResponse response = atendimentoService.registrarComprovantePagamento(id, vinculoId, arquivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Sincroniza uma intimação específica com a tabela ctp001.
     *
     * @param vinculoId ID do vínculo da intimação
     * @return Resultado da sincronização
     */
    @PostMapping("/vinculos/{vinculoId}/sincronizar")
    @Operation(summary = "Sincronizar intimação", description = "Sincroniza os dados da intimação com a tabela ctp001")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sincronização realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vínculo não encontrado")
    })
    public ResponseEntity<Map<String, Object>> sincronizarIntimacao(
            @Parameter(description = "ID do vínculo") @PathVariable Long vinculoId
    ) {
        log.info("Solicitação de sincronização para vínculo {}", vinculoId);
        boolean sucesso = sincronizacaoService.sincronizarPorId(vinculoId);
        return ResponseEntity.ok(Map.of(
                "sucesso", sucesso,
                "mensagem", sucesso ? "Sincronização realizada com sucesso" : "Falha na sincronização"
        ));
    }

    /**
     * Sincroniza todas as intimações pendentes com a tabela ctp001.
     *
     * @return Resultado da sincronização em massa
     */
    @PostMapping("/sincronizar-todas")
    @Operation(summary = "Sincronizar todas", description = "Sincroniza todas as intimações pendentes com a tabela ctp001")
    @ApiResponse(responseCode = "200", description = "Sincronização em massa realizada")
    public ResponseEntity<Map<String, Object>> sincronizarTodas() {
        log.info("Solicitação de sincronização em massa");
        int sincronizadas = sincronizacaoService.sincronizarTodasPendentes();
        return ResponseEntity.ok(Map.of(
                "sincronizadas", sincronizadas,
                "mensagem", String.format("%d registros sincronizados", sincronizadas)
        ));
    }
}
