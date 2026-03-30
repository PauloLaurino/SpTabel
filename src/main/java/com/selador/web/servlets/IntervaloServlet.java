package com.selador.web.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet para buscar intervalo de apontamentos (primeiro e último)
 * Endpoint: /api/apontamentos/intervalo
 */
public class IntervaloServlet extends BaseServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        long startTime = System.currentTimeMillis();
        
        try {
            String dataParam = request.getParameter("data");
            String codigoOperacao = request.getParameter("codigoOperacao");
            String descricaoOperacao = request.getParameter("descricaoOperacao"); // ← NOVO PARÂMETRO
            
            // Validar parâmetros obrigatórios
            if (dataParam == null || dataParam.trim().isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Parâmetro 'data' é obrigatório");
                return;
            }
            
            if (codigoOperacao == null || codigoOperacao.trim().isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Parâmetro 'codigoOperacao' é obrigatório");
                return;
            }
            
            // Converter data
            Date data = null;
            try {
                data = parseDate(dataParam, "yyyy-MM-dd");
            } catch (ParseException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Formato de data inválido. Use YYYY-MM-DD. Erro: " + e.getMessage());
                return;
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Erro ao converter data: " + e.getMessage());
                return;
            }
            
            // Buscar intervalo COMPLETO (primeiro e último) com descrição
            Map<String, Object> intervaloCompleto = buscarIntervaloCompleto(
                data, codigoOperacao, descricaoOperacao);
            
            // Verificar se houve erro
            if (intervaloCompleto.containsKey("erro")) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Erro ao buscar intervalo: " + intervaloCompleto.get("erro"));
                return;
            }
            
            // Extrair primeiro e último do resultado
            @SuppressWarnings("unchecked")
            Map<String, Object> primeiro = (Map<String, Object>) intervaloCompleto.get("primeiro");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> ultimo = (Map<String, Object>) intervaloCompleto.get("ultimo");
            
            // Preparar resposta
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("primeiro", primeiro);
            resultado.put("ultimo", ultimo);
            resultado.put("data", dataParam);
            resultado.put("codigoOperacao", codigoOperacao);
            resultado.put("descricaoOperacao", descricaoOperacao);
            resultado.put("timestamp", new Date());
            
            // Log da operação
            long duration = System.currentTimeMillis() - startTime;
            logInfo(String.format("✅ Intervalo encontrado: Data=%s, Codigo=%s, Desc=%s, Primeiro=%s/%s, Último=%s/%s, Tempo=%dms",
                dataParam, codigoOperacao, descricaoOperacao,
                primeiro.get("numapo1"), primeiro.get("numapo2"),
                ultimo.get("numapo1"), ultimo.get("numapo2"),
                duration));
            
            sendSuccessResponse(response, resultado);
            
        } catch (Exception e) {
            logError("❌ Erro ao buscar intervalo: " + e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Erro interno ao buscar intervalo: " + e.getMessage());
        }
    }
    
    /**
     * Método para parsear data
     */
    private Date parseDate(String dateString, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        return sdf.parse(dateString);
    }
    
    /**
     * Busca o intervalo COMPLETO (primeiro e último) para a data e código de operação
     * VERSÃO SIMPLIFICADA: Chama diretamente o Service
     */
    private Map<String, Object> buscarIntervaloCompleto(
            Date data, String codigoOperacao, String descricaoOperacao) {
        
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Formatar data para YYYY-MM-DD (formato que o Service espera)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dataFormatada = sdf.format(data);
            
            System.out.println("🔍 [IntervaloServlet] Buscando intervalo COMPLETO");
            System.out.println("   Data: " + dataFormatada);
            System.out.println("   Código frontend: " + codigoOperacao);
            System.out.println("   Descrição: " + descricaoOperacao);
            
            // ⚠️ CORREÇÃO: Usar o ApontamentoService com o CÓDIGO ORIGINAL
            com.selador.service.ApontamentoService service = 
                com.selador.service.ApontamentoService.getInstance();
            
            com.selador.web.dto.IntervaloApontamentoDTO intervalo = 
                service.buscarIntervaloPorDataECodigo(
                    dataFormatada,           // Data no formato YYYY-MM-DD
                    codigoOperacao,          // ⚠️ CÓDIGO ORIGINAL do frontend!
                    descricaoOperacao        // ⚠️ DESCRIÇÃO ORIGINAL do frontend!
                );
            
            if (intervalo == null) {
                System.out.println("⚠️  Nenhum apontamento encontrado no banco");
                resultado.put("primeiro", criarMapApontamentoNaoEncontrado("Primeiro"));
                resultado.put("ultimo", criarMapApontamentoNaoEncontrado("Último"));
            } else {
                System.out.println("✅ DTO recebido do banco:");
                System.out.println("   Primeiro: " + intervalo.getNumApo1Ini() + "/" + intervalo.getNumApo2Ini());
                System.out.println("   Último: " + intervalo.getNumApo1Fim() + "/" + intervalo.getNumApo2Fim());
                
                // Buscar valores REAIS dos apontamentos
                Double valorPrimeiro = buscarValorApontamento(intervalo.getNumApo1Ini(), intervalo.getNumApo2Ini());
                Double valorUltimo = buscarValorApontamento(intervalo.getNumApo1Fim(), intervalo.getNumApo2Fim());
                
                resultado.put("primeiro", criarMapApontamento(
                    intervalo.getNumApo1Ini(), 
                    intervalo.getNumApo2Ini(), 
                    data, codigoOperacao, descricaoOperacao, "Primeiro", "BANCO_REAL", valorPrimeiro
                ));
                resultado.put("ultimo", criarMapApontamento(
                    intervalo.getNumApo1Fim(), 
                    intervalo.getNumApo2Fim(), 
                    data, codigoOperacao, descricaoOperacao, "Último", "BANCO_REAL", valorUltimo
                ));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar intervalo COMPLETO: " + e.getMessage());
            e.printStackTrace();
            resultado.put("erro", e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Busca valor REAL do apontamento no banco
     */
    private Double buscarValorApontamento(String numApo1, String numApo2) {
        if (numApo1 == null || numApo2 == null) {
            return 0.0;
        }
        
        try {
            com.selador.dao.ApontamentoDAO dao = new com.selador.dao.ApontamentoDAO();
            com.selador.model.Apontamento ap = dao.buscarPorNumero(numApo1, numApo2);
            
            if (ap != null && ap.getValor() != null) {
                return ap.getValor().doubleValue();
            }
        } catch (Exception e) {
            System.err.println("⚠️  Não foi possível buscar valor do apontamento " + numApo1 + "/" + numApo2 + ": " + e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Cria mapa para apontamento encontrado
     */
    private Map<String, Object> criarMapApontamento(String numApo1, String numApo2, 
            Date data, String codigoOperacao, String descricaoOperacao, 
            String posicao, String origem, Double valor) {
        
        Map<String, Object> apontamento = new HashMap<>();
        
        boolean encontrado = numApo1 != null && !numApo1.trim().isEmpty();
        
        apontamento.put("encontrado", encontrado);
        apontamento.put("data", data);
        apontamento.put("numapo1", numApo1 != null ? numApo1 : "");
        apontamento.put("numapo2", numApo2 != null ? numApo2 : "");
        apontamento.put("valor", valor != null ? valor : 0.0);
        apontamento.put("tipoOperacao", codigoOperacao);
        apontamento.put("descricaoOperacao", descricaoOperacao);
        apontamento.put("descricao", posicao + " apontamento do intervalo: " + descricaoOperacao);
        apontamento.put("origem", origem);
        apontamento.put("status", "PENDENTE");
        
        if (!encontrado) {
            apontamento.put("status", "NAO_ENCONTRADO");
        }
        
        return apontamento;
    }
    
    /**
     * Cria mapa para apontamento NÃO encontrado
     */
    private Map<String, Object> criarMapApontamentoNaoEncontrado(String posicao) {
        Map<String, Object> apontamento = new HashMap<>();
        
        apontamento.put("encontrado", false);
        apontamento.put("numapo1", null);
        apontamento.put("numapo2", null);
        apontamento.put("valor", 0.0);
        apontamento.put("descricao", posicao + " apontamento não encontrado");
        apontamento.put("status", "NAO_ENCONTRADO");
        
        return apontamento;
    }
    
    /**
     * Log de informação
     */
    private void logInfo(String mensagem) {
        System.out.println("INFO [IntervaloServlet]: " + mensagem);
    }
    
    /**
     * Log de erro
     */
    private void logError(String mensagem, Exception e) {
        System.err.println("ERRO [IntervaloServlet]: " + mensagem);
        if (e != null) {
            e.printStackTrace();
        }
    }
}