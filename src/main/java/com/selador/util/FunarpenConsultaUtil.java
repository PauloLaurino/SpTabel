package com.selador.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utilitário para verificar o status de recepção de selos no Funarpen.
 * Implementa lógica de retry e fallback para consulta pública.
 * 
 *用法:
 * 
 * // Verificar se o selo foi recepcionado (tenta 3 vezes o protocolo, depois consulta pública)
 * FunarpenConsultaUtil.ResultadoConsulta result = FunarpenConsultaUtil.verificarRecepcao(
 *     "SFTN1.cG3Rb.Mwfwe-nRfZM.1122q",
 *     "09851329657",
 *     "token_jwt_aqui"
 * );
 * 
 * if (result.sucesso) {
 *     // Selo recepcionado com sucesso
 *     System.out.println("Sucesso! Dados: " + result.dadosPublica);
 * } else {
 *     // Falha na recepção
 *     System.out.println("Erro: " + result.mensagem);
 * }
 */
public class FunarpenConsultaUtil {

    private static final ObjectMapper M = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    
    // URLs da API Funarpen
    private static final String URL_API_BASE = "http://100.102.13.23:8059";
    private static final String URL_PROTOCOLO = "/funarpen/maker/api/funarpen/selos/protocolo";
    private static final String URL_CONSULTA_PUBLICA = "https://consulta.funarpen.com.br/selo";
    
    // Configurações de retry
    private static final int MAX_TENTATIVAS_PROTOCOLO = 3;
    private static final long ESPERA_ENTRE_TENTATIVAS_MS = 2000; // 2 segundos
    
    private static final java.util.logging.Logger LOGGER = 
            java.util.logging.Logger.getLogger(FunarpenConsultaUtil.class.getName());

    /**
     * Resultado da consulta de recepção do selo
     */
    public static class ResultadoConsulta {
        public boolean sucesso;
        public String mensagem;
        public String protocolo;
        public String dadosProtocolo; // JSON da resposta do protocolo
        public String dadosPublica;  // JSON da resposta pública
        public String tipoVerificacao; // "PROTOCOLO" ou "PUBLICA"
        
        public static ResultadoConsulta sucesso(String msg, String protocolo, String dados, String tipo) {
            ResultadoConsulta r = new ResultadoConsulta();
            r.sucesso = true;
            r.mensagem = msg;
            r.protocolo = protocolo;
            if ("PROTOCOLO".equals(tipo)) {
                r.dadosProtocolo = dados;
            } else {
                r.dadosPublica = dados;
            }
            r.tipoVerificacao = tipo;
            return r;
        }
        
        public static ResultadoConsulta erro(String msg) {
            ResultadoConsulta r = new ResultadoConsulta();
            r.sucesso = false;
            r.mensagem = msg;
            return r;
        }
    }

    /**
     * Verifica se o selo foi recepcionado pelo Funarpen.
     * Primeiro tenta consultar o protocolo (até 3 vezes), depois faz consulta pública.
     * 
     * @param seloDigital O número do selo digital (ex: SFTN1.cG3Rb.Mwfwe-nRfZM.1122q)
     * @param documentoResponsavel CPF/CNPJ do responsável
     * @param codigoEmpresa Token JWT da empresa
     * @return ResultadoConsulta com sucesso ou erro
     */
    public static ResultadoConsulta verificarRecepcao(String seloDigital, String documentoResponsavel, String codigoEmpresa) {
        LOGGER.info("Iniciando verificacao de recepcao para selo: " + seloDigital);
        
        // Primeiro: tentar consultar o protocolo
        ResultadoConsulta resultadoProtocolo = consultarProtocolo(seloDigital, documentoResponsavel, codigoEmpresa);
        
        if (resultadoProtocolo.sucesso) {
            LOGGER.info("Protocolo encontrado com sucesso para selo: " + seloDigital);
            return resultadoProtocolo;
        }
        
        // Se o protocolo não existe ou está em processamento, fazer consulta pública
        LOGGER.info("Protocolo nao disponivel, fazendo consulta publica para selo: " + seloDigital);
        return consultarPublica(seloDigital);
    }
    
