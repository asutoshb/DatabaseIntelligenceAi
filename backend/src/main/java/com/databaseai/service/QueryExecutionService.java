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

    @Autowired
    private RealTimeUpdateService realTimeUpdateService;

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
        return executeQuery(databaseInfoId, sqlQuery, timeoutSeconds, null);
    }

    public QueryExecutionResponse executeQuery(Long databaseInfoId, String sqlQuery, Integer timeoutSeconds, String requestId) {
        String effectiveRequestId = (requestId != null && !requestId.isBlank())
                ? requestId
                : UUID.randomUUID().toString();

        QueryExecutionResponse response = new QueryExecutionResponse();
        response.setDatabaseInfoId(databaseInfoId);
        response.setSqlQuery(sqlQuery);
        response.setExecutedAt(LocalDateTime.now());
        response.setRequestId(effectiveRequestId);

        long startTime = System.currentTimeMillis();

        Map<String, Object> requestMeta = new HashMap<>();
        requestMeta.put("databaseInfoId", databaseInfoId);
        realTimeUpdateService.publishQueryExecutionProgress(
                effectiveRequestId,
                "REQUEST_RECEIVED",
                "Received query execution request",
                requestMeta
        );

        // Step 1: Validate SQL query
        SQLValidator.ValidationResult validation = sqlValidator.validate(sqlQuery);
        if (!validation.isValid()) {
            Map<String, Object> validationData = new HashMap<>();
            validationData.put("errors", validation.getErrors());
            realTimeUpdateService.publishQueryExecutionError(
                    effectiveRequestId,
                    "VALIDATION_FAILED",
                    "SQL validation failed",
                    validationData
            );
            response.setSuccess(false);
            response.setErrorMessage("SQL validation failed: " + String.join(", ", validation.getErrors()));
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            return response;
        }

        Map<String, Object> validationSuccessData = new HashMap<>();
        validationSuccessData.put("timeoutSeconds", timeoutSeconds != null ? timeoutSeconds : DEFAULT_TIMEOUT_SECONDS);
        realTimeUpdateService.publishQueryExecutionProgress(
                effectiveRequestId,
                "VALIDATED",
                "SQL validation successful",
                validationSuccessData
        );

        // Step 2: Get database connection info
        DatabaseInfo databaseInfo = databaseInfoRepository.findById(databaseInfoId)
                .orElse(null);

        if (databaseInfo == null) {
            Map<String, Object> databaseError = new HashMap<>();
            databaseError.put("databaseInfoId", databaseInfoId);
            realTimeUpdateService.publishQueryExecutionError(
                    effectiveRequestId,
                    "DATABASE_NOT_FOUND",
                    "Database not found with ID: " + databaseInfoId,
                    databaseError
            );
            response.setSuccess(false);
            response.setErrorMessage("Database not found with ID: " + databaseInfoId);
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            return response;
        }

        Map<String, Object> connectionData = new HashMap<>();
        connectionData.put("databaseType", databaseInfo.getDatabaseType());
        realTimeUpdateService.publishQueryExecutionProgress(
                effectiveRequestId,
                "CONNECTING",
                "Establishing read-only database connection",
                connectionData
        );

        // Step 3: Execute query
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Step 3a: Create database connection
            connection = createConnection(databaseInfo);

            // Step 3b: Set read-only mode (security!)
            connection.setReadOnly(true);

            Map<String, Object> executionMeta = new HashMap<>();
            executionMeta.put("sqlPreview", sqlQuery.substring(0, Math.min(sqlQuery.length(), 80)));
            realTimeUpdateService.publishQueryExecutionProgress(
                    effectiveRequestId,
                    "EXECUTING",
                    "Executing SQL query",
                    executionMeta
            );

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

            Map<String, Object> successData = new HashMap<>();
            successData.put("rowCount", rows.size());
            successData.put("columns", columns);
            realTimeUpdateService.publishQueryExecutionSuccess(
                    effectiveRequestId,
                    "COMPLETED",
                    "Query execution completed",
                    successData
            );

        } catch (SQLTimeoutException e) {
            Map<String, Object> timeoutData = new HashMap<>();
            timeoutData.put("timeoutSeconds", timeoutSeconds != null ? timeoutSeconds : DEFAULT_TIMEOUT_SECONDS);
            realTimeUpdateService.publishQueryExecutionError(
                    effectiveRequestId,
                    "TIMEOUT",
                    "Query timed out",
                    timeoutData
            );
            response.setSuccess(false);
            response.setErrorMessage("Query timeout: Query took longer than " + timeoutSeconds + " seconds");
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        } catch (SQLException e) {
            Map<String, Object> sqlErrorData = new HashMap<>();
            sqlErrorData.put("sqlState", e.getSQLState());
            sqlErrorData.put("errorCode", e.getErrorCode());
            
            // Provide more user-friendly error messages for common database errors
            String errorMessage = e.getMessage();
            String userFriendlyMessage = errorMessage;
            
            if (errorMessage != null) {
                if (errorMessage.contains("role") && errorMessage.contains("does not exist")) {
                    userFriendlyMessage = "Database connection failed: The database user '" + 
                        databaseInfo.getUsername() + "' does not exist. Please check your database configuration.";
                } else if (errorMessage.contains("password authentication failed") || 
                           errorMessage.contains("password") && errorMessage.contains("authentication")) {
                    String passwordStatus = (databaseInfo.getPassword() != null && !databaseInfo.getPassword().isEmpty()) 
                        ? "Password is set (length: " + databaseInfo.getPassword().length() + ")" 
                        : "NO PASSWORD SET";
                    userFriendlyMessage = "Database connection failed: Password authentication error. " + passwordStatus + 
                        ". Please check your database credentials in Settings. For Render databases, get the password from Render → Database → Connect → Internal Database URL.";
                } else if (errorMessage.contains("Connection refused")) {
                    userFriendlyMessage = "Database connection failed: Cannot connect to database at " + 
                        databaseInfo.getHost() + ":" + databaseInfo.getPort() + ". Please check if the database is running.";
                } else if (errorMessage.contains("database") && errorMessage.contains("does not exist")) {
                    userFriendlyMessage = "Database connection failed: The database '" + 
                        databaseInfo.getDatabaseName() + "' does not exist. Please check your database configuration.";
                } else if (errorMessage.contains("timeout")) {
                    userFriendlyMessage = "Database connection failed: Connection timeout. Please check if the database is accessible.";
                }
            }
            
            realTimeUpdateService.publishQueryExecutionError(
                    effectiveRequestId,
                    "SQL_ERROR",
                    "SQL execution error: " + userFriendlyMessage,
                    sqlErrorData
            );
            response.setSuccess(false);
            response.setErrorMessage(userFriendlyMessage);
            response.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            realTimeUpdateService.publishQueryExecutionError(
                    effectiveRequestId,
                    "UNEXPECTED_ERROR",
                    "Unexpected error: " + e.getMessage(),
                    null
            );
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
        
        // Get password from DatabaseInfo (if provided)
        // Note: In production, password should be encrypted and stored securely
        String password = databaseInfo.getPassword();
        
        // Password is optional - only set if provided
        // Some databases (like local PostgreSQL) don't require passwords
        // Cloud databases (like Render) require passwords
        if (password != null && !password.isEmpty()) {
            props.setProperty("password", password);
            System.out.println("INFO: Using password for database: " + databaseInfo.getName() + " (ID: " + databaseInfo.getId() + ") [Password length: " + password.length() + "]");
        } else {
            System.out.println("ERROR: NO PASSWORD PROVIDED for database: " + databaseInfo.getName() + " (ID: " + databaseInfo.getId() + ")");
            System.out.println("ERROR: This will fail for databases that require authentication (like Render)");
            System.out.println("ERROR: Please add password in Settings page for database ID: " + databaseInfo.getId());
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

