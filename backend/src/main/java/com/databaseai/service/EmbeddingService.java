package com.databaseai.service;

import com.databaseai.config.OpenAIConfig;
import com.databaseai.dto.EmbeddingRequest;
import com.databaseai.dto.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Embedding Service
 * 
 * Generates embeddings (vector representations) from text using OpenAI API.
 * 
 * What are Embeddings?
 * - Embeddings convert text into a list of numbers (vector)
 * - Example: "Hello" → [0.1, 0.5, -0.3, ..., 0.9] (1536 numbers)
 * - Similar texts have similar vectors
 * - Used for: search, similarity matching, clustering
 * 
 * Why Embeddings?
 * - Convert schema descriptions to vectors
 * - Store in pgvector (Chunk 4)
 * - Find similar schemas for RAG (Chunk 5)
 */
@Service
public class EmbeddingService {

    @Autowired
    private WebClient openAiWebClient;

    @Autowired
    private OpenAIConfig openAIConfig;

    /**
     * Generate embedding for a single text
     * 
     * @param text Text to convert to embedding
     * @return List of numbers (embedding vector) - typically 1536 numbers
     */
    public List<Double> generateEmbedding(String text) {
        return generateEmbeddingWithRetry(text, 3);
    }

    /**
     * Generate embedding with retry logic (handles rate limits)
     */
    private List<Double> generateEmbeddingWithRetry(String text, int maxRetries) {
        EmbeddingRequest request = new EmbeddingRequest(List.of(text));
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                EmbeddingResponse response = openAiWebClient
                        .post()
                        .uri("/embeddings")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(EmbeddingResponse.class)
                        .block();

                if (response != null && 
                    response.getData() != null && 
                    !response.getData().isEmpty()) {
                    return response.getData().get(0).getEmbedding();
                }
                
                throw new RuntimeException("Failed to generate embedding: Empty response");
                
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == 401) {
                    throw new RuntimeException("OpenAI API key is invalid or not configured", e);
                }
                
                // Handle rate limit (429) - retry with exponential backoff
                if (e.getStatusCode().value() == 429) {
                    if (attempt < maxRetries - 1) {
                        // Wait before retry: 2^attempt seconds (2s, 4s, 8s)
                        int waitSeconds = (int) Math.pow(2, attempt + 1);
                        try {
                            Thread.sleep(waitSeconds * 1000L);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Interrupted while waiting for rate limit", ie);
                        }
                        continue; // Retry
                    } else {
                        throw new RuntimeException("Rate limit exceeded. Please wait a minute and try again.", e);
                    }
                }
                
                throw new RuntimeException("Failed to generate embedding: " + e.getMessage(), e);
            }
        }
        
        throw new RuntimeException("Failed to generate embedding after " + maxRetries + " attempts");
    }

    /**
     * Generate embeddings for multiple texts at once
     * 
     * @param texts List of texts to convert
     * @return List of embeddings (one per text)
     */
    public List<List<Double>> generateEmbeddings(List<String> texts) {
        EmbeddingRequest request = new EmbeddingRequest(texts);
        
        try {
            EmbeddingResponse response = openAiWebClient
                    .post()
                    .uri("/embeddings")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(EmbeddingResponse.class)
                    .block();

            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .map(EmbeddingResponse.EmbeddingData::getEmbedding)
                        .collect(Collectors.toList());
            }
            
            throw new RuntimeException("Failed to generate embeddings: Empty response");
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                throw new RuntimeException("OpenAI API key is invalid or not configured", e);
            }
            throw new RuntimeException("Failed to generate embeddings: " + e.getMessage(), e);
        }
    }

    /**
     * Check if API key is configured
     */
    public boolean isApiKeyConfigured() {
        return openAIConfig.isApiKeyConfigured();
    }
}