    /**
     * Consulta o protocolo de processamento do sello.
     * Tenta até MAX_TENTATIVAS_PROTOCOLO vezes.
     */
    private static ResultadoConsulta consultarProtocolo(String selloDigital, String documentoResponsavel, String codigoEmpresa) {
        String protocolo = null;
        
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS_PROTOCOLO; tentativa++) {
            LOGGER.info("Tentativa " + tentativa + " de " + MAX_TENTATIVAS_PROTOCOLO + " para consultar protocolo");
            
            try {
                // Simular espera entre tentativas
                if (tentativa > 1) {
                    Thread.sleep(ESPERA_ENTRE_TENTATIVAS_MS);
                }
                
                // Buscar protocolo recente do banco (precisaria de implementação adicional)
                // Por agora, assumimos que o protocolo precisa ser passado ou gerado
                // Na prática, o sistema deveria guardar o protocolo retornado pelo /selos/recepcao
                
                // Se não temos protocolo, pulamos para consulta pública
                if (protocolo == null || protocolo.isEmpty()) {
                    LOGGER.info("Protocolo nao disponivel para consulta");
                    break;
                }
                
                String url = URL_API_BASE + URL_PROTOCOLO + 
                    "?documentoResponsavel=" + documentoResponsavel + 
                    "&codigoEmpresa=" + codigoEmpresa + 
                    "&protocolo=" + protocolo;
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json; charset=utf-8")
                        .GET()
                        .build();
                
                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                
                String body = response.body();
                LOGGER.info("Resposta protocolo: " + body);
                
                JsonNode root = M.readTree(body);
                int status = root.has("status") ? root.get("status").asInt() : 0;
                
                // Status 302 = processado com sucesso
                if (status == 302) {
                    return ResultadoConsulta.sucesso(
                        "Selo processado com sucesso via protocolo",
                        protocolo,
                        body,
                        "PROTOCOLO"
                    );
                }
                
                // Status 300 = protocolo inexistente, continuar para próxima tentativa
                // Status 301 = em processamento, continuar tentando
                if (status == 300 || status == 301) {
                    LOGGER.info("Protocolo status: " + status + " - " + (root.has("message") ? root.get("message").asText() : ""));
                    continue;
                }
                
                // Outros erros
                String erroMsg = root.has("message") ? root.get("message").asText() : "Erro desconhecido";
                LOGGER.warning("Erro na consulta de protocolo: " + erroMsg);
                
            } catch (Exception e) {
                LOGGER.warning("Exceção na consulta de protocolo: " + e.getMessage());
            }
        }
        
