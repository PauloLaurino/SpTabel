package com.selador.enums;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Enum que representa os tipos de operação/atos no sistema de selagem.
 * Cada tipo mapeia para uma coluna específica na tabela ctp001 e tem
 * regras de formatação de data específicas.
 */
public enum TipoOperacao {
    
    // Operações com formato de data YYYYMMDD (sem barras)
    APONTAMENTO("APONTAMENTO", "dataapo_001", FormatoData.YYYYMMDD, "701"),
    RETIRADA("RETIRADA", "dataret_001", FormatoData.YYYYMMDD, "702"),
    PAGAMENTO("PAGAMENTO", "datapag_001", FormatoData.YYYYMMDD, "703"),
    INSTRUMENTO_DIFERIDO("INSTRUMENTO DIFERIDO", "databai_001", FormatoData.YYYYMMDD, "704"),
    INSTRUMENTO_ANTECIPADO("INSTRUMENTO ANTECIPADO", "databai_001", FormatoData.YYYYMMDD, "705"),
    SEGUNDA_REVOGACAO("SEGUNDA REVOGACAO", "datasusp2_001", FormatoData.YYYYMMDD, "706"),
    
    // Operações com formato de data DDMMYYYY (sem barras)
    INTIMA_DIGITALIZA("INTIMA/DIGITALIZA", "dataapo_001_001", FormatoData.DDMMYYYY, "707"),
    // ADICIONAR OPERAÇÃO COMPOSTA QUE ESTÁ FALTANDO
    INTIMA_DIGITALIZA_COMPOSTO("INTIMAÇÃO/DIGITALIZAÇÃO", "dataapo_001_001", FormatoData.DDMMYYYY, "702-738/725-740/703"),
    DEVOLUCAO("DEVOLUCAO", "dataret_001", FormatoData.DDMMYYYY, "708"),
    SUSTACAO("SUSTACAO", "dtsusta_001", FormatoData.DDMMYYYY, "709"),
    REVOGACAO("REVOGACAO", "dtrevog_001", FormatoData.DDMMYYYY, "710"),
    SUSTACAO_DEFINITIVA("SUSTACAO DEFINITIVA", "dtsusdef_001", FormatoData.DDMMYYYY, "711"),
    SUSPENSAO("SUSPENSAO", "dtsuspenso_001", FormatoData.DDMMYYYY, "712"),
    REVOGACAO_SUSPENSO("REVOGACAO SUSPENSO", "dtrevsusp_001", FormatoData.DDMMYYYY, "713"),
    
    // ADICIONAR OUTRAS OPERAÇÕES COMPOSTAS DO MAPEAMENTO
    RETIRADA_BAIXA_COMPOSTO("RETIRADA/BAIXA", "dataret_001", FormatoData.YYYYMMDD, "705/735-736"),
    PAGAMENTO_BAIXA_COMPOSTO("PAGAMENTO/BAIXA", "datapag_001", FormatoData.YYYYMMDD, "704/735-736"),
    PROTESTO_CUSTAS_COMPOSTO("PROTESTO COM CUSTAS ANTECIPADAS", "databai_001", FormatoData.YYYYMMDD, "706"),
    INSTRUMENTO_DIFERIDO_COMPOSTO("INSTRUMENTO DIFERIDO", "databai_001", FormatoData.YYYYMMDD, "739");
    
    private final String descricao;
    private final String colunaBanco;
    private final FormatoData formatoData;
    private final String codigoTipoAto;
    
    // Cache para busca rápida
    private static final Map<String, TipoOperacao> POR_DESCRICAO = new HashMap<>();
    private static final Map<String, TipoOperacao> POR_CODIGO = new HashMap<>();
    
    static {
        for (TipoOperacao tipo : values()) {
            POR_DESCRICAO.put(tipo.descricao.toUpperCase(), tipo);
            // Permitir múltiplos códigos para o mesmo tipo
            POR_CODIGO.put(tipo.codigoTipoAto, tipo);
            
            // Se for um código composto, também mapear as partes individuais (opcional)
            if (tipo.codigoTipoAto.contains("/")) {
                String[] partes = tipo.codigoTipoAto.split("/");
                for (String parte : partes) {
                    parte = parte.trim();
                    if (!parte.isEmpty() && !POR_CODIGO.containsKey(parte)) {
                        POR_CODIGO.put(parte, tipo);
                    }
                }
            }
        }
    }
    
    TipoOperacao(String descricao, String colunaBanco, FormatoData formatoData, String codigoTipoAto) {
        this.descricao = descricao;
        this.colunaBanco = colunaBanco;
        this.formatoData = formatoData;
        this.codigoTipoAto = codigoTipoAto;
    }
    
