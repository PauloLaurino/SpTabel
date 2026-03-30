package com.selador.util;
import com.monitor.funarpen.util.DbUtil;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class DebugDb {
    public static void main(String[] args) {
        System.out.println("=== Diagnóstico de Banco de Dados ===");
        System.out.println(DbUtil.debugDiagnostics());
        
        try {
            System.out.print("\nTentando conectar via DbUtil... ");
            Connection c = DbUtil.getConnection();
            if (c != null) {
                System.out.println("✅ SUCESSO!");
                System.out.println("URL: " + c.getMetaData().getURL());
                
                // Listar colunas da tabela parametros
                System.out.println("\n=== Colunas da tabela 'parametros' ===");
                DatabaseMetaData meta = c.getMetaData();
                ResultSet columns = meta.getColumns(null, null, "parametros", null);
                while (columns.next()) {
                    System.out.println("  - " + columns.getString("COLUMN_NAME") + 
                                       " (" + columns.getString("TYPE_NAME") + 
                                       ", size=" + columns.getInt("COLUMN_SIZE") + ")");
                }
                columns.close();
                
                c.close();
            }
        } catch (Exception e) {
            System.out.println("❌ FALHA: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
