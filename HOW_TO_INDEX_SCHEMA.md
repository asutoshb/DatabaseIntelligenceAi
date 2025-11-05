# How to Index Schema for RAG (Fix: "customers" vs "test_customers")

## 🐛 The Problem

When you asked: "Show me all customers"

**Generated SQL:** `SELECT * FROM customers;` ❌

**Expected SQL:** `SELECT * FROM test_customers;` ✅

**Why?** The response shows `"relevantSchemas":[]` - meaning RAG couldn't find any schema information!

---

## 💡 The Solution

**You need to index your schema first!**

RAG (Retrieval-Augmented Generation) needs to know about your database tables before it can generate accurate SQL. You do this by "indexing" the schema - storing a description of your table with its embedding.

---

## 📝 Step-by-Step Fix

### Step 1: Index the `test_customers` Schema

**What this does:**
- Stores a description of your `test_customers` table
- Generates an embedding (vector) for that description
- Makes it searchable for RAG

```bash
curl -X POST http://localhost:8080/api/schema-embeddings/index \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "schemaName": "test_customers",
    "schemaDescription": "test_customers table with id (integer primary key), name (varchar), email (varchar), created_at (timestamp) columns. Stores customer information."
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "databaseInfoId": 2,
  "schemaName": "test_customers",
  "schemaDescription": "test_customers table with id (integer primary key), name (varchar), email (varchar), created_at (timestamp) columns. Stores customer information.",
  "createdAt": "2025-11-04T16:00:00"
}
```

**🎯 Important:** 
- Use `databaseInfoId: 2` (your database ID)
- Use `schemaName: "test_customers"` (your actual table name)
- Provide a detailed description with column names and types

---

### Step 2: Verify Schema is Indexed

```bash
curl http://localhost:8080/api/schema-embeddings/database/2
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "databaseInfoId": 2,
    "schemaName": "test_customers",
    "schemaDescription": "test_customers table with id (integer primary key), name (varchar), email (varchar), created_at (timestamp) columns. Stores customer information.",
    "createdAt": "2025-11-04T16:00:00"
  }
]
```

---

### Step 3: Try NL to SQL Again

Now when you ask "Show me all customers", RAG will:
1. Search for similar schemas
2. Find `test_customers` schema
3. Pass it to GPT
4. GPT generates SQL with correct table name!

```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "naturalLanguageQuery": "Show me all customers",
    "topK": 5
  }'
```

**Expected Response (Now with schema!):**
```json
{
  "sqlQuery": "SELECT * FROM test_customers",
  "naturalLanguageQuery": "Show me all customers",
  "relevantSchemas": [
    {
      "schemaName": "test_customers",
      "description": "test_customers table with id (integer primary key), name (varchar), email (varchar), created_at (timestamp) columns. Stores customer information."
    }
  ],
  "valid": true,
  "validationErrors": []
}
```

**✅ Notice:**
- SQL now uses `test_customers` (correct table name!)
- `relevantSchemas` is not empty (RAG found the schema!)

---

## 🎓 How RAG Works (Why This Matters)

### Without Schema Indexing (What Happened):

```
User: "Show me all customers"
    ↓
RAG Search: No schemas found → `relevantSchemas: []`
    ↓
GPT Prompt: "Generate SQL for: Show me all customers"
    (No schema context!)
    ↓
GPT: "SELECT * FROM customers"  ❌ (Guessed wrong table name!)
```

### With Schema Indexing (What Should Happen):

```
User: "Show me all customers"
    ↓
RAG Search: Finds "test_customers" schema → `relevantSchemas: [test_customers]`
    ↓
GPT Prompt: "Schema: test_customers (id, name, email, created_at) | Question: Show me all customers"
    (Has schema context!)
    ↓
GPT: "SELECT * FROM test_customers"  ✅ (Uses correct table name!)
```

---

## 📋 Best Practices for Schema Descriptions

### Good Schema Description:

```json
{
  "schemaName": "test_customers",
  "schemaDescription": "test_customers table with id (integer primary key), name (varchar), email (varchar), created_at (timestamp) columns. Stores customer information."
}
```

**Why good:**
- ✅ Includes table name
- ✅ Lists all columns with types
- ✅ Describes what the table stores

### Better Schema Description:

```json
{
  "schemaName": "test_customers",
  "schemaDescription": "test_customers table stores customer information. Columns: id (SERIAL PRIMARY KEY, auto-incrementing customer ID), name (VARCHAR(100), customer full name), email (VARCHAR(100), customer email address, unique), created_at (TIMESTAMP, when customer record was created). Used for customer management and queries."
}
```

**Why better:**
- ✅ More detailed column descriptions
- ✅ Includes constraints (PRIMARY KEY, UNIQUE)
- ✅ Explains purpose/usage

---

## 🔄 Complete Workflow

### 1. Create Database Record
```bash
POST /api/databases
→ Get databaseInfoId (e.g., 2)
```

### 2. Index All Your Tables
```bash
POST /api/schema-embeddings/index
{
  "databaseInfoId": 2,
  "schemaName": "test_customers",
  "schemaDescription": "..."
}

POST /api/schema-embeddings/index
{
  "databaseInfoId": 2,
  "schemaName": "orders",
  "schemaDescription": "..."
}

# ... index all tables
```

### 3. Ask Questions
```bash
POST /api/nl-to-sql/convert
{
  "databaseInfoId": 2,
  "naturalLanguageQuery": "Show me all customers"
}
→ RAG finds relevant schemas
→ GPT generates accurate SQL
```

### 4. Execute SQL
```bash
POST /api/query-execution/execute
{
  "databaseInfoId": 2,
  "sqlQuery": "SELECT * FROM test_customers"
}
→ Returns results
```

---

## 🚀 Quick Fix Command

Here's the complete command to fix your issue right now:

```bash
# Step 1: Index the schema
curl -X POST http://localhost:8080/api/schema-embeddings/index \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "schemaName": "test_customers",
    "schemaDescription": "test_customers table with id (integer primary key), name (varchar), email (varchar), created_at (timestamp) columns. Stores customer information."
  }'

# Step 2: Try NL to SQL again
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 2,
    "naturalLanguageQuery": "Show me all customers",
    "topK": 5
  }'
```

**Expected Result:**
- ✅ SQL will use `test_customers`
- ✅ `relevantSchemas` will not be empty

---

## 💡 Pro Tip: Get Schema Info Automatically

In a real application, you would:
1. Connect to user's database
2. Query `INFORMATION_SCHEMA` or `pg_catalog` tables
3. Get table names, column names, types automatically
4. Generate descriptions and index them

**Example (PostgreSQL):**
```sql
SELECT 
    table_name,
    column_name,
    data_type
FROM information_schema.columns
WHERE table_schema = 'public'
ORDER BY table_name, ordinal_position;
```

This would generate schema descriptions automatically!

---

## ✅ Summary

**The Issue:**
- RAG couldn't find schema information (`relevantSchemas: []`)
- GPT guessed table name → `customers` instead of `test_customers`

**The Fix:**
- Index your schema first using `/api/schema-embeddings/index`
- Provide detailed description with table name and columns
- RAG will find it and GPT will use correct table name!

**After indexing, your SQL will be accurate!** 🎉

