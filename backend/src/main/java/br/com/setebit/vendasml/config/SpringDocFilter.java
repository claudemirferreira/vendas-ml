package br.com.setebit.vendasml.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro para garantir que rotas do SpringDoc/Swagger não sejam interceptadas
 * pelo GlobalExceptionHandler. Este filtro deve ter alta prioridade.
 */
@Component
@Order(1)
public class SpringDocFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        
        // Adiciona atributo para identificar rotas do SpringDoc
        if (path != null && (path.contains("/api-docs") || 
                            path.contains("/swagger-ui") || 
                            path.contains("/v3/api-docs") ||
                            path.contains("/swagger") ||
                            path.contains("/openapi"))) {
            request.setAttribute("SPRINGDOC_REQUEST", true);
        }
        
        chain.doFilter(request, response);
    }
}

