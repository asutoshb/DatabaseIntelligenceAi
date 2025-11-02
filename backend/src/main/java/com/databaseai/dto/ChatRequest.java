package com.databaseai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Chat Request DTO
 * 
 * Represents request for OpenAI Chat/Completion API (GPT).
 */
public class ChatRequest {
    
    /**
     * Model to use (GPT-3.5-turbo is cheaper, GPT-4 is better but expensive)
     */
    private String model = "gpt-3.5-turbo";
    
    /**
     * List of messages (conversation)
     */
    private List<Message> messages;
    
    /**
     * Temperature (0-2): Controls randomness
     * 0 = deterministic, 2 = very creative
     */
    private Double temperature = 0.7;
    
    /**
     * Maximum tokens in response
     * OpenAI API expects "max_tokens" (snake_case)
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens = 500;

    // Constructors
    public ChatRequest() {
    }

    public ChatRequest(List<Message> messages) {
        this.messages = messages;
    }

    // Getters and Setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    /**
     * Inner class: Message in conversation
     */
    public static class Message {
        /**
         * Role: "system", "user", or "assistant"
         */
        private String role;
        
        /**
         * Content of the message
         */
        private String content;

        public Message() {
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        // Getters and Setters
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

