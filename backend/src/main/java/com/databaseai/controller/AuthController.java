package com.databaseai.controller;

import com.databaseai.dto.AuthResponse;
import com.databaseai.dto.LoginRequest;
import com.databaseai.dto.RegisterRequest;
import com.databaseai.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthController - REST API for Authentication
 * 
 * Handles:
 * - User registration
 * - User login
 * 
 * These endpoints are PUBLIC (no authentication required)
 * as defined in SecurityConfig.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * POST /api/auth/register
     * 
     * Register a new user
     * 
     * Request body:
     * {
     *   "username": "john",
     *   "email": "john@example.com",
     *   "password": "password123",
     *   "role": "USER" (optional, defaults to USER)
     * }
     * 
     * Response:
     * {
     *   "token": "eyJhbGci...",
     *   "user": {
     *     "id": 1,
     *     "username": "john",
     *     "email": "john@example.com",
     *     "role": "USER"
     *   }
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * POST /api/auth/login
     * 
     * Login user
     * 
     * Request body:
     * {
     *   "username": "john" (or email),
     *   "password": "password123"
     * }
     * 
     * Response:
     * {
     *   "token": "eyJhbGci...",
     *   "user": {
     *     "id": 1,
     *     "username": "john",
     *     "email": "john@example.com",
     *     "role": "USER"
     *   }
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authenticationService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}

