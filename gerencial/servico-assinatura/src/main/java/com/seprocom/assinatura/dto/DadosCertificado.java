package com.seprocom.assinatura.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosCertificado {
    private String cn;
    private String cpfCnpj;
    private String validade;
    private String emissor;
    private String algoritmo;
}
