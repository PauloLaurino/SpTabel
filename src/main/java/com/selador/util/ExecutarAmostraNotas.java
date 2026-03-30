package com.selador.util;

import com.selador.dao.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ExecutarAmostraNotas {
    public static void main(String[] args) {
        int idInicio = 48233;
        int idFim = 48764;
        
        System.out.println("========================================");
        System.out.println("🚀 SANITIZAÇÃO IDs " + idInicio + " a " + idFim);
        System.out.println("========================================");
        
        try (Connection conn = ConnectionFactory.getConnection()) {
            String sql = "SELECT sel.SELO FROM selados sel WHERE sel.ID >= ? AND sel.ID <= ? ORDER BY sel.ID";
            List<String> selos = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idInicio);
                ps.setInt(2, idFim);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) selos.add(rs.getString("SELO"));
                }
            }
            
            System.out.println("📊 Selos: " + selos.size());
            int sucesso = 0, erro = 0;
            
            for (String selo : selos) {
                System.out.println("🔄 " + selo);
                try {
                    boolean ok = SeloJsonSanitizerNotas.sanitizarESalvar(selo, true);
                    if (ok) { System.out.println("✅"); sucesso++; }
                    else { System.out.println("❌"); erro++; }
                } catch (Exception e) {
                    System.out.println("💥 " + e.getMessage());
                    erro++;
                }
            }
            
            System.out.println("========================================");
            System.out.println("✅ Sucesso: " + sucesso + " | ❌ Falha: " + erro);
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
        }
    }
}