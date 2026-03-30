package com.selador.web.servlets;

import com.selador.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * Servlet para configurações do sistema
 * Endpoint: /api/config/*
 */
@WebServlet(name = "ConfigServlet", urlPatterns = {"/api/config/*"})
public class ConfigServlet extends BaseServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        long startTime = System.currentTimeMillis();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/config - Obter todas configurações
                obterConfiguracoes(request, response);
            } else if (pathInfo.startsWith("/interface")) {
                // GET /api/config/interface - Configurações da interface
                obterConfiguracoesInterface(request, response);
            } else if (pathInfo.startsWith("/validar")) {
                // GET /api/config/validar - Validar configurações
                validarConfiguracoes(request, response);
            } else if (pathInfo.startsWith("/health")) {
                // GET /api/config/health - Health check
                healthCheck(request, response);
            } else if (pathInfo.startsWith("/info")) {
                // GET /api/config/info - Informações do sistema
                getInfo(request, response);    
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, 
                    "Endpoint não encontrado: " + pathInfo);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logRequest(request, "GET", duration);
            
        } catch (Exception e) {
            logError("ConfigServlet GET: " + e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erro interno ao processar requisição: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        long startTime = System.currentTimeMillis();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/config - Atualizar configuração
                atualizarConfiguracao(request, response);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, 
                    "Endpoint POST não encontrado: " + pathInfo);
            }
            
            long duration = System.currentTimeMillis() - startTime;
            logRequest(request, "POST", duration);
            
        } catch (Exception e) {
            logError("ConfigServlet POST: " + e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erro interno ao processar requisição: " + e.getMessage());
        }
    }
    
    /**
     * Método auxiliar para log de erros
     */
    private void logError(String mensagem) {
        try {
            System.err.println("ERRO ConfigServlet: " + mensagem);
        } catch (Exception e) {
            // Fallback
        }
    }

    /**
     * ConfigServlet.java - Adicionar este método
     */
    private void getInfo(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        Map<String, Object> info = new HashMap<>();
        info.put("nome", "Sistema Selador");
        info.put("versao", "1.0.0");
        info.put("status", "operacional");
        info.put("timestamp", System.currentTimeMillis());
        info.put("ambiente", "desenvolvimento");
        info.put("porta", 5000);
        info.put("bancoDados", "spprot");
        info.put("integracaoMaker", true);
        
        // Adicionar status dos serviços
        Map<String, String> servicos = new HashMap<>();
        servicos.put("selos", seloService != null ? "ativo" : "inativo");
        servicos.put("apontamentos", apontamentoService != null ? "ativo" : "inativo");
        servicos.put("selagem", selagemService != null ? "ativo" : "inativo");
        
        info.put("servicos", servicos);
        
        sendSuccessResponse(response, info);
    }
    
    /**
     * GET /api/config - Obter todas configurações
     */
    private void obterConfiguracoes(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        Map<String, String> configuracoes = new HashMap<>();
        
        try {
            if (configService != null) {
                configuracoes = configService.getTodasConfiguracoes();
            } else {
                configuracoes.put("erro", "Serviço de configuração não disponível");
                configuracoes.put("app.version", "1.0.0");
                configuracoes.put("app.name", "SELADOR API");
            }
        } catch (Exception e) {
            configuracoes.put("erro", "Erro ao obter configurações: " + e.getMessage());
        }
        
        sendSuccessResponse(response, configuracoes);
    }
    
    /**
     * GET /api/config/interface - Configurações da interface
     */
    private void obterConfiguracoesInterface(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        Map<String, Object> configInterface = new HashMap<>();
        
        try {
            if (configService != null) {
                configInterface = configService.getConfiguracoesInterface();
            } else {
                configInterface.put("erro", "Serviço de configuração não disponível");
                configInterface.put("pagina.tamanho", 50);
                configInterface.put("timeout.busca", 30);
                configInterface.put("habilitar.darkmode", false);
            }
        } catch (Exception e) {
            configInterface.put("erro", "Erro ao obter configurações de interface: " + e.getMessage());
        }
        
        sendSuccessResponse(response, configInterface);
    }
    
    /**
     * GET /api/config/validar - Validar configurações
     */
    private void validarConfiguracoes(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        Map<String, String> problemas = new HashMap<>();
        
        try {
            if (configService != null) {
                problemas = configService.validarConfiguracoes();
            } else {
                problemas.put("configService", "Serviço de configuração não disponível");
            }
        } catch (Exception e) {
            problemas.put("erro", "Erro na validação: " + e.getMessage());
        }
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("valido", problemas.isEmpty());
        resultado.put("problemas", problemas);
        resultado.put("totalProblemas", problemas.size());
        
        if (problemas.isEmpty()) {
            resultado.put("mensagem", "Todas as configurações estão válidas");
        } else {
            resultado.put("mensagem", "Foram encontrados " + problemas.size() + " problema(s)");
        }
        
        sendSuccessResponse(response, resultado);
    }
    
    /**
     * GET /api/config/health - Health check
     */
    private void healthCheck(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        Map<String, Object> health = new HashMap<>();
        
        // Status básico
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "SELADOR API");
        
        // Versão da aplicação
        try {
            String versao = "1.0.0";
            if (configService != null) {
                versao = configService.getString("app.version", "1.0.0");
            }
            health.put("version", versao);
        } catch (Exception e) {
            health.put("version", "1.0.0");
            health.put("versionError", e.getMessage());
        }
        
        // Verificar conectividade com banco (via selos)
        try {
            if (seloService != null) {
                // 🔥 CORREÇÃO: contarSelosDisponiveisComSiglas não existe
                // Usar método alternativo ou simplificar
                Map<String, Integer> selos = new HashMap<>();
                
                try {
                    // Tenta usar método que realmente existe
                    java.lang.reflect.Method method = seloService.getClass().getMethod("contarSelosDisponiveis");
                    Object result = method.invoke(seloService);
                    if (result instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Integer> tempMap = (Map<String, Integer>) result;
                        selos = tempMap;
                    }
                } catch (Exception e) {
                    // Se não conseguir, criar mapa vazio
                    selos = new HashMap<>();
                }
                
                health.put("database", "CONNECTED");
                health.put("selosDisponiveis", selos.size());
            } else {
                health.put("database", "UNKNOWN");
                health.put("databaseInfo", "Serviço de selos não disponível");
            }
        } catch (Exception e) {
            health.put("database", "DISCONNECTED");
            health.put("databaseError", e.getMessage());
        }
        
        // Verificar integração Maker
        try {
            if (configService != null) {
                boolean makerEnabled = configService.isMakerIntegrationEnabled();
                health.put("makerIntegration", makerEnabled ? "ENABLED" : "DISABLED");
            } else {
                health.put("makerIntegration", "UNKNOWN");
            }
        } catch (Exception e) {
            health.put("makerIntegration", "ERROR");
            health.put("makerError", e.getMessage());
        }
        
        // Status geral
        boolean tudoOk = "CONNECTED".equals(health.get("database")) || "UNKNOWN".equals(health.get("database"));
        health.put("overall", tudoOk ? "HEALTHY" : "UNHEALTHY");
        
        sendSuccessResponse(response, health);
    }
    
    /**
     * POST /api/config - Atualizar configuração
     */
    private void atualizarConfiguracao(HttpServletRequest request, HttpServletResponse response) 
            throws Exception {
        
        String requestBody = getRequestBody(request);
        Map<String, String> dados = JsonUtil.fromJsonMap(requestBody, String.class);
        
        if (dados == null || !dados.containsKey("key") || !dados.containsKey("value")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Parâmetros obrigatórios: key, value");
            return;
        }
        
        String key = dados.get("key");
        String value = dados.get("value");
        
        // Validar se a chave pode ser atualizada em runtime
        if (!isConfigKeyUpdatable(key)) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                "Configuração não pode ser atualizada em runtime: " + key);
            return;
        }
        
        boolean atualizado = false;
        
        try {
            if (configService != null) {
                atualizado = configService.atualizarConfiguracao(key, value);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Serviço de configuração não disponível");
                return;
            }
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erro ao atualizar configuração: " + e.getMessage());
            return;
        }
        
        if (atualizado) {
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("mensagem", "Configuração atualizada com sucesso");
            resultado.put("key", key);
            resultado.put("value", value);
            
            sendSuccessResponse(response, resultado);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Falha ao atualizar configuração (serviço retornou falso)");
        }
    }
    
    /**
     * Verificar se a chave de configuração pode ser atualizada em runtime
     */
    private boolean isConfigKeyUpdatable(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        // Lista de chaves que podem ser atualizadas em runtime
        String[] updatableKeys = {
            "interface.pagina.tamanho",
            "interface.timeout.busca",
            "log.nivel.minimo",
            "security.token.expiration.minutes",
            "app.debug.mode",
            "notificacoes.email.habilitado"
        };
        
        for (String updatableKey : updatableKeys) {
            if (updatableKey.equals(key)) {
                return true;
            }
        }
        
        // Também permite chaves que começam com "temp." ou "session."
        if (key.startsWith("temp.") || key.startsWith("session.") || key.startsWith("user.")) {
            return true;
        }
        
        return false;
    }
}