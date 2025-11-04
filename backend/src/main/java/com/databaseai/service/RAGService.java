package com.databaseai.service;

import com.databaseai.model.SchemaEmbedding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG Service (Retrieval-Augmented Generation)
 * 
 * What is RAG?
 * - RAG = Retrieval-Augmented Generation
 * - Technique to enhance LLM responses with relevant context
 * - Instead of asking GPT blindly, we first retrieve relevant information
 * - Then we pass that context to GPT along with the question
 * 
 * How it works:
 * 1. User asks: "Show me top 5 customers"
 * 2. Generate embedding for the query
 * 3. Search similar schema embeddings (from Chunk 4)
 * 4. Retrieve relevant schema details (table names, columns)
 * 5. Pass schema context + query to GPT
 * 6. GPT generates accurate SQL using the context
 * 
 * Why RAG is better:
 * - Without RAG: GPT guesses table/column names → Often wrong
 * - With RAG: GPT uses actual schema → Accurate SQL!
 */
@Service
public class RAGService {

    @Autowired
    private SchemaEmbeddingService schemaEmbeddingService;

    /**
     * Retrieve relevant schema context for a natural language query
     * 
     * This is the "Retrieval" part of RAG!
     * 
     * @param databaseInfoId Database to search in
     * @param query Natural language query
     * @param topK Number of relevant schemas to retrieve
     * @return List of relevant schema contexts
     */
    public List<SchemaContext> retrieveRelevantSchemas(Long databaseInfoId, String query, int topK) {
        // Step 1: Find similar schemas using vector similarity search
        List<SchemaEmbedding> similarSchemas = schemaEmbeddingService.findSimilarSchemas(
                databaseInfoId, query, topK
        );

        // Step 2: Convert to SchemaContext objects
        return similarSchemas.stream()
                .map(schema -> new SchemaContext(
                        schema.getSchemaName(),
                        schema.getSchemaDescription()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Build context string from retrieved schemas
     * 
     * This formats the schema information for the GPT prompt.
     * 
     * @param schemas List of relevant schemas
     * @return Formatted context string
     */
    public String buildContextString(List<SchemaContext> schemas) {
        if (schemas == null || schemas.isEmpty()) {
            return "No schema information available.";
        }

        StringBuilder context = new StringBuilder();
        context.append("Available database schemas:\n\n");

        for (int i = 0; i < schemas.size(); i++) {
            SchemaContext schema = schemas.get(i);
            context.append(String.format("%d. Schema: %s\n", i + 1, schema.getSchemaName()));
            context.append(String.format("   Description: %s\n\n", schema.getDescription()));
        }

        return context.toString();
    }

    /**
     * Schema context information
     */
    public static class SchemaContext {
        private final String schemaName;
        private final String description;

        public SchemaContext(String schemaName, String description) {
            this.schemaName = schemaName;
            this.description = description;
        }

        public String getSchemaName() {
            return schemaName;
        }

        public String getDescription() {
            return description;
        }
    }
}

