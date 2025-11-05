package com.databaseai.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for SQL Query Execution
 * 
 * Contains the results of executing a SQL query.
 * 
 * Example:
 * {
 *   "success": true,
 *   "rows": [
 *     {"id": 1, "name": "John", "email": "john@example.com"},
 *     {"id": 2, "name": "Jane", "email": "jane@example.com"}
 *   ],
 *   "columns": ["id", "name", "email"],
 *   "rowCount": 2,
 *   "executionTimeMs": 45,
 *   "executedAt": "2025-11-04T15:30:00"
 * }
 */
public class QueryExecutionResponse {

    /**
     * Whether the query executed successfully
     */
    private boolean success;

    /**
     * Query results as a list of maps (rows)
     * Each map represents one row with column names as keys
     */
    private List<Map<String, Object>> rows;

    /**
     * Column names from the result set
     */
    private List<String> columns;

    /**
     * Number of rows returned
     */
    private Integer rowCount;

    /**
     * Execution time in milliseconds
     */
    private Long executionTimeMs;

    /**
     * Timestamp when query was executed
     */
    private LocalDateTime executedAt;

    /**
     * Error message (if execution failed)
     */
    private String errorMessage;

    /**
     * SQL query that was executed
     */
    private String sqlQuery;

    /**
     * Database ID that was queried
     */
    private Long databaseInfoId;

    // Constructors
    public QueryExecutionResponse() {
        this.executedAt = LocalDateTime.now();
    }

    public QueryExecutionResponse(boolean success) {
        this.success = success;
        this.executedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
        if (rows != null) {
            this.rowCount = rows.size();
        }
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public Long getDatabaseInfoId() {
        return databaseInfoId;
    }

    public void setDatabaseInfoId(Long databaseInfoId) {
        this.databaseInfoId = databaseInfoId;
    }
}

