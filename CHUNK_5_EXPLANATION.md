# Chunk 5: NL to SQL with RAG - Detailed Explanation

## 🎯 What We Built

We built the **core feature** of the AI Database Intelligence Platform: converting natural language queries to SQL using **RAG (Retrieval-Augmented Generation)**.

---

## 📚 Key Concepts Explained

### 1. **RAG (Retrieval-Augmented Generation)**

**What is RAG?**
- RAG = Retrieval-Augmented Generation
- Technique to enhance LLM responses with relevant context
- Instead of asking GPT blindly, we first **retrieve** relevant information
- Then we **augment** the prompt with that context
- Finally, GPT **generates** a response using the context

**Why RAG?**
- **Without RAG:** GPT guesses table/column names → Often wrong SQL
- **With RAG:** GPT uses actual schema information → Accurate SQL!

**Example:**
```
User: "Show me top 5 customers"

Without RAG:
GPT: "SELECT * FROM users LIMIT 5"  ❌ (Wrong table name!)

With RAG:
1. Retrieve: "customers table with id, name, revenue"
2. GPT sees the schema → "SELECT * FROM customers ORDER BY revenue DESC LIMIT 5"  ✅
```

---

### 2. **Prompt Engineering**

**What is Prompt Engineering?**
- Art of writing effective prompts for LLMs
- Well-written prompts = Better results

**Our System Prompt:**
```java
"You are an expert SQL query generator for PostgreSQL database. 
Convert natural language questions into valid SQL SELECT queries only. 
Do not include DROP, DELETE, UPDATE, INSERT, or any data modification commands. 
Return ONLY the SQL query, no explanations, no markdown formatting, no code blocks."
```

**Key Elements:**
- **Role:** "You are an expert SQL query generator"
- **Task:** "Convert natural language to SQL"
- **Constraints:** "Only SELECT queries, no data modification"
- **Format:** "Return ONLY the SQL query"

---

### 3. **SQL Validation**

**Why Validate?**
- Security: Prevent dangerous SQL operations
- Safety: Ensure only SELECT queries (read-only)
- Correctness: Check basic syntax

**What We Check:**
- ✅ Dangerous keywords: DROP, DELETE, UPDATE, INSERT, etc.
- ✅ SQL injection patterns
- ✅ Balanced parentheses
- ✅ Only SELECT queries allowed

**Example:**
```java
// ✅ Valid
"SELECT * FROM customers LIMIT 5"

// ❌ Invalid (dangerous)
"DROP TABLE customers"

// ❌ Invalid (not SELECT)
"UPDATE customers SET name = 'John'"
```

---

## 🏗️ Architecture Overview

### Components

```
┌─────────────────┐
│  User Query     │
│ "Show top 5..." │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ NLToSQLController│  ← REST API endpoint
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ NLToSQLService  │  ← Main orchestration
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌─────────┐ ┌────────────┐
│ RAGService│ │LLMService│
│ Retrieve │ │ Generate  │
│ Schemas  │ │    SQL    │
└────┬─────┘ └─────┬──────┘
     │             │
     │             │
     ▼             ▼
┌──────────────────────┐
│ SchemaEmbeddingService│ ← From Chunk 4
│ (Vector Search)      │
└──────────────────────┘
         │
         ▼
┌──────────────────────┐
│ SQLValidator         │ ← Security checks
└──────────────────────┘
```

---

## 📂 Files Created

### 1. **DTOs (Data Transfer Objects)**

#### `NLToSQLRequest.java`
```java
{
  "databaseInfoId": 1,
  "naturalLanguageQuery": "Show me top 5 customers",
  "topK": 5  // Number of relevant schemas to retrieve
}
```

#### `NLToSQLResponse.java`
```java
{
  "sqlQuery": "SELECT * FROM customers LIMIT 5",
  "naturalLanguageQuery": "Show me top 5 customers",
  "relevantSchemas": [...],
  "explanation": "...",
  "isValid": true,
  "validationErrors": []
}
```

---

### 2. **Services**

#### `RAGService.java`
**Purpose:** Retrieve relevant schema context using vector similarity

**Key Method:**
```java
public List<SchemaContext> retrieveRelevantSchemas(
    Long databaseInfoId, 
    String query, 
    int topK
)
```

**How it works:**
1. Generate embedding for the query
2. Search similar schema embeddings (vector similarity)
3. Return top K most relevant schemas

**Example:**
```java
Query: "Show me top customers"
→ Finds: ["customers table with id, name, revenue"]
```

---

#### `NLToSQLService.java`
**Purpose:** Main service that orchestrates NL to SQL conversion

**Process:**
1. Verify database exists
2. Retrieve relevant schemas using RAG
3. Build enhanced prompt with schema context
4. Generate SQL using GPT
5. Clean and validate SQL
6. Generate explanation
7. Return response

**Key Method:**
```java
public NLToSQLResponse convertToSQL(
    Long databaseInfoId,
    String naturalLanguageQuery,
    int topK
)
```

---

#### `SQLValidator.java`
**Purpose:** Validate SQL for security and correctness

