package com.databaseai.service;

import com.databaseai.model.SchemaEmbedding;
import com.databaseai.repository.SchemaEmbeddingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SchemaEmbeddingService - Business Logic for Schema Embeddings
 * 
 * Handles:
 * - Generating embeddings for schemas
 * - Storing schema embeddings
 * - Searching similar schemas (vector similarity)
 */
@Service
public class SchemaEmbeddingService {

    @Autowired
    private SchemaEmbeddingRepository schemaEmbeddingRepository;

    @Autowired
    private EmbeddingService embeddingService;

    /**
     * Index a schema (generate embedding and store it)
     * 
     * @param databaseInfoId Database ID
     * @param schemaName Schema/table name
     * @param schemaDescription Natural language description
     * @return Created SchemaEmbedding
     */
    public SchemaEmbedding indexSchema(Long databaseInfoId, String schemaName, String schemaDescription) {
        // Generate embedding from description
        List<Double> embedding = embeddingService.generateEmbedding(schemaDescription);

        // Create and save
        SchemaEmbedding schemaEmbedding = new SchemaEmbedding(
                databaseInfoId,
                schemaName,
                schemaDescription,
                embedding
        );

        return schemaEmbeddingRepository.save(schemaEmbedding);
    }

    /**
     * Find similar schemas using cosine similarity
     * 
     * @param databaseInfoId Database ID to search in
     * @param queryText Natural language query
     * @param topK Number of top results to return
     * @return List of similar schemas, sorted by similarity (highest first)
     */
    public List<SchemaEmbedding> findSimilarSchemas(Long databaseInfoId, String queryText, int topK) {
        // Generate embedding for query
        List<Double> queryEmbedding = embeddingService.generateEmbedding(queryText);

        // Get all embeddings for this database
        List<SchemaEmbedding> allSchemas = schemaEmbeddingRepository.findAllByDatabaseInfoId(databaseInfoId);

        // Calculate cosine similarity for each
        List<SchemaWithSimilarity> scored = allSchemas.stream()
                .map(schema -> {
                    double similarity = cosineSimilarity(queryEmbedding, schema.getEmbeddingAsList());
                    return new SchemaWithSimilarity(schema, similarity);
                })
                .sorted(Comparator.comparing(SchemaWithSimilarity::getSimilarity).reversed())
                .limit(topK)
                .collect(Collectors.toList());

        return scored.stream()
                .map(SchemaWithSimilarity::getSchema)
                .collect(Collectors.toList());
    }

    /**
     * Calculate cosine similarity between two vectors
     * 
     * Formula: cos(θ) = (A · B) / (||A|| * ||B||)
     * 
     * @param vec1 First vector
     * @param vec2 Second vector
     * @return Similarity score (0 to 1, higher = more similar)
     */
    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += vec1.get(i) * vec1.get(i);
            norm2 += vec2.get(i) * vec2.get(i);
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Get all embeddings for a database
     */
    public List<SchemaEmbedding> getSchemasByDatabase(Long databaseInfoId) {
        return schemaEmbeddingRepository.findByDatabaseInfoId(databaseInfoId);
    }

    /**
     * Delete schema embedding
     */
    public void deleteSchemaEmbedding(Long id) {
        schemaEmbeddingRepository.deleteById(id);
    }

    /**
     * Helper class to store schema with similarity score
     */
    private static class SchemaWithSimilarity {
        private final SchemaEmbedding schema;
        private final double similarity;

        public SchemaWithSimilarity(SchemaEmbedding schema, double similarity) {
            this.schema = schema;
            this.similarity = similarity;
        }

        public SchemaEmbedding getSchema() {
            return schema;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
}

