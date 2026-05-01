
package com.monitor.funarpen.util;

import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.zip.*;
import java.util.Base64;
import java.net.URLEncoder;

public class FixMenuZlib {
    public static void main(String[] args) {
        try {
            // 1. Ler o XML que editamos
            String menuXmlPath = "C:\\Desenvolvimento\\Seprocom\\Notas\\menu_decoded.xml";
            String xml = new String(Files.readAllBytes(Paths.get(menuXmlPath)), "iso-8859-1");
            
            // 2. Usar o XML diretamente (ele ja possui as entidades %E2 etc se necessario)
            String xmlEncoded = xml;
            
            // 3. Comprimir usando ZLIB (Deflater padrao do Java inclui header e footer)
            byte[] input = xmlEncoded.getBytes("iso-8859-1");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            DeflaterOutputStream dos = new DeflaterOutputStream(baos, deflater);
            dos.write(input);
            dos.finish();
            dos.close();
            
            byte[] compressed = baos.toByteArray();
            String newBase64 = Base64.getEncoder().encodeToString(compressed);
            
            System.out.println("Novo Base64 gerado com ZLIB integro. Comprimento: " + newBase64.length());
            
            // 4. Atualizar o banco de dados
            try (Connection conn = DbUtil.getConnection()) {
                String sql = "UPDATE FR_PROPRIEDADE SET PRO_VALOR = ? WHERE COM_CODIGO = 996847 AND PRO_NOME = 'XMLMenu'";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, newBase64);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("SUCESSO! Tabela FR_PROPRIEDADE atualizada com ZLIB correto.");
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
