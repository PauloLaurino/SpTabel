package com.selador.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.selador.dao.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReviewJson {
    public static void main(String[] args) {
        boolean countOnly = false;
        for (String s : args) if (s.equals("count=true")) countOnly = true;
        
        try (Connection conn = ConnectionFactory.getConnection()) {
            if (countOnly) {
                String sql = "SELECT COUNT(*) FROM selados WHERE DATAENVIO >= '2026-03-19' AND DATAENVIO <= '2026-03-27'";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) System.out.println("TOTAL_SELOS_PERIODO: " + rs.getInt(1));
                    }
                }
                String sqlNull = "SELECT COUNT(*) FROM selados WHERE DATAENVIO >= '2026-03-19' AND DATAENVIO <= '2026-03-27' AND JSON12 IS NULL";
                try (PreparedStatement ps = conn.prepareStatement(sqlNull)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) System.out.println("TOTAL_SELOS_PENDENTES_SANITIZAR: " + rs.getInt(1));
                    }
                }
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            // Busca especificamente selos onde a injeção de propriedades (Escritura 459) deveria ocorrer
            String sql = "SELECT SELO, JSON12 FROM selados WHERE JSON12 LIKE '%\"codigoTipoAto\": 459%' AND JSON12 IS NOT NULL ORDER BY ID DESC LIMIT 3";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    System.out.println("=== REVISÃO DE JSONS SANITIZADOS (ÚLTIMOS 3) ===");
                    while (rs.next()) {
                        String selo = rs.getString("SELO");
                        String json = rs.getString("JSON12");
                        System.out.println("\nSelo: " + selo);
                        try {
                            Object obj = mapper.readValue(json, Object.class);
                            System.out.println(mapper.writeValueAsString(obj));
                        } catch (Exception e) {
                            System.out.println("JSON Bruto (Erro Formatação): " + json);
                        }
                        System.out.println("-------------------------------------------------");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
