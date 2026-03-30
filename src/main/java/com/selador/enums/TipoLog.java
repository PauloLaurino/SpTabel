package com.selador.enums;

import java.time.LocalDateTime;

/**
 * Tipos de log/registro para auditoria e monitoramento
 */
public enum TipoLog {
    
    // Logs de sistema
    SISTEMA_INICIO("SISTEMA_INICIO", "Sistema", "Inicialização do sistema"),
    SISTEMA_FIM("SISTEMA_FIM", "Sistema", "Finalização do sistema"),
    SISTEMA_ERRO("SISTEMA_ERRO", "Sistema", "Erro no sistema"),
    SISTEMA_WARNING("SISTEMA_WARNING", "Sistema", "Aviso do sistema"),
    
    // Logs de autenticação
    LOGIN_SUCESSO("LOGIN_SUCESSO", "Autenticação", "Login realizado com sucesso"),
    LOGIN_FALHA("LOGIN_FALHA", "Autenticação", "Falha no login"),
    LOGOUT("LOGOUT", "Autenticação", "Logout realizado"),
    
    // Logs de banco de dados
    DB_CONEXAO("DB_CONEXAO", "Banco de Dados", "Conexão com banco de dados"),
    DB_CONSULTA("DB_CONSULTA", "Banco de Dados", "Consulta ao banco de dados"),
    DB_ATUALIZACAO("DB_ATUALIZACAO", "Banco de Dados", "Atualização no banco de dados"),
    DB_ERRO("DB_ERRO", "Banco de Dados", "Erro no banco de dados"),
    
    // Logs de selagem
    SELAGEM_INICIO("SELAGEM_INICIO", "Selagem", "Início do processo de selagem"),
    SELAGEM_FIM("SELAGEM_FIM", "Selagem", "Fim do processo de selagem"),
    SELAGEM_APONTAMENTO("SELAGEM_APONTAMENTO", "Selagem", "Selagem de apontamento"),
    SELAGEM_ERRO("SELAGEM_ERRO", "Selagem", "Erro na selagem"),
    SELAGEM_SUCESSO("SELAGEM_SUCESSO", "Selagem", "Selagem concluída com sucesso"),
    
    // Logs de integração
    INTEGRACAO_INICIO("INTEGRACAO_INICIO", "Integração", "Início da integração"),
    INTEGRACAO_FIM("INTEGRACAO_FIM", "Integração", "Fim da integração"),
    INTEGRACAO_ENVIO("INTEGRACAO_ENVIO", "Integração", "Envio para sistema externo"),
    INTEGRACAO_RETORNO("INTEGRACAO_RETORNO", "Integração", "Retorno do sistema externo"),
    INTEGRACAO_ERRO("INTEGRACAO_ERRO", "Integração", "Erro na integração"),
    
    // Logs de negócio
    BUSCA_APONTAMENTOS("BUSCA_APONTAMENTOS", "Negócio", "Busca de apontamentos"),
    ATUALIZACAO_STATUS("ATUALIZACAO_STATUS", "Negócio", "Atualização de status"),
    GERACAO_RELATORIO("GERACAO_RELATORIO", "Negócio", "Geração de relatório"),
    EXPORTACAO_DADOS("EXPORTACAO_DADOS", "Negócio", "Exportação de dados"),
    
    // Logs de auditoria
    AUDITORIA_ACESSO("AUDITORIA_ACESSO", "Auditoria", "Acesso a recurso"),
    AUDITORIA_ALTERACAO("AUDITORIA_ALTERACAO", "Auditoria", "Alteração de dados"),
    AUDITORIA_EXCLUSAO("AUDITORIA_EXCLUSAO", "Auditoria", "Exclusão de dados");
    
    private final String codigo;
    private final String categoria;
    private final String descricao;
    
    TipoLog(String codigo, String categoria, String descricao) {
        this.codigo = codigo;
        this.categoria = categoria;
        this.descricao = descricao;
    }
    
    // Getters
    public String getCodigo() { return codigo; }
    public String getCategoria() { return categoria; }
    public String getDescricao() { return descricao; }
    
    /**
     * Obtém o nível de log correspondente
     */
    public NivelLog getNivelLog() {
        switch (this) {
            case SISTEMA_ERRO:
            case DB_ERRO:
            case SELAGEM_ERRO:
            case INTEGRACAO_ERRO:
            case LOGIN_FALHA:
                return NivelLog.ERROR;
            case SISTEMA_WARNING:
            case AUDITORIA_EXCLUSAO:
                return NivelLog.WARN;
            case SISTEMA_INICIO:
            case SISTEMA_FIM:
            case SELAGEM_INICIO:
            case SELAGEM_FIM:
            case INTEGRACAO_INICIO:
            case INTEGRACAO_FIM:
            case LOGIN_SUCESSO:
            case LOGOUT:
                return NivelLog.INFO;
            default:
                return NivelLog.DEBUG;
        }
    }
    
    /**
     * Formata uma mensagem de log
     */
    public String formatarMensagem(String mensagem, String usuario) {
        LocalDateTime agora = LocalDateTime.now();
        return String.format("[%s] [%s] [%s] %s - %s", 
            agora.toString(),
            this.codigo,
            this.getNivelLog(),
            usuario != null ? "Usuário: " + usuario : "Sistema",
            mensagem);
    }
    
    /**
     * Verifica se é um log de erro
     */
    public boolean isErro() {
        return getNivelLog() == NivelLog.ERROR;
    }
    
    /**
     * Verifica se é um log de auditoria
     */
    public boolean isAuditoria() {
        return this.categoria.equals("Auditoria");
    }
    
    @Override
    public String toString() {
        return categoria + ": " + descricao;
    }
}