package com.selador.web.servlets;

import com.selador.service.ApontamentoService;
import com.selador.service.SeloService;
import com.selador.service.SelagemService;
import com.selador.service.ValidationService;
import com.selador.service.ConfigService;
import com.selador.service.ReportService;
import com.selador.util.JsonUtil;
import com.selador.util.LogUtil;
import com.selador.web.dto.*;
import com.selador.model.FiltroBusca;
import com.selador.enums.TipoOperacao;
import com.selador.util.ExceptionHandlerUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * Servlet base com métodos utilitários compartilhados
 */
public abstract class BaseServlet extends HttpServlet {
    
    protected ApontamentoService apontamentoService;
    protected SeloService seloService;
    protected SelagemService selagemService;
    protected ValidationService validationService;
    protected ConfigService configService;
    protected ReportService reportService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Inicializar serviços
        try {
            this.apontamentoService = ApontamentoService.getInstance();
        } catch (Exception e) {
            this.apontamentoService = null;
        }
        
        try {
            this.seloService = SeloService.getInstance();
        } catch (Exception e) {
            this.seloService = null;
        }
        
        try {
            this.selagemService = SelagemService.getInstance();
        } catch (Exception e) {
            this.selagemService = null;
        }
        
        try {
            this.validationService = ValidationService.getInstance();
        } catch (Exception e) {
            this.validationService = null;
        }
        
        try {
            this.configService = ConfigService.getInstance();
        } catch (Exception e) {
            this.configService = null;
        }
        
