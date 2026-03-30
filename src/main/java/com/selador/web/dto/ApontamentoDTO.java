package com.selador.web.dto;

import com.selador.model.Apontamento;
import java.math.BigDecimal;

/**
 * DTO para Apontamento (versão simplificada para API)
 */
public class ApontamentoDTO {
    private String numapo1;
    private String numapo2;
    private String controle;
    private String devedor;
    private String cgccpfsac;
    private BigDecimal valor;
    private BigDecimal protocolo;
    private String situacao;
    private String seloaponta;
    private String dataApontamento;
    private String dataRetirada;
    private String dataPagamento;
    private String dataBaixa;
    private String statusDescricao;
    private boolean aptoParaSelagem;
    
    /**
     * Construtor a partir do Model usando reflection para evitar problemas
     */
    public ApontamentoDTO(Apontamento apontamento) {
        if (apontamento == null) {
            return;
        }
        
        // Usar métodos genéricos para obter valores
        this.numapo1 = getFieldValue(apontamento, "numapo1");
        this.numapo2 = getFieldValue(apontamento, "numapo2");
        this.controle = getFieldValue(apontamento, "controle");
        this.devedor = getFieldValue(apontamento, "devedor");
        this.cgccpfsac = getFieldValue(apontamento, "cgccpfsac");
        
        // Valor e protocolo como BigDecimal
        this.valor = getBigDecimalField(apontamento, "valor");
        this.protocolo = getBigDecimalField(apontamento, "protocolo");
        
        this.situacao = getFieldValue(apontamento, "situacao");
        this.seloaponta = getFieldValue(apontamento, "seloaponta");
        
        // Datas
        this.dataApontamento = formatarData(getBigDecimalField(apontamento, "dataapo"));
        this.dataRetirada = formatarData(getBigDecimalField(apontamento, "dataret"));
        this.dataPagamento = formatarData(getBigDecimalField(apontamento, "datapag"));
        this.dataBaixa = formatarData(getBigDecimalField(apontamento, "databai"));
        
        // Status descrição (simplificado)
        this.statusDescricao = getStatusDescricao(this.situacao);
        
        // Verificar se está apto para selagem (simplificado)
        this.aptoParaSelagem = isAptoParaSelagem(apontamento);
    }
    
    /**
     * Método para obter valor de campo string usando reflection
     */
    private String getFieldValue(Apontamento apontamento, String fieldName) {
        if (apontamento == null) return null;
        
        try {
            // Tentar diferentes padrões de nome de método
            String[] methodNames = {
                "get" + capitalize(fieldName),
                "get" + capitalize(fieldName) + "001",
                "get" + fieldName.toLowerCase(),
                fieldName
            };
            
            for (String methodName : methodNames) {
                try {
                    Object result = apontamento.getClass()
                        .getMethod(methodName)
                        .invoke(apontamento);
                    
                    if (result != null) {
                        return result.toString();
                    }
                } catch (Exception e) {
                    // Continuar tentando outros métodos
                }
            }
        } catch (Exception e) {
            // Ignorar erros de reflection
        }
        
        return null;
    }
    
    /**
     * Método para obter valor de campo BigDecimal usando reflection
     */
    private BigDecimal getBigDecimalField(Apontamento apontamento, String fieldName) {
        if (apontamento == null) return null;
        
        try {
            String[] methodNames = {
                "get" + capitalize(fieldName),
                "get" + capitalize(fieldName) + "001",
                "getValor"
            };
            
            for (String methodName : methodNames) {
                try {
                    Object result = apontamento.getClass()
                        .getMethod(methodName)
                        .invoke(apontamento);
                    
                    if (result instanceof BigDecimal) {
                        return (BigDecimal) result;
                    } else if (result != null) {
                        // Tentar converter para BigDecimal
                        try {
                            return new BigDecimal(result.toString());
                        } catch (NumberFormatException e) {
                            // Ignorar e continuar
                        }
                    }
                } catch (Exception e) {
                    // Continuar tentando outros métodos
                }
            }
        } catch (Exception e) {
            // Ignorar erros de reflection
        }
        
        return null;
    }
    
