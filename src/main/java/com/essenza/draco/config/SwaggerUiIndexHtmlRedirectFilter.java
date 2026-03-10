package com.essenza.draco.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SwaggerUiIndexHtmlRedirectFilter extends OncePerRequestFilter {

    private static final String MISROUTED_PREFIX = "/swagger-ui/index.html/";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (requestUri != null && requestUri.startsWith(MISROUTED_PREFIX)) {
            String contextPath = request.getContextPath();
            String suffix = requestUri.substring(MISROUTED_PREFIX.length());

            String redirectPath;
            if (suffix.isBlank()) {
                redirectPath = "/swagger-ui/index.html";
            } else {
                redirectPath = "/swagger-ui/" + suffix;
            }

            String query = request.getQueryString();
            String target = contextPath + redirectPath + (query != null ? ("?" + query) : "");
            response.sendRedirect(target);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
