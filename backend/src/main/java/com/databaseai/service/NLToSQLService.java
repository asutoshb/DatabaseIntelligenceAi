package com.databaseai.service;

import com.databaseai.dto.NLToSQLResponse;
import com.databaseai.model.DatabaseInfo;
import com.databaseai.repository.DatabaseInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Natural Language to SQL Conversion Service (with RAG)
 * 
 * This is the core service that converts natural language to SQL using RAG.
 * 
 * Process:
 * 1. Receive natural language query: "Show me top 5 customers"
 * 2. Retrieve relevant schema context using RAG
 * 3. Build enhanced prompt with schema context
 * 4. Call GPT to generate SQL
 * 5. Validate generated SQL
 * 6. Return SQL with explanation
 * 
 * This combines:
 * - RAGService (retrieval of relevant schemas)
 * - LLMService (GPT for SQL generation)
 * - SQLValidator (safety checks)
 */
@Service
public class NLToSQLService {

    @Autowired
    private RAGService ragService;

    @Autowired
    private LLMService llmService;

    @Autowired
    private SQLValidator sqlValidator;

    @Autowired
    private DatabaseInfoRepository databaseInfoRepository;

    @Autowired
    private RealTimeUpdateService realTimeUpdateService;

    /**
     * Convert natural language query to SQL using RAG
     * 
     * @param databaseInfoId Database to query
     * @param naturalLanguageQuery User's question
     * @param topK Number of relevant schemas to retrieve
     * @return NLToSQLResponse with generated SQL
     */
    public NLToSQLResponse convertToSQL(Long databaseInfoId, String naturalLanguageQuery, int topK) {
        return convertToSQL(databaseInfoId, naturalLanguageQuery, topK, null);
    }

    /**
     * Convert natural language query to SQL using RAG (with request correlation ID)
     */
    public NLToSQLResponse convertToSQL(Long databaseInfoId, String naturalLanguageQuery, int topK, String requestId) {
        String effectiveRequestId = (requestId != null && !requestId.isBlank())
                ? requestId
                : UUID.randomUUID().toString();

        Map<String, Object> meta = new HashMap<>();
        meta.put("databaseInfoId", databaseInfoId);
        meta.put("query", naturalLanguageQuery);
        meta.put("topK", topK);
        realTimeUpdateService.publishNlToSqlProgress(
                effectiveRequestId,
                "REQUEST_RECEIVED",
                "Received NL to SQL conversion request",
                meta
        );

        // Step 1: Verify database exists
        DatabaseInfo databaseInfo = null;
        try {
            databaseInfo = databaseInfoRepository.findById(databaseInfoId)
                    .orElseThrow(() -> new RuntimeException("Database not found with ID: " + databaseInfoId));
        } catch (RuntimeException e) {
            realTimeUpdateService.publishNlToSqlError(
                    effectiveRequestId,
                    "DATABASE_LOOKUP_FAILED",
                    e.getMessage(),
                    meta
            );
            throw e;
        }

        Map<String, Object> retrievalMeta = new HashMap<>();
        retrievalMeta.put("databaseInfoId", databaseInfoId);
        realTimeUpdateService.publishNlToSqlProgress(
                effectiveRequestId,
                "RETRIEVING_SCHEMA",
                "Retrieving relevant schema context",
                retrievalMeta
        );

        // Step 2: Retrieve relevant schemas using RAG (Retrieval step)
        List<RAGService.SchemaContext> relevantSchemas;
        try {
            relevantSchemas = ragService.retrieveRelevantSchemas(
                    databaseInfoId, naturalLanguageQuery, topK
            );
        } catch (Exception e) {
            Map<String, Object> retrievalErrorMeta = new HashMap<>();
            retrievalErrorMeta.put("databaseInfoId", databaseInfoId);
            realTimeUpdateService.publishNlToSqlError(
                    effectiveRequestId,
                    "RETRIEVAL_FAILED",
                    "Schema retrieval failed: " + e.getMessage(),
                    retrievalErrorMeta
            );
            throw new RuntimeException(e);
        }

        Map<String, Object> promptMeta = new HashMap<>();
        promptMeta.put("schemaCount", relevantSchemas.size());
        realTimeUpdateService.publishNlToSqlProgress(
                effectiveRequestId,
                "PROMPT_BUILDING",
                "Building prompt with schema context",
                promptMeta
        );

        // Step 3: Build enhanced prompt with schema context
        String systemPrompt = buildSystemPrompt(databaseInfo);
        String userPrompt = buildUserPrompt(naturalLanguageQuery, relevantSchemas);

        // Step 4: Generate SQL using GPT (Generation step)
        realTimeUpdateService.publishNlToSqlProgress(
                effectiveRequestId,
                "LLM_CALL",
                "Generating SQL with GPT",
                null
        );

        String generatedSQL;
        try {
            generatedSQL = llmService.generateText(userPrompt, systemPrompt);
        } catch (Exception e) {
            realTimeUpdateService.publishNlToSqlError(
                    effectiveRequestId,
                    "GENERATION_FAILED",
                    "LLM generation failed: " + e.getMessage(),
                    null
            );
            throw new RuntimeException(e);
        }

        // Step 5: Clean and validate SQL
        String cleanedSQL = cleanSQL(generatedSQL);
        SQLValidator.ValidationResult validation = sqlValidator.validate(cleanedSQL);
        int errorCount = validation.getErrors() != null ? validation.getErrors().size() : 0;

        Map<String, Object> validationMeta = new HashMap<>();
        validationMeta.put("isValid", validation.isValid());
        validationMeta.put("errorCount", errorCount);
        realTimeUpdateService.publishNlToSqlProgress(
                effectiveRequestId,
                "VALIDATION",
                "Validating generated SQL",
                validationMeta
        );

        // Step 6: Generate explanation
        String explanation = generateExplanation(naturalLanguageQuery, cleanedSQL, relevantSchemas);

        // Step 7: Build response
        NLToSQLResponse response = new NLToSQLResponse(cleanedSQL, naturalLanguageQuery, databaseInfoId);
        response.setRelevantSchemas(convertToResponseSchemas(relevantSchemas));
        response.setExplanation(explanation);
        response.setValid(validation.isValid());
        response.setValidationErrors(validation.getErrors());
        response.setRequestId(effectiveRequestId);

        Map<String, Object> successMeta = new HashMap<>();
        successMeta.put("isValid", validation.isValid());
        successMeta.put("schemaCount", relevantSchemas.size());
        realTimeUpdateService.publishNlToSqlSuccess(
                effectiveRequestId,
                "COMPLETED",
                "NL to SQL conversion completed",
                successMeta
        );

        return response;
    }

