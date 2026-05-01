package com.seprocom.protesto.nfse.controller;

import com.seprocom.protesto.nfse.dto.NfseEnvioDTO;
import com.seprocom.protesto.nfse.dto.NfseRetornoDTO;
import com.seprocom.protesto.nfse.service.NfseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller para NFSe.
 * Endpoints para emissao, consulta e cancelamento de NFS-e.
 */
@RestController
@RequestMapping("/api/nfse")
public class NfseController {

    private final NfseService nfseService;

    public NfseController(NfseService nfseService) {
        this.nfseService = nfseService;
    }

    /**
     * Emite uma nova NFSe.
     */
    @PostMapping("/emitir")
    public ResponseEntity<NfseRetornoDTO> emitirNfse(@Valid @RequestBody NfseEnvioDTO envio) {
        NfseRetornoDTO retorno = nfseService.emitirNfse(envio);
        return ResponseEntity.ok(retorno);
    }

    /**
     * Consulta NFSe pelo numero.
     */
    @GetMapping("/consultar/{numero}")
    public ResponseEntity<NfseRetornoDTO> consultarNfse(@PathVariable String numero) {
        NfseRetornoDTO retorno = nfseService.consultarNfse(numero);
        return ResponseEntity.ok(retorno);
    }

    /**
     * Cancela uma NFSe.
     */
    @PostMapping("/cancelar")
    public ResponseEntity<NfseRetornoDTO> cancelarNfse(@RequestParam String numero, @RequestParam String motivo) {
        NfseRetornoDTO retorno = nfseService.cancelarNfse(numero, motivo);
        return ResponseEntity.ok(retorno);
    }
}
