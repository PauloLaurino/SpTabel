package com.selador.enums;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tipos de selos disponíveis no sistema (campo tiposelo_sel na tabela selos)
 * Baseado na tabela de tipos do Funarpen
 */
public enum TipoSelo {
    
    // Selos padrão (TP = Tipo Padrão)
    TP1("0001", "TP1", "Selo Tipo 1 - Protesto Comum", 5.21, "PROTESTO"),
    TPD("0003", "TPD", "Selo Tipo D - Distribuição", 10.42, "DISTRIBUICAO"),
    TPI("0004", "TPI", "Selo Tipo I - Intimação/Digitalização", 7.81, "INTIMACAO"),
    TP3("0009", "TP3", "Selo Tipo 3 - Edital", 20.83, "EDITAL"),
    TP4("0010", "TP4", "Selo Tipo 4 - Jornal", 15.63, "JORNAL");
    
    private final String codigo;
    private final String sigla;
    private final String descricao;
    private final double valorPadrao;
    private final String categoria;
    
    // Caches para busca rápida
    private static final Map<String, TipoSelo> POR_CODIGO = new HashMap<>();
    private static final Map<String, TipoSelo> POR_SIGLA = new HashMap<>();
    private static final Map<String, List<TipoSelo>> POR_CATEGORIA = new HashMap<>();
    
    static {
        for (TipoSelo tipo : values()) {
            POR_CODIGO.put(tipo.codigo, tipo);
            POR_SIGLA.put(tipo.sigla.toUpperCase(), tipo);
            
            // Agrupa por categoria
            POR_CATEGORIA
                .computeIfAbsent(tipo.categoria, k -> new ArrayList<>())
                .add(tipo);
        }
    }
    
    TipoSelo(String codigo, String sigla, String descricao, double valorPadrao, String categoria) {
        this.codigo = codigo;
        this.sigla = sigla;
        this.descricao = descricao;
        this.valorPadrao = valorPadrao;
        this.categoria = categoria;
    }
    
    // Getters
    public String getCodigo() { return codigo; }
    public String getSigla() { return sigla; }
    public String getDescricao() { return descricao; }
    public double getValorPadrao() { return valorPadrao; }
    public String getCategoria() { return categoria; }
    
    /**
     * Obtém o enum a partir do código
     */
    public static TipoSelo fromCodigo(String codigo) {
        if (codigo == null) return null;
        TipoSelo tipo = POR_CODIGO.get(codigo.trim());
        if (tipo == null) {
            throw new IllegalArgumentException("Código de tipo de selo desconhecido: " + codigo);
        }
        return tipo;
    }
    
    /**
     * Obtém o enum a partir da sigla (case-insensitive)
     */
    public static TipoSelo fromSigla(String sigla) {
        if (sigla == null) return null;
        return POR_SIGLA.get(sigla.trim().toUpperCase());
    }
    
    /**
     * Verifica se o código é válido
     */
    public static boolean isValid(String codigo) {
        if (codigo == null) return false;
        return POR_CODIGO.containsKey(codigo.trim());
    }
    
    /**
     * Obtém tipos por categoria
     */
    public static List<TipoSelo> getPorCategoria(String categoria) {
        if (categoria == null) return Collections.emptyList();
        return POR_CATEGORIA.getOrDefault(categoria, Collections.emptyList());
    }
    
    /**
     * Retorna todos os tipos principais (TP1, TPD, TPI, TP3, TP4)
     */
    public static List<TipoSelo> getPrincipais() {
        return Arrays.asList(TP1, TPD, TPI, TP3, TP4);
    }
    
    /**
     * Retorna todos os tipos especiais
     */
    public static List<TipoSelo> getEspeciais() {
        return Arrays.stream(values())
            .filter(t -> !getPrincipais().contains(t))
            .collect(Collectors.toList());
    }
    
    /**
     * Mapeia tipo de operação para tipo de selo padrão
     */
    public static TipoSelo getParaTipoOperacao(TipoOperacao tipoOperacao) {
        if (tipoOperacao == null) return TPI; // Default
        
        // Usar if-else ou switch baseado no que existe em TipoOperacao
        // Primeiro, vamos verificar se conseguimos obter o nome da operação
        String operacaoNome = tipoOperacao.name();
        
        switch (operacaoNome) {
            case "APONTAMENTO": return TP1;
            case "INTIMA_DIGITALIZA": return TPI;
            case "RETIRADA": return TP1;
            case "PAGAMENTO":
                return TP1;
            case "INSTRUMENTO_DIFERIDO":
                return TP4;
            case "INSTRUMENTO_ANTECIPADO":
                return TPD;
            case "REVOGACAO": // Se este campo existir
                return TPI;
            case "SUSTACAO":
            case "SUSTACAO_DEFINITIVA":
            case "SUSPENSAO":
                return TPI;
            case "REVOGACAO_SUSPENSO":
            case "SEGUNDA_REVOGACAO":
                return TPI;
            default:
                return TPI; // Default para intimação/digitalização
        }
    }
    
    /**
     * Verifica se este tipo de selo é válido para um tipo de operação
     */
    public boolean isValidoPara(TipoOperacao tipoOperacao) {
        TipoSelo padrao = getParaTipoOperacao(tipoOperacao);
        return this == padrao;
    }
    
    /**
     * Retorna se é um selo de alto valor (> R$ 15,00)
     */
    public boolean isAltoValor() {
        return this.valorPadrao > 15.00;
    }
    
    /**
     * Retorna se é um selo de baixo valor (< R$ 8,00)
     */
    public boolean isBaixoValor() {
        return this.valorPadrao < 8.00;
    }
    
    /**
     * Formata o valor para exibição
     */
    public String getValorFormatado() {
        return String.format("R$ %.2f", this.valorPadrao);
    }
    
    /**
     * Retorna a cor CSS recomendada para exibição
     */
    public String getCorCSS() {
        switch (this) {
            case TP1: return "#bd1237"; // Vermelho
            case TPD: return "#bd1237"; // Vermelho
            case TPI: return "#bd1237"; // Vermelho
            case TP3: return "#4caf50"; // Verde
            case TP4: return "#0b1a6e"; // Azul Escuro
            default: return "#607d8b"; // Cinza
        }
    }
    
    /**
     * Retorna o ícone recomendado para exibição
     */
    public String getIcone() {
        switch (this) {
            case TP1: return "🏷️";
            case TPD: return "📦";
            case TPI: return "📄";
            case TP3: return "📢";
            case TP4: return "📰";
            default: return "🏷️";
        }
    }
    
    @Override
    public String toString() {
        return sigla + " - " + descricao + " (" + getValorFormatado() + ")";
    }
}