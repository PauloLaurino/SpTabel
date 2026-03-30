package com.selador.web.dto;

import java.math.BigDecimal;
import java.util.Map;  // Adicione esta linha

public class EstatisticasDTO {
    private Map<String, Integer> totalPorStatus;
    private Map<String, Integer> selosDisponiveisPorTipo;
    private BigDecimal valorTotalSelado;
    private Integer totalApontamentos;
    private Integer totalAptos;
    private Map<String, Object> evolucaoDiaria;
    
    // Construtores
    public EstatisticasDTO() {
    }
    
    public EstatisticasDTO(Map<String, Integer> totalPorStatus, 
                          Map<String, Integer> selosDisponiveisPorTipo,
                          BigDecimal valorTotalSelado,
                          Integer totalApontamentos,
                          Integer totalAptos,
                          Map<String, Object> evolucaoDiaria) {
        this.totalPorStatus = totalPorStatus;
        this.selosDisponiveisPorTipo = selosDisponiveisPorTipo;
        this.valorTotalSelado = valorTotalSelado;
        this.totalApontamentos = totalApontamentos;
        this.totalAptos = totalAptos;
        this.evolucaoDiaria = evolucaoDiaria;
    }
    
    // Getters e Setters
    public Map<String, Integer> getTotalPorStatus() { 
        return totalPorStatus; 
    }
    
    public void setTotalPorStatus(Map<String, Integer> totalPorStatus) { 
        this.totalPorStatus = totalPorStatus; 
    }
    
    public Map<String, Integer> getSelosDisponiveisPorTipo() { 
        return selosDisponiveisPorTipo; 
    }
    
    public void setSelosDisponiveisPorTipo(Map<String, Integer> selosDisponiveisPorTipo) { 
        this.selosDisponiveisPorTipo = selosDisponiveisPorTipo; 
    }
    
    public BigDecimal getValorTotalSelado() { 
        return valorTotalSelado; 
    }
    
    public void setValorTotalSelado(BigDecimal valorTotalSelado) { 
        this.valorTotalSelado = valorTotalSelado; 
    }
    
    public Integer getTotalApontamentos() { 
        return totalApontamentos; 
    }
    
    public void setTotalApontamentos(Integer totalApontamentos) { 
        this.totalApontamentos = totalApontamentos; 
    }
    
    public Integer getTotalAptos() { 
        return totalAptos; 
    }
    
    public void setTotalAptos(Integer totalAptos) { 
        this.totalAptos = totalAptos; 
    }
    
    public Map<String, Object> getEvolucaoDiaria() { 
        return evolucaoDiaria; 
    }
    
    public void setEvolucaoDiaria(Map<String, Object> evolucaoDiaria) { 
        this.evolucaoDiaria = evolucaoDiaria; 
    }
    
    // Método toString para debug
    @Override
    public String toString() {
        return "EstatisticasDTO{" +
               "totalPorStatus=" + totalPorStatus +
               ", selosDisponiveisPorTipo=" + selosDisponiveisPorTipo +
               ", valorTotalSelado=" + valorTotalSelado +
               ", totalApontamentos=" + totalApontamentos +
               ", totalAptos=" + totalAptos +
               ", evolucaoDiaria=" + evolucaoDiaria +
               '}';
    }
}