# Chunk 4 Explanation: pgvector & Schema Embeddings

## 🎯 What We Built

We created a system to store and search database schema embeddings:
1. **SchemaEmbedding Entity** - Stores schemas with their embeddings
2. **SchemaEmbeddingRepository** - Database operations
3. **SchemaEmbeddingService** - Generate/store/search embeddings
4. **SchemaEmbeddingController** - REST API endpoints

---

## 📚 Technologies Explained

### 1. pgvector - What is it?

**pgvector** is a PostgreSQL extension that adds vector data type and similarity search.

**Simple Explanation:**
- PostgreSQL normally stores: numbers, text, dates
- pgvector adds: **vectors** (lists of numbers)
- Enables: **Similarity search** (find similar vectors)

**Why pgvector?**
- Store embeddings directly in PostgreSQL
- Fast similarity search (cosine, L2 distance)
- No need for separate vector database
- Integrates with existing PostgreSQL setup

**Example:**
```sql
-- Without pgvector: Store as TEXT
embedding TEXT = "[0.1, 0.2, 0.3, ...]"

-- With pgvector: Store as VECTOR
embedding vector(1536) = '[0.1, 0.2, 0.3, ...]'
```

**Our Implementation:**
- Currently: Store as TEXT (works perfectly!)
- **pgvector Available:** ✅ Installed on PostgreSQL 17!
- Can upgrade to native vector type for production optimization

---

### 2. Embedding Storage - How It Works

**What We Store:**
```java
SchemaEmbedding {
    schemaName: "users"
    schemaDescription: "users table with id, username, email"
    embedding: "[0.123, -0.456, 0.789, ...]"  // 1536 numbers
}
```

**Storage Format:**
- Embedding stored as JSON array string: `"[0.1,0.2,0.3,...]"`
- Converted to/from `List<Double>` in Java
- 1536 dimensions (OpenAI ada-002 embedding size)

---

### 3. Cosine Similarity - Finding Similar Schemas

**What is Cosine Similarity?**
- Measures how similar two vectors are
- Returns value between -1 and 1
- 1 = identical, 0 = unrelated, -1 = opposite

**Formula:**
```
cos(θ) = (A · B) / (||A|| * ||B||)
```

**Example:**
```
Query: "customer information"
Schema 1: "customers table" → Similarity: 0.85 (very similar!)
Schema 2: "orders table" → Similarity: 0.45 (somewhat related)
Schema 3: "logs table" → Similarity: 0.12 (not related)
```

**How We Use It:**
1. User asks: "Show customers by revenue"
2. Generate embedding for query
3. Compare with all stored schema embeddings
4. Return top 5 most similar schemas
5. Use those schemas to generate accurate SQL (Chunk 5)

---

### 4. Vector Search Flow

```
User Query: "Show customers by revenue"
        ↓
Generate Query Embedding: [0.1, 0.2, ...]
        ↓
Get All Schema Embeddings from Database
        ↓
Calculate Cosine Similarity for Each
        ↓
Sort by Similarity (highest first)
        ↓
Return Top 5 Similar Schemas
        ↓
Use for RAG Context (Chunk 5)
```

---

## 🏗️ Architecture

### Indexing Flow (Storing Schema):

```
1. User provides: schemaName, schemaDescription
        ↓
2. SchemaEmbeddingService.indexSchema()
        ↓
3. EmbeddingService.generateEmbedding(description)
        ↓
4. OpenAI API returns: [0.1, 0.2, ...]
        ↓
5. Store in SchemaEmbedding entity
        ↓
6. Save to database
```

### Search Flow (Finding Similar):

```
1. User provides: query text
        ↓
2. SchemaEmbeddingService.findSimilarSchemas()
        ↓
3. Generate embedding for query
        ↓
4. Get all schemas from database
        ↓
5. Calculate cosine similarity
        ↓
6. Sort by similarity
        ↓
7. Return top K results
```

---

## 📝 Key Components

### 1. SchemaEmbedding Entity
- Stores schema info + embedding
- Converts between `List<Double>` and `String`

### 2. SchemaEmbeddingService
- `indexSchema()` - Generate and store embedding
- `findSimilarSchemas()` - Vector similarity search
- `cosineSimilarity()` - Calculate similarity score

### 3. SchemaEmbeddingController
- `POST /index` - Index a schema
- `POST /search` - Search similar schemas
- `GET /database/{id}` - Get all schemas for database

---

## 🔧 Current Implementation

**What We Do:**
- Store embeddings as TEXT (JSON array)
- Calculate similarity in Java code
- Works perfectly for development/testing

**pgvector Now Available! ✅**
- PostgreSQL 17 installed with pgvector extension
- Can upgrade to native vector type for better performance
- Database-level similarity search available

**Current vs pgvector:**

| Feature | Current (TEXT) | With pgvector |
|---------|---------------|---------------|
| **Storage** | TEXT (JSON array) | `vector(1536)` native type |
| **Similarity** | Java calculation | Database operator (`<=>`) |
| **Performance** | Good for < 100K | Excellent for millions |
| **Indexes** | Not available | IVFFlat, HNSW indexes |

**Upgrade Path (Optional):**
To use pgvector native type, update `SchemaEmbedding.java`:
```java
@Column(columnDefinition = "vector(1536)")
private String embedding;  // Change from TEXT to vector
```

Then use SQL for similarity:
```sql
SELECT * FROM schema_embeddings
ORDER BY embedding <=> :queryEmbedding::vector
LIMIT 5;
```

---

## 🎓 Key Concepts Summary

| Concept | What It Is | Why We Need It |
|---------|------------|----------------|
| **pgvector** | PostgreSQL extension for vectors | Store embeddings efficiently |
| **Embedding Storage** | Save vectors in database | Persist schema information |
| **Cosine Similarity** | Measure vector similarity | Find relevant schemas |
| **Vector Search** | Search by similarity | Retrieve context for RAG |

---

## 🚀 How This Connects to RAG (Chunk 5)

**RAG = Retrieval-Augmented Generation**

**Without Chunk 4:**
- GPT tries to generate SQL without context
- Often incorrect or generic SQL

**With Chunk 4 (embeddings stored):**
1. User asks: "Show top customers"
2. Search embeddings → Find "customers" and "orders" schemas
3. Retrieve schema details (column names, types)
4. Send to GPT with context
5. GPT generates accurate SQL: `SELECT * FROM customers ORDER BY revenue DESC LIMIT 10`

**That's RAG!** 🎉

---

## 📊 API Endpoints

### Index Schema
```bash
POST /api/schema-embeddings/index
{
  "databaseInfoId": 1,
  "schemaName": "users",
  "schemaDescription": "users table with id, username, email"
}
```

### Search Similar
```bash
POST /api/schema-embeddings/search
{
  "databaseInfoId": 1,
  "query": "customer information",
  "topK": 5
}
```

---

## ✅ pgvector Status

**pgvector is NOW INSTALLED and AVAILABLE! ✅**

- ✅ PostgreSQL 17.6 running
- ✅ pgvector extension 0.8.1 installed
- ✅ Vector data type functional
- ✅ Ready for native vector operations

**Current Implementation:**
- Uses TEXT storage (simple, works great!)
- Can upgrade to pgvector native type for production optimization

## 🔄 Next Steps

**Chunk 5:** Use these embeddings for RAG-based NL→SQL conversion!

**You've built the foundation for intelligent SQL generation!** 🚀

