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
                        parametros.put("nomeTabeliao", rs.getString("NOME_TABELIAO"));
                        parametros.put("codigoTabeliao", rs.getString("CODIGO_TABELIAO"));
                        parametros.put("codigoCartorio", rs.getString("CODIGO_CARTORIO"));
                        parametros.put("nomeCartorio", rs.getString("NOME_CARTORIO"));
                        parametros.put("cnpjCartorio", rs.getString("CNPJ_CARTORIO"));
                        parametros.put("inscricaoEstadual", rs.getString("INSCRICAO_ESTADUAL"));
                        parametros.put("enderecoCartorio", rs.getString("ENDERECO_CARTORIO"));
                        parametros.put("telefoneCartorio", rs.getString("TELEFONE_CARTORIO"));
                        parametros.put("emailCartorio", rs.getString("EMAIL_CARTORIO"));
                        parametros.put("ativa", rs.getString("ATIVA"));
                        parametros.put("dtVersaoFunarpen", rs.getString("DTVERSAO_FUNARPEN"));
                        parametros.put("obsFunarpen", rs.getString("OBS_FUNARPEN"));
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
