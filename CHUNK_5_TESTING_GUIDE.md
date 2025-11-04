# Chunk 5: NL to SQL with RAG - Testing Guide

## 🧪 Testing Overview

This guide will help you test the NL to SQL conversion feature with RAG.

**Prerequisites:**
- ✅ Backend running on `http://localhost:8080`
- ✅ Database created (via Chunk 2)
- ✅ Schema embeddings indexed (via Chunk 4)
- ✅ OpenAI API key configured (via `.env` file)

---

## 📋 Testing Checklist

### 1. ✅ Prerequisites Check

#### Check Backend is Running
```bash
curl http://localhost:8080/api/health
```

**Expected:**
```json
{
  "status": "UP",
  "message": "AI Database Intelligence Platform is running!",
  "timestamp": "2024-11-03T..."
}
```

#### Check Database Info
```bash
curl http://localhost:8080/api/database-info
```

**Expected:** List of databases with IDs

#### Check Schema Embeddings
```bash
curl http://localhost:8080/api/schema-embeddings/database/1
```

**Expected:** List of indexed schemas

---

### 2. 🎯 Test Basic NL to SQL Conversion

#### Test 1: Simple Query
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "Show me top 5 customers",
    "topK": 5
  }'
```

**Expected Response:**
```json
{
  "sqlQuery": "SELECT * FROM customers LIMIT 5",
  "naturalLanguageQuery": "Show me top 5 customers",
  "relevantSchemas": [
    {
      "schemaName": "customers",
      "schemaDescription": "customers table with id, name, revenue columns"
    }
  ],
  "explanation": "Generated SQL for: 'Show me top 5 customers'...",
  "isValid": true,
  "validationErrors": [],
  "databaseInfoId": 1
}
```

**What to Check:**
- ✅ `sqlQuery` is valid SQL
- ✅ `relevantSchemas` contains relevant schemas
- ✅ `isValid` is `true`
- ✅ SQL is a SELECT query

---

#### Test 2: Query with Ordering
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "Show me customers ordered by revenue descending",
    "topK": 5
  }'
```

**Expected:**
```json
{
  "sqlQuery": "SELECT * FROM customers ORDER BY revenue DESC",
  ...
}
```

---

#### Test 3: Query with Filtering
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "Show me customers where revenue is greater than 1000",
    "topK": 5
  }'
```

**Expected:**
```json
{
  "sqlQuery": "SELECT * FROM customers WHERE revenue > 1000",
  ...
}
```

---

### 3. 🔍 Test RAG Retrieval

#### Test: Verify Relevant Schemas
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "Show me orders",
    "topK": 3
  }'
```

**What to Check:**
- ✅ `relevantSchemas` contains schemas related to "orders"
- ✅ Schema names match the query intent
- ✅ Number of schemas matches `topK` (or less if fewer available)

---

### 4. 🔐 Test SQL Validation

#### Test 1: Valid SQL (Should Pass)
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "SELECT * FROM customers",
    "topK": 5
  }'
```

**Expected:**
- ✅ `isValid: true`
- ✅ `validationErrors: []`

---

#### Test 2: Invalid SQL - Dangerous Keyword
**Note:** This test requires the LLM to generate invalid SQL, which is unlikely. 
Instead, test the validator directly by checking the response after generation.

If GPT accidentally generates dangerous SQL, you should see:
```json
{
  "sqlQuery": "...",
  "isValid": false,
  "validationErrors": [
    "Dangerous SQL keyword detected: DROP. Only SELECT queries are allowed."
  ]
}
```

---

### 5. ⚠️ Test Error Handling

#### Test 1: Invalid Database ID
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 999,
    "naturalLanguageQuery": "Show me customers",
    "topK": 5
  }'
```

**Expected:**
```json
{
  "sqlQuery": "",
  "naturalLanguageQuery": "Show me customers",
  "databaseInfoId": 999,
  "isValid": false,
  "validationErrors": [
    "Error: Database not found with ID: 999"
  ]
}
```

---

#### Test 2: Missing Required Fields
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "naturalLanguageQuery": "Show me customers"
  }'
```

**Expected:** HTTP 400 Bad Request (validation error)

---

#### Test 3: Empty Query
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "",
    "topK": 5
  }'
```

**Expected:** HTTP 400 Bad Request (validation error)

---

### 6. 📊 Test Status Endpoint

```bash
curl http://localhost:8080/api/nl-to-sql/status
```

**Expected:**
```json
{
  "sqlQuery": "",
  "naturalLanguageQuery": "Status check",
  "isValid": true,
  "explanation": "NL to SQL service is running"
}
```

---

## 🎯 Complete Test Scenario

### Scenario: Query Customer Data

**Step 1: Index Customer Schema**
```bash
curl -X POST http://localhost:8080/api/schema-embeddings/index \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "schemaName": "customers",
    "schemaDescription": "customers table with id, name, email, revenue columns"
  }'
```

**Step 2: Query with NL**
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "Show me top 5 customers by revenue",
    "topK": 5
  }'
```

**Step 3: Verify Response**
- ✅ SQL is generated correctly
- ✅ Relevant schemas include "customers"
- ✅ SQL is valid SELECT query
- ✅ Explanation is provided

---

## 🔧 Using Postman

### Import Collection

1. **Create New Request:**
   - Method: `POST`
   - URL: `http://localhost:8080/api/nl-to-sql/convert`
   - Headers: `Content-Type: application/json`

2. **Body (JSON):**
```json
{
  "databaseInfoId": 1,
  "naturalLanguageQuery": "Show me top 5 customers",
  "topK": 5
}
```

3. **Send Request**

4. **Verify Response:**
   - Status: `200 OK`
   - Body contains `sqlQuery`, `isValid: true`

---

## 📝 Test Cases Summary

| Test Case | Input | Expected Result |
|-----------|-------|----------------|
| Simple Query | "Show me top 5 customers" | Valid SELECT SQL |
| Query with Ordering | "Show customers by revenue" | SQL with ORDER BY |
| Query with Filtering | "Show customers where revenue > 1000" | SQL with WHERE |
| RAG Retrieval | Any query | Relevant schemas in response |
| Invalid Database | databaseInfoId: 999 | Error response |
| Missing Fields | Empty request | 400 Bad Request |
| Status Check | GET /status | Service running |

---

## 🐛 Troubleshooting

### Issue: "Database not found"
**Solution:** Create database first via `/api/database-info` endpoint

### Issue: "No relevant schemas found"
**Solution:** Index schemas first via `/api/schema-embeddings/index` endpoint

### Issue: "OpenAI API key invalid"
**Solution:** Check `.env` file has correct `OPENAI_API_KEY`

### Issue: "Rate limit exceeded"
**Solution:** Wait a minute and retry, or upgrade OpenAI plan

### Issue: SQL validation fails
**Solution:** Check if GPT generated invalid SQL. Try rephrasing the query.

---

## ✅ Success Criteria

**Chunk 5 is working correctly if:**
- ✅ Can convert natural language to SQL
- ✅ Relevant schemas are retrieved (RAG works)
- ✅ SQL is validated for security
- ✅ Error handling works for invalid inputs
- ✅ Status endpoint returns service status

---

## 🎉 Next Steps

After testing Chunk 5:
1. **Chunk 6:** Execute the generated SQL safely
2. **Chunk 7:** Add authentication and authorization
3. **Frontend:** Build UI for NL queries

---

**Happy Testing! 🚀**

