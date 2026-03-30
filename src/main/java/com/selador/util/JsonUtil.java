package com.selador.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.selador.enums.TipoOperacao;
import com.selador.enums.FormatoData;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Utilitário para serialização/deserialização JSON usando GSON
 */
public class JsonUtil {
    
    private static final Gson GSON;
    private static final Gson GSON_PRETTY;
    
    static {
        // Gson padrão - SIMPLIFICADO
        GsonBuilder builder = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .disableHtmlEscaping();
        
        // Registrar serializadores apenas se realmente existirem
        try {
            builder.registerTypeAdapter(Date.class, new DateSerializer());
        } catch (Exception e) {
            // Se falhar, usar serialização padrão
            System.err.println("Aviso: DateSerializer não disponível, usando padrão");
        }
        
        try {
            builder.registerTypeAdapter(TipoOperacao.class, new TipoOperacaoSerializer());
        } catch (Exception e) {
            // Se TipoOperacao não existir, pular
        }
        
        try {
            builder.registerTypeAdapter(FormatoData.class, new FormatoDataSerializer());
        } catch (Exception e) {
            // Se FormatoData não existir, pular
        }
        
        GSON = builder.create();
        GSON_PRETTY = builder.setPrettyPrinting().create();
    }
    
    private JsonUtil() {
        // Classe utilitária - não instanciável
    }
    
    /**
     * Converte objeto para JSON string
     */
    public static String toJson(Object objeto) {
        if (objeto == null) {
            return "null";
        }
        return GSON.toJson(objeto);
    }
    
    /**
     * Converte objeto para JSON formatado (pretty print)
     */
    public static String toJsonPretty(Object objeto) {
        if (objeto == null) {
            return "null";
        }
        return GSON_PRETTY.toJson(objeto);
    }
    
    /**
     * Converte JSON string para objeto do tipo especificado
     */
    public static <T> T fromJson(String json, Class<T> classe) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return GSON.fromJson(json.trim(), classe);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Converte JSON string para List do tipo especificado
     */
    public static <T> List<T> fromJsonList(String json, Class<T> classe) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            Type type = TypeToken.getParameterized(List.class, classe).getType();
            return GSON.fromJson(json.trim(), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Converte JSON string para Map
     */
    public static Map<String, Object> fromJsonMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            return GSON.fromJson(json.trim(), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Converte JSON string para Map com tipo específico de valor
     */
    public static <T> Map<String, T> fromJsonMap(String json, Class<T> valorClasse) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            Type type = TypeToken.getParameterized(
                Map.class, String.class, valorClasse).getType();
            return GSON.fromJson(json.trim(), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Valida se string é um JSON válido
     */
    public static boolean isJsonValido(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        
        try {
            GSON.fromJson(json.trim(), Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Converte objeto para JSON com campos nulos incluídos
     */
    public static String toJsonIncluirNulos(Object objeto) {
        if (objeto == null) {
            return "null";
        }
        
        Gson gsonComNulos = new GsonBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .create();
            
        return gsonComNulos.toJson(objeto);
    }
    
    /**
     * Serializador customizado para Date - VERSÃO SIMPLIFICADA
     */
    private static class DateSerializer implements com.google.gson.JsonSerializer<Date>,
                                                  com.google.gson.JsonDeserializer<Date> {
        
        @Override
        public com.google.gson.JsonElement serialize(Date src, Type typeOfSrc,
                com.google.gson.JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            // Delegate para serialização padrão do Gson
            return new com.google.gson.JsonPrimitive(src.getTime());
        }
        
        @Override
        public Date deserialize(com.google.gson.JsonElement json, Type typeOfT,
                com.google.gson.JsonDeserializationContext context) {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            
            try {
                // Primeiro tentar como timestamp (número)
                if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                    return new Date(json.getAsLong());
                }
                
                // Se não for número, tentar como string
                String dateStr = json.getAsString();
                
                // Se for string vazia, retornar null
                if (dateStr == null || dateStr.trim().isEmpty()) {
                    return null;
                }
                
                // Tentar parse ISO
                try {
                    return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                        .parse(dateStr);
                } catch (java.text.ParseException e1) {
                    // Tentar outro formato comum
                    try {
                        return new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .parse(dateStr);
                    } catch (java.text.ParseException e2) {
                        // Tentar formato brasileiro
                        try {
                            return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                .parse(dateStr);
                        } catch (java.text.ParseException e3) {
                            // Se tudo falhar, retornar null
                            return null;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    /**
     * Serializador customizado para TipoOperacao - VERSÃO SIMPLIFICADA
     */
    private static class TipoOperacaoSerializer 
            implements com.google.gson.JsonSerializer<TipoOperacao>,
                       com.google.gson.JsonDeserializer<TipoOperacao> {
        
        @Override
        public com.google.gson.JsonElement serialize(TipoOperacao src, Type typeOfSrc,
                com.google.gson.JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return new com.google.gson.JsonPrimitive(src.name());
        }
        
        @Override
        public TipoOperacao deserialize(com.google.gson.JsonElement json, Type typeOfT,
                com.google.gson.JsonDeserializationContext context) {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            
            try {
                String nome = json.getAsString();
                return TipoOperacao.valueOf(nome);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    /**
     * Serializador customizado para FormatoData - VERSÃO SIMPLIFICADA
     */
    private static class FormatoDataSerializer 
            implements com.google.gson.JsonSerializer<FormatoData>,
                       com.google.gson.JsonDeserializer<FormatoData> {
        
        @Override
        public com.google.gson.JsonElement serialize(FormatoData src, Type typeOfSrc,
                com.google.gson.JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            return new com.google.gson.JsonPrimitive(src.name());
        }
        
        @Override
        public FormatoData deserialize(com.google.gson.JsonElement json, Type typeOfT,
                com.google.gson.JsonDeserializationContext context) {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            
            try {
                String nome = json.getAsString();
                return FormatoData.valueOf(nome);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    /**
     * Métodos utilitários extras
     */
    public static String toJsonCustom(Object objeto, Gson gson) {
        if (objeto == null) {
            return "null";
        }
        return gson.toJson(objeto);
    }
    
    public static String mapToJson(Map<?, ?> mapa) {
        if (mapa == null) {
            return "null";
        }
        return GSON.toJson(mapa);
    }
    
    public static String listToJson(List<?> lista) {
        if (lista == null) {
            return "null";
        }
        return GSON.toJson(lista);
    }
    
    public static String safeToJson(Object objeto) {
        if (objeto == null) {
            return "null";
        }
        
        try {
            return GSON.toJson(objeto);
        } catch (Exception e) {
            // Fallback para toString() se não puder serializar
            return "\"" + objeto.toString().replace("\"", "\\\"") + "\"";
        }
    }
}