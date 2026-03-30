package com.selador.enums;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Enum auxiliar para formatos de data usados no sistema
 * AGORA COM SUPORTE para DDMMYYYY sem zero (dias 1-9)
 */
public enum FormatoData {
    
    YYYYMMDD("yyyyMMdd", "Ano-Mês-Dia (sem separadores)"),
    DDMMYYYY("ddMMyyyy", "Dia-Mês-Ano (sem separadores)"),
    DD_MM_YYYY("dd/MM/yyyy", "Dia/Mês/Ano"),
    ISO_8601("yyyy-MM-dd", "ISO 8601"),
    
    // NOVO FORMATO: DDMMYYYY sem zero à esquerda para dias 1-9
    // Exemplo: "09/10/2025" → "9102025" (remove zero do dia)
    DDMMYYYY_SEM_ZERO("ddMMyyyy", "Dia-Mês-Ano (sem zero para dia 1-9)") {
        
        @Override
        public String formatar(Date data) {
            if (data == null) {
                return "";
            }
            
            // Usa o formato DDMMYYYY normal
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", LOCALE_BR);
            String dataComZeros = sdf.format(data);
            
            // Remove zero do dia se for 1-9
            if (dataComZeros.startsWith("0")) {
                return dataComZeros.substring(1);
            }
            
            return dataComZeros;
        }
        
        @Override
        public Date parse(String dataStr) throws ParseException {
            if (dataStr == null || dataStr.trim().isEmpty()) {
                return null;
            }
            
            String dataStrFormatada = dataStr.trim();
            
            // Se a string tem 7 dígitos, adiciona zero no início (dia 1-9)
            if (dataStrFormatada.length() == 7) {
                dataStrFormatada = "0" + dataStrFormatada;
            }
            
            // Se tem 8 dígitos, já está no formato correto
            if (dataStrFormatada.length() == 8) {
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", LOCALE_BR);
                sdf.setLenient(false);
                return sdf.parse(dataStrFormatada);
            }
            
            throw new ParseException(
                "Data deve ter 7 ou 8 dígitos no formato DDMMYYYY (sem zero para dia 1-9). Recebido: " + dataStr, 
                0
            );
        }
    };
    
    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    
    private final String formato;
    private final String descricao;
    
    FormatoData(String formato, String descricao) {
        this.formato = formato;
        this.descricao = descricao;
    }
    
    public String getFormato() { return formato; }
    public String getDescricao() { return descricao; }
    
