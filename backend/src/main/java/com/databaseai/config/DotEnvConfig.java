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
     */
    static {
        try {
            // Load .env file from backend root directory (same level as pom.xml)
            dotenv = Dotenv.configure()
                    .directory("./")  // Look in backend root (backend/.env)
                    .ignoreIfMissing() // Don't fail if .env doesn't exist
                    .load();
            
            System.out.println("✅ .env file loaded successfully");
            
        } catch (Exception e) {
            // If .env file doesn't exist, that's okay
            System.out.println("ℹ️  .env file not found, using system environment variables");
        }
    }

    /**
     * Set system properties from .env file
     * 
     * This runs early in Spring Boot initialization.
     * System properties set here will be read by @Value annotations.
     */
    @PostConstruct
    public void setSystemProperties() {
        if (dotenv != null) {
            String apiKey = dotenv.get("OPENAI_API_KEY");
            if (apiKey != null && !apiKey.isEmpty()) {
                // Set as system property so Spring Boot can read it
                System.setProperty("OPENAI_API_KEY", apiKey);
                System.out.println("✅ OPENAI_API_KEY loaded from .env file");
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

