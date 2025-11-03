package com.databaseai.repository;

import com.databaseai.model.SchemaEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SchemaEmbeddingRepository - Data Access Layer
 * 
 * Provides CRUD operations and custom queries for schema embeddings.
 */
@Repository
public interface SchemaEmbeddingRepository extends JpaRepository<SchemaEmbedding, Long> {

    /**
     * Find all embeddings for a specific database
     */
    List<SchemaEmbedding> findByDatabaseInfoId(Long databaseInfoId);

    /**
     * Find embedding by schema name
     */
    List<SchemaEmbedding> findBySchemaName(String schemaName);

    /**
     * Find embeddings by database and schema name
     */
    List<SchemaEmbedding> findByDatabaseInfoIdAndSchemaName(Long databaseInfoId, String schemaName);

    /**
     * Custom query: Find similar schemas using cosine similarity
     * 
     * Note: This is a simplified version. With pgvector, we'd use:
     * ORDER BY embedding <=> :queryEmbedding::vector
     * 
     * For now, we'll fetch all and calculate similarity in Java (not optimal for large datasets)
     */
    @Query(value = "SELECT * FROM schema_embeddings WHERE database_info_id = :databaseInfoId", nativeQuery = true)
    List<SchemaEmbedding> findAllByDatabaseInfoId(@Param("databaseInfoId") Long databaseInfoId);
}

