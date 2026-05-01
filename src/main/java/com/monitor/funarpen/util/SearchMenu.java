
package com.monitor.funarpen.util;

import java.sql.*;
import java.util.*;

public class SearchMenu {
    public static void main(String[] args) {
        try (Connection conn = DbUtil.getConnection()) {
            // Busca tabelas que contem colunas de texto com o fragmento do menu
            String fragment = "V6HZ3G/Hmu6+hWy5nSrS51w";
            
            System.out.println("Buscando no information_schema...");
            String findSql = "SELECT TABLE_NAME, COLUMN_NAME FROM information_schema.COLUMNS " +
                             "WHERE TABLE_SCHEMA = 'sptabel' AND TABLE_NAME LIKE 'FR_%' " +
                             "AND DATA_TYPE IN ('varchar', 'longtext', 'clob', 'text', 'blob', 'longblob')";
            
            List<String[]> candidates = new ArrayList<>();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(findSql)) {
                while (rs.next()) {
                    candidates.add(new String[]{rs.getString("TABLE_NAME"), rs.getString("COLUMN_NAME")});
                }
            }
            
            System.out.println("Candidatos encontrados: " + candidates.size());
            for (String[] candidate : candidates) {
                String table = candidate[0];
                String col = candidate[1];
                String checkSql = "SELECT count(*) FROM " + table + " WHERE " + col + " LIKE ?";
                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setString(1, "%" + fragment + "%");
                    ResultSet rsCount = ps.executeQuery();
                    if (rsCount.next() && rsCount.getInt(1) > 0) {
                        System.out.println("ALVO ENCONTRADO! Tabela: " + table + " | Coluna: " + col);
                        
                        // Mostra o registro
                        String dataSql = "SELECT * FROM " + table + " WHERE " + col + " LIKE ? LIMIT 1";
                        try (PreparedStatement psData = conn.prepareStatement(dataSql)) {
                            psData.setString(1, "%" + fragment + "%");
                            ResultSet rsData = psData.executeQuery();
                            ResultSetMetaData rsMd = rsData.getMetaData();
                            if (rsData.next()) {
                                for (int i = 1; i <= rsMd.getColumnCount(); i++) {
                                    System.out.println("  " + rsMd.getColumnName(i) + ": " + rsData.getObject(i));
                                }
                            }
                        }
                    }
                } catch (Exception e) {}
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
