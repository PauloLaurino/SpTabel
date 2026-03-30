package com.selador.enums;

/**
 * Níveis de log para controle de verbosidade
 */
public enum NivelLog {
    
    ERROR(4, "ERROR", "Erros críticos que impedem o funcionamento"),
    WARN(3, "WARN", "Avisos sobre situações anormais"),
    INFO(2, "INFO", "Informações sobre o fluxo normal"),
    DEBUG(1, "DEBUG", "Informações detalhadas para depuração"),
    TRACE(0, "TRACE", "Informações muito detalhadas");
    
    private final int nivel;
    private final String nome;
    private final String descricao;
    
    NivelLog(int nivel, String nome, String descricao) {
        this.nivel = nivel;
        this.nome = nome;
        this.descricao = descricao;
    }
    
    public int getNivel() { return nivel; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    
    /**
     * Verifica se este nível inclui outro nível
     */
    public boolean inclui(NivelLog outro) {
        return this.nivel <= outro.nivel;
    }
    
    /**
     * Obtém o enum a partir do nome (case-insensitive)
     */
    public static NivelLog fromNome(String nome) {
        if (nome == null) return INFO; // Default
        
        for (NivelLog nivel : values()) {
            if (nivel.nome.equalsIgnoreCase(nome.trim())) {
                return nivel;
            }
        }
        
        return INFO; // Default
    }
    
    /**
     * Retorna a cor CSS recomendada para exibição
     */
    public String getCorCSS() {
        switch (this) {
            case ERROR: return "#f44336"; // Vermelho
            case WARN: return "#ff9800"; // Laranja
            case INFO: return "#2196f3"; // Azul
            case DEBUG: return "#4caf50"; // Verde
            case TRACE: return "#9e9e9e"; // Cinza
            default: return "#607d8b";
        }
    }
    
    @Override
    public String toString() {
        return nome;
    }
}