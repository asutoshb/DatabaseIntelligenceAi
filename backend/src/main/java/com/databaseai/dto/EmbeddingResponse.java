package com.databaseai.dto;

import java.util.List;

/**
 * Embedding Response DTO
 * 
 * Represents the response from OpenAI Embeddings API.
 */
public class EmbeddingResponse {
    
    /**
     * List of embedding data objects
     */
    private List<EmbeddingData> data;
    
    /**
     * Model used
     */
    private String model;
    
    /**
     * Usage statistics
     */
    private Usage usage;

    // Getters and Setters
    public List<EmbeddingData> getData() {
        return data;
    }

    public void setData(List<EmbeddingData> data) {
        this.data = data;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    /**
     * Inner class: Embedding data
     */
    public static class EmbeddingData {
        /**
         * The embedding vector (array of numbers)
         * This is what we need! It's a list of ~1536 numbers
         */
        private List<Double> embedding;
        
        /**
         * Index of the input text
         */
        private Integer index;

        // Getters and Setters
        public List<Double> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Double> embedding) {
            this.embedding = embedding;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
    }

    /**
     * Inner class: Usage statistics
     */
    public static class Usage {
        private Integer promptTokens;
        private Integer totalTokens;

        // Getters and Setters
        public Integer getPromptTokens() {
            return promptTokens;
        }

        public void setPromptTokens(Integer promptTokens) {
            this.promptTokens = promptTokens;
        }

        public Integer getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(Integer totalTokens) {
            this.totalTokens = totalTokens;
        }
    }
}

