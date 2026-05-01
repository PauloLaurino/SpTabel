package com.seprocom.gerencial.service;

import com.seprocom.gerencial.dto.ParametrosDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeloService {

    private final JdbcTemplate jdbcTemplate;
    private final ParametrosService parametrosService;

    /**
     * Conta selos disponíveis por tipo no banco de dados atual
     */
    public Map<String, Integer> contarEstoqueDisponivel() {
        String sql = "SELECT st.sigle as tiposelo_label, COUNT(*) as total " +
                     "FROM selos s " +
                     "JOIN selo_tipo st ON s.tiposelo_sel = st.tipo " +
                     "WHERE s.tipo_sel = '0000' " +
                     "AND s.versao = '112' " +
                     "GROUP BY st.sigle " +
                     "ORDER BY st.sigle";
        
        Map<String, Integer> resultado = new LinkedHashMap<>();
        
        jdbcTemplate.query(sql, rs -> {
            resultado.put(rs.getString("tiposelo_label"), rs.getInt("total"));
        });
        
        return resultado;
    }

    /**
     * Valida o certificado digital configurado no banco de dados atual
     */
    public Map<String, Object> validarCertificado() {
        Map<String, Object> info = new HashMap<>();
        info.put("valid", false);
        info.put("matchesName", false);

        try {
            ParametrosDTO params = parametrosService.buscarParametros();
            
            // No banco, os campos são CAMINHOCERTIFICADO_PAR e SENHACERTIFICADO_PAR
            // No DTO atual do Gerencial, precisamos mapear ou buscar via JDBC se não houver no DTO
            String pfxPath = params.getObsFunarpen(); // Usando campo temporário se necessário ou busca direta
            
            // Busca direta via JDBC para garantir os campos específicos
            Map<String, Object> dbParams = jdbcTemplate.queryForMap(
                "SELECT CAMINHOCERTIFICADO_PAR, SENHACERTIFICADO_PAR, TITULAR_PAR FROM parametros WHERE CODIGO_PAR = 1");
            
            pfxPath = (String) dbParams.get("CAMINHOCERTIFICADO_PAR");
            String pfxPass = (String) dbParams.get("SENHACERTIFICADO_PAR");
            String titular = (String) dbParams.get("TITULAR_PAR");

            if (pfxPath == null || pfxPath.isEmpty()) {
                info.put("message", "Certificado não configurado no banco");
                return info;
            }

            File pfxFile = new File(pfxPath);
            if (!pfxFile.exists()) {
                info.put("message", "Arquivo não encontrado: " + pfxPath);
                return info;
            }

            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] pass = (pfxPass != null ? pfxPass : "").toCharArray();
            try (InputStream is = new FileInputStream(pfxFile)) {
                ks.load(is, pass);
            }

            String alias = ks.aliases().nextElement();
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

            if (cert != null) {
                Date now = new Date();
                String subject = cert.getSubjectDN().getName();
                info.put("subject", subject);
                info.put("notAfter", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(cert.getNotAfter()));
                boolean valid = now.after(cert.getNotBefore()) && now.before(cert.getNotAfter());
                info.put("valid", valid);

                if (titular != null && !titular.isEmpty()) {
                    info.put("matchesName", subject.toLowerCase().contains(titular.toLowerCase()));
                }
                
                if (!valid) {
                    info.put("message", now.after(cert.getNotAfter()) ? "Certificado expirado" : "Ainda não válido");
                }
            }

        } catch (Exception e) {
            log.error("Erro ao validar certificado", e);
            info.put("message", "Erro: " + e.getMessage());
        }

        return info;
    }
}
