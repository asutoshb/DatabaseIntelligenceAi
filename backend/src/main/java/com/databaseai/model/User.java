package com.databaseai.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * User Entity
 * 
 * This represents the "users" table in our database.
 * 
 * @Entity = Tells JPA: "This class is a database table"
 * @Table = Specifies the table name (optional, defaults to class name)
 * 
 * When this class is loaded, JPA automatically creates a table:
 * CREATE TABLE users (
 *     id BIGSERIAL PRIMARY KEY,
 *     username VARCHAR(255),
 *     email VARCHAR(255),
 *     created_at TIMESTAMP
 * );
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Primary Key - Unique identifier for each user
     * 
     * @Id = This field is the primary key
     * @GeneratedValue = Auto-increment (PostgreSQL generates ID automatically)
     * 
     * Example: First user gets id=1, second gets id=2, etc.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username - must be unique
     * 
     * @Column = Customize column properties
     * unique = true means no two users can have same username
     * nullable = false means username is required
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Email address
     */
    @Column(nullable = false)
    private String email;

    /**
     * Timestamp when user was created
     * 
     * @CreationTimestamp = Hibernate automatically sets this when user is created
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when user was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor (required by JPA)
    public User() {
    }

    // Constructor for creating new users
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters (required for JPA to access fields)
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

