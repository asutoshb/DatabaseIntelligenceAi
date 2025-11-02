package com.databaseai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * OpenAI Configuration
 * 
 * This class configures the OpenAI API client.
 * 
 * We use WebClient (Spring's modern HTTP client) to make REST API calls to OpenAI.
 * 
 * @Configuration = Spring manages this class
 * @Value = Reads values from application.properties or environment variables
 */
@Configuration
public class OpenAIConfig {

    /**
     * OpenAI API Key
     * 
     * Reads from:
     * 1. Environment variable: OPENAI_API_KEY
     * 2. application.properties: openai.api.key
     * 
     * NEVER commit API keys to Git!
     */
    @Value("${openai.api.key:${OPENAI_API_KEY:}}")
    private String apiKey;

    /**
     * OpenAI API Base URL
     * Default: https://api.openai.com/v1
     */
    @Value("${openai.api.url:https://api.openai.com/v1}")
    private String apiUrl;

    /**
     * Create WebClient bean for OpenAI API calls
     * 
     * WebClient is Spring's reactive HTTP client (better than RestTemplate).
     * 
     * @return Configured WebClient for OpenAI API
     */
    @Bean
    public WebClient openAiWebClient() {
        return WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * Get API Key (for validation purposes)
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Check if API key is configured
     */
    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}

