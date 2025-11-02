package com.databaseai.service;

import com.databaseai.config.OpenAIConfig;
import com.databaseai.dto.ChatRequest;
import com.databaseai.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * LLM Service (Large Language Model Service)
 * 
 * Calls OpenAI GPT API for text generation.
 * 
 * What is GPT?
 * - GPT = Generative Pre-trained Transformer
 * - AI model that generates human-like text
 * - Can understand context and respond to prompts
 * 
 * How we'll use it:
 * - Convert natural language to SQL (Chunk 5)
 * - Generate explanations
 * - Answer questions about data
 */
@Service
public class LLMService {

    @Autowired
    private WebClient openAiWebClient;

    @Autowired
    private OpenAIConfig openAIConfig;

    /**
     * Generate text response using GPT
     * 
     * @param prompt User's question or instruction
     * @return GPT's response text
     */
    public String generateText(String prompt) {
        return generateText(prompt, null);
    }

    /**
     * Generate text with system prompt
     * 
     * @param userPrompt User's question
     * @param systemPrompt System instruction (optional)
     * @return GPT's response
     */
    public String generateText(String userPrompt, String systemPrompt) {
        List<ChatRequest.Message> messages = new ArrayList<>();
        
        // Add system message if provided
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(new ChatRequest.Message("system", systemPrompt));
        }
        
        // Add user message
        messages.add(new ChatRequest.Message("user", userPrompt));
        
        return generateTextWithRetry(messages, 3);
    }

    /**
     * Generate text with retry logic (handles rate limits)
     */
    private String generateTextWithRetry(List<ChatRequest.Message> messages, int maxRetries) {
        ChatRequest request = new ChatRequest();
        request.setModel("gpt-3.5-turbo"); // Using cheaper model for now
        request.setMessages(messages);
        request.setTemperature(0.7); // Balanced creativity
        request.setMaxTokens(500);
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                ChatResponse response = openAiWebClient
                        .post()
                        .uri("/chat/completions")
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(ChatResponse.class)
                        .block();

                if (response != null && 
                    response.getChoices() != null && 
                    !response.getChoices().isEmpty()) {
                    return response.getChoices().get(0).getMessage().getContent();
                }
                
                throw new RuntimeException("Failed to generate text: Empty response");
                
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
                
                throw new RuntimeException("Failed to generate text: " + e.getMessage(), e);
            }
        }
        
        throw new RuntimeException("Failed to generate text after " + maxRetries + " attempts");
    }

    /**
     * Generate SQL from natural language (preview for Chunk 5)
     * 
     * This is a simple version. We'll enhance it in Chunk 5 with RAG!
     * 
     * @param naturalLanguageQuery User's question in plain English
     * @return Generated SQL query
     */
    public String generateSQL(String naturalLanguageQuery) {
        String systemPrompt = "You are a SQL expert. Convert the user's natural language question into a valid SQL query. " +
                             "Only return the SQL query, no explanations.";
        
        return generateText(naturalLanguageQuery, systemPrompt);
    }

    /**
     * Check if API key is configured
     */
    public boolean isApiKeyConfigured() {
        return openAIConfig.isApiKeyConfigured();
    }
}

