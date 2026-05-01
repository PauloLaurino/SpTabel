package com.seprocom.assinatura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssinaturaResponse {
    private String status;
    private String documento_assinado;
    private DadosCertificado certificado;
    private String timestamp;
    private String mensagem;
}
