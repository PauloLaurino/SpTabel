package com.selador.util;

import com.selador.enums.TipoOperacao;
import com.selador.enums.FormatoData;
import java.util.*;

/**
 * Classe utilitária para mapeamento de tipos de operação.
 * Fornece métodos para validar e converter labels de tipos de operação.
 */
public class TipoOperacaoMapeamento {
    
    // Cache de labels válidos
    private static final Set<String> LABELS_VALIDOS = new HashSet<>();
    private static final Map<String, String> LABEL_PARA_CODIGO = new HashMap<>();
    private static final Map<String, String> CODIGO_PARA_LABEL = new HashMap<>();
    private static final Map<String, MapeamentoTipo> MAPEAMENTOS = new HashMap<>();
    
    static {
        // Inicializar com os labels do enum TipoOperacao
        for (TipoOperacao tipo : TipoOperacao.values()) {
            String label = tipo.getDescricao();
            String codigo = tipo.getCodigoTipoAto();
            String colunaBanco = tipo.getColunaBanco();
            FormatoData formatoData = tipo.getFormatoData();
            
            LABELS_VALIDOS.add(label.toUpperCase());
            LABELS_VALIDOS.add(label);  // Permitir case-sensitive também
            
            LABEL_PARA_CODIGO.put(label.toUpperCase(), codigo);
            LABEL_PARA_CODIGO.put(label, codigo);
            
            CODIGO_PARA_LABEL.put(codigo, label);
            
            // Criar MapeamentoTipo
            MapeamentoTipo mapeamento = new MapeamentoTipo(
                label,
                codigo,
                colunaBanco,
                formatoData.name(),
                formatoData == FormatoData.DDMMYYYY_SEM_ZERO
            );
            MAPEAMENTOS.put(label.toUpperCase(), mapeamento);
            MAPEAMENTOS.put(label, mapeamento);
            MAPEAMENTOS.put(codigo, mapeamento);
        }
    }
    
    /**
     * Classe interna que representa um mapeamento de tipo
     */
    public static class MapeamentoTipo {
        private final String labelFrontend;
        private final String codigo;
        private final String colunaData;
        private final String formatoData;
        private final boolean usaFormatoDDMMYYYYSemZero;
        
        public MapeamentoTipo(String labelFrontend, String codigo, String colunaData, 
                           String formatoData, boolean usaFormatoDDMMYYYYSemZero) {
            this.labelFrontend = labelFrontend;
            this.codigo = codigo;
            this.colunaData = colunaData;
            this.formatoData = formatoData;
            this.usaFormatoDDMMYYYYSemZero = usaFormatoDDMMYYYYSemZero;
        }
        
        public String getLabelFrontend() { return labelFrontend; }
        public String getCodigo() { return codigo; }
        public String getColunaData() { return colunaData; }
        public String getFormatoData() { return formatoData; }
        public boolean isUsaFormatoDDMMYYYYSemZero() { return usaFormatoDDMMYYYYSemZero; }
    }
    
    /**
     * Verifica se um label é válido
     */
    public static boolean isLabelValido(String label) {
        if (label == null || label.trim().isEmpty()) {
            return false;
        }
        return LABELS_VALIDOS.contains(label.trim()) || 
               LABELS_VALIDOS.contains(label.trim().toUpperCase());
    }
    
    /**
     * Obtém todos os labels válidos como array
     */
    public static String[] getTodosLabels() {
        return LABELS_VALIDOS.toArray(new String[0]);
    }
    
    /**
     * Obtém o código do tipo de ato a partir do label
     */
    public static String getCodigoPorLabel(String label) {
        if (label == null) {
            return null;
        }
        return LABEL_PARA_CODIGO.get(label.trim().toUpperCase());
    }
    
    /**
     * Obtém o label a partir do código do tipo de ato
     */
    public static String getLabelPorCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        return CODIGO_PARA_LABEL.get(codigo.trim());
    }
    
    /**
     * Obtém o mapeamento a partir do label
     */
    public static MapeamentoTipo getPorLabel(String label) {
        if (label == null) {
            return null;
        }
        return MAPEAMENTOS.get(label.trim().toUpperCase());
    }
    
    /**
     * Obtém o mapeamento a partir do código com descrição
     */
    public static MapeamentoTipo getPorCodigoComDescricao(String codigo, String descricao) {
        if (codigo == null && descricao == null) {
            return null;
        }
        
        // Primeiro tenta pelo código
        if (codigo != null) {
            MapeamentoTipo mapeamento = MAPEAMENTOS.get(codigo.trim());
            if (mapeamento != null) {
                return mapeamento;
            }
        }
        
        // Depois tenta pela descrição
        if (descricao != null) {
            MapeamentoTipo mapeamento = MAPEAMENTOS.get(descricao.trim().toUpperCase());
            if (mapeamento != null) {
                return mapeamento;
            }
            mapeamento = MAPEAMENTOS.get(descricao.trim());
            if (mapeamento != null) {
                return mapeamento;
            }
        }
        
        // Se não encontrou, retorna o padrão (APONTAMENTO)
        return MAPEAMENTOS.get("APONTAMENTO");
    }
    
    /**
     * Obtém todos os labels válidos
     */
    public static Set<String> getLabelsValidos() {
        return Collections.unmodifiableSet(LABELS_VALIDOS);
    }
    
    /**
     * Obtém todos os labels como lista
     */
    public static List<String> getLabelsValidosLista() {
        List<String> labels = new ArrayList<>(LABELS_VALIDOS);
        Collections.sort(labels);
        return labels;
    }
    
    /**
     * Converte um label para o enum TipoOperacao
     */
    public static TipoOperacao getTipoOperacao(String label) {
        if (label == null) {
            return null;
        }
        
        try {
            return TipoOperacao.fromDescricao(label);
        } catch (Exception e) {
            return null;
        }
    }
}
