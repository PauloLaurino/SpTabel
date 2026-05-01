package com.selador.util;

import java.sql.*;
import java.util.*;
import java.net.http.*;
import java.net.URI;
import com.google.gson.*;

public class SanitizeAndTransmitScratch {

    private static final String DB_USER = "root";
    private static final String DB_PASS = "k15720";
    private static final String FUNARPEN_URL = "http://100.102.13.23:8059/funarpen/maker/api/funarpen/selos/recepcao/lote";

    public static void main(String[] args) throws Exception {
        Map<String, List<String>> targets = new LinkedHashMap<>();
        targets.put("100.75.153.127", Arrays.asList("SFTN1.ZGoAb.mE3mk-FmMID.1196p"));
        targets.put("100.102.13.23", Arrays.asList("SFTN2.cJ8RN.Mwcwe-ER4fM.1122q", "SFTN2.cJURN.Mwcwe-URDfM.1122q"));

        for (Map.Entry<String, List<String>> entry : targets.entrySet()) {
            String ip = entry.getKey();
            List<String> selos = entry.getValue();
            System.out.println("\n>>> Processando Servidor: " + ip);
            
            String url = "jdbc:mariadb://" + ip + ":3306/sptabel?useSSL=false&serverTimezone=UTC";
            
            try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASS)) {
                System.out.println("✅ Conectado ao banco em " + ip);
                
                List<JsonObject> jsonList = new ArrayList<>();
                
                for (String selo : selos) {
                    System.out.println("   - Sanitizando selo: " + selo);
                    
                    // 1. Buscar JSON original
                    String originalJson = null;
                    try (PreparedStatement ps = conn.prepareStatement("SELECT JSON FROM selados WHERE SELO = ?")) {
                        ps.setString(1, selo);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) originalJson = rs.getString(1);
                        }
                    }
                    
                    if (originalJson == null) {
                        System.err.println("   ❌ Selo não encontrado no banco!");
                        continue;
                    }
                    
                    // 2. Sanitizar
                    String sanitizado = SeloJsonSanitizerNotas.sanitizar(originalJson);
                    
                    // 3. Salvar JSON12
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE selados SET JSON12 = ?, STATUS = 'PENDENTE' WHERE SELO = ?")) {
                        ps.setString(1, sanitizado);
                        ps.setString(2, selo);
                        ps.executeUpdate();
                        System.out.println("   ✅ JSON12 atualizado.");
                    }
                    
                    System.out.println("   📝 JSON Sanitizado: " + sanitizado);
                    
                    jsonList.add(JsonParser.parseString(sanitizado).getAsJsonObject());
                }
                
                // 4. Enviar para FUNARPEN
                if (!jsonList.isEmpty()) {
                    System.out.println("   - Enviando lote de " + jsonList.size() + " selos para FUNARPEN...");
                    transmitir(jsonList, conn, selos);
                }
                
            } catch (Exception e) {
                System.err.println("❌ Erro no servidor " + ip + ": " + e.getMessage());
            }
        }
    }

    private static void transmitir(List<JsonObject> jsonList, Connection conn, List<String> selos) throws Exception {
        Gson gson = new Gson();
        String payload = gson.toJson(jsonList);
        
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
                
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FUNARPEN_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
                
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("   - Resposta FUNARPEN (" + response.statusCode() + "): " + response.body());
        
        if (response.statusCode() == 200) {
            JsonObject resObj = JsonParser.parseString(response.body()).getAsJsonObject();
            if (resObj.has("protocolo")) {
                String protocolo = resObj.get("protocolo").getAsString();
                System.out.println("   ✅ Sucesso! Protocolo: " + protocolo);
                
                try (PreparedStatement ps = conn.prepareStatement("UPDATE selados SET STATUS = 'SUCESSO', PROTOCOLO = ? WHERE SELO = ?")) {
                    for (String selo : selos) {
                        ps.setString(1, protocolo);
                        ps.setString(2, selo);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    System.out.println("   ✅ Status atualizado para SUCESSO no banco.");
                }
            }
        } else {
            System.err.println("   ❌ Falha na transmissão.");
        }
    }
}
