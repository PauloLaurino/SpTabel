package com.selador.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.selador.dao.ConnectionFactory;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilitário para gerar amostras de JSON sanitizados para validação visual (Notas).
 * 
 * Uso:
 *   java -cp notas.jar com.selador.util.GerarAmostraValidacao
 * 
 * Gera 1 amostra de cada tipo existente entre 405-499.
 * Filtros:
 *   - selados.NUMTIPATO BETWEEN 405 AND 499
 *   - selados.DATAENVIO >= parametros.DTVERSAO_FUNARPEN
 *   - selados.STATUS != 'SUCESSO'
 *   - ORDER BY selados.ID ASC (mais antigos primeiro)
 */
public class GerarAmostraValidacao {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final int AMOSTRAS_POR_TIPO = 1;  // 1 amostra por tipo
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("📋 GERADOR DE AMOSTRAS PARA VALIDAÇÃO VISUAL");
        System.out.println("                    [PROJETO NOTAS]");
        System.out.println("=================================================");
        System.out.println("Data: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        System.out.println("Tipos: 405-499 (1 amostra por tipo existente)");
        System.out.println("Filtros:");
        System.out.println("  - DATAENVIO >= DTVERSAO_FUNARPEN");
        System.out.println("  - STATUS != 'SUCESSO'");
        System.out.println("  - ORDER BY ID ASC (mais antigos)");
        System.out.println("=================================================\n");
        
        try {
            // Criar diretório de saída
            String dirSaida = "C:/Desenvolvimento/Seprocom/Notas/amostras_validacao_" + DATE_FORMAT.format(new Date());
            new File(dirSaida).mkdirs();
            System.out.println("📁 Diretório de saída: " + dirSaida + "\n");
            
            // Primeiro, detectar quais tipos existem entre 405-499
            List<Integer> tiposExistentes = detectarTiposExistentes();
            
            System.out.println("📊 Tipos detectados: " + tiposExistentes.size());
            for (Integer tipo : tiposExistentes) {
                System.out.println("  - " + tipo);
            }
            System.out.println();
            
            if (tiposExistentes.isEmpty()) {
                System.out.println("⚠️  Nenhum tipo encontrado na faixa 405-499\n");
                return;
            }
            
            int totalProcessados = 0;
            int totalSucesso = 0;
            int totalErro = 0;
            
            for (Integer tipo : tiposExistentes) {
                System.out.println("-------------------------------------------------");
                System.out.println("🔄 Processando tipo: " + tipo);
                System.out.println("-------------------------------------------------");
                
                List<String[]> selosInfo = buscarSelosPorTipo(tipo, AMOSTRAS_POR_TIPO);
                
                if (selosInfo.isEmpty()) {
                    System.out.println("⚠️  Nenhum selo encontrado para tipo " + tipo + "\n");
                    continue;
                }
                
                System.out.println("📦 Selos encontrados: " + selosInfo.size());
                
                String dirTipo = dirSaida + "/tipo_" + tipo;
                new File(dirTipo).mkdirs();
                
                int count = 0;
                for (String[] info : selosInfo) {
                    String selo = info[0];
                    String jsonOriginal = info[1];
                    count++;
                    totalProcessados++;
                    System.out.print("  [" + count + "/" + selosInfo.size() + "] " + selo + " ... ");
                    
                    try {
                        // Sanitizar usando o método existente
                        String jsonSanitizado = SeloJsonSanitizerNotas.sanitizar(jsonOriginal);
                        
                        if (jsonSanitizado == null || jsonSanitizado.isEmpty()) {
                            System.out.println("❌ (sanitização retornou vazio)");
                            totalErro++;
                            continue;
                        }
                        
                        // Salvar arquivo JSON
                        String nomeArquivo = "tipo_" + tipo + "_" + count + "_" + selo.replace(".", "_").replace("-", "_") + ".json";
                        String caminhoArquivo = dirTipo + "/" + nomeArquivo;
                        
                        try (FileWriter fw = new FileWriter(caminhoArquivo)) {
                            fw.write(jsonSanitizado);
                        }
                        
                        // Salvar versão formatada
                        String nomePretty = "tipo_" + tipo + "_" + count + "_pretty.json";
                        String caminhoPretty = dirTipo + "/" + nomePretty;
                        try (FileWriter fw = new FileWriter(caminhoPretty)) {
                            Object obj = com.google.gson.JsonParser.parseString(jsonSanitizado);
                            fw.write(GSON.toJson(obj));
                        }
                        
                        // Salvar metadata
                        String nomeMeta = "tipo_" + tipo + "_" + count + "_meta.txt";
                        String caminhoMeta = dirTipo + "/" + nomeMeta;
                        try (FileWriter fw = new FileWriter(caminhoMeta)) {
                            fw.write("SELO: " + selo + "\n");
                            fw.write("TIPO: " + tipo + "\n");
                            fw.write("ARQUIVO_JSON: " + nomeArquivo + "\n");
                            fw.write("DATA: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
                        }
                        
                        System.out.println("✅");
                        totalSucesso++;
                        
                    } catch (Exception e) {
                        System.out.println("❌ Erro: " + e.getMessage());
                        totalErro++;
                    }
                }
                System.out.println();
            }
            
            // Gerar sumário
            gerarSumario(dirSaida, tiposExistentes);
            
            System.out.println("\n=================================================");
            System.out.println("📊 RESUMO:");
            System.out.println("  Total processados: " + totalProcessados);
            System.out.println("  Sucessos: " + totalSucesso);
            System.out.println("  Erros: " + totalErro);
            System.out.println("=================================================");
            System.out.println("✅ GERAÇÃO CONCLUÍDA!");
            System.out.println("📁 Verifique os arquivos em: " + dirSaida);
            System.out.println("💡 DICA: Abra os arquivos *_pretty.json para visualizar a estrutura JSON.");
            System.out.println("=================================================");
            
        } catch (Exception e) {
            System.err.println("❌ Erro fatal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Detecta quais tipos existem entre 405-499 com dados para amostragem
     */
    private static List<Integer> detectarTiposExistentes() {
        List<Integer> tipos = new ArrayList<>();
        Set<Integer> tiposSet = new HashSet<>();
        
        String sql = "SELECT DISTINCT sel.NUMTIPATO FROM selados sel " +
                     "INNER JOIN parametros p ON 1=1 " +
                     "WHERE sel.NUMTIPATO BETWEEN 405 AND 499 " +
                     "  AND sel.DATAENVIO >= p.DTVERSAO_FUNARPEN " +
                     "  AND sel.STATUS != 'SUCESSO' " +
                     "  AND (sel.JSON IS NOT NULL AND sel.JSON != '') " +
                     "ORDER BY sel.NUMTIPATO";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int tipo = rs.getInt("NUMTIPATO");
                    if (!tiposSet.contains(tipo)) {
                        tiposSet.add(tipo);
                        tipos.add(tipo);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao detectar tipos: " + e.getMessage());
        }
        
        return tipos;
    }
    
    /**
     * Busca selos mais antigos para um tipo específico
     * Filtros:
     *   - DATAENVIO >= DTVERSAO_FUNARPEN
     *   - STATUS != 'SUCESSO'
     *   - ORDER BY ID ASC (mais antigos primeiro)
     */
    private static List<String[]> buscarSelosPorTipo(int tipoAto, int limite) {
        List<String[]> resultados = new ArrayList<>();
        
        String sql = "SELECT sel.SELO, sel.JSON FROM selados sel " +
                     "INNER JOIN parametros p ON 1=1 " +
                     "WHERE sel.NUMTIPATO = ? " +
                     "  AND sel.DATAENVIO >= p.DTVERSAO_FUNARPEN " +
                     "  AND sel.STATUS != 'SUCESSO' " +
                     "  AND (sel.JSON IS NOT NULL AND sel.JSON != '') " +
                     "ORDER BY sel.ID ASC LIMIT ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tipoAto);
            ps.setInt(2, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String selo = rs.getString("SELO");
                    String json = rs.getString("JSON");
                    resultados.add(new String[]{selo, json});
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar selos: " + e.getMessage());
        }
        
        return resultados;
    }
    
    private static void gerarSumario(String dirSaida, List<Integer> tipos) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================\n");
        sb.append("📋 SUMÁRIO - AMOSTRAS PARA VALIDAÇÃO VISUAL\n");
        sb.append("              [PROJETO NOTAS]\n");
        sb.append("=================================================\n");
        sb.append("Data: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
        sb.append("Tipos: 405-499\n");
        sb.append("Amostras por tipo: ").append(AMOSTRAS_POR_TIPO).append("\n");
        sb.append("Filtros:\n");
        sb.append("  - DATAENVIO >= DTVERSAO_FUNARPEN\n");
        sb.append("  - STATUS != 'SUCESSO'\n");
        sb.append("  - ORDER BY ID ASC\n\n");
        
        for (Integer tipo : tipos) {
            String dirTipo = dirSaida + "/tipo_" + tipo;
            File f = new File(dirTipo);
            if (f.exists()) {
                String[] arquivos = f.list((d, n) -> n.endsWith("_pretty.json"));
                int count = arquivos != null ? arquivos.length : 0;
                if (count > 0) {
                    sb.append("TIPO ").append(tipo).append(": ").append(count).append(" amostra(s)\n");
                }
            }
        }
        
        sb.append("\n📁 ESTRUTURA DE ARQUIVOS:\n");
        sb.append("  [dir_saida]/\n");
        sb.append("    ├── SUMARIO.txt\n");
        for (Integer tipo : tipos) {
            sb.append("    ├── tipo_").append(tipo).append("/\n");
            sb.append("    │   ├── tipo_").append(tipo).append("_1_pretty.json\n");
            sb.append("    │   ├── tipo_").append(tipo).append("_1.json\n");
            sb.append("    │   └── tipo_").append(tipo).append("_1_meta.txt\n");
        }
        sb.append("\n💡 DICA: Abra os arquivos *_pretty.json para visualizar a estrutura JSON formatada.\n");
        sb.append("=================================================\n");
        
        try (FileWriter fw = new FileWriter(dirSaida + "/SUMARIO.txt")) {
            fw.write(sb.toString());
        }
    }
}
