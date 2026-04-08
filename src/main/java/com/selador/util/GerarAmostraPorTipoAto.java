package com.selador.util;

import com.selador.dao.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilitário para gerar uma amostra de 5 registros de cada tipo de ato 
 * que ainda não foram processados com sucesso.
 */
public class GerarAmostraPorTipoAto {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("🚀 GERANDO AMOSTRA: 5 REGISTROS POR TIPO DE ATO");
        System.out.println("=================================================");

        try (Connection conn = ConnectionFactory.getConnection()) {
            // Query para pegar 5 de cada tipo usando ROW_NUMBER (MySQL 8+)
            // Se for MySQL antigo, a query precisaria de variávies de sessão, 
            // mas assumindo ambiente moderno conforme as documentações.
            String sql = "SELECT * FROM (" +
                         "  SELECT sel.SELO, s.atotipo_sel as ATO, " +
                         "         ROW_NUMBER() OVER(PARTITION BY s.atotipo_sel ORDER BY sel.ID DESC) as rn " +
                         "  FROM selados sel " +
                         "  INNER JOIN selos s ON s.selo_sel = sel.SELO " +
                         "  WHERE (sel.STATUS IS NULL OR sel.STATUS != 'SUCESSO') " +
                         "  AND sel.DATAENVIO > '2026-03-01' " +
                         ") t " +
                         "WHERE rn <= 5 " +
                         "ORDER BY ATO, SELO";

            Map<Integer, List<String>> amostraPorAto = new HashMap<>();
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String selo = rs.getString("SELO");
                        int ato = rs.getInt("ATO");
                        amostraPorAto.computeIfAbsent(ato, k -> new ArrayList<>()).add(selo);
                    }
                }
            }

            if (amostraPorAto.isEmpty()) {
                System.out.println("ℹ️ Nenhum selo pendente encontrado para amostragem.");
                return;
            }

            System.out.println("📊 Tipos de Atos identificados: " + amostraPorAto.size());
            System.out.println("-------------------------------------------------");

            int totalProcessado = 0;
            int totalSucesso = 0;
            int totalErro = 0;

            for (Map.Entry<Integer, List<String>> entry : amostraPorAto.entrySet()) {
                int ato = entry.getKey();
                List<String> selos = entry.getValue();
                
                System.out.print("📦 Tipo [" + ato + "] - " + selos.size() + " selos: ");
                
                for (String selo : selos) {
                    try {
                        totalProcessado++;
                        // Passando false para processar sem salvar como SUCESSO se quiser apenas teste,
                        // mas o sanitizarESalvar(selo, true) é o que o usuário quer para visualizar resultados.
                        boolean ok = SeloJsonSanitizerNotas.sanitizarESalvar(selo, true);
                        if (ok) {
                            System.out.print("✅ ");
                            totalSucesso++;
                        } else {
                            System.out.print("❌ ");
                            totalErro++;
                        }
                    } catch (Exception e) {
                        System.out.print("💥 ");
                        totalErro++;
                    }
                }
                System.out.println();
            }

            System.out.println("-------------------------------------------------");
            System.out.println("🏁 RESUMO:");
            System.out.println("📈 Total Processado: " + totalProcessado);
            System.out.println("✨ Sucessos: " + totalSucesso);
            System.out.println("⚠️ Falhas: " + totalErro);
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("❌ Erro fatal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
