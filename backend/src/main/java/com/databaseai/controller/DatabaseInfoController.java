package com.databaseai.controller;

import com.databaseai.model.DatabaseInfo;
import com.databaseai.service.DatabaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DatabaseInfoController - REST API Endpoints for Database Management
 * 
 * Handles HTTP requests for managing database connections.
 * 
 * Endpoints:
 * - GET /api/databases - Get all databases
 * - GET /api/databases/{id} - Get database by ID
 * - POST /api/databases - Register new database
 * - PUT /api/databases/{id} - Update database info
 * - DELETE /api/databases/{id} - Delete database registration
 */
@RestController
@RequestMapping("/databases")
public class DatabaseInfoController {

    @Autowired
    private DatabaseInfoService databaseInfoService;

    /**
     * GET /api/databases
     * 
     * Get all registered databases
     */
    @GetMapping
    public ResponseEntity<List<DatabaseInfo>> getAllDatabases() {
        List<DatabaseInfo> databases = databaseInfoService.getAllDatabases();
        return ResponseEntity.ok(databases);
    }

    /**
     * GET /api/databases/{id}
     * 
     * Get database by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DatabaseInfo> getDatabaseById(@PathVariable Long id) {
        return databaseInfoService.getDatabaseById(id)
                .map(database -> ResponseEntity.ok(database))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/databases
     * 
     * Register a new database
     * 
     * Example request body:
     * {
     *   "name": "Production DB",
     *   "databaseType": "PostgreSQL",
     *   "host": "localhost",
     *   "port": 5432,
     *   "databaseName": "mydb",
     *   "username": "postgres",
     *   "password": "optional-password"
     * }
     */
    @PostMapping
    public ResponseEntity<?> createDatabase(@RequestBody DatabaseInfo databaseInfo) {
        // Debug: Log password status
        System.out.println("DEBUG: Create request received");
        System.out.println("DEBUG: Password in request: " + (databaseInfo.getPassword() != null ? "SET (length: " + databaseInfo.getPassword().length() + ")" : "NULL"));
        
        try {
            DatabaseInfo created = databaseInfoService.createDatabase(databaseInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * PUT /api/databases/{id}
     * 
     * Update database information
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDatabase(@PathVariable Long id, @RequestBody DatabaseInfo databaseInfo) {
        // Debug: Log password status
        System.out.println("DEBUG: Update request received for database ID: " + id);
        System.out.println("DEBUG: Password in request: " + (databaseInfo.getPassword() != null ? "SET (length: " + databaseInfo.getPassword().length() + ")" : "NULL"));
        
        return databaseInfoService.updateDatabase(id, databaseInfo)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/databases/{id}
     * 
     * Delete database registration
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDatabase(@PathVariable Long id) {
        if (databaseInfoService.deleteDatabase(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /api/databases/type/{databaseType}
     * 
     * Get all databases of a specific type
     * 
     * Example: GET /api/databases/type/PostgreSQL
     */
    @GetMapping("/type/{databaseType}")
    public ResponseEntity<List<DatabaseInfo>> getDatabasesByType(@PathVariable String databaseType) {
        List<DatabaseInfo> databases = databaseInfoService.getDatabasesByType(databaseType);
        return ResponseEntity.ok(databases);
    }
}

