package com.databaseai.service;

import com.databaseai.model.User;
import com.databaseai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserService - Business Logic Layer
 * 
 * This is where business logic goes.
 * Controllers call Services, Services call Repositories.
 * 
 * Why have a Service layer?
 * - Keeps controllers simple
 * - Reusable logic
 * - Easy to test
 * - Can add validation, logging, etc.
 * 
 * @Service = Tells Spring: "This is a service, manage it automatically"
 */
@Service
public class UserService {

    /**
     * @Autowired = Spring automatically injects UserRepository
     * 
     * What is Dependency Injection?
     * - You don't create objects manually: UserRepository repo = new UserRepository();
     * - Spring creates and gives it to you automatically!
     * - Makes code testable and flexible
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Get all users
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User if found, empty otherwise
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Create a new user
     * 
     * Business logic: Check if username/email already exists
     * 
     * @param user User to create
     * @return Created user
     * @throws IllegalArgumentException if username/email already exists
     */
    public User createUser(User user) {
        // Validate: Check if username exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }

        // Validate: Check if email exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Save to database
        return userRepository.save(user);
    }

    /**
     * Update an existing user
     * 
     * @param id User ID
     * @param user Updated user data
     * @return Updated user if found, empty otherwise
     */
    public Optional<User> updateUser(Long id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        
        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            return Optional.of(userRepository.save(userToUpdate));
        }
        
        return Optional.empty();
    }

    /**
     * Delete a user
     * 
     * @param id User ID
     * @return true if deleted, false if not found
     */
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Find user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