**Security Checks:**
- Blocks dangerous keywords (DROP, DELETE, UPDATE, etc.)
- Detects SQL injection patterns
- Ensures only SELECT queries

**Example:**
```java
validate("SELECT * FROM users")  → ✅ Valid
validate("DROP TABLE users")     → ❌ Invalid (dangerous keyword)
validate("UPDATE users SET...") → ❌ Invalid (not SELECT)
```

---

### 3. **Controller**

#### `NLToSQLController.java`
**REST Endpoints:**
- `POST /api/nl-to-sql/convert` - Convert NL to SQL
- `GET /api/nl-to-sql/status` - Health check

**Example Request:**
```bash
POST /api/nl-to-sql/convert
{
  "databaseInfoId": 1,
  "naturalLanguageQuery": "Show me top 5 customers"
}
```

**Example Response:**
```json
{
  "sqlQuery": "SELECT * FROM customers ORDER BY revenue DESC LIMIT 5",
  "naturalLanguageQuery": "Show me top 5 customers",
  "relevantSchemas": [
    {
      "schemaName": "customers",
      "schemaDescription": "customers table with id, name, revenue columns"
    }
  ],
  "explanation": "Generated SQL for: 'Show me top 5 customers'...",
  "isValid": true,
  "validationErrors": []
}
```

---

## 🔄 Complete Flow

### Step-by-Step Process

1. **User sends request:**
   ```
   POST /api/nl-to-sql/convert
   {
     "databaseInfoId": 1,
     "naturalLanguageQuery": "Show me top 5 customers"
   }
   ```

2. **Controller receives request:**
   - Validates input
   - Calls `NLToSQLService.convertToSQL()`

3. **Service verifies database:**
   - Checks if database exists
   - Loads database info

4. **RAG Retrieval:**
   - `RAGService.retrieveRelevantSchemas()` called
   - Generates embedding for query
   - Searches similar schema embeddings
   - Returns top 5 relevant schemas

5. **Prompt Building:**
   - System prompt: "You are an expert SQL generator..."
   - User prompt: Schema context + user query
   - Example:
     ```
     Database Schema Information:
     1. Schema: customers
        Description: customers table with id, name, revenue columns
     
     Question: Show me top 5 customers
     ```

6. **SQL Generation:**
   - `LLMService.generateText()` called
   - GPT generates SQL using context
   - Example: `SELECT * FROM customers ORDER BY revenue DESC LIMIT 5`

7. **SQL Cleaning:**
   - Removes markdown code blocks
   - Removes quotes
   - Trims whitespace

8. **SQL Validation:**
   - `SQLValidator.validate()` called
   - Checks for dangerous keywords
   - Verifies it's a SELECT query
   - Returns validation result

9. **Response Building:**
   - Combines SQL, schemas, explanation
   - Returns `NLToSQLResponse`

10. **Response sent to user:**
    - JSON response with SQL and metadata

---

## 🎓 Why This Approach Works

### Comparison: With vs Without RAG

**Scenario:** User asks "Show me top customers"

#### Without RAG:
```
Prompt: "Convert to SQL: Show me top customers"
GPT: "SELECT * FROM users LIMIT 10"  ❌ (Wrong table!)
```

#### With RAG:
```
Prompt: 
  "Schema: customers table with id, name, revenue
   Question: Show me top customers"
GPT: "SELECT * FROM customers ORDER BY revenue DESC LIMIT 10"  ✅
```

**Result:** RAG provides context → Accurate SQL!

---

## 🔐 Security Features

### SQL Validation

**What we block:**
- ❌ DROP, DELETE, UPDATE, INSERT commands
- ❌ SQL injection patterns
- ❌ Data modification operations

**What we allow:**
- ✅ SELECT queries only
- ✅ Read-only operations

**Example:**
```java
// ✅ Allowed
"SELECT * FROM customers"

// ❌ Blocked
"DROP TABLE customers"
"DELETE FROM customers"
"UPDATE customers SET ..."
```

---

## 📊 API Endpoints

### Convert NL to SQL
```bash
POST /api/nl-to-sql/convert
Content-Type: application/json

{
  "databaseInfoId": 1,
  "naturalLanguageQuery": "Show me top 5 customers",
  "topK": 5
}
```

**Response:**
```json
{
  "sqlQuery": "SELECT * FROM customers ORDER BY revenue DESC LIMIT 5",
  "naturalLanguageQuery": "Show me top 5 customers",
  "relevantSchemas": [...],
  "explanation": "...",
  "isValid": true,
  "validationErrors": []
}
```

### Status Check
```bash
GET /api/nl-to-sql/status
```

---

## 🎯 Key Takeaways

1. **RAG enhances LLM accuracy** by providing relevant context
2. **Prompt engineering** is crucial for good results
3. **SQL validation** ensures security and safety
4. **Vector similarity search** finds relevant schemas efficiently
5. **Complete flow** from NL → SQL with validation

---

## 🚀 Next Steps (Chunk 6)

**Chunk 6:** Execute the generated SQL safely
- Query execution service
- Read-only database connections
- Result processing
- Error handling

**You've built the core intelligence! Now let's execute it safely! 🎉**

