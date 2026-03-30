package com.selador.web.dto;

import com.selador.model.Selo;
import com.selador.enums.TipoSelo;
import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.lang.reflect.Method;

public class SeloDTO {
    private Long id;
    private String tiposelo;
    private String selo;
    private BigDecimal valorselo;
    private String tipo;
    private String chave1;
    private String chave2;
    private String dataselo;
    private Integer codtipoato;
    private String nome;
    private String cpfcnpj;
    private String protocolo;
    private String datahora;
    private String chavedigital;
    private String idap;
    private boolean disponivel;
    private String tipoDescricao;
    
    // Construtor usando reflection para encontrar os métodos corretos
    public SeloDTO(Selo selo) {
        if (selo != null) {
            // Usando reflection para acessar os métodos
            this.id = getValueAsLong(selo, "id");
            this.tiposelo = getValueAsString(selo, "tiposelo");
            this.selo = getValueAsString(selo, "selo");
            this.valorselo = getValueAsBigDecimal(selo, "valorselo");
            this.tipo = getValueAsString(selo, "tipo");
            this.chave1 = getValueAsString(selo, "chave1");
            this.chave2 = getValueAsString(selo, "chave2");
            this.dataselo = formatarData(getValueAsDate(selo, "dataselo"));
            this.codtipoato = getValueAsInteger(selo, "codtipoato");
            this.nome = getValueAsString(selo, "nome");
            this.cpfcnpj = getValueAsString(selo, "cpfcnpj");
            this.protocolo = getValueAsString(selo, "protocolo");
            this.datahora = formatarDataHora(getValueAsDate(selo, "datahora"));
            this.chavedigital = getValueAsString(selo, "chavedigital");
            this.idap = getValueAsString(selo, "idap");
            this.disponivel = getValueAsBoolean(selo, "disponivel");
            
            if (this.tiposelo != null) {
                this.tipoDescricao = obterDescricaoTipoSelo(this.tiposelo);
            }
        }
    }
    
    // ========== MÉTODOS GENÉRICOS DE ACESSO ==========
    
    private Object invokeGetter(Selo selo, String fieldName) {
        // Tenta diferentes padrões de nome de método
        String[] methodPatterns = {
            "get" + capitalize(fieldName),
            "get" + fieldName,
            "is" + capitalize(fieldName),
            fieldName
        };
        
        for (String methodName : methodPatterns) {
            try {
                Method method = selo.getClass().getMethod(methodName);
                return method.invoke(selo);
            } catch (NoSuchMethodException e) {
                continue;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    private Long getValueAsLong(Selo selo, String fieldName) {
        Object value = invokeGetter(selo, fieldName);
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private Integer getValueAsInteger(Selo selo, String fieldName) {
        Object value = invokeGetter(selo, fieldName);
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Long) return ((Long) value).intValue();
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private String getValueAsString(Selo selo, String fieldName) {
        Object value = invokeGetter(selo, fieldName);
        if (value == null) return null;
        
        // Se for enum TipoSelo, converte para string
        if (value instanceof TipoSelo) {
            TipoSelo tipoSelo = (TipoSelo) value;
            // Tenta getCodigo(), senão usa name()
            try {
                Method getCodigo = tipoSelo.getClass().getMethod("getCodigo");
                return (String) getCodigo.invoke(tipoSelo);
            } catch (Exception e) {
                return tipoSelo.name();
            }
        }
        
        return value.toString();
    }
    
    private BigDecimal getValueAsBigDecimal(Selo selo, String fieldName) {
        Object value = invokeGetter(selo, fieldName);
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value != null) {
            try {
                return new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private Date getValueAsDate(Selo selo, String fieldName) {
        Object value = invokeGetter(selo, fieldName);
        if (value instanceof Date) return (Date) value;
        if (value instanceof String) {
            try {
                String str = (String) value;
                // Tenta formatos comuns de data
                String[] patterns = {
                    "yyyy-MM-dd HH:mm:ss",
                    "dd/MM/yyyy HH:mm:ss",
                    "yyyy-MM-dd",
                    "dd/MM/yyyy"
                };
                
                for (String pattern : patterns) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        return sdf.parse(str);
                    } catch (Exception e) {
                        continue;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    private boolean getValueAsBoolean(Selo selo, String fieldName) {
        Object value = invokeGetter(selo, fieldName);
        if (value instanceof Boolean) return (Boolean) value;
        if (value != null) {
            String str = value.toString().toLowerCase();
            return "true".equals(str) || "1".equals(str) || "sim".equals(str) || "s".equals(str);
        }
        return false;
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    private String formatarData(Date data) {
        if (data == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy").format(data);
    }
    
    private String formatarDataHora(Date data) {
        if (data == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(data);
    }
    
    private String obterDescricaoTipoSelo(String codigo) {
        if (codigo == null || codigo.isEmpty()) return "";
        
        // Tenta métodos diferentes para obter a descrição
        for (TipoSelo tipo : TipoSelo.values()) {
            try {
                // Tenta getCodigo()
                Method getCodigo = tipo.getClass().getMethod("getCodigo");
                String codigoEnum = (String) getCodigo.invoke(tipo);
                if (codigo.equals(codigoEnum)) {
                    Method getDescricao = tipo.getClass().getMethod("getDescricao");
                    return (String) getDescricao.invoke(tipo);
                }
            } catch (Exception e1) {
                // Tenta name() ou toString()
                if (codigo.equals(tipo.name()) || codigo.equals(tipo.toString())) {
                    // Tenta getDescricao() no próprio enum
                    try {
                        Method getDescricao = tipo.getClass().getMethod("getDescricao");
                        return (String) getDescricao.invoke(tipo);
                    } catch (Exception e2) {
                        return tipo.toString();
                    }
                }
            }
        }
        return codigo;
    }
    
    // ========== GETTERS E SETTERS ==========
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTiposelo() { return tiposelo; }
    public void setTiposelo(String tiposelo) { this.tiposelo = tiposelo; }
    
    public String getSelo() { return selo; }
    public void setSelo(String selo) { this.selo = selo; }
    
    public BigDecimal getValorselo() { return valorselo; }
    public void setValorselo(BigDecimal valorselo) { this.valorselo = valorselo; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getChave1() { return chave1; }
    public void setChave1(String chave1) { this.chave1 = chave1; }
    
    public String getChave2() { return chave2; }
    public void setChave2(String chave2) { this.chave2 = chave2; }
    
    public String getDataselo() { return dataselo; }
    public void setDataselo(String dataselo) { this.dataselo = dataselo; }
    
    public Integer getCodtipoato() { return codtipoato; }
    public void setCodtipoato(Integer codtipoato) { this.codtipoato = codtipoato; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCpfcnpj() { return cpfcnpj; }
    public void setCpfcnpj(String cpfcnpj) { this.cpfcnpj = cpfcnpj; }
    
    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }
    
    public String getDatahora() { return datahora; }
    public void setDatahora(String datahora) { this.datahora = datahora; }
    
    public String getChavedigital() { return chavedigital; }
    public void setChavedigital(String chavedigital) { this.chavedigital = chavedigital; }
    
    public String getIdap() { return idap; }
    public void setIdap(String idap) { this.idap = idap; }
    
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    
    public String getTipoDescricao() { return tipoDescricao; }
    public void setTipoDescricao(String tipoDescricao) { this.tipoDescricao = tipoDescricao; }
}