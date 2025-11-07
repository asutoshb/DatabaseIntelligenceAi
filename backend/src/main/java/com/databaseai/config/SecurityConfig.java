package com.databaseai.config;

import com.databaseai.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security Configuration
 * 
 * What is Spring Security?
 * - Framework for authentication and authorization
 * - Protects your API endpoints
 * - Handles password encoding, token validation, etc.
 * 
 * What we configure:
 * 1. Password Encoder (bcrypt) - Hashes passwords
 * 2. Security Filter Chain - Defines which endpoints are public/protected
 * 3. JWT Filter - Validates JWT tokens on each request
 * 4. Session Management - Stateless (no server-side sessions)
 * 
 * Public Endpoints (no auth required):
 * - POST /api/auth/register
 * - POST /api/auth/login
 * - GET /api/health
 * 
 * Protected Endpoints (auth required):
 * - All other endpoints require valid JWT token
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password Encoder Bean
     * 
     * Bcrypt is used to hash passwords before storing in database.
     * - One-way function (can't reverse)
     * - Includes salt (prevents rainbow table attacks)
     * - Slow by design (prevents brute force)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication Manager Bean
     * 
     * Used for authentication operations (not needed for JWT, but required by Spring Security)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Security Filter Chain
     * 
     * Defines security rules for the application:
     * - Which endpoints are public (no auth)
     * - Which endpoints require authentication
     * - How to handle CORS
     * - Session management (stateless for JWT)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (Cross-Site Request Forgery) - not needed for stateless JWT
            .csrf(csrf -> csrf.disable())
            
            // Configure CORS (Cross-Origin Resource Sharing)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Session management - STATELESS (no server-side sessions, use JWT instead)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                .requestMatchers("/health").permitAll()
                .requestMatchers("/ws/**", "/topic/**", "/app/**").permitAll()
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            // This filter validates JWT tokens on each request
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration Source
     * 
     * Allows frontend (React) to make requests to backend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

