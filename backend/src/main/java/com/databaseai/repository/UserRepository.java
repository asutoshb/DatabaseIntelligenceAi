package com.databaseai.repository;

import com.databaseai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Data Access Layer for User Entity
 * 
 * This is an INTERFACE (not a class). Spring automatically implements it!
 * 
 * @Repository = Tells Spring: "This is a repository, manage it automatically"
 * extends JpaRepository<User, Long> = Gives us free CRUD methods!
 * 
 * JpaRepository<User, Long> means:
 * - User = Entity type
 * - Long = Type of the ID (primary key)
 * 
 * Spring automatically provides these methods:
 * - findById(Long id) - Find user by ID
 * - findAll() - Get all users
 * - save(User user) - Create or update user
 * - deleteById(Long id) - Delete user by ID
 * - count() - Count total users
 * - existsById(Long id) - Check if user exists
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Custom method: Find user by username
     * 
     * Spring automatically generates SQL:
     * SELECT * FROM users WHERE username = ?
     * 
     * Method naming convention:
     * - findByUsername = Spring knows to query "username" field
     * - Optional = Returns empty if not found (better than null)
     */
    Optional<User> findByUsername(String username);

    /**
     * Custom method: Find user by email
     * 
     * Spring generates: SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);

    /**
     * Custom method: Check if username exists
     * 
     * Spring generates: SELECT COUNT(*) FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);

    /**
     * Custom method: Check if email exists
     */
    boolean existsByEmail(String email);
}

