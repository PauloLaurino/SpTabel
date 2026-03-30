package com.selador.enums;

/**
 * Categorias gerais de operações para agrupamento e relatórios
 */
public enum CategoriaOperacao {
    
    APONTAMENTO("Apontamento", "Operações de apontamento inicial"),
    INTIMACAO("Intimação/Digitalização", "Operações de intimação e digitalização"),
    BAIXA("Baixa", "Operações de baixa (pagamento, instrumento, devolução)"),
    SUSPENSAO_REVOGACAO("Suspensão/Revogação", "Operações de suspensão e revogação"),
    RETIRADA("Retirada", "Operações de retirada de cartório"),
    DISTRIBUICAO("Distribuição", "Operações de distribuição"),
    EDITAL_JORNAL("Edital/Jornal", "Publicações em edital e jornal"),
    OUTROS("Outros", "Demais operações");
    
    private final String nome;
    private final String descricao;
    
    CategoriaOperacao(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
    
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    
    /**
     * Obtém todos os tipos de operação desta categoria
     */
    public java.util.List<TipoOperacao> getTiposOperacao() {
        java.util.List<TipoOperacao> tipos = new java.util.ArrayList<>();
        
        for (TipoOperacao tipo : TipoOperacao.values()) {
            if (tipo.getCategoria() == this) {
                tipos.add(tipo);
            }
        }
        
        return tipos;
    }
    
    @Override
    public String toString() {
        return nome;
    }
}