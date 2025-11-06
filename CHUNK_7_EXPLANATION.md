# Chunk 7: Authentication & Authorization - Detailed Explanation

## 🎯 What We Built

We built a **complete authentication and authorization system** using JWT (JSON Web Tokens) and Spring Security to secure our API endpoints.

---

## 📚 Key Concepts Explained

### 1. **JWT (JSON Web Token)**

**What is JWT?**
- JWT = JSON Web Token
- A secure way to transmit information between parties
- Contains user info (username, role) encoded in the token
- Signed with a secret key (prevents tampering)

**Simple Explanation:**
Think of JWT like a **ticket**:
- You get a ticket when you log in
- You show the ticket for each request
- The ticket contains your info (username, role)
- The ticket is signed (can't be forged)
- The ticket expires after some time

**Token Structure:**
```
Header.Payload.Signature

Example:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImpvaG4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTY5OTk5OTk5OX0.signature
```

**Parts:**
1. **Header:** Algorithm and token type
2. **Payload:** User data (claims) - username, role, expiration
3. **Signature:** Secret key signature (prevents tampering)

**Why JWT?**
- ✅ **Stateless:** No server-side sessions needed
- ✅ **Scalable:** Works across multiple servers
- ✅ **Secure:** Signed, can't be tampered with
- ✅ **Portable:** Contains all user info

**Token vs Session:**
- **Session:** Server stores user info, client gets session ID
- **JWT:** All user info in token, no server storage needed

---

### 2. **Password Hashing (Bcrypt)**

**What is Password Hashing?**
- Converting plain password to a hash (one-way function)
- **NEVER** store plain passwords in database!
- Bcrypt is a secure hashing algorithm

**Simple Explanation:**
```
Plain Password: "password123"
    ↓ (bcrypt hash)
Hashed Password: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
```

**Why Hash?**
- If database is hacked, attackers can't see passwords
- One-way function (can't reverse hash to get password)
- Bcrypt includes salt (prevents rainbow table attacks)

**How It Works:**
```java
// Registration: Hash password before storing
String plainPassword = "password123";
String hashedPassword = passwordEncoder.encode(plainPassword);
// Store hashedPassword in database

// Login: Hash input password and compare
String inputPassword = "password123";
boolean matches = passwordEncoder.matches(inputPassword, hashedPassword);
// matches = true if correct, false otherwise
```

**Bcrypt Features:**
- ✅ **Slow by design:** Prevents brute force attacks
- ✅ **Includes salt:** Each hash is unique
- ✅ **Industry standard:** Used by major companies

---

### 3. **Spring Security**

**What is Spring Security?**
- Framework for authentication and authorization
- Protects your API endpoints
- Handles password encoding, token validation, etc.

**Simple Explanation:**
Spring Security is like a **bouncer** at a club:
- Checks if you have a valid ticket (JWT token)
- Decides if you can enter (authorization)
- Protects all endpoints automatically

**Key Components:**

1. **SecurityFilterChain:**
   - Defines which endpoints are public/protected
   - Configures authentication rules

2. **JWT Filter:**
   - Intercepts every request
   - Validates JWT token
   - Sets authentication in SecurityContext

3. **Password Encoder:**
   - Hashes passwords with bcrypt
   - Verifies passwords on login

**Configuration:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Define security rules
    // Configure CORS
    // Add JWT filter
}
```

---

### 4. **Role-Based Access Control (RBAC)**

**What is RBAC?**
- Different users have different permissions
- Roles: ADMIN, USER
- Different endpoints for different roles

**Simple Explanation:**
- **USER:** Can use basic features
- **ADMIN:** Can access admin endpoints

**How We Implement:**
```java
// User model has role field
private String role = "USER"; // or "ADMIN"

// JWT token contains role
claims.put("role", user.getRole());

// SecurityConfig can check roles
.antMatchers("/admin/**").hasRole("ADMIN")
```

**Future Enhancement:**
- Can add more roles (MODERATOR, VIEWER, etc.)
- Can have role hierarchies
- Can have fine-grained permissions

---

## 🏗️ Architecture

### Registration Flow:

```
1. User sends: POST /api/auth/register
   {
     "username": "john",
     "email": "john@example.com",
     "password": "password123"
   }
        ↓
2. AuthenticationService.register()
   - Check if username/email exists
   - Hash password with bcrypt
   - Create user in database
        ↓
3. JwtUtil.generateToken()
   - Create JWT token with username, role
   - Sign with secret key
        ↓
4. Return: { "token": "eyJhbGc...", "user": {...} }
```

### Login Flow:

```
1. User sends: POST /api/auth/login
   {
     "username": "john",
     "password": "password123"
   }
        ↓
2. AuthenticationService.login()
   - Find user by username/email
   - Verify password (bcrypt comparison)
        ↓
3. JwtUtil.generateToken()
   - Create JWT token
        ↓
4. Return: { "token": "eyJhbGc...", "user": {...} }
```

### Protected Request Flow:

```
1. User sends: GET /api/users
   Header: Authorization: Bearer eyJhbGc...
        ↓
2. JwtAuthenticationFilter intercepts
   - Extract token from header
   - Validate token (signature, expiration)
   - Extract username and role
        ↓
3. Set authentication in SecurityContext
        ↓
4. SecurityConfig checks authorization
   - Endpoint requires authentication? ✅
   - User has valid token? ✅
        ↓
5. Request proceeds to controller
   - Controller can access authenticated user
        ↓
6. Return response
```

---

## 📝 Key Components

### 1. User Model (Updated)
- Added `password` field (hashed)
- Added `role` field (USER or ADMIN)

### 2. JwtUtil
- `generateToken()` - Create JWT token
- `validateToken()` - Validate token
- `extractUsername()` - Get username from token
- `extractRole()` - Get role from token

### 3. AuthenticationService
- `register()` - Create new user, hash password, generate token
- `login()` - Verify credentials, generate token

### 4. SecurityConfig
- Configures Spring Security
- Defines public/protected endpoints
- Adds JWT filter
- Configures CORS

### 5. JwtAuthenticationFilter
- Intercepts every request
- Validates JWT token
- Sets authentication in SecurityContext

### 6. AuthController
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

---

## 🔐 Security Features

### ✅ Password Security:
- Passwords are hashed with bcrypt
- Never stored in plain text
- One-way function (can't reverse)

### ✅ Token Security:
- Tokens are signed with secret key
- Can't be tampered with
- Tokens expire after 24 hours
- Secret key should be in environment variable

### ✅ Endpoint Protection:
- Most endpoints require authentication
- Only register/login are public
- JWT filter validates every request

### ✅ CORS Protection:
- Only allows requests from frontend origins
- Prevents unauthorized cross-origin requests

---

## 🎓 What You Learned

1. **JWT Tokens:** Stateless authentication tokens
2. **Password Hashing:** Bcrypt for secure password storage
3. **Spring Security:** Framework for securing Spring Boot apps
4. **JWT Filter:** Request interception and validation
5. **Role-Based Access:** Different permissions for different users
6. **Security Best Practices:** Never store plain passwords, use HTTPS in production

---

## 🚀 How This Connects to Other Chunks

**Chunk 2:** User model now has password and role
**Chunk 3-6:** All endpoints now require authentication
**Chunk 8:** WebSocket can use JWT for authentication
**Chunk 9-14:** Frontend will use JWT tokens for API calls

---

## 📊 API Endpoints

### Public Endpoints (No Auth Required):
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/health` - Health check

### Protected Endpoints (Auth Required):
- All other endpoints require valid JWT token in `Authorization: Bearer <token>` header

---

## ✅ Summary

**What We Built:**
- ✅ JWT token generation and validation
- ✅ User registration and login
- ✅ Password hashing with bcrypt
- ✅ Spring Security configuration
- ✅ Protected API endpoints
- ✅ Role-based access control

**Security Features:**
- ✅ Passwords are hashed (never plain text)
- ✅ Tokens are signed and expire
- ✅ Endpoints are protected
- ✅ CORS is configured

**You've secured your API with industry-standard authentication!** 🎉

