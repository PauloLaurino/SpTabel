package com.selador.enums;

import java.util.*;

/**
 * Status do processo de selagem de apontamentos
 */
public enum StatusSelagem {
    
    // Status principais
    PENDENTE("PENDENTE", "Pendente", "Aguardando processamento", 1),
    PROCESSANDO("PROCESSANDO", "Processando", "Em processamento", 2),
    SELADO("SELADO", "Selado", "Selagem concluída com sucesso", 3),
    ERRO("ERRO", "Erro", "Erro na selagem", 4),
    CANCELADO("CANCELADO", "Cancelado", "Selagem cancelada", 5),
    
    // Status específicos
    SELO_INDISPONIVEL("SELO_INDISPONIVEL", "Selo Indisponível", "Nenhum selo disponível", 6),
    REGISTRO_NAO_ENCONTRADO("REGISTRO_NAO_ENCONTRADO", "Registro Não Encontrado", 
                           "Registro não encontrado no banco", 7),
    ERRO_BANCO_DADOS("ERRO_BANCO_DADOS", "Erro Banco de Dados", 
                    "Erro na comunicação com o banco", 8),
    VALIDACAO("VALIDACAO", "Erro Validação", "Erro na validação dos dados", 9),
    INTEGRACAO("INTEGRACAO", "Erro Integração", "Erro na integração com sistemas externos", 10),
    
    // Status de sistema
    AGUARDANDO_RETORNO("AGUARDANDO_RETORNO", "Aguardando Retorno", 
                       "Aguardando retorno do sistema externo", 11),
    RETORNO_RECEBIDO("RETORNO_RECEBIDO", "Retorno Recebido", 
                    "Retorno do sistema externo recebido", 12),
    CONCLUIDO("CONCLUIDO", "Concluído", "Processo totalmente concluído", 13);
    
    private final String codigo;
    private final String descricao;
    private final String detalhe;
    private final int ordem;
    
    // Caches para busca rápida
    private static final Map<String, StatusSelagem> POR_CODIGO = new HashMap<>();
    private static final Map<String, StatusSelagem> POR_DESCRICAO = new HashMap<>();
    
    static {
        for (StatusSelagem status : values()) {
            POR_CODIGO.put(status.codigo, status);
            POR_DESCRICAO.put(status.descricao.toUpperCase(), status);
        }
    }
    
    StatusSelagem(String codigo, String descricao, String detalhe, int ordem) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.detalhe = detalhe;
        this.ordem = ordem;
    }
    
    // Getters
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public String getDetalhe() { return detalhe; }
    public int getOrdem() { return ordem; }
    
    /**
     * Obtém o enum a partir do código
     */
    public static StatusSelagem fromCodigo(String codigo) {
        if (codigo == null) return null;
        StatusSelagem status = POR_CODIGO.get(codigo.trim());
        if (status == null) {
            throw new IllegalArgumentException("Código de status de selagem desconhecido: " + codigo);
        }
        return status;
    }
    
    /**
     * Obtém o enum a partir da descrição (case-insensitive)
     */
    public static StatusSelagem fromDescricao(String descricao) {
        if (descricao == null) return null;
        return POR_DESCRICAO.get(descricao.trim().toUpperCase());
    }
    
    /**
     * Verifica se o código é válido
     */
    public static boolean isValid(String codigo) {
        if (codigo == null) return false;
        return POR_CODIGO.containsKey(codigo.trim());
    }
    
    /**
     * Retorna todos os status finais (que não mudam mais)
     */
    public static List<StatusSelagem> getFinais() {
        return Arrays.asList(SELADO, ERRO, CANCELADO, CONCLUIDO);
    }
    
    /**
     * Retorna todos os status intermediários
     */
    public static List<StatusSelagem> getIntermediarios() {
        return Arrays.asList(PENDENTE, PROCESSANDO, AGUARDANDO_RETORNO, RETORNO_RECEBIDO);
    }
    
    /**
     * Retorna status de erro
     */
    public static List<StatusSelagem> getErros() {
        return Arrays.asList(ERRO, SELO_INDISPONIVEL, REGISTRO_NAO_ENCONTRADO, 
                           ERRO_BANCO_DADOS, VALIDACAO, INTEGRACAO);
    }
    
    /**
     * Verifica se este status é final
     */
    public boolean isFinal() {
        return getFinais().contains(this);
    }
    
    /**
     * Verifica se este status é de erro
     */
    public boolean isErro() {
        return getErros().contains(this);
    }
    
    /**
     * Verifica se este status é de sucesso
     */
    public boolean isSucesso() {
        return this == SELADO || this == CONCLUIDO;
    }
    
    /**
     * Retorna se pode ser reprocessado
     */
    public boolean podeReprocessar() {
        return this == ERRO || 
               this == SELO_INDISPONIVEL || 
               this == REGISTRO_NAO_ENCONTRADO ||
               this == VALIDACAO;
    }
    
    /**
     * Retorna a cor CSS recomendada para exibição
     */
    public String getCorCSS() {
        switch (this) {
            case PENDENTE: return "#ff9800"; // Laranja
            case PROCESSANDO: return "#2196f3"; // Azul
            case SELADO: return "#4caf50"; // Verde
            case CONCLUIDO: return "#4caf50"; // Verde
            case ERRO: return "#f44336"; // Vermelho
            case CANCELADO: return "#9e9e9e"; // Cinza
            case SELO_INDISPONIVEL: return "#ff5722"; // Laranja escuro
            case REGISTRO_NAO_ENCONTRADO: return "#795548"; // Marrom
            default: return "#607d8b"; // Cinza azulado
        }
    }
    
    /**
     * Retorna o ícone recomendado para exibição
     */
    public String getIcone() {
        switch (this) {
            case PENDENTE: return "⏳";
            case PROCESSANDO: return "⚙️";
            case SELADO: return "✅";
            case CONCLUIDO: return "🏁";
            case ERRO: return "❌";
            case CANCELADO: return "🚫";
            case SELO_INDISPONIVEL: return "🏷️❓";
            case REGISTRO_NAO_ENCONTRADO: return "📋❓";
            default: return "📋";
        }
    }
    
    /**
     * Retorna próximos status possíveis
     */
    public List<StatusSelagem> getProximosStatus() {
        switch (this) {
            case PENDENTE:
                return Arrays.asList(PROCESSANDO, CANCELADO);
            case PROCESSANDO:
                return Arrays.asList(SELADO, ERRO, SELO_INDISPONIVEL, REGISTRO_NAO_ENCONTRADO);
            case SELADO:
                return Arrays.asList(CONCLUIDO, AGUARDANDO_RETORNO);
            case AGUARDANDO_RETORNO:
                return Arrays.asList(RETORNO_RECEBIDO, ERRO);
            case RETORNO_RECEBIDO:
                return Arrays.asList(CONCLUIDO);
            case ERRO:
            case SELO_INDISPONIVEL:
            case REGISTRO_NAO_ENCONTRADO:
                return Arrays.asList(PROCESSANDO, CANCELADO); // Reprocessar
            default:
                return Collections.emptyList();
        }
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}