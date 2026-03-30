package com.selador.web.dto;

import com.selador.model.Selado;
import com.selador.enums.StatusSelagem;
import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public class SeladoDTO {
    private Long id;
    private Integer numTipoAto;
    private String dataTransmissao;
    private String nomeTransmissor;
    private String lote;
    private String protocolo;
    private String numeroAto;
    private BigDecimal valorAto;
    private String dataEnvio;
    private String dataRetorno;
    private String status;
    private String statusDescricao;
    private String mensagem;
    
    // Construtor baseado nos erros anteriores
    public SeladoDTO(Selado selado) {
        if (selado != null) {
            // ID - provavelmente Long
            this.id = selado.getId();
            
            // numtipato - baseado no erro anterior
            this.numTipoAto = (Integer) getFieldValue(selado, "numtipato");
            
            // Tentando diferentes variações de nomes de métodos
            this.dataTransmissao = getStringField(selado, "datatransmissao", "dataTransmissao");
            this.nomeTransmissor = getStringField(selado, "nometransmissor", "nomeTransmissor");
            this.lote = getStringField(selado, "lote");
            this.protocolo = getStringField(selado, "protocolo");
            this.numeroAto = getStringField(selado, "numeroato", "numeroAto");
            this.valorAto = getBigDecimalField(selado, "valorato", "valorAto");
            
            // Datas - podem ser Date ou String
            this.dataEnvio = formatarDataOuString(getFieldValue(selado, "dataenvio"));
            this.dataRetorno = formatarDataOuString(getFieldValue(selado, "dataretorno"));
            
            this.status = getStringField(selado, "status");
            this.mensagem = getStringField(selado, "mensagem");
            
            // Descrição do status
            if (this.status != null) {
                this.statusDescricao = obterDescricaoStatus(this.status);
            }
        }
    }
    
    // Métodos auxiliares para lidar com diferentes nomes de campos
    private Object getFieldValue(Selado selado, String fieldName) {
        try {
            // Tenta diferentes variações de getters
            String[] methodVariations = {
                "get" + fieldName,
                "get" + capitalize(fieldName),
                "is" + capitalize(fieldName)
            };
            
            for (String methodName : methodVariations) {
                try {
                    java.lang.reflect.Method method = selado.getClass().getMethod(methodName);
                    return method.invoke(selado);
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String getStringField(Selado selado, String... possibleFieldNames) {
        for (String fieldName : possibleFieldNames) {
            Object value = getFieldValue(selado, fieldName);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
    
    private BigDecimal getBigDecimalField(Selado selado, String... possibleFieldNames) {
        for (String fieldName : possibleFieldNames) {
            Object value = getFieldValue(selado, fieldName);
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else if (value != null) {
                try {
                    return new BigDecimal(value.toString());
                } catch (NumberFormatException e) {
                    // Ignora e tenta o próximo
                }
            }
        }
        return null;
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    private String formatarDataOuString(Object obj) {
        if (obj == null) {
            return "";
        }
        
        if (obj instanceof Date) {
            return formatarDataHora((Date) obj);
        } else if (obj instanceof String) {
            // Se já for string, tenta formatar se for uma data
            String str = (String) obj;
            try {
                // Tenta parsear como data
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(str);
                return formatarDataHora(date);
            } catch (Exception e) {
                // Se não conseguir parsear, retorna a string original
                return str;
            }
        }
        
        return obj.toString();
    }
    
    private String formatarDataHora(Date data) {
        if (data == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(data);
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getNumTipoAto() { return numTipoAto; }
    public void setNumTipoAto(Integer numTipoAto) { this.numTipoAto = numTipoAto; }
    
    public String getDataTransmissao() { return dataTransmissao; }
    public void setDataTransmissao(String dataTransmissao) { this.dataTransmissao = dataTransmissao; }
    
    public String getNomeTransmissor() { return nomeTransmissor; }
    public void setNomeTransmissor(String nomeTransmissor) { this.nomeTransmissor = nomeTransmissor; }
    
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    
    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }
    
    public String getNumeroAto() { return numeroAto; }
    public void setNumeroAto(String numeroAto) { this.numeroAto = numeroAto; }
    
    public BigDecimal getValorAto() { return valorAto; }
    public void setValorAto(BigDecimal valorAto) { this.valorAto = valorAto; }
    
    public String getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(String dataEnvio) { this.dataEnvio = dataEnvio; }
    
    public String getDataRetorno() { return dataRetorno; }
    public void setDataRetorno(String dataRetorno) { this.dataRetorno = dataRetorno; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStatusDescricao() { return statusDescricao; }
    public void setStatusDescricao(String statusDescricao) { this.statusDescricao = statusDescricao; }
    
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    
    // Método para debug - use para ver os métodos disponíveis
    public static void printAvailableMethods(Selado selado) {
        if (selado != null) {
            System.out.println("=== MÉTODOS DISPONÍVEIS EM SELADO ===");
            java.lang.reflect.Method[] methods = selado.getClass().getMethods();
            for (java.lang.reflect.Method method : methods) {
                if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                    System.out.println(method.getName() + " -> " + method.getReturnType().getSimpleName());
                }
            }
        }
    }
    
    private String obterDescricaoStatus(String codigo) {
        if (codigo == null) return "";
        
        // Verifica se StatusSelagem tem o método estático
        try {
            java.lang.reflect.Method method = StatusSelagem.class.getMethod("getDescricaoPorCodigo", String.class);
            return (String) method.invoke(null, codigo);
        } catch (Exception e) {
            // Se não tiver o método, tenta alternativas
            return obterDescricaoStatusAlternativa(codigo);
        }
    }

    private String obterDescricaoStatusAlternativa(String codigo) {
        // Tenta encontrar pelo código
        for (StatusSelagem status : StatusSelagem.values()) {
            // Tenta diferentes métodos para obter o código
            try {
                java.lang.reflect.Method getCodigoMethod = status.getClass().getMethod("getCodigo");
                String codigoEnum = (String) getCodigoMethod.invoke(status);
                if (codigo.equals(codigoEnum)) {
                    java.lang.reflect.Method getDescricaoMethod = status.getClass().getMethod("getDescricao");
                    return (String) getDescricaoMethod.invoke(status);
                }
            } catch (Exception e) {
                // Continua tentando
            }
        }
        return codigo; // Retorna o código se não encontrar
    }
}