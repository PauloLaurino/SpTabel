package com.seprocom.gerencial.service;

import com.seprocom.gerencial.entity.NotasCab;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service para cálculo de impostos sobre serviços (NFSe)
 * Considera a Reforma Tributária (EC 132/2023) com CBS, IBS e CPP
 * 
 * @author Seprocom
 */
@Service
@Slf4j
public class CalculoImpostosService {

    // Alíquotas padrão
    private static final BigDecimal ALIQUOTA_ISS_DEFAULT = new BigDecimal("0.05");          // 5%
    private static final BigDecimal ALIQUOTA_CBS_DEFAULT = new BigDecimal("0.10");          // 10%
    private static final BigDecimal ALIQUOTA_IBS_DEFAULT = new BigDecimal("0.10");          // 10%
    private static final BigDecimal ALIQUOTA_PIS_DEFAULT = new BigDecimal("0.0165");        // 1,65%
    private static final BigDecimal ALIQUOTA_COFINS_DEFAULT = new BigDecimal("0.076");       // 7,6%
    private static final BigDecimal ALIQUOTA_CSLL_DEFAULT = new BigDecimal("0.09");          // 9%
    private static final BigDecimal ALIQUOTA_IRPJ_DEFAULT = new BigDecimal("0.15");          // 15%
    private static final BigDecimal ALIQUOTA_CPP_DEFAULT = new BigDecimal("0.12");           // 12%

    // Data de início da Reforma Tributária
    private static final LocalDate DATA_INICIO_REFORMA = LocalDate.of(2026, 4, 1);

    /**
     * Calcula os impostos sobre o valor do serviço
     * Considera a fase de transição da reforma tributária
     * 
     * @param notasCab Dados da nota fiscal
     * @return Resultado do cálculo com todos os impostos
     */
    public ResultadoCalculoImpostos calcularImpostos(NotasCab notasCab) {
        log.info("Iniciando cálculo de impostos para nota ID: {}", notasCab.getId());
        
        BigDecimal valorServico = notasCab.getValorServico() != null 
            ? notasCab.getValorServico() 
            : BigDecimal.ZERO;
        
        BigDecimal aliquotaIss = notasCab.getAliquota() != null && notasCab.getAliquota().compareTo(BigDecimal.ZERO) > 0
            ? notasCab.getAliquota()
            : ALIQUOTA_ISS_DEFAULT;
        
        LocalDate dataEmissao = notasCab.getDataEmissao() != null 
            ? notasCab.getDataEmissao() 
            : LocalDate.now();
        
        String codigoMunicipio = notasCab.getCodigoMunicipio();
        
        // Determinar fase da reforma
        FaseTransicao fase = determinarFase(dataEmissao);
        
        ResultadoCalculoImpostos resultado = ResultadoCalculoImpostos.builder()
            .valorServico(valorServico)
            .codigoMunicipio(codigoMunicipio)
            .dataEmissao(dataEmissao)
            .faseTransicao(fase.name())
            .build();
        
        // Calcular ISS (sempre aplica até 2033)
        BigDecimal valorIss = calcularIss(valorServico, aliquotaIss);
        resultado.setValorIss(valorIss);
        resultado.setAliquotaIss(aliquotaIss);
        
        // Se fase de transição ou plena, calcular CBS e IBS
        if (fase == FaseTransicao.TRANSICAO || fase == FaseTransicao.PLENA) {
            // CBS (federal)
            BigDecimal valorCbs = calcularCbs(valorServico);
            resultado.setValorCbs(valorCbs);
            resultado.setAliquotaCbs(ALIQUOTA_CBS_DEFAULT);
            
            // IBS (estadual/municipal)
            BigDecimal valorIbs = calcularIbs(valorServico);
            resultado.setValorIbs(valorIbs);
            resultado.setAliquotaIbs(ALIQUOTA_IBS_DEFAULT);
            
            // CPP (contribuição patronal - apenas na fase plena)
            if (fase == FaseTransicao.PLENA) {
                BigDecimal valorCpp = calcularCpp(valorServico);
                resultado.setValorCpp(valorCpp);
                resultado.setAliquotaCpp(ALIQUOTA_CPP_DEFAULT);
            }
        }
        
        // Calcular PIS/COFINS/CSLL/IRPJ (opcional, conforme configuração)
        // Por padrão, não calculamos para serviços de cartório
        BigDecimal valorPis = BigDecimal.ZERO;
        BigDecimal valorCofins = BigDecimal.ZERO;
        BigDecimal valorCsll = BigDecimal.ZERO;
        BigDecimal valorIrpj = BigDecimal.ZERO;
        
        resultado.setValorPis(valorPis);
        resultado.setValorCofins(valorCofins);
        resultado.setValorCsll(valorCsll);
        resultado.setValorIrpj(valorIrpj);
        
        // Calcular totais
        BigDecimal totalImpostos = resultado.getValorIss()
            .add(resultado.getValorCbs())
            .add(resultado.getValorIbs())
            .add(resultado.getValorCpp())
            .add(valorPis)
            .add(valorCofins)
            .add(valorCsll)
            .add(valorIrpj);
        
        resultado.setTotalImpostos(totalImpostos);
        resultado.setValorLiquido(valorServico.subtract(totalImpostos));
        
        log.info("Cálculo concluído: valor servico={}, total impostos={}, líquido={}", 
            valorServico, totalImpostos, resultado.getValorLiquido());
        
        return resultado;
    }