    /**
     * Build system prompt for GPT
     * 
     * This tells GPT what role it should play and what rules to follow.
     */
    private String buildSystemPrompt(DatabaseInfo databaseInfo) {
        return String.format(
                "You are an expert SQL query generator for %s database. " +
                "Convert natural language questions into valid SQL SELECT queries only. " +
                "Do not include DROP, DELETE, UPDATE, INSERT, or any data modification commands. " +
                "Return ONLY the SQL query, no explanations, no markdown formatting, no code blocks. " +
                "The SQL should be clean and ready to execute.",
                databaseInfo.getDatabaseType()
        );
    }

    /**
     * Build user prompt with schema context (RAG Augmentation)
     * 
     * This is where RAG magic happens - we give GPT the relevant schema context!
     */
    private String buildUserPrompt(String query, List<RAGService.SchemaContext> schemas) {
        StringBuilder prompt = new StringBuilder();

        // Add schema context (this is the RAG part!)
        if (!schemas.isEmpty()) {
            prompt.append("Database Schema Information:\n");
            prompt.append(ragService.buildContextString(schemas));
            prompt.append("\n");
        }

        // Add the user's question
        prompt.append("Question: ");
        prompt.append(query);
        prompt.append("\n\n");
        prompt.append("Generate a SQL SELECT query for this question. Use the schema information provided above.");

        return prompt.toString();
    }

    /**
     * Clean SQL query (remove markdown, code blocks, etc.)
     */
    private String cleanSQL(String sql) {
        if (sql == null) {
            return "";
        }

        // Remove markdown code blocks
        String cleaned = sql.trim()
                .replaceAll("^```sql\\s*", "")
                .replaceAll("^```\\s*", "")
                .replaceAll("```\\s*$", "")
                .trim();

        // Remove leading/trailing quotes if present
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        if (cleaned.startsWith("'") && cleaned.endsWith("'")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        return cleaned.trim();
    }

    /**
     * Generate explanation for the SQL query
     */
    private String generateExplanation(String query, String sql, List<RAGService.SchemaContext> schemas) {
        StringBuilder explanation = new StringBuilder();

        explanation.append("Generated SQL for: \"").append(query).append("\"\n\n");

        if (!schemas.isEmpty()) {
            explanation.append("Used schema context from: ");
            explanation.append(schemas.stream()
                    .map(RAGService.SchemaContext::getSchemaName)
                    .collect(Collectors.joining(", ")));
            explanation.append("\n");
        }

        explanation.append("\nSQL Query:\n").append(sql);

        return explanation.toString();
    }

    /**
     * Convert RAGService.SchemaContext to NLToSQLResponse.SchemaContext
     */
    private List<NLToSQLResponse.SchemaContext> convertToResponseSchemas(List<RAGService.SchemaContext> schemas) {
        return schemas.stream()
                .map(s -> new NLToSQLResponse.SchemaContext(s.getSchemaName(), s.getDescription()))
                .collect(Collectors.toList());
    }
}