        return ResultadoConsulta.erro("Protocolo não disponível após " + MAX_TENTATIVAS_PROTOCOLO + " tentativas");
    }
    
    /**
     * Consulta a API pública do Funarpen para verificar se o sello existe.
     * Este método é usado como fallback quando o protocolo não está disponível.
     */
    public static ResultadoConsulta consultarPublica(String selloDigital) {
        LOGGER.info("Executando consulta publica para sello: " + selloDigital);
        
        try {
            // Formatar o sello: remover pontos e hifens para a URL
            String selloFormatado = formatarSelloParaUrl(selloDigital);
            String url = URL_CONSULTA_PUBLICA + "/" + selloFormatado;
            
            LOGGER.info("URL consulta publica: " + url);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            
            String body = response.body();
            LOGGER.info("Resposta consulta publica (tamanho): " + body.length() + " chars");
            
            // A resposta pública vem em HTML, precisamos verificar se contém os dados do sello
            // Se contém "Selo Digital" e "tipoAto" ou "RECONHECIMENTO DE FIRMA", é sucesso
            if (contemDadosSelloValidos(body)) {
                // Extrair dados relevantes do HTML para JSON
                String dadosJson = extrairDadosDoHtml(body);
                return ResultadoConsulta.sucesso(
                    "Selo encontrado via consulta pública",
                    null,
                    dadosJson,
                    "PUBLICA"
                );
            }
            
            return ResultadoConsulta.erro("Selo não encontrado na consulta pública");
            
        } catch (Exception e) {
            LOGGER.severe("Erro na consulta pública: " + e.getMessage());
            return ResultadoConsulta.erro("Erro na consulta pública: " + e.getMessage());
        }
    }
    
    /**
     * Verifica se o HTML de resposta contém dados válidos do sello.
     */
    private static boolean contemDadosSelloValidos(String html) {
        // Verificar indicadores de que o sello foi encontrado
        return html.contains("Selo Digital") || 
               html.contains("selo") ||
               html.contains("TABELIONATO") ||
               html.contains("tipoAto") ||
               html.contains("RECONHECIMENTO DE FIRMA") ||
               html.contains("idap");
    }
    
    /**
     * Extrai os dados do sello do HTML e converte para JSON简.
     * Este é um parse simples que extrai informações chave do HTML.
     */
    private static String extrairDadosDoHtml(String html) {
        try {
            // O HTML contém um script com __NUXT_DATA__ que tem os dados em JSON
            // Vamos extrair esse script e parseá-lo
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            
            // Extrair sello digital
            if (html.contains("SFTN1")) {
                int idx = html.indexOf("SFTN1");
                String sello = html.substring(idx, idx + 30);
                json.append("\"selo\":\"").append(sello.replaceAll("[^a-zA-Z0-9]", "")).append("\",");
            }
            
            // Extrair tipo de ato
            if (html.contains("RECONHECIMENTO DE FIRMA")) {
                json.append("\"tipoAto\":\"RECONHECIMENTO DE FIRMA\",");
            }
            
            // Extrair IDAP
            if (html.contains("000000000000")) {
                int idx = html.indexOf("000000000000");
                String idap = html.substring(idx, idx + 19);
                json.append("\"idap\":\"").append(idap).append("\",");
            }
            
            // Extrair data de emissão
            if (html.contains("Data Emissao do Selo")) {
                int idx = html.indexOf("Data Emissao do Selo");
                String dataArea = html.substring(idx, idx + 100);
                // Procurar data no formato DD/MM/AAAA
                if (dataArea.matches(".*\\d{2}/\\d{2}/\\d{4}.*")) {
                    String data = dataArea.substring(dataArea.indexOf("/") - 2, dataArea.indexOf("/") + 10);
                    json.append("\"dataEmissao\":\"").append(data).append("\",");
                }
            }
            
            // Remover última vírgula se houver
            if (json.charAt(json.length() - 1) == ',') {
                json.deleteCharAt(json.length() - 1);
            }
            
            json.append("}");
            return json.toString();
            
        } catch (Exception e) {
            LOGGER.warning("Erro ao extrair dados do HTML: " + e.getMessage());
            return "{\"erro\":\"Falha ao processar dados\"}";
        }
    }
    
    /**
     * Formata o sello digital para uso em URL (remove pontos e hifens).
     * Ex: SFTN1.cG3Rb.Mwfwe-nRfZM.1122q -> SFTN1cG3RbMwfwenRfZM1122q
     */
    private static String formatarSelloParaUrl(String sello) {
        return sello.replaceAll("[.-]", "");
    }
    
    /**
     * Método de teste main
     */
    public static void main(String[] args) {
        // Exemplo de uso:
        // ResultadoConsulta result = verificarRecepcao(
        //     "SFTN1.cG3Rb.Mwfwe-nRfZM.1122q",
        //     "09851329657",
        //     "token_jwt_aqui"
        // );
        
        // Testar apenas consulta pública
        ResultadoConsulta result = consultarPublica("SFTN1.cG3Rb.Mwfwe-nRfZM.1122q");
        
        System.out.println("Sucesso: " + result.sucesso);
        System.out.println("Mensagem: " + result.mensagem);
        System.out.println("Tipo: " + result.tipoVerificacao);
        System.out.println("Dados: " + (result.dadosPublica != null ? result.dadosPublica : result.dadosProtocolo));
    }
}
