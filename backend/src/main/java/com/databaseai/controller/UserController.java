package com.databaseai.controller;

import com.databaseai.model.User;
import com.databaseai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserController - REST API Endpoints for User Management
 * 
 * This controller handles HTTP requests for user operations.
 * 
 * @RestController = Combination of @Controller + @ResponseBody
 *   - @Controller = Marks this as a controller
 *   - @ResponseBody = Automatically converts return values to JSON
 * 
 * @RequestMapping("/users") = Base path for all endpoints in this controller
 *   - GET /api/users - Get all users
 *   - GET /api/users/1 - Get user by ID
 *   - POST /api/users - Create user
 *   - PUT /api/users/1 - Update user
 *   - DELETE /api/users/1 - Delete user
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/users
     * 
     * Get all users
     * 
     * @return List of all users (JSON)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id}
     * 
     * Get user by ID
     * 
     * @param id User ID (from URL path)
     * @return User if found, 404 if not found
     * 
     * Example: GET /api/users/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/users
     * 
     * Create a new user
     * 
     * @param user User data from request body (JSON)
     * @return Created user with HTTP 201 status
     * 
     * Example request body:
     * {
     *   "username": "john_doe",
     *   "email": "john@example.com"
     * }
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            // Return error if validation fails (username/email exists)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * PUT /api/users/{id}
     * 
     * Update an existing user
     * 
     * @param id User ID
     * @param user Updated user data
     * @return Updated user if found, 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user)
                .map(updatedUser -> ResponseEntity.ok(updatedUser))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/users/{id}
     * 
     * Delete a user
     * 
     * @param id User ID
     * @return 204 No Content if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }
        return ResponseEntity.notFound().build(); // HTTP 404
    }
}

