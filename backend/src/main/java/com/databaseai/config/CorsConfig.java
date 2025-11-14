package com.databaseai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration
 * 
 * CORS = Cross-Origin Resource Sharing
 * 
 * Problem: By default, browsers block requests from one domain (frontend) 
 * to another domain (backend) for security.
 * 
 * Solution: This configuration allows our React frontend to make requests to backend.
 * Reads allowed origins from CORS_ALLOWED_ORIGINS environment variable.
 * 
 * Without this, you'll get CORS errors in the browser console.
 */
@Configuration
public class CorsConfig {

    @Value("${spring.web.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Parse allowed origins from environment variable (comma-separated)
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        // Use setAllowedOriginPatterns to support wildcards and credentials together
        config.setAllowedOriginPatterns(origins);
        
        // Allow these HTTP methods
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        // Allow these headers
        config.addAllowedHeader("*");
        
        // Allow cookies/credentials
        config.setAllowCredentials(true);
        
        // Apply this configuration to all paths
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

