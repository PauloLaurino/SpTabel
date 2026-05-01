package com.selador.web.servlets;

import com.selador.service.ConfigService;
import com.selador.util.JsonUtil;
import com.monitor.funarpen.util.DbUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet para compatibilidade com API do Protesto (Funarpen)
 * Endpoint: /maker/api/funarpen/parametros
 */
@WebServlet(name = "FunarpenParametrosServlet", urlPatterns = {"/maker/api/funarpen/parametros"})
public class FunarpenParametrosServlet extends HttpServlet {

    private ConfigService configService;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.configService = ConfigService.getInstance();
        } catch (Exception e) {
            this.configService = null;
            System.err.println("FunarpenParametrosServlet: Erro ao inicializar ConfigService: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Parâmetros do sistema - busca da tabela parametros
            Map<String, Object> parametros = new HashMap<>();
            
            // Buscar dados reais da tabela parametros
            try (Connection conn = DbUtil.getConnection()) {
                String sql = "SELECT * FROM parametros WHERE CODIGO_PAR = 1";
                try (PreparedStatement pstmt = conn.prepareStatement(sql);
                     ResultSet rs = pstmt.executeQuery()) {
                    
                    if (rs.next()) {
                        parametros.put("codigoPar", rs.getInt("CODIGO_PAR"));
                        parametros.put("docPar", rs.getString("DOC_PAR"));
                        parametros.put("nomeTabelionato", rs.getString("NOME_TABELIONATO_PAR"));
                        parametros.put("titular", rs.getString("TITULAR_PAR"));
                        parametros.put("tabeliao", rs.getString("TABELIAO_PAR"));
                        parametros.put("codigoOficio", rs.getString("CODTABEL_PAR"));
                        parametros.put("naturezas", rs.getString("SELOS_PAR"));
                        parametros.put("ambiente", rs.getString("AMBIENTE_PAR"));
                        parametros.put("contexto", rs.getString("CONTEXTO_PAR"));
                        parametros.put("dtVersaoFunarpen", rs.getString("DTVERSAO_FUNARPEN"));
                        
                        // ISS
                        parametros.put("issPar", rs.getObject("ISS_PAR") != null ? rs.getDouble("ISS_PAR") : 0.05);
                        
                        // Campos de compatibilidade para o React
                        Map<String, Object> selosPar = new HashMap<>();
                        selosPar.put("naturezas", rs.getString("SELOS_PAR"));
                        parametros.put("SELOS_PAR", selosPar);
                        
                        // Usuário padrão
                        parametros.put("usuario", rs.getString("USUARIO_PAR"));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar parametros: " + e.getMessage());
                // Fallback para valores padrão
                parametros.put("empresa", "Tabelionato de Notas");
                parametros.put("ambiente", "producao");
                parametros.put("versao", "1.0.0");
            }
            
            // Adicionar valores fixos
            parametros.put("ambiente", "producao");
            parametros.put("versao", "1.0.0");
            
            result.put("data", parametros);
            result.put("success", true);
            
            sendJsonResponse(response, result);
            
        } catch (Exception e) {
            System.err.println("FunarpenParametrosServlet Error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensagem", "Erro interno: " + e.getMessage());
            sendJsonResponse(response, error);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = JsonUtil.toJson(data);
        response.getWriter().write(json);
    }
}
