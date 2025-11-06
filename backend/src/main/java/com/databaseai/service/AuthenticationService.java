package com.databaseai.service;

import com.databaseai.dto.AuthResponse;
import com.databaseai.dto.LoginRequest;
import com.databaseai.dto.RegisterRequest;
import com.databaseai.model.User;
import com.databaseai.repository.UserRepository;
import com.databaseai.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Authentication Service
 * 
 * Handles user registration and login with password hashing and JWT token generation.
 * 
 * What is Password Hashing?
 * - We NEVER store plain passwords in the database
 * - Instead, we hash passwords using bcrypt
 * - Bcrypt is a one-way function (can't reverse it)
 * - When user logs in, we hash their password and compare with stored hash
 * 
 * Why Bcrypt?
 * - Slow by design (prevents brute force attacks)
 * - Includes salt (prevents rainbow table attacks)
 * - Industry standard for password hashing
 * 
 * Flow:
 * 1. Registration: Hash password → Store user → Generate JWT
 * 2. Login: Find user → Verify password → Generate JWT
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user
     * 
     * @param request Registration request (username, email, password)
     * @return AuthResponse with JWT token and user info
     * @throws RuntimeException if username or email already exists
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Hash password (NEVER store plain password!)
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                request.getRole() != null ? request.getRole() : "USER"
        );

        // Save user to database
        user = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Return response with token and user info
        return new AuthResponse(token, user);
    }

    /**
     * Login user
     * 
     * @param request Login request (username/email, password)
     * @return AuthResponse with JWT token and user info
     * @throws RuntimeException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        // Find user by username or email
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()));

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOpt.get();

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Return response with token and user info
        return new AuthResponse(token, user);
    }
}

