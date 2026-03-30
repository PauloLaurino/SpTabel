package com.selador.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utilitário para manipulação de datas com suporte aos formatos do sistema Maker 5
 */
public class DateUtil {
    
    private static final Locale LOCALE_BR = new Locale("pt", "BR");
    
    // Constantes para formatos comuns
    public static final String FORMATO_DDMMYYYY = "dd/MM/yyyy";
    public static final String FORMATO_DDMMYYYY_HHMMSS = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMATO_YYYYMMDD = "yyyyMMdd";
    public static final String FORMATO_YYYYMMDD_HHMMSS = "yyyyMMddHHmmss";
    public static final String FORMATO_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    
    private DateUtil() {
        // Classe utilitária - não instanciável
    }
    
    /**
     * Converte String para Date conforme formato especificado
     */
    public static Date stringParaDate(String dataStr, String formato) throws ParseException {
        if (dataStr == null || dataStr.trim().isEmpty() || formato == null) {
            return null;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(formato, LOCALE_BR);
        sdf.setLenient(false);
        return sdf.parse(dataStr.trim());
    }
    
    /**
     * Formata Date para String conforme formato especificado
     */
    public static String dateParaString(Date data, String formato) {
        if (data == null || formato == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(formato, LOCALE_BR);
        return sdf.format(data);
    }
    
    /**
     * Converte data entre formatos (ex: DDMMYYYY → YYYYMMDD)
     */
    public static String converterFormato(String dataStr, String formatoOrigem, String formatoDestino) 
            throws ParseException {
        Date data = stringParaDate(dataStr, formatoOrigem);
        return dateParaString(data, formatoDestino);
    }
    
    /**
     * Formata data para formato do banco (YYYYMMDD como número)
     */
    public static String formatarParaBanco(Date data, String formatoBanco) {
        if (data == null) return "";
        
        String dataFormatada = dateParaString(data, formatoBanco);
        
        // Remove separadores para formato numérico do banco
        if (FORMATO_YYYYMMDD.equals(formatoBanco) || 
            FORMATO_DDMMYYYY.equals(formatoBanco.replace("/", ""))) {
            return dataFormatada.replaceAll("[^0-9]", "");
        }
        
        return dataFormatada;
    }
    
    /**
     * Verifica se a string é uma data válida no formato especificado
     */
    public static boolean isDataValida(String dataStr, String formato) {
        if (dataStr == null || dataStr.trim().isEmpty() || formato == null) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formato, LOCALE_BR);
            sdf.setLenient(false);
            Date data = sdf.parse(dataStr.trim());
            
            // Verificação adicional para datas como 31/02/2024
            String dataFormatada = sdf.format(data);
            return dataFormatada.equals(dataStr.trim());
            
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Compara se data1 é maior que data2
     */
    public static boolean data1MaiorQueData2(Date data1, Date data2) {
        if (data1 == null || data2 == null) return false;
        return data1.after(data2);
    }
    
    /**
     * Compara se data1 é menor que data2
     */
    public static boolean data1MenorQueData2(Date data1, Date data2) {
        if (data1 == null || data2 == null) return false;
        return data1.before(data2);
    }
    
    /**
     * Adiciona dias a uma data
     */
    public static Date adicionarDias(Date data, int dias) {
        if (data == null) return null;
        
        Calendar cal = Calendar.getInstance(LOCALE_BR);
        cal.setTime(data);
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }
    
    /**
     * Adiciona meses a uma data
     */
    public static Date adicionarMeses(Date data, int meses) {
        if (data == null) return null;
        
        Calendar cal = Calendar.getInstance(LOCALE_BR);
        cal.setTime(data);
        cal.add(Calendar.MONTH, meses);
        return cal.getTime();
    }
    
    /**
     * Obtém o primeiro dia do mês
     */
    public static Date primeiroDiaDoMes(Date data) {
        if (data == null) return null;
        
        Calendar cal = Calendar.getInstance(LOCALE_BR);
        cal.setTime(data);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }
    
    /**
     * Obtém o último dia do mês
     */
    public static Date ultimoDiaDoMes(Date data) {
        if (data == null) return null;
        
        Calendar cal = Calendar.getInstance(LOCALE_BR);
        cal.setTime(data);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }
    
    /**
     * Calcula diferença em dias entre duas datas
     */
    public static long diferencaEmDias(Date data1, Date data2) {
        if (data1 == null || data2 == null) return 0;
        
        long diff = Math.abs(data1.getTime() - data2.getTime());
        return diff / (24 * 60 * 60 * 1000);
    }
    
    /**
     * Verifica se a data está no formato especificado
     */
    public static boolean isDataNoFormato(Date data, String formato) {
        if (data == null || formato == null) return false;
        
        try {
            String dataFormatada = dateParaString(data, formato);
            Date dataConvertida = stringParaDate(dataFormatada, formato);
            return data.equals(dataConvertida);
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Formata data no padrão ISO 8601 (para tabela selados)
     */
    public static String formatarDataISO(Date data) {
        if (data == null) return "";
        
        SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_ISO, LOCALE_BR);
        return sdf.format(data);
    }
    
    /**
     * Obtém data atual formatada
     */
    public static String getDataAtualFormatada(String formato) {
        return dateParaString(new Date(), formato);
    }
    
    /**
     * Converte data do banco (YYYYMMDD numérico) para Date
     */
    public static Date converterDoBanco(String dataBanco, String formatoOrigem) 
            throws ParseException {
        if (dataBanco == null || dataBanco.trim().isEmpty()) {
            return null;
        }
        
        // Se for numérico sem separadores, adiciona separadores se necessário
        String dataFormatada = dataBanco.trim();
        if (dataFormatada.matches("^\\d{8}$")) {
            if (FORMATO_YYYYMMDD.equals(formatoOrigem)) {
                dataFormatada = dataFormatada.substring(0, 4) + "/" + 
                               dataFormatada.substring(4, 6) + "/" + 
                               dataFormatada.substring(6, 8);
            } else if (FORMATO_DDMMYYYY.equals(formatoOrigem)) {
                dataFormatada = dataFormatada.substring(0, 2) + "/" + 
                               dataFormatada.substring(2, 4) + "/" + 
                               dataFormatada.substring(4, 8);
            }
        }
        
        return stringParaDate(dataFormatada, formatoOrigem);
    }
    
    /**
     * Valida período entre datas (máximo 30 dias por padrão)
     */
    public static boolean validarPeriodo(Date dataInicial, Date dataFinal, int maxDias) {
        if (dataInicial == null || dataFinal == null) return false;
        if (data1MaiorQueData2(dataInicial, dataFinal)) return false;
        
        long dias = diferencaEmDias(dataInicial, dataFinal);
        return dias <= maxDias;
    }
    
    /**
     * Formata data no formato curto para exibição
     */
    public static String formatarDataCurta(Date data) {
        return dateParaString(data, FORMATO_DDMMYYYY);
    }
    
    /**
     * Formata data e hora para exibição
     */
    public static String formatarDataHora(Date data) {
        return dateParaString(data, FORMATO_DDMMYYYY_HHMMSS);
    }
    
    /**
     * Verifica se duas datas são do mesmo dia (ignorando horas)
     */
    public static boolean isMesmoDia(Date data1, Date data2) {
        if (data1 == null || data2 == null) return false;
        
        Calendar cal1 = Calendar.getInstance(LOCALE_BR);
        Calendar cal2 = Calendar.getInstance(LOCALE_BR);
        cal1.setTime(data1);
        cal2.setTime(data2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * Obtém a data atual
     */
    public static Date getDataAtual() {
        return new Date();
    }
    
    /**
     * Verifica se uma data está entre duas datas (inclusive)
     */
    public static boolean isDataEntre(Date data, Date inicio, Date fim) {
        if (data == null || inicio == null || fim == null) return false;
        
        return !data.before(inicio) && !data.after(fim);
    }
    
    /**
     * Converte timestamp em milissegundos para Date
     */
    public static Date timestampParaDate(long timestamp) {
        return new Date(timestamp);
    }
    
    /**
     * Obtém apenas a data (remove hora, minuto, segundo)
     */
    public static Date getApenasData(Date data) {
        if (data == null) return null;
        
        Calendar cal = Calendar.getInstance(LOCALE_BR);
        cal.setTime(data);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Método para validar datas no formato DD/MM/YYYY (compatibilidade)
     */
    public static boolean isDataDDMMYYYYValida(String dataStr) {
        return isDataValida(dataStr, FORMATO_DDMMYYYY);
    }
    
    /**
     * Método para converter de DD/MM/YYYY para Date (compatibilidade)
     */
    public static Date stringDDMMYYYYParaDate(String dataStr) throws ParseException {
        return stringParaDate(dataStr, FORMATO_DDMMYYYY);
    }
    
    /**
     * Método para formatar data como DD/MM/YYYY (compatibilidade)
     */
    public static String formatarDDMMYYYY(Date data) {
        return dateParaString(data, FORMATO_DDMMYYYY);
    }
    
    /**
     * Valida se a data está no passado
     */
    public static boolean isDataNoPassado(Date data) {
        if (data == null) return false;
        return data.before(getDataAtual());
    }
    
    /**
     * Valida se a data está no futuro
     */
    public static boolean isDataNoFuturo(Date data) {
        if (data == null) return false;
        return data.after(getDataAtual());
    }
    
    /**
     * Obtém o ano de uma data
     */
    public static int getAno(Date data) {
        if (data == null) return 0;
        
        Calendar cal = Calendar.getInstance(LOCALE_BR);
        cal.setTime(data);
        return cal.get(Calendar.YEAR);
    }
    
    /**
     * Obtém o mês de uma data (1-12)
     */
    public static int getMes(Date data) {
        if (data == null) return 0;
        
        Calendar cal = Calendar.getInstance(LOCALE_BR);
        cal.setTime(data);
        return cal.get(Calendar.MONTH) + 1; // Calendar.MONTH é 0-based
    }
    
    /**
     * Obtém o dia do mês de uma data (1-31)
     */
    public static int getDiaDoMes(Date data) {
        if (data == null) return 0;
        
        Calendar cal = Calendar.getInstance(LOCALE_BR);
        cal.setTime(data);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}