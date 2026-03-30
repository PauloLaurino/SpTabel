package com.selador.enums;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Enum auxiliar para mapear colunas de data na tabela ctp001
 */
public enum ColunaData {
    
    // Datas principais
    DATA_APONTAMENTO("dataapo_001", "Data Apontamento", "Data do apontamento inicial"),
    DATA_INTIMACAO("dtintimacao_001", "Data Intimação", "Data da intimação/digitalização"),
    DATA_RETIRADA("dataret_001", "Data Retirada", "Data de retirada do cartório"),
    DATA_PAGAMENTO("datapag_001", "Data Pagamento", "Data de pagamento"),
    DATA_BAIXA("databai_001", "Data Baixa", "Data de baixa/instrumento"),
    
    // Datas processuais
    DATA_DISTRIBUICAO("dtdistrib_001", "Data Distribuição", "Data de distribuição"),
    DATA_EDITAL("dtedital_001", "Data Edital", "Data de edital"),
    DATA_JORNAL("dtjornal_001", "Data Jornal", "Data de publicação no jornal"),
    DATA_ACEITE("dtaceite_001", "Data Aceite", "Data de aceite"),
    DATA_ACERTO("dtacerto_001", "Data Acerto", "Data de acerto"),
    
    // Datas de controle
    DATA_CONTROLE("dtcontrole_001", "Data Controle", "Data de controle"),
    DATA_DEPOSITO("dtdeposito_001", "Data Depósito", "Data de depósito"),
    DATA_DEVOLUCAO("dtdevol_001", "Data Devolução", "Data de devolução"),
    DATA_EMISSAO("dtemissao_001", "Data Emissão", "Data de emissão"),
    DATA_OCR("dataocr_001", "Data OCR", "Data do processamento OCR"),
    
    // Datas de vencimento
    DATA_VENCIMENTO("dtvenci_001", "Data Vencimento", "Data de vencimento"),
    DATA_POS_PROTESTO("dtposprot_001", "Data Pós-Protesto", "Data pós-protesto"),
    DATA_REV_SUSP("datarevsusp_001", "Data Revogação/Suspensão", "Data de revogação/suspensão"),
    
    // Datas específicas
    DATA_PROCESSO("dtproces_001", "Data Processo", "Data do processo"),
    DATA_RETORNO("dtretorno_001", "Data Retorno", "Data de retorno"),
    DATA_SUSPENSO("dtsuspenso_001", "Data Suspenso", "Data de suspenso"),
    DATA_SUSTACAO("dtsusta_001", "Data Sustação", "Data de sustação"),
    DATA_REVOGACAO("dtrevog_001", "Data Revogação", "Data de revogação"),
    DATA_SUSPENSAO_DEFINITIVA("dtsusdef_001", "Data Suspensão Definitiva", "Data de suspensão definitiva");
    
    private final String nomeColuna;
    private final String descricao;
    private final String detalhe;
    
    // Cache para busca rápida
    private static final Map<String, ColunaData> POR_NOME_COLUNA = new HashMap<>();
    
    static {
        for (ColunaData coluna : values()) {
            POR_NOME_COLUNA.put(coluna.nomeColuna.toUpperCase(), coluna);
        }
    }
    
    ColunaData(String nomeColuna, String descricao, String detalhe) {
        this.nomeColuna = nomeColuna;
        this.descricao = descricao;
        this.detalhe = detalhe;
    }
    
    // Getters
    public String getNomeColuna() { return nomeColuna; }
    public String getDescricao() { return descricao; }
    public String getDetalhe() { return detalhe; }
    
    /**
     * Obtém o enum a partir do nome da coluna (case-insensitive)
     */
    public static ColunaData fromNomeColuna(String nomeColuna) {
        if (nomeColuna == null) return null;
        ColunaData coluna = POR_NOME_COLUNA.get(nomeColuna.trim().toUpperCase());
        if (coluna == null) {
            throw new IllegalArgumentException("Coluna de data desconhecida: " + nomeColuna);
        }
        return coluna;
    }
    
