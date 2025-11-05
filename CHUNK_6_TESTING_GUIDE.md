# Chunk 6: Query Execution Service - Testing Guide

## 🎯 Testing Overview

This guide will help you test the **Query Execution Service** that safely executes SQL queries against user databases.

---

## ✅ Prerequisites

1. **Backend Running**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. **Database Setup**
   - PostgreSQL running on `localhost:5432`
   - Database `database_intelligence` exists
   - At least one `DatabaseInfo` record exists

3. **Test Database**
   - You need a test database with some tables and data
   - Or use the same `database_intelligence` database

---

## 📝 Step 1: Create/Verify Database Info

**💡 Important Note:** In a real application, users would register their databases through a UI form. Here, we're simulating that by creating a database record via API. See `HOW_DATABASE_INFO_WORKS.md` for a detailed explanation of how this works in production.

First, ensure you have a `DatabaseInfo` record. If not, create one:

```bash
curl -X POST http://localhost:8080/api/databases \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Database",
    "databaseType": "PostgreSQL",
    "host": "localhost",
    "port": 5432,
    "databaseName": "database_intelligence",
    "username": "asutoshbhere"
  }'
```

**What this does:**
- Creates a record in the `database_info` table
- Stores connection details (host, port, database name, username)
- Returns a database ID (e.g., `1`) that you'll use in all future queries

**In real app:** This would be done through a "Connect Database" form in the UI, where users enter their database connection details once, and then use the database ID for all future queries.

**Expected Response:**
```json
{
  "id": 1,
  "name": "Test Database",
  "databaseType": "PostgreSQL",
  "host": "localhost",
  "port": 5432,
  "databaseName": "database_intelligence",
  "username": "asutoshbhere",
  "createdAt": "2025-11-04T15:30:00"
}
```

**Note:** Save the `id` value (e.g., `1`) - you'll need it for testing!

---

## 📝 Step 2: Create Test Table and Data

**💡 Important:** After creating the table, you need to **index the schema** for RAG to work properly. See `HOW_TO_INDEX_SCHEMA.md` for details.

Connect to your PostgreSQL database and create a test table:

```sql
-- Connect to database
psql -U asutoshbhere -d database_intelligence

-- Create test table
CREATE TABLE IF NOT EXISTS test_customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test data
INSERT INTO test_customers (name, email) VALUES
    ('John Doe', 'john@example.com'),
    ('Jane Smith', 'jane@example.com'),
    ('Bob Johnson', 'bob@example.com'),
    ('Alice Williams', 'alice@example.com'),
    ('Charlie Brown', 'charlie@example.com');

-- Verify data
SELECT * FROM test_customers;
```

---

## 📝 Step 3: Test Health Check Endpoint

**Test:** Service status

```bash
curl http://localhost:8080/api/query-execution/status
```

**Expected Response:**
```json
{
  "service": "Query Execution Service",
  "status": "UP",
  "message": "Query execution service is running"
}
```

---

## 📝 Step 4: Test Database Connection

**Test:** Verify we can connect to the database

```bash
curl http://localhost:8080/api/query-execution/test-connection/1
```

**Replace `1` with your actual `databaseInfoId`!**

**Expected Response (Success):**
```json
{
  "databaseInfoId": 1,
  "connected": true,
  "message": "Database connection successful"
}
```

**Expected Response (Failure):**
```json
{
  "databaseInfoId": 1,
  "connected": false,
  "message": "Database connection failed"
}
```

---

## 📝 Step 5: Execute Valid SELECT Query

**Test:** Execute a simple SELECT query