        try {
            this.reportService = ReportService.getInstance();
        } catch (Exception e) {
            this.reportService = null;
        }
    }
    
    /**
     * Processar requisição JSON
     */
    protected String getRequestBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining());
    }
    
    /**
     * Enviar resposta JSON
     */
    protected void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String json = JsonUtil.toJson(data);
        response.getWriter().write(json);
    }
    
    /**
     * Enviar resposta de erro
     */
    protected void sendErrorResponse(HttpServletResponse response, int statusCode, String message) 
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("sucesso", false);
        errorResponse.put("mensagem", message);
        errorResponse.put("codigo", statusCode);
        
        sendJsonResponse(response, errorResponse);
    }
    
    /**
     * Enviar resposta de sucesso
     */
    protected void sendSuccessResponse(HttpServletResponse response, Object data) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Converter para JSON
        String json = JsonUtil.toJson(data);  // ← Como está sendo convertido?
        
        System.out.println("📤 [BASE SERVLET] Enviando JSON: " + json);  // ← ADICIONE ESTE LOG!
        
        response.getWriter().write(json);
    }
    
    /**
     * Extrair parâmetros da URL
     */
    protected Map<String, String> extractPathParameters(String pathInfo) {
        Map<String, String> params = new HashMap<>();
        if (pathInfo == null || pathInfo.equals("/")) {
            return params;
        }
        
        String[] parts = pathInfo.substring(1).split("/");
        for (int i = 0; i < parts.length; i += 2) {
            if (i + 1 < parts.length) {
                params.put(parts[i], parts[i + 1]);
            }
        }
        
        return params;
    }
    
    /**
     * Logar requisição
     */
    protected void logRequest(HttpServletRequest request, String operation, long duration) {
        try {
            String method = request.getMethod();
            String endpoint = request.getRequestURI();
            String params = request.getQueryString() != null ? request.getQueryString() : "";
            
            // Tenta usar LogUtil se disponível
            if (LogUtil.class != null) {
                try {
                    java.lang.reflect.Method methodLog = LogUtil.class.getMethod("logRequisicao", 
                        String.class, String.class, String.class, int.class, long.class);
                    methodLog.invoke(null, method, endpoint, params, HttpServletResponse.SC_OK, duration);
                } catch (NoSuchMethodException e) {
                    // Método não existe, usa log simples
                    System.out.println(String.format("[LOG] %s %s?%s - %dms", 
                        method, endpoint, params, duration));
                }
            }
        } catch (Exception e) {
            // Fallback para console
            System.out.println("Log de requisição: " + operation + " - " + duration + "ms");
        }
    }
    
    /**
     * Método auxiliar para converter string para Date
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        
        SimpleDateFormat[] formats = {
            new SimpleDateFormat("dd/MM/yyyy"),
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("ddMMyyyy")
        };
        
        for (SimpleDateFormat sdf : formats) {
            try {
                return sdf.parse(dateStr);
            } catch (Exception e) {
                continue;
            }
        }
        
        return null;
    }
    
    /**
     * Converter FiltroBuscaDTO para FiltroBusca (model) - VERSÃO CORRIGIDA
     */
    protected FiltroBusca convertToFiltroBusca(FiltroBuscaDTO dto) throws Exception {
        FiltroBusca filtro = new FiltroBusca();
        
        try {
            // Usar reflection para configurar os campos corretamente
            
            // Tipo de operação
            if (dto.getTipoOperacao() != null) {
                try {
                    // Tenta como String
                    java.lang.reflect.Method setTipoOperacao = filtro.getClass()
                        .getMethod("setTipoOperacao", String.class);
                    setTipoOperacao.invoke(filtro, dto.getTipoOperacao());
                } catch (NoSuchMethodException e1) {
                    // Tenta como enum
                    try {
                        TipoOperacao tipo = TipoOperacao.valueOf(dto.getTipoOperacao());
                        java.lang.reflect.Method setTipoOperacaoEnum = filtro.getClass()
                            .getMethod("setTipoOperacao", TipoOperacao.class);
                        setTipoOperacaoEnum.invoke(filtro, tipo);
                    } catch (Exception e2) {
                        // Ignora se não conseguir
                    }
                }
            }
            
            // Datas
            if (dto.getDataInicial() != null) {
                try {
                    // Tenta como String primeiro
                    java.lang.reflect.Method setDataInicial = filtro.getClass()
                        .getMethod("setDataInicial", String.class);
                    setDataInicial.invoke(filtro, dto.getDataInicial());
                } catch (NoSuchMethodException e1) {
                    try {
                        // Tenta nome alternativo
                        java.lang.reflect.Method setDataInicialStr = filtro.getClass()
                            .getMethod("setDataInicialStr", String.class);
                        setDataInicialStr.invoke(filtro, dto.getDataInicial());
                    } catch (NoSuchMethodException e2) {
                        // Tenta como Date
                        try {
                            Date data = parseDate(dto.getDataInicial());
                            if (data != null) {
                                java.lang.reflect.Method setDataInicialDate = filtro.getClass()
                                    .getMethod("setDataInicial", Date.class);
                                setDataInicialDate.invoke(filtro, data);
                            }
                        } catch (Exception e3) {
                            // Ignora
                        }
                    }
                }
            }
            
            if (dto.getDataFinal() != null) {
                try {
                    // Tenta como String primeiro
                    java.lang.reflect.Method setDataFinal = filtro.getClass()
                        .getMethod("setDataFinal", String.class);
                    setDataFinal.invoke(filtro, dto.getDataFinal());
                } catch (NoSuchMethodException e1) {
                    try {
                        // Tenta nome alternativo
                        java.lang.reflect.Method setDataFinalStr = filtro.getClass()
                            .getMethod("setDataFinalStr", String.class);
                        setDataFinalStr.invoke(filtro, dto.getDataFinal());
                    } catch (NoSuchMethodException e2) {
                        // Tenta como Date
                        try {
                            Date data = parseDate(dto.getDataFinal());
                            if (data != null) {
                                java.lang.reflect.Method setDataFinalDate = filtro.getClass()
                                    .getMethod("setDataFinal", Date.class);
                                setDataFinalDate.invoke(filtro, data);
                            }
                        } catch (Exception e3) {
                            // Ignora
                        }
                    }
                }
            }
            
            // Status
            if (dto.getStatus() != null) {
                try {
                    // Tenta como String
                    java.lang.reflect.Method setStatus = filtro.getClass()
                        .getMethod("setStatus", String.class);
                    setStatus.invoke(filtro, dto.getStatus());
                } catch (NoSuchMethodException e1) {
                    // Tenta como List
                    try {
                        List<String> statusList = new java.util.ArrayList<>();
                        statusList.add(dto.getStatus());
                        
                        java.lang.reflect.Method setStatusList = filtro.getClass()
                            .getMethod("setStatus", List.class);
                        setStatusList.invoke(filtro, statusList);
                    } catch (Exception e2) {
                        // Ignora
                    }
                }
            }
            
            // Devedor
            if (dto.getDevedor() != null) {
                try {
                    java.lang.reflect.Method setDevedor = filtro.getClass()
                        .getMethod("setDevedor", String.class);
                    setDevedor.invoke(filtro, dto.getDevedor());
                } catch (NoSuchMethodException e) {
                    // Tenta outros nomes
                    try {
                        java.lang.reflect.Method setNomeDevedor = filtro.getClass()
                            .getMethod("setNomeDevedor", String.class);
                        setNomeDevedor.invoke(filtro, dto.getDevedor());
                    } catch (Exception e2) {
                        // Ignora
                    }
                }
            }
            
            // CPF/CNPJ
            if (dto.getCpfCnpj() != null) {
                try {
                    java.lang.reflect.Method setCpfCnpj = filtro.getClass()
                        .getMethod("setCpfCnpj", String.class);
                    setCpfCnpj.invoke(filtro, dto.getCpfCnpj());
                } catch (Exception e) {
                    // Ignora
                }
            }
            
            // Paginação
            if (dto.getPagina() != null) {
                try {
                    java.lang.reflect.Method setPagina = filtro.getClass()
                        .getMethod("setPagina", Integer.class);
                    setPagina.invoke(filtro, dto.getPagina());
                } catch (Exception e) {
                    // Ignora
                }
            }
            
            if (dto.getTamanhoPagina() != null) {
                try {
                    // Tenta diferentes nomes de método
                    String[] methodNames = {"setTamanhoPagina", "setPageSize", "setLimit"};
                    for (String methodName : methodNames) {
                        try {
                            java.lang.reflect.Method setTamanho = filtro.getClass()
                                .getMethod(methodName, Integer.class);
                            setTamanho.invoke(filtro, dto.getTamanhoPagina());
                            break;
                        } catch (NoSuchMethodException e) {
                            continue;
                        }
                    }
                } catch (Exception e) {
                    // Ignora
                }
            }
            
        } catch (Exception e) {
            System.err.println("Aviso ao converter filtro: " + e.getMessage());
        }
        
        return filtro;
    }
    
    /**
     * Validar parâmetros obrigatórios
     */
    protected List<String> validateRequiredParams(Map<String, String> params, String... required) {
        List<String> errors = new java.util.ArrayList<>();
        
        for (String param : required) {
            if (!params.containsKey(param) || params.get(param) == null || params.get(param).trim().isEmpty()) {
                errors.add("Parâmetro obrigatório ausente: " + param);
            }
        }
        
        return errors;
    }

    /**
     * Trata exceção e envia resposta apropriada
     */
    protected void handleException(HttpServletResponse response, Exception exception) 
            throws IOException {
        
        // Determinar código HTTP
        int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        
        if (exception instanceof IllegalArgumentException || 
            exception instanceof NullPointerException) {
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
        } else if (exception instanceof java.io.FileNotFoundException) {
            statusCode = HttpServletResponse.SC_NOT_FOUND;
        }
        
        // Logar a exceção
        try {
            if (ExceptionHandlerUtil.class != null) {
                java.lang.reflect.Method logMethod = ExceptionHandlerUtil.class
                    .getMethod("logException", Exception.class, String.class);
                logMethod.invoke(null, exception, "Servlet Error");
            }
        } catch (Exception e) {
            // Fallback para console
            System.err.println("Exception in servlet: " + exception.getMessage());
            exception.printStackTrace();
        }
        
        // Enviar resposta de erro
        sendErrorResponse(response, statusCode, 
            "Erro: " + exception.getMessage() + 
            " (Tipo: " + exception.getClass().getSimpleName() + ")");
    }
}