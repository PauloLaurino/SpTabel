package com.selador.service;

import com.selador.enums.FormatoData;
import com.selador.model.FiltroBusca;
import com.selador.model.Apontamento;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Service para validações de dados e regras de negócio
 * Versão compatível com Java 8
 */
public class ValidationService {
    
    private static ValidationService instance;
    
    // Definir constantes para códigos de status
    private static final String STATUS_BAIXADO = "BAIXADO";
    private static final String STATUS_CANCELADO = "CANCELADO";
    private static final String STATUS_REVOGADO = "REVOGADO";
    private static final String STATUS_DEVOLVIDO = "DEVOLVIDO";
    
    // Definir constantes para tipos de operação
    private static final String TIPO_APONTAMENTO = "APONTAMENTO";
    private static final String TIPO_RETIRADA = "RETIRADA";
    private static final String TIPO_PAGAMENTO = "PAGAMENTO";
    private static final String TIPO_BAIXA = "BAIXA";
    private static final String TIPO_INTIMACAO = "INTIMACAO";
    private static final String TIPO_DIGITALIZACAO = "DIGITALIZACAO";
    private static final String TIPO_DEVOLUCAO = "DEVOLUCAO";
    private static final String TIPO_SUSTACAO = "SUSTACAO";
    
    private ValidationService() {}
    
    public static synchronized ValidationService getInstance() {
        if (instance == null) {
            instance = new ValidationService();
        }
        return instance;
    }
    
    /**
     * Valida se um apontamento está apto para selagem
     */
    public boolean isAptoParaSelagem(Apontamento apontamento) {
        if (apontamento == null) {
            return false;
        }
        
        // Obter dados do apontamento usando métodos que DEVERIAM existir
        // Você precisa ajustar para os métodos reais da sua classe Apontamento
        
        // Exemplo genérico - ajuste conforme sua implementação
        String status = extrairValorComoString(apontamento, "situacao");
        String selo = extrairValorComoString(apontamento, "selo");
        BigDecimal valor = extrairValorComoBigDecimal(apontamento, "valor");
        
        return validarDadosParaSelagem(status, selo, valor);
    }
    
