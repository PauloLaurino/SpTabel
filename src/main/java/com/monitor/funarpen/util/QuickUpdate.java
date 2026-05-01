
package com.monitor.funarpen.util;

import java.sql.*;

public class QuickUpdate {
    public static void main(String[] args) {
        try (Connection conn = DbUtil.getConnection()) {
            System.out.println("Buscando propriedades para o componente 996847...");
            // No Maker 3, as propriedades costumam estar em FR_PROPRIEDADE
            // Colunas provaveis: COM_CODIGO, PRO_NOME, PRO_VALOR
            String sql = "SELECT * FROM FR_PROPRIEDADE WHERE COM_CODIGO = 996847";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                ResultSetMetaData md = rs.getMetaData();
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("--- PROPRIEDADE ---");
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        String name = md.getColumnName(i);
                        Object val = rs.getObject(i);
                        String sVal = String.valueOf(val);
                        if (sVal.length() > 50) sVal = sVal.substring(0, 47) + "...";
                        System.out.println("  " + name + ": " + sVal);
                    }
                }
                if (!found) {
                    System.out.println("Nao encontrado em FR_PROPRIEDADE.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
