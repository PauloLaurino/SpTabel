package com.selador.service;

import com.selador.dao.ApontamentoDAO;
import com.selador.model.Apontamento;
import com.selador.util.TipoOperacaoMapeamento;
import com.selador.web.dto.IntervaloApontamentoDTO;
import com.selador.exception.DatabaseException;
import com.selador.enums.TipoOperacao;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Service para operações com apontamentos
 * VERSÃO COMPLETA E CORRIGIDA: Usa mapeamento correto por tipo de operação
 */
public class ApontamentoService {
    
    private static final Logger logger = Logger.getLogger(ApontamentoService.class.getName());
    
    private final ApontamentoDAO apontamentoDAO;
    private static ApontamentoService instance;
    
    private ApontamentoService() {
        this.apontamentoDAO = new ApontamentoDAO();
    }
    
    public static synchronized ApontamentoService getInstance() {
        if (instance == null) {
            instance = new ApontamentoService();
        }
        return instance;
    }
    
    /**
     * Busca apontamentos por data e tipo de operação (CORRIGIDO)
     * Agora recebe String tipoFrontend em vez de TipoOperacao enum
     */
    public List<Apontamento> buscarPorData(String dataFormatada, String tipoFrontend) throws Exception {
        try {
            System.out.println("🔍 [SERVICE] Buscando apontamentos por data:");
            System.out.println("   Data: " + dataFormatada);
            System.out.println("   Tipo: " + tipoFrontend);
            
            // Validar tipo
            if (!TipoOperacaoMapeamento.isLabelValido(tipoFrontend)) {
                throw new IllegalArgumentException("Tipo de operação inválido: " + tipoFrontend);
            }
            
            // Usar o novo método do DAO que aceita String
            List<Apontamento> resultado = apontamentoDAO.buscarPorData(dataFormatada, tipoFrontend);
            
            System.out.println("✅ [SERVICE] Apontamentos encontrados: " + resultado.size());
            return resultado;
            
        } catch (DatabaseException e) {
            logger.severe("Erro ao buscar apontamentos por data: " + e.getMessage());
            throw new Exception("Erro ao buscar apontamentos", e);
        } catch (Exception e) {
            logger.severe("Erro geral ao buscar apontamentos: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Método mantido para compatibilidade (usa enum)
     * Converte TipoOperacao para String e chama o novo método
     */
    public List<Apontamento> buscarPorData(Date data, TipoOperacao tipoOperacao) throws Exception {
        try {
            // Converter Date para String no formato DD/MM/YYYY
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(data);
            
            // Converter TipoOperacao para String (label)
            String tipoFrontend = tipoOperacao.getDescricao();
            
            // Chamar o novo método
            return buscarPorData(dataFormatada, tipoFrontend);
            
        } catch (Exception e) {
            logger.severe("Erro na conversão compatível: " + e.getMessage());
            throw new Exception("Erro ao buscar apontamentos", e);
        }
    }
    
    /**
     * Busca intervalo de apontamentos por data e tipo (CORRIGIDO)
     * Versão principal que recebe String tipoFrontend
     */
    public IntervaloApontamentoDTO buscarIntervaloPorDataETipo(String dataFormatada, String tipoFrontend) throws Exception {
        System.out.println("🔍 [SERVICE] Buscando intervalo - VERSÃO CORRIGIDA:");
        System.out.println("   Data: " + dataFormatada);
        System.out.println("   Tipo: " + tipoFrontend);
        
        // Validar tipo
        if (!TipoOperacaoMapeamento.isLabelValido(tipoFrontend)) {
            throw new IllegalArgumentException("Tipo de operação inválido: " + tipoFrontend);
        }
        
        try {
            IntervaloApontamentoDTO resultado = apontamentoDAO.buscarIntervaloPorTipoEData(dataFormatada, tipoFrontend, null);
            System.out.println("   ✅ Resultado do DAO: " + (resultado != null ? resultado.toString() : "null"));
            return resultado;
        } catch (Exception e) {
            System.out.println("❌ [SERVICE] Erro no DAO: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Busca intervalo de apontamentos por data e código (CORRIGIDO)
     */
    public IntervaloApontamentoDTO buscarIntervaloPorDataECodigo(String dataFormatada, 
                                                                String codigoOperacao,
                                                                String descricaoOperacao) throws Exception {
        
        System.out.println("🔍 [SERVICE] Buscando intervalo por CÓDIGO:");
        System.out.println("   Data: " + dataFormatada);
        System.out.println("   Código: " + codigoOperacao);
        System.out.println("   Descrição: " + descricaoOperacao);
        
        try {
            // VALIDAR PARÂMETROS
            if (dataFormatada == null || dataFormatada.trim().isEmpty()) {
                throw new IllegalArgumentException("Data é obrigatória");
            }
            
            if (codigoOperacao == null || codigoOperacao.trim().isEmpty()) {
                throw new IllegalArgumentException("Código da operação é obrigatório");
            }
            
            // Garantir que data está no formato YYYY-MM-DD (padrão frontend)
            // Se vier como DD/MM/YYYY, converter
            String dataParaBusca = dataFormatada;
            if (dataFormatada.contains("/")) {
                // Converter DD/MM/YYYY para YYYY-MM-DD
                String[] partes = dataFormatada.split("/");
                if (partes.length == 3) {
                    dataParaBusca = partes[2] + "-" + partes[1] + "-" + partes[0];
                }
            }
            
            System.out.println("🔄 [SERVICE] Data convertida para busca: " + dataParaBusca);
            
            // CHAMAR O DAO
            IntervaloApontamentoDTO resultado = apontamentoDAO.buscarIntervaloPorTipoEData(
                codigoOperacao, 
                descricaoOperacao, 
                dataParaBusca
            );
            
            if (resultado == null) {
                System.out.println("⚠️ [SERVICE] Nenhum intervalo encontrado");
                
                // Criar DTO vazio
                IntervaloApontamentoDTO dtoVazio = new IntervaloApontamentoDTO();
                dtoVazio.setNumApo1Ini("");
                dtoVazio.setNumApo2Ini("");
                dtoVazio.setNumApo1Fim("");
                dtoVazio.setNumApo2Fim("");
                return dtoVazio;
            }
            
            System.out.println("✅ [SERVICE] Intervalo encontrado:");
            System.out.println("   Primeiro: " + resultado.getNumApo1Ini() + "/" + resultado.getNumApo2Ini());
            System.out.println("   Último: " + resultado.getNumApo1Fim() + "/" + resultado.getNumApo2Fim());
            
            return resultado;
            
        } catch (DatabaseException e) {
            System.err.println("❌ [SERVICE] Erro no DAO: " + e.getMessage());
            throw new Exception("Erro ao buscar intervalo no banco de dados", e);
        } catch (Exception e) {
            System.err.println("❌ [SERVICE] Erro geral: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Método mantido para compatibilidade (usa enum)
     */
    public IntervaloApontamentoDTO buscarIntervaloPorDataETipo(String dataFormatada, TipoOperacao tipoOperacao) throws Exception {
        System.out.println("⚠️ [SERVICE] Usando versão compatível (enum)");
        
        // Converter TipoOperacao para String
        String tipoFrontend = tipoOperacao.getDescricao();
        
        // Chamar o novo método
        return buscarIntervaloPorDataETipo(dataFormatada, tipoFrontend);
    }
    
    /**
     * Busca apontamentos por período (COMPLETO E CORRIGIDO)
     */
    public List<Apontamento> buscarPorPeriodo(String dataInicio, String dataFim, String tipoFrontend) throws Exception {
        try {
            System.out.println("🔍 [SERVICE] Buscando por período - VERSÃO COMPLETA:");
            System.out.println("   Início: " + dataInicio);
            System.out.println("   Fim: " + dataFim);
            System.out.println("   Tipo: " + tipoFrontend);
            
            // Validar tipo
            if (!TipoOperacaoMapeamento.isLabelValido(tipoFrontend)) {
                throw new IllegalArgumentException("Tipo de operação inválido: " + tipoFrontend);
            }
            
            // Validar datas
            if (dataInicio == null || dataFim == null) {
                throw new IllegalArgumentException("Datas de início e fim são obrigatórias");
            }
            
            // Verificar se data início <= data fim
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            Date inicioDate = sdf.parse(dataInicio);
            Date fimDate = sdf.parse(dataFim);
            
            if (inicioDate.after(fimDate)) {
                throw new IllegalArgumentException("Data de início não pode ser maior que data de fim");
            }
            
            // Usar o método do DAO
            List<Apontamento> resultado = apontamentoDAO.buscarPorPeriodo(dataInicio, dataFim, tipoFrontend);
            
            System.out.println("✅ [SERVICE] Período retornou " + resultado.size() + " apontamentos");
            return resultado;
            
        } catch (DatabaseException e) {
            logger.severe("Erro ao buscar apontamentos por período: " + e.getMessage());
            throw new Exception("Erro ao buscar apontamentos por período", e);
        } catch (java.text.ParseException e) {
            logger.severe("Erro ao parsear datas: " + e.getMessage());
            throw new Exception("Formato de data inválido. Use DD/MM/YYYY", e);
        } catch (Exception e) {
            logger.severe("Erro geral ao buscar por período: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Método mantido para compatibilidade
     */
    public List<Apontamento> buscarPorPeriodo(Date dataInicio, Date dataFim, TipoOperacao tipoOperacao) throws Exception {
        try {
            // Converter Date para String
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String dataInicioFormatada = sdf.format(dataInicio);
            String dataFimFormatada = sdf.format(dataFim);
            
            // Converter TipoOperacao para String
            String tipoFrontend = tipoOperacao.getDescricao();
            
            // Chamar o novo método
            return buscarPorPeriodo(dataInicioFormatada, dataFimFormatada, tipoFrontend);
        } catch (Exception e) {
            logger.severe("Erro ao buscar apontamentos por período: " + e.getMessage());
            throw new Exception("Erro ao buscar apontamentos por período", e);
        }
    }
    
    /**
     * Busca apontamento específico pelas chaves
     */
    public Apontamento buscarApontamentoPorChave(String numApo1, String numApo2) throws Exception {
        try {
            return apontamentoDAO.buscarPorNumero(numApo1, numApo2);
        } catch (DatabaseException e) {
            logger.severe(String.format("Erro ao buscar apontamento %s/%s: %s", 
                numApo1, numApo2, e.getMessage()));
            throw new Exception("Erro ao buscar apontamento", e);
        }
    }
    
    /**
     * Atualiza selo no apontamento
     */
    public boolean atualizarSeloApontamento(String numApo1, String numApo2, String selo) throws Exception {
        if (numApo1 == null || numApo2 == null || selo == null) {
            throw new IllegalArgumentException("Parâmetros não podem ser nulos");
        }
        
        try {
            int rowsAffected = apontamentoDAO.atualizarSelo(numApo1, numApo2, selo);
            
            if (rowsAffected > 0) {
                logger.info(String.format("Selo %s atribuído ao apontamento %s/%s", 
                    selo, numApo1, numApo2));
                return true;
            } else {
                logger.warning(String.format("Falha ao atribuir selo %s ao apontamento %s/%s", 
                    selo, numApo1, numApo2));
                return false;
            }
            
        } catch (DatabaseException e) {
            logger.severe(String.format("Erro ao atualizar selo no apontamento %s/%s: %s",
                numApo1, numApo2, e.getMessage()));
            throw new Exception("Erro ao atualizar apontamento", e);
        }
    }
    
    /**
     * Atualiza múltiplos selos em lote
     */
    public boolean atualizarSelosEmLote(Map<String, String> apontamentosSelos) throws Exception {
        if (apontamentosSelos == null || apontamentosSelos.isEmpty()) {
            throw new IllegalArgumentException("Map de apontamentos não pode ser nulo ou vazio");
        }
        
        try {
            apontamentoDAO.atualizarSelosEmLote(apontamentosSelos);
            logger.info(String.format("Atualizados %d apontamentos em lote", 
                apontamentosSelos.size()));
            return true;
            
        } catch (DatabaseException e) {
            logger.severe("Erro ao atualizar selos em lote: " + e.getMessage());
            throw new Exception("Erro ao atualizar selos em lote", e);
        }
    }
    
    /**
     * Busca apontamentos não selados (CORRIGIDO)
     */
    public List<Apontamento> buscarNaoSelados(String dataFormatada, String tipoFrontend) throws Exception {
        try {
            System.out.println("🔍 [SERVICE] Buscando não selados:");
            System.out.println("   Data: " + dataFormatada);
            System.out.println("   Tipo: " + tipoFrontend);
            
            // Validar tipo
            if (!TipoOperacaoMapeamento.isLabelValido(tipoFrontend)) {
                throw new IllegalArgumentException("Tipo de operação inválido: " + tipoFrontend);
            }
            
            // Usar método do DAO
            List<Apontamento> resultado = apontamentoDAO.buscarNaoSelados(dataFormatada, tipoFrontend);
            
            System.out.println("✅ [SERVICE] Não selados encontrados: " + resultado.size());
            return resultado;
            
        } catch (DatabaseException e) {
            logger.severe("Erro ao buscar apontamentos não selados: " + e.getMessage());
            throw new Exception("Erro ao buscar apontamentos não selados", e);
        }
    }
    
    /**
     * Método mantido para compatibilidade
     */
    public List<Apontamento> buscarNaoSelados(Date data, TipoOperacao tipoOperacao) throws Exception {
        try {
            // Converter Date para String
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(data);
            
            // Converter TipoOperacao para String
            String tipoFrontend = tipoOperacao.getDescricao();
            
            // Chamar o novo método
            return buscarNaoSelados(dataFormatada, tipoFrontend);
        } catch (Exception e) {
            logger.severe("Erro ao buscar apontamentos não selados: " + e.getMessage());
            throw new Exception("Erro ao buscar apontamentos não selados", e);
        }
    }
    
    /**
     * Conta registros não selados - VERSÃO ATUALIZADA COM INTERVALO COMPLETO
     */
    public Map<String, Object> contarRegistrosNaoSelados(String dataFormatada, String tipoFrontend,
                                                       String anoMesInicial, String anoMesFinal,
                                                       String aponInicial, String aponFinal) throws DatabaseException {
        
        System.out.println("🔍 [SERVICE] Contando registros não selados:");
        System.out.println("   Tipo: " + tipoFrontend);
        System.out.println("   Data: " + dataFormatada);
        System.out.println("   Intervalo: " + anoMesInicial + " a " + anoMesFinal);
        System.out.println("   Apontamentos: " + aponInicial + " a " + aponFinal);
        
        try {
            // 🔥 CORREÇÃO: Agora passando 6 parâmetros conforme a nova assinatura
            int total = apontamentoDAO.contarRegistrosNaoSeladosCompleto(
                dataFormatada, tipoFrontend, 
                anoMesInicial, anoMesFinal,
                aponInicial, aponFinal);
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("success", true);
            resultado.put("total", total);
            resultado.put("data", dataFormatada);
            resultado.put("tipoOperacao", tipoFrontend);
            resultado.put("anoMesInicial", anoMesInicial);
            resultado.put("anoMesFinal", anoMesFinal);
            resultado.put("aponInicial", aponInicial);
            resultado.put("aponFinal", aponFinal);
            resultado.put("mensagem", "Total de registros não selados: " + total);
            
            System.out.println("✅ [SERVICE] Total contado: " + total);
            return resultado;
            
        } catch (DatabaseException e) {
            System.err.println("❌ [SERVICE] Erro ao contar registros: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Verifica se apontamento existe
     */
    public boolean verificarExistencia(String numApo1, String numApo2) throws Exception {
        try {
            return apontamentoDAO.existeApontamento(numApo1, numApo2);
        } catch (DatabaseException e) {
            logger.severe(String.format("Erro ao verificar existência do apontamento %s/%s: %s",
                numApo1, numApo2, e.getMessage()));
            throw new Exception("Erro ao verificar existência do apontamento", e);
        }
    }
    
    /**
     * Obtém selos disponíveis por tipo
     */
    public Map<String, Integer> contarSelosDisponiveis() throws Exception {
        // Este método pertence a SeloService, mas mantido para compatibilidade
        try {
            return new HashMap<>(); // Retorno vazio por enquanto
        } catch (Exception e) {
            logger.severe("Erro ao contar selos disponíveis: " + e.getMessage());
            throw new Exception("Erro ao contar selos disponíveis", e);
        }
    }
    
    /**
     * Gera estatísticas básicas dos apontamentos (CORRIGIDO)
     */
    public Map<String, Object> gerarEstatisticasBasicas(String dataFormatada, String tipoFrontend) throws Exception {
        Map<String, Object> estatisticas = new HashMap<>();
        
        try {
            // Validar tipo
            if (!TipoOperacaoMapeamento.isLabelValido(tipoFrontend)) {
                throw new IllegalArgumentException("Tipo de operação inválido: " + tipoFrontend);
            }
            
            // Buscar apontamentos
            List<Apontamento> apontamentos = buscarPorData(dataFormatada, tipoFrontend);
            
            // Calcular quantidade
            estatisticas.put("quantidade", apontamentos.size());
            
            // Calcular quantidade não selados
            int naoSelados = 0;
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (Apontamento ap : apontamentos) {
                if (!ap.isSelado()) {
                    naoSelados++;
                }
                if (ap.getValor() != null) {
                    valorTotal = valorTotal.add(ap.getValor());
                }
            }
            estatisticas.put("naoSelados", naoSelados);
            estatisticas.put("selados", apontamentos.size() - naoSelados);
            estatisticas.put("valorTotal", valorTotal);
            
            // Calcular média
            BigDecimal mediaValor = apontamentos.isEmpty() ? BigDecimal.ZERO : 
                valorTotal.divide(new BigDecimal(apontamentos.size()), 2, java.math.RoundingMode.HALF_UP);
            estatisticas.put("mediaValor", mediaValor);
            
            // Informações do tipo
            TipoOperacaoMapeamento.MapeamentoTipo mapeamento = getMapeamentoTipo(tipoFrontend);
            if (mapeamento != null) {
                estatisticas.put("tipoCodigo", mapeamento.getCodigo());
                estatisticas.put("tipoColuna", mapeamento.getColunaData());
            }
            
            logger.info(String.format("Estatísticas geradas: Data=%s, Tipo=%s, Total=%d, Não Selados=%d",
                dataFormatada, tipoFrontend, apontamentos.size(), naoSelados));
            
        } catch (DatabaseException e) {
            logger.severe("Erro ao gerar estatísticas: " + e.getMessage());
            throw new Exception("Erro ao gerar estatísticas", e);
        }
        
        return estatisticas;
    }
    
    /**
     * Método mantido para compatibilidade
     */
    public Map<String, Object> gerarEstatisticasBasicas(Date data, TipoOperacao tipoOperacao) throws Exception {
        try {
            // Converter Date para String
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = sdf.format(data);
            
            // Converter TipoOperacao para String
            String tipoFrontend = tipoOperacao.getDescricao();
            
            // Chamar o novo método
            return gerarEstatisticasBasicas(dataFormatada, tipoFrontend);
        } catch (Exception e) {
            logger.severe("Erro ao gerar estatísticas: " + e.getMessage());
            throw new Exception("Erro ao gerar estatísticas", e);
        }
    }
    
    /**
     * Valida apontamento para selagem
     */
    public boolean validarParaSelagem(Apontamento apontamento) {
        if (apontamento == null) {
            return false;
        }
        
        // Verificar campos obrigatórios
        if (apontamento.getNumApo1() == null || apontamento.getNumApo1().trim().isEmpty()) {
            return false;
        }
        
        if (apontamento.getNumApo2() == null || apontamento.getNumApo2().trim().isEmpty()) {
            return false;
        }
        
        // Verificar se já está selado
        if (apontamento.isSelado()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Processa lote de apontamentos para selagem
     */
    public Map<String, Object> processarLoteParaSelagem(List<Apontamento> apontamentos) {
        Map<String, Object> resultado = new HashMap<>();
        List<Apontamento> aptos = new ArrayList<>();
        List<Apontamento> inaptos = new ArrayList<>();
        List<String> erros = new ArrayList<>();
        
        if (apontamentos == null || apontamentos.isEmpty()) {
            resultado.put("sucesso", false);
            resultado.put("mensagem", "Lista de apontamentos vazia");
            return resultado;
        }
        
        for (Apontamento ap : apontamentos) {
            if (validarParaSelagem(ap)) {
                aptos.add(ap);
            } else {
                inaptos.add(ap);
                erros.add(String.format("Apontamento %s/%s não está apto para selagem", 
                    ap.getNumApo1(), ap.getNumApo2()));
            }
        }
        
        resultado.put("sucesso", true);
        resultado.put("totalProcessados", apontamentos.size());
        resultado.put("aptos", aptos);
        resultado.put("inaptos", inaptos);
        resultado.put("erros", erros);
        
        return resultado;
    }
    
    /**
     * Método auxiliar para obter mapeamento do tipo
     */
    public TipoOperacaoMapeamento.MapeamentoTipo getMapeamentoTipo(String tipoFrontend) {
        return TipoOperacaoMapeamento.getPorLabel(tipoFrontend);
    }
    
    /**
     * Método auxiliar para validar tipo
     */
    public boolean isTipoValido(String tipoFrontend) {
        return TipoOperacaoMapeamento.isLabelValido(tipoFrontend);
    }
    
    /**
     * Método auxiliar para listar todos os tipos disponíveis
     */
    public String[] getTodosTiposDisponiveis() {
        return TipoOperacaoMapeamento.getTodosLabels();
    }
    
    /**
     * Testa o mapeamento (para debug)
     */
    public void testarMapeamento() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🧪 [SERVICE] TESTANDO MAPEAMENTO");
        System.out.println("═══════════════════════════════════════════════════");
        
        String[] todosTipos = getTodosTiposDisponiveis();
        System.out.println("Total de tipos mapeados: " + todosTipos.length);
        
        for (String tipo : todosTipos) {
            TipoOperacaoMapeamento.MapeamentoTipo mapeamento = getMapeamentoTipo(tipo);
            if (mapeamento != null) {
                System.out.println(String.format("  %-30s → %-20s (%s)", 
                    tipo,
                    mapeamento.getColunaData(),
                    mapeamento.isUsaFormatoDDMMYYYYSemZero() ? "Sem zero" : "Com zero"
                ));
            }
        }
        
        System.out.println("═══════════════════════════════════════════════════");
    }
}