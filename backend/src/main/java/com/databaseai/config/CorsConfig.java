package com.databaseai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS Configuration
 * 
 * CORS = Cross-Origin Resource Sharing
 * 
 * Problem: By default, browsers block requests from one domain (frontend) 
 * to another domain (backend) for security.
 * 
 * Solution: This configuration allows our React frontend (localhost:3000)
 * to make requests to our Spring Boot backend (localhost:8080).
 * 
 * Without this, you'll get CORS errors in the browser console.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow requests from these origins (where frontend runs)
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:5173"); // Vite default port
        
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

