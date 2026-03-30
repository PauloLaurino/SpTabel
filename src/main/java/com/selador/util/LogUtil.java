package com.selador.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário para logging consistente do sistema
 */
public class LogUtil {
    
    private static final String LOG_DIR = "logs";
    private static final SimpleDateFormat DATE_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat FILE_DATE_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd");
    
    private static PrintWriter logWriter = null;
    private static String currentLogFile = null;
    private static String nivelMinimo = "INFO";
    
    static {
        inicializarLog();
        Runtime.getRuntime().addShutdownHook(new Thread(LogUtil::fecharLog));
    }
    
    private LogUtil() {
        // Classe utilitária - não instanciável
    }
    
    /**
     * Inicializa sistema de log
     */
    private static synchronized void inicializarLog() {
        try {
            // Criar diretório de logs se não existir
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Nome do arquivo com data
            String dataAtual = FILE_DATE_FORMAT.format(new Date());
            currentLogFile = LOG_DIR + File.separator + "selador-" + dataAtual + ".log";
            
            // Configurar writer (append mode)
            logWriter = new PrintWriter(new FileWriter(currentLogFile, true), true);
            
            logInterno("SISTEMA", "INFO", "Sistema de log inicializado: " + currentLogFile);
            
        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Não foi possível inicializar sistema de log: " + 
                e.getMessage());
            // Fallback para console
            logWriter = new PrintWriter(System.out, true);
        }
    }
    
    /**
     * Registra log genérico
     */
    public static void log(String tipo, String nivel, String mensagem) {
        // Verificar se nível é suficiente
        if (!isNivelSuficiente(nivel)) {
            return;
        }
        
        logInterno(tipo, nivel, mensagem);
    }
    
    /**
     * Verifica se o nível é suficiente para logar
     */
    private static boolean isNivelSuficiente(String nivel) {
        String[] niveis = {"DEBUG", "INFO", "WARN", "ERROR", "FATAL"};
        int nivelAtualIdx = -1;
        int nivelMinimoIdx = -1;
        
        for (int i = 0; i < niveis.length; i++) {
            if (niveis[i].equalsIgnoreCase(nivel)) {
                nivelAtualIdx = i;
            }
            if (niveis[i].equalsIgnoreCase(nivelMinimo)) {
                nivelMinimoIdx = i;
            }
        }
        
        if (nivelAtualIdx == -1) nivelAtualIdx = 1; // Default INFO
        if (nivelMinimoIdx == -1) nivelMinimoIdx = 1; // Default INFO
        
        return nivelAtualIdx >= nivelMinimoIdx;
    }
    
    /**
     * Método interno de logging
     */
    private static synchronized void logInterno(String tipo, String nivel, String mensagem) {
        if (logWriter == null) {
            inicializarLog();
        }
        
        String timestamp = DATE_FORMAT.format(new Date());
        String thread = Thread.currentThread().getName();
        
        String logEntry = String.format("[%s] [%s] [%s] [%s] [%s] %s",
            timestamp, nivel, thread, tipo, getCallerInfo(), mensagem);
        
        logWriter.println(logEntry);
        
        // Se for ERROR ou FATAL, também enviar para stderr
        if ("ERROR".equalsIgnoreCase(nivel) || "FATAL".equalsIgnoreCase(nivel)) {
            System.err.println(logEntry);
        }
    }
    
    /**
     * Obtém informações do chamador
     */
    private static String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        // Encontrar o primeiro elemento fora do LogUtil
        for (int i = 0; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (!className.equals(LogUtil.class.getName()) && 
                !className.startsWith("java.lang.Thread")) {
                
                String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
                return simpleClassName + "." + stackTrace[i].getMethodName() + 
                       ":" + stackTrace[i].getLineNumber();
            }
        }
        
        return "Desconhecido";
    }
    
    /**
     * Define nível mínimo de log
     */
    public static void setNivelMinimo(String nivel) {
        nivelMinimo = nivel;
        log("CONFIGURACAO", "INFO", 
            "Nível mínimo de log alterado para: " + nivel);
    }
    
    /**
     * Registra início de operação
     */
    public static void logInicio(String tipo, String operacao) {
        log(tipo, "INFO", "INÍCIO: " + operacao);
    }
    
    /**
     * Registra fim de operação
     */
    public static void logFim(String tipo, String operacao, long duracaoMs) {
        log(tipo, "INFO", 
            String.format("FIM: %s [%d ms]", operacao, duracaoMs));
    }
    
    /**
     * Registra erro com exceção
     */
    public static void logErro(String tipo, String mensagem, Throwable excecao) {
        log(tipo, "ERROR", mensagem + " - " + excecao.getMessage());
        
        // Log stack trace apenas em DEBUG
        if ("DEBUG".equalsIgnoreCase(nivelMinimo)) {
            StringBuilder stackTrace = new StringBuilder();
            for (StackTraceElement element : excecao.getStackTrace()) {
                stackTrace.append("    ").append(element.toString()).append("\n");
            }
            log(tipo, "DEBUG", "Stack Trace:\n" + stackTrace.toString());
        }
    }
    
    /**
     * Registra acesso a banco de dados
     */
    public static void logBancoDados(String operacao, String parametros, long duracaoMs) {
        String mensagem = String.format("DB: %s | Params: %s | Tempo: %d ms", 
            operacao, parametros, duracaoMs);
        
        String tipo = "OPERACAO_BANCO";
        String nivel = "DEBUG";
        
        if (duracaoMs > 1000) { // Mais de 1 segundo é WARN
            nivel = "WARN";
            tipo = "ERRO_BANCO";
        } else if (duracaoMs > 100) { // Mais de 100ms é INFO
            nivel = "INFO";
        }
        
        log(tipo, nivel, mensagem);
    }
    
    /**
     * Registra requisição HTTP
     */
    public static void logRequisicao(String metodo, String endpoint, 
                                     String parametros, int status, long duracaoMs) {
        String mensagem = String.format("HTTP: %s %s | Params: %s | Status: %d | Tempo: %d ms",
            metodo, endpoint, parametros, status, duracaoMs);
        
        String tipo = "REQUISICAO_HTTP";
        String nivel = "INFO";
        
        if (status >= 400) {
            nivel = "WARN";
        }
        if (status >= 500) {
            nivel = "ERROR";
        }
        
        log(tipo, nivel, mensagem);
    }
    
    /**
     * Rotaciona arquivo de log se necessário (verificar diariamente)
     */
    public static synchronized void verificarRotacao() {
        String dataAtual = FILE_DATE_FORMAT.format(new Date());
        String arquivoEsperado = LOG_DIR + File.separator + "selador-" + dataAtual + ".log";
        
        if (!arquivoEsperado.equals(currentLogFile)) {
            logInterno("SISTEMA", "INFO", "Rotacionando arquivo de log");
            fecharLog();
            inicializarLog();
        }
    }
    
    /**
     * Fecha recursos do log
     */
    private static synchronized void fecharLog() {
        if (logWriter != null) {
            logInterno("SISTEMA", "INFO", "Fechando sistema de log");
            logWriter.flush();
            logWriter.close();
            logWriter = null;
        }
    }
    
    /**
     * Obtém estatísticas do log
     */
    public static Map<String, Object> getEstatisticasLog() {
        Map<String, Object> estatisticas = new HashMap<>();
        
        try {
            File dir = new File(LOG_DIR);
            File[] arquivos = dir.listFiles((d, nome) -> nome.startsWith("selador-") && nome.endsWith(".log"));
            
            if (arquivos != null) {
                long tamanhoTotal = 0;
                for (File arquivo : arquivos) {
                    tamanhoTotal += arquivo.length();
                }
                
                estatisticas.put("totalArquivos", arquivos.length);
                estatisticas.put("tamanhoTotalBytes", tamanhoTotal);
                estatisticas.put("tamanhoTotalMB", String.format("%.2f", tamanhoTotal / (1024.0 * 1024.0)));
                
                if (currentLogFile != null) {
                    File logAtual = new File(currentLogFile);
                    if (logAtual.exists()) {
                        estatisticas.put("tamanhoAtualBytes", logAtual.length());
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar erros em estatísticas
        }
        
        return estatisticas;
    }
    
    /**
     * Métodos de conveniência
     */
    
    public static void info(String mensagem) {
        log("GERAL", "INFO", mensagem);
    }
    
    public static void debug(String mensagem) {
        log("GERAL", "DEBUG", mensagem);
    }
    
    public static void warn(String mensagem) {
        log("GERAL", "WARN", mensagem);
    }
    
    public static void error(String mensagem) {
        log("GERAL", "ERROR", mensagem);
    }
    
    public static void error(String mensagem, Throwable excecao) {
        logErro("GERAL", mensagem, excecao);
    }
    
    /**
     * Método para logar entrada/saída de métodos
     */
    public static void logEntrada(String metodo, Object... parametros) {
        StringBuilder params = new StringBuilder();
        for (Object param : parametros) {
            if (params.length() > 0) params.append(", ");
            params.append(param != null ? param.toString() : "null");
        }
        log("METODO", "DEBUG", String.format("ENTRADA: %s(%s)", metodo, params.toString()));
    }
    
    public static void logSaida(String metodo, Object resultado) {
        log("METODO", "DEBUG", String.format("SAÍDA: %s -> %s", 
            metodo, resultado != null ? resultado.toString() : "null"));
    }
}