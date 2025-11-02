package com.databaseai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class
 * 
 * This is the entry point of our Spring Boot application.
 * When you run this, Spring Boot starts the embedded server (Tomcat)
 * and makes your application available at http://localhost:8080
 * 
 * @SpringBootApplication does three things:
 * 1. @EnableAutoConfiguration - Spring automatically configures based on dependencies
 * 2. @ComponentScan - Scans for components (@Service, @Repository, @Controller)
 * 3. @Configuration - Marks this as a configuration class
 */
@SpringBootApplication
public class AiDatabaseIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDatabaseIntelligenceApplication.class, args);
        System.out.println("\n🚀 AI Database Intelligence Platform is running!");
        System.out.println("📍 API available at: http://localhost:8080/api\n");
    }
}