    // Getters
    public String getDescricao() { return descricao; }
    public String getColunaBanco() { return colunaBanco; }
    public FormatoData getFormatoData() { return formatoData; }
    public String getCodigoTipoAto() { return codigoTipoAto; }
    
    /**
     * Obtém o enum a partir da descrição (case-insensitive)
     */
    public static TipoOperacao fromDescricao(String descricao) {
        if (descricao == null) return null;
        TipoOperacao tipo = POR_DESCRICAO.get(descricao.trim().toUpperCase());
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de operação desconhecido: " + descricao);
        }
        return tipo;
    }
    
    /**
     * Obtém o enum a partir do código do tipo de ato
     * ACEITA CÓDIGOS COMPOSTOS
     */
    public static TipoOperacao fromCodigoTipoAto(String codigo) {
        if (codigo == null) return null;
        
        // Tentar buscar direto
        TipoOperacao tipo = POR_CODIGO.get(codigo.trim());
        if (tipo != null) {
            return tipo;
        }
        
        // Se não encontrou, verificar se é um código composto
        if (codigo.contains("/")) {
            // Tentar encontrar por similaridade
            for (TipoOperacao op : values()) {
                if (op.codigoTipoAto.equals(codigo.trim())) {
                    return op;
                }
            }
        }
        
        return null; // Não lançar exceção, apenas retornar null
    }
    
    /**
     * Verifica se a descrição é válida
     */
    public static boolean isValid(String descricao) {
        if (descricao == null) return false;
        return POR_DESCRICAO.containsKey(descricao.trim().toUpperCase());
    }
    
    /**
     * Verifica se o código é válido (incluindo códigos compostos)
     */
    public static boolean isCodigoValido(String codigo) {
        if (codigo == null) return false;
        
        // Verificar no mapa de códigos
        if (POR_CODIGO.containsKey(codigo.trim())) {
            return true;
        }
        
        // Verificar se algum enum tem exatamente este código
        for (TipoOperacao op : values()) {
            if (op.codigoTipoAto.equals(codigo.trim())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Retorna todos os tipos como lista de descrições
     */
    public static List<String> getDescricoes() {
        return Arrays.stream(values())
            .map(TipoOperacao::getDescricao)
            .collect(Collectors.toList());
    }
    
    /**
     * Retorna tipos agrupados por formato de data
     */
    public static Map<FormatoData, List<TipoOperacao>> getAgrupadosPorFormatoData() {
        return Arrays.stream(values())
            .collect(Collectors.groupingBy(TipoOperacao::getFormatoData));
    }
    
    /**
     * Retorna tipos que usam uma determinada coluna no banco
     */
    public static List<TipoOperacao> getPorColunaBanco(String coluna) {
        return Arrays.stream(values())
            .filter(t -> t.getColunaBanco().equals(coluna))
            .collect(Collectors.toList());
    }
    
    /**
     * Formata uma data conforme as regras do tipo de operação
     */
    public String formatarDataParaBanco(String dataDDMMYYYY) {
        if (dataDDMMYYYY == null || !dataDDMMYYYY.matches("\\d{2}/\\d{2}/\\d{4}")) {
            throw new IllegalArgumentException("Data inválida. Formato esperado: DD/MM/YYYY");
        }
        
        String dataLimpa = dataDDMMYYYY.replace("/", "");
        
        switch (this.formatoData) {
            case YYYYMMDD:
                // Converte DDMMYYYY para YYYYMMDD
                return dataLimpa.substring(4, 8) + dataLimpa.substring(2, 4) + dataLimpa.substring(0, 2);
                
            case DDMMYYYY:
                // Mantém DDMMYYYY
                return dataLimpa;
                
            case DDMMYYYY_SEM_ZERO:
                // Formato DDMMYYYY sem zero à esquerda no dia
                // Remove zero do início do dia se existir
                String dia = dataLimpa.substring(0, 2);
                String mes = dataLimpa.substring(2, 4);
                String ano = dataLimpa.substring(4, 8);
                
                // Remove zero à esquerda do dia
                if (dia.startsWith("0")) {
                    dia = dia.substring(1);
                }
                
                return dia + mes + ano;
                
            case ISO_8601:
                // Converte DD/MM/YYYY para YYYY-MM-DD
                return dataLimpa.substring(4, 8) + "-" + dataLimpa.substring(2, 4) + "-" + dataLimpa.substring(0, 2);
                
            case DD_MM_YYYY:
                // Mantém DD/MM/YYYY (com barras)
                return dataDDMMYYYY;
                
            default:
                throw new IllegalStateException("Formato de data não implementado: " + this.formatoData);
        }
    }
    
    /**
     * Formata uma data do banco para exibição (DD/MM/YYYY)
     */
    public String formatarDataParaExibicao(String dataBanco) {
        if (dataBanco == null || dataBanco.trim().isEmpty()) {
            return "";
        }
        
        String dataLimpa = dataBanco.trim();
        
        switch (this.formatoData) {
            case YYYYMMDD:
                // Converte YYYYMMDD para DD/MM/YYYY
                if (dataLimpa.length() == 8) {
                    return dataLimpa.substring(6, 8) + "/" + 
                        dataLimpa.substring(4, 6) + "/" + 
                        dataLimpa.substring(0, 4);
                }
                break;
                
            case DDMMYYYY:
                // Converte DDMMYYYY para DD/MM/YYYY
                if (dataLimpa.length() == 8) {
                    return dataLimpa.substring(0, 2) + "/" + 
                        dataLimpa.substring(2, 4) + "/" + 
                        dataLimpa.substring(4, 8);
                }
                break;
                
            case DDMMYYYY_SEM_ZERO:
                // Formato DDMMYYYY sem zero à esquerda no dia
                // Exemplo: "1052024" representa "10/05/2024" sem zero
                if (dataLimpa.length() == 7) {
                    // Adiciona zero à esquerda se necessário
                    String dia = dataLimpa.length() > 0 ? dataLimpa.substring(0, Math.min(2, dataLimpa.length())) : "0";
                    if (dia.length() == 1) {
                        dia = "0" + dia;
                    }
                    String mes = dataLimpa.length() > 2 ? dataLimpa.substring(2, Math.min(4, dataLimpa.length())) : "0";
                    if (mes.length() == 1) {
                        mes = "0" + mes;
                    }
                    String ano = dataLimpa.length() > 4 ? dataLimpa.substring(4) : "";
                    
                    return dia + "/" + mes + "/" + ano;
                } else if (dataLimpa.length() == 8) {
                    // Já tem 8 dígitos, formata normalmente
                    return dataLimpa.substring(0, 2) + "/" + 
                        dataLimpa.substring(2, 4) + "/" + 
                        dataLimpa.substring(4, 8);
                }
                break;
                
            case ISO_8601:
                // Converte YYYY-MM-DD para DD/MM/YYYY
                if (dataLimpa.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    String[] partes = dataLimpa.split("-");
                    return partes[2] + "/" + partes[1] + "/" + partes[0];
                }
                break;
                
            case DD_MM_YYYY:
                // Já está no formato DD/MM/YYYY (ou similar)
                // Verifica se já tem barras
                if (dataLimpa.contains("/")) {
                    return dataLimpa;
                } else if (dataLimpa.length() == 8) {
                    // Converte DDMMYYYY para DD/MM/YYYY
                    return dataLimpa.substring(0, 2) + "/" + 
                        dataLimpa.substring(2, 4) + "/" + 
                        dataLimpa.substring(4, 8);
                }
                break;
                
            default:
                break;
        }
        
        // Se não conseguir converter, retorna o original
        return dataBanco;
    }
    
    /**
     * Verifica se esta operação requer selagem
     */
    public boolean requerSelagem() {
        // Todas as operações listadas requerem selagem
        return true;
    }
    
    /**
     * Retorna se o tipo é considerado uma "baixa"
     */
    public boolean isBaixa() {
        return this == PAGAMENTO || 
               this == INSTRUMENTO_DIFERIDO || 
               this == INSTRUMENTO_ANTECIPADO ||
               this == DEVOLUCAO;
    }
    
    /**
     * Retorna se o tipo é considerado uma "intimação"
     */
    public boolean isIntimacao() {
        return this == INTIMA_DIGITALIZA;
    }
    
    /**
     * Retorna se o tipo é considerado um "apontamento"
     */
    public boolean isApontamento() {
        return this == APONTAMENTO;
    }
    
    /**
     * Retorna a categoria da operação
     */
    public CategoriaOperacao getCategoria() {
        if (isApontamento()) return CategoriaOperacao.APONTAMENTO;
        if (isIntimacao()) return CategoriaOperacao.INTIMACAO;
        if (isBaixa()) return CategoriaOperacao.BAIXA;
        if (this.name().contains("REVOGACAO") || this.name().contains("SUSTACAO")) {
            return CategoriaOperacao.SUSPENSAO_REVOGACAO;
        }
        return CategoriaOperacao.OUTROS;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}