package com.selador.web.servlets;

import com.selador.service.CalculoCustasService;
import com.selador.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * Servlet para operações de cálculo de custas cartoriais
 * Endpoint: /api/custas/*
 */
@WebServlet(name = "CustasServlet", urlPatterns = {"/api/custas/*"})
public class CustasServlet extends BaseServlet {
    
    private CalculoCustasService calculoCustasService;
    
    @Override
    public void init() throws javax.servlet.ServletException {
        super.init();
        calculoCustasService = CalculoCustasService.getInstance();
        System.out.println("✅ CustasServlet inicializado");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        long startTime = System.currentTimeMillis();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/custas - Lista tipos de atos
                listarTiposAtos(request, response);
            } else if (pathInfo.startsWith("/atos")) {
                // GET /api/custas/atos - Lista atos por módulo
                listarAtosPorModulo(request, response);
            } else if (pathInfo.startsWith("/encargos")) {
                // GET /api/custas/encargos - Lista encargos
                listarEncargos(request, response);
            } else if (pathInfo.startsWith("/tipos")) {
                // GET /api/custas/tipos - Busca atos por descrição
                buscarAtosPorDescricao(request, response);
            } else if (pathInfo.startsWith("/modulos")) {
                // GET /api/custas/modulos - Lista módulos
                listarModulos(request, response);
            } else if (pathInfo.startsWith("/tipos-ato")) {
                // GET /api/custas/tipos-ato?modulo=N - Lista tipos de ato por módulo
                listarTiposAtoPorModulo(request, response);
            } else if (pathInfo.startsWith("/protocolos")) {
                // GET /api/custas/protocolos - Lista protocolos
                listarProtocolos(request, response);
            } else if (pathInfo.startsWith("/imoveis-protocolo")) {
                // GET /api/custas/imoveis-protocolo?protocolo=X - Lista imóveis por protocolo
                listarImoveisPorProtocolo(request, response);
            } else if (pathInfo.startsWith("/imoveis")) {
                // GET /api/custas/imoveis?termo=X - Busca imóveis
                buscarImoveis(request, response);
            } else if (pathInfo.startsWith("/partes")) {
                // GET /api/custas/partes?imovelId=X - Busca partes de imóveis
                buscarPartes(request, response);
            } else if (pathInfo.startsWith("/protocolo/")) {
                // GET /api/custas/protocolo/{codigo} - Busca dados de um protocolo específico
                buscarDadosProtocolo(request, response);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, 
                    "Endpoint não encontrado: " + pathInfo);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logRequest(request, "GET", duration);
            
        } catch (Exception e) {
            logSimplificado("ERRO", "CustasServlet GET: " + e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erro interno: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        long startTime = System.currentTimeMillis();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.startsWith("/calcular")) {
                // POST /api/custas ou /api/custas/calcular - Calcula custas
                calcularCustas(request, response);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Endpoint POST não reconhecido: " + pathInfo);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logRequest(request, "POST", duration);
            
        } catch (Exception e) {
            logSimplificado("ERRO", "CustasServlet POST: " + e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erro interno: " + e.getMessage());
        }
    }
    
    /**
     * GET /api/custas - Lista todos os tipos de atos
     */
    private void listarTiposAtos(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        System.out.println("📋 [CUSTAS] Listando tipos de atos");
        
        String modulo = request.getParameter("modulo");
        Map<String, Object> resultado = calculoCustasService.listarTiposAtos(modulo);
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/atos?modulo=N - Lista atos por módulo
     */
    private void listarAtosPorModulo(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        System.out.println("📋 [CUSTAS] Listando atos por módulo");
        
        String modulo = request.getParameter("modulo");
        if (modulo == null || modulo.isEmpty()) {
            modulo = "N"; // Padrão: Tabelionato de Notas
        }
        
        Map<String, Object> resultado = calculoCustasService.listarTiposAtos(modulo);
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/encargos - Lista encargos (FUNDEP, ISS, FUNREJUS, SELOS)
     */
    private void listarEncargos(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        System.out.println("💰 [CUSTAS] Listando encargos");
        
        Map<String, Object> resultado = calculoCustasService.listarEncargos();
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/tipos?search=escritura - Busca atos por descrição
     */
    private void buscarAtosPorDescricao(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String search = request.getParameter("search");
        System.out.println("🔍 [CUSTAS] Buscando atos por: " + search);
        
        // TODO: Implementar busca por descrição no service
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("success", false);
        resultado.put("mensagem", "Funcionalidade de busca em desenvolvimento");
        resultado.put("search", search);
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * POST /api/custas/calcular - Calcula custas para um ato
     */
    private void calcularCustas(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String requestBody = getRequestBody(request);
        System.out.println("🧮 [CUSTAS] Calculando custas - Dados: " + requestBody);
        
        // Parsear JSON
        Map<String, Object> dados;
        try {
            dados = JsonUtil.fromJsonMap(requestBody);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                "JSON inválido: " + e.getMessage());
            return;
        }
        
        // Validar parâmetros obrigatórios
        if (dados == null || !dados.containsKey("tipoAto") || !dados.containsKey("baseCalculo")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Parâmetros obrigatórios: tipoAto, baseCalculo");
            return;
        }
        
        try {
            // Executar cálculo
            Map<String, Object> resultado = calculoCustasService.calcularCustas(dados);
            
            // Adicionar timestamp
            resultado.put("timestamp", new Date());
            
            sendSuccessResponse(response, resultado);
            
        } catch (Exception e) {
            System.out.println("❌ [CUSTAS] Erro no cálculo: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> erro = new HashMap<>();
            erro.put("success", false);
            erro.put("mensagem", "Erro ao calcular custas: " + e.getMessage());
            erro.put("timestamp", new Date());
            
            sendJsonResponse(response, erro);
        }
    }
    
    /**
     * Método de log simplificado
     */
    private void logSimplificado(String tipo, String mensagem) {
        System.out.println("[" + tipo + "] [CUSTAS] " + mensagem);
    }
    
    /**
     * GET /api/custas/imoveis?termo={termo} - Busca imóveis
     */
    private void buscarImoveis(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String termo = request.getParameter("termo");
        System.out.println("🏠 [CUSTAS] Buscando imóveis com termo: " + termo);
        
        Map<String, Object> resultado = calculoCustasService.buscarImoveis(termo);
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/partes?imovelId={id} - Busca partes de um imóvel
     */
    private void buscarPartes(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String imovelId = request.getParameter("imovelId");
        System.out.println("👥 [CUSTAS] Buscando partes do imóvel: " + imovelId);
        
        Map<String, Object> resultado = calculoCustasService.buscarPartes(imovelId);
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/protocolo/{codigo} - Busca dados de um protocolo específico
     */
    private void buscarDadosProtocolo(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        // Extrai o código do protocolo da URL
        String pathInfo = request.getPathInfo();
        String codigoProtocolo = pathInfo.replace("/protocolo/", "").replace("/", "");
        
        System.out.println("📋 [CUSTAS] Buscando dados do protocolo: " + codigoProtocolo);
        
        Map<String, Object> resultado = calculoCustasService.buscarDadosProtocolo(codigoProtocolo);
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/modulos - Lista módulos
     */
    private void listarModulos(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        System.out.println("📋 [CUSTAS] Listando módulos");
        
        Map<String, Object> resultado = calculoCustasService.listarModulos();
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/tipos-ato?modulo=N - Lista tipos de ato por módulo
     */
    private void listarTiposAtoPorModulo(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String modulo = request.getParameter("modulo");
        System.out.println("📋 [CUSTAS] Listando tipos de ato para módulo: " + modulo);
        
        Map<String, Object> resultado = calculoCustasService.listarTiposAtoPorModulo(modulo);
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/protocolos - Lista protocolos
     */
    private void listarProtocolos(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        System.out.println("📋 [CUSTAS] Listando protocolos");
        
        Map<String, Object> resultado = calculoCustasService.listarProtocolos();
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/custas/imoveis-protocolo?protocolo=X - Lista imóveis por protocolo
     */
    private void listarImoveisPorProtocolo(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String protocolo = request.getParameter("protocolo");
        System.out.println("📋 [CUSTAS] Listando imóveis para protocolo: " + protocolo);
        
        Map<String, Object> resultado = calculoCustasService.listarImoveisPorProtocolo(protocolo);
        
        sendSuccessResponse(response, resultado);
    }
}
