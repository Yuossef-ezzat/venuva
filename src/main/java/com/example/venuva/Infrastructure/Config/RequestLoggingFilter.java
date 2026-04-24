package com.example.venuva.Infrastructure.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP request/response logging filter.
 * Logs HTTP method, URI, and response status code for every request.
 * Runs once per request to provide unified logging across the application.
 */
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        try {
            // Process the request
            filterChain.doFilter(request, response);
        } finally {
            // Log after response is generated
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            log.info("[REQUEST] {} {} → {} ({}ms)", method, uri, status, duration);
        }
    }
}
