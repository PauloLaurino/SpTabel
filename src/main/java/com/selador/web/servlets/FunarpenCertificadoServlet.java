package com.selador.web.servlets;

import com.selador.service.ConfigService;
import com.monitor.funarpen.util.DbUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Retorna informações do certificado digital configurado (PFX/A3).
 * Endpoint: GET /maker/api/funarpen/certificado
 *
 * Resposta JSON:
 * {
 *   "valid":       true|false,
 *   "subject":     "CN=...",
 *   "notBefore":   "2024-01-01T00:00:00Z",
 *   "notAfter":    "2026-12-31T00:00:00Z",
 *   "matchesDoc":  true,
 *   "matchesName": true,
 *   "message":     "..."
 * }
 */
@WebServlet(name = "FunarpenCertificadoServlet", urlPatterns = {"/maker/api/funarpen/certificado"})
public class FunarpenCertificadoServlet extends HttpServlet {

    private ConfigService configService;

    @Override
    public void init() throws ServletException {
        configService = ConfigService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Cache-Control", "no-cache");

        try (PrintWriter out = resp.getWriter()) {
            CertInfo info = loadCertificateInfo();
            out.print(info.toJson());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(204);
    }

    // ------------------------------------------------------------------
    // Carrega informações do certificado
    // ------------------------------------------------------------------
    private CertInfo loadCertificateInfo() {
        CertInfo info = new CertInfo();

        try {
            // 1) Tenta carregar o cert configurado (pfxPath / pfxPassword)
            String pfxPath = configService.getString("pfxPath", null);
            String pfxPass = configService.getString("pfxPassword", null);

            if (pfxPath == null || pfxPath.isEmpty()) {
                // Tenta buscar do banco de dados (tabela parametros)
                pfxPath = getParamFromDb("pfxPath");
                pfxPass = getParamFromDb("pfxPassword");
            }

            if (pfxPath == null || pfxPath.isEmpty()) {
                info.valid   = false;
                info.message = "Certificado não configurado";
                return info;
            }

            File pfxFile = new File(pfxPath);
            if (!pfxFile.exists()) {
                info.valid   = false;
                info.message = "Arquivo de certificado não encontrado: " + pfxPath;
                return info;
            }

            // Carrega o KeyStore
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] pass = (pfxPass != null ? pfxPass : "").toCharArray();
            try (InputStream is = new FileInputStream(pfxFile)) {
                ks.load(is, pass);
            }

            // Pega o primeiro certificado encontrado
            String alias = ks.aliases().nextElement();
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

            if (cert == null) {
                info.valid   = false;
                info.message = "Nenhum certificado no arquivo";
                return info;
            }

            // Preenche as informações
            Date now = new Date();
            info.subject   = cert.getSubjectDN().getName();
            info.notBefore = iso8601(cert.getNotBefore());
            info.notAfter  = iso8601(cert.getNotAfter());
            info.valid     = now.after(cert.getNotBefore()) && now.before(cert.getNotAfter());

            // Verifica se o CN bate com o titular configurado
            String titular = configService.getString("tabeliao", null);
            if (titular == null || titular.isEmpty()) titular = getParamFromDb("tabeliao");
            
            if (titular != null && !titular.isEmpty()) {
                // Limpeza básica: remove "Tabeli:", "Tabeliao:", "Escrevente:", "CPF", hífens e números
                String titularLimpo = titular.split(":")[titular.contains(":") ? 1 : 0]
                                      .split("-")[0]
                                      .replaceAll("[0-9]", "")
                                      .replace(".", "")
                                      .trim();
                                      
                info.matchesName = info.subject.toLowerCase().contains(titularLimpo.toLowerCase());
                
                if (info.valid && !info.matchesName) {
                    info.valid = false;
                    info.message = "Titular n\u00e3o confere: " + titularLimpo;
                }
            }

            if (!info.valid && info.message == null) {
                info.message = info.notAfter != null && now.after(cert.getNotAfter())
                        ? "Certificado expirado"
                        : "Certificado ainda não válido";
            }

        } catch (Exception e) {
            info.valid   = false;
            info.message = "Erro ao carregar certificado: " + e.getMessage();
        }

        return info;
    }

    private String getParamFromDb(String key) {
        try (Connection conn = DbUtil.getConnection()) {
            // A tabela 'parametros' no Notas tem apenas uma linha (CODIGO_PAR=1) com várias colunas
            String column = "";
            if ("pfxPath".equals(key)) column = "CAMINHOCERTIFICADO_PAR";
            else if ("pfxPassword".equals(key)) column = "SENHACERTIFICADO_PAR";
            else if ("tabeliao".equals(key)) column = "TITULAR_PAR"; // ou TABELIAO_PAR
            
            if (column.isEmpty()) return null;

            String sql = "SELECT " + column + " FROM parametros WHERE CODIGO_PAR = 1";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (Exception e) {
            System.err.println("FunarpenCertificadoServlet: Erro ao buscar parametro " + key + ": " + e.getMessage());
        }
        return null;
    }

    private static String iso8601(Date d) {
        if (d == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(d);
    }

    // ------------------------------------------------------------------
    // DTO simples
    // ------------------------------------------------------------------
    private static class CertInfo {
        boolean valid       = false;
        Boolean matchesDoc  = null;
        Boolean matchesName = null;
        String  subject     = null;
        String  notBefore   = null;
        String  notAfter    = null;
        String  message     = null;

        String toJson() {
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"valid\":").append(valid);
            if (matchesDoc  != null) sb.append(",\"matchesDoc\":").append(matchesDoc);
            if (matchesName != null) sb.append(",\"matchesName\":").append(matchesName);
            if (subject   != null)  sb.append(",\"subject\":\"").append(esc(subject)).append('"');
            if (notBefore != null)  sb.append(",\"notBefore\":\"").append(notBefore).append('"');
            if (notAfter  != null)  sb.append(",\"notAfter\":\"").append(notAfter).append('"');
            if (message   != null)  sb.append(",\"message\":\"").append(esc(message)).append('"');
            sb.append('}');
            return sb.toString();
        }

        private static String esc(String s) {
            return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }
}
