package com.seprocom.assinatura.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssinaturaCaminhoRequest {
    
    @NotBlank(message = "Caminho do PDF e obrigatorio")
    private String caminho;
    
    private String saida;
    
    private String pin;
    
    private Boolean pdfa;
    
    private Boolean timestamp = true;
}
