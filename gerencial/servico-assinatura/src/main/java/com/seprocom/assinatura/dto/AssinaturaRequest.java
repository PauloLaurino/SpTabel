package com.seprocom.assinatura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssinaturaRequest {
    
    @NotBlank(message = "Documento é obrigatório")
    private String documento;
    
    @NotBlank(message = "Tipo de certificado é obrigatório")
    private String certificado;
    
    private String pin;
    
    @NotNull(message = "Timestamp é obrigatório")
    private Boolean timestamp;
}
