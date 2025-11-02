# Chunk 2: Database Setup & Basic API - Development Plan

## What We're Building

In Chunk 2, we'll:
1. Connect to PostgreSQL database
2. Create database tables (using JPA entities)
3. Build REST API endpoints for CRUD operations
4. Test the API

## Step-by-Step Development

### Step 1: Update Dependencies
- Ensure PostgreSQL driver is in pom.xml
- We already have Spring Data JPA

### Step 2: Configure Database Connection
- Update application.properties
- Add PostgreSQL connection settings

### Step 3: Create Database Models (Entities)
- User entity
- DatabaseInfo entity (to store info about databases)
- SchemaInfo entity (to store schema metadata)

### Step 4: Create Repositories
- UserRepository
- DatabaseInfoRepository

### Step 5: Create REST Controllers
- UserController (CRUD operations)
- DatabaseInfoController (CRUD operations)

### Step 6: Test Everything
- Test API endpoints
- Verify database operations

---

## Technologies We'll Learn

### 1. PostgreSQL
- What: Relational database management system
- Why: Industry-standard, supports advanced features (pgvector later)

### 2. Spring Data JPA
- What: Makes database access super easy
- Why: No need to write SQL manually (mostly)

### 3. @Entity Annotation
- What: Marks a Java class as a database table
- Why: JPA automatically creates the table

### 4. @Repository
- What: Marks an interface as a data access layer
- Why: Spring automatically implements common operations

### 5. CRUD Operations
- Create (POST)
- Read (GET)
- Update (PUT)
- Delete (DELETE)

---

Let's start building! 🚀

