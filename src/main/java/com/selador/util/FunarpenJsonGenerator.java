package com.selador.util;

import java.sql.Connection;
import java.util.Map;

/**
 * Classe para geração de JSON para a API Funarpen
 * VERSÃO STUB - Necessária para compilação
 */
public class FunarpenJsonGenerator {
    
    /**
     * Gera JSON para um ato notarial
     */
    public static String gerarJsonParaAto(Connection conn, int numtipato, 
            String tipoOperacao, String numapo1, String numapo2,
            String numeroSelo, String idap, Map<String, Object> dadosSelo) {
        
        // Stub - retorna JSON vazio
        // A implementação real deve buscar dados do banco e gerar o JSON
        return "";
    }
}
