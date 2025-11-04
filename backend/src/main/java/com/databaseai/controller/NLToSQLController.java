package com.databaseai.controller;

import com.databaseai.dto.NLToSQLRequest;
import com.databaseai.dto.NLToSQLResponse;
import com.databaseai.service.NLToSQLService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Natural Language to SQL Controller
 * 
 * REST API endpoints for converting natural language queries to SQL.
 * 
 * This is the core feature of the application!
 * 
 * Example:
 * POST /api/nl-to-sql/convert (context path /api is added automatically)
 * {
 *   "databaseInfoId": 1,
 *   "naturalLanguageQuery": "Show me top 5 customers"
 * }
 * 
 * Response:
 * {
 *   "sqlQuery": "SELECT * FROM customers LIMIT 5",
 *   "naturalLanguageQuery": "Show me top 5 customers",
 *   "relevantSchemas": [...],
 *   "explanation": "...",
 *   "isValid": true
 * }
 */
@RestController
@RequestMapping("/nl-to-sql")
@CrossOrigin(origins = "*")
public class NLToSQLController {

    @Autowired
    private NLToSQLService nlToSQLService;

    /**
     * Convert natural language to SQL using RAG
     * 
     * POST /api/nl-to-sql/convert (context path /api is added automatically)
     * 
     * @param request NLToSQLRequest with database ID and query
     * @return NLToSQLResponse with generated SQL
     */
    @PostMapping("/convert")
    public ResponseEntity<NLToSQLResponse> convertToSQL(
            @Valid @RequestBody NLToSQLRequest request
    ) {
        try {
            NLToSQLResponse response = nlToSQLService.convertToSQL(
                    request.getDatabaseInfoId(),
                    request.getNaturalLanguageQuery(),
                    request.getTopK() != null ? request.getTopK() : 5
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Create error response
            NLToSQLResponse errorResponse = new NLToSQLResponse();
            errorResponse.setSqlQuery("");
            errorResponse.setNaturalLanguageQuery(request.getNaturalLanguageQuery());
            errorResponse.setDatabaseInfoId(request.getDatabaseInfoId());
            errorResponse.setValid(false);
            errorResponse.setValidationErrors(List.of("Error: " + e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     * 
     * GET /api/nl-to-sql/status (context path /api is added automatically)
     */
    @GetMapping("/status")
    public ResponseEntity<NLToSQLResponse> status() {
        NLToSQLResponse response = new NLToSQLResponse();
        response.setSqlQuery("");
        response.setNaturalLanguageQuery("Status check");
        response.setValid(true);
        response.setExplanation("NL to SQL service is running");

        return ResponseEntity.ok(response);
    }
}

