package com.databaseai.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * 
 * Enhanced health check endpoint for production deployment.
 * Provides detailed status including database connectivity.
 * 
 * @RestController = This class handles HTTP requests
 * @RequestMapping = Base URL path for all methods in this class
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * GET /api/health
     * 
     * Returns a comprehensive health check response.
     * Checks:
     * - Application status
     * - Database connectivity
     * 
     * This is useful for:
     * - Checking if backend is up
     * - Deployment health checks (Railway, Render, etc.)
     * - Monitoring and alerting
     * - Testing the API
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> checks = new HashMap<>();
        
        // Application status
        checks.put("application", Map.of("status", "UP"));
        
        // Database connectivity check
        boolean dbHealthy = checkDatabase();
        checks.put("database", Map.of("status", dbHealthy ? "UP" : "DOWN"));
        
        // Overall status
        boolean allHealthy = dbHealthy;
        String overallStatus = allHealthy ? "UP" : "DOWN";
        
        response.put("status", overallStatus);
        response.put("message", "AI Database Intelligence Platform");
        response.put("timestamp", LocalDateTime.now());
        response.put("checks", checks);
        
        // Return appropriate HTTP status
        if (allHealthy) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(503).body(response);
        }
    }
    
    /**
     * Check database connectivity by executing a simple query
     */
    private boolean checkDatabase() {
        try {
            // Execute a simple query to check database connectivity
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (Exception e) {
            // Database is not accessible
            return false;
        }
    }
}

