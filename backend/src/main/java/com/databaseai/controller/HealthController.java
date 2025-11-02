package com.databaseai.controller;

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
 * This is a simple REST controller that responds to GET requests.
 * We'll use this to test if our backend is running.
 * 
 * @RestController = This class handles HTTP requests
 * @RequestMapping = Base URL path for all methods in this class
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * GET /api/health
     * 
     * Returns a simple JSON response indicating the server is running.
     * This is useful for:
     * - Checking if backend is up
     * - Deployment health checks
     * - Testing the API
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "AI Database Intelligence Platform is running!");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}

