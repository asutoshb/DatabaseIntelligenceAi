package com.databaseai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for SQL Query Execution
 * 
 * This is used when the user wants to execute a generated SQL query.
 * 
 * Example:
 * {
 *   "databaseInfoId": 1,
 *   "sqlQuery": "SELECT * FROM customers LIMIT 5",
 *   "timeoutSeconds": 30
 * }
 */
public class QueryExecutionRequest {

    /**
     * Database ID to execute query against
     */
    @NotNull(message = "Database ID is required")
    private Long databaseInfoId;

    /**
     * SQL query to execute (must be SELECT only)
     */
    @NotBlank(message = "SQL query is required")
    private String sqlQuery;

    /**
     * Query timeout in seconds (default: 30)
     * Maximum allowed: 300 seconds (5 minutes)
     */
    private Integer timeoutSeconds = 30;

    /**
     * Optional client-provided request ID for correlating WebSocket updates
     */
    private String clientRequestId;

    // Constructors
    public QueryExecutionRequest() {
    }

    public QueryExecutionRequest(Long databaseInfoId, String sqlQuery) {
        this.databaseInfoId = databaseInfoId;
        this.sqlQuery = sqlQuery;
        this.timeoutSeconds = 30;
    }

    public QueryExecutionRequest(Long databaseInfoId, String sqlQuery, Integer timeoutSeconds) {
        this.databaseInfoId = databaseInfoId;
        this.sqlQuery = sqlQuery;
        this.timeoutSeconds = timeoutSeconds != null ? timeoutSeconds : 30;
    }

    // Getters and Setters
    public Long getDatabaseInfoId() {
        return databaseInfoId;
    }

    public void setDatabaseInfoId(Long databaseInfoId) {
        this.databaseInfoId = databaseInfoId;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        // Enforce maximum timeout
        if (timeoutSeconds != null && timeoutSeconds > 300) {
            this.timeoutSeconds = 300;
        } else {
            this.timeoutSeconds = timeoutSeconds != null ? timeoutSeconds : 30;
        }
    }

    public String getClientRequestId() {
        return clientRequestId;
    }

    public void setClientRequestId(String clientRequestId) {
        this.clientRequestId = clientRequestId;
    }
}

