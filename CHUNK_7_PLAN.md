# Chunk 7: Authentication & Authorization - Development Plan

## 🎯 Goal
Secure the API with JWT (JSON Web Token) authentication and implement role-based access control.

## 📋 What We'll Build

1. **JWT Token Management** - Generate and validate JWT tokens
2. **User Authentication** - Registration and login endpoints
3. **Password Security** - Bcrypt password hashing
4. **Spring Security Configuration** - Secure API endpoints
5. **JWT Filter** - Validate tokens on each request
6. **Role-Based Access Control** - Admin and User roles
7. **Protected Endpoints** - Require authentication for API access
8. **DTOs** - Request/Response objects for auth

## 🔧 Technologies We'll Use

- **Spring Security** - Authentication and authorization framework
- **JWT (JSON Web Tokens)** - Stateless authentication tokens
- **Bcrypt** - Password hashing algorithm
- **JJWT Library** - JWT creation and validation
- **Spring Security Filters** - Request interception and validation

## 📝 Step-by-Step

1. ✅ Add Spring Security and JWT dependencies to pom.xml
2. ✅ Update User model (add password, role fields)
3. ✅ Create JWT utility class (token generation/validation)
4. ✅ Create AuthenticationService (register, login, password hashing)
5. ✅ Create Spring Security configuration (JWT filter, public endpoints)
6. ✅ Create AuthController (register/login endpoints)
7. ✅ Create DTOs (RegisterRequest, LoginRequest, AuthResponse)
8. ✅ Protect API endpoints (require authentication)
9. ✅ Test authentication flow

## 🎓 What You'll Learn

- **JWT (JSON Web Tokens)** - What they are and why we use them
- **Spring Security** - How to secure Spring Boot applications
- **Password Hashing** - Why we never store plain passwords
- **Token-Based Auth** - Stateless vs stateful authentication
- **Role-Based Access Control** - Different permissions for different users
- **Security Filters** - How Spring Security intercepts requests

## 🔄 How Authentication Works

**Registration Flow:**
1. User: Sends username, email, password
2. System: Hashes password with bcrypt
3. System: Creates user in database
4. System: Generates JWT token
5. System: Returns token to user

**Login Flow:**
1. User: Sends username/email and password
2. System: Finds user in database
3. System: Verifies password (bcrypt comparison)
4. System: Generates JWT token
5. System: Returns token to user

**Protected Request Flow:**
1. User: Sends request with JWT token in header
2. System: JWT filter intercepts request
3. System: Validates token (signature, expiration)
4. System: Extracts user info from token
5. System: Allows request to proceed
6. System: Controller can access authenticated user

## 🔐 Security Features

- ✅ **Password Hashing** - Bcrypt (one-way, can't reverse)
- ✅ **JWT Tokens** - Stateless, secure, signed
- ✅ **Token Expiration** - Tokens expire after set time
- ✅ **Role-Based Access** - Admin vs User permissions
- ✅ **Protected Endpoints** - Most APIs require authentication
- ✅ **Public Endpoints** - Only register/login are public

## 📊 Example Flow

```
User Registration:
POST /api/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
    ↓
System: Hash password, create user, generate JWT
    ↓
Response: { "token": "eyJhbGc...", "user": {...} }

User Login:
POST /api/auth/login
{
  "username": "john",
  "password": "password123"
}
    ↓
System: Verify password, generate JWT
    ↓
Response: { "token": "eyJhbGc...", "user": {...} }

Protected Request:
GET /api/users
Header: Authorization: Bearer eyJhbGc...
    ↓
JWT Filter: Validate token, extract user
    ↓
Controller: Process request with authenticated user
```

## 🚀 Next Steps

**Chunk 8:** WebSocket for Real-time Updates
- Real-time progress tracking
- WebSocket configuration
- Status updates

---

**Chunk 7 Complete! API is now secured with JWT authentication! 🎉**