    /**
     * Determina a fase da reforma tributária baseada na data
     */
    private FaseTransicao determinarFase(LocalDate dataEmissao) {
        if (dataEmissao.isBefore(DATA_INICIO_REFORMA)) {
            return FaseTransicao.NAO_INICIADA;
        } else if (dataEmissao.isBefore(LocalDate.of(2028, 1, 1))) {
            return FaseTransicao.TRANSICAO;
        } else if (dataEmissao.isBefore(LocalDate.of(2033, 1, 1))) {
            return FaseTransicao.CONVERGENCIA;
        } else {
            return FaseTransicao.PLENA;
        }
    }

    /**
     * Calcula ISS
     */
    private BigDecimal calcularIss(BigDecimal valorServico, BigDecimal aliquota) {
        return valorServico.multiply(aliquota).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula CBS (Contribuição sobre Bens e Serviços)
     */
    private BigDecimal calcularCbs(BigDecimal valorServico) {
        return valorServico.multiply(ALIQUOTA_CBS_DEFAULT).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula IBS (Imposto sobre Bens e Serviços)
     */
    private BigDecimal calcularIbs(BigDecimal valorServico) {
        return valorServico.multiply(ALIQUOTA_IBS_DEFAULT).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula CPP (Contribuição Patronal)
     */
    private BigDecimal calcularCpp(BigDecimal valorServico) {
        return valorServico.multiply(ALIQUOTA_CPP_DEFAULT).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula PIS
     */
    private BigDecimal calcularPis(BigDecimal valorServico) {
        return valorServico.multiply(ALIQUOTA_PIS_DEFAULT).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula COFINS
     */
    private BigDecimal calcularCofins(BigDecimal valorServico) {
        return valorServico.multiply(ALIQUOTA_COFINS_DEFAULT).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Retorna lista de impostos para exibição detalhada
     */
    public List<ItemImposto> getItensImposto(ResultadoCalculoImpostos resultado) {
        List<ItemImposto> itens = new ArrayList<>();
        
        // ISS (sempre presente até 2033)
        if (resultado.getValorIss().compareTo(BigDecimal.ZERO) > 0) {
            itens.add(ItemImposto.builder()
                .codigo("ISS")
                .nome("Imposto Sobre Serviços")
                .aliquota(resultado.getAliquotaIss())
                .valor(resultado.getValorIss())
                .tipo("MUNICIPAL")
                .ordem(1)
                .build());
        }
        
        // CBS (após início da reforma)
        if (resultado.getValorCbs().compareTo(BigDecimal.ZERO) > 0) {
            itens.add(ItemImposto.builder()
                .codigo("CBS")
                .nome("Contribuição sobre Bens e Serviços")
                .aliquota(resultado.getAliquotaCbs())
                .valor(resultado.getValorCbs())
                .tipo("FEDERAL")
                .ordem(2)
                .build());
        }
        
        // IBS (após início da reforma)
        if (resultado.getValorIbs().compareTo(BigDecimal.ZERO) > 0) {
            itens.add(ItemImposto.builder()
                .codigo("IBS")
                .nome("Imposto sobre Bens e Serviços")
                .aliquota(resultado.getAliquotaIbs())
                .valor(resultado.getValorIbs())
                .tipo("ESTADUAL")
                .ordem(3)
                .build());
        }
        
        // CPP (apenas na fase plena)
        if (resultado.getValorCpp().compareTo(BigDecimal.ZERO) > 0) {
            itens.add(ItemImposto.builder()
                .codigo("CPP")
                .nome("Contribuição Patronal")
                .aliquota(resultado.getAliquotaCpp())
                .valor(resultado.getValorCpp())
                .tipo("FEDERAL")
                .ordem(4)
                .build());
        }
        
        return itens;
    }

    /**
     * Fase da transição tributária
     */
    public enum FaseTransicao {
        NAO_INICIADA,    // Antes de abril/2026
        TRANSICAO,       // 2026-2027
        CONVERGENCIA,    // 2028-2032
        PLENA            // 2033+
    }

    /**
     * Resultado do cálculo de impostos
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class ResultadoCalculoImpostos {
        private BigDecimal valorServico;
        private String codigoMunicipio;
        private LocalDate dataEmissao;
        private String faseTransicao;
        
        // Valores
        @Builder.Default
        private BigDecimal valorIss = BigDecimal.ZERO;
        private BigDecimal aliquotaIss;
        
        @Builder.Default
        private BigDecimal valorCbs = BigDecimal.ZERO;
        private BigDecimal aliquotaCbs;
        
        @Builder.Default
        private BigDecimal valorIbs = BigDecimal.ZERO;
        private BigDecimal aliquotaIbs;
        
        @Builder.Default
        private BigDecimal valorCpp = BigDecimal.ZERO;
        private BigDecimal aliquotaCpp;
        
        @Builder.Default
        private BigDecimal valorPis = BigDecimal.ZERO;
        @Builder.Default
        private BigDecimal valorCofins = BigDecimal.ZERO;
        @Builder.Default
        private BigDecimal valorCsll = BigDecimal.ZERO;
        @Builder.Default
        private BigDecimal valorIrpj = BigDecimal.ZERO;
        
        @Builder.Default
        private BigDecimal totalImpostos = BigDecimal.ZERO;
        @Builder.Default
        private BigDecimal valorLiquido = BigDecimal.ZERO;
    }

    /**
     * Item individual de imposto para exibição
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class ItemImposto {
        private String codigo;
        private String nome;
        private BigDecimal aliquota;
        private BigDecimal valor;
        private String tipo;
        private Integer ordem;
    }
}