```bash
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "sqlQuery": "SELECT * FROM test_customers LIMIT 5",
    "timeoutSeconds": 30
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "rows": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "created_at": "2025-11-04T15:30:00"
    },
    {
      "id": 2,
      "name": "Jane Smith",
      "email": "jane@example.com",
      "created_at": "2025-11-04T15:30:00"
    },
    {
      "id": 3,
      "name": "Bob Johnson",
      "email": "bob@example.com",
      "created_at": "2025-11-04T15:30:00"
    },
    {
      "id": 4,
      "name": "Alice Williams",
      "email": "alice@example.com",
      "created_at": "2025-11-04T15:30:00"
    },
    {
      "id": 5,
      "name": "Charlie Brown",
      "email": "charlie@example.com",
      "created_at": "2025-11-04T15:30:00"
    }
  ],
  "columns": ["id", "name", "email", "created_at"],
  "rowCount": 5,
  "executionTimeMs": 12,
  "executedAt": "2025-11-04T15:30:00",
  "sqlQuery": "SELECT * FROM test_customers LIMIT 5",
  "databaseInfoId": 1
}
```

---

## 📝 Step 6: Test Query with WHERE Clause

**Test:** Execute query with conditions

```bash
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "sqlQuery": "SELECT id, name FROM test_customers WHERE id > 2",
    "timeoutSeconds": 30
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "rows": [
    { "id": 3, "name": "Bob Johnson" },
    { "id": 4, "name": "Alice Williams" },
    { "id": 5, "name": "Charlie Brown" }
  ],
  "columns": ["id", "name"],
  "rowCount": 3,
  "executionTimeMs": 8,
  "executedAt": "2025-11-04T15:30:00",
  "sqlQuery": "SELECT id, name FROM test_customers WHERE id > 2",
  "databaseInfoId": 1
}
```

---

## 📝 Step 7: Test SQL Validation (Dangerous Keywords)

**Test:** Try to execute a dangerous query (should be blocked)

```bash
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "sqlQuery": "DROP TABLE test_customers",
    "timeoutSeconds": 30
  }'
```

**Expected Response:**
```json
{
  "success": false,
  "errorMessage": "SQL validation failed: Dangerous SQL keyword detected: DROP. Only SELECT queries are allowed.",
  "executionTimeMs": 1,
  "sqlQuery": "DROP TABLE test_customers",
  "databaseInfoId": 1
}
```

**Test Other Dangerous Keywords:**
```bash
# DELETE
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "sqlQuery": "DELETE FROM test_customers WHERE id = 1"
  }'

# UPDATE
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "sqlQuery": "UPDATE test_customers SET name = \"Hacked\""
  }'

# INSERT
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "sqlQuery": "INSERT INTO test_customers (name, email) VALUES (\"Test\", \"test@example.com\")"
  }'
```

**All should return validation errors!**

---

## 📝 Step 8: Test SQL Validation (Non-SELECT Query)

**Test:** Try to execute a non-SELECT query

```bash
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "sqlQuery": "SHOW TABLES"
  }'
```

**Expected Response:**
```json
{
  "success": false,
  "errorMessage": "SQL validation failed: Only SELECT queries are allowed.",
  "executionTimeMs": 1,
  "sqlQuery": "SHOW TABLES",
  "databaseInfoId": 1
}
```

---

## 📝 Step 9: Test Empty Result Set

**Test:** Query that returns no results

```bash
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "sqlQuery": "SELECT * FROM test_customers WHERE id = 999",
    "timeoutSeconds": 30
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "rows": [],
  "columns": ["id", "name", "email", "created_at"],
  "rowCount": 0,
  "executionTimeMs": 5,
  "executedAt": "2025-11-04T15:30:00",
  "sqlQuery": "SELECT * FROM test_customers WHERE id = 999",
  "databaseInfoId": 1
}
```

---

## 📝 Step 10: Test Invalid Database ID

**Test:** Try to execute query with non-existent database ID

```bash
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 999,
    "sqlQuery": "SELECT * FROM test_customers",
    "timeoutSeconds": 30
  }'
```

**Expected Response:**
```json
{
  "success": false,
  "errorMessage": "Database not found with ID: 999",
  "executionTimeMs": 1,
  "sqlQuery": "SELECT * FROM test_customers",
  "databaseInfoId": 999
}
```

