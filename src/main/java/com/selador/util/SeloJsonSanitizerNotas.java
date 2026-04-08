/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.node.ArrayNode
 *  com.fasterxml.jackson.databind.node.ObjectNode
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.selador.dao.ConnectionFactory
 */
package com.selador.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.selador.dao.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class SeloJsonSanitizerNotas {
    private static final ObjectMapper M = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FORMAT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger LOGGER = Logger.getLogger(SeloJsonSanitizerNotas.class.getName());
    private static final int[] TIPOS_ATO_VALIDOS = new int[]{401, 406, 407, 416, 417, 418, 421, 424, 428, 429, 430, 433, 436, 437, 439, 440, 443, 445, 446, 447, 449, 450, 452, 453, 454, 455, 456, 457, 458, 459};

    private static String converterV10ParaV11(String json, Map<String, Object> dadosComplementares) {
        try {
            String jsonPrincipal = json;
            String dadosExtras = null;
            
            // Localiza o fechamento do primeiro objeto JSON raiz
            int level = 0;
            int firstOpen = json.indexOf("{");
            if (firstOpen == -1) return json;
            
            int closeBrace = -1;
            boolean inString = false;
            for (int i = firstOpen; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '\"' && (i == 0 || json.charAt(i-1) != '\\')) {
                    inString = !inString;
                } else if (!inString) {
                    if (c == '{') level++;
                    else if (c == '}') {
                        level--;
                        if (level == 0) {
                            closeBrace = i;
                            break;
                        }
                    }
                }
            }

            if (closeBrace != -1) {
                jsonPrincipal = json.substring(0, closeBrace + 1);
                dadosExtras = json.substring(closeBrace + 1).trim();
            }

            // Uso de leitura leniente para suportar JSONs mal formados (Modelo 1 - campo sem aspas)
            JsonNode root = lerJsonLeniente(jsonPrincipal);
            if (root == null || !root.isObject()) return json;
            
            ObjectNode obj = (ObjectNode)root;
            
            // Dados de Parâmetros
            String ambientePar = (String)dadosComplementares.getOrDefault("AMBIENTE_PAR", "prod");
            if ("P".equalsIgnoreCase(ambientePar)) ambientePar = "prod";
            else if ("H".equalsIgnoreCase(ambientePar)) ambientePar = "homolog";
            
            String docPar = (String)dadosComplementares.getOrDefault("DOC_PAR", "0");
            String codigoPar = (String)dadosComplementares.getOrDefault("CODIGO_PAR", "1");
            String codtabelPar = (String)dadosComplementares.getOrDefault("CODTABEL_PAR", "181122");

            ObjectNode seloObj = M.createObjectNode();
            
            // Mapeamento Flat -> V11
            if (obj.has("seloDigital")) seloObj.set("seloDigital", obj.get("seloDigital"));
            
            if (obj.has("codigoPedido")) {
                JsonNode cpNode = obj.get("codigoPedido");
                if (cpNode.isNumber()) {
                    seloObj.put("codigoPedido", (long)cpNode.asDouble());
                } else {
                    String cpStr = cpNode.asText().replaceAll("[^0-9]", "");
                    if (!cpStr.isEmpty()) {
                        try {
                            seloObj.put("codigoPedido", Long.parseLong(cpStr));
                        } catch (Exception e) {}
                    }
                }
            }

            int tipoAto = 0;
            if (obj.has("numeroTipoAto")) {
                tipoAto = obj.get("numeroTipoAto").asInt();
                if (tipoAto == 408) tipoAto = 459;
                seloObj.put("codigoTipoAto", tipoAto);
            }

            seloObj.put("tipoEmissaoAto", obj.has("tipoEmissaoAto") ? obj.get("tipoEmissaoAto").asInt() : 1);
            if (obj.has("idap")) seloObj.set("idap", obj.get("idap"));
            seloObj.put("versao", "11.12");
            
            if (obj.has("dataAto")) {
                String data = obj.get("dataAto").asText();
                if (data.contains("T")) data = data.substring(0, 10);
                seloObj.put("dataAtoPraticado", data);
                seloObj.put("dataSeloEmitido", data);
            }

            seloObj.put("tipoGratuidade", obj.has("tipoGratuidade") ? obj.get("tipoGratuidade").asInt() : 0);
            
            // Verbas
            if (obj.has("ListaVerbas") && obj.get("ListaVerbas").isArray()) {
                seloObj.set("verbas", processarListaVerbasV10((ArrayNode)obj.get("ListaVerbas")));
            }

            // Propriedades (Pool)
            ObjectNode propriedades = processarListaPropriedadesV10(obj);
            
            // Ingestão de Dados Extras (fragmentos concatenados)
            if (dadosExtras != null && !dadosExtras.isEmpty()) {
                ObjectNode propsExtras = parseQualquerFragmentoProps(dadosExtras);
                if (propsExtras != null) mesclarPropriedades(propriedades, propsExtras);
            }
            
            seloObj.set("propriedades", propriedades);
            
            // Montagem Final do Wrapper
            ObjectNode wrapper = M.createObjectNode();
            wrapper.put("ambiente", ambientePar);
            wrapper.put("documentoResponsavel", docPar);
            wrapper.put("codigoEmpresa", codigoPar);
            wrapper.put("codigoOficio", Integer.parseInt(codtabelPar.replaceAll("[^0-9]", "")));
            wrapper.set("selo", seloObj);

            return M.writeValueAsString(wrapper);
            
        } catch (Exception e) {
            LOGGER.severe("Erro ao converter V10 para V11: " + e.getMessage());
            return json;
        }
    }

    private static JsonNode lerJsonLeniente(String json) {
        if (json == null) return null;
        String raw = json.trim();
        
        // Remove prefixos como (String), (string), etc.
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            raw = raw.substring(start, end + 1);
        }
        try {
            // Limpeza radial: substitui placeholders e detritos que quebram o parse
            raw = raw.replace("|codoficio|", "0")
                     .replace("|ambiente|", "prod")
                     .replace("|codigoEmpresa|", "1")
                     .replace("|dataato|", "")
                     .replace("|numapo1|", "0")
                     .replace("|numser|", "0")
                     .replace("...", "")
                     .replace("{ , }", "{}")
                     .replace("{,}", "{}")
                     .replace(", }", "}")
                     .replace(",}", "}");

            // Aspas em qualquer literal que não pareça JSON válido
            String limpo = raw.replaceAll(":\\s*([^\\\"\\d\\-\\{\\[\\stf\\n][^,}\\n]*)([\\s,}\\n])", ": \"$1\"$2");
            
            Gson gson = new GsonBuilder().setLenient().create();
            Object obj = null;
            try {
                obj = gson.fromJson(limpo, Object.class);
            } catch (Exception e) {
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("c:/Desenvolvimento/Seprocom/Notas/tmp/falha_limpo.json"), limpo.getBytes());
                } catch (Exception e_log) {}
                throw e;
            }
            return M.valueToTree(obj);
        } catch (Exception e) {
            try {
                // Tenta Gson puro se a regex falhar
                Gson gson2 = new GsonBuilder().setLenient().create();
                Object obj2 = gson2.fromJson(raw, Object.class);
                return M.valueToTree(obj2);
            } catch (Exception e2) {
                return null;
            }
        }
    }

    /**
     * Tenta extrair pares NomePropriedade/ValorPropriedade de qualquer fragmento de string,
     * mesmo que não seja um JSON perfeito (suporta arrays truncados, objetos soltos, etc).
     */
    private static ObjectNode parseQualquerFragmentoProps(String fragmento) {
        ObjectNode result = M.createObjectNode();
        ArrayNode signatarios = M.createArrayNode();
        HashSet<String> seen = new HashSet<>();
        
        try {
            // Limpeza radical para tornar o fragmento um array JSON válido
            String limpo = fragmento.trim();
            if (limpo.startsWith("}{")) limpo = "{" + limpo.substring(2);
            if (limpo.startsWith("}")) limpo = limpo.substring(1);
            if (limpo.endsWith("]}") || limpo.endsWith("]")) {
                if (!limpo.startsWith("[")) limpo = "[" + limpo;
            } else {
                if (!limpo.startsWith("[")) limpo = "[" + limpo + "]";
            }
            // Garante que objetos entre vírgulas estejam corretos
            limpo = limpo.replace("}{", "},{");
            
            Gson gson = new GsonBuilder().setLenient().create();
            JsonArray arr = gson.fromJson(limpo, JsonArray.class);
            
            String ultNome = null, ultDoc = null;
            
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                com.google.gson.JsonObject item = el.getAsJsonObject();
                
                String nome = item.has("NomePropriedade") ? item.get("NomePropriedade").getAsString() : null;
                String valor = item.has("ValorPropriedade") ? item.get("ValorPropriedade").getAsString() : "";
                
                if (nome == null) continue;
                String n = nome.toLowerCase();
                
                if (n.contains("nome_razao")) ultNome = valor;
                else if (n.contains("cpf") || n.contains("cnpj") || n.contains("emvolvidos") || n.contains("documento")) ultDoc = valor;
                
                // Se temos um par completo, adiciona aos signatários
                if (ultNome != null && !ultNome.isEmpty() && ultDoc != null && !ultDoc.isEmpty()) {
                    String key = ultDoc.replaceAll("[^0-9]", "");
                    if (!seen.contains(key)) {
                        seen.add(key);
                        ObjectNode s = M.createObjectNode();
                        s.put("nomeRazao", ultNome);
                        s.put("documentoTipo", detectarTipoDocumento(ultDoc));
                        s.put("documentoNumero", ultDoc);
                        signatarios.add(s);
                    }
                    ultNome = null; ultDoc = null;
                }
                
                // Também guarda no pool genérico para mapeamento por tipo de ato
                result.put(nome, valor);
            }
            
            if (signatarios.size() > 0) result.set("signatarios", signatarios);
            
        } catch (Exception e) {
            LOGGER.fine("Falha ao parsear fragmento de propriedades: " + e.getMessage());
        }
        return result;
    }

    private static ObjectNode parseListaPropriedadesExtras(String dadosExtras) {
        try {
            String jsonExtras = "[" + dadosExtras.replace(",{", "},{") + "]";
            JsonNode arrayNode = M.readTree(jsonExtras);
            if (arrayNode.isArray()) {
                ObjectNode result = M.createObjectNode();
                ArrayNode arr = (ArrayNode)arrayNode;
                String ultimoNome = null;
                String ultimoDoc = null;
                String ultimoTipo = null;
                HashSet<String> seen = new HashSet<String>();
                ArrayNode signatarios = M.createArrayNode();
                for (int i = 0; i < arr.size(); ++i) {
                    String valorProp;
                    JsonNode item = arr.get(i);
                    if (item == null || !item.isObject()) continue;
                    String nomeProp = item.has("NomePropriedade") && !item.get("NomePropriedade").isNull() ? item.get("NomePropriedade").asText() : null;
                    String string = valorProp = item.has("ValorPropriedade") && !item.get("ValorPropriedade").isNull() ? item.get("ValorPropriedade").asText() : null;
                    if (nomeProp == null && valorProp == null) continue;
                    if (nomeProp != null) {
                        String npLower = nomeProp.toLowerCase();
                        if (npLower.contains("tipo_envolvido")) {
                            ultimoTipo = valorProp;
                        } else if (npLower.contains("nome_razao")) {
                            ultimoNome = valorProp;
                        } else if (npLower.contains("cpf") || npLower.contains("cnpj") || npLower.contains("emvolvidos")) {
                            ultimoDoc = valorProp;
                        }
                    }
                    if (ultimoNome == null || ultimoNome.isEmpty() || ultimoDoc == null || ultimoDoc.isEmpty()) continue;
                    String key = ultimoDoc.replaceAll("[^0-9]", "");
                    if (!seen.contains(key)) {
                        seen.add(key);
                        ObjectNode sign = M.createObjectNode();
                        sign.put("nomeRazao", ultimoNome);
                        sign.put("documentoTipo", SeloJsonSanitizerNotas.detectarTipoDocumento(ultimoDoc));
                        sign.put("documentoNumero", ultimoDoc);
                        signatarios.add((JsonNode)sign);
                    }
                    ultimoNome = null;
                    ultimoDoc = null;
                    ultimoTipo = null;
                }
                result.set("signatarios", (JsonNode)signatarios);
                return result;
            }
        }
        catch (Exception e) {
            LOGGER.warning("Erro ao parsear ListaPropriedadesExtras: " + e.getMessage());
        }
        return null;
    }

    private static void mesclarPropriedades(ObjectNode props, ObjectNode extras) {
        if (extras.has("signatarios") && extras.get("signatarios").isArray()) {
            if (props.has("signatarios") && props.get("signatarios").isArray()) {
                JsonNode item;
                int i;
                ArrayNode existentes = (ArrayNode)props.get("signatarios");
                ArrayNode novos = (ArrayNode)extras.get("signatarios");
                HashSet<String> seen = new HashSet<String>();
                for (i = 0; i < existentes.size(); ++i) {
                    item = existentes.get(i);
                    if (!item.isObject() || !item.has("documentoNumero")) continue;
                    seen.add(item.get("documentoNumero").asText().replaceAll("[^0-9]", ""));
                }
                for (i = 0; i < novos.size(); ++i) {
                    String doc;
                    item = novos.get(i);
                    if (!item.isObject() || !item.has("documentoNumero") || seen.contains(doc = item.get("documentoNumero").asText().replaceAll("[^0-9]", ""))) continue;
                    existentes.add(item);
                    seen.add(doc);
                }
            } else {
                props.set("signatarios", extras.get("signatarios"));
            }
        }
    }

    private static ObjectNode processarListaVerbasV10(ArrayNode listaVerbas) {
        ObjectNode verbas = M.createObjectNode();
        for (int i = 0; i < listaVerbas.size(); ++i) {
            JsonNode valorNode;
            JsonNode item = listaVerbas.get(i);
            if (item == null || !item.isObject()) continue;
            String nomeVerba = item.has("NomeVerba") ? item.get("NomeVerba").asText() : null;
            JsonNode jsonNode = valorNode = item.has("ValorVerba") ? item.get("ValorVerba") : null;
            if (nomeVerba == null || valorNode == null) continue;
            String nomeLower = nomeVerba.toLowerCase();
            double valor = 0.0;
            if (valorNode.isNumber()) {
                valor = valorNode.asDouble();
                String valStr = valorNode.asText();
                if (!valStr.contains(".") && valor >= 10.0) {
                    valor /= 100.0;
                }
            }
            if (nomeLower.contains("emolumentos")) {
                verbas.put("emolumentos", valor);
                continue;
            }
            if (nomeLower.contains("funrejus")) {
                verbas.put("funrejus", valor);
                continue;
            }
            if (nomeLower.contains("iss")) {
                verbas.put("iss", valor);
                continue;
            }
            if (nomeLower.contains("fundep")) {
                verbas.put("fundep", valor);
                continue;
            }
            if (nomeLower.contains("funarpen")) {
                verbas.put("funarpen", valor);
                continue;
            }
            if (nomeLower.contains("distribuidor")) {
                verbas.put("distribuidor", valor);
                continue;
            }
            if (nomeLower.contains("vrcext")) {
                verbas.put("vrcExt", valor);
                continue;
            }
            if (!nomeLower.contains("adicional")) continue;
            verbas.put("valorAdicional", valor);
        }
        return verbas;
    }

    private static ObjectNode processarListaPropriedadesV10(ObjectNode obj) {
        ObjectNode propriedades = M.createObjectNode();
        int tipoAto = 0;
        if (obj.has("numeroTipoAto") && (tipoAto = obj.get("numeroTipoAto").asInt()) == 408) {
            tipoAto = 459;
        }
        if (!obj.has("ListaPropriedades") || !obj.get("ListaPropriedades").isArray()) {
            return propriedades;
        }
        ArrayNode listaProp = (ArrayNode)obj.get("ListaPropriedades");
        if (tipoAto == 455 || tipoAto == 401 || tipoAto == 402 || tipoAto == 403 || tipoAto == 404) {
            String nomeRazao = null;
            String cpfCnpj = null;
            String dataRegistro = null;
            for (int i = 0; i < listaProp.size(); ++i) {
                String valorProp;
                JsonNode item = listaProp.get(i);
                if (item == null || !item.isObject()) continue;
                String nomeProp = item.has("NomePropriedade") ? item.get("NomePropriedade").asText(null) : null;
                String string = valorProp = item.has("ValorPropriedade") && !item.get("ValorPropriedade").isNull() ? item.get("ValorPropriedade").asText(null) : null;
                if (nomeProp == null || valorProp == null) continue;
                String npLower = nomeProp.toLowerCase();
                if (npLower.contains("nome_razao")) {
                    nomeRazao = valorProp;
                    continue;
                }
                if (npLower.contains("cpf") || npLower.contains("cnpj")) {
                    cpfCnpj = valorProp;
                    continue;
                }
                if (!npLower.contains("data_registro")) continue;
                dataRegistro = valorProp;
            }
            if (nomeRazao != null && !nomeRazao.isEmpty()) {
                ObjectNode solicitante = M.createObjectNode();
                solicitante.put("nomeRazao", nomeRazao);
                solicitante.put("numeroDocumento", cpfCnpj != null ? cpfCnpj : "");
                solicitante.put("tipoDocumento", SeloJsonSanitizerNotas.detectarTipoDocumento(cpfCnpj != null ? cpfCnpj : ""));
                propriedades.set("solicitanteAto", (JsonNode)solicitante);
                ObjectNode envolvido = M.createObjectNode();
                envolvido.put("nomeRazao", nomeRazao);
                envolvido.put("documentoNumero", cpfCnpj != null ? cpfCnpj : "");
                propriedades.set("envolvidos", (JsonNode)M.createArrayNode().add((JsonNode)envolvido));
            }
            ObjectNode reconhecimento = M.createObjectNode();
            reconhecimento.put("especie", 1);
            reconhecimento.put("quantidadePaginasAto", (String)null);
            reconhecimento.put("quantidadePartesEnvolvidasAto", 0);
            reconhecimento.put("data", dataRegistro != null ? dataRegistro : "");
            reconhecimento.put("descricao", (String)null);
            propriedades.set("reconhecimento", (JsonNode)reconhecimento);
            propriedades.put("tipo", 2);
        } else if (tipoAto == 459 || tipoAto == 408 || tipoAto == 409) {
            String dataAto;
            ArrayNode outorgantes = M.createArrayNode();
            String ultimoNome = null;
            String ultimoDoc = null;
            HashSet<String> seen = new HashSet<String>();
            for (int i = 0; i < listaProp.size(); ++i) {
                String valorProp;
                JsonNode item = listaProp.get(i);
                if (item == null || !item.isObject()) continue;
                String nomeProp = item.has("NomePropriedade") ? item.get("NomePropriedade").asText(null) : null;
                String string = valorProp = item.has("ValorPropriedade") && !item.get("ValorPropriedade").isNull() ? item.get("ValorPropriedade").asText(null) : null;
                if (nomeProp == null || valorProp == null) continue;
                String npLower = nomeProp.toLowerCase();
                if (npLower.contains("nome_razao")) {
                    ultimoNome = valorProp;
                } else if (npLower.contains("cpf") || npLower.contains("cnpj") || npLower.contains("emvolvidos")) {
                    ultimoDoc = valorProp;
                }
                if (ultimoNome == null || ultimoNome.isEmpty() || ultimoDoc == null || ultimoDoc.isEmpty()) continue;
                String key = ultimoDoc.replaceAll("[^0-9]", "");
                if (!seen.contains(key)) {
                    seen.add(key);
                    ObjectNode pn = M.createObjectNode();
                    pn.put("nomeRazao", ultimoNome);
                    pn.put("numeroDocumento", ultimoDoc);
                    pn.put("documentoTipo", SeloJsonSanitizerNotas.detectarTipoDocumento(ultimoDoc));
                    outorgantes.add((JsonNode)pn);
                }
                ultimoNome = null;
                ultimoDoc = null;
            }
            propriedades.set("outorgantes", (JsonNode)outorgantes);
            propriedades.set("outorgados", (JsonNode)M.createArrayNode());
            String idap = obj.has("idap") ? obj.get("idap").asText() : "";
            ObjectNode escritura = M.createObjectNode();
            if (idap.length() >= 24) {
                String folha;
                String parte = idap.substring(12, 20);
                String livro = parte.substring(0, 4).replaceFirst("^0+(?=\\d)", "");
                if (livro.isEmpty()) {
                    livro = "0";
                }
                if ((folha = (parte = idap.substring(20, 24)).substring(0, 4).replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    folha = "0";
                }
                escritura.put("livro", livro);
                escritura.put("folha", folha);
            } else {
                escritura.put("livro", "0");
                escritura.put("folha", "0");
            }
            escritura.put("termo", "1");
            String string = dataAto = obj.has("dataAto") ? obj.get("dataAto").asText() : "";
            if (dataAto.contains("T")) {
                dataAto = dataAto.substring(0, 10);
            }
            escritura.put("data", dataAto);
            propriedades.set("escritura", (JsonNode)escritura);
            propriedades.set("bens", (JsonNode)M.createArrayNode());
        } else if (tipoAto == 430 || tipoAto == 433) {
            String idap = obj.has("idap") ? obj.get("idap").asText() : "";
            ObjectNode traslado = M.createObjectNode();
            if (idap.length() >= 24) {
                String folha;
                String parte = idap.substring(12, 20);
                String livro = parte.substring(0, 4).replaceFirst("^0+(?=\\d)", "");
                if (livro.isEmpty()) {
                    livro = "0";
                }
                if ((folha = (parte = idap.substring(20, 24)).substring(0, 4).replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    folha = "0";
                }
                traslado.put("livro", livro);
                traslado.put("folha", folha);
            } else {
                traslado.put("livro", "0");
                traslado.put("folha", "0");
            }
            traslado.put("termo", (String)null);
            propriedades.set("traslado", (JsonNode)traslado);
            ArrayNode signatarios = M.createArrayNode();
            String ultimoNome = null;
            String ultimoDoc = null;
            HashSet<String> seen = new HashSet<String>();
            for (int i = 0; i < listaProp.size(); ++i) {
                String npLower;
                String valorProp;
                JsonNode item = listaProp.get(i);
                if (item == null || !item.isObject()) continue;
                String nomeProp = item.has("NomePropriedade") ? item.get("NomePropriedade").asText(null) : null;
                String string = valorProp = item.has("ValorPropriedade") && !item.get("ValorPropriedade").isNull() ? item.get("ValorPropriedade").asText(null) : null;
                if (nomeProp == null || valorProp == null || !(npLower = nomeProp.toLowerCase()).contains("envolvidos") && !npLower.contains("emvolvidos")) continue;
                if (npLower.contains("nome") || npLower.contains("razao") || npLower.contains("nome_razao")) {
                    ultimoNome = valorProp;
                } else if (npLower.contains("cpf") || npLower.contains("cnpj") || npLower.contains("cpf_cnpj")) {
                    ultimoDoc = valorProp;
                }
                if (ultimoNome == null || ultimoNome.isEmpty() || ultimoDoc == null || ultimoDoc.isEmpty()) continue;
                String key = ultimoDoc.replaceAll("[^0-9]", "");
                if (!seen.contains(key)) {
                    seen.add(key);
                    ObjectNode sign = M.createObjectNode();
                    sign.put("nomeRazao", ultimoNome);
                    sign.put("documentoTipo", SeloJsonSanitizerNotas.detectarTipoDocumento(ultimoDoc));
                    sign.put("documentoNumero", ultimoDoc);
                    signatarios.add((JsonNode)sign);
                }
                ultimoNome = null;
                ultimoDoc = null;
            }
            propriedades.set("signatarios", (JsonNode)signatarios);
        } else if (tipoAto == 432 || tipoAto == 454) {
            String idap = obj.has("idap") ? obj.get("idap").asText() : "";
            ObjectNode certidao = M.createObjectNode();
            if (idap.length() >= 24) {
                String folha;
                String parte = idap.substring(12, 20);
                String livro = parte.substring(0, 4).replaceFirst("^0+(?=\\d)", "");
                if (livro.isEmpty()) {
                    livro = "0";
                }
                if ((folha = (parte = idap.substring(20, 24)).substring(0, 4).replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    folha = "0";
                }
                certidao.put("livro", livro);
                certidao.put("folha", folha);
            } else {
                certidao.put("livro", "0");
                certidao.put("folha", "0");
            }
            certidao.put("tipo", tipoAto);
            propriedades.set("certidao", (JsonNode)certidao);
            ArrayNode signatarios = M.createArrayNode();
            String ultimoNome = null;
            String ultimoDoc = null;
            HashSet<String> seen = new HashSet<String>();
            for (int i = 0; i < listaProp.size(); ++i) {
                String npLower;
                String valorProp;
                JsonNode item = listaProp.get(i);
                if (item == null || !item.isObject()) continue;
                String nomeProp = item.has("NomePropriedade") ? item.get("NomePropriedade").asText(null) : null;
                String string = valorProp = item.has("ValorPropriedade") && !item.get("ValorPropriedade").isNull() ? item.get("ValorPropriedade").asText(null) : null;
                if (nomeProp == null || valorProp == null || !(npLower = nomeProp.toLowerCase()).contains("envolvidos") && !npLower.contains("emvolvidos")) continue;
                if (npLower.contains("nome") || npLower.contains("razao") || npLower.contains("nome_razao")) {
                    ultimoNome = valorProp;
                } else if (npLower.contains("cpf") || npLower.contains("cnpj") || npLower.contains("cpf_cnpj")) {
                    ultimoDoc = valorProp;
                }
                if (ultimoNome == null || ultimoNome.isEmpty() || ultimoDoc == null || ultimoDoc.isEmpty()) continue;
                String key = ultimoDoc.replaceAll("[^0-9]", "");
                if (!seen.contains(key)) {
                    seen.add(key);
                    ObjectNode sign = M.createObjectNode();
                    sign.put("nomeRazao", ultimoNome);
                    sign.put("documentoTipo", SeloJsonSanitizerNotas.detectarTipoDocumento(ultimoDoc));
                    sign.put("documentoNumero", ultimoDoc);
                    signatarios.add((JsonNode)sign);
                }
                ultimoNome = null;
                ultimoDoc = null;
            }
            propriedades.set("signatarios", (JsonNode)signatarios);
        }
        return propriedades;
    }

    private static int detectarTipoDocumento(String numero) {
        if (numero == null || numero.isEmpty()) {
            return 12;
        }
        String num = numero.replaceAll("[^0-9]", "");
        if (num.length() == 11) {
            return 1;
        }
        if (num.length() == 14) {
            return 2;
        }
        return 12;
    }

    private static ArrayNode extrairSignatarios(ObjectNode seloObj) {
        ArrayNode signatarios = M.createArrayNode();
        HashSet<String> seen = new HashSet<>();
        
        // --- FONTE 1: POOL UNIFICADO (MAIS CONFIÁVEL) ---
        if (seloObj.has("_pool_propriedades")) {
            ObjectNode pool = (ObjectNode) seloObj.get("_pool_propriedades");
            if (pool.has("signatarios") && pool.get("signatarios").isArray()) {
                for (JsonNode s : pool.get("signatarios")) {
                    if (s.isObject() && s.has("documentoNumero")) {
                        String doc = s.get("documentoNumero").asText().replaceAll("[^0-9]", "");
                        if (!doc.isEmpty() && !seen.contains(doc)) {
                            seen.add(doc);
                            signatarios.add(s);
                        }
                    }
                }
            }
        }
        
        // --- FONTE 2: PROPRIEDADES LEGADAS NO JSON ---
        if (seloObj.has("propriedades") && seloObj.get("propriedades").isObject()) {
            ObjectNode props = (ObjectNode)seloObj.get("propriedades");
            if (props.has("signatarios") && props.get("signatarios").isArray()) {
                for (JsonNode s : props.get("signatarios")) {
                    if (s.isObject() && s.has("documentoNumero")) {
                        String doc = s.get("documentoNumero").asText().replaceAll("[^0-9]", "");
                        if (!doc.isEmpty() && !seen.contains(doc)) {
                            seen.add(doc);
                            signatarios.add(s);
                        }
                    }
                }
            }
            // Envolvidos flat (Modelo 2)
            if (props.has("envolvidos") && props.get("envolvidos").isObject()) {
                ObjectNode env = (ObjectNode)props.get("envolvidos");
                String nome = env.has("nome_razao") ? env.get("nome_razao").asText() : (env.has("nome") ? env.get("nome").asText() : null);
                String docX = env.has("CPF_CNPJ") ? env.get("CPF_CNPJ").asText() : (env.has("documento") ? env.get("documento").asText() : null);
                if (nome != null && docX != null) {
                    String normDoc = docX.replaceAll("[^0-9]", "");
                    if (!normDoc.isEmpty() && !seen.contains(normDoc)) {
                        seen.add(normDoc);
                        ObjectNode s = M.createObjectNode();
                        s.put("nomeRazao", nome);
                        s.put("documentoTipo", detectarTipoDocumento(docX));
                        s.put("documentoNumero", docX);
                        signatarios.add(s);
                    }
                }
            }
        }
        
        // --- FONTE 3: LISTAS V10 ---
        if (seloObj.has("ListaPropriedades") && seloObj.get("ListaPropriedades").isArray()) {
            ArrayNode v10p = processarListaPropriedadesParaSignatarios((ArrayNode)seloObj.get("ListaPropriedades"), seen);
            for (JsonNode s : v10p) signatarios.add(s);
        }
        
        return signatarios;
    }

    private static ArrayNode processarListaPropriedadesExtras(JsonNode listaExtras) {
        ArrayNode signatarios = M.createArrayNode();
        HashSet<String> seen = new HashSet<String>();
        String ultimoNome = null;
        String ultimoTipoEnvolvido = null;
        String ultimoCpfCnpj = null;
        ArrayList<String[]> pares = new ArrayList<String[]>();
        if (listaExtras.isArray()) {
            ArrayNode arr = (ArrayNode)listaExtras;
            for (int i = 0; i < arr.size(); ++i) {
                JsonNode item = arr.get(i);
                if (!item.isObject() || !item.has("NomePropriedade") || !item.has("ValorPropriedade")) continue;
                String nome = item.get("NomePropriedade").asText();
                String valor = item.get("ValorPropriedade").isNull() ? "" : item.get("ValorPropriedade").asText();
                pares.add(new String[]{nome, valor});
            }
        } else if (listaExtras.isObject()) {
            ObjectNode obj = (ObjectNode)listaExtras;
            Iterator fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry entry = (Map.Entry)fields.next();
                String key = (String)entry.getKey();
                if (!key.startsWith("NomePropriedade")) continue;
                String idx = key.substring(15);
                String valorKey = "ValorPropriedade" + idx;
                JsonNode valorNode = obj.get(valorKey);
                String valor = valorNode != null && !valorNode.isNull() ? valorNode.asText() : "";
                pares.add(new String[]{((JsonNode)entry.getValue()).asText(), valor});
            }
        }
        for (String[] par : pares) {
            String nomeProp = par[0].toLowerCase();
            String valorProp = par[1];
            if (nomeProp.contains("tipo_envolvido")) {
                ultimoTipoEnvolvido = valorProp;
            } else if (nomeProp.contains("nome_razao") || nomeProp.contains("razao")) {
                ultimoNome = valorProp;
            } else if (nomeProp.contains("cpf_cnpj") || nomeProp.contains("cpf") || nomeProp.contains("cnpj")) {
                ultimoCpfCnpj = valorProp;
            }
            if (ultimoNome == null || ultimoNome.isEmpty() || ultimoCpfCnpj == null || ultimoCpfCnpj.isEmpty()) continue;
            String key = ultimoCpfCnpj.replaceAll("[^0-9]", "");
            if (!seen.contains(key)) {
                seen.add(key);
                ObjectNode sign = M.createObjectNode();
                sign.put("nomeRazao", ultimoNome);
                sign.put("documentoTipo", SeloJsonSanitizerNotas.detectarTipoDocumento(ultimoCpfCnpj));
                sign.put("documentoNumero", ultimoCpfCnpj);
                signatarios.add((JsonNode)sign);
            }
            ultimoNome = null;
            ultimoCpfCnpj = null;
            ultimoTipoEnvolvido = null;
        }
        return signatarios;
    }

    private static ArrayNode processarListaPropriedadesParaSignatarios(ArrayNode listaProp, Set<String> seen) {
        ArrayNode signatarios = M.createArrayNode();
        String ultimoNome = null;
        String ultimoNum = null;
        for (int i = 0; i < listaProp.size(); ++i) {
            String npLower;
            String valorProp;
            JsonNode item = listaProp.get(i);
            if (item == null || !item.isObject()) continue;
            ObjectNode it = (ObjectNode)item;
            String nomeProp = it.has("NomePropriedade") ? it.get("NomePropriedade").asText(null) : null;
            String string = valorProp = it.has("ValorPropriedade") && !it.get("ValorPropriedade").isNull() ? it.get("ValorPropriedade").asText(null) : null;
            if (nomeProp == null || valorProp == null || !(npLower = nomeProp.toLowerCase()).contains("envolvid") && !npLower.contains("envolv") && !npLower.contains("emvolv")) continue;
            if (npLower.contains("nome") || npLower.contains("razao") || npLower.contains("nome_razao")) {
                ultimoNome = valorProp;
            } else if (npLower.contains("cpf") || npLower.contains("cnpj") || npLower.contains("cpf_cnpj") || npLower.contains("documento")) {
                ultimoNum = valorProp;
            }
            if (ultimoNome == null || ultimoNum == null) continue;
            String key = ultimoNum.replaceAll("[^0-9]", "");
            if (!seen.contains(key)) {
                seen.add(key);
                ObjectNode sign = M.createObjectNode();
                sign.put("nomeRazao", ultimoNome);
                sign.put("documentoTipo", SeloJsonSanitizerNotas.detectarTipoDocumento(ultimoNum));
                sign.put("documentoNumero", ultimoNum);
                signatarios.add((JsonNode)sign);
            }
            ultimoNome = null;
            ultimoNum = null;
        }
        return signatarios;
    }

    public static boolean sanitizarESalvar(String seloDigital) {
        return SeloJsonSanitizerNotas.sanitizarESalvar(seloDigital, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static boolean sanitizarESalvar(String seloDigital, boolean forceRevalidate) {
        Connection conn = null;
        try {
            String registro;
            String codtabelPar;
            String nomeTabelionatoPar;
            String cnpjPar;
            String tabeliaoPar;
            String codigoPar;
            String ambientePar;
            String docPar;
            String dtVersaoFunarpen;
            String jsonAtual;
            block58: {
                conn = ConnectionFactory.getConnection();
                String sqlSelect = "SELECT sel.JSON, sel.JSON12, sel.DATAENVIO, sel.REGISTRO, p.DTVERSAO_FUNARPEN, p.DOC_PAR, p.AMBIENTE_PAR, p.CODIGO_PAR, p.TABELIAO_PAR, p.CNPJ_PAR, p.NOME_TABELIONATO_PAR, p.CODTABEL_PAR FROM selados sel INNER JOIN selos s ON sel.SELO = s.selo_sel INNER JOIN parametros p ON p.CODIGO_PAR = 1 WHERE s.selo_sel = ? LIMIT 1";
                jsonAtual = null;
                String json12Atual = null;
                dtVersaoFunarpen = null;
                docPar = null;
                ambientePar = null;
                codigoPar = null;
                tabeliaoPar = null;
                cnpjPar = null;
                nomeTabelionatoPar = null;
                codtabelPar = null;
                registro = null;
                try (PreparedStatement ps = conn.prepareStatement(sqlSelect);){
                    ps.setString(1, seloDigital);
                    try (ResultSet rs = ps.executeQuery();){
                        if (rs.next()) {
                            jsonAtual = rs.getString("JSON");
                            json12Atual = rs.getString("JSON12");
                            dtVersaoFunarpen = rs.getString("DTVERSAO_FUNARPEN");
                            docPar = rs.getString("DOC_PAR");
                            ambientePar = rs.getString("AMBIENTE_PAR");
                            codigoPar = rs.getString("CODIGO_PAR");
                            tabeliaoPar = rs.getString("TABELIAO_PAR");
                            cnpjPar = rs.getString("CNPJ_PAR");
                            nomeTabelionatoPar = rs.getString("NOME_TABELIONATO_PAR");
                            codtabelPar = rs.getString("CODTABEL_PAR");
                            registro = rs.getString("REGISTRO");
                        }
                    }
                }
                if (jsonAtual == null || jsonAtual.isEmpty()) {
                    LOGGER.warning("[SeloJsonSanitizerNotas] JSON n\u00e3o encontrado para selo: " + seloDigital);
                    boolean ps = false;
                    return ps;
                }
                if (json12Atual != null && !json12Atual.isEmpty() && !forceRevalidate) {
                    try {
                        String codOf;
                        JsonNode docResp;
                        String amb;
                        JsonNode test = M.readTree(json12Atual);
                        if (!test.isObject() || !test.has("selo")) break block58;
                        boolean temErroEstrutural = false;
                        ObjectNode testObj = (ObjectNode)test;
                        if (testObj.has("ambiente") && ("P".equals(amb = testObj.get("ambiente").asText()) || amb.contains("|") || amb.isEmpty())) {
                            temErroEstrutural = true;
                            LOGGER.info("[SeloJsonSanitizerNotas] Erro: ambiente='P' no selo: " + seloDigital);
                        }
                        if (testObj.has("documentoResponsavel") && ((docResp = testObj.get("documentoResponsavel")).isNull() || docResp.asText().contains("|") || docResp.asText().equals("0"))) {
                            temErroEstrutural = true;
                            LOGGER.info("[SeloJsonSanitizerNotas] Erro: documentoResponsavel null no selo: " + seloDigital);
                        }
                        if (testObj.has("codigoOficio") && ("codoficio".equals(codOf = testObj.get("codigoOficio").asText()) || codOf.contains("|"))) {
                            temErroEstrutural = true;
                            LOGGER.info("[SeloJsonSanitizerNotas] Erro: codigoOficio='codoficio' no selo: " + seloDigital);
                        }
                        if (testObj.has("selo") && testObj.get("selo").isObject()) {
                            double val;
                            ObjectNode verbas;
                            String data;
                            String cp;
                            ObjectNode selo = (ObjectNode)testObj.get("selo");
                            if (selo.has("codigoPedido") && (cp = selo.get("codigoPedido").asText()).contains(".")) {
                                temErroEstrutural = true;
                                LOGGER.info("[SeloJsonSanitizerNotas] Erro: codigoPedido decimal no selo: " + seloDigital);
                            }
                            if (selo.has("dataAtoPraticado") && ("dataato".equals(data = selo.get("dataAtoPraticado").asText()) || data.contains("|"))) {
                                temErroEstrutural = true;
                                LOGGER.info("[SeloJsonSanitizerNotas] Erro: dataAtoPraticado='dataato' no selo: " + seloDigital);
                            }
                            if (selo.has("seloRetificado_original") || selo.has("propriedades_sanitizadas") || selo.has("codigoPedido_sanitizado")) {
                                temErroEstrutural = true;
                                LOGGER.info("[SeloJsonSanitizerNotas] Erro: campos injetados incorretamente no selo: " + seloDigital);
                            }
                            if (selo.has("verbas") && selo.get("verbas").isObject() && (verbas = (ObjectNode)selo.get("verbas")).has("emolumentos") && (val = verbas.get("emolumentos").asDouble()) > 1000.0) {
                                temErroEstrutural = true;
                                LOGGER.info("[SeloJsonSanitizerNotas] Erro: verbas em centavos no selo: " + seloDigital);
                            }
                        }
                        if (!temErroEstrutural) {
                            boolean needsConversion;
                            boolean bl = needsConversion = !testObj.has("selo") || !testObj.get("selo").isObject();
                            if (!needsConversion) {
                                LOGGER.info("[SeloJsonSanitizerNotas] JSON12 v\u00e1lido para selo: " + seloDigital);
                                boolean verbas = true;
                                return verbas;
                            }
                            LOGGER.info("[SeloJsonSanitizerNotas] JSON12 \u00e9 V10 (sem objeto 'selo'), convertendo: " + seloDigital);
                        } else {
                            LOGGER.info("[SeloJsonSanitizerNotas] JSON12 com erros, refazendo para selo: " + seloDigital);
                        }
                    }
                    catch (Exception e) {
                        LOGGER.info("[SeloJsonSanitizerNotas] JSON12 corrompido, reconstruindo para selo: " + seloDigital);
                    }
                }
            }
            HashMap<String, Object> dadosComplementares = new HashMap<String, Object>();
            dadosComplementares.put("DOC_PAR", docPar);
            dadosComplementares.put("AMBIENTE_PAR", ambientePar);
            dadosComplementares.put("CODIGO_PAR", codigoPar);
            dadosComplementares.put("TABELIAO_PAR", tabeliaoPar);
            dadosComplementares.put("CNPJ_PAR", cnpjPar);
            dadosComplementares.put("NOME_TABELIONATO_PAR", nomeTabelionatoPar);
            dadosComplementares.put("CODTABEL_PAR", codtabelPar);
            dadosComplementares.put("REGISTRO", registro);
            String jsonSanitizado = SeloJsonSanitizerNotas.sanitizarComDados(jsonAtual, dadosComplementares, dtVersaoFunarpen);
            String sqlUpdate = "UPDATE selados SET JSON12 = ? WHERE SELO = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate);){
                ps.setString(1, jsonSanitizado);
                ps.setString(2, seloDigital);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    LOGGER.info("[SeloJsonSanitizerNotas] JSON12 salvo com sucesso para selo: " + seloDigital);
                    boolean bl = true;
                    return bl;
                }
            }
            boolean bl = false;
            return bl;
        }
        catch (Exception e) {
            LOGGER.severe("[SeloJsonSanitizerNotas] Erro ao sanitizar/salvar: " + e.getMessage());
            e.printStackTrace();
            boolean bl = false;
            return bl;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e) {
                    LOGGER.severe("Erro ao fechar conex\u00e3o: " + e.getMessage());
                }
            }
        }
    }

    private static String sanitizarComDados(String jsonOriginal, Map<String, Object> dadosComplementares, String dtVersaoFunarpen) throws Exception {
        if (jsonOriginal == null || jsonOriginal.trim().isEmpty()) {
            throw new Exception("JSON original está vazio");
        }

        String raw = jsonOriginal.trim();
        if (raw.startsWith("(String)")) raw = raw.substring(8).trim();
        // Remove aspas envoltas em toda a string se for o caso
        if (raw.startsWith("\"") && raw.endsWith("\"") && raw.length() > 2) {
            raw = raw.substring(1, raw.length() - 1).replace("\\\"", "\"");
        }
        
        // --- DETECÇÃO DE MODELO E NORMALIZAÇÃO ---
        // Se começa com wrapper mas tem lixo no final ou é flat, normalizamos
        boolean isV10 = raw.contains("\"numeroTipoAto\"") || raw.contains("\"ListaVerbas\"");
        boolean hasSelo = raw.contains("\"selo\":");
        
        String jsonSanitizado;
        if (isV10 && !hasSelo) {
            jsonSanitizado = converterV10ParaV11(raw, dadosComplementares);
        } else {
            // Se for Modelo 1/3 (Wrapper + Lixo concatenado), extraímos o principal e o pool
            List<String> fragmentos = splitRootObjects(raw);
            if (fragmentos.isEmpty()) throw new Exception("Não foi possível extrair JSON válido");
            
            String principal = fragmentos.get(0);
            JsonNode root = lerJsonLeniente(principal);
            if (root == null || !root.isObject()) {
                throw new Exception("Não foi possível parsear objeto principal");
            }
            ObjectNode obj = (ObjectNode) root;
            
            // Pool de propriedades de todas as fontes
            ObjectNode pool = M.createObjectNode();
            
            // Fonte 1: Propriedades já existentes no JSON (Modelo 2)
            if (obj.has("selo") && obj.get("selo").isObject()) {
                ObjectNode s = (ObjectNode) obj.get("selo");
                if (s.has("propriedades") && s.get("propriedades").isObject()) {
                    consolidarObjectNodeNoPool((ObjectNode)s.get("propriedades"), pool);
                }
            }
            
            // Fonte 2: Fragmentos extras concatenados (Modelo 1/3/5)
            for (int i = 1; i < fragmentos.size(); i++) {
                ObjectNode extra = parseQualquerFragmentoProps(fragmentos.get(i));
                if (extra != null) consolidarObjectNodeNoPool(extra, pool);
            }
            
            // Fonte 3: ListaPropriedadesExtras dentro do selo (se existir)
            if (obj.has("selo") && obj.get("selo").has("ListaPropriedadesExtras")) {
                ObjectNode extraInterno = parseQualquerFragmentoProps(obj.get("selo").get("ListaPropriedadesExtras").toString());
                if (extraInterno != null) consolidarObjectNodeNoPool(extraInterno, pool);
            }

            // Injeta o pool no objeto selo para processamento posterior por tipo de ato
            if (obj.has("selo") && obj.get("selo").isObject()) {
                ((ObjectNode)obj.get("selo")).set("_pool_propriedades", pool);
            }

            jsonSanitizado = M.writeValueAsString(obj);
        }

        // --- LIMPEZA E INJEÇÃO DE PARÂMETROS ---
        JsonNode node = M.readTree(jsonSanitizado);
        node = corrigirComDadosDoBanco(node, dadosComplementares, dtVersaoFunarpen, (String)dadosComplementares.get("REGISTRO"));
        
        String finalOutput = M.writeValueAsString(node);
        // Limpezas finais de placeholders e caracteres de controle que quebram a API
        finalOutput = finalOutput.replace("|ambiente|", (String)dadosComplementares.getOrDefault("AMBIENTE_PAR", "prod"))
                               .replace("|documentoResponsavel|", (String)dadosComplementares.getOrDefault("DOC_PAR", ""))
                               .replace("|codoficio|", (String)dadosComplementares.getOrDefault("CODTABEL_PAR", ""))
                               .replace("...", "").replace("|", "");
                               
        return finalOutput;
    }

    private static void consolidarObjectNodeNoPool(ObjectNode origem, ObjectNode pool) {
        Iterator<Map.Entry<String, JsonNode>> fields = origem.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            // Prioridade para o novo valor (conforme regra do usuário de fragmentos extras)
            pool.set(entry.getKey(), entry.getValue());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void processarTipo455(ObjectNode seloObj, ObjectNode rootObj, String docPar) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            ObjectNode pool = seloObj.has("_pool_propriedades") ? (ObjectNode) seloObj.get("_pool_propriedades") : M.createObjectNode();
            
            // --- 1. Determina o Subtipo (Com Valor, Sem Valor ou Sinal Público) ---
            int tipo = 2; // Default: Sem Valor
            if (seloObj.has("codigoTipoAto")) {
                int cod = seloObj.get("codigoTipoAto").asInt();
                if (cod == 403) tipo = 1;
                else if (cod == 404) tipo = 3;
            }
            
            // --- 2. Busca Solicitante (Prioridade: Pool -> Banco) ---
            String solNome = obterDoPool(pool, "solicitanteAto.nomeRazao", "solicitante.nome", "nomecli_rec");
            String solDoc = obterDoPool(pool, "solicitanteAto.numeroDocumento", "solicitante.doc", "cpfcli_rec");
            
            if (solNome == null) {
                String idap = seloObj.has("idap") ? seloObj.get("idap").asText() : null;
                String[] dbSol = buscarSolicitanteFinRecCab(idap, conn);
                if (dbSol != null) {
                    solNome = dbSol[0];
                    solDoc = dbSol[1];
                }
            }
            
            ObjectNode solicitanteAto = M.createObjectNode();
            solicitanteAto.put("nomeRazao", solNome != null ? solNome : "");
            solicitanteAto.put("documentoTipo", detectarTipoDocumento(solDoc));
            solicitanteAto.put("numeroDocumento", solDoc != null ? solDoc : "");
            solicitanteAto.put("endereco", (String)null);

            // --- 3. Extrai Signatários ---
            ArrayNode signatarios = extrairSignatarios(seloObj);
            
            // --- 4. Reconhecimento ---
            ObjectNode reconhecimento = M.createObjectNode();
            reconhecimento.put("especie", 1); // 1 = Por Verdadeira (Manual v11.12)
            reconhecimento.putNull("quantidadePaginasAto");
            reconhecimento.put("quantidadePartesEnvolvidasAto", signatarios.size());
            
            String dataAto = seloObj.has("dataAtoPraticado") ? seloObj.get("dataAtoPraticado").asText() : "";
            reconhecimento.put("data", dataAto);
            reconhecimento.putNull("descricao");

            // --- 5. Monta Propriedades Finais ---
            ObjectNode propsFinal = M.createObjectNode();
            propsFinal.put("tipo", tipo);
            propsFinal.set("solicitanteAto", solicitanteAto);
            propsFinal.set("reconhecimento", reconhecimento);
            propsFinal.set("signatarios", signatarios);
            
            seloObj.set("propriedades", propsFinal);
            seloObj.put("codigoTipoAto", 455); // Força código Funarpen Notas

            LOGGER.info("[SeloJsonSanitizerNotas] Tipo 455 processado. Solicitante: " + solNome + ", Signatários: " + signatarios.size());
            
        } catch (Exception e) {
            LOGGER.severe("Erro processarTipo455: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    private static String obterDoPool(ObjectNode pool, String... chaves) {
        for (String c : chaves) {
            // Busca exata
            if (pool.has(c) && !pool.get(c).isNull()) return pool.get(c).asText();
            
            // Busca case-insensitive básica se necessário
            Iterator<String> fields = pool.fieldNames();
            while (fields.hasNext()) {
                String f = fields.next();
                if (f.equalsIgnoreCase(c) && !pool.get(f).isNull()) return pool.get(f).asText();
            }
        }
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String[] buscarSolicitanteFinRecCab(String idap, Connection conn) {
        if (idap == null) return null;
        if (idap.length() < 20) {
            return null;
        }
        String numRec = idap.substring(10, 20);
        String sql = "SELECT nomecli_rec, cpfcli_rec FROM fin_reccab WHERE num_rec = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setString(1, numRec);
            try (ResultSet rs = ps.executeQuery();){
                if (!rs.next()) return null;
                String nome = rs.getString("nomecli_rec");
                String cpf = rs.getString("cpfcli_rec");
                String[] stringArray = new String[]{nome, cpf};
                return stringArray;
            }
        }
        catch (SQLException e) {
            LOGGER.warning("[SeloJsonSanitizerNotas] Erro ao buscar solicitante em fin_reccab: " + e.getMessage());
        }
        return null;
    }

    private static JsonNode corrigirComDadosDoBanco(JsonNode root, Map<String, Object> dadosComplementares, String dtVersaoFunarpen, String registro) throws Exception {
        if (root == null || !root.isObject()) return root;
        ObjectNode obj = (ObjectNode)root;

        String docPar = (String)dadosComplementares.get("DOC_PAR");
        String codigoPar = (String)dadosComplementares.get("CODIGO_PAR");
        String codtabelPar = (String)dadosComplementares.get("CODTABEL_PAR");
        String ambienteParRaw = (String)dadosComplementares.get("AMBIENTE_PAR");
        String ambientePar = (ambienteParRaw == null || ambienteParRaw.isEmpty()) ? "prod" : ambienteParRaw;
        
        if ("P".equalsIgnoreCase(ambientePar)) ambientePar = "prod";
        else if ("H".equalsIgnoreCase(ambientePar)) ambientePar = "homolog";
        else if ("D".equalsIgnoreCase(ambientePar) || "DEV".equalsIgnoreCase(ambientePar)) ambientePar = "dev";

        obj.put("ambiente", ambientePar);
        
        if (docPar != null && !docPar.isEmpty() && !docPar.equals("0")) {
            obj.put("documentoResponsavel", docPar);
        }
        
        if (codigoPar != null && !codigoPar.isEmpty()) {
            if (codigoPar.length() > 50 && codigoPar.contains(".")) {
                obj.put("codigoEmpresa", "1"); 
            } else {
                obj.put("codigoEmpresa", codigoPar);
            }
        }
        
        if (codtabelPar != null && !codtabelPar.isEmpty()) {
            obj.put("codigoOficio", codtabelPar);
        }

        if (obj.has("selo") && obj.get("selo").isObject()) {
            ObjectNode seloObj = (ObjectNode)obj.get("selo");
            
            // --- 1. Normalização de Dados Básicos (codigoPedido, datas) ---
            if (seloObj.has("codigoPedido") && !seloObj.get("codigoPedido").isNull()) {
                JsonNode cpNode = seloObj.get("codigoPedido");
                if (cpNode.isNumber()) {
                    seloObj.put("codigoPedido", (long)cpNode.asDouble());
                } else {
                    String valStr = cpNode.asText().replaceAll("[^0-9]", "");
                    if (!valStr.isEmpty()) try { seloObj.put("codigoPedido", Long.parseLong(valStr)); } catch (Exception e) {}
                }
            }
            
            if (seloObj.has("dataAtoPraticado") && !seloObj.get("dataAtoPraticado").isNull()) {
                String dAt = seloObj.get("dataAtoPraticado").asText();
                if (dAt.contains("|") || dAt.equalsIgnoreCase("dataato") || dAt.isEmpty()) {
                    if (seloObj.has("dataSeloEmitido") && !seloObj.get("dataSeloEmitido").isNull()) {
                        seloObj.set("dataAtoPraticado", seloObj.get("dataSeloEmitido"));
                    }
                }
            }

            // --- 2. Normalização de Verbas (Pre-processamento) ---
            if (seloObj.has("verbas") && seloObj.get("verbas").isObject()) {
                ObjectNode v = (ObjectNode)seloObj.get("verbas");
                String[] camposVerbas = {"emolumentos", "funrejus", "iss", "fundep", "funarpen", "distribuidor", "vrcExt", "valorAdicional"};
                for (String c : camposVerbas) {
                    if (v.has(c) && v.get(c).isNumber()) {
                        double dVal = v.get(c).asDouble();
                        String sVal = v.get(c).asText();
                        if (!sVal.contains(".") && dVal >= 10.0) {
                            v.put(c, (double)Math.round(dVal) / 100.0);
                        }
                    }
                }
            }

            // --- 3. Processamento Específico por Tipo de Ato ---
            int codigoTipoAtoOriginal = 0;
            if (obj.has("numeroTipoAto") && !obj.get("numeroTipoAto").isNull()) {
                codigoTipoAtoOriginal = obj.get("numeroTipoAto").asInt();
            } else if (seloObj.has("codigoTipoAto") && !seloObj.get("codigoTipoAto").isNull()) {
                codigoTipoAtoOriginal = seloObj.get("codigoTipoAto").asInt();
            }
            if (codigoTipoAtoOriginal == 408) codigoTipoAtoOriginal = 459;

            if (codigoTipoAtoOriginal > 0) {
                switch (codigoTipoAtoOriginal) {
                    case 401: case 402: case 403: case 404: case 455:
                        processarTipo455(seloObj, obj, docPar);
                        break;
                    case 409: case 411: case 412: case 413: case 459:
                        processarTipo459(seloObj, obj, docPar, registro);
                        break;
                    case 430: case 433: case 439: case 443: case 446: case 450:
                        processarTipo430(seloObj, obj);
                        break;
                    case 452:
                        processarTipo452(seloObj, obj, docPar);
                        break;
                    case 453:
                        processarTipo453(seloObj, obj);
                        break;
                    case 432: case 454:
                        String seloDigitalAtual = seloObj.has("seloDigital") ? seloObj.get("seloDigital").asText("") : "";
                        processarTipo454(seloObj, obj, docPar, seloDigitalAtual);
                        break;
                    case 456:
                        processarTipo456(seloObj, obj);
                        break;
                    case 407:
                        processarTipo407(seloObj, obj, docPar);
                        break;
                    case 416:
                        processarTipo416(seloObj, obj);
                        break;
                    case 457:
                        processarTipo457(seloObj, obj);
                        break;
                    case 458:
                        processarTipo458(seloObj, obj);
                        break;
                    default:
                        corrigirIdapNotas(seloObj);
                }
            }

            // --- 4. Limpeza Final Global e Normalização Pós-processamento ---
            seloObj.remove("ListaPropriedades");
            seloObj.remove("ListaVerbas");
            seloObj.remove("ListaPropriedadesExtras");
            seloObj.remove("verbas_sanitizadas");
            seloObj.remove("propriedades_sanitizadas");
            seloObj.remove("codigoPedido_sanitizado");
            seloObj.remove("seloRetificado_original");
            seloObj.remove("_pool_propriedades");

            if (seloObj.has("verbas") && seloObj.get("verbas").isObject()) {
                ObjectNode v = (ObjectNode)seloObj.get("verbas");
                String[] champs = {"emolumentos", "funrejus", "iss", "fundep", "funarpen", "distribuidor", "vrcExt", "valorAdicional"};
                for (String c : champs) {
                    if (v.has(c) && v.get(c).isNumber()) {
                        double dVal = v.get(c).asDouble();
                        String sVal = v.get(c).asText();
                        if (!sVal.contains(".") && dVal >= 10.0) {
                            v.put(c, (double)Math.round(dVal) / 100.0);
                        }
                    }
                }
            }
        }
        return root;
    }

    private static void corrigirIdapNotas(ObjectNode seloObj) {
        if (!seloObj.has("idap")) {
            return;
        }
        String idapAtual = seloObj.get("idap").asText();
        if (idapAtual == null || idapAtual.length() == 40 && idapAtual.contains("N")) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 12; ++i) {
                sb.append("0");
            }
            String prot = idapAtual.replaceAll("[^0-9]", "");
            if (prot.length() > 8) {
                prot = prot.substring(prot.length() - 8);
            }
            while (prot.length() < 8) {
                prot = "0" + prot;
            }
            sb.append(prot).append("N");
            while (sb.length() < 31) {
                sb.append("0");
            }
            while (sb.length() < 40) {
                sb.append("0");
            }
            seloObj.put("idap", sb.toString());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static void processarTipo430(ObjectNode seloObj, ObjectNode rootObj) {
        try {
            ObjectNode pool = seloObj.has("_pool_propriedades") ? (ObjectNode) seloObj.get("_pool_propriedades") : M.createObjectNode();
            
            // Prioridade 1: Dados do Pool (Dados extras ou V10 mapeado)
            String livro = obterDoPool(pool, "traslado.livro", "livro_ato", "livro");
            String folha = obterDoPool(pool, "traslado.folha", "folha_ato", "folha");
            String termo = obterDoPool(pool, "traslado.termo", "ato_ato", "termo");
            
            // Prioridade 2: Fallback parse IDAP
            if (livro == null || folha == null) {
                String idap = seloObj.has("idap") ? seloObj.get("idap").asText() : "";
                if (idap.length() >= 24) {
                    livro = idap.substring(12, 20).replaceFirst("^0+(?=\\d)", "");
                    folha = idap.substring(20, 24).replaceFirst("^0+(?=\\d)", "");
                }
            }

            ObjectNode prop = M.createObjectNode();
            ObjectNode traslado = M.createObjectNode();
            traslado.put("livro", livro != null ? livro : "0");
            traslado.put("folha", folha != null ? folha : "0");
            traslado.putNull("termo");
            
            prop.set("traslado", traslado);
            seloObj.set("propriedades", prop);
            corrigirIdapNotas(seloObj);
            
        } catch (Exception e) {
            LOGGER.warning("Erro processarTipo430 (Traslado): " + e.getMessage());
        }
    }

    private static void processarTipo459(ObjectNode seloObj, ObjectNode rootObj, String docPar, String registro) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            ObjectNode pool = seloObj.has("_pool_propriedades") ? (ObjectNode) seloObj.get("_pool_propriedades") : M.createObjectNode();
            String selo = seloObj.has("seloDigital") ? seloObj.get("seloDigital").asText() : "";
            
            // --- 1. Dados da Escritura (Pool -> Banco) ---
            String livro = obterDoPool(pool, "escritura.livro", "livro_ato", "livro");
            String folha = obterDoPool(pool, "escritura.folha", "folha_ato", "folha");
            String termo = obterDoPool(pool, "escritura.termo", "ato_ato", "termo", "ato");
            String data = obterDoPool(pool, "escritura.data", "data_ato", "data");
            
            if (livro == null || data == null) {
                Map<String, Object> dbAto = buscarDadosAtoPorSelo(selo, conn);
                if (!dbAto.isEmpty()) {
                    if (livro == null) livro = (String)dbAto.get("LIVRO_ATO");
                    if (folha == null) folha = (String)dbAto.get("FOLHA_ATO");
                    if (termo == null) termo = (String)dbAto.get("ATO_ATO");
                    if (data == null) data = (String)dbAto.get("DATA_ATO");
                }
            }
            
            ObjectNode escritura = M.createObjectNode();
            escritura.put("livro", livro != null ? livro : "0");
            escritura.put("folha", folha != null ? folha : "0");
            escritura.put("termo", termo != null ? termo : "1");
            escritura.put("data", data != null ? data : "");
            
            ObjectNode props = M.createObjectNode();
            props.set("escritura", escritura);

            // --- 2. Bens/Imóveis (Pool -> Banco) ---
            ArrayNode bens = M.createArrayNode();
            if (pool.has("bens") && pool.get("bens").isArray()) {
                bens = (ArrayNode) pool.get("bens");
            } else {
                List<Map<String, Object>> dbImoveis = buscarImoveisAto(selo, conn);
                for (Map<String, Object> imo : dbImoveis) {
                    ObjectNode b = M.createObjectNode();
                    b.put("descricao", (String)imo.getOrDefault("DESCRICAO_IMO", ""));
                    b.put("matricula", (String)imo.getOrDefault("MATRICULA_IMO", ""));
                    b.put("valorAvaliacao", (Double)imo.getOrDefault("VALORITBI_IMO", 0.0));
                    bens.add(b);
                }
            }
            props.set("bens", bens);

            // --- 3. Outorgantes / Outorgados ---
            ArrayNode outorgantes = M.createArrayNode();
            ArrayNode outorgados = M.createArrayNode();
            
            // Primeiro checa se o pool já tem os arrays organizados
            if (pool.has("outorgantes") && pool.get("outorgantes").isArray()) {
                outorgantes = (ArrayNode) pool.get("outorgantes");
            }
            if (pool.has("outorgados") && pool.get("outorgados").isArray()) {
                outorgados = (ArrayNode) pool.get("outorgados");
            }
            
            // Caso contrário, tenta extrair de 'signatarios' no pool ou do banco
            if (outorgantes.size() == 0 && outorgados.size() == 0) {
                List<Map<String, Object>> dbPartes = buscarPartesNot1PorRegistro(registro, conn);
                for (Map<String, Object> p : dbPartes) {
                    ObjectNode pn = M.createObjectNode();
                    String nome = (String)p.getOrDefault("NOME_NOT1", "");
                    String numDoc = (String)p.getOrDefault("CPF_NOT1", "");
                    pn.put("nomeRazao", nome);
                    pn.put("documentoNumero", numDoc);
                    pn.put("documentoTipo", detectarTipoDocumento(numDoc));
                    
                    String qualif = String.valueOf(p.getOrDefault("QUALIF_NOT1", ""));
                    if ("2".equals(qualif)) outorgados.add(pn);
                    else outorgantes.add(pn);
                }
            }
            
            props.set("outorgantes", outorgantes);
            props.set("outorgados", outorgados);
            
            seloObj.set("propriedades", props);
            seloObj.put("codigoTipoAto", 459);
            corrigirIdapNotas(seloObj);

        } catch (Exception e) {
            LOGGER.warning("Erro processarTipo459: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    private static boolean processarListaPropriedadesParaEscritura(ObjectNode seloObj, ObjectNode prop) {
        String idap;
        if (!seloObj.has("ListaPropriedades") || !seloObj.get("ListaPropriedades").isArray()) {
            return false;
        }
        ArrayNode listaProp = (ArrayNode)seloObj.get("ListaPropriedades");
        String livro = "0";
        String folha = "0";
        String termo = "1";
        String data = "";
        String string = idap = seloObj.has("idap") ? seloObj.get("idap").asText() : "";
        if (idap.length() >= 24) {
            String parte = idap.substring(12, 20);
            livro = parte.substring(0, 4);
            if ((livro = livro.replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                livro = "0";
            }
            parte = idap.substring(20, 24);
            folha = parte.substring(0, 4);
            if ((folha = folha.replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                folha = "0";
            }
        }
        if (seloObj.has("dataAto") && !seloObj.get("dataAto").isNull()) {
            data = seloObj.get("dataAto").asText();
        } else if (seloObj.has("dataAtoPraticado") && !seloObj.get("dataAtoPraticado").isNull()) {
            data = seloObj.get("dataAtoPraticado").asText();
        }
        ObjectNode escritura = M.createObjectNode();
        escritura.put("livro", livro);
        escritura.put("folha", folha);
        escritura.put("termo", termo);
        escritura.put("data", data);
        prop.set("escritura", (JsonNode)escritura);
        prop.set("bens", (JsonNode)M.createArrayNode());
        ArrayList<ObjectNode> outorgantes = new ArrayList<ObjectNode>();
        ArrayList outorgados = new ArrayList();
        HashSet<String> seen = new HashSet<String>();
        String ultimoNome = null;
        String ultimoDoc = null;
        for (int i = 0; i < listaProp.size(); ++i) {
            String valorProp;
            JsonNode item = listaProp.get(i);
            if (item == null || !item.isObject()) continue;
            ObjectNode it = (ObjectNode)item;
            String nomeProp = it.has("NomePropriedade") ? it.get("NomePropriedade").asText(null) : null;
            String string2 = valorProp = it.has("ValorPropriedade") && !it.get("ValorPropriedade").isNull() ? it.get("ValorPropriedade").asText(null) : null;
            if (nomeProp == null || valorProp == null) continue;
            String npLower = nomeProp.toLowerCase();
            if (npLower.contains("nome_razao") || npLower.contains("nome")) {
                ultimoNome = valorProp;
            } else if (npLower.contains("cpf") || npLower.contains("cnpj")) {
                ultimoDoc = valorProp;
            }
            if (ultimoNome == null || ultimoNome.isEmpty() || ultimoDoc == null || ultimoDoc.isEmpty()) continue;
            String key = ultimoDoc.replaceAll("[^0-9]", "");
            if (!seen.contains(key)) {
                seen.add(key);
                ObjectNode pn = M.createObjectNode();
                pn.put("nomeRazao", ultimoNome);
                pn.put("numeroDocumento", ultimoDoc);
                pn.put("documentoTipo", SeloJsonSanitizerNotas.detectarTipoDocumento(ultimoDoc));
                outorgantes.add(pn);
            }
            ultimoNome = null;
            ultimoDoc = null;
        }
        ArrayNode outorgantesArray = M.createArrayNode();
        for (ObjectNode o : outorgantes) {
            outorgantesArray.add((JsonNode)o);
        }
        prop.set("outorgantes", (JsonNode)outorgantesArray);
        ArrayNode outorgadosArray = M.createArrayNode();
        for (Object o : outorgados) {
            outorgadosArray.add((JsonNode)o);
        }
        prop.set("outorgados", (JsonNode)outorgadosArray);
        return outorgantes.size() > 0 || outorgados.size() > 0;
    }

    private static Map<String, Object> buscarDadosAtoPorSelo(String selo, Connection conn) throws SQLException {
        HashMap<String, Object> res = new HashMap<String, Object>();
        String sql = "SELECT a.* FROM ato a INNER JOIN selados s ON s.PROTOC_LIVRO_ATO = a.PROTOC_LIVRO_ATO WHERE s.SELO = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setString(1, selo);
            try (ResultSet rs = ps.executeQuery();){
                if (rs.next()) {
                    res.put("LIVRO_ATO", rs.getString("LIVRO_ATO"));
                    res.put("FOLHA_ATO", rs.getString("FOLHA_ATO"));
                    res.put("ATO_ATO", rs.getString("ATO_ATO"));
                    res.put("DATA_ATO", rs.getString("DATA_ATO"));
                }
            }
        }
        return res;
    }

    private static List<Map<String, Object>> buscarImoveisAto(String selo, Connection conn) throws SQLException {
        ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        String sql = "SELECT i.* FROM imoveis i INNER JOIN selados s ON s.PROTOC_LIVRO_ATO = i.PROTOC_LIVRO_ATO WHERE s.SELO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setString(1, selo);
            try (ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    HashMap<String, Object> m = new HashMap<String, Object>();
                    m.put("MATRICULA_IMO", rs.getString("MATRICULA_IMO"));
                    m.put("DESCRICAO_IMO", rs.getString("DESCRICAO_IMO"));
                    m.put("VALORITBI_IMO", rs.getDouble("VALORITBI_IMO"));
                    res.add(m);
                }
            }
        }
        return res;
    }

    private static List<Map<String, Object>> buscarPartesNot1(String selo, Connection conn) throws SQLException {
        ArrayList<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        String sql = "SELECT n.* FROM not_1 n INNER JOIN selados s ON (s.LIVRO = n.LIVRO_ATO AND s.FOLHA = n.FOLHA_ATO) WHERE s.SELO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setString(1, selo);
            try (ResultSet rs = ps.executeQuery();){
                while (rs.next()) {
                    HashMap<String, Object> m = new HashMap<String, Object>();
                    m.put("NOME_NOT1", rs.getString("NOME_NOT1"));
                    m.put("CPF_NOT1", rs.getString("CPF_NOT1"));
                    m.put("QUALIF_NOT1", rs.getString("QUALIF_NOT1"));
                    m.put("TIPOPESSOA_NOT1", rs.getString("TIPOPESSOA_NOT1"));
                    res.add(m);
                }
            }
        }
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void processarTipo452(ObjectNode seloObj, ObjectNode rootObj, String docPar) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            String selo = seloObj.has("seloDigital") ? seloObj.get("seloDigital").asText() : "";
            Map<String, Object> dadosAto = SeloJsonSanitizerNotas.buscarDadosAtoPorSelo(selo, conn);
            List<Map<String, Object>> partes = SeloJsonSanitizerNotas.buscarPartesNot1(selo, conn);
            ObjectNode prop = M.createObjectNode();
            ObjectNode procuracao = M.createObjectNode();
            procuracao.put("livro", (String)dadosAto.getOrDefault("LIVRO_ATO", "0"));
            procuracao.put("folha", (String)dadosAto.getOrDefault("FOLHA_ATO", "0"));
            procuracao.put("data", (String)dadosAto.getOrDefault("DATA_ATO", ""));
            prop.set("procuracao", (JsonNode)procuracao);
            ArrayNode outorgantes = M.createArrayNode();
            ArrayNode outorgados = M.createArrayNode();
            for (Map<String, Object> p : partes) {
                ObjectNode pn = M.createObjectNode();
                pn.put("nomeRazao", (String)p.getOrDefault("NOME_NOT1", ""));
                pn.put("numeroDocumento", (String)p.getOrDefault("CPF_NOT1", ""));
                pn.put("tipoDocumento", SeloJsonSanitizerNotas.detectarTipoDocumento((String)p.getOrDefault("CPF_NOT1", "")));
                String qualif = (String)p.getOrDefault("QUALIF_NOT1", "");
                if (qualif != null && qualif.toLowerCase().contains("outorgado")) {
                    outorgados.add((JsonNode)pn);
                    continue;
                }
                outorgantes.add((JsonNode)pn);
            }
            prop.set("outorgantes", (JsonNode)outorgantes);
            prop.set("outorgados", (JsonNode)outorgados);
            seloObj.set("propriedades", (JsonNode)prop);
            seloObj.put("codigoTipoAto", 452);
            SeloJsonSanitizerNotas.corrigirIdapNotas(seloObj);
        }
        catch (Exception e) {
            LOGGER.warning("Erro 452: " + e.getMessage());
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException sQLException) {}
        }
    }

    private static void processarTipo454(ObjectNode seloObj, ObjectNode rootObj, String docPar, String seloDigital) {
        ObjectNode prop = M.createObjectNode();
        String nomeRazaoEnvolvido = null;
        String cpfCnpjEnvolvido = null;
        String dataRegistro = null;
        ArrayNode envolvidos = M.createArrayNode();
        
        // --- Extrair dados de ListaPropriedades ---
        if (seloObj.has("ListaPropriedades") && seloObj.get("ListaPropriedades").isArray()) {
            ArrayNode listaProp = (ArrayNode) seloObj.get("ListaPropriedades");
            ObjectNode envolvidoAtual = null;
            for (int i = 0; i < listaProp.size(); i++) {
                JsonNode item = listaProp.get(i);
                if (item == null || !item.isObject()) continue;
                String nomeProp = item.has("NomePropriedade") ? item.get("NomePropriedade").asText("") : "";
                String valorProp = item.has("ValorPropriedade") ? item.get("ValorPropriedade").asText("") : "";
                String npLower = nomeProp.toLowerCase();
                if (npLower.contains("nome_razao")) {
                    if (envolvidoAtual != null && envolvidoAtual.has("nomeRazao")) {
                        envolvidos.add(envolvidoAtual);
                    }
                    envolvidoAtual = M.createObjectNode();
                    envolvidoAtual.put("nomeRazao", valorProp);
                    if (nomeRazaoEnvolvido == null) nomeRazaoEnvolvido = valorProp;
                } else if ((npLower.contains("cpf") || npLower.contains("cnpj")) && envolvidoAtual != null) {
                    envolvidoAtual.put("numeroDocumento", valorProp);
                    envolvidoAtual.put("tipoDocumento", SeloJsonSanitizerNotas.detectarTipoDocumento(valorProp));
                    if (cpfCnpjEnvolvido == null) cpfCnpjEnvolvido = valorProp;
                } else if (npLower.contains("data_registro")) {
                    dataRegistro = valorProp;
                }
            }
            if (envolvidoAtual != null && envolvidoAtual.has("nomeRazao")) {
                envolvidos.add(envolvidoAtual);
            }
        }
        
        try {
            ObjectNode pool = seloObj.has("_pool_propriedades") ? (ObjectNode) seloObj.get("_pool_propriedades") : M.createObjectNode();
            ObjectNode certProps = M.createObjectNode();
            
            // --- 1. Dados da Certidão (Pool -> Banco) ---
            String livro = obterDoPool(pool, "certidao.livro", "livro_ato", "livro");
            String folha = obterDoPool(pool, "certidao.folha", "folha_ato", "folha");
            String dataReg = obterDoPool(pool, "certidao.dataRegistro", "data_registro", "data");
            String solicAto = obterDoPool(pool, "solicitanteAto.nomeRazao", "solic_ato", "nome_razao");
            int certidaoTipo = 1; // Default 1 = Procuração (Manual v11.12)
            
            if (livro == null || solicAto == null) {
                try (Connection conn = ConnectionFactory.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                         "SELECT c.SOLIC_ATO, c.LIVRO_ATO, c.LETRA_ATO, c.FOLHA_ATO, f.codigo_ser " +
                         "FROM certidao c " +
                         "LEFT JOIN fin_reccab f ON c.NUM_REC_ATO = f.num_rec " +
                         "WHERE c.SELO_ATO = ? LIMIT 1")) {
                    ps.setString(1, seloDigital);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if (solicAto == null) solicAto = rs.getString("SOLIC_ATO");
                            if (livro == null) {
                                String l = rs.getString("LIVRO_ATO");
                                String letra = rs.getString("LETRA_ATO");
                                livro = (l != null ? l : "") + (letra != null ? letra : "");
                            }
                            if (folha == null) folha = rs.getString("FOLHA_ATO");
                            
                            String codSer = rs.getString("codigo_ser");
                            if ("28".equals(codSer)) certidaoTipo = 2; // Escritura
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warning("Erro banco Certidao: " + e.getMessage());
                }
            }

            // --- 2. Monta Objetos v11.12 ---
            ObjectNode solicitante = M.createObjectNode();
            solicitante.put("nomeRazao", solicAto != null ? solicAto : "");
            solicitante.put("tipoDocumento", 12);
            solicitante.putNull("numeroDocumento");
            certProps.set("solicitanteAto", solicitante);

            ObjectNode certidaoNode = M.createObjectNode();
            certidaoNode.put("tipo", certidaoTipo);
            certidaoNode.put("livro", livro != null ? livro : "0");
            certidaoNode.put("folha", folha != null ? folha : "0");
            certidaoNode.put("dataRegistro", dataReg != null ? dataReg : "");
            certidaoNode.putNull("termo");
            certProps.set("certidao", certidaoNode);

            seloObj.set("propriedades", certProps);
            seloObj.put("codigoTipoAto", 454);
            corrigirIdapNotas(seloObj);

        } catch (Exception e) {
            LOGGER.warning("Erro processarTipo454: " + e.getMessage());
        }
    }

    private static void processarTipo453(ObjectNode seloObj, ObjectNode rootObj) {
        try {
            String idap = seloObj.has("idap") ? seloObj.get("idap").asText() : "";
            String livro = "0";
            String folha = "0";
            if (idap.length() >= 24) {
                String parte = idap.substring(12, 20);
                livro = parte.substring(0, 4);
                if ((livro = livro.replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    livro = "0";
                }
                parte = idap.substring(20, 24);
                folha = parte.substring(0, 4);
                if ((folha = folha.replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    folha = "0";
                }
            }
            ObjectNode prop = M.createObjectNode();
            ObjectNode testamento = M.createObjectNode();
            testamento.put("tipo", 1);
            testamento.putPOJO("livro", (Object)livro);
            testamento.putPOJO("folha", (Object)folha);
            testamento.putPOJO("termo", null);
            prop.set("testamento", (JsonNode)testamento);
            ArrayNode sign = SeloJsonSanitizerNotas.extrairSignatarios(seloObj);
            if (sign.size() > 0) {
                prop.set("outorgantes", (JsonNode)sign);
            }
            seloObj.set("propriedades", (JsonNode)prop);
            seloObj.put("codigoTipoAto", 453);
            SeloJsonSanitizerNotas.corrigirIdapNotas(seloObj);
        }
        catch (Exception e) {
            LOGGER.warning("Erro 453: " + e.getMessage());
        }
    }

    private static void processarTipo456(ObjectNode seloObj, ObjectNode rootObj) {
        try {
            String idap = seloObj.has("idap") ? seloObj.get("idap").asText() : "";
            String livro = "0";
            String folha = "0";
            if (idap.length() >= 24) {
                String parte = idap.substring(12, 20);
                livro = parte.substring(0, 4);
                if ((livro = livro.replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    livro = "0";
                }
                parte = idap.substring(20, 24);
                folha = parte.substring(0, 4);
                if ((folha = folha.replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    folha = "0";
                }
            }
            ObjectNode prop = M.createObjectNode();
            ObjectNode ata = M.createObjectNode();
            ata.put("tipo", 1);
            ata.put("especie", 1);
            ata.putPOJO("livro", (Object)livro);
            ata.putPOJO("folha", (Object)folha);
            ata.putPOJO("termo", null);
            prop.set("ataNotarial", (JsonNode)ata);
            ArrayNode sign = SeloJsonSanitizerNotas.extrairSignatarios(seloObj);
            if (sign.size() > 0) {
                prop.set("outorgantes", (JsonNode)sign);
            }
            seloObj.set("propriedades", (JsonNode)prop);
            seloObj.put("codigoTipoAto", 456);
            SeloJsonSanitizerNotas.corrigirIdapNotas(seloObj);
        }
        catch (Exception e) {
            LOGGER.warning("Erro 456: " + e.getMessage());
        }
    }

    private static void processarTipo407(ObjectNode seloObj, ObjectNode rootObj, String docPar) {
        try {
            ObjectNode prop = M.createObjectNode();
            ObjectNode solicitanteAto = M.createObjectNode();
            solicitanteAto.putPOJO("nomeRazao", null);
            solicitanteAto.putPOJO("tipoDocumento", null);
            solicitanteAto.putPOJO("numeroDocumento", null);
            prop.set("solicitanteAto", (JsonNode)solicitanteAto);
            ObjectNode cartaSentenca = M.createObjectNode();
            cartaSentenca.putPOJO("totalPaginas", null);
            prop.set("cartaSentenca", (JsonNode)cartaSentenca);
            ArrayNode sign = SeloJsonSanitizerNotas.extrairSignatarios(seloObj);
            if (sign.size() > 0) {
                prop.set("signatarios", (JsonNode)sign);
            }
            seloObj.set("propriedades", (JsonNode)prop);
            SeloJsonSanitizerNotas.corrigirIdapNotas(seloObj);
        }
        catch (Exception e) {
            LOGGER.warning("Erro 407: " + e.getMessage());
        }
    }

    private static void processarTipo416(ObjectNode seloObj, ObjectNode rootObj) {
        try {
            String idap = seloObj.has("idap") ? seloObj.get("idap").asText() : "";
            String livro = "0";
            String folha = "0";
            if (idap.length() >= 24) {
                String parte = idap.substring(12, 20);
                livro = parte.substring(0, 4);
                if ((livro = livro.replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    livro = "0";
                }
                parte = idap.substring(20, 24);
                folha = parte.substring(0, 4);
                if ((folha = folha.replaceFirst("^0+(?=\\d)", "")).isEmpty()) {
                    folha = "0";
                }
            }
            ObjectNode prop = M.createObjectNode();
            ObjectNode solicitanteAto = M.createObjectNode();
            solicitanteAto.putPOJO("nomeRazao", null);
            solicitanteAto.putPOJO("tipoDocumento", null);
            solicitanteAto.putPOJO("numeroDocumento", null);
            prop.set("solicitanteAto", (JsonNode)solicitanteAto);
            ObjectNode escritura = M.createObjectNode();
            escritura.put("tipo", 1);
            ObjectNode unidades = M.createObjectNode();
            unidades.putPOJO("quantidadeTotal", null);
            unidades.putPOJO("quantidadePartes", null);
            escritura.set("unidades", (JsonNode)unidades);
            escritura.putPOJO("livro", (Object)livro);
            escritura.putPOJO("folha", (Object)folha);
            escritura.putPOJO("termo", null);
            prop.set("escritura", (JsonNode)escritura);
            ArrayNode sign = SeloJsonSanitizerNotas.extrairSignatarios(seloObj);
            if (sign.size() > 0) {
                prop.set("outorgantes", (JsonNode)sign);
            }
            seloObj.set("propriedades", (JsonNode)prop);
            seloObj.put("codigoTipoAto", 416);
            SeloJsonSanitizerNotas.corrigirIdapNotas(seloObj);
        }
        catch (Exception e) {
            LOGGER.warning("Erro 416: " + e.getMessage());
        }
    }

    private static void processarTipo457(ObjectNode seloObj, ObjectNode rootObj) {
        try {
            ObjectNode prop = M.createObjectNode();
            ObjectNode solicitanteAto = M.createObjectNode();
            solicitanteAto.putPOJO("nomeRazao", null);
            solicitanteAto.putPOJO("tipoDocumento", null);
            solicitanteAto.putPOJO("numeroDocumento", null);
            prop.set("solicitanteAto", (JsonNode)solicitanteAto);
            ObjectNode materializacao = M.createObjectNode();
            materializacao.putPOJO("data", null);
            materializacao.putPOJO("descricao", null);
            ObjectNode envolvido = M.createObjectNode();
            envolvido.putPOJO("nomeRazao", null);
            materializacao.set("envolvido", (JsonNode)envolvido);
            prop.set("materializacao", (JsonNode)materializacao);
            ArrayNode sign = SeloJsonSanitizerNotas.extrairSignatarios(seloObj);
            if (sign.size() > 0) {
                prop.set("signatarios", (JsonNode)sign);
            }
            seloObj.set("propriedades", (JsonNode)prop);
            SeloJsonSanitizerNotas.corrigirIdapNotas(seloObj);
        }
        catch (Exception e) {
            LOGGER.warning("Erro 457: " + e.getMessage());
        }
    }

    private static void processarTipo458(ObjectNode seloObj, ObjectNode rootObj) {
        try {
            ObjectNode prop = M.createObjectNode();
            ObjectNode solicitanteAto = M.createObjectNode();
            solicitanteAto.putPOJO("nomeRazao", null);
            solicitanteAto.putPOJO("tipoDocumento", null);
            solicitanteAto.putPOJO("numeroDocumento", null);
            prop.set("solicitanteAto", (JsonNode)solicitanteAto);
            ObjectNode certidao = M.createObjectNode();
            certidao.put("tipo", 2);
            certidao.putPOJO("consulta", null);
            prop.set("certidao", (JsonNode)certidao);
            ObjectNode buscas = M.createObjectNode();
            buscas.putPOJO("quantidade", null);
            buscas.putPOJO("escritura", null);
            buscas.putPOJO("quantidadeFolhaAdicional", null);
            prop.set("buscas", (JsonNode)buscas);
            seloObj.set("propriedades", (JsonNode)prop);
            SeloJsonSanitizerNotas.corrigirIdapNotas(seloObj);
        }
        catch (Exception e) {
            LOGGER.warning("Erro 458: " + e.getMessage());
        }
    }

    private static String converterParaISO(String data) {
        String[] formatos;
        if (data == null || data.isEmpty()) {
            return null;
        }
        String digits = data.replaceAll("[^0-9]", "").trim();
        if (digits.matches("^\\d{8}$")) {
            try {
                DateTimeFormatter fmtYmd = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate d = LocalDate.parse(digits, fmtYmd);
                return d.format(DATE_FORMAT_ISO);
            }
            catch (Exception ignore) {
                try {
                    DateTimeFormatter fmtDmy = DateTimeFormatter.ofPattern("ddMMyyyy");
                    LocalDate d2 = LocalDate.parse(digits, fmtDmy);
                    return d2.format(DATE_FORMAT_ISO);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        for (String formato : formatos = new String[]{"dd/MM/yyyy", "d/M/yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "yyyy/MM/dd"}) {
            try {
                LocalDate date = LocalDate.parse(data, DateTimeFormatter.ofPattern(formato));
                return date.format(DATE_FORMAT_ISO);
            }
            catch (DateTimeParseException dateTimeParseException) {
            }
        }
        LOGGER.warning("[SeloJsonSanitizerNotas] N\u00e3o foi poss\u00edvel converter data: " + data);
        return null;
    }

    private static JsonNode corrigirErrosDigitacao(JsonNode root) {
        block11: {
            block10: {
                JsonNode sr;
                if (!root.isObject()) break block10;
                ObjectNode obj = (ObjectNode)root;
                if (obj.has("emvolvidos")) {
                    JsonNode emvolvidos = obj.get("emvolvidos");
                    if (!obj.has("envolvidos")) {
                        obj.set("envolvidos", emvolvidos);
                        LOGGER.fine("[SeloJsonSanitizerNotas] Copiado: emvolvidos -> envolvidos (mantido campo original)");
                    }
                }
                if (obj.has("emolumentoss")) {
                    JsonNode emolumentoss = obj.get("emolumentoss");
                    if (!obj.has("emolumentos")) {
                        obj.set("emolumentos", emolumentoss);
                        LOGGER.fine("[SeloJsonSanitizerNotas] Copiado: emolumentoss -> emolumentos (mantido campo original)");
                    }
                }
                if (obj.has("seloRetificado") && ((sr = obj.get("seloRetificado")).isNull() || sr.asText().isEmpty() || sr.asText().equalsIgnoreCase("null") || sr.asText().equals(""))) {
                    if (!obj.has("seloRetificado_original")) {
                        obj.set("seloRetificado_original", sr);
                    }
                    obj.putNull("seloRetificado");
                    LOGGER.fine("[SeloJsonSanitizerNotas] seloRetificado vazio convertido para null (original preservado em seloRetificado_original)");
                }
                Iterator fields = obj.fields();
                while (fields.hasNext()) {
                    Map.Entry field = (Map.Entry)fields.next();
                    JsonNode value = (JsonNode)field.getValue();
                    if (value.isObject()) {
                        SeloJsonSanitizerNotas.corrigirErrosDigitacao(value);
                        continue;
                    }
                    if (!value.isArray()) continue;
                    for (JsonNode item : value) {
                        if (!item.isObject()) continue;
                        SeloJsonSanitizerNotas.corrigirErrosDigitacao(item);
                    }
                }
                break block11;
            }
            if (!root.isArray()) break block11;
            ArrayNode arr = (ArrayNode)root;
            for (JsonNode item : arr) {
                if (!item.isObject()) continue;
                SeloJsonSanitizerNotas.corrigirErrosDigitacao(item);
            }
        }
        return root;
    }

    private static JsonNode corrigirSeloRetificado(JsonNode root) {
        block6: {
            block5: {
                JsonNode sr;
                if (!root.isObject()) break block5;
                ObjectNode obj = (ObjectNode)root;
                if (obj.has("seloRetificado") && ((sr = obj.get("seloRetificado")).isNull() || sr.asText().isEmpty() || sr.asText().equalsIgnoreCase("null") || sr.asText().equals(""))) {
                    obj.putNull("seloRetificado");
                    LOGGER.info("[SeloJsonSanitizerNotas] Corrigido seloRetificado para null");
                }
                Iterator fields = obj.fields();
                while (fields.hasNext()) {
                    Map.Entry field = (Map.Entry)fields.next();
                    JsonNode value = (JsonNode)field.getValue();
                    if (value.isObject()) {
                        SeloJsonSanitizerNotas.corrigirSeloRetificado(value);
                        continue;
                    }
                    if (!value.isArray()) continue;
                    for (JsonNode item : value) {
                        if (!item.isObject()) continue;
                        SeloJsonSanitizerNotas.corrigirSeloRetificado(item);
                    }
                }
                break block6;
            }
            if (!root.isArray()) break block6;
            ArrayNode arr = (ArrayNode)root;
            for (JsonNode item : arr) {
                if (!item.isObject()) continue;
                SeloJsonSanitizerNotas.corrigirSeloRetificado(item);
            }
        }
        return root;
    }

    private static JsonNode converterListaVerbasParaVerbas(JsonNode root) {
        if (!root.isObject()) {
            return root;
        }
        ObjectNode obj = (ObjectNode)root;
        if (obj.has("selo") && obj.get("selo").isObject()) {
            return SeloJsonSanitizerNotas.converterListaVerbasParaVerbasEmSelo(obj, (ObjectNode)obj.get("selo"));
        }
        return SeloJsonSanitizerNotas.converterListaVerbasParaVerbasEmSelo(obj, obj);
    }

    private static JsonNode converterListaVerbasParaVerbasEmSelo(ObjectNode root, ObjectNode seloObj) {
        if (seloObj.has("ListaVerbas") && seloObj.get("ListaVerbas").isArray()) {
            JsonNode seloNode;
            ArrayNode listaVerbas = (ArrayNode)seloObj.get("ListaVerbas");
            ObjectNode verbas = M.createObjectNode();
            for (int i = 0; i < listaVerbas.size(); ++i) {
                JsonNode valorNode;
                JsonNode item = listaVerbas.get(i);
                if (!item.isObject()) continue;
                String nomeVerba = item.has("NomeVerba") ? item.get("NomeVerba").asText() : null;
                JsonNode jsonNode = valorNode = item.has("ValorVerba") ? item.get("ValorVerba") : null;
                if (nomeVerba == null || valorNode == null) continue;
                String nomeNormalizado = nomeVerba.toUpperCase().replace(" ", "");
                if (nomeNormalizado.contains("EMOLUMENTO")) {
                    verbas.put("emolumentos", valorNode.isNumber() ? valorNode.doubleValue() : 0.0);
                    continue;
                }
                if (nomeNormalizado.contains("FUNREJUS")) {
                    verbas.put("funrejus", valorNode.isNumber() ? valorNode.doubleValue() : 0.0);
                    continue;
                }
                if (nomeNormalizado.contains("ISS")) {
                    verbas.put("iss", valorNode.isNumber() ? valorNode.doubleValue() : 0.0);
                    continue;
                }
                if (nomeNormalizado.contains("FUNDEP")) {
                    verbas.put("fundep", valorNode.isNumber() ? valorNode.doubleValue() : 0.0);
                    continue;
                }
                if (nomeNormalizado.contains("FUNARPEN")) {
                    verbas.put("funarpen", valorNode.isNumber() ? valorNode.doubleValue() : 0.0);
                    continue;
                }
                if (nomeNormalizado.contains("DISTRIBUIDOR")) {
                    verbas.put("distribuidor", valorNode.isNumber() ? valorNode.doubleValue() : 0.0);
                    continue;
                }
                if (nomeNormalizado.contains("VRCEXT") || nomeNormalizado.contains("VRC")) {
                    verbas.put("vrcExt", valorNode.isNumber() ? valorNode.doubleValue() : 0.0);
                    continue;
                }
                if (!nomeNormalizado.contains("ADICIONAL") && !nomeNormalizado.contains("VALORADICIONAL")) continue;
                verbas.put("valorAdicional", valorNode.isNumber() ? valorNode.doubleValue() : 0.0);
            }
            if (!verbas.has("emolumentos")) {
                verbas.put("emolumentos", 0.0);
            }
            if (!verbas.has("vrcExt")) {
                verbas.put("vrcExt", 0.0);
            }
            if (!verbas.has("funrejus")) {
                verbas.put("funrejus", 0.0);
            }
            if (!verbas.has("iss")) {
                verbas.put("iss", 0.0);
            }
            if (!verbas.has("fundep")) {
                verbas.put("fundep", 0.0);
            }
            if (!verbas.has("funarpen")) {
                verbas.put("funarpen", 0.0);
            }
            if (!verbas.has("distribuidor")) {
                verbas.put("distribuidor", 0.0);
            }
            if (!verbas.has("valorAdicional")) {
                verbas.put("valorAdicional", 0.0);
            }
            if ((seloNode = root.get("selo")) != null && seloNode.isObject()) {
                ((ObjectNode)seloNode).set("verbas", (JsonNode)verbas);
            } else {
                root.set("verbas", (JsonNode)verbas);
            }
            LOGGER.info("[SeloJsonSanitizerNotas] ListaVerbas convertida para objeto verbas");
        }
        return root;
    }

    private static JsonNode converterListaPropriedades(JsonNode root) {
        if (!root.isObject()) {
            return root;
        }
        ObjectNode obj = (ObjectNode)root;
        if (obj.has("selo") && obj.get("selo").isObject()) {
            return SeloJsonSanitizerNotas.converterListaPropriedadesEmSelo(obj, (ObjectNode)obj.get("selo"));
        }
        return SeloJsonSanitizerNotas.converterListaPropriedadesEmSelo(obj, obj);
    }

    private static JsonNode converterListaPropriedadesEmSelo(ObjectNode root, ObjectNode seloObj) {
        if (seloObj.has("ListaPropriedades") && seloObj.get("ListaPropriedades").isArray()) {
            JsonNode seloNode;
            ArrayNode listaProp = (ArrayNode)seloObj.get("ListaPropriedades");
            ObjectNode props = M.createObjectNode();
            for (int i = 0; i < listaProp.size(); ++i) {
                String valorProp;
                JsonNode item = listaProp.get(i);
                if (!item.isObject()) continue;
                String nomeProp = item.has("NomePropriedade") ? item.get("NomePropriedade").asText() : null;
                String string = valorProp = item.has("ValorPropriedade") && !item.get("ValorPropriedade").isNull() ? item.get("ValorPropriedade").asText() : null;
                if (nomeProp == null || valorProp == null) continue;
                if (nomeProp.contains(".")) {
                    ObjectNode objPai;
                    String[] partes = nomeProp.split("\\.", 2);
                    String nomeObj = partes[0];
                    String nomeCampo = partes[1];
                    if (props.has(nomeObj) && props.get(nomeObj).isObject()) {
                        objPai = (ObjectNode)props.get(nomeObj);
                    } else {
                        objPai = M.createObjectNode();
                        props.set(nomeObj, (JsonNode)objPai);
                    }
                    objPai.put(nomeCampo, valorProp);
                    continue;
                }
                props.put(nomeProp, valorProp);
            }
            if (root.has("selo") && root.get("selo").isObject() && (seloNode = root.get("selo")) != null && seloNode.isObject()) {
                ((ObjectNode)seloNode).set("propriedades_sanitizadas", (JsonNode)props);
            }
            LOGGER.fine("[SeloJsonSanitizerNotas] ListaPropriedades convertida para propriedades_sanitizadas (original mantido)");
        }
        return root;
    }

    public static String sanitizar(String jsonString) throws Exception {
        JsonNode root = M.readTree(jsonString);
        root = SeloJsonSanitizerNotas.corrigirDatasNoJson(root);
        root = SeloJsonSanitizerNotas.corrigirPlaceholders(root);
        root = SeloJsonSanitizerNotas.corrigirErrosDigitacao(root);
        root = SeloJsonSanitizerNotas.converterListaVerbasParaVerbas(root);
        root = SeloJsonSanitizerNotas.converterListaPropriedades(root);
        root = SeloJsonSanitizerNotas.corrigirSeloRetificado(root);
        return M.writeValueAsString((Object)root);
    }

    private static JsonNode corrigirPlaceholders(JsonNode root) {
        if (root.isObject()) {
            String[] camposParaVerificar;
            ObjectNode obj = (ObjectNode)root;
            for (String campo : camposParaVerificar = new String[]{"documentoResponsavel", "codigoEmpresa", "codigoOficio"}) {
                String valor;
                if (!obj.has(campo) || (valor = obj.get(campo).asText()) != null && !valor.isEmpty() && !valor.equals("|campo|") && !valor.equals("0")) continue;
                obj.putNull(campo);
            }
            Iterator fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry field = (Map.Entry)fields.next();
                JsonNode value = (JsonNode)field.getValue();
                if (value.isObject()) {
                    SeloJsonSanitizerNotas.corrigirPlaceholders(value);
                    continue;
                }
                if (!value.isArray()) continue;
                for (JsonNode item : value) {
                    if (!item.isObject()) continue;
                    SeloJsonSanitizerNotas.corrigirPlaceholders(item);
                }
            }
        }
        return root;
    }

    private static JsonNode corrigirDatasNoJson(JsonNode root) {
        if (root.isObject()) {
            String[] camposData;
            ObjectNode obj = (ObjectNode)root;
            for (String campo : camposData = new String[]{"dataSeloEmitido", "dataAtoPraticado", "dataAto", "dataEmissao"}) {
                String[] partes;
                String dataOriginal;
                if (!obj.has(campo) || obj.get(campo).isNull() || (dataOriginal = obj.get(campo).asText()) == null || dataOriginal.isEmpty() || dataOriginal.equals("null") || dataOriginal.contains("/") || !dataOriginal.contains("-") || (partes = dataOriginal.split("T")).length <= 0) continue;
                String dataSemHora = partes[0];
                try {
                    LocalDate date = LocalDate.parse(dataSemHora);
                    obj.put(campo, date.format(DATE_FORMAT));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            Iterator fields = obj.fields();
            while (fields.hasNext()) {
                Map.Entry field = (Map.Entry)fields.next();
                JsonNode value = (JsonNode)field.getValue();
                if (value.isObject()) {
                    SeloJsonSanitizerNotas.corrigirDatasNoJson(value);
                    continue;
                }
                if (!value.isArray()) continue;
                for (JsonNode item : value) {
                    if (!item.isObject()) continue;
                    SeloJsonSanitizerNotas.corrigirDatasNoJson(item);
                }
            }
        }
        return root;
    }

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  SeloJsonSanitizerNotas - Sanitiza\u00e7\u00e3o de Selos");
        System.out.println("===========================================");
        System.out.println("");
        try {
            String dtVersao = null;
            if (args.length > 0) {
                dtVersao = args[0];
            }
            System.out.println("[1/3] Buscando selos pendentes...");
            List<String> selosPendentes = SeloJsonSanitizerNotas.buscarSelosPendentes(dtVersao);
            if (selosPendentes.isEmpty()) {
                System.out.println("Nenhum selo pendente encontrado para a versao " + dtVersao);
                return;
            }
            System.out.println("   Total de selos pendentes: " + selosPendentes.size());
            System.out.println("");
            System.out.println("[2/3] Processando selos...");
            int sucessos = 0;
            int erros = 0;
            for (int i = 0; i < selosPendentes.size(); ++i) {
                String sello = selosPendentes.get(i);
                System.out.print("   Processando " + (i + 1) + "/" + selosPendentes.size() + ": " + sello + " ... ");
                boolean resultado = SeloJsonSanitizerNotas.sanitizarESalvar(sello);
                if (resultado) {
                    System.out.println("OK");
                    ++sucessos;
                    continue;
                }
                System.out.println("ERRO");
                ++erros;
            }
            System.out.println("");
            System.out.println("[3/3] Resumo:");
            System.out.println("   OK: " + sucessos);
            System.out.println("   Erros: " + erros);
            System.out.println("   Total: " + selosPendentes.size());
            System.out.println("");
            if (erros > 0) {
                System.out.println("Alguns selos nao puderam ser sanitizados. Verifique os logs.");
            } else {
                System.out.println("Sanitizacao concluida com sucesso!");
            }
        }
        catch (Exception e) {
            System.err.println("Erro durante a sanitizacao: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<String> buscarSelosPendentes(String dtVersao) {
        ArrayList<String> selos = new ArrayList<String>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionFactory.getConnection();
            String sql = dtVersao != null && !dtVersao.isEmpty() ? "SELECT DISTINCT s.selo_sel FROM selos s INNER JOIN selados sel ON sel.SELO = s.selo_sel INNER JOIN parametros p ON p.CODIGO_PAR = 1 WHERE sel.DATAENVIO >= p.DTVERSAO_FUNARPEN AND (sel.STATUS IS NULL OR sel.STATUS != 'SUCESSO') ORDER BY sel.DATAENVIO" : "SELECT DISTINCT s.selo_sel FROM selos s INNER JOIN selados sel ON sel.SELO = s.selo_sel INNER JOIN parametros p ON p.CODIGO_PAR = 1 WHERE sel.DATAENVIO >= p.DTVERSAO_FUNARPEN AND (sel.STATUS IS NULL OR sel.STATUS != 'SUCESSO') ORDER BY sel.DATAENVIO";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                selos.add(rs.getString("selo_sel"));
            }
        }
        catch (Exception e) {
            LOGGER.severe("[SeloJsonSanitizerNotas] Erro ao buscar selos pendentes: " + e.getMessage());
        }
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException sQLException) {}
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException sQLException) {}
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException sQLException) {}
        }
        return selos;
    }
    private static List<String> splitRootObjects(String json) {
        ArrayList<String> result = new ArrayList<String>();
        if (json == null || json.isEmpty()) return result;
        int level = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (level == 0) start = i;
                level++;
            } else if (c == '}') {
                level--;
                if (level == 0 && start != -1) {
                    result.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        if (level > 0 && start != -1) {
            String truncated = json.substring(start);
            for (int i = 0; i < level; i++) {
                truncated = truncated + "}";
            }
            result.add(truncated);
        }
        return result;
    }

    private static List<Map<String, Object>> buscarPartesNot1PorRegistro(String registro, Connection conn) {
        ArrayList<Map<String, Object>> partes = new ArrayList<Map<String, Object>>();
        if (registro == null || registro.isEmpty()) return partes;
        String sql = "SELECT n.NOME_NOT1, n.CPF_NOT1, n.QUALIF_NOT1 FROM not_1 n WHERE n.REGISTRO_NOT1 = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, registro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, Object> p = new HashMap<String, Object>();
                    p.put("NOME_NOT1", rs.getString("NOME_NOT1"));
                    p.put("CPF_NOT1", rs.getString("CPF_NOT1"));
                    p.put("QUALIF_NOT1", rs.getString("QUALIF_NOT1"));
                    partes.add(p);
                }
            }
        } catch (SQLException e) {
            LOGGER.warning("[SeloJsonSanitizerNotas] Erro buscarPartesNot1: " + e.getMessage());
        }
        return partes;
    }
}
