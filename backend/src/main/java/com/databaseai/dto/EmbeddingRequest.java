package com.databaseai.dto;

import java.util.List;

/**
 * Embedding Request DTO (Data Transfer Object)
 * 
 * This represents the request body for OpenAI Embeddings API.
 * 
 * DTO = Simple class that carries data between layers
 * (No business logic, just data structure)
 */
public class EmbeddingRequest {
    
    /**
     * Model to use for embeddings
     * OpenAI's text-embedding-ada-002 is recommended (cheap, good quality)
     */
    private String model = "text-embedding-ada-002";
    
    /**
     * List of texts to convert to embeddings
     */
    private List<String> input;

    // Constructors
    public EmbeddingRequest() {
    }

    public EmbeddingRequest(List<String> input) {
        this.input = input;
    }

    public EmbeddingRequest(String model, List<String> input) {
        this.model = model;
        this.input = input;
    }

    // Getters and Setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }
}

