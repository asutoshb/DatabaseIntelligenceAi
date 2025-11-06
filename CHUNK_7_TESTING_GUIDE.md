# Chunk 7: Authentication & Authorization - Testing Guide

## 🎯 Testing Overview

This guide will help you test the **Authentication & Authorization** system with JWT tokens and Spring Security.

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
   - User table will be automatically updated with password and role columns

---

## 📝 Step 1: Test User Registration

**Test:** Register a new user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Expected Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImpvaG4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTY5OTk5OTk5OX0.signature",
  "user": {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

**💡 Important:** Save the `token` value - you'll need it for protected endpoints!

**Expected Response (Error - Username Exists):**
```json
{
  "error": "Username already exists"
}
```

**Expected Response (Error - Email Exists):**
```json
{
  "error": "Email already exists"
}
```

**Expected Response (Error - Validation):**
```json
{
  "error": "Username must be between 3 and 50 characters"
}
```

---

## 📝 Step 2: Test User Login

**Test:** Login with username and password

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Expected Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

**Test:** Login with email (should also work)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john@example.com",
    "password": "password123"
  }'
```

**Expected Response (Error - Invalid Credentials):**
```json
{
  "error": "Invalid username or password"
}
```

---

## 📝 Step 3: Test Protected Endpoints (Without Token)

**Test:** Try to access a protected endpoint without token

```bash
curl -X GET http://localhost:8080/api/users
```

**Expected Response:**
```json
{
  "timestamp": "2025-11-04T16:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/users"
}
```

**Status Code:** `401 Unauthorized`

This confirms that endpoints are protected!

---

## 📝 Step 4: Test Protected Endpoints (With Token)

**Test:** Access protected endpoint with valid token

**First, get a token by logging in:**
```bash
# Login and save token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }' | jq -r '.token')

echo "Token: $TOKEN"
```

**Then use the token:**
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2025-11-04T16:00:00"
  }
]
```

**Status Code:** `200 OK`

---

## 📝 Step 5: Test Invalid Token

**Test:** Try to access protected endpoint with invalid token

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJuYW1lIjoiam9obiIsInN1YiI6ImpvaG4iLCJpYXQiOjE3NjI0NDU4MDIsImV4cCI6MTc2MjUzMjIwMn0.UWQRFHXs7PmjM6bxzS4JPhb4grKBaMbY5R5J4rp4OZA"
```

**Expected Response:**
```json
{
  "timestamp": "2025-11-04T16:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/users"
}
```

**Status Code:** `401 Unauthorized`

---

## 📝 Step 6: Test Expired Token

**Note:** Tokens expire after 24 hours by default. To test expiration, you would need to:
1. Generate a token with short expiration (modify JWT config)
2. Wait for it to expire
3. Try to use it

For now, this is just for understanding - tokens are valid for 24 hours.

---

## 📝 Step 7: Test Public Endpoints (No Auth Required)

**Test:** Health check endpoint (should work without token)

```bash
curl http://localhost:8080/api/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "message": "AI Database Intelligence Platform is running"
}
```

**Status Code:** `200 OK`

**Test:** Register endpoint (should work without token)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane",
    "email": "jane@example.com",
    "password": "password123"
  }'
```

**Expected Response:** Success (200 or 201)

---

## 📝 Step 8: Test Password Validation

**Test:** Register with weak password

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "email": "test@example.com",
    "password": "123"
  }'
```

**Expected Response:**
```json
{
  "error": "Password must be at least 6 characters"
}
```

**Test:** Register with invalid email

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "email": "invalid-email",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "error": "Email must be valid"
}
```

---

## 📝 Step 9: Test Admin Role (Optional)

**Test:** Register user with ADMIN role

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "role": "ADMIN"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGci...",
  "user": {
    "id": 2,
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}
```

**Note:** Role-based endpoint protection can be added later. For now, all authenticated users can access all protected endpoints.

---

## 📝 Step 10: Test Complete Flow

**Complete Authentication Flow:**

1. **Register a new user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "alice123"
  }'
```

2. **Save the token:**
```bash
TOKEN="eyJhbGci..." # Copy from response
```

3. **Use token to access protected endpoint:**
```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN"
```

4. **Login again (get new token):**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "alice123"
  }'
```

5. **Use new token:**
```bash
NEW_TOKEN="eyJhbGci..." # Copy from response
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $NEW_TOKEN"
```

---

## 🔍 Verification Checklist

- ✅ Can register new user
- ✅ Cannot register duplicate username/email
- ✅ Can login with username
- ✅ Can login with email
- ✅ Cannot login with wrong password
- ✅ Protected endpoints require token
- ✅ Public endpoints work without token
- ✅ Invalid token is rejected
- ✅ Password validation works
- ✅ Email validation works
- ✅ Token contains user info (username, role)

---

## 🐛 Troubleshooting

### Issue: "401 Unauthorized" on all endpoints

**Solution:** Make sure you're including the token in the Authorization header:
```bash
-H "Authorization: Bearer <your-token>"
```

### Issue: "Username already exists"

**Solution:** Use a different username, or delete the existing user from database.

### Issue: Token not working

**Solution:** 
1. Make sure token is not expired (24 hours default)
2. Make sure you're using the correct format: `Bearer <token>`
3. Try logging in again to get a new token

### Issue: CORS errors

**Solution:** Make sure frontend origin is in SecurityConfig CORS configuration.

---

## 📊 Expected Database Changes

After running the application, the `users` table will have new columns:
- `password` (VARCHAR) - Hashed password
- `role` (VARCHAR) - User role (USER or ADMIN)

**Check in database:**
```sql
SELECT id, username, email, role, created_at FROM users;
```

**Note:** Passwords are hashed, so you'll see something like:
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

---

## ✅ Summary

**What We Tested:**
- ✅ User registration
- ✅ User login
- ✅ JWT token generation
- ✅ Protected endpoints
- ✅ Public endpoints
- ✅ Password validation
- ✅ Email validation
- ✅ Token validation

**Security Features Verified:**
- ✅ Passwords are hashed (not plain text)
- ✅ Tokens are required for protected endpoints
- ✅ Invalid tokens are rejected
- ✅ Public endpoints work without tokens

---

**Chunk 7 Testing Complete! Authentication is working! 🎉**

