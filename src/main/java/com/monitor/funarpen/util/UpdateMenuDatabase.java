
package com.monitor.funarpen.util;

import java.sql.*;
import java.nio.file.*;
import java.io.*;

public class UpdateMenuDatabase {
    public static void main(String[] args) {
        try {
            // 1. Ler o novo XML decodificado e recodificar para garantir integridade
            // Na verdade, vou usar o Base64 que o encode_menu.ps1 geraria ou 
            // vou simplesmente ler o Menu.xml que ja foi atualizado no sistema de arquivos
            
            String menuXmlPath = "C:\\Program Files (x86)\\Softwell Solutions\\Maker Studio\\Webrun Studio\\tomcat\\webapps\\webrunstudio\\classes\\wfr\\com\\systems\\system_ttb\\forms\\safiraformularioprincipal\\Menu.xml";
            String fileContent = new String(Files.readAllBytes(Paths.get(menuXmlPath)), "iso-8859-1");
            
            // Extrair o Base64 entre <PROPERTY KEY="XMLMenu"> e </PROPERTY>
            String startTag = "<PROPERTY KEY=\"XMLMenu\">";
            String endTag = "</PROPERTY>";
            int start = fileContent.indexOf(startTag);
            int end = fileContent.indexOf(endTag, start);
            
            if (start == -1 || end == -1) {
                System.err.println("Erro: Nao foi possivel encontrar a tag XMLMenu no arquivo Menu.xml");
                return;
            }
            
            String newBase64 = fileContent.substring(start + startTag.length(), end).trim();
            System.out.println("Base64 extraido do arquivo Menu.xml. Comprimento: " + newBase64.length());
            
            // 2. Atualizar o banco de dados
            try (Connection conn = DbUtil.getConnection()) {
                String sql = "UPDATE FR_PROPRIEDADE SET PRO_VALOR = ? WHERE COM_CODIGO = 996847 AND PRO_NOME = 'XMLMenu'";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, newBase64);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("SUCESSO! Tabela FR_PROPRIEDADE atualizada. Linhas afetadas: " + rows);
                    } else {
                        System.out.println("AVISO: Nenhuma linha foi atualizada. Verifique se o registro existe.");
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
