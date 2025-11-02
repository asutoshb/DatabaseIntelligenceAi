# Chunk 2: Testing Guide

## 🚀 Quick Start Testing

### Step 1: Choose Your Database

**Option A: Use H2 (Easiest - No Installation Required)**

Edit `backend/src/main/resources/application.properties`:

```properties
# Comment out PostgreSQL
#spring.datasource.url=jdbc:postgresql://localhost:5432/database_intelligence
#spring.datasource.username=postgres
#spring.datasource.password=postgres

# Use H2 instead
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

**Option B: Use PostgreSQL (Production-like)**

1. Install PostgreSQL:
   ```bash
   # macOS
   brew install postgresql@14
   brew services start postgresql@14
   ```

2. Create database:
   ```bash
   createdb database_intelligence
   ```

3. Update `application.properties` (already configured)

---

### Step 2: Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

**Watch for:**
- ✅ "Started AiDatabaseIntelligenceApplication"
- ✅ "Hibernate: create table users" (if using H2)
- ❌ Any errors about database connection

---

### Step 3: Test API Endpoints

#### Test 1: Create a User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": null
}
```

#### Test 2: Get All Users

```bash
curl http://localhost:8080/api/users
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": null
  }
]
```

#### Test 3: Get User by ID

```bash
curl http://localhost:8080/api/users/1
```

#### Test 4: Create a Database

```bash
curl -X POST http://localhost:8080/api/databases \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Production Database",
    "databaseType": "PostgreSQL",
    "host": "localhost",
    "port": 5432,
    "databaseName": "mydb",
    "username": "postgres"
  }'
```

#### Test 5: Get All Databases

```bash
curl http://localhost:8080/api/databases
```

#### Test 6: Update User

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_updated",
    "email": "john.new@example.com"
  }'
```

#### Test 7: Delete User

```bash
curl -X DELETE http://localhost:8080/api/users/1
```

#### Test 8: Test Validation (Username Already Exists)

```bash
# Try to create user with same username (should fail)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "different@example.com"
  }'
```

**Expected Response:**
```json
"Error: Username already exists: john_doe"
```

---

## 🖥️ Using Postman (Visual Testing)

### Import Collection

1. Open Postman
2. Click "Import"
3. Create a new collection called "Chunk 2 API"

### Create Requests

**1. Create User (POST)**
- Method: `POST`
- URL: `http://localhost:8080/api/users`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
  ```json
  {
    "username": "testuser",
    "email": "test@example.com"
  }
  ```

**2. Get All Users (GET)**
- Method: `GET`
- URL: `http://localhost:8080/api/users`

**3. Get User by ID (GET)**
- Method: `GET`
- URL: `http://localhost:8080/api/users/1`

**4. Create Database (POST)**
- Method: `POST`
- URL: `http://localhost:8080/api/databases`
- Body:
  ```json
  {
    "name": "My DB",
    "databaseType": "PostgreSQL",
    "host": "localhost",
    "port": 5432,
    "databaseName": "mydb",
    "username": "postgres"
  }
  ```

---

## 🗄️ Using H2 Console (Database Viewer)

**Only works if using H2 database!**

1. Start backend
2. Open browser: `http://localhost:8080/api/h2-console`

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)
- Click "Connect"

**Try SQL Queries:**
```sql
-- See all users
SELECT * FROM users;

-- See all databases
SELECT * FROM database_info;

-- Count users
SELECT COUNT(*) FROM users;
```

---

## ✅ Success Checklist

- [ ] Backend starts without errors
- [ ] Can create a user (POST `/api/users`)
- [ ] Can get all users (GET `/api/users`)
- [ ] Can get user by ID (GET `/api/users/1`)
- [ ] Can update user (PUT `/api/users/1`)
- [ ] Can delete user (DELETE `/api/users/1`)
- [ ] Validation works (can't create duplicate username)
- [ ] Can create database (POST `/api/databases`)
- [ ] Can get all databases (GET `/api/databases`)
- [ ] Database tables exist (check H2 console or PostgreSQL)

---

## 🐛 Common Issues & Fixes

### Issue: "Could not connect to database"

**If using PostgreSQL:**
```bash
# Check if PostgreSQL is running
brew services list | grep postgresql

# Start if not running
brew services start postgresql@14

# Check if database exists
psql -l | grep database_intelligence

# Create if doesn't exist
createdb database_intelligence
```

**If using H2:**
- Make sure H2 configuration is uncommented in `application.properties`
- Restart the application

### Issue: "Table 'users' doesn't exist"

**Solution:**
- Make sure `spring.jpa.hibernate.ddl-auto=update` is set
- Restart application
- Check logs for "Hibernate: create table" messages

### Issue: "Port 8080 already in use"

**Solution:**
```bash
# Find process
lsof -i :8080

# Kill it
kill -9 <PID>
```

---

## 📊 Expected Database Schema

After starting the app, you should see these tables created automatically:

### `users` Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### `database_info` Table
```sql
CREATE TABLE database_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    database_type VARCHAR(255) NOT NULL,
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    database_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## 🎯 Next: Verify Everything Works

Once all tests pass, you're ready for **Chunk 3: OpenAI Integration**!

---

**Happy Testing!** 🚀

