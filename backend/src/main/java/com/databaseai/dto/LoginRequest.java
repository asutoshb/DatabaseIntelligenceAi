package com.databaseai.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * LoginRequest DTO
 * 
 * Request body for user login endpoint.
 * 
 * Example:
 * {
 *   "username": "john",
 *   "password": "password123"
 * }
 * 
 * Note: username can be either username or email
 */
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    private String username; // Can be username or email

    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
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
}

