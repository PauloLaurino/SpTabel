package com.selador.util;

import com.selador.dao.ConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VerificarJson {
    public static void main(String[] args) {
        String[] selos = {"SFTN1.wGFJb.C4f2c-q6zZh.1122p", "SFTN1.zGGKb.3hfRs-TRtZO.1122p"};
        
        System.out.println("=== Verificacao de JSON12 Sanitizado ===\n");
        
        try {
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT JSON12 FROM selados WHERE SELO = ?");
            
            for (String selo : selos) {
                stmt.setString(1, selo);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String json = rs.getString("JSON12");
                    System.out.println("Selo: " + selo);
                    System.out.println("JSON12 tamanho: " + (json != null ? json.length() : "null"));
                    
                    if (json != null && json.length() > 100) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            Object jsonObj = mapper.readValue(json, Object.class);
                            String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
                            System.out.println(pretty.substring(0, Math.min(pretty.length(), 2000)));
                        } catch (Exception e) {
                            System.out.println(json.substring(0, Math.min(json.length(), 500)));
                        }
                    }
                    System.out.println("\n---\n");
                }
                rs.close();
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}