    /**
     * Verifica se o nome da coluna é válido
     */
    public static boolean isValid(String nomeColuna) {
        if (nomeColuna == null) return false;
        return POR_NOME_COLUNA.containsKey(nomeColuna.trim().toUpperCase());
    }
    
    /**
     * Retorna colunas relacionadas a um tipo de operação (versão completa)
     */
    public static List<ColunaData> getPorTipoOperacao(TipoOperacao tipoOperacao) {
        if (tipoOperacao == null) return Collections.emptyList();
        
        // Mapeamento direto da coluna do tipo de operação
        String colunaPrincipal = tipoOperacao.getColunaBanco();
        ColunaData coluna = null;
        
        try {
            coluna = fromNomeColuna(colunaPrincipal);
        } catch (IllegalArgumentException e) {
            // Se não encontrar a coluna, retorna lista vazia
            return Collections.emptyList();
        }
        
        List<ColunaData> colunas = new ArrayList<>();
        if (coluna != null) {
            colunas.add(coluna);
        }
        
        // Adiciona colunas relacionadas
        switch (tipoOperacao) {
            case APONTAMENTO:
                colunas.add(DATA_OCR);
                colunas.add(DATA_VENCIMENTO);
                break;
            case INTIMA_DIGITALIZA:
                colunas.add(DATA_DISTRIBUICAO);
                break;
            case PAGAMENTO:
                colunas.add(DATA_BAIXA);
                colunas.add(DATA_CONTROLE);
                break;
            case INSTRUMENTO_DIFERIDO:
            case INSTRUMENTO_ANTECIPADO:
                colunas.add(DATA_PAGAMENTO);
                colunas.add(DATA_CONTROLE);
                break;
            case RETIRADA:
                colunas.add(DATA_RETIRADA);
                break;
            case DEVOLUCAO:
                colunas.add(DATA_DEVOLUCAO);
                break;
            case REVOGACAO:
                colunas.add(DATA_REVOGACAO);
                break;
            case REVOGACAO_SUSPENSO:
                colunas.add(DATA_REV_SUSP);
                break;
            case SEGUNDA_REVOGACAO:
                colunas.add(DATA_REVOGACAO);
                break;
            case SUSPENSAO:
                colunas.add(DATA_SUSPENSO);
                break;
            case SUSTACAO:
                colunas.add(DATA_SUSTACAO);
                break;
            case SUSTACAO_DEFINITIVA:
                colunas.add(DATA_SUSPENSAO_DEFINITIVA);
                break;
            default:
                // Para tipos não mapeados, não adiciona colunas extras
                break;
        }
        
        return colunas;
    }
    
    /**
     * Retorna colunas principais (mais usadas)
     */
    public static List<ColunaData> getPrincipais() {
        return Arrays.asList(
            DATA_APONTAMENTO,
            DATA_INTIMACAO,
            DATA_RETIRADA,
            DATA_PAGAMENTO,
            DATA_BAIXA,
            DATA_DISTRIBUICAO,
            DATA_EDITAL,
            DATA_JORNAL
        );
    }
    
    /**
     * Retorna colunas de datas processuais
     */
    public static List<ColunaData> getProcessuais() {
        return Arrays.stream(values())
            .filter(c -> c.descricao.contains("Distribuição") || 
                        c.descricao.contains("Edital") || 
                        c.descricao.contains("Jornal") ||
                        c.descricao.contains("Processo"))
            .collect(Collectors.toList());
    }
    
    /**
     * Retorna colunas de datas financeiras
     */
    public static List<ColunaData> getFinanceiras() {
        return Arrays.stream(values())
            .filter(c -> c.descricao.contains("Pagamento") || 
                        c.descricao.contains("Depósito") || 
                        c.descricao.contains("Vencimento"))
            .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return descricao + " (" + nomeColuna + ")";
    }
}