package com.selador.web.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro de autenticação (simplificado para integração com Maker 5)
 */
public class AuthFilter implements Filter {
    
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String VALID_API_KEY = "MAKER5_INTEGRATION_KEY"; // Em produção, buscar do config
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Se for requisição de health check ou config, permitir sem autenticação
        String path = httpRequest.getRequestURI();
        if (path.contains("/api/health") || path.contains("/api/config")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar API Key (simplificado)
        String apiKey = httpRequest.getHeader(API_KEY_HEADER);
        
        if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"API Key inválida ou ausente\"}");
            return;
        }
        
        // Adicionar informações de autenticação ao request
        httpRequest.setAttribute("authenticated", true);
        httpRequest.setAttribute("source", "MAKER5");
        
        // Continuar com a cadeia de filtros
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Limpeza do filtro
    }
}