    /**
     * Validação baseada em dados específicos
     */
    public boolean validarDadosParaSelagem(String status, String selo, BigDecimal valor) {
        // Status que NÃO permitem selagem
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String statusUpper = status.toUpperCase();
        if (statusUpper.contains(STATUS_BAIXADO) ||
            statusUpper.contains(STATUS_CANCELADO) ||
            statusUpper.contains(STATUS_REVOGADO) ||
            statusUpper.contains(STATUS_DEVOLVIDO)) {
            return false;
        }
        
        // Verificar se já possui selo
        if (selo != null && !selo.trim().isEmpty()) {
            return false;
        }
        
        // Verificar valor (deve ser maior que zero)
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Métodos auxiliares para extrair valores usando reflection
     */
    private String extrairValorComoString(Object obj, String campo) {
        if (obj == null) return null;
        
        String[] possiveisMetodos = {
            "get" + capitalize(campo),
            "get" + capitalize(campo) + "001",
            "get" + capitalize(campo).toUpperCase(),
            campo
        };
        
        for (String metodo : possiveisMetodos) {
            try {
                Object resultado = obj.getClass()
                    .getMethod(metodo)
                    .invoke(obj);
                if (resultado != null) {
                    return resultado.toString();
                }
            } catch (Exception e) {
                // Continue tentando outros métodos
            }
        }
        
        return null;
    }
    
    private BigDecimal extrairValorComoBigDecimal(Object obj, String campo) {
        if (obj == null) return null;
        
        String[] possiveisMetodos = {
            "get" + capitalize(campo),
            "get" + capitalize(campo) + "001",
            "getValor"
        };
        
        for (String metodo : possiveisMetodos) {
            try {
                Object resultado = obj.getClass()
                    .getMethod(metodo)
                    .invoke(obj);
                if (resultado instanceof BigDecimal) {
                    return (BigDecimal) resultado;
                } else if (resultado != null) {
                    // Tentar converter para BigDecimal
                    try {
                        return new BigDecimal(resultado.toString());
                    } catch (NumberFormatException e) {
                        // Ignorar e continuar
                    }
                }
            } catch (Exception e) {
                // Continue tentando outros métodos
            }
        }
        
        return null;
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * Método auxiliar para obter string do formato de data
     */
    private String getFormatoString(FormatoData formato) {
        if (formato == null) {
            return "dd/MM/yyyy";
        }
        
        // Converter o enum para string
        String nomeFormato = formato.name();
        
        // Mapear para strings de formato
        switch (nomeFormato) {
            case "DDMMYYYY":
                return "dd/MM/yyyy";
            case "DDMMYYYY_HHMMSS":
                return "dd/MM/yyyy HH:mm:ss";
            case "YYYYMMDD":
                return "yyyyMMdd";
            case "YYYYMMDD_HHMMSS":
                return "yyyyMMdd HH:mm:ss";
            case "YYYY_MM_DD":
                return "yyyy-MM-dd";
            case "YYYY_MM_DD_HH_MM_SS":
                return "yyyy-MM-dd HH:mm:ss";
            default:
                return "dd/MM/yyyy";
        }
    }
    
    /**
     * Método auxiliar para verificar se data é válida
     */
    private boolean isDataValidaCustom(String dataStr, FormatoData formato) {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getFormatoString(formato));
            sdf.setLenient(false); // Não permitir datas inválidas como 31/02/2023
            Date data = sdf.parse(dataStr.trim());
            return data != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Método auxiliar para converter string para Date
     */
    private Date stringParaDateCustom(String dataStr, FormatoData formato) {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getFormatoString(formato));
            sdf.setLenient(false);
            return sdf.parse(dataStr.trim());
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Método auxiliar para verificar se data está no formato correto
     */
    private boolean isDataNoFormatoCustom(Date data, FormatoData formato) {
        if (data == null) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getFormatoString(formato));
            String dataFormatada = sdf.format(data);
            // Tenta parsear novamente para verificar se é válida
            Date dataVerificada = sdf.parse(dataFormatada);
            return dataVerificada != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Valida filtro de busca
     */
    public List<String> validarFiltroBusca(FiltroBusca filtro) {
        List<String> erros = new ArrayList<>();
        
        if (filtro == null) {
            erros.add("Filtro não pode ser nulo");
            return erros;
        }
        
        if (filtro.getTipoOperacao() == null) {
            erros.add("Tipo de operação é obrigatório");
        }
        
        if (filtro.getDataInicialStr() == null || filtro.getDataInicialStr().trim().isEmpty()) {
            erros.add("Data inicial é obrigatória");
        } else {
            try {
                if (!isDataValidaCustom(filtro.getDataInicialStr(), FormatoData.DDMMYYYY)) {
                    erros.add("Data inicial inválida. Use formato DD/MM/YYYY");
                }
            } catch (Exception e) {
                erros.add("Data inicial inválida: " + e.getMessage());
            }
        }
        
        if (filtro.getDataFinalStr() != null && !filtro.getDataFinalStr().trim().isEmpty()) {
            try {
                if (!isDataValidaCustom(filtro.getDataFinalStr(), FormatoData.DDMMYYYY)) {
                    erros.add("Data final inválida. Use formato DD/MM/YYYY");
                } else {
                    Date dataInicial = stringParaDateCustom(filtro.getDataInicialStr(), FormatoData.DDMMYYYY);
                    Date dataFinal = stringParaDateCustom(filtro.getDataFinalStr(), FormatoData.DDMMYYYY);
                    
                    if (dataInicial != null && dataFinal != null) {
                        // Método auxiliar para comparar datas
                        if (data1MaiorQueData2(dataInicial, dataFinal)) {
                            erros.add("Data inicial não pode ser maior que data final");
                        }
                    }
                }
            } catch (Exception e) {
                erros.add("Data final inválida: " + e.getMessage());
            }
        }
        
        return erros;
    }
    
    /**
     * Método auxiliar para comparar datas
     */
    private boolean data1MaiorQueData2(Date data1, Date data2) {
        if (data1 == null || data2 == null) {
            return false;
        }
        
        // Comparar sem considerar horas/minutos/segundos
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strData1 = sdf.format(data1);
        String strData2 = sdf.format(data2);
        
        return strData1.compareTo(strData2) > 0;
    }
    
    /**
     * Valida selo para uso
     */
    public boolean validarSeloParaUso(String numeroSelo, String tipoSelo) {
        if (numeroSelo == null || numeroSelo.trim().isEmpty()) {
            return false;
        }
        
        if (tipoSelo == null || tipoSelo.trim().isEmpty()) {
            return false;
        }
        
        // Selo deve ter exatamente 50 caracteres
        if (numeroSelo.trim().length() != 50) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida formato de data conforme tipo de operação
     */
    public boolean validarDataPorOperacao(Date data, String tipoOperacao) {
        if (data == null || tipoOperacao == null) {
            return false;
        }
        
        String tipoUpper = tipoOperacao.toUpperCase();
        
        // Lógica específica por tipo de operação usando strings
        if (tipoUpper.equals(TIPO_APONTAMENTO) ||
            tipoUpper.equals(TIPO_RETIRADA) ||
            tipoUpper.equals(TIPO_PAGAMENTO)) {
            // Datas no formato YYYYMMDD
            return isDataNoFormatoCustom(data, FormatoData.YYYYMMDD);
        } else if (tipoUpper.equals(TIPO_BAIXA) ||
                   tipoUpper.equals(TIPO_INTIMACAO) ||
                   tipoUpper.equals(TIPO_DIGITALIZACAO) ||
                   tipoUpper.equals(TIPO_DEVOLUCAO) ||
                   tipoUpper.equals(TIPO_SUSTACAO)) {
            // Datas no formato DDMMYYYY
            return isDataNoFormatoCustom(data, FormatoData.DDMMYYYY);
        }
        
        return true;
    }
    
    /**
     * Valida CPF/CNPJ
     */
    public boolean validarCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.trim().isEmpty()) {
            return false;
        }
        
        String numeros = cpfCnpj.replaceAll("[^0-9]", "");
        
        if (numeros.length() == 11) {
            return validarCPF(numeros);
        } else if (numeros.length() == 14) {
            return validarCNPJ(numeros);
        }
        
        return false;
    }
    
    private boolean validarCPF(String cpf) {
        if (cpf.length() != 11) return false;
        
        // Verificar se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;
        
        try {
            int[] digitos = new int[11];
            for (int i = 0; i < 11; i++) {
                digitos[i] = Integer.parseInt(cpf.substring(i, i + 1));
            }
            
            // Primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += digitos[i] * (10 - i);
            }
            int resto = soma % 11;
            int dv1 = (resto < 2) ? 0 : 11 - resto;
            
            if (dv1 != digitos[9]) return false;
            
            // Segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += digitos[i] * (11 - i);
            }
            resto = soma % 11;
            int dv2 = (resto < 2) ? 0 : 11 - resto;
            
            return dv2 == digitos[10];
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean validarCNPJ(String cnpj) {
        if (cnpj.length() != 14) return false;
        
        // Verificar se todos os dígitos são iguais
        if (cnpj.matches("(\\d)\\1{13}")) return false;
        
        try {
            int[] digitos = new int[14];
            for (int i = 0; i < 14; i++) {
                digitos[i] = Integer.parseInt(cnpj.substring(i, i + 1));
            }
            
            // Primeiro dígito verificador
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += digitos[i] * peso1[i];
            }
            int resto = soma % 11;
            int dv1 = (resto < 2) ? 0 : 11 - resto;
            
            if (dv1 != digitos[12]) return false;
            
            // Segundo dígito verificador
            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += digitos[i] * peso2[i];
            }
            resto = soma % 11;
            int dv2 = (resto < 2) ? 0 : 11 - resto;
            
            return dv2 == digitos[13];
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida se uma data está dentro de um período
     */
    public boolean validarDataNoPeriodo(Date data, Date dataInicio, Date dataFim) {
        if (data == null || dataInicio == null) {
            return false;
        }
        
        // Se dataFim for null, considera apenas a data de início
        if (dataFim == null) {
            return !data.before(dataInicio);
        }
        
        return !data.before(dataInicio) && !data.after(dataFim);
    }
    
    /**
     * Valida se um número está dentro de um intervalo
     */
    public boolean validarIntervaloNumerico(int valor, int min, int max) {
        return valor >= min && valor <= max;
    }
    
    /**
     * Valida se uma string não é nula e tem tamanho mínimo
     */
    public boolean validarStringObrigatoria(String valor, int tamanhoMinimo) {
        return valor != null && 
               !valor.trim().isEmpty() && 
               valor.trim().length() >= tamanhoMinimo;
    }
}