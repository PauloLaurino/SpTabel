package com.selador.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Sanitizador de emergência rodando a partir do Notas para o Protesto (Ato 730).
 * IP: 100.102.13.23 | Banco: spprot
 */
public class SanitizarProtesto730 {
    private static final ObjectMapper M = new ObjectMapper();

    public static void main(String[] args) {
        String url = "jdbc:mariadb://100.102.13.23:3306/spprot?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = "k15720";

        String[] selos = {
            "SFTP1.FqERo.Mw4wC-MRNJM.1122q",
            "SFTP1.FqFRo.Mw4wC-CRtJM.1122q",
            "SFTP1.FqIRo.Mw4wC-KRtJM.1122q",
            "SFTP1.FqjRo.Mw4wC-ZRNJM.1122q",
            "SFTP1.FqKRo.Mw4wC-UR6JM.1122q",
            "SFTP1.FqkRo.Mw4wC-4R3JM.1122q"
        };

        System.out.println("### INICIANDO SANITIZAÇÃO DE EMERGÊNCIA (NOTAS -> PROTESTO) ###");

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            for (String selo : selos) {
                System.out.print("Selo: " + selo + "... ");
                
                String select = "SELECT JSON, JSON12, IDAP FROM selados WHERE SELO = ?";
                try (PreparedStatement ps = conn.prepareStatement(select)) {
                    ps.setString(1, selo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String j12 = rs.getString("JSON12");
                            String j = rs.getString("JSON");
                            String idap = rs.getString("IDAP");
                            String jsonRaw = (j12 != null && j12.length() > 5) ? j12 : j;

                            if (jsonRaw == null || jsonRaw.length() < 5) {
                                System.out.println("[ERRO] JSON não disponível.");
                                continue;
                            }

                            String sanitizado = sanitizar730(conn, jsonRaw, idap);
                            
                            String update = "UPDATE selados SET JSON12 = ? WHERE SELO = ?";
                            try (PreparedStatement pu = conn.prepareStatement(update)) {
                                pu.setString(1, sanitizado);
                                pu.setString(2, selo);
                                pu.executeUpdate();
                                System.out.println("[OK]");
                            }
                        } else {
                            System.out.println("[FINALIZADO/NÃO ENCONTRADO]");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sanitizar730(Connection conn, String json, String idap) throws Exception {
        JsonNode root = M.readTree(json);
        ObjectNode seloObj = null;

        // Extração do selo
        if (root.has("selo") && root.get("selo").isObject()) {
            seloObj = (ObjectNode) root.get("selo");
        } else if (root.has("selos") && root.get("selos").isArray() && root.get("selos").size() > 0) {
            seloObj = (ObjectNode) root.get("selos").get(0);
        } else if (root.has("seloDigital")) {
            seloObj = (ObjectNode) root;
        }

        if (seloObj == null) return json;

        // 1. Zera Verbas
        ObjectNode verbas = (ObjectNode) seloObj.get("verbas");
        if (verbas == null) {
            verbas = M.createObjectNode();
            seloObj.set("verbas", verbas);
        }
        verbas.put("emolumentos", 0.0).put("vrcExt", 0.0).put("funrejus", 0.0)
              .put("iss", 0.0).put("fundep", 0.0).put("funarpen", 0.0)
              .put("distribuidor", 0.0).put("valorAdicional", 0.0);

        // 2. Propriedades
        ObjectNode props = (ObjectNode) seloObj.get("propriedades");
        if (props == null) {
            props = M.createObjectNode();
            seloObj.set("propriedades", props);
        }
        props.remove("envolvido"); // Remove singular

        // 3. Solicitante SERASA (Busca real no recibo)
        String sqlRec = "SELECT r.nomesol_rec, r.cgccpfsol_rec, r.classsol_rec, r.dtinirel_rec FROM recibo r WHERE r.selo_rec = ? LIMIT 1";
        String dataCert = "";
        try (PreparedStatement ps = conn.prepareStatement(sqlRec)) {
            ps.setString(1, seloObj.path("seloDigital").asText(""));
            try (ResultSet rs = ps.executeQuery()) {
                ObjectNode sol = M.createObjectNode();
                if (rs.next()) {
                    sol.put("nomeRazao", rs.getString("nomesol_rec").toUpperCase());
                    sol.put("documentoTipo", "F".equalsIgnoreCase(rs.getString("classsol_rec")) ? 1 : 2);
                    sol.put("documentoNumero", rs.getString("cgccpfsol_rec").replaceAll("[^0-9]", ""));
                    String d = rs.getString("dtinirel_rec");
                    if (d != null && d.length() == 8) dataCert = d.substring(6,8)+"/"+d.substring(4,6)+"/"+d.substring(0,4);
                } else {
                    sol.put("nomeRazao", "SERASA - CENTRALIZAÇÃO DE SERVIÇOS DOS BANCOS S/A");
                    sol.put("documentoTipo", 2);
                    sol.put("documentoNumero", "07502241000140");
                }
                sol.putNull("endereco");
                props.set("solicitanteAto", sol);
            }
        }

        // 4. Envolvidos (Busca no ctp001)
        if (idap != null && idap.length() >= 20) {
            String n1 = idap.substring(0, 6);
            String n2 = idap.substring(10, 20);
            String sqlCtp = "SELECT devedor_001, numer_001, digito_001, class_001 FROM ctp001 " +
                           "WHERE numapo1_001 = ? AND numapo2_001 = ? AND controle_001 = '01'";
            Set<String> seen = new HashSet<>();
            ArrayNode envsApp = M.createArrayNode();
            try (PreparedStatement ps = conn.prepareStatement(sqlCtp)) {
                ps.setString(1, n1); ps.setString(2, n2);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String doc = rs.getString("numer_001") + rs.getString("digito_001");
                        if (!seen.contains(doc) && doc.length() > 5) {
                            seen.add(doc);
                            ObjectNode e = M.createObjectNode();
                            e.put("documentoTipo", "F".equalsIgnoreCase(rs.getString("class_001")) ? 1 : 2);
                            e.put("documentoNumero", doc);
                            e.put("nomeRazao", rs.getString("devedor_001"));
                            envsApp.add(e);
                        }
                    }
                }
            }
            if (envsApp.size() > 0) {
                props.set("envolvidos", envsApp);
                ObjectNode cert = M.createObjectNode();
                cert.put("data", dataCert);
                cert.put("quantidadeEnvolvidos", envsApp.size());
                props.set("certidao", cert);
            }
        }

        // Retorna o JSON completo no formato individual {"selo":...}
        ObjectNode finalRoot = M.createObjectNode();
        finalRoot.put("ambiente", "prod");
        finalRoot.put("documentoResponsavel", "10752241000140");
        finalRoot.put("codigoOficio", 181122);
        finalRoot.set("selo", seloObj);
        
        return M.writeValueAsString(finalRoot);
    }
}
