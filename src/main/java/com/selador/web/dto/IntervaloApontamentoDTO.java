package com.selador.web.dto;

/**
 * DTO para intervalo de apontamentos - VERSÃO COM MÉTODOS UTILITÁRIOS
 */
public class IntervaloApontamentoDTO {
    private String numApo1Ini;    // Ano/Mês Inicial
    private String numApo2Ini;    // Apon. Inicial  
    private String numApo1Fim;    // Ano/Mês Final
    private String numApo2Fim;    // Apon. Final
    
    // Campos para compatibilidade com frontend (nomes diferentes)
    private String numapo1;       // Para 'primeiro.numapo1'
    private String numapo2;       // Para 'primeiro.numapo2'
    
    public IntervaloApontamentoDTO() {}
    
    public IntervaloApontamentoDTO(String numApo1Ini, String numApo2Ini, 
                                  String numApo1Fim, String numApo2Fim) {
        this.numApo1Ini = numApo1Ini;
        this.numApo2Ini = numApo2Ini;
        this.numApo1Fim = numApo1Fim;
        this.numApo2Fim = numApo2Fim;
        
        // Para compatibilidade
        this.numapo1 = numApo1Ini;
        this.numapo2 = numApo2Ini;
    }
    
    // Getters e Setters padrão
    public String getNumApo1Ini() { return numApo1Ini; }
    public void setNumApo1Ini(String numApo1Ini) { 
        this.numApo1Ini = numApo1Ini;
        this.numapo1 = numApo1Ini; // Sincronizar
    }
    
    public String getNumApo2Ini() { return numApo2Ini; }
    public void setNumApo2Ini(String numApo2Ini) { 
        this.numApo2Ini = numApo2Ini;
        this.numapo2 = numApo2Ini; // Sincronizar
    }
    
    public String getNumApo1Fim() { return numApo1Fim; }
    public void setNumApo1Fim(String numApo1Fim) { this.numApo1Fim = numApo1Fim; }
    
    public String getNumApo2Fim() { return numApo2Fim; }
    public void setNumApo2Fim(String numApo2Fim) { this.numApo2Fim = numApo2Fim; }
    
    // Getters para compatibilidade com frontend
    public String getNumapo1() { 
        return numapo1 != null ? numapo1 : numApo1Ini; 
    }
    
    public String getNumapo2() { 
        return numapo2 != null ? numapo2 : numApo2Ini; 
    }
    
    // MÉTODOS UTILITÁRIOS NOVOS
    public String getPrimeiroApontamento() {
        return getNumApo1Ini() + "/" + getNumApo2Ini();
    }
    
    public String getUltimoApontamento() {
        return getNumApo1Fim() + "/" + getNumApo2Fim();
    }
    
    public boolean isValido() {
        return numApo1Ini != null && !numApo1Ini.isEmpty() &&
               numApo2Ini != null && !numApo2Ini.isEmpty();
    }
    
    @Override
    public String toString() {
        return "IntervaloApontamentoDTO{" +
               "primeiro='" + getPrimeiroApontamento() + '\'' +
               ", ultimo='" + getUltimoApontamento() + '\'' +
               '}';
    }
}