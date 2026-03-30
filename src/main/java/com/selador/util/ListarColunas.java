package com.selador.util;
import com.selador.dao.ConnectionFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class ListarColunas {
    public static void main(String[] args) {
        System.out.println("=== Colunas da tabela 'parametros' ===");
        try (Connection conn = ConnectionFactory.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet columns = meta.getColumns(null, null, "parametros", null);
            while (columns.next()) {
                System.out.println("  - " + columns.getString("COLUMN_NAME") + 
                                   " (" + columns.getString("TYPE_NAME") + 
                                   ", size=" + columns.getInt("COLUMN_SIZE") + ")");
            }
            columns.close();
            
            System.out.println("\n=== Dados da primeira linha ===");
            try (java.sql.Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM parametros LIMIT 1")) {
                if (rs.next()) {
                    java.sql.ResultSetMetaData metaData = rs.getMetaData();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        System.out.println("  " + metaData.getColumnName(i) + " = " + rs.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}