package com.databaseai.controller;

import com.databaseai.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Embedding Controller
 * 
 * REST API endpoints for testing embedding generation.
 */
@RestController
@RequestMapping("/embeddings")
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;

    /**
     * POST /api/embeddings/generate
     * 
     * Generate embedding for a single text
     * 
     * Request body:
     * {
     *   "text": "Hello world"
     * }
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateEmbedding(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            
            if (text == null || text.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Text is required"));
            }

            if (!embeddingService.isApiKeyConfigured()) {
                return ResponseEntity.status(500)
                        .body(Map.of("error", "OpenAI API key is not configured. Please set OPENAI_API_KEY environment variable or openai.api.key in application.properties"));
            }

            List<Double> embedding = embeddingService.generateEmbedding(text);
            
            Map<String, Object> response = new HashMap<>();
            response.put("text", text);
            response.put("embedding", embedding);
            response.put("dimension", embedding.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/embeddings/generate-batch
     * 
     * Generate embeddings for multiple texts
     * 
     * Request body:
     * {
     *   "texts": ["Hello", "World", "Test"]
     * }
     */
    @PostMapping("/generate-batch")
    public ResponseEntity<?> generateEmbeddings(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> texts = request.get("texts");
            
            if (texts == null || texts.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Texts list is required"));
            }

            if (!embeddingService.isApiKeyConfigured()) {
                return ResponseEntity.status(500)
                        .body(Map.of("error", "OpenAI API key is not configured"));
            }

            List<List<Double>> embeddings = embeddingService.generateEmbeddings(texts);
            
            Map<String, Object> response = new HashMap<>();
            response.put("texts", texts);
            response.put("embeddings", embeddings);
            response.put("count", embeddings.size());
            response.put("dimension", embeddings.isEmpty() ? 0 : embeddings.get(0).size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/embeddings/status
     * 
     * Check if API key is configured
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("apiKeyConfigured", embeddingService.isApiKeyConfigured());
        response.put("message", embeddingService.isApiKeyConfigured() 
                ? "OpenAI API key is configured" 
                : "OpenAI API key is not configured");
        return ResponseEntity.ok(response);
    }
}

