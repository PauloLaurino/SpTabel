package com.seprocom.gerencial.controller;

import com.seprocom.gerencial.dto.ParametrosDTO;
import com.seprocom.gerencial.service.ParametrosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de compatibilidade para suportar as chamadas legadas do Maker
 * centralizadas agora no projeto Gerencial.
 */
@RestController
@RequestMapping("/maker/api")
@RequiredArgsConstructor
public class LegacyCompatibilityController {

    private final ParametrosService parametrosService;
    private final com.seprocom.gerencial.service.SeloService seloService;

    @GetMapping("/parametros")
    public ResponseEntity<Map<String, Object>> getParametrosLegacy() {
        ParametrosDTO dto = parametrosService.buscarParametros();
        
        // Mapeia para o formato esperado pelo HTML legado
        Map<String, Object> legacy = new HashMap<>();
        legacy.put("CODTABEL_PAR", dto.getCodigoCartorio());
        legacy.put("USUARIO_PAR", dto.getNomeTabeliao());
        legacy.put("SELOS_PAR", "{\"naturezas\":[\"tp\",\"tn\",\"ah\"]}"); // Exemplo de fallback
        legacy.put("codigoOficio", dto.getCodigoCartorio());
        
        return ResponseEntity.ok(legacy);
    }

    @GetMapping("/session/info")
    public ResponseEntity<Map<String, Object>> getSessionInfo() {
        Map<String, Object> session = new HashMap<>();
        Map<String, Object> sLista = new HashMap<>();
        
        ParametrosDTO dto = parametrosService.buscarParametros();
        sLista.put("CODTABEL_PAR", dto.getCodigoCartorio());
        sLista.put("USUARIO_PAR", dto.getNomeTabeliao());
        
        session.put("sLista", sLista);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/notas/selos/cards/estoque")
    public ResponseEntity<Map<String, Object>> getEstoqueLegacy() {
        Map<String, Object> response = new HashMap<>();
        response.put("estoque", seloService.contarEstoqueDisponivel());
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notas/certificado")
    public ResponseEntity<Map<String, Object>> getCertificadoLegacy() {
        return ResponseEntity.ok(seloService.validarCertificado());
    }

    // Encaminha chamadas de monitoramento para os controllers reais se existirem
    // ou retorna mock/vazio por enquanto para evitar 404
    @GetMapping("/funarpen/selos/monitor")
    public ResponseEntity<Map<String, Object>> getMonitorLegacy(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> response = new HashMap<>();
        response.put("logs", new java.util.ArrayList<>());
        response.put("selos", new java.util.ArrayList<>());
        response.put("estoque", seloService.contarEstoqueDisponivel());
        return ResponseEntity.ok(response);
    }
}
