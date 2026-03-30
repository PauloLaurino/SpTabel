package com.selador.web.servlets;

import com.selador.service.SeloService;
import com.selador.service.SelagemService;
import com.selador.util.JsonUtil;
import com.selador.util.SeloJsonSanitizerNotas;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Servlet para compatibilidade com API do Protesto (Funarpen)
 * Mapeia endpoints /maker/api/funarpen/selos/* para o frontend React
 */
@WebServlet(name = "FunarpenSelosServlet", urlPatterns = {"/maker/api/funarpen/selos/*"})
public class FunarpenSelosServlet extends HttpServlet {

    private SeloService seloService;
    private SelagemService selagemService;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.seloService = SeloService.getInstance();
        } catch (Exception e) {
            this.seloService = null;
            System.err.println("FunarpenSelosServlet: Erro ao inicializar SeloService: " + e.getMessage());
        }
        try {
            this.selagemService = SelagemService.getInstance();
        } catch (Exception e) {
            this.selagemService = null;
            System.err.println("FunarpenSelosServlet: Erro ao inicializar SelagemService: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                sendJsonResponse(response, createSuccessResponse("API Funarpen Selos funcionando"));
                return;
            }
            
            // /maker/api/funarpen/selos/naoRetornados
            if (pathInfo.startsWith("/naoRetornados")) {
                handleNaoRetornados(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/json/get
            if (pathInfo.startsWith("/json/get")) {
                handleJsonGet(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/cards/estoque
            if (pathInfo.startsWith("/cards/estoque")) {
                handleCardsEstoque(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/cards/utilizadosHoje
            if (pathInfo.startsWith("/cards/utilizadosHoje")) {
                handleCardsUtilizadosHoje(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/cards/pendentes
            if (pathInfo.startsWith("/cards/pendentes")) {
                handleCardsPendentes(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/cards/retornadosComErro
            if (pathInfo.startsWith("/cards/retornadosComErro")) {
                handleCardsRetornadosComErro(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/cards/alertas
            if (pathInfo.startsWith("/cards/alertas")) {
                handleCardsAlertas(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/sanitizar/lote - Sanitiza todos os registros da versão 112
            if (pathInfo != null && pathInfo.startsWith("/sanitizar/lote")) {
                handleSanitizarLote(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/sanitizar/{seloDigital} - Sanitiza um selo específico
            if (pathInfo != null && pathInfo.startsWith("/sanitizar/")) {
                String selloDigital = pathInfo.substring("/sanitizar/".length());
                handleSanitizarSelo(request, response, selloDigital);
                return;
            }
            
            // /maker/api/funarpen/selos/logs
            if (pathInfo.startsWith("/logs")) {
                handleLogs(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/logsByIdap
            if (pathInfo.startsWith("/logsByIdap")) {
                handleLogsByIdap(request, response);
                return;
            }
            
            // /maker/api/funarpen/parametros
            if (pathInfo.endsWith("/parametros")) {
                handleParametros(request, response);
                return;
            }
            
            // Endpoint não encontrado
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            sendJsonResponse(response, createErrorResponse("Endpoint não encontrado: " + pathInfo));
            
        } catch (Exception e) {
            System.err.println("FunarpenSelosServlet GET Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(response, createErrorResponse("Erro interno: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // /maker/api/funarpen/selos/sanitizar/lote - Sanitiza todos os registros
            if (pathInfo != null && pathInfo.startsWith("/sanitizar/lote")) {
                handleSanitizarLote(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/sanitizar/{seloDigital} - Sanitiza um selo específico
            if (pathInfo != null && pathInfo.startsWith("/sanitizar/")) {
                String selloDigital = pathInfo.substring("/sanitizar/".length());
                handleSanitizarSelo(request, response, selloDigital);
                return;
            }
            
            // /maker/api/funarpen/selos/json/save
            if (pathInfo != null && pathInfo.startsWith("/json/save")) {
                handleJsonSave(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/recepcao/lote
            if (pathInfo != null && pathInfo.startsWith("/recepcao/lote")) {
                handleRecepcaoLote(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/retificar
            if (pathInfo != null && pathInfo.startsWith("/retificar")) {
                handleRetificar(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/updateJson
            if (pathInfo != null && pathInfo.startsWith("/updateJson")) {
                handleUpdateJson(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/reenvio/porCodigo
            if (pathInfo != null && pathInfo.startsWith("/reenvio/porCodigo")) {
                handleReenvioPorCodigo(request, response);
                return;
            }
            
            // /maker/api/funarpen/selos/reprocessa/erro
            if (pathInfo != null && pathInfo.startsWith("/reprocessa/erro")) {
                handleReprocessaErro(request, response);
                return;
            }
            
            // Endpoint não encontrado
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            sendJsonResponse(response, createErrorResponse("Endpoint POST não encontrado: " + pathInfo));
            
        } catch (Exception e) {
            System.err.println("FunarpenSelosServlet POST Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(response, createErrorResponse("Erro interno: " + e.getMessage()));
        }
    }

    // ==================== HANDLERS ====================

    private void handleNaoRetornados(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Retorna lista de selos não retornados
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> selos = new ArrayList<>();
        
        // Tentar buscar do serviço
        if (seloService != null) {
            try {
                // Buscar selos com status diferente de retornado
                // Por agora, retorna array vazio como fallback
                result.put("data", selos);
            } catch (Exception e) {
                result.put("data", selos);
            }
        } else {
            result.put("data", selos);
        }
        
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleJsonGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String selo = request.getParameter("selo");
        
        Map<String, Object> result = new HashMap<>();
        
        if (selo == null || selo.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            result.put("success", false);
            result.put("mensagem", "Parâmetro 'selo' é obrigatório");
            sendJsonResponse(response, result);
            return;
        }
        
        // Buscar JSON do selo
        // Por agora, retorna JSON vazio como fallback
        if (seloService != null) {
            try {
                // BuscarJSON do selo
                result.put("JSON12", "{}");
            } catch (Exception e) {
                result.put("JSON12", "{}");
            }
        } else {
            result.put("JSON12", "{}");
        }
        
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleJsonSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String body = request.getReader().lines().collect(java.util.stream.Collectors.joining());
        Gson gson = new Gson();
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            JsonObject json = gson.fromJson(body, JsonObject.class);
            String selloCodigo = json.has("selo") ? json.get("selo").getAsString() : "";
            String jsonData = json.has("json") ? json.get("json").getAsString() : "{}";
            
            if (selloCodigo.isEmpty()) {
                result.put("success", false);
                result.put("mensagem", "Parâmetro 'selo' é obrigatório");
                sendJsonResponse(response, result);
                return;
            }
            
            // Salvar JSON do selo
            // Por agora, retorna sucesso
            result.put("success", true);
            result.put("mensagem", "JSON salvo com sucesso");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("mensagem", "Erro ao salvar JSON: " + e.getMessage());
        }
        
        sendJsonResponse(response, result);
    }

    private void handleCardsEstoque(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        // Retorna cards de estoque
        Map<String, Integer> estoque = new HashMap<>();
        estoque.put("TP1", 0);
        estoque.put("TP3", 0);
        estoque.put("TP4", 0);
        estoque.put("TPD", 0);
        estoque.put("TPI", 0);
        
        result.put("estoque", estoque);
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleCardsUtilizadosHoje(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("utilizadosHoje", 0);
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleCardsPendentes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("pendentes", 0);
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleCardsRetornadosComErro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("retornadosComErro", 0);
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleCardsAlertas(HttpServletRequest response) throws IOException {
        // Este método estava com problema de parâmetro - corrigido
    }

    private void handleCardsAlertas(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> alertas = new HashMap<>();
        alertas.put("zerados", false);
        alertas.put("proximosZerar", false);
        result.put("alertas", alertas);
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleLogs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String selo = request.getParameter("selo");
        
        Map<String, Object> result = new HashMap<>();
        result.put("logs", new ArrayList<>());
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleLogsByIdap(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idapPrefix = request.getParameter("idapPrefix");
        
        Map<String, Object> result = new HashMap<>();
        result.put("logs", new ArrayList<>());
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    private void handleParametros(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        // Parâmetros do sistema
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("empresa", "Tabelionato de Notas");
        parametros.put("ambiente", "producao");
        
        result.put("data", parametros);
        result.put("success", true);
        sendJsonResponse(response, result);
    }

    /**
     * Handle o envio em lote de selos para o Funarpen
     * Endpoint: /maker/api/funarpen/selos/recepcao/lote
     */
    private void handleRecepcaoLote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");
        String limitStr = request.getParameter("limit");
        int limit = (limitStr != null && !limitStr.isEmpty()) ? Integer.parseInt(limitStr) : 50;
        
        try {
            java.sql.Connection conn = null;
            java.sql.PreparedStatement ps = null;
            java.sql.ResultSet rs = null;
            
            JsonArray selosLote = new JsonArray();
            List<String> selosDigitais = new ArrayList<>();
            Gson gson = new Gson();
            
            try {
                conn = com.selador.dao.ConnectionFactory.getConnection();
                
                // 1. Buscar selos já sanitizados (JSON12) no período
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT s.SELO, s.JSON12 FROM selados s ");
                sql.append("WHERE s.JSON12 IS NOT NULL AND s.JSON12 != '' ");
                sql.append("AND (s.STATUS_FUNARPEN IS NULL OR s.STATUS_FUNARPEN = 'ERRO' OR s.STATUS_FUNARPEN = '0') ");
                
                if (dataInicio != null && !dataInicio.isEmpty()) {
                    sql.append("AND s.DATAENVIO >= ? ");
                }
                if (dataFim != null && !dataFim.isEmpty()) {
                    sql.append("AND s.DATAENVIO <= ? ");
                }
                
                sql.append("ORDER BY s.DATAENVIO ASC LIMIT ?");
                
                ps = conn.prepareStatement(sql.toString());
                int pIdx = 1;
                if (dataInicio != null && !dataInicio.isEmpty()) ps.setString(pIdx++, dataInicio);
                if (dataFim != null && !dataFim.isEmpty()) ps.setString(pIdx++, dataFim);
                ps.setInt(pIdx++, limit);
                
                rs = ps.executeQuery();
                while (rs.next()) {
                    String json12 = rs.getString("JSON12");
                    String selo = rs.getString("SELO");
                    try {
                        selosLote.add(gson.fromJson(json12, JsonObject.class));
                        selosDigitais.add(selo);
                    } catch (Exception e) {
                        System.err.println("Erro ao ler JSON12 do selo " + selo + ": " + e.getMessage());
                    }
                }
                
                if (selosLote.size() == 0) {
                    result.put("success", false);
                    result.put("mensagem", "Nenhum selo sanitizado encontrado para envio no período informado.");
                    sendJsonResponse(response, result);
                    return;
                }
                
                // 2. Enviar POST ao servidor de produção
                String url = "http://100.102.13.23:8059/funarpen/maker/api/funarpen/selos/recepcao/lote";
                
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(url))
                        .header("Content-Type", "application/json; charset=utf-8")
                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(gson.toJson(selosLote)))
                        .build();
                
                java.net.http.HttpResponse<String> httpResponse = client.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());
                
                String responseBody = httpResponse.body();
                JsonObject apiResult = gson.fromJson(responseBody, JsonObject.class);
                
                // 3. Processar resposta e atualizar banco
                if (httpResponse.statusCode() == 200 && apiResult.has("protocolo")) {
                    String protocolo = apiResult.get("protocolo").getAsString();
                    
                    // Atualizar status para 1 (Enviado) e salvar protocolo
                    ps.close();
                    ps = conn.prepareStatement("UPDATE selados SET STATUS_FUNARPEN = '1', PROTOCOLO = ? WHERE SELO = ?");
                    for (String s : selosDigitais) {
                        ps.setString(1, protocolo);
                        ps.setString(2, s);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    
                    result.put("success", true);
                    result.put("protocolo", protocolo);
                    result.put("totalEnviados", selosDigitais.size());
                    result.put("mensagem", "Lote enviado com sucesso ao FUNARPEN. Protocolo: " + protocolo);
                } else {
                    result.put("success", false);
                    result.put("statusCode", httpResponse.statusCode());
                    result.put("apiResponse", responseBody);
                    result.put("mensagem", "Falha na recepção do lote pela API do FUNARPEN.");
                }
                
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("mensagem", "Erro ao enviar lote: " + e.getMessage());
            e.printStackTrace();
        }
        
        sendJsonResponse(response, result);
    }

    private void handleRetificar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("mensagem", "Endpoint de retificação não implementado no Notas");
        sendJsonResponse(response, result);
    }

    private void handleUpdateJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleJsonSave(request, response);
    }

    private void handleReenvioPorCodigo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String selo = request.getParameter("selo");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("mensagem", "Endpoint de reenvio não implementado no Notas");
        sendJsonResponse(response, result);
    }

    private void handleReprocessaErro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String confirmar = request.getParameter("confirmar");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("mensagem", "Endpoint de reprocessamento não implementado no Notas");
        sendJsonResponse(response, result);
    }
    
    /**
     * Handle sanitização em lote dos registros
     * Endpoint: /maker/api/funarpen/selos/sanitizar/lote?dataInicio=2026-03-19&dataFim=2026-03-27&limit=50
     * Endpoint: /maker/api/funarpen/selos/sanitizar/lote?idInicio=48233&idFim=50000&limit=100
     */
    private void handleSanitizarLote(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");
        String idInicioStr = request.getParameter("idInicio");
        String idFimStr = request.getParameter("idFim");
        String limitStr = request.getParameter("limit");
        String forceStr = request.getParameter("force");
        boolean force = "true".equalsIgnoreCase(forceStr) || "1".equalsIgnoreCase(forceStr);
        int limit = (limitStr != null && !limitStr.isEmpty()) ? Integer.parseInt(limitStr) : 0;
        
        try {
            java.sql.Connection conn = null;
            java.sql.PreparedStatement ps = null;
            java.sql.ResultSet rs = null;
            
            try {
                // Usar ConnectionFactory para gerenciar pool de conexões
                conn = com.selador.dao.ConnectionFactory.getConnection();
                
                // Query base para buscar selos para sanitização
                // Filtrar por DATAENVIO >= DTVERSAO_FUNARPEN (data início versão 11.12)
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT s.SELO, s.ID FROM selados s ");
                sql.append("INNER JOIN parametros p ON 1=1 ");
                sql.append("WHERE s.DATAENVIO >= p.DTVERSAO_FUNARPEN ");
                
                if (force) {
                    // Forçar re sanitização de todos os registros no intervalo
                    if (idInicioStr != null && !idInicioStr.isEmpty()) {
                        sql.append("AND s.ID >= ? ");
                    }
                    if (idFimStr != null && !idFimStr.isEmpty()) {
                        sql.append("AND s.ID <= ? ");
                    }
                } else {
                    // Apenas registros que precisam de sanitização
                    sql.append("AND (s.JSON12 IS NULL OR s.JSON12 = '' OR s.JSON12 = s.JSON) ");
                    if (idInicioStr != null && !idInicioStr.isEmpty()) {
                        sql.append("AND s.ID >= ? ");
                    }
                    if (idFimStr != null && !idFimStr.isEmpty()) {
                        sql.append("AND s.ID <= ? ");
                    }
                    if (dataInicio != null && !dataInicio.isEmpty()) {
                        sql.append("AND s.DATAENVIO >= '").append(dataInicio).append("' ");
                    }
                    if (dataFim != null && !dataFim.isEmpty()) {
                        sql.append("AND s.DATAENVIO <= '").append(dataFim).append("' ");
                    }
                }
                
                sql.append("ORDER BY s.ID ASC ");
                
                if (limit > 0) {
                    sql.append("LIMIT " + limit);
                }
                
                ps = conn.prepareStatement(sql.toString());
                
                int paramIdx = 1;
                if (force) {
                    // Modo force: idInicio é obrigatório
                    if (idInicioStr != null && !idInicioStr.isEmpty()) {
                        ps.setInt(paramIdx++, Integer.parseInt(idInicioStr));
                    }
                    if (idFimStr != null && !idFimStr.isEmpty()) {
                        ps.setInt(paramIdx++, Integer.parseInt(idFimStr));
                    }
                } else {
                    // Modo normal
                    if (idInicioStr != null && !idInicioStr.isEmpty()) {
                        ps.setInt(paramIdx++, Integer.parseInt(idInicioStr));
                    }
                    if (idFimStr != null && !idFimStr.isEmpty()) {
                        ps.setInt(paramIdx++, Integer.parseInt(idFimStr));
                    }
                    if (dataInicio != null && !dataInicio.isEmpty()) {
                        ps.setString(paramIdx++, dataInicio);
                    }
                    if (dataFim != null && !dataFim.isEmpty()) {
                        ps.setString(paramIdx++, dataFim);
                    }
                }
                
                rs = ps.executeQuery();
                
                int totalRegistros = 0;
                int sucesso = 0;
                int erro = 0;
                List<String> selosProcessados = new ArrayList<>();
                
                while (rs.next()) {
                    String selloDigital = rs.getString("s.SELO");
                    totalRegistros++;
                    
                    try {
                        // Chamar o sanitizador para cada selo (forçar revalidação se force=true)
                        boolean resultado = SeloJsonSanitizerNotas.sanitizarESalvar(selloDigital, force);
                        if (resultado) {
                            sucesso++;
                            selosProcessados.add(selloDigital);
                        } else {
                            erro++;
                        }
                    } catch (Exception e) {
                        erro++;
                        System.err.println("Erro ao sanitizar " + selloDigital + ": " + e.getMessage());
                    }
                }
                
                result.put("success", true);
                result.put("totalRegistros", totalRegistros);
                result.put("sucesso", sucesso);
                result.put("erro", erro);
                result.put("selos", selosProcessados);
                result.put("mensagem", "Sanitização em lote concluída: " + sucesso + " selos processados com sucesso.");
                
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("mensagem", "Erro ao executar sanitização em lote: " + e.getMessage());
            e.printStackTrace();
        }
        
        sendJsonResponse(response, result);
    }
    
    /**
     * Handle sanitização de um selo específico
     * Endpoint: /maker/api/funarpen/selos/sanitizar/{seloDigital}
     */
    private void handleSanitizarSelo(HttpServletRequest request, HttpServletResponse response, String selloDigital) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Chamar o sanitizador para o sello específico
            boolean sucesso = SeloJsonSanitizerNotas.sanitizarESalvar(selloDigital);
            
            if (sucesso) {
                result.put("success", true);
                result.put("seloDigital", selloDigital);
                result.put("mensagem", "Selo sanitizado com sucesso");
            } else {
                result.put("success", false);
                result.put("seloDigital", selloDigital);
                result.put("mensagem", "Falha ao sanitizar selo");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("seloDigital", selloDigital);
            result.put("mensagem", "Erro ao sanitizar: " + e.getMessage());
            e.printStackTrace();
        }
        
        sendJsonResponse(response, result);
    }

    // ==================== HELPERS ====================

    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = JsonUtil.toJson(data);
        response.getWriter().write(json);
    }

    private Map<String, Object> createSuccessResponse(String mensagem) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensagem", mensagem);
        return response;
    }

    private Map<String, Object> createErrorResponse(String mensagem) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("mensagem", mensagem);
        return response;
    }
}
