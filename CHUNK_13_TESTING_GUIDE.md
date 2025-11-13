# Chunk 13: Backend Deployment - Testing Guide

## 🧪 Testing Checklist

This guide helps you verify that your backend is ready for deployment and test the deployment process.

## Prerequisites

Before testing deployment:

1. ✅ Backend code is complete and working locally
2. ✅ All environment variables are externalized
3. ✅ Database is accessible (or plan to use platform-provided DB)
4. ✅ GitHub repository is set up
5. ✅ You have accounts on deployment platforms (Render/Railway)

---

## Local Testing

### 1. Test Production Profile Locally

**Purpose**: Verify production configuration works before deploying

```bash
cd backend

# Set environment variables
export SPRING_PROFILES_ACTIVE=production
export OPENAI_API_KEY=sk-your-key-here
export JWT_SECRET=your-secret-key-at-least-32-characters-long
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/database_intelligence
export SPRING_DATASOURCE_USERNAME=your-username
export SPRING_DATASOURCE_PASSWORD=your-password

# Run with production profile
./mvnw spring-boot:run
```

**Expected:**
- ✅ Application starts successfully
- ✅ Connects to database
- ✅ Health check returns 200 OK
- ✅ No errors in logs

**Verify:**
```bash
curl http://localhost:8080/api/health
```

Should return:
```json
{
  "status": "UP",
  "message": "AI Database Intelligence Platform",
  "timestamp": "2024-01-01T12:00:00",
  "checks": {
    "application": {"status": "UP"},
    "database": {"status": "UP"}
  }
}
```

---

### 2. Test Docker Build Locally

**Purpose**: Verify Dockerfile works before deploying

```bash
cd backend

# Build Docker image
docker build -t ai-database-intelligence-backend .

# Run Docker container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e OPENAI_API_KEY=sk-your-key \
  -e JWT_SECRET=your-secret-key-at-least-32-characters-long \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/database_intelligence \
  -e SPRING_DATASOURCE_USERNAME=your-username \
  -e SPRING_DATASOURCE_PASSWORD=your-password \
  ai-database-intelligence-backend
```

**Expected:**
- ✅ Docker image builds successfully
- ✅ Container starts without errors
- ✅ Health check works
- ✅ Application connects to database

**Verify:**
```bash
# Check container is running
docker ps

# Check logs
docker logs <container-id>

# Test health endpoint
curl http://localhost:8080/api/health
```

**Troubleshooting:**
- If database connection fails, use `host.docker.internal` for localhost access
- Check environment variables are set correctly
- Verify database is accessible from Docker network

---

### 3. Test Health Check Endpoint

**Purpose**: Verify enhanced health check works correctly

```bash
# Test when database is connected
curl http://localhost:8080/api/health

# Expected: 200 OK with database UP
{
  "status": "UP",
  "checks": {
    "application": {"status": "UP"},
    "database": {"status": "UP"}
  }
}
```

**Test Database Failure:**
1. Stop PostgreSQL: `brew services stop postgresql` (macOS) or `sudo systemctl stop postgresql` (Linux)
2. Restart backend
3. Test health endpoint:

```bash
curl http://localhost:8080/api/health
```

**Expected:**
- ✅ HTTP status: 503 Service Unavailable
- ✅ Response shows database DOWN:
```json
{
  "status": "DOWN",
  "checks": {
    "application": {"status": "UP"},
    "database": {"status": "DOWN"}
  }
}
```

---

## Platform-Specific Testing

### Render.com Testing

#### Step 1: Verify Configuration

Check `render.yaml` is valid:
```bash
cd backend
cat render.yaml
```

**Verify:**
- ✅ Service type is `web`
- ✅ Build command is correct
- ✅ Start command is correct
- ✅ Environment variables are listed

#### Step 2: Deploy to Render

