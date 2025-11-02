package com.databaseai.controller;

import com.databaseai.service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * LLM Controller
 * 
 * REST API endpoints for testing GPT/LLM calls.
 */
@RestController
@RequestMapping("/llm")
public class LLMController {

    @Autowired
    private LLMService llmService;

    /**
     * POST /api/llm/generate
     * 
     * Generate text using GPT
     * 
     * Request body:
     * {
     *   "prompt": "What is artificial intelligence?"
     * }
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateText(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            
            if (prompt == null || prompt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Prompt is required"));
            }

            if (!llmService.isApiKeyConfigured()) {
                return ResponseEntity.status(500)
                        .body(Map.of("error", "OpenAI API key is not configured. Please set OPENAI_API_KEY environment variable or openai.api.key in application.properties"));
            }

            String response = llmService.generateText(prompt);
            
            Map<String, Object> result = new HashMap<>();
            result.put("prompt", prompt);
            result.put("response", response);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/llm/generate-sql
     * 
     * Generate SQL from natural language (preview)
     * 
     * Request body:
     * {
     *   "query": "Show me all users"
     * }
     */
    @PostMapping("/generate-sql")
    public ResponseEntity<?> generateSQL(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            
            if (query == null || query.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Query is required"));
            }

            if (!llmService.isApiKeyConfigured()) {
                return ResponseEntity.status(500)
                        .body(Map.of("error", "OpenAI API key is not configured"));
            }

            String sql = llmService.generateSQL(query);
            
            Map<String, Object> result = new HashMap<>();
            result.put("naturalLanguage", query);
            result.put("sql", sql);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/llm/status
     * 
     * Check if API key is configured
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("apiKeyConfigured", llmService.isApiKeyConfigured());
        response.put("message", llmService.isApiKeyConfigured() 
                ? "OpenAI API key is configured" 
                : "OpenAI API key is not configured");
        return ResponseEntity.ok(response);
    }
}

