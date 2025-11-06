package com.databaseai.security;

import com.databaseai.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter
 * 
 * What does this filter do?
 * - Intercepts every HTTP request
 * - Extracts JWT token from Authorization header
 * - Validates token
 * - Sets authentication in Spring Security context
 * 
 * How it works:
 * 1. Request comes in → Filter intercepts
 * 2. Extract token from "Authorization: Bearer <token>" header
 * 3. Validate token (signature, expiration)
 * 4. Extract user info (username, role) from token
 * 5. Set authentication in SecurityContext
 * 6. Request continues to controller
 * 
 * Why OncePerRequestFilter?
 * - Ensures filter runs only once per request
 * - Prevents duplicate processing
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Get Authorization header
        final String authHeader = request.getHeader("Authorization");

        // Check if header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token, continue filter chain (will be rejected by SecurityConfig if endpoint requires auth)
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token (remove "Bearer " prefix)
            final String token = authHeader.substring(7);

            // Extract username from token
            final String username = jwtUtil.extractUsername(token);

            // Validate token
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(token)) {
                    // Extract role from token
                    String role = jwtUtil.extractRole(token);

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );

                    // Set details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token, continue filter chain (will be rejected if endpoint requires auth)
            logger.error("JWT validation failed", e);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}

