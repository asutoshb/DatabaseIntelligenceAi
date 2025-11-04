package com.databaseai.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SQL Validator Service
 * 
 * Validates SQL queries for security and correctness.
 * 
 * Security Checks:
 * - Prevents dangerous operations (DROP, DELETE, UPDATE, TRUNCATE, etc.)
 * - Checks for SQL injection patterns
 * - Ensures only SELECT queries are allowed (for now)
 * 
 * Basic Syntax Checks:
 * - Ensures SQL is not empty
 * - Checks for balanced parentheses
 * - Validates basic SQL structure
 */
@Service
public class SQLValidator {

    /**
     * Dangerous SQL keywords that should be blocked
     */
    private static final List<String> DANGEROUS_KEYWORDS = List.of(
            "DROP", "DELETE", "UPDATE", "INSERT", "TRUNCATE",
            "ALTER", "CREATE", "GRANT", "REVOKE", "EXEC", "EXECUTE"
    );

    /**
     * SQL injection patterns to detect
     */
    private static final List<Pattern> SQL_INJECTION_PATTERNS = List.of(
            Pattern.compile("(?i).*;\\s*DROP\\s+TABLE.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i).*;\\s*DELETE\\s+FROM.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i).*UNION\\s+SELECT.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i).*OR\\s+1\\s*=\\s*1.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?i).*'\\s*OR\\s*'.*", Pattern.CASE_INSENSITIVE)
    );

    /**
     * Validate SQL query
     * 
     * @param sql SQL query to validate
     * @return Validation result with errors (if any)
     */
    public ValidationResult validate(String sql) {
        List<String> errors = new ArrayList<>();

        if (sql == null || sql.trim().isEmpty()) {
            errors.add("SQL query cannot be empty");
            return new ValidationResult(false, errors);
        }

        String sqlUpper = sql.trim().toUpperCase();

        // Check for dangerous keywords
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (sqlUpper.contains(keyword)) {
                errors.add("Dangerous SQL keyword detected: " + keyword + ". Only SELECT queries are allowed.");
            }
        }

        // Check for SQL injection patterns
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(sql).find()) {
                errors.add("Potential SQL injection detected");
            }
        }

        // Check for balanced parentheses
        if (!areParenthesesBalanced(sql)) {
            errors.add("Unbalanced parentheses in SQL query");
        }

        // Ensure it's a SELECT query (for now, we only allow SELECT)
        if (!sqlUpper.trim().startsWith("SELECT")) {
            errors.add("Only SELECT queries are allowed");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Check if parentheses are balanced
     */
    private boolean areParenthesesBalanced(String sql) {
        int count = 0;
        for (char c : sql.toCharArray()) {
            if (c == '(') count++;
            if (c == ')') count--;
            if (count < 0) return false; // More closing than opening
        }
        return count == 0; // Balanced if count is 0
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final List<String> errors;

        public ValidationResult(boolean isValid, List<String> errors) {
            this.isValid = isValid;
            this.errors = errors;
        }

        public boolean isValid() {
            return isValid;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}