---

## 📝 Step 11: Test Invalid SQL Syntax

**Test:** Try to execute invalid SQL

```bash
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "sqlQuery": "SELECT * FROM test_customers WHERE (",
    "timeoutSeconds": 30
  }'
```

**Expected Response:**
```json
{
  "success": false,
  "errorMessage": "SQL validation failed: Unbalanced parentheses in SQL query.",
  "executionTimeMs": 1,
  "sqlQuery": "SELECT * FROM test_customers WHERE (",
  "databaseInfoId": 1
}
```

---

## 📝 Step 12: Test Query Timeout

**Test:** Execute a query that takes too long (optional - requires a slow query)

```bash
# Create a slow query (if you have a large table)
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "sqlQuery": "SELECT * FROM large_table WHERE complex_condition",
    "timeoutSeconds": 5
  }'
```

**Expected Response (if timeout occurs):**
```json
{
  "success": false,
  "errorMessage": "Query timeout: Query took longer than 5 seconds",
  "executionTimeMs": 5000,
  "sqlQuery": "SELECT * FROM large_table WHERE complex_condition",
  "databaseInfoId": 1
}
```

---

## 📝 Step 13: Test Complete Flow (NL → SQL → Execute)

**Test:** Complete flow from natural language to executed SQL

### Step 13a: Generate SQL from NL (Chunk 5)

```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "naturalLanguageQuery": "Show me all customers",
    "topK": 5
  }'
```

**Expected Response:**
```json
{
  "sqlQuery": "SELECT * FROM test_customers",
  "naturalLanguageQuery": "Show me all customers",
  "databaseInfoId": 2,
  "valid": true,
  "validationErrors": []
}
```

**Copy the `sqlQuery` value!**

### Step 13b: Execute Generated SQL (Chunk 6)

```bash
curl -X POST http://localhost:8080/api/query-execution/execute \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "sqlQuery": "SELECT * FROM test_customers",
    "timeoutSeconds": 30
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "rows": [
    { "id": 1, "name": "John Doe", "email": "john@example.com" },
    { "id": 2, "name": "Jane Smith", "email": "jane@example.com" },
    ...
  ],
  "columns": ["id", "name", "email", "created_at"],
  "rowCount": 5,
  "executionTimeMs": 10,
  "executedAt": "2025-11-04T15:30:00"
}
```

**🎉 Complete flow working!**

---

## 🔍 Troubleshooting

### Error: "Database not found"
- **Solution:** Create a `DatabaseInfo` record first (Step 1)

### Error: "Connection refused"
- **Solution:** Ensure PostgreSQL is running and connection details are correct

### Error: "SQL validation failed"
- **Solution:** Ensure SQL is a SELECT query only, no dangerous keywords

### Error: "Table does not exist"
- **Solution:** Create test table first (Step 2)

### Error: "Query timeout"
- **Solution:** Increase `timeoutSeconds` or optimize your query

---

## ✅ Testing Checklist

- [ ] Health check endpoint works
- [ ] Database connection test works
- [ ] Valid SELECT query executes successfully
- [ ] Results are returned in correct format
- [ ] SQL validation blocks dangerous keywords
- [ ] SQL validation blocks non-SELECT queries
- [ ] Empty result set handled correctly
- [ ] Invalid database ID handled correctly
- [ ] Invalid SQL syntax handled correctly
- [ ] Query timeout works (if applicable)
- [ ] Complete flow (NL → SQL → Execute) works

---

## 🎉 Success!

If all tests pass, **Chunk 6 is complete!** You now have:
- ✅ Safe SQL query execution
- ✅ Read-only connections
- ✅ Query timeout protection
- ✅ Result processing to JSON
- ✅ Complete error handling

**Next:** Chunk 7 - Authentication & Authorization! 🔐

