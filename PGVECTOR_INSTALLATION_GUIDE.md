# pgvector Installation Guide

## Current Status

**pgvector is NOT installed** for PostgreSQL 14.

The Homebrew package installed pgvector for PostgreSQL 17/18, but we're using PostgreSQL 14.

## Our Current Implementation

**Good News:** Our implementation works **perfectly without pgvector!**

- ✅ Embeddings stored as TEXT (JSON array)
- ✅ Similarity calculated in Java (cosine similarity)
- ✅ Works great for development and small/medium datasets
- ✅ No installation needed!

**When you need pgvector:**
- Very large datasets (millions of embeddings)
- Production with high performance requirements
- Want database-level vector operations

---

## Option 1: Keep Current Implementation (Recommended for Now)

**Pros:**
- ✅ Works immediately - no installation needed
- ✅ Perfect for development and testing
- ✅ Simple and straightforward
- ✅ Easy to understand

**Cons:**
- ⚠️ Not optimal for very large datasets (millions)
- ⚠️ Loads all embeddings into memory

**Our current code works great!** You can use it as-is for Chunk 4 and 5.

---

## Option 2: Install pgvector for PostgreSQL 14

### Method A: Build from Source (Recommended)

1. **Install prerequisites:**
   ```bash
   brew install git cmake
   ```

2. **Clone pgvector:**
   ```bash
   git clone --branch v0.8.1 https://github.com/pgvector/pgvector.git
   cd pgvector
   ```

3. **Build for PostgreSQL 14:**
   ```bash
   export PG_CONFIG=/opt/homebrew/opt/postgresql@14/bin/pg_config
   make
   sudo make install
   ```

4. **Create extension:**
   ```bash
   /opt/homebrew/opt/postgresql@14/bin/psql database_intelligence -c "CREATE EXTENSION vector;"
   ```

### Method B: Upgrade PostgreSQL to 17/18

If you want to use the pre-built pgvector:

1. **Install PostgreSQL 17:**
   ```bash
   brew install postgresql@17
   ```

2. **Start PostgreSQL 17:**
   ```bash
   brew services start postgresql@17
   ```

3. **Create database:**
   ```bash
   /opt/homebrew/opt/postgresql@17/bin/createdb database_intelligence
   ```

4. **Install extension:**
   ```bash
   /opt/homebrew/opt/postgresql@17/bin/psql database_intelligence -c "CREATE EXTENSION vector;"
   ```

5. **Update application.properties:**
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/database_intelligence
   # (port might be different for PG17)
   ```

---

## Option 3: Use Docker (Easiest for pgvector)

If you want pgvector without building:

```bash
# Run PostgreSQL with pgvector
docker run -d \
  --name postgres-pgvector \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=database_intelligence \
  -p 5433:5432 \
  pgvector/pgvector:pg14

# Connect
psql -h localhost -p 5433 -U postgres -d database_intelligence

# Create extension
CREATE EXTENSION vector;
```

Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/database_intelligence
```

---

## Recommendation

**For now:** Keep the current implementation (Option 1)

**Why:**
- ✅ Works perfectly for your use case
- ✅ No installation complexity
- ✅ Can migrate to pgvector later if needed
- ✅ Good enough for development and learning

**When to upgrade to pgvector:**
- When you have millions of embeddings
- When similarity search becomes slow
- When deploying to production with high load

---

## Current Implementation Details

**How it works:**
1. Embeddings stored as TEXT: `"[0.1, 0.2, 0.3, ...]"`
2. Java code parses JSON array
3. Cosine similarity calculated in Java
4. Results sorted by similarity

**Performance:**
- Small datasets (< 10,000): Excellent
- Medium datasets (10K - 100K): Good
- Large datasets (> 100K): Consider pgvector

---

## Verify Current Setup Works

Test it:
```bash
# Index a schema
curl -X POST http://localhost:8080/api/schema-embeddings/index \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "schemaName": "users",
    "schemaDescription": "users table"
  }'

# Search
curl -X POST http://localhost:8080/api/schema-embeddings/search \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "query": "user information",
    "topK": 5
  }'
```

**If this works, you're good to go!** 🎉

---

## Summary

**Answer:** pgvector is NOT installed, but **you don't need it yet!**

Our current implementation:
- ✅ Works without pgvector
- ✅ Perfect for development
- ✅ Can upgrade later if needed

**Recommendation:** Use current implementation for now, upgrade to pgvector only if you need better performance with large datasets.

