package com.databaseai.repository;

import com.databaseai.model.DatabaseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DatabaseInfoRepository - Data Access Layer for DatabaseInfo Entity
 * 
 * Provides CRUD operations for database connection information.
 * 
 * @Repository = Spring manages this automatically
 * extends JpaRepository<DatabaseInfo, Long> = Free CRUD methods!
 */
@Repository
public interface DatabaseInfoRepository extends JpaRepository<DatabaseInfo, Long> {

    /**
     * Find database by name
     * 
     * Spring generates: SELECT * FROM database_info WHERE name = ?
     */
    Optional<DatabaseInfo> findByName(String name);

    /**
     * Find all databases by type (PostgreSQL, MySQL, etc.)
     * 
     * Spring generates: SELECT * FROM database_info WHERE database_type = ?
     */
    List<DatabaseInfo> findByDatabaseType(String databaseType);

    /**
     * Find databases by host
     * 
     * Spring generates: SELECT * FROM database_info WHERE host = ?
     */
    List<DatabaseInfo> findByHost(String host);

    /**
     * Check if database name already exists
     */
    boolean existsByName(String name);
}