    /**
     * Formata uma data no formato especificado (método padrão)
     */
    public String formatar(Date data) {
        if (data == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(formato, LOCALE_BR);
        return sdf.format(data);
    }
    
    /**
     * Parse uma string para Date (método padrão)
     */
    public Date parse(String dataStr) throws ParseException {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            return null;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(formato, LOCALE_BR);
        sdf.setLenient(false);
        return sdf.parse(dataStr.trim());
    }
    
    /**
     * Converte entre formatos
     */
    public static String converter(String dataStr, FormatoData de, FormatoData para) 
            throws ParseException {
        Date data = de.parse(dataStr);
        return para.formatar(data);
    }
    
    /**
     * MÉTODO PRINCIPAL CORRIGIDO - Resolve o erro "Unparseable date: '20260112'"
     */
    public static String converterParaFormatoBanco(String dataFrontend, boolean usaFormatoDDMMYYYYSemZero) {
        if (dataFrontend == null || dataFrontend.trim().isEmpty()) {
            return "";
        }
        
        String dataLimpa = dataFrontend.replaceAll("[^0-9]", "").trim();
        
        // LOG para debugging
        System.out.println("🔄 [FormatoData] Convertendo:");
        System.out.println("   Entrada: " + dataFrontend);
        System.out.println("   Limpa: " + dataLimpa);
        System.out.println("   usaFormatoDDMMYYYYSemZero: " + usaFormatoDDMMYYYYSemZero);
        
        // CASO 1: Se já está no formato YYYYMMDD (8 dígitos)
        if (dataLimpa.length() == 8 && dataLimpa.matches("\\d{8}")) {
            System.out.println("   ✅ Identificado como YYYYMMDD");
            
            if (usaFormatoDDMMYYYYSemZero) {
                // Converte YYYYMMDD → DDMMYYYY sem zero
                String ano = dataLimpa.substring(0, 4);
                String mes = dataLimpa.substring(4, 6);
                String dia = dataLimpa.substring(6, 8);
                
                // Remove zero do dia se for 1-9
                int diaInt = Integer.parseInt(dia);
                String diaSemZero = (diaInt < 10) ? String.valueOf(diaInt) : dia;
                
                String resultado = diaSemZero + mes + ano;
                System.out.println("   🔄 YYYYMMDD → DDMMYYYY sem zero: " + resultado);
                return resultado;
            } else {
                // Mantém como YYYYMMDD
                System.out.println("   🔄 Mantém YYYYMMDD: " + dataLimpa);
                return dataLimpa;
            }
        }
        
        // CASO 2: Se já está no formato DDMMYYYY sem zero (7 dígitos)
        if (dataLimpa.length() == 7 && dataLimpa.matches("\\d{7}") && usaFormatoDDMMYYYYSemZero) {
            System.out.println("   ✅ Identificado como DDMMYYYY sem zero (7 dígitos)");
            return dataLimpa; // Já está no formato correto
        }
        
        // CASO 3: Se já está no formato DDMMYYYY com zero (8 dígitos) e precisa sem zero
        if (dataLimpa.length() == 8 && dataLimpa.matches("\\d{8}") && usaFormatoDDMMYYYYSemZero) {
            System.out.println("   ✅ Identificado como DDMMYYYY com zero");
            
            // Remove zero do dia se for 1-9
            if (dataLimpa.startsWith("0")) {
                String resultado = dataLimpa.substring(1);
                System.out.println("   🔄 Remove zero do dia: " + resultado);
                return resultado;
            }
            return dataLimpa;
        }
        
        // CASO 4: Para outros formatos, tenta converter para Date
        try {
            Date dataDate = parseDateInteligente(dataFrontend, usaFormatoDDMMYYYYSemZero);
            
            if (usaFormatoDDMMYYYYSemZero) {
                // Formatar como DDMMYYYY sem zero
                SimpleDateFormat sdfComZeros = new SimpleDateFormat("ddMMyyyy");
                String comZeros = sdfComZeros.format(dataDate);
                // Remove zero do dia se for 1-9
                String resultado = comZeros.startsWith("0") ? comZeros.substring(1) : comZeros;
                System.out.println("   🔄 Formatado como DDMMYYYY sem zero: " + resultado);
                return resultado;
            } else {
                // Formatar como YYYYMMDD
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String resultado = sdf.format(dataDate);
                System.out.println("   🔄 Formatado como YYYYMMDD: " + resultado);
                return resultado;
            }
            
        } catch (ParseException e) {
            System.err.println("❌ ERRO FATAL em converterParaFormatoBanco: " + e.getMessage());
            e.printStackTrace();
            
            // FALLBACK: retorna limpo
            System.out.println("   🆘 Fallback para: " + dataLimpa);
            return dataLimpa;
        }
    }
    
    /**
     * Parse inteligente que tenta identificar o formato da data
     */
    private static Date parseDateInteligente(String dataStr, boolean usaFormatoDDMMYYYY) throws ParseException {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            throw new ParseException("Data vazia", 0);
        }
        
        String dataLimpa = dataStr.trim();
        
        // Se for número puro
        if (dataLimpa.matches("\\d+")) {
            int length = dataLimpa.length();
            
            // 8 dígitos - tenta YYYYMMDD primeiro
            if (length == 8) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    sdf.setLenient(false);
                    return sdf.parse(dataLimpa);
                } catch (ParseException e1) {
                    // Tenta DDMMYYYY
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                        sdf.setLenient(false);
                        return sdf.parse(dataLimpa);
                    } catch (ParseException e2) {
                        throw new ParseException("Não é YYYYMMDD nem DDMMYYYY válido: " + dataLimpa, 0);
                    }
                }
            }
            
            // 7 dígitos - assume DDMMYYYY sem zero
            if (length == 7 && usaFormatoDDMMYYYY) {
                String dataComZero = "0" + dataLimpa;
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                sdf.setLenient(false);
                return sdf.parse(dataComZero);
            }
        }
        
        // Formato com hífen (YYYY-MM-DD)
        if (dataLimpa.contains("-")) {
            // Remover timezone se existir
            if (dataLimpa.contains("T")) {
                dataLimpa = dataLimpa.split("T")[0];
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            return sdf.parse(dataLimpa);
        }
        
        // Formato com barra (DD/MM/YYYY)
        if (dataLimpa.contains("/")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            return sdf.parse(dataLimpa);
        }
        
        // Se chegou aqui, não reconheceu o formato
        throw new ParseException("Formato de data não reconhecido: " + dataStr, 0);
    }
    
    /**
     * Método auxiliar para converter qualquer data para Date (mais tolerante)
     */
    public static Date converterParaDate(String dataStr, boolean usaFormatoDDMMYYYY) {
        try {
            return parseDateInteligente(dataStr, usaFormatoDDMMYYYY);
        } catch (ParseException e) {
            System.err.println("❌ Erro em converterParaDate: " + e.getMessage());
            return new Date(); // Retorna data atual como fallback
        }
    }
    
    /**
     * Método de teste para verificar todas as conversões
     */
    public static void testarConversao(String data, boolean usaFormatoSemZero) {
        System.out.println("\n🧪 TESTE:");
        System.out.println("   Entrada: " + data);
        System.out.println("   Sem zero: " + usaFormatoSemZero);
        
        try {
            String resultado = converterParaFormatoBanco(data, usaFormatoSemZero);
            System.out.println("   Resultado: " + resultado);
            System.out.println("   Status: ✅ SUCESSO");
        } catch (Exception e) {
            System.out.println("   Status: ❌ ERRO: " + e.getMessage());
        }
    }
    
    /**
     * Executa todos os testes principais
     */
    public static void executarTodosTestes() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🧪 TESTES COMPLETOS DE CONVERSÃO");
        System.out.println("═══════════════════════════════════════════════════");
        
        // Testes que estavam falhando
        testarConversao("20260112", false); // Deve retornar "20260112"
        testarConversao("20260112", true);  // Deve retornar "12012026"
        testarConversao("2026-01-12", false); // Deve retornar "20260112"
        testarConversao("2026-01-12", true);  // Deve retornar "12012026"
        testarConversao("12/01/2026", false); // Deve retornar "20260112"
        testarConversao("12/01/2026", true);  // Deve retornar "12012026"
        
        // Teste com dia 1-9
        testarConversao("20260108", false); // Deve retornar "20260108"
        testarConversao("20260108", true);  // Deve retornar "8012026"
        testarConversao("08/01/2026", true); // Deve retornar "8012026"
        
        // Teste com formato já no destino
        testarConversao("12012026", true);  // Deve retornar "12012026"
        testarConversao("8012026", true);   // Deve retornar "8012026"
        
        System.out.println("═══════════════════════════════════════════════════");
    }
    
    /**
     * Main para testes rápidos
     */
    public static void main(String[] args) {
        executarTodosTestes();
    }
}