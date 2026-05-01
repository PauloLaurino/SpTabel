package com.seprocom.gerencial.controller;

import com.seprocom.gerencial.dao.FunarpenMonitorDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/maker/api/funarpen")
public class FunarpenController {

    @Autowired
    private FunarpenMonitorDAO funarpenDAO;

    /**
     * Endpoint unificado para os cards do dashboard (Estoque, Utilizados, Pendentes, Erros)
     */
    @GetMapping("/monitor/cards")
    public Map<String, Object> getMonitorCards() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Integer> kpis = funarpenDAO.getKpisDashboard();
        Map<String, Integer> estoque = funarpenDAO.contarEstoquePorTipo();

        response.put("kpis", kpis);
        response.put("estoquePorTipo", estoque);
        response.put("status", "success");
        return response;
    }

    /**
     * Listagem de selos para a tela principal (Selos Utilizados)
     */
    @GetMapping("/selos/naoRetornados")
    public Map<String, Object> getSelosNaoRetornados(
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "50") Integer limit,
            @RequestParam(defaultValue = "false") boolean incluirTransmitidos,
            @RequestParam(required = false) String statusFiltro) {

        List<Map<String, Object>> selos = funarpenDAO.listarSelosNaoRetornados(dataInicio, dataFim, offset, limit, incluirTransmitidos, statusFiltro);
        
        Map<String, Object> response = new HashMap<>();
        response.put("selosNaoRetornados", selos);
        response.put("total", selos.size());
        return response;
    }

    /**
     * Endpoints de compatibilidade para Cards individuais (se o frontend chamar separadamente)
     */
    @GetMapping("/selos/cards/estoque")
    public Map<String, Object> getEstoqueCards() {
        return getMonitorCards();
    }

    @GetMapping("/selos/cards/utilizadosHoje")
    public Map<String, Object> getUtilizadosHoje() {
        Map<String, Object> response = new HashMap<>();
        response.put("utilizadosHoje", funarpenDAO.getKpisDashboard().get("utilizadosHoje"));
        return response;
    }

    @PostMapping("/selos/reprocess")
    public Map<String, Object> reprocessarSelo(@RequestBody Map<String, Object> payload) {
        // Implementação do reenvio será movida para um Service especializado
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Selo enfileirado para reprocessamento (Simulado)");
        return response;
    }

    /**
     * Endpoint para informações do certificado digital
     */
    @GetMapping("/certificado")
    public Map<String, Object> getCertificadoInfo() {
        Map<String, Object> info = new HashMap<>();
        try {
            // Busca parâmetros do banco (JdbcTemplate injetado via DAO ou diretamente)
            // Para simplificar, vou usar os valores da tabela parametros
            Map<String, Object> params = funarpenDAO.getParametrosCartorio();
            
            String pfxPath = (String) params.get("CAMINHOCERTIFICADO_PAR");
            String pfxPass = (String) params.get("SENHACERTIFICADO_PAR");
            String titular = (String) params.get("TITULAR_PAR");

            if (pfxPath == null || pfxPath.isEmpty()) {
                info.put("valid", false);
                info.put("message", "Certificado não configurado");
                return info;
            }

            File pfxFile = new File(pfxPath);
            if (!pfxFile.exists()) {
                info.put("valid", false);
                info.put("message", "Arquivo não encontrado: " + pfxPath);
                return info;
            }

            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (InputStream is = new FileInputStream(pfxFile)) {
                ks.load(is, (pfxPass != null ? pfxPass : "").toCharArray());
            }

            String alias = ks.aliases().nextElement();
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

            Date now = new Date();
            info.put("subject", cert.getSubjectDN().getName());
            info.put("notBefore", iso8601(cert.getNotBefore()));
            info.put("notAfter", iso8601(cert.getNotAfter()));
            info.put("valid", now.after(cert.getNotBefore()) && now.before(cert.getNotAfter()));
            
            if (titular != null && !titular.isEmpty()) {
                info.put("matchesName", ((String)info.get("subject")).toLowerCase().contains(titular.toLowerCase()));
            }

        } catch (Exception e) {
            info.put("valid", false);
            info.put("message", "Erro: " + e.getMessage());
        }
        return info;
    }

    private static String iso8601(Date d) {
        if (d == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(d);
    }
}
