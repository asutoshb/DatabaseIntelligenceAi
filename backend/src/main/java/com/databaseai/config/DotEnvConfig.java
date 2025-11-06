package com.databaseai.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import jakarta.annotation.PostConstruct;

/**
 * DotEnv Configuration
 * 
 * This class loads variables from .env file into system properties.
 * Spring Boot will automatically read these system properties via @Value.
 * 
 * This allows us to use .env file for local development while keeping
 * environment variables for production deployment.
 * 
 * Priority: System environment variables > .env file > application.properties
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE) // Load early, before other configurations
public class DotEnvConfig {

    private static Dotenv dotenv;

    /**
     * Static block to load .env file as early as possible
     * This runs BEFORE Spring Boot starts, so we can set system properties
     * that @Value annotations will read.
     */
    static {
        try {
            // Load .env file from backend root directory (same level as pom.xml)
            dotenv = Dotenv.configure()
                    .directory("./")  // Look in backend root (backend/.env)
                    .ignoreIfMissing() // Don't fail if .env doesn't exist
                    .load();
            
            System.out.println("✅ .env file loaded successfully");
            
            // Set system properties IMMEDIATELY (before Spring Boot starts)
            // This ensures @Value annotations can read them
            if (dotenv != null) {
                // Load OpenAI API Key
                String apiKey = dotenv.get("OPENAI_API_KEY");
                if (apiKey != null && !apiKey.isEmpty()) {
                    System.setProperty("OPENAI_API_KEY", apiKey);
                }
                
                // Load JWT Secret (REQUIRED!)
                String jwtSecret = dotenv.get("JWT_SECRET");
                if (jwtSecret != null && !jwtSecret.isEmpty()) {
                    System.setProperty("JWT_SECRET", jwtSecret);
                    // Also set as jwt.secret for Spring Boot @Value to read
                    System.setProperty("jwt.secret", jwtSecret);
                    System.out.println("✅ JWT_SECRET loaded from .env file");
                } else {
                    System.err.println("⚠️  WARNING: JWT_SECRET not found in .env file!");
                    System.err.println("⚠️  Application may fail to start without JWT_SECRET!");
                }
                
                // Load JWT Expiration (optional)
                String jwtExpiration = dotenv.get("JWT_EXPIRATION");
                if (jwtExpiration != null && !jwtExpiration.isEmpty()) {
                    System.setProperty("JWT_EXPIRATION", jwtExpiration);
                    System.setProperty("jwt.expiration", jwtExpiration);
                }
            }
            
        } catch (Exception e) {
            // If .env file doesn't exist, that's okay
            System.out.println("ℹ️  .env file not found, using system environment variables");
        }
    }

    /**
     * PostConstruct method - logs confirmation that properties were loaded
     * 
     * Note: System properties are set in the static block (before Spring Boot starts)
     * This method just confirms they were loaded.
     */
    @PostConstruct
    public void confirmPropertiesLoaded() {
        if (dotenv != null) {
            String apiKey = System.getProperty("OPENAI_API_KEY");
            if (apiKey != null && !apiKey.isEmpty()) {
                System.out.println("✅ OPENAI_API_KEY confirmed loaded from .env file");
            }
            
            String jwtSecret = System.getProperty("jwt.secret");
            if (jwtSecret != null && !jwtSecret.isEmpty()) {
                System.out.println("✅ JWT_SECRET confirmed loaded from .env file");
            }
        }
    }

    /**
     * Get value from .env file (for direct access if needed)
     */
    public static String get(String key) {
        if (dotenv != null) {
            return dotenv.get(key);
        }
        return null;
    }

    /**
     * Get value from .env file with default
     */
    public static String get(String key, String defaultValue) {
        if (dotenv != null) {
            return dotenv.get(key, defaultValue);
        }
        return defaultValue;
    }
}

