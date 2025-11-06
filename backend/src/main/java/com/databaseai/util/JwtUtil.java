package com.databaseai.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility Class
 * 
 * What is JWT?
 * - JWT = JSON Web Token
 * - A secure way to transmit information between parties
 * - Contains user info (username, role) encoded in the token
 * - Signed with a secret key (prevents tampering)
 * 
 * How it works:
 * 1. User logs in → System generates JWT token
 * 2. Token contains: username, role, expiration time
 * 3. Token is signed with secret key
 * 4. User sends token in Authorization header for each request
 * 5. System validates token and extracts user info
 * 
 * Token Structure:
 * - Header: Algorithm and token type
 * - Payload: User data (claims)
 * - Signature: Secret key signature (prevents tampering)
 * 
 * Example Token:
 * eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImpvaG4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTY5OTk5OTk5OX0.signature
 */
@Component
public class JwtUtil {

    /**
     * Secret key for signing JWT tokens
     * 
     * MUST be set via environment variable JWT_SECRET or in .env file
     * - At least 256 bits (32 characters)
     * - Never committed to git
     * - Loaded from .env file via DotEnvConfig
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token expiration time in milliseconds
     * Default: 24 hours (86400000 ms)
     */
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    /**
     * Get secret key for signing
     */
    private SecretKey getSigningKey() {
        // Ensure secret is at least 32 characters (256 bits)
        String key = secret;
        if (key.length() < 32) {
            key = key + "0".repeat(32 - key.length());
        }
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    /**
     * Generate JWT token for a user
     * 
     * @param username User's username
     * @param role User's role (USER or ADMIN)
     * @return JWT token string
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);
        return createToken(claims, username);
    }

    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract role from token
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token
     * 
     * @param token JWT token
     * @param username Username to validate against
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate token (without username check)
     * 
     * @param token JWT token
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}

