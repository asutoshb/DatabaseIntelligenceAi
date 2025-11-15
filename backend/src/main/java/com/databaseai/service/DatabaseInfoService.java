package com.databaseai.service;

import com.databaseai.model.DatabaseInfo;
import com.databaseai.repository.DatabaseInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * DatabaseInfoService - Business Logic for Database Information
 * 
 * Handles business logic for managing database connections.
 */
@Service
public class DatabaseInfoService {

    @Autowired
    private DatabaseInfoRepository databaseInfoRepository;

    /**
     * Get all registered databases
     */
    public List<DatabaseInfo> getAllDatabases() {
        return databaseInfoRepository.findAll();
    }

    /**
     * Get database by ID
     */
    public Optional<DatabaseInfo> getDatabaseById(Long id) {
        return databaseInfoRepository.findById(id);
    }

    /**
     * Register a new database
     * 
     * @param databaseInfo Database information
     * @return Created database info
     * @throws IllegalArgumentException if database name already exists
     */
    public DatabaseInfo createDatabase(DatabaseInfo databaseInfo) {
        // Validate: Check if name already exists
        if (databaseInfoRepository.existsByName(databaseInfo.getName())) {
            throw new IllegalArgumentException("Database name already exists: " + databaseInfo.getName());
        }

        // Log password status for debugging
        if (databaseInfo.getPassword() != null && !databaseInfo.getPassword().isEmpty()) {
            System.out.println("INFO: Creating database with password: " + databaseInfo.getName() + " [Password length: " + databaseInfo.getPassword().length() + "]");
        } else {
            System.out.println("INFO: Creating database without password: " + databaseInfo.getName());
        }

        DatabaseInfo saved = databaseInfoRepository.save(databaseInfo);
        
        // Verify password was saved correctly
        if (saved.getPassword() != null && !saved.getPassword().isEmpty()) {
            System.out.println("VERIFIED: Password is saved in database for: " + saved.getName() + " (ID: " + saved.getId() + ") [Length: " + saved.getPassword().length() + "]");
        } else {
            System.out.println("WARNING: Password is NULL or EMPTY in database for: " + saved.getName() + " (ID: " + saved.getId() + ")");
        }
        
        return saved;
    }

    /**
     * Update database information
     */
    public Optional<DatabaseInfo> updateDatabase(Long id, DatabaseInfo databaseInfo) {
        Optional<DatabaseInfo> existing = databaseInfoRepository.findById(id);
        
        if (existing.isPresent()) {
            DatabaseInfo dbToUpdate = existing.get();
            dbToUpdate.setName(databaseInfo.getName());
            dbToUpdate.setDatabaseType(databaseInfo.getDatabaseType());
            dbToUpdate.setHost(databaseInfo.getHost());
            dbToUpdate.setPort(databaseInfo.getPort());
            dbToUpdate.setDatabaseName(databaseInfo.getDatabaseName());
            dbToUpdate.setUsername(databaseInfo.getUsername());
            // Only update password if a new one is provided (not null and not empty)
            if (databaseInfo.getPassword() != null && !databaseInfo.getPassword().isEmpty()) {
                dbToUpdate.setPassword(databaseInfo.getPassword());
                System.out.println("INFO: Password updated for database: " + dbToUpdate.getName() + " (ID: " + id + ") [Length: " + databaseInfo.getPassword().length() + "]");
            } else {
                System.out.println("INFO: No password provided in update request for database: " + dbToUpdate.getName() + " (ID: " + id + ") - keeping existing password");
            }
            
            DatabaseInfo saved = databaseInfoRepository.save(dbToUpdate);
            
            // Verify password was saved correctly
            if (saved.getPassword() != null && !saved.getPassword().isEmpty()) {
                System.out.println("VERIFIED: Password is saved in database for: " + saved.getName() + " (ID: " + id + ") [Length: " + saved.getPassword().length() + "]");
            } else {
                System.out.println("WARNING: Password is NULL or EMPTY in database for: " + saved.getName() + " (ID: " + id + ")");
            }
            
            return Optional.of(saved);
        }
        
        return Optional.empty();
    }

    /**
     * Delete database registration
     */
    public boolean deleteDatabase(Long id) {
        if (databaseInfoRepository.existsById(id)) {
            databaseInfoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Find databases by type
     */
    public List<DatabaseInfo> getDatabasesByType(String databaseType) {
        return databaseInfoRepository.findByDatabaseType(databaseType);
    }
}

