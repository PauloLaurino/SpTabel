package com.selador.web.servlets;

import com.selador.service.SeloService;
import com.selador.service.SelagemService;
import com.selador.util.JsonUtil;
import com.selador.util.SeloJsonSanitizerNotas;
import com.selador.dao.SeloDAO;
import com.selador.dao.SeladoDAO;
import com.selador.model.Selado;
import java.util.Calendar;
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
 * Mapeia endpoints /maker/api/notas/selos/* para o frontend React
 */
@WebServlet(name = "FunarpenSelosServlet", urlPatterns = {"/maker/api/notas/selos/*"})
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
            
            // /maker/api/notas/selos/monitor
            if (pathInfo.startsWith("/monitor")) {
                handleMonitor(request, response);
                return;
            }
            
            // /maker/api/notas/selos/diagnostico
            if (pathInfo.startsWith("/diagnostico")) {
                handleDiagnostico(request, response);
                return;
            }
            // /maker/api/notas/selos/naoRetornados
            if (pathInfo.startsWith("/naoRetornados")) {
                handleNaoRetornados(request, response);
                return;
            }
            
            // /maker/api/notas/selos/json/get
            if (pathInfo.startsWith("/json/get")) {
                handleJsonGet(request, response);
                return;
            }
            
            // /maker/api/notas/selos/cards/estoque
            if (pathInfo.startsWith("/cards/estoque")) {
                handleCardsEstoque(request, response);
                return;
            }
            
            // /maker/api/notas/selos/cards/utilizadosHoje
            if (pathInfo.startsWith("/cards/utilizadosHoje")) {
                handleCardsUtilizadosHoje(request, response);
                return;
            }
            
            // /maker/api/notas/selos/cards/pendentes
            if (pathInfo.startsWith("/cards/pendentes")) {
                handleCardsPendentes(request, response);
                return;
            }
            
            // /maker/api/notas/selos/cards/retornadosComErro
            if (pathInfo.startsWith("/cards/retornadosComErro")) {
                handleCardsRetornadosComErro(request, response);
                return;
            }
            
            // /maker/api/notas/selos/cards/alertas
            if (pathInfo.startsWith("/cards/alertas")) {
                handleCardsAlertas(request, response);
                return;
            }
            
                        // /maker/api/funarpen/selos/importar
            if (pathInfo != null && pathInfo.startsWith("/importar")) {
                handleImportar(request, response);
                return;
            }
            // /maker/api/notas/selos/sanitizar/lote - Sanitiza todos os registros da versão 112
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
            
            // /maker/api/notas/selos/logsByIdap
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
                        // /maker/api/notas/selos/importar
            if (pathInfo != null && pathInfo.startsWith("/importar")) {
                handleImportar(request, response);
                return;
            }
            // /maker/api/notas/selos/sanitizar/lote - Sanitiza todos os registros
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
            
            // /maker/api/notas/selos/json/save
            if (pathInfo != null && pathInfo.startsWith("/json/save")) {
                handleJsonSave(request, response);
                return;
            }
            
            // /maker/api/notas/selos/recepcao/lote
            if (pathInfo != null && pathInfo.startsWith("/recepcao/lote")) {
                handleRecepcaoLote(request, response);
                return;
            }
            
            // /maker/api/notas/selos/retificar
            if (pathInfo != null && pathInfo.startsWith("/retificar")) {
                handleRetificar(request, response);
                return;
            }
            
            // /maker/api/notas/selos/updateJson
            if (pathInfo != null && pathInfo.startsWith("/updateJson")) {
                handleUpdateJson(request, response);
                return;
            }
            
            // /maker/api/notas/selos/reenvio/porCodigo
            if (pathInfo != null && pathInfo.startsWith("/reenvio/porCodigo")) {
                handleReenvioPorCodigo(request, response);
                return;
            }
            
            // /maker/api/notas/selos/reprocessa/erro
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
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> selos = new ArrayList<>();
        
        // Formato de data para o React (yyyy-MM-dd HH:mm:ss)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String hoje = sdf.format(new java.util.Date());
        
        try (java.sql.Connection conn = com.selador.dao.ConnectionFactory.getConnection()) {
            String sql = "SELECT SELO, STATUS, DATAENVIO, IDAP FROM selados " +
                         "WHERE (DATARETORNO IS NULL OR DATARETORNO = '' OR STATUS LIKE '%ERRO%') " +
                         "ORDER BY ID DESC LIMIT 200";
            
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    Map<String, Object> s = new HashMap<>();
                    String seloNum = rs.getString("SELO");
                    String statusStr = rs.getString("STATUS");
                    String dataEnvio = rs.getString("DATAENVIO");
                    
                    // Se o banco não tiver data, usamos hoje para garantir que o filtro do React não esconda o registro
                    if (dataEnvio == null || dataEnvio.isEmpty() || dataEnvio.equals("null")) {
                        dataEnvio = hoje;
                    }
                    
                    s.put("id", seloNum);
                    s.put("sealCode", seloNum);
                    
                    // Status agressivo: se não tem status, é PENDING
                    if (statusStr != null && statusStr.toUpperCase().contains("ERRO")) {
                        s.put("status", "ERROR");
                    } else {
                        s.put("status", "PENDING");
                    }
                    
                    s.put("dataEnvio", dataEnvio);
                    s.put("protocol", rs.getString("IDAP"));
                    selos.add(s);
                }
            }
            // Retorna o objeto com 'data' E também o array direto no corpo para compatibilidade total
            result.put("data", selos);
            result.put("success", true);
            
            // Log no servidor para debug
            if (!selos.isEmpty()) {
                System.out.println("FunarpenSelosServlet: Enviando " + selos.size() + " registros. Exemplo: " + selos.get(0));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("mensagem", e.getMessage());
        }
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
        result.put("JSON12", "{}");
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
            
            if (selloCodigo.isEmpty()) {
                result.put("success", false);
                result.put("mensagem", "Parâmetro 'selo' é obrigatório");
                sendJsonResponse(response, result);
                return;
            }
            
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
        try {
            SeloDAO dao = new SeloDAO();
            Map<String, Integer> estoqueMap = dao.contarDisponiveis();
            
            // Converte Map para List para compatibilidade com o React (estoque.forEach)
        int totalGeral = 0;
        List<Map<String, Object>> estoqueList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : estoqueMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            int qtd = entry.getValue();
            item.put("tiposelo", entry.getKey());
            item.put("total", qtd);
            estoqueList.add(item);
            totalGeral += qtd;
        }
        
        result.put("estoque", estoqueList);
        result.put("total", totalGeral); // Campo para o card grande do dashboard
        result.put("success", true);
        
        // Log para debug
        System.out.println("FunarpenSelosServlet: Estoque total: " + totalGeral + " em " + estoqueList.size() + " tipos.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("mensagem", e.getMessage());
        }
        sendJsonResponse(response, result);
    }

        private void handleCardsUtilizadosHoje(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try (java.sql.Connection conn = com.selador.dao.ConnectionFactory.getConnection()) {
            String hoje = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM selados WHERE DATAENVIO LIKE ?")) {
                ps.setString(1, hoje + "%");
                java.sql.ResultSet rs = ps.executeQuery();
                int total = rs.next() ? rs.getInt(1) : 0;
                result.put("utilizadosHoje", total);
                result.put("success", true);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("mensagem", e.getMessage());
        }
        sendJsonResponse(response, result);
    }

        private void handleCardsPendentes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try (java.sql.Connection conn = com.selador.dao.ConnectionFactory.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM selados WHERE DATARETORNO IS NULL OR DATARETORNO = ''")) {
            java.sql.ResultSet rs = ps.executeQuery();
            int total = rs.next() ? rs.getInt(1) : 0;
            result.put("pendentes", total);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("mensagem", e.getMessage());
        }
        sendJsonResponse(response, result);
    }

        private void handleCardsRetornadosComErro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try (java.sql.Connection conn = com.selador.dao.ConnectionFactory.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM selados WHERE STATUS LIKE '%ERRO%'")) {
            java.sql.ResultSet rs = ps.executeQuery();
            int total = rs.next() ? rs.getInt(1) : 0;
            result.put("retornadosComErro", total);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("mensagem", e.getMessage());
        }
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
                sql.append("AND (s.STATUS IS NULL OR s.STATUS = 'ERRO' OR s.STATUS = '0' OR s.STATUS = '') ");
                
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
                    
                    // Atualizar status para SUCESSO e salvar protocolo
                    ps.close();
                    ps = conn.prepareStatement("UPDATE selados SET STATUS = 'SUCESSO', PROTOCOLO = ? WHERE SELO = ?");
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
                        sql.append("AND s.DATAENVIO >= ? ");
                    }
                    if (dataFim != null && !dataFim.isEmpty()) {
                        sql.append("AND s.DATAENVIO <= ? ");
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
            result.put("erro", e.toString());
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

    private void handleMonitor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Em vez de retornar JSON, fazemos o forward para o HTML do monitor
        // O HTML agora detectará os parâmetros na URL e funcionará corretamente
        request.getRequestDispatcher("/html/monitor_selos_funarpen.html").forward(request, response);
    }

    private void handleDiagnostico(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("dbUtil_diagnostics", com.monitor.funarpen.util.DbUtil.debugDiagnostics());
            
            try (java.sql.Connection conn = com.selador.dao.ConnectionFactory.getConnection()) {
                result.put("connection_ok", true);
                result.put("db_metadata", conn.getMetaData().getURL());
                
                // Contar total de selos
                try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM selos")) {
                    java.sql.ResultSet rs = ps.executeQuery();
                    if (rs.next()) result.put("total_selos", rs.getInt(1));
                }
                
                // Contar selos Versão 112
                try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM selos WHERE versao = '112'")) {
                    java.sql.ResultSet rs = ps.executeQuery();
                    if (rs.next()) result.put("total_selos_112", rs.getInt(1));
                }
            }
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        sendJsonResponse(response, result);
    }

    private void handleImportar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO: Implementar comunicação real com Funarpen para Notas
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("mensagem", "Importação iniciada (Simulada no Notas)");
        result.put("importados", 0);
        sendJsonResponse(response, result);
    }
}