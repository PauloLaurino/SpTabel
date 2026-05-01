package com.seprocom.assinatura.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgruparRequest {
    
    @NotBlank(message = "Pasta é obrigatória")
    private String pasta;
    
    private String prefixo;
    
    @NotBlank(message = "Caminho de saída é obrigatório")
    private String saida;
}