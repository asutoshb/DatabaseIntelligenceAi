# Chunk 4: Testing Guide

## 🚀 Quick Start

### Prerequisites
- ✅ Backend running
- ✅ OpenAI API key configured
- ✅ PostgreSQL 17 database running (upgraded from PostgreSQL 14)
- ✅ pgvector extension installed (version 0.8.1) ✅

---

## 📋 Step-by-Step Testing

### Step 1: Create/Get Database Info ID

First, you need a database entry. Create one or get existing:

```bash
# Create new database entry
curl -X POST http://localhost:8080/api/databases \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Database",
    "databaseType": "PostgreSQL",
    "host": "localhost",
    "port": 5432,
    "databaseName": "mydb",
    "username": "postgres"
  }'
```

**Save the `id` from response!** (e.g., `{"id": 1, ...}`)

Or get existing:
```bash
curl http://localhost:8080/api/databases
```

---

### Step 2: Index Your First Schema

**Example 1: Users Table**

```bash
curl -X POST http://localhost:8080/api/schema-embeddings/index \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "schemaName": "users",
    "schemaDescription": "users table with id (bigint primary key), username (varchar unique), email (varchar), created_at (timestamp) columns"
  }'
```

**Response:**
```json
{
  "id": 1,
  "databaseInfoId": 1,
  "schemaName": "users",
  "schemaDescription": "users table with...",
  "embedding": "[0.123, -0.456, ...]",
  "createdAt": "2024-11-03T10:30:00"
}
```

---

### Step 3: Index More Schemas

**Example 2: Orders Table**

```bash
curl -X POST http://localhost:8080/api/schema-embeddings/index \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "schemaName": "orders",
    "schemaDescription": "orders table with id (bigint), customer_id (bigint foreign key), total_amount (decimal), order_date (date), status (varchar) columns"
  }'
```

**Example 3: Products Table**

```bash
curl -X POST http://localhost:8080/api/schema-embeddings/index \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "schemaName": "products",
    "schemaDescription": "products table with id (bigint), name (varchar), price (decimal), category (varchar), stock_quantity (integer) columns"
  }'
```

---

### Step 4: Test Similarity Search

**Search 1: Find Customer-Related Schemas**

```bash
curl -X POST http://localhost:8080/api/schema-embeddings/search \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "query": "customer information",
    "topK": 3
  }'
```

**Expected:** Should return schemas related to customers (if you indexed any)

**Search 2: Find Order-Related Schemas**

```bash
curl -X POST http://localhost:8080/api/schema-embeddings/search \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "query": "order and purchase data",
    "topK": 5
  }'
```

**Expected:** Should return "orders" schema as top result

---

### Step 5: Get All Schemas for Database

```bash
curl http://localhost:8080/api/schema-embeddings/database/1
```

**Response:**
```json
[
  {
    "id": 1,
    "schemaName": "users",
    "schemaDescription": "...",
    ...
  },
  {
    "id": 2,
    "schemaName": "orders",
    ...
  }
]
```

---

### Step 6: Test Real-World Query

**Scenario:** User wants to query "customers by revenue"

```bash
curl -X POST http://localhost:8080/api/schema-embeddings/search \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "query": "customers by revenue",
    "topK": 3
  }'
```

**Expected Result:**
1. **customers** schema (high similarity - contains revenue!)
2. **orders** schema (medium similarity - related to customers)
3. **users** schema (lower similarity - but might have some relevance)

---

## 🐛 Troubleshooting

### Error: "OpenAI API key is not configured"
- Set environment variable: `export OPENAI_API_KEY=sk-...`
- Restart backend

### Error: "Failed to generate embedding"
- Check OpenAI API key is valid
- Check you have credits: https://platform.openai.com/account/billing

### Error: "Rate limit exceeded"
- Wait 60 seconds
- Retry logic should handle this automatically

### No Similar Results
- Make sure you've indexed multiple schemas
- Try different query terms
- Check databaseInfoId is correct

---

## 📊 Understanding Similarity Scores

**Cosine Similarity Range:**
- **0.8 - 1.0**: Very similar (almost identical)
- **0.6 - 0.8**: Similar (related concepts)
- **0.4 - 0.6**: Somewhat related
- **0.0 - 0.4**: Not very related

**Example:**
- Query: "customer"
- Result 1: "customers" schema → 0.92 (very similar!)
- Result 2: "orders" schema → 0.65 (related - orders have customers)
- Result 3: "products" schema → 0.35 (not very related)

---

## ✅ Testing Checklist

- [ ] Can create database entry
- [ ] Can index a schema
- [ ] Embedding generated successfully
- [ ] Schema stored in database
- [ ] Can search similar schemas
- [ ] Results are relevant (high similarity)
- [ ] Can get all schemas for database
- [ ] Can delete schema embedding

---

## 🎯 What to Test

### Basic Tests
1. ✅ Index single schema
2. ✅ Index multiple schemas
3. ✅ Search by similarity
4. ✅ Get all schemas

### Advanced Tests
1. Test with various query terms
2. Verify similarity scores make sense
3. Test with different topK values
4. Test edge cases (empty database, no matches)

---

## ✅ pgvector Status

**pgvector is INSTALLED and AVAILABLE! ✅**

- PostgreSQL 17 running
- pgvector 0.8.1 extension installed
- Vector data type available
- Ready for production optimization

**Current Implementation:**
- Uses TEXT storage (works perfectly!)
- Can upgrade to native vector type for better performance with large datasets

## 🚀 Next Steps

Once testing is complete:
- **Chunk 5**: Use these embeddings for RAG-based SQL generation!

**The foundation is ready for intelligent NL→SQL conversion!** 🎉

