package com.seprocom.gerencial.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        
        // Tenta obter o parâmetro 'sys' da URL
        String sys = req.getParameter("sys");
        
        // Se não encontrar na URL, tenta no Referer (fallback caso a chamada AJAX perca o param)
        if (sys == null || sys.isEmpty()) {
            String referer = req.getHeader("Referer");
            if (referer != null) {
                if (referer.contains("sys=TTB") || referer.contains("/SpTabel/")) {
                    sys = "TTB";
                } else if (referer.contains("sys=STP") || referer.contains("/STP/")) {
                    sys = "STP";
                }
            }
        }

        // Mapeia para o schema correspondente
        String schema = "sptabel"; // Default
        if ("STP".equalsIgnoreCase(sys)) {
            schema = "spprot";
        } else if ("TTB".equalsIgnoreCase(sys)) {
            schema = "sptabel";
        }

        try {
            TenantContext.setCurrentTenant(schema);
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
