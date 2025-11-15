package com.databaseai.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * DatabaseInfo Entity
 * 
 * This represents information about databases that users want to query.
 * For example: A user registers their PostgreSQL database, and we store
 * connection info here.
 * 
 * @Entity = Database table
 * @Table = Table name in database
 */
@Entity
@Table(name = "database_info")
public class DatabaseInfo {

    /**
     * Primary Key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the database (user-friendly name)
     * Example: "Production Database", "Customer DB"
     */
    @Column(nullable = false)
    private String name;

    /**
     * Database type (PostgreSQL, MySQL, Oracle, etc.)
     */
    @Column(nullable = false)
    private String databaseType;

    /**
     * Host address (where database is running)
     * Example: "localhost", "db.example.com"
     */
    @Column(nullable = false)
    private String host;

    /**
     * Port number
     * Example: 5432 (PostgreSQL), 3306 (MySQL)
     */
    @Column(nullable = false)
    private Integer port;

    /**
     * Database name
     */
    @Column(nullable = false)
    private String databaseName;

    /**
     * Username for database connection
     * Note: In production, this should be encrypted!
     */
    @Column(nullable = false)
    private String username;

    /**
     * Password for database connection
     * Note: In production, this should be encrypted!
     * Optional - some databases don't require passwords
     * 
     * WRITE_ONLY allows password to be set via POST/PUT requests (deserialization)
     * but prevents it from being returned in GET responses (serialization)
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = true)
    private String password;

    /**
     * Timestamp when this database was registered
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this database info was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public DatabaseInfo() {
    }

    // Constructor
    public DatabaseInfo(String name, String databaseType, String host, 
                       Integer port, String databaseName, String username) {
        this.name = name;
        this.databaseType = databaseType;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Check if password is set (without revealing it)
     * Used for API responses to indicate if password exists
     * @JsonProperty ensures this is included in JSON responses as "hasPassword"
     */
    @JsonProperty("hasPassword")
    public boolean isHasPassword() {
        return password != null && !password.isEmpty();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // JPA lifecycle callbacks - automatically called by JPA

    /**
     * Called before INSERT
     * Sets createdAt timestamp automatically
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Called before UPDATE
     * Sets updatedAt timestamp automatically
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "DatabaseInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", databaseName='" + databaseName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

