package com.databaseai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for Natural Language to SQL conversion
 * 
 * Example:
 * {
 *   "databaseInfoId": 1,
 *   "naturalLanguageQuery": "Show me top 5 customers by revenue"
 * }
 */
public class NLToSQLRequest {

    /**
     * Database ID to query against
     */
    @NotNull(message = "Database ID is required")
    private Long databaseInfoId;

    /**
     * User's question in natural language
     * Example: "Show me top 5 customers"
     */
    @NotBlank(message = "Natural language query is required")
    private String naturalLanguageQuery;

    /**
     * Number of relevant schemas to retrieve (default: 5)
     */
    private Integer topK = 5;

    /**
     * Optional client-provided request ID for correlating WebSocket updates
     */
    private String clientRequestId;

    // Constructors
    public NLToSQLRequest() {
    }

    public NLToSQLRequest(Long databaseInfoId, String naturalLanguageQuery) {
        this.databaseInfoId = databaseInfoId;
        this.naturalLanguageQuery = naturalLanguageQuery;
        this.topK = 5;
    }

    // Getters and Setters
    public Long getDatabaseInfoId() {
        return databaseInfoId;
    }

    public void setDatabaseInfoId(Long databaseInfoId) {
        this.databaseInfoId = databaseInfoId;
    }

    public String getNaturalLanguageQuery() {
        return naturalLanguageQuery;
    }

    public void setNaturalLanguageQuery(String naturalLanguageQuery) {
        this.naturalLanguageQuery = naturalLanguageQuery;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public String getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }
}

