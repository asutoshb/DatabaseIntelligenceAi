package com.databaseai.dto;

import java.util.List;

/**
 * Response DTO for Natural Language to SQL conversion
 * 
 * Contains:
 * - Generated SQL query
 * - Relevant schema context used
 * - Explanation of the query
 * - Validation status
 */
public class NLToSQLResponse {

    /**
     * Generated SQL query
     */
    private String sqlQuery;

    /**
     * Natural language query that was converted
     */
    private String naturalLanguageQuery;

    /**
     * List of relevant schemas used for RAG
     */
    private List<SchemaContext> relevantSchemas;

    /**
     * Explanation of the generated SQL
     */
    private String explanation;

    /**
     * Whether SQL passed validation
     */
    private boolean isValid;

    /**
     * Validation errors (if any)
     */
    private List<String> validationErrors;

    /**
     * Database ID that was queried
     */
    private Long databaseInfoId;

    // Constructors
    public NLToSQLResponse() {
    }

    public NLToSQLResponse(String sqlQuery, String naturalLanguageQuery, Long databaseInfoId) {
        this.sqlQuery = sqlQuery;
        this.naturalLanguageQuery = naturalLanguageQuery;
        this.databaseInfoId = databaseInfoId;
        this.isValid = true;
    }

    // Getters and Setters
    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getNaturalLanguageQuery() {
        return naturalLanguageQuery;
    }

    public void setNaturalLanguageQuery(String naturalLanguageQuery) {
        this.naturalLanguageQuery = naturalLanguageQuery;
    }

    public List<SchemaContext> getRelevantSchemas() {
        return relevantSchemas;
    }

    public void setRelevantSchemas(List<SchemaContext> relevantSchemas) {
        this.relevantSchemas = relevantSchemas;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Long getDatabaseInfoId() {
        return databaseInfoId;
    }

    public void setDatabaseInfoId(Long databaseInfoId) {
        this.databaseInfoId = databaseInfoId;
    }

    /**
     * Inner class for schema context information
     */
    public static class SchemaContext {
        private String schemaName;
        private String schemaDescription;

        public SchemaContext() {
        }

        public SchemaContext(String schemaName, String schemaDescription) {
            this.schemaName = schemaName;
            this.schemaDescription = schemaDescription;
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
    }
}

