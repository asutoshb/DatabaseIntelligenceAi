package com.databaseai.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SchemaEmbedding Entity
 * 
 * Stores database schema descriptions as embeddings (vectors).
 * 
 * Purpose:
 * - Store schema information (table names, columns, descriptions)
 * - Store embeddings (vector representation) for that schema
 * - Enable similarity search to find relevant schemas for RAG
 * 
 * Example:
 * - Schema: "users table with id, username, email columns"
 * - Embedding: [0.123, -0.456, 0.789, ...] (1536 numbers)
 */
@Entity
@Table(name = "schema_embeddings")
public class SchemaEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Which database this schema belongs to
     */
    @Column(name = "database_info_id")
    private Long databaseInfoId;

    /**
     * Schema name (e.g., "users", "orders", "customers")
     */
    @Column(nullable = false)
    private String schemaName;

    /**
     * Schema description in natural language
     * Example: "users table with id, username, email columns"
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String schemaDescription;

    /**
     * Embedding vector (stored as array)
     * For pgvector: Would use vector(1536) type
     * For now: Stored as TEXT (comma-separated) or using native array
     * 
     * Dimension: 1536 (OpenAI ada-002 embedding size)
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String embedding; // Stored as JSON array string: "[0.1, 0.2, ...]"

    /**
     * Additional metadata (table name, column info, etc.)
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public SchemaEmbedding() {
    }

    public SchemaEmbedding(Long databaseInfoId, String schemaName, String schemaDescription, List<Double> embedding) {
        this.databaseInfoId = databaseInfoId;
        this.schemaName = schemaName;
        this.schemaDescription = schemaDescription;
        this.embedding = convertEmbeddingToString(embedding);
        this.createdAt = LocalDateTime.now();
    }

    // Helper methods to convert between List<Double> and String
    public List<Double> getEmbeddingAsList() {
        return convertStringToEmbedding(this.embedding);
    }

    public void setEmbeddingFromList(List<Double> embedding) {
        this.embedding = convertEmbeddingToString(embedding);
    }

    private String convertEmbeddingToString(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            return "[]";
        }
        // Convert to JSON array format
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(embedding.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    private List<Double> convertStringToEmbedding(String embeddingStr) {
        if (embeddingStr == null || embeddingStr.trim().equals("[]")) {
            return List.of();
        }
        // Parse JSON array: "[0.1,0.2,0.3]" -> List<Double>
        String clean = embeddingStr.trim().replaceAll("^\\[|\\]$", "");
        if (clean.isEmpty()) {
            return List.of();
        }
        return java.util.Arrays.stream(clean.split(","))
                .map(String::trim)
                .map(Double::parseDouble)
                .toList();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDatabaseInfoId() {
        return databaseInfoId;
    }

    public void setDatabaseInfoId(Long databaseInfoId) {
        this.databaseInfoId = databaseInfoId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaDescription() {
        return schemaDescription;
    }

    public void setSchemaDescription(String schemaDescription) {
        this.schemaDescription = schemaDescription;
    }

    public String getEmbedding() {
        return embedding;
    }

    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "SchemaEmbedding{" +
                "id=" + id +
                ", databaseInfoId=" + databaseInfoId +
                ", schemaName='" + schemaName + '\'' +
                ", schemaDescription='" + schemaDescription + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

