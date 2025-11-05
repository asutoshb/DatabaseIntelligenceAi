package com.databaseai.service;

import com.databaseai.dto.QueryExecutionResponse;
import com.databaseai.model.DatabaseInfo;
import com.databaseai.repository.DatabaseInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Query Execution Service
 * 
 * Safely executes SQL queries against user databases.
 * 
 * Features:
 * - Read-only connections (can't modify data)
 * - Query timeout (prevents long-running queries)
 * - Result processing (ResultSet → JSON)
 * - Error handling (graceful failure)
 * - SQL validation (SELECT only)
 * 
 * Process:
 * 1. Validate SQL query (SELECT only, no dangerous keywords)
 * 2. Get database connection info from DatabaseInfo
 * 3. Create JDBC connection (read-only)
 * 4. Execute SQL with timeout
 * 5. Process ResultSet → JSON
 * 6. Return results
 */
@Service
public class QueryExecutionService {

    @Autowired
    private DatabaseInfoRepository databaseInfoRepository;

    @Autowired
    private SQLValidator sqlValidator;

    /**
     * Default query timeout (30 seconds)
     */
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * Maximum query timeout (5 minutes)
     */
    private static final int MAX_TIMEOUT_SECONDS = 300;

    /**
     * Execute SQL query against a database
     * 
     * @param databaseInfoId Database to query
     * @param sqlQuery SQL query to execute (must be SELECT only)
     * @param timeoutSeconds Query timeout in seconds
     * @return QueryExecutionResponse with results or error
     */
    public QueryExecutionResponse executeQuery(Long databaseInfoId, String sqlQuery, Integer timeoutSeconds) {
        QueryExecutionResponse response = new QueryExecutionResponse();
        response.setDatabaseInfoId(databaseInfoId);
        response.setSqlQuery(sqlQuery);
        response.setExecutedAt(LocalDateTime.now());

        long startTime = System.currentTimeMillis();

        // Step 1: Validate SQL query
        SQLValidator.ValidationResult validation = sqlValidator.validate(sqlQuery);
        if (!validation.isValid()) {
            response.setSuccess(false);
            response.setErrorMessage("SQL validation failed: " + String.join(", ", validation.getErrors()));
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            return response;
        }

        // Step 2: Get database connection info
        DatabaseInfo databaseInfo = databaseInfoRepository.findById(databaseInfoId)
                .orElse(null);

        if (databaseInfo == null) {
            response.setSuccess(false);
            response.setErrorMessage("Database not found with ID: " + databaseInfoId);
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            return response;
        }

        // Step 3: Execute query
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Step 3a: Create database connection
            connection = createConnection(databaseInfo);

            // Step 3b: Set read-only mode (security!)
            connection.setReadOnly(true);

            // Step 3c: Create statement with timeout
            statement = connection.createStatement();
            int timeout = (timeoutSeconds != null && timeoutSeconds > 0) 
                    ? Math.min(timeoutSeconds, MAX_TIMEOUT_SECONDS) 
                    : DEFAULT_TIMEOUT_SECONDS;
            statement.setQueryTimeout(timeout);

            // Step 3d: Execute query
            resultSet = statement.executeQuery(sqlQuery);

            // Step 4: Process results
            List<Map<String, Object>> rows = processResultSet(resultSet);
            List<String> columns = getColumnNames(resultSet);

            // Step 5: Build response
            response.setSuccess(true);
            response.setRows(rows);
            response.setColumns(columns);
            response.setRowCount(rows.size());
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);

        } catch (SQLTimeoutException e) {
            response.setSuccess(false);
            response.setErrorMessage("Query timeout: Query took longer than " + timeoutSeconds + " seconds");
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        } catch (SQLException e) {
            response.setSuccess(false);
            response.setErrorMessage("SQL execution error: " + e.getMessage());
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("Unexpected error: " + e.getMessage());
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        } finally {
            // Step 6: Close resources (important!)
            closeResources(connection, statement, resultSet);
        }

        return response;
    }

    /**
     * Create JDBC connection to database
     * 
     * Supports PostgreSQL and MySQL for now.
     * Can be extended for other databases.
     */
    private Connection createConnection(DatabaseInfo databaseInfo) throws SQLException {
        String url = buildConnectionUrl(databaseInfo);
        
        Properties props = new Properties();
        props.setProperty("user", databaseInfo.getUsername());
        
        // Note: In production, password should be encrypted and stored securely
        // For now, we'll use empty password (you'll need to add password field to DatabaseInfo)
        String password = ""; // TODO: Get encrypted password from DatabaseInfo
        
        if (!password.isEmpty()) {
            props.setProperty("password", password);
        }

        // Additional connection properties for security and performance
        props.setProperty("readOnly", "true"); // Read-only connection
        props.setProperty("connectTimeout", "10"); // Connection timeout (10 seconds)

        return DriverManager.getConnection(url, props);
    }

    /**
     * Build JDBC connection URL based on database type
     */
    private String buildConnectionUrl(DatabaseInfo databaseInfo) {
        String databaseType = databaseInfo.getDatabaseType().toLowerCase();
        
        switch (databaseType) {
            case "postgresql":
            case "postgres":
                return String.format("jdbc:postgresql://%s:%d/%s",
                        databaseInfo.getHost(),
                        databaseInfo.getPort(),
                        databaseInfo.getDatabaseName());
            
            case "mysql":
                return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                        databaseInfo.getHost(),
                        databaseInfo.getPort(),
                        databaseInfo.getDatabaseName());
            
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }

    /**
     * Process ResultSet and convert to List of Maps
     * 
     * Each map represents a row with column names as keys.
     */
    private List<Map<String, Object>> processResultSet(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>(); // Preserve column order
            
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                
                // Handle null values
                if (value == null) {
                    row.put(columnName, null);
                } else {
                    // Convert database-specific types to Java types
                    row.put(columnName, convertValue(value));
                }
            }
            
            rows.add(row);
        }

        return rows;
    }

    /**
     * Convert database value to JSON-friendly Java type
     */
    private Object convertValue(Object value) {
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate().toString();
        } else if (value instanceof java.sql.Time) {
            return ((java.sql.Time) value).toString();
        } else if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toInstant().toString();
        } else if (value instanceof java.math.BigDecimal) {
            // Convert BigDecimal to Double for JSON (or String if too large)
            return ((java.math.BigDecimal) value).doubleValue();
        } else {
            return value;
        }
    }

    /**
     * Get column names from ResultSet
     */
    private List<String> getColumnNames(ResultSet resultSet) throws SQLException {
        List<String> columns = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columns.add(metaData.getColumnName(i));
        }

        return columns;
    }

    /**
     * Close database resources properly
     * 
     * Always close resources in finally block to prevent leaks!
     */
    private void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // Log error but don't throw (we're in cleanup)
            System.err.println("Error closing database resources: " + e.getMessage());
        }
    }

    /**
     * Test database connection
     * 
     * Useful for verifying database connectivity before executing queries.
     */
    public boolean testConnection(Long databaseInfoId) {
        DatabaseInfo databaseInfo = databaseInfoRepository.findById(databaseInfoId)
                .orElse(null);

        if (databaseInfo == null) {
            return false;
        }

        Connection connection = null;
        try {
            connection = createConnection(databaseInfo);
            return connection.isValid(5); // Test with 5 second timeout
        } catch (SQLException e) {
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }
}

