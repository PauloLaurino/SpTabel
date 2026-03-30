package com.selador.service;

import com.selador.dao.SeloDAO;
import com.selador.enums.TipoOperacao;
import com.selador.exception.DatabaseException;
import com.selador.model.Apontamento;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * Service para geração de relatórios e estatísticas
 */
public class ReportService {
    
    private static final Logger logger = Logger.getLogger(ReportService.class.getName());
    
    private final ApontamentoService apontamentoService;
    private static ReportService instance;
    
    private ReportService() {
        this.apontamentoService = ApontamentoService.getInstance();
    }
    
    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }
    
    /**
     * Gera relatório básico de produtividade por período
     */
    public Map<String, Object> gerarRelatorioProdutividade(Date dataInicial, Date dataFinal) 
            throws Exception {
        
        Map<String, Object> relatorio = new HashMap<>();
        
        try {
            // Buscar apontamentos por período para cada tipo de operação
            List<Map<String, Object>> distribuicaoPorTipo = new ArrayList<>();
            int totalApontamentos = 0;
            
            for (TipoOperacao tipo : TipoOperacao.values()) {
                try {
                    List<Apontamento> apontamentos = apontamentoService.buscarPorPeriodo(
                        dataInicial, dataFinal, tipo);
                    
                    if (!apontamentos.isEmpty()) {
                        Map<String, Object> tipoData = new HashMap<>();
                        tipoData.put("tipo", tipo.name());
                        tipoData.put("descricao", tipo.getDescricao());
                        tipoData.put("quantidade", apontamentos.size());
                        
                        distribuicaoPorTipo.add(tipoData);
                        totalApontamentos += apontamentos.size();
                    }
                } catch (Exception e) {
                    logger.warning("Erro ao buscar apontamentos para tipo " + tipo + ": " + e.getMessage());
                }
            }
            
            relatorio.put("periodo", formatarPeriodo(dataInicial, dataFinal));
            relatorio.put("totalApontamentos", totalApontamentos);
            relatorio.put("distribuicaoPorTipo", distribuicaoPorTipo);
            relatorio.put("dataGeracao", new Date());
            
            logger.info("Relatório de produtividade gerado: " + totalApontamentos + " apontamentos");
            
        } catch (Exception e) {
            logger.severe("Erro ao gerar relatório de produtividade: " + e.getMessage());
            throw new Exception("Erro ao gerar relatório", e);
        }
        
        return relatorio;
    }
    
    /**
     * Gera relatório financeiro básico por período
     */
    public Map<String, Object> gerarRelatorioFinanceiro(Date dataInicial, Date dataFinal) 
            throws Exception {
        
        Map<String, Object> relatorio = new HashMap<>();
        BigDecimal valorTotal = BigDecimal.ZERO;
        List<Map<String, Object>> topDevedores = new ArrayList<>();
        
        try {
            // Calcular valor total por tipo de operação
            Map<String, BigDecimal> valorPorTipo = new HashMap<>();
            
            for (TipoOperacao tipo : TipoOperacao.values()) {
                try {
                    List<Apontamento> apontamentos = apontamentoService.buscarPorPeriodo(
                        dataInicial, dataFinal, tipo);
                    
                    BigDecimal valorTipo = BigDecimal.ZERO;
                    for (Apontamento ap : apontamentos) {
                        if (ap.getValor() != null) {
                            valorTipo = valorTipo.add(ap.getValor());
                        }
                    }
                    
                    if (valorTipo.compareTo(BigDecimal.ZERO) > 0) {
                        valorPorTipo.put(tipo.getDescricao(), valorTipo);
                        valorTotal = valorTotal.add(valorTipo);
                    }
                } catch (Exception e) {
                    logger.warning("Erro ao processar tipo " + tipo + " para relatório financeiro: " + e.getMessage());
                }
            }
            
            // Identificar maiores devedores (simplificado)
            topDevedores = identificarTopDevedores(dataInicial, dataFinal, 10);
            
            relatorio.put("periodo", formatarPeriodo(dataInicial, dataFinal));
            relatorio.put("valorTotal", valorTotal);
            relatorio.put("valorPorTipo", valorPorTipo);
            relatorio.put("topDevedores", topDevedores);
            relatorio.put("dataGeracao", new Date());
            
            logger.info("Relatório financeiro gerado: Valor total = " + valorTotal);
            
        } catch (Exception e) {
            logger.severe("Erro ao gerar relatório financeiro: " + e.getMessage());
            throw new Exception("Erro ao gerar relatório financeiro", e);
        }
        
        return relatorio;
    }
    
    /**
     * Gera relatório de estoque de selos
     */
    public Map<String, Object> gerarRelatorioEstoque() throws Exception {
        Map<String, Object> relatorio = new HashMap<>();
        
        try {
            SeloDAO seloDAO = new SeloDAO();
            Map<String, Integer> disponiveisPorTipo = seloDAO.contarDisponiveis();
            
            // Calcular totais
            int totalDisponivel = 0;
            for (Integer quantidade : disponiveisPorTipo.values()) {
                totalDisponivel += quantidade;
            }
            
            // Calcular projeções
            Map<String, Integer> projecaoDias = calcularProjecaoEstoque(disponiveisPorTipo);
            
            relatorio.put("dataGeracao", new Date());
            relatorio.put("totalDisponivel", totalDisponivel);
            relatorio.put("disponiveisPorTipo", disponiveisPorTipo);
            relatorio.put("projecaoDias", projecaoDias);
            
            logger.info("Relatório de estoque gerado: " + totalDisponivel + " selos disponíveis");
            
        } catch (DatabaseException e) {
            logger.severe("Erro ao gerar relatório de estoque: " + e.getMessage());
            throw new Exception("Erro ao gerar relatório de estoque", e);
        }
        
        return relatorio;
    }
    
    /**
     * Gera relatório de status dos apontamentos para uma data específica
     */
    public Map<String, Object> gerarRelatorioStatus(Date data, TipoOperacao tipoOperacao) 
            throws Exception {
        
        Map<String, Object> relatorio = new HashMap<>();
        
        try {
            
            
            relatorio.put("data", formatarData(data));
            relatorio.put("tipoOperacao", tipoOperacao.getDescricao());
            relatorio.put("dataGeracao", new Date());
            
            
        } catch (Exception e) {
            logger.severe("Erro ao gerar relatório de status: " + e.getMessage());
            throw new Exception("Erro ao gerar relatório de status", e);
        }
        
        return relatorio;
    }
    
    /**
     * Gera relatório consolidado para dashboard
     */
    public Map<String, Object> gerarRelatorioDashboard() throws Exception {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            Date hoje = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            Date trintaDiasAtras = cal.getTime();
            
            // Estatísticas do dia
            Map<String, Object> hojeStats = new HashMap<>();
            int hojeTotal = 0;
            int hojeAptos = 0;
            
            for (TipoOperacao tipo : TipoOperacao.values()) {
                try {
                    List<Apontamento> apontamentos = apontamentoService.buscarPorData(hoje, tipo);
                    hojeTotal += apontamentos.size();
                } catch (Exception e) {
                    // Ignorar tipos sem dados
                }
            }
            
            hojeStats.put("total", hojeTotal);
            hojeStats.put("aptos", hojeAptos);
            
            // Estatísticas dos últimos 30 dias
            Map<String, Object> mensalStats = new HashMap<>();
            int mensalTotal = 0;
            BigDecimal mensalValor = BigDecimal.ZERO;
            
            for (TipoOperacao tipo : TipoOperacao.values()) {
                try {
                    List<Apontamento> apontamentos = apontamentoService.buscarPorPeriodo(
                        trintaDiasAtras, hoje, tipo);
                    mensalTotal += apontamentos.size();
                    
                    for (Apontamento ap : apontamentos) {
                        if (ap.getValor() != null) {
                            mensalValor = mensalValor.add(ap.getValor());
                        }
                    }
                } catch (Exception e) {
                    // Ignorar tipos sem dados
                }
            }
            
            mensalStats.put("total", mensalTotal);
            mensalStats.put("valor", mensalValor);
            
            // Estoque atual
            Map<String, Object> estoqueStats = gerarRelatorioEstoque();
            
            dashboard.put("hoje", hojeStats);
            dashboard.put("ultimos30Dias", mensalStats);
            dashboard.put("estoque", estoqueStats);
            dashboard.put("ultimaAtualizacao", new Date());
            
            logger.info("Dashboard gerado: Hoje=" + hojeTotal + ", 30 dias=" + mensalTotal);
            
        } catch (Exception e) {
            logger.severe("Erro ao gerar dashboard: " + e.getMessage());
            throw new Exception("Erro ao gerar dashboard", e);
        }
        
        return dashboard;
    }
    
    private List<Map<String, Object>> identificarTopDevedores(Date dataInicial, Date dataFinal, int limite) {
        List<Map<String, Object>> topDevedores = new ArrayList<>();
        
        try {
            // Para cada tipo de operação, agrupar por devedor
            Map<String, BigDecimal> valorPorDevedor = new HashMap<>();
            
            for (TipoOperacao tipo : TipoOperacao.values()) {
                try {
                    List<Apontamento> apontamentos = apontamentoService.buscarPorPeriodo(
                        dataInicial, dataFinal, tipo);
                    
                    for (Apontamento ap : apontamentos) {
                        if (ap.getValor() != null && ap.getDevedor() != null) {
                            String devedor = ap.getDevedor().trim();
                            if (!devedor.isEmpty()) {
                                BigDecimal valorAtual = valorPorDevedor.getOrDefault(devedor, BigDecimal.ZERO);
                                valorPorDevedor.put(devedor, valorAtual.add(ap.getValor()));
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continuar com outros tipos
                }
            }
            
            // Ordenar por valor e pegar os top N
            List<Map.Entry<String, BigDecimal>> entries = new ArrayList<>(valorPorDevedor.entrySet());
            entries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            
            for (int i = 0; i < Math.min(limite, entries.size()); i++) {
                Map.Entry<String, BigDecimal> entry = entries.get(i);
                Map<String, Object> devedorInfo = new HashMap<>();
                devedorInfo.put("nome", entry.getKey());
                devedorInfo.put("valor", entry.getValue());
                devedorInfo.put("posicao", i + 1);
                topDevedores.add(devedorInfo);
            }
            
        } catch (Exception e) {
            logger.warning("Erro ao identificar top devedores: " + e.getMessage());
        }
        
        return topDevedores;
    }
    
    private Map<String, Integer> calcularProjecaoEstoque(Map<String, Integer> disponiveisPorTipo) {
        Map<String, Integer> projecao = new HashMap<>();
        
        // Consumo médio estimado (dados fictícios - pode ser ajustado)
        Map<String, Integer> consumoDiarioEstimado = new HashMap<>();
        consumoDiarioEstimado.put("0001", 10); // TP1
        consumoDiarioEstimado.put("0003", 5);  // TPD
        consumoDiarioEstimado.put("0004", 15); // TPI
        consumoDiarioEstimado.put("0009", 2);  // TP3
        consumoDiarioEstimado.put("0010", 3);  // TP4
        
        for (Map.Entry<String, Integer> entry : disponiveisPorTipo.entrySet()) {
            String tipo = entry.getKey();
            int disponivel = entry.getValue();
            int consumoDiario = consumoDiarioEstimado.getOrDefault(tipo, 5);
            
            int diasRestantes = consumoDiario > 0 ? disponivel / consumoDiario : 0;
            projecao.put(tipo, diasRestantes);
        }
        
        return projecao;
    }
    
    private String formatarPeriodo(Date inicio, Date fim) {
        return formatarData(inicio) + " a " + formatarData(fim);
    }
    
    private String formatarData(Date data) {
        if (data == null) return "";
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }
}