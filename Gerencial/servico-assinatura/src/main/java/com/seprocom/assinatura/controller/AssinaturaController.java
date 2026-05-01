package com.seprocom.assinatura.controller;

import com.seprocom.assinatura.dto.AgruparRequest;
import com.seprocom.assinatura.dto.AssinaturaCaminhoRequest;
import com.seprocom.assinatura.dto.AssinaturaRequest;
import com.seprocom.assinatura.dto.AssinaturaResponse;
import com.seprocom.assinatura.dto.CopiarPaginaRequest;
import com.seprocom.assinatura.service.AssinaturaService;
import com.seprocom.assinatura.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AssinaturaController {

    private final AssinaturaService assinaturaService;
    private final PdfService pdfService;

    @PostMapping(value = "/assinar", consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssinaturaResponse> assinar(@Valid @RequestBody AssinaturaRequest request) {
        log.info("Recebida requisicao de assinatura Base64 - timestamp: {}", request.getTimestamp());
        
        AssinaturaResponse response = assinaturaService.assinar(request);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/assinar-caminho", consumes = MediaType.APPLICATION_JSON_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> assinarPorCaminho(@Valid @RequestBody AssinaturaCaminhoRequest request) {
        log.info("Recebida requisicao de assinatura por caminho: {}", request.getCaminho());
        log.info("Saida: {}", request.getSaida());
        
        try {
            assinaturaService.assinarPorCaminho(request);
            
            return ResponseEntity.ok(Map.of(
                "status", "ok",
                "mensagem", "Documento assinado com sucesso",
                "arquivo", request.getSaida()
            ));
        } catch (Exception e) {
            log.error("Erro ao assinar por caminho", e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "erro",
                "mensagem", e.getMessage()
            ));
        }
    }

    @PostMapping(value = "/pdf/copiar-paginas", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> copiarPaginas(@Valid @RequestBody CopiarPaginaRequest request) {
        log.info("Recebida requisicao de copiar paginas");
        log.info("Livro: {}, Paginas: {} a {}, Saida: {}", 
                 request.getCaminhoLivro(), request.getPaginaInicial(), request.getPaginaFinal(), request.getSaida());
        
        try {
            pdfService.copiarPaginas(request);
            
            return ResponseEntity.ok(Map.of(
                "status", "ok",
                "mensagem", "Paginas copiadas com sucesso",
                "arquivo", request.getSaida()
            ));
        } catch (Exception e) {
            log.error("Erro ao copiar paginas", e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "erro",
                "mensagem", e.getMessage()
            ));
        }
    }

    @PostMapping(value = "/pdf/agrupar", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> agruparPdfs(@Valid @RequestBody AgruparRequest request) {
        log.info("Recebida requisicao de agrupar PDFs");
        log.info("Pasta: {}, Prefixo: {}, Saida: {}", 
                 request.getPasta(), request.getPrefixo(), request.getSaida());
        
        try {
            int quantidade = pdfService.agruparPdfs(request);
            
            return ResponseEntity.ok(Map.of(
                "status", "ok",
                "mensagem", "PDFs agrupados com sucesso",
                "arquivo", request.getSaida(),
                "quantidade", quantidade
            ));
        } catch (Exception e) {
            log.error("Erro ao agrupar PDFs", e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "erro",
                "mensagem", e.getMessage()
            ));
        }
    }

    @PostMapping(value = "/pdf/listar", consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> listarArquivos(@RequestBody Map<String, String> request) {
        String caminho = request.get("caminho");
        log.info("Recebida requisicao de listagem de arquivos: {}", caminho);
        
        try {
            List<Map<String, Object>> arquivos = pdfService.listarPdfs(caminho);
            return ResponseEntity.ok(Map.of(
                "status", "ok",
                "arquivos", arquivos
            ));
        } catch (Exception e) {
            log.error("Erro ao listar arquivos", e);
            return ResponseEntity.badRequest().body(Map.of(
                "status", "erro",
                "mensagem", e.getMessage()
            ));
        }
    }

    @GetMapping("/health")

    public ResponseEntity<String> health() {
        return ResponseEntity.ok("{\"status\": \"UP\"}");
    }
}