1. Go to [render.com](https://render.com)
2. Create new Web Service
3. Connect GitHub repository
4. Render should auto-detect `render.yaml`
5. Add environment variables
6. Deploy

#### Step 3: Verify Deployment

**Check Build Logs:**
- ✅ Maven build succeeds
- ✅ JAR file is created
- ✅ No compilation errors

**Check Runtime Logs:**
- ✅ Application starts
- ✅ "AI Database Intelligence Platform is running!" message
- ✅ Database connection successful
- ✅ No errors

**Test Health Endpoint:**
```bash
curl https://your-service-name.onrender.com/api/health
```

**Expected:**
- ✅ 200 OK response
- ✅ Database status is UP
- ✅ Response time < 2 seconds

**Test API Endpoints:**
```bash
# Test registration
curl -X POST https://your-service-name.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"test123"}'

# Test health
curl https://your-service-name.onrender.com/api/health
```

---

### Railway.app Testing

#### Step 1: Verify Configuration

Check `railway.json` is valid:
```bash
cd backend
cat railway.json
```

**Verify:**
- ✅ Build command is correct
- ✅ Start command is correct
- ✅ JSON is valid

#### Step 2: Deploy to Railway

1. Go to [railway.app](https://railway.app)
2. Create new project
3. Add GitHub repository
4. Railway auto-detects Java project
5. Add PostgreSQL database (optional)
6. Add environment variables
7. Deploy

#### Step 3: Verify Deployment

**Check Build Logs:**
- ✅ Maven build succeeds
- ✅ JAR file is created
- ✅ No errors

**Check Runtime Logs:**
- ✅ Application starts
- ✅ Database connection successful
- ✅ No errors

**Test Health Endpoint:**
```bash
curl https://your-service-name.up.railway.app/api/health
```

**Expected:**
- ✅ 200 OK response
- ✅ Database status is UP

---

## Production Configuration Testing

### 1. Test CORS Configuration

**Purpose**: Verify CORS works for frontend domain

**Test:**
```bash
# Simulate frontend request
curl -X OPTIONS https://your-backend-url.com/api/health \
  -H "Origin: https://your-frontend.vercel.app" \
  -H "Access-Control-Request-Method: GET" \
  -v
```

**Expected:**
- ✅ `Access-Control-Allow-Origin` header present
- ✅ `Access-Control-Allow-Methods` includes GET, POST, etc.
- ✅ `Access-Control-Allow-Headers` includes Content-Type, Authorization

### 2. Test Database Connection Pooling

**Purpose**: Verify connection pool settings work

**Check Logs:**
- Look for HikariCP connection pool messages
- Verify pool size limits are respected
- Check for connection timeout errors

**Test:**
- Make multiple concurrent requests
- Verify no connection pool exhaustion errors

### 3. Test Compression

**Purpose**: Verify response compression works

**Test:**
```bash
curl -H "Accept-Encoding: gzip" \
  https://your-backend-url.com/api/health \
  -v
```

**Expected:**
- ✅ `Content-Encoding: gzip` header present
- ✅ Response is compressed (smaller size)

### 4. Test Logging

**Purpose**: Verify production logging is appropriate

**Check Logs:**
- ✅ No DEBUG messages (should be INFO only)
- ✅ SQL queries not logged (show-sql=false)
- ✅ Log format is clean and readable

---

## Security Testing

### 1. Verify Secrets Not in Code

**Test:**
```bash
# Search for hardcoded secrets
grep -r "sk-" backend/src/
grep -r "JWT_SECRET" backend/src/ | grep -v "System.getenv"
```

**Expected:**
- ✅ No API keys in code
- ✅ No JWT secrets in code
- ✅ All secrets use environment variables

### 2. Test HTTPS

**Purpose**: Verify HTTPS is enabled (platforms usually provide this)

**Test:**
```bash
curl https://your-backend-url.com/api/health
```

**Expected:**
- ✅ HTTPS works (not HTTP)
- ✅ SSL certificate is valid
- ✅ No certificate errors

### 3. Test Security Headers

**Purpose**: Verify security headers are set

**Test:**
```bash
curl -I https://your-backend-url.com/api/health
```

**Expected Headers:**
- ✅ `X-Content-Type-Options: nosniff` (if configured)
- ✅ Secure cookie settings (if using sessions)

---

## Performance Testing

### 1. Test Response Times

**Purpose**: Verify acceptable response times

**Test:**
```bash
# Time health check
time curl https://your-backend-url.com/api/health

# Time API endpoint
time curl -X POST https://your-backend-url.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"test123"}'
```

**Expected:**
- ✅ Health check: < 500ms
- ✅ API endpoints: < 2 seconds (depending on operation)

### 2. Test Concurrent Requests

**Purpose**: Verify application handles load

**Test:**
```bash
# Make 10 concurrent requests
for i in {1..10}; do
  curl https://your-backend-url.com/api/health &
done
wait
```

**Expected:**
- ✅ All requests succeed
- ✅ No connection errors
- ✅ Response times remain acceptable

---

## Integration Testing

### 1. Test Frontend Connection

**Purpose**: Verify frontend can connect to deployed backend

**Steps:**
1. Update frontend `.env`:
   ```env
   VITE_API_URL=https://your-backend-url.com/api
   VITE_WS_URL=https://your-backend-url.com/api/ws
   ```
2. Build and run frontend
3. Test all features:
   - ✅ Authentication (register/login)
   - ✅ NL to SQL conversion
   - ✅ Query execution
   - ✅ WebSocket connection

**Expected:**
- ✅ All features work
- ✅ No CORS errors
- ✅ WebSocket connects successfully

### 2. Test Database Operations

**Purpose**: Verify database operations work in production

**Test:**
1. Register a new user
2. Create a database entry
3. Query the database
4. Verify data persists

**Expected:**
- ✅ All database operations succeed
- ✅ Data persists correctly
- ✅ No connection errors

---

## Monitoring and Alerts

### 1. Set Up Health Check Monitoring

**Purpose**: Get notified if service goes down

**Options:**
- **Uptime Robot**: Free uptime monitoring
- **Pingdom**: Advanced monitoring
- **Platform-native**: Render/Railway have built-in monitoring

**Configure:**
- URL: `https://your-backend-url.com/api/health`
- Interval: 5 minutes
- Alert on: Non-200 response

### 2. Monitor Logs

**Purpose**: Detect issues early

**Check Regularly:**
- ✅ Error logs
- ✅ Database connection issues
- ✅ API errors
- ✅ Performance issues

---

## Troubleshooting Tests

### Test 1: Database Connection Failure

**Simulate:**
- Stop database or use wrong credentials

**Expected Behavior:**
- ✅ Health check returns 503
- ✅ Database status is DOWN
- ✅ Application logs show connection error
- ✅ No crashes (graceful degradation)

### Test 2: Missing Environment Variables

**Simulate:**
- Remove `JWT_SECRET` environment variable

**Expected Behavior:**
- ✅ Application fails to start
- ✅ Clear error message about missing variable
- ✅ Logs show what's missing

### Test 3: Invalid Configuration

**Simulate:**
- Use invalid database URL

**Expected Behavior:**
- ✅ Application starts but database check fails
- ✅ Health check shows database DOWN
- ✅ Clear error messages in logs

---

## ✅ Acceptance Criteria

All of the following must pass:

- [ ] Application builds successfully
- [ ] Docker image builds successfully
- [ ] Application starts in production profile
- [ ] Health check returns 200 OK when healthy
- [ ] Health check returns 503 when database is down
- [ ] Database connection works
- [ ] Environment variables are externalized
- [ ] CORS is configured correctly
- [ ] HTTPS works (platform-provided)
- [ ] Response times are acceptable
- [ ] Frontend can connect to backend
- [ ] All API endpoints work
- [ ] WebSocket connection works
- [ ] Logs are appropriate for production
- [ ] No secrets in code

---

## 📝 Test Results Template

```
Deployment Platform: [Render/Railway/Docker]
Deployment URL: https://...
Deployment Date: YYYY-MM-DD

✅ Build: PASS/FAIL
✅ Health Check: PASS/FAIL
✅ Database Connection: PASS/FAIL
✅ API Endpoints: PASS/FAIL
✅ Frontend Integration: PASS/FAIL
✅ WebSocket: PASS/FAIL
✅ CORS: PASS/FAIL
✅ Security: PASS/FAIL
✅ Performance: PASS/FAIL

Notes:
- [Any issues encountered]
- [Any configuration changes needed]
```

---

**Testing Status**: Ready for testing
**Estimated Testing Time**: 1-2 hours
**Priority**: High (Required for production)

