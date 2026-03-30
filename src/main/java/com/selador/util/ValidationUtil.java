package com.selador.util;

import com.selador.enums.TipoOperacao;
import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utilitário para validação de inputs e dados
 */
public class ValidationUtil {
    
    // Padrões regex
    private static final Pattern PATTERN_EMAIL = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PATTERN_NUMERO = 
        Pattern.compile("^\\d+$");
    private static final Pattern PATTERN_DECIMAL = 
        Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    private static final Pattern PATTERN_ALFANUMERICO = 
        Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern PATTERN_SELO = 
        Pattern.compile("^[A-Za-z0-9]{50}$"); // Selos tem 50 caracteres
    
    private ValidationUtil() {
        // Classe utilitária - não instanciável
    }
    
    /**
     * Valida se string não é nula nem vazia
     */
    public static boolean isNotEmpty(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
    
    /**
     * Valida se string tem tamanho mínimo
     */
    public static boolean hasMinLength(String valor, int minLength) {
        return valor != null && valor.trim().length() >= minLength;
    }
    
    /**
     * Valida se string tem tamanho máximo
     */
    public static boolean hasMaxLength(String valor, int maxLength) {
        return valor != null && valor.trim().length() <= maxLength;
    }
    
    /**
     * Valida se string tem tamanho exato
     */
    public static boolean hasExactLength(String valor, int length) {
        return valor != null && valor.trim().length() == length;
    }
    
    /**
     * Valida se é número inteiro
     */
    public static boolean isInteger(String valor) {
        if (!isNotEmpty(valor)) return false;
        try {
            Integer.parseInt(valor.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida se é número decimal
     */
    public static boolean isDecimal(String valor) {
        if (!isNotEmpty(valor)) return false;
        return PATTERN_DECIMAL.matcher(valor.trim()).matches();
    }
    
    /**
     * Valida se é número positivo
     */
    public static boolean isPositiveNumber(String valor) {
        if (!isDecimal(valor)) return false;
        try {
            BigDecimal num = new BigDecimal(valor.trim());
            return num.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida formato de data usando string de formato
     */
    public static boolean isDataValida(String dataStr, String formato) {
        return DateUtil.isDataValida(dataStr, formato);
    }
    
    /**
     * Valida formato de data DD/MM/YYYY
     */
    public static boolean isDataDDMMYYYYValida(String dataStr) {
        return DateUtil.isDataDDMMYYYYValida(dataStr);
    }
    
    /**
     * Valida se data está no período permitido
     */
    public static boolean isDataNoPeriodo(String dataStr, String formato, 
                                          int diasMaxPassado, int diasMaxFuturo) {
        try {
            Date data = DateUtil.stringParaDate(dataStr, formato);
            Date hoje = new Date();
            
            Date limitePassado = DateUtil.adicionarDias(hoje, -diasMaxPassado);
            Date limiteFuturo = DateUtil.adicionarDias(hoje, diasMaxFuturo);
            
            return !data.before(limitePassado) && !data.after(limiteFuturo);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Valida CPF
     */
    public static boolean isCpfValido(String cpf) {
        if (!isNotEmpty(cpf)) return false;
        
        String cpfNumeros = cpf.replaceAll("[^0-9]", "");
        if (cpfNumeros.length() != 11) return false;
        
        // Verificar se todos os dígitos são iguais
        if (cpfNumeros.matches("(\\d)\\1{10}")) return false;
        
        try {
            int[] digitos = new int[11];
            for (int i = 0; i < 11; i++) {
                digitos[i] = Integer.parseInt(cpfNumeros.substring(i, i + 1));
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
    
    /**
     * Valida CNPJ
     */
    public static boolean isCnpjValido(String cnpj) {
        if (!isNotEmpty(cnpj)) return false;
        
        String cnpjNumeros = cnpj.replaceAll("[^0-9]", "");
        if (cnpjNumeros.length() != 14) return false;
        
        // Verificar se todos os dígitos são iguais
        if (cnpjNumeros.matches("(\\d)\\1{13}")) return false;
        
        try {
            int[] digitos = new int[14];
            for (int i = 0; i < 14; i++) {
                digitos[i] = Integer.parseInt(cnpjNumeros.substring(i, i + 1));
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
     * Valida CPF ou CNPJ
     */
    public static boolean isCpfCnpjValido(String cpfCnpj) {
        if (!isNotEmpty(cpfCnpj)) return false;
        
        String numeros = cpfCnpj.replaceAll("[^0-9]", "");
        if (numeros.length() == 11) {
            return isCpfValido(cpfCnpj);
        } else if (numeros.length() == 14) {
            return isCnpjValido(cpfCnpj);
        }
        
        return false;
    }
    
    /**
     * Valida formato de email
     */
    public static boolean isEmailValido(String email) {
        if (!isNotEmpty(email)) return false;
        return PATTERN_EMAIL.matcher(email.trim()).matches();
    }
    
    /**
     * Valida formato de selo
     */
    public static boolean isSeloValido(String selo) {
        if (!isNotEmpty(selo)) return false;
        return PATTERN_SELO.matcher(selo.trim()).matches();
    }
    
    /**
     * Valida número de apontamento (numapo1)
     */
    public static boolean isNumApo1Valido(String numapo1) {
        return hasExactLength(numapo1, 6) && PATTERN_NUMERO.matcher(numapo1).matches();
    }
    
    /**
     * Valida número de apontamento (numapo2)
     */
    public static boolean isNumApo2Valido(String numapo2) {
        return hasExactLength(numapo2, 10) && PATTERN_NUMERO.matcher(numapo2).matches();
    }
    
    /**
     * Valida par de apontamento
     */
    public static boolean isApontamentoValido(String numapo1, String numapo2) {
        return isNumApo1Valido(numapo1) && isNumApo2Valido(numapo2);
    }
    
    /**
     * Valida tipo de operação
     */
    public static boolean isTipoOperacaoValido(String tipoOperacaoStr) {
        if (!isNotEmpty(tipoOperacaoStr)) return false;
        
        try {
            TipoOperacao tipo = TipoOperacao.valueOf(tipoOperacaoStr);
            return tipo != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Valida valor monetário
     */
    public static boolean isValorMonetarioValido(String valorStr) {
        if (!isNotEmpty(valorStr)) return false;
        
        try {
            BigDecimal valor = new BigDecimal(valorStr.trim().replace(",", "."));
            return valor.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida se valor está entre mínimo e máximo
     */
    public static boolean isValorEntre(String valorStr, BigDecimal min, BigDecimal max) {
        if (!isDecimal(valorStr)) return false;
        
        try {
            BigDecimal valor = new BigDecimal(valorStr.trim());
            return valor.compareTo(min) >= 0 && valor.compareTo(max) <= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida lista de valores (não vazia, sem nulos)
     */
    public static boolean isListaValida(java.util.List<?> lista) {
        return lista != null && !lista.isEmpty() && 
               lista.stream().allMatch(item -> item != null);
    }
    
    /**
     * Valida se objeto não é nulo
     */
    public static boolean isNotNull(Object objeto) {
        return objeto != null;
    }
    
    /**
     * Valida se array não é nulo nem vazio
     */
    public static boolean isArrayValido(Object[] array) {
        return array != null && array.length > 0;
    }
    
    /**
     * Valida se mapa não é nulo nem vazio
     */
    public static boolean isMapValido(java.util.Map<?, ?> mapa) {
        return mapa != null && !mapa.isEmpty();
    }
    
    /**
     * Sanitiza string (remove caracteres perigosos)
     */
    public static String sanitizarString(String input) {
        if (!isNotEmpty(input)) return "";
        
        // Remover caracteres potencialmente perigosos para SQL/HTML
        return input.trim()
            .replace("'", "''")
            .replace("\"", "\\\"")
            .replace(";", "")
            .replace("--", "")
            .replace("/*", "")
            .replace("*/", "")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }
    
    /**
     * Valida intervalo de datas
     */
    public static boolean isIntervaloDatasValido(String dataInicialStr, String dataFinalStr, 
                                                 String formato, int maxDias) {
        try {
            Date dataInicial = DateUtil.stringParaDate(dataInicialStr, formato);
            Date dataFinal = DateUtil.stringParaDate(dataFinalStr, formato);
            
            if (dataInicial.after(dataFinal)) {
                return false;
            }
            
            long dias = DateUtil.diferencaEmDias(dataInicial, dataFinal);
            return dias <= maxDias;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Valida intervalo de datas DD/MM/YYYY
     */
    public static boolean isIntervaloDatasDDMMYYYYValido(String dataInicialStr, String dataFinalStr, int maxDias) {
        return isIntervaloDatasValido(dataInicialStr, dataFinalStr, DateUtil.FORMATO_DDMMYYYY, maxDias);
    }
    
    /**
     * Valida código de status
     */
    public static boolean isStatusValido(String status) {
        if (!isNotEmpty(status)) return false;
        
        // Status deve ser 2 dígitos numéricos (01-99)
        if (!hasExactLength(status, 2)) return false;
        if (!PATTERN_NUMERO.matcher(status).matches()) return false;
        
        int statusNum = Integer.parseInt(status);
        return statusNum >= 1 && statusNum <= 99;
    }
    
    /**
     * Gera mensagem de erro baseada no tipo de validação
     */
    public static String getMensagemErro(String campo, String tipoValidacao) {
        switch (tipoValidacao.toUpperCase()) {
            case "REQUIRED":
                return "Campo '" + campo + "' é obrigatório";
            case "MIN_LENGTH":
                return "Campo '" + campo + "' deve ter tamanho mínimo";
            case "MAX_LENGTH":
                return "Campo '" + campo + "' excede tamanho máximo";
            case "EXACT_LENGTH":
                return "Campo '" + campo + "' deve ter tamanho exato";
            case "INTEGER":
                return "Campo '" + campo + "' deve ser um número inteiro";
            case "DECIMAL":
                return "Campo '" + campo + "' deve ser um número decimal";
            case "POSITIVE":
                return "Campo '" + campo + "' deve ser um número positivo";
            case "DATE":
                return "Campo '" + campo + "' deve ser uma data válida";
            case "CPF":
                return "Campo '" + campo + "' deve ser um CPF válido";
            case "CNPJ":
                return "Campo '" + campo + "' deve ser um CNPJ válido";
            case "CPF_CNPJ":
                return "Campo '" + campo + "' deve ser um CPF ou CNPJ válido";
            case "EMAIL":
                return "Campo '" + campo + "' deve ser um email válido";
            case "SELO":
                return "Campo '" + campo + "' deve ser um selo válido (50 caracteres alfanuméricos)";
            case "APONTAMENTO":
                return "Campo '" + campo + "' deve ser um número de apontamento válido";
            case "TIPO_OPERACAO":
                return "Campo '" + campo + "' deve ser um tipo de operação válido";
            default:
                return "Campo '" + campo + "' inválido";
        }
    }
    
    /**
     * Métodos de validação adicionais
     */
    
    /**
     * Valida se string contém apenas letras e espaços
     */
    public static boolean isApenasLetras(String valor) {
        if (!isNotEmpty(valor)) return false;
        return valor.matches("^[A-Za-zÀ-ÿ\\s]+$");
    }
    
    /**
     * Valida se string é alfanumérica
     */
    public static boolean isAlfanumerico(String valor) {
        if (!isNotEmpty(valor)) return false;
        return PATTERN_ALFANUMERICO.matcher(valor).matches();
    }
    
    /**
     * Valida CEP (8 dígitos)
     */
    public static boolean isCepValido(String cep) {
        if (!isNotEmpty(cep)) return false;
        String cepNumeros = cep.replaceAll("[^0-9]", "");
        return cepNumeros.length() == 8;
    }
    
    /**
     * Valida telefone (10 ou 11 dígitos)
     */
    public static boolean isTelefoneValido(String telefone) {
        if (!isNotEmpty(telefone)) return false;
        String numeros = telefone.replaceAll("[^0-9]", "");
        return numeros.length() == 10 || numeros.length() == 11;
    }
    
    /**
     * Valida URL
     */
    public static boolean isUrlValida(String url) {
        if (!isNotEmpty(url)) return false;
        try {
            new java.net.URL(url);
            return true;
        } catch (java.net.MalformedURLException e) {
            return false;
        }
    }
    
    /**
     * Valida se valor está em lista de opções
     */
    public static boolean isValorEmLista(String valor, String... opcoes) {
        if (!isNotEmpty(valor)) return false;
        for (String opcao : opcoes) {
            if (valor.equals(opcao)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Valida se string é boolean válido
     */
    public static boolean isBooleanValido(String valor) {
        if (!isNotEmpty(valor)) return false;
        String valLower = valor.toLowerCase();
        return valLower.equals("true") || valLower.equals("false") || 
               valLower.equals("1") || valLower.equals("0") ||
               valLower.equals("sim") || valLower.equals("não") ||
               valLower.equals("yes") || valLower.equals("no");
    }
    
    /**
     * Valida se é UUID válido
     */
    public static boolean isUuidValido(String uuid) {
        if (!isNotEmpty(uuid)) return false;
        return uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }
    
    /**
     * Limita string ao tamanho máximo, truncando se necessário
     */
    public static String limitarTamanho(String valor, int maxLength) {
        if (valor == null) return null;
        if (valor.length() <= maxLength) return valor;
        return valor.substring(0, maxLength);
    }
    
    /**
     * Validação de senha (mínimo 8 caracteres, com letra e número)
     */
    public static boolean isSenhaValida(String senha) {
        if (!isNotEmpty(senha)) return false;
        if (senha.length() < 8) return false;
        // Pelo menos uma letra e um número
        return senha.matches(".*[A-Za-z].*") && senha.matches(".*\\d.*");
    }
}