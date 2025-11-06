package com.databaseai.dto;

import com.databaseai.model.User;

/**
 * AuthResponse DTO
 * 
 * Response body for registration and login endpoints.
 * 
 * Example:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "user": {
 *     "id": 1,
 *     "username": "john",
 *     "email": "john@example.com",
 *     "role": "USER"
 *   }
 * }
 */
public class AuthResponse {

    private String token;
    private UserInfo user;

    // Constructors
    public AuthResponse() {
    }

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = new UserInfo(user);
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    /**
     * UserInfo - Simplified user info (without password)
     */
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String role;

        public UserInfo() {
        }

        public UserInfo(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.role = user.getRole();
        }

        // Getters and Setters
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

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}

