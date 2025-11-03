package com.databaseai.controller;

import com.databaseai.model.SchemaEmbedding;
import com.databaseai.service.SchemaEmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SchemaEmbeddingController - REST API for Schema Embeddings
 * 
 * Handles:
 * - Indexing schemas (generating and storing embeddings)
 * - Searching similar schemas
 * - Managing schema embeddings
 */
@RestController
@RequestMapping("/schema-embeddings")
public class SchemaEmbeddingController {

    @Autowired
    private SchemaEmbeddingService schemaEmbeddingService;

    /**
     * POST /api/schema-embeddings/index
     * 
     * Index a schema (generate embedding and store)
     * 
     * Request body:
     * {
     *   "databaseInfoId": 1,
     *   "schemaName": "users",
     *   "schemaDescription": "users table with id, username, email columns"
     * }
     */
    @PostMapping("/index")
    public ResponseEntity<?> indexSchema(@RequestBody Map<String, Object> request) {
        try {
            Long databaseInfoId = Long.valueOf(request.get("databaseInfoId").toString());
            String schemaName = request.get("schemaName").toString();
            String schemaDescription = request.get("schemaDescription").toString();

            SchemaEmbedding embedding = schemaEmbeddingService.indexSchema(
                    databaseInfoId,
                    schemaName,
                    schemaDescription
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(embedding);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/schema-embeddings/search
     * 
     * Find similar schemas using vector similarity
     * 
     * Request body:
     * {
     *   "databaseInfoId": 1,
     *   "query": "customer information",
     *   "topK": 5
     * }
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchSimilarSchemas(@RequestBody Map<String, Object> request) {
        try {
            Long databaseInfoId = Long.valueOf(request.get("databaseInfoId").toString());
            String query = request.get("query").toString();
            int topK = request.containsKey("topK") 
                    ? Integer.parseInt(request.get("topK").toString()) 
                    : 5;

            List<SchemaEmbedding> results = schemaEmbeddingService.findSimilarSchemas(
                    databaseInfoId,
                    query,
                    topK
            );

            Map<String, Object> response = new HashMap<>();
            response.put("query", query);
            response.put("results", results);
            response.put("count", results.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/schema-embeddings/database/{databaseInfoId}
     * 
     * Get all schema embeddings for a database
     */
    @GetMapping("/database/{databaseInfoId}")
    public ResponseEntity<List<SchemaEmbedding>> getSchemasByDatabase(
            @PathVariable Long databaseInfoId) {
        List<SchemaEmbedding> schemas = schemaEmbeddingService.getSchemasByDatabase(databaseInfoId);
        return ResponseEntity.ok(schemas);
    }

    /**
     * DELETE /api/schema-embeddings/{id}
     * 
     * Delete a schema embedding
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchemaEmbedding(@PathVariable Long id) {
        schemaEmbeddingService.deleteSchemaEmbedding(id);
        return ResponseEntity.noContent().build();
    }
}

