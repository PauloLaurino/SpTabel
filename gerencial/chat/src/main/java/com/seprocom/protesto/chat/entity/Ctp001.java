package com.seprocom.protesto.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que mapeia a tabela ctp001 do sistema de protesto.
 * 
 * Chave composta: numapo1_001 (char 4) + numapo2_001 (char 10) + controle_001 (char 2)
 * 
 * Campos atualizáveis pelo chat:
 * - dtintimacao_001: Data de intimação (DDMMAAAA)
 * - dataocr_001: Data de ocorrência (AAAAMMDD)
 * - datapag_001: Data do pagamento
 * 
 * @author Seprocom
 * @version 1.0.0
 */
@Entity
@Table(name = "ctp001")
@IdClass(Ctp001Id.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Ctp001 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Número do apontamento 1 (AAAAMM) - parte da chave composta
     */
    @Id
    @Column(name = "numapo1_001", length = 4, nullable = false)
    private String numapo1_001;

    /**
     * Número do apontamento 2 (NNNNNNNNNN) - parte da chave composta
     */
    @Id
    @Column(name = "numapo2_001", length = 10, nullable = false)
    private String numapo2_001;

    /**
     * Controle (NN) - parte da chave composta
     */
    @Id
    @Column(name = "controle_001", length = 2, nullable = false)
    private String controle_001;

    /**
     * Nome do devedor
     */
    @Column(name = "devedor_001", length = 100)
    private String devedor_001;

    /**
     * CPF/CNPJ do devedor
     */
    @Column(name = "cpfcnpj_001", length = 20)
    private String cpfcnpj_001;

    /**
     * Endereço do devedor
     */
    @Column(name = "endereco_001", length = 200)
    private String endereco_001;

    /**
     * Cidade do devedor
     */
    @Column(name = "cidade_001", length = 60)
    private String cidade_001;

    /**
     * UF do devedor
     */
    @Column(name = "uf_001", length = 2)
    private String uf_001;

    /**
     * CEP do devedor
     */
    @Column(name = "cep_001", length = 10)
    private String cep_001;

    /**
     * Valor do título
     */
    @Column(name = "valor_001", precision = 15, scale = 2)
    private BigDecimal valor_001;

    /**
     * Espécie do título
     */
    @Column(name = "especie_001", length = 10)
    private String especie_001;

    /**
     * Número do título
     */
    @Column(name = "numtitulo_001", length = 20)
    private String numtitulo_001;

    /**
     * Data de vencimento
     */
    @Column(name = "datavenc_001", length = 8)
    private String datavenc_001;

    /**
     * Nome do apresentante/credor
     */
    @Column(name = "apresentante_001", length = 100)
    private String apresentante_001;

    /**
     * Data de intimação (DDMMAAAA)
     * Registrado quando o devedor é intimado
     */
    @Column(name = "dtintimacao_001", length = 8)
    private String dtintimacao_001;

    /**
     * Data de ocorrência (AAAAMMDD)
     * Registrado quando há uma ocorrência
     */
    @Column(name = "dataocr_001", length = 8)
    private String dataocr_001;

    /**
     * Data do pagamento
     */
    @Column(name = "datapag_001")
    private LocalDate datapag_001;

    /**
     * Situação do título
     */
    @Column(name = "situacao_001", length = 2)
    private String situacao_001;

    /**
     * Protocolo
     */
    @Column(name = "protocolo_001", length = 20)
    private String protocolo_001;

    /**
     * Data do protocolo
     */
    @Column(name = "dataprot_001", length = 8)
    private String dataprot_001;

    /**
     * Saldo devedor
     */
    @Column(name = "saldo_001", precision = 15, scale = 2)
    private BigDecimal saldo_001;

    /**
     * Custas/emolumentos
     */
    @Column(name = "custas_001", precision = 15, scale = 2)
    private BigDecimal custas_001;
}
