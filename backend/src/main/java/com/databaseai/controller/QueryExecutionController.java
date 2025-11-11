package com.databaseai.controller;

import com.databaseai.dto.QueryExecutionRequest;
import com.databaseai.dto.QueryExecutionResponse;
import com.databaseai.service.QueryExecutionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Query Execution Controller
 * 
 * REST API endpoints for executing SQL queries safely.
 * 
 * This controller handles:
 * - Executing generated SQL queries
 * - Testing database connections
 * - Query status and health checks
 * 
 * Security:
 * - All queries are validated (SELECT only)
 * - Connections are read-only
 * - Query timeouts enforced
 * 
 * Example:
 * POST /api/query-execution/execute
 * {
 *   "databaseInfoId": 1,
 *   "sqlQuery": "SELECT * FROM customers LIMIT 5",
 *   "timeoutSeconds": 30
 * }
 */
@RestController
@RequestMapping("/query-execution")
@CrossOrigin(origins = "*")
public class QueryExecutionController {

    @Autowired
    private QueryExecutionService queryExecutionService;

    /**
     * Execute SQL query
     * 
     * POST /api/query-execution/execute
     * 
     * This endpoint:
     * 1. Validates SQL query (SELECT only, no dangerous keywords)
     * 2. Gets database connection info
     * 3. Creates read-only connection
     * 4. Executes query with timeout
     * 5. Returns results as JSON
     * 
     * @param request QueryExecutionRequest with database ID and SQL query
     * @return QueryExecutionResponse with results or error
     */
    @PostMapping("/execute")
    public ResponseEntity<QueryExecutionResponse> executeQuery(
            @Valid @RequestBody QueryExecutionRequest request
    ) {
        String requestId = (request.getClientRequestId() != null && !request.getClientRequestId().isBlank())
                ? request.getClientRequestId()
                : UUID.randomUUID().toString();

        try {
            QueryExecutionResponse response = queryExecutionService.executeQuery(
                    request.getDatabaseInfoId(),
                    request.getSqlQuery(),
                    request.getTimeoutSeconds(),
                    requestId
            );

            response.setRequestId(requestId);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                // Check if it's a validation error (400) or database/execution error (500)
                String errorMessage = response.getErrorMessage();
                boolean isValidationError = errorMessage != null && 
                    (errorMessage.contains("validation") || errorMessage.contains("Only SELECT queries"));
                
                HttpStatus status = isValidationError ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
                return ResponseEntity.status(status).body(response);
            }
        } catch (Exception e) {
            QueryExecutionResponse errorResponse = new QueryExecutionResponse();
            errorResponse.setSuccess(false);
            errorResponse.setErrorMessage("Unexpected error: " + e.getMessage());
            errorResponse.setDatabaseInfoId(request.getDatabaseInfoId());
            errorResponse.setSqlQuery(request.getSqlQuery());
            errorResponse.setRequestId(requestId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Test database connection
     * 
     * GET /api/query-execution/test-connection/{databaseInfoId}
     * 
     * Tests if we can connect to the database.
     * Useful for verifying database connectivity before executing queries.
     * 
     * @param databaseInfoId Database ID to test
     * @return Status response with connection test result
     */
    @GetMapping("/test-connection/{databaseInfoId}")
    public ResponseEntity<Map<String, Object>> testConnection(@PathVariable Long databaseInfoId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean connected = queryExecutionService.testConnection(databaseInfoId);
            response.put("databaseInfoId", databaseInfoId);
            response.put("connected", connected);
            response.put("message", connected 
                    ? "Database connection successful" 
                    : "Database connection failed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("databaseInfoId", databaseInfoId);
            response.put("connected", false);
            response.put("message", "Connection test error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Health check endpoint
     * 
     * GET /api/query-execution/status
     * 
     * Returns service status information.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Query Execution Service");
        response.put("status", "UP");
        response.put("message", "Query execution service is running");
        return ResponseEntity.ok(response);
    }
}