    /**
     * Capitalizar primeira letra
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * Formatar data numérica para DD/MM/YYYY
     */
    private String formatarData(BigDecimal dataNum) {
        if (dataNum == null) return "";
        
        String dataStr = dataNum.toString();
        if (dataStr.length() == 8) {
            return dataStr.substring(6, 8) + "/" + 
                   dataStr.substring(4, 6) + "/" + 
                   dataStr.substring(0, 4);
        }
        return dataStr;
    }
    
    /**
     * Obter descrição do status
     */
    private String getStatusDescricao(String codigoStatus) {
        if (codigoStatus == null) return "Desconhecido";
        
        // Mapeamento simplificado de códigos de status
        switch (codigoStatus) {
            case "01": return "Pendente";
            case "02": return "Intimado";
            case "03": return "Retirado";
            case "04": return "Pago";
            case "05": return "Baixado";
            case "06": return "Cancelado";
            case "07": return "Revogado";
            default: return "Código " + codigoStatus;
        }
    }
    
    /**
     * Verificar se está apto para selagem
     */
    private boolean isAptoParaSelagem(Apontamento apontamento) {
        if (apontamento == null) return false;
        
        try {
            // Tentar chamar método isAptoParaSelagem se existir
            Object result = apontamento.getClass()
                .getMethod("isAptoParaSelagem")
                .invoke(apontamento);
            
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
        } catch (Exception e) {
            // Se não existir, usar lógica básica
            String status = this.situacao;
            String selo = this.seloaponta;
            BigDecimal valor = this.valor;
            
            // Status que não permitem selagem
            if (status == null || status.equals("05") || status.equals("06") || status.equals("07")) {
                return false;
            }
            
            // Já tem selo
            if (selo != null && !selo.trim().isEmpty()) {
                return false;
            }
            
            // Valor inválido
            if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Construtor vazio para serialização
     */
    public ApontamentoDTO() {
        // Construtor vazio para serialização JSON
    }
    
    // Getters e Setters
    public String getNumapo1() { return numapo1; }
    public void setNumapo1(String numapo1) { this.numapo1 = numapo1; }
    
    public String getNumapo2() { return numapo2; }
    public void setNumapo2(String numapo2) { this.numapo2 = numapo2; }
    
    public String getControle() { return controle; }
    public void setControle(String controle) { this.controle = controle; }
    
    public String getDevedor() { return devedor; }
    public void setDevedor(String devedor) { this.devedor = devedor; }
    
    public String getCgccpfsac() { return cgccpfsac; }
    public void setCgccpfsac(String cgccpfsac) { this.cgccpfsac = cgccpfsac; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public BigDecimal getProtocolo() { return protocolo; }
    public void setProtocolo(BigDecimal protocolo) { this.protocolo = protocolo; }
    
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    
    public String getSeloaponta() { return seloaponta; }
    public void setSeloaponta(String seloaponta) { this.seloaponta = seloaponta; }
    
    public String getDataApontamento() { return dataApontamento; }
    public void setDataApontamento(String dataApontamento) { this.dataApontamento = dataApontamento; }
    
    public String getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(String dataRetirada) { this.dataRetirada = dataRetirada; }
    
    public String getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(String dataPagamento) { this.dataPagamento = dataPagamento; }
    
    public String getDataBaixa() { return dataBaixa; }
    public void setDataBaixa(String dataBaixa) { this.dataBaixa = dataBaixa; }
    
    public String getStatusDescricao() { return statusDescricao; }
    public void setStatusDescricao(String statusDescricao) { this.statusDescricao = statusDescricao; }
    
    public boolean isAptoParaSelagem() { return aptoParaSelagem; }
    public void setAptoParaSelagem(boolean aptoParaSelagem) { this.aptoParaSelagem = aptoParaSelagem; }
    
    /**
     * Método para converter lista de Apontamentos para lista de DTOs
     */
    public static java.util.List<ApontamentoDTO> fromList(java.util.List<Apontamento> apontamentos) {
        if (apontamentos == null) return new java.util.ArrayList<>();
        
        return apontamentos.stream()
            .map(ApontamentoDTO::new)
            .collect(java.util.stream.Collectors.toList());
    }
}