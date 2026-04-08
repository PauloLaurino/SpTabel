package com.seprocom.gerencial.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO para Items de Nota Fiscal.
 * 
 * @author Seprocom
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotasDetDTO {

    private Long id;
    private Long notaCabId;
    private Integer numeroItem;
    private String codigoServico;
    private String discriminacao;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorDesc;
    private BigDecimal valorIss;
    private BigDecimal aliquota;
    private String tributavel;
}