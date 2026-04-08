package com.seprocom.gerencial.entity;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade JPA para Itens de Nota Fiscal de Servicos (NFSe).
 * Mapeia a tabela notasdet do banco de dados sptabel.
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "notasdet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotasDet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nota_cab_id")
    private Long notaCabId;

    @Column(name = "numero_item")
    private Integer numeroItem;

    @Column(name = "codigo_servico", length = 20)
    private String codigoServico;

    @Column(name = "discriminacao", length = 500)
    private String discriminacao;

    @Column(name = "quantidade", precision = 10, scale = 4)
    @Builder.Default
    private BigDecimal quantidade = BigDecimal.ONE;

    @Column(name = "valor_unitario", precision = 15, scale = 4)
    @Builder.Default
    private BigDecimal valorUnitario = BigDecimal.ZERO;

    @Column(name = "valor_total", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "valor_desc", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorDesc = BigDecimal.ZERO;

    @Column(name = "valor_iss", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorIss = BigDecimal.ZERO;

    @Column(name = "aliquota", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal aliquota = BigDecimal.ZERO;

    @Column(name = "tributavel", length = 1)
    @Builder.Default
    private String tributavel = "S"; // S=Sim, N=Não

    /**
     * Calcula o valor do ISS baseado na aliquota e valor total.
     */
    public void calcularIss() {
        if (this.aliquota != null && this.aliquota.compareTo(BigDecimal.ZERO) > 0) {
            // Aliquota em formato de porcentagem
            this.valorIss = this.valorTotal.multiply(this.aliquota).divide(BigDecimal.valueOf(100));
        }
    }
}