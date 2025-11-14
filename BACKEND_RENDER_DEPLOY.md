# Backend Deployment to Render - Step by Step

Simplest way to deploy your Spring Boot backend.

---

## 🚀 Step-by-Step Instructions

### 1. Create Render Account
- Go to: https://render.com
- Click "Get Started for Free"
- Sign up with GitHub (easiest)

### 2. Create PostgreSQL Database

1. Click **"New +"** → **"PostgreSQL"**
2. Fill in:
   ```
   Name: database-intelligence-db
   Database: database_intelligence
   Region: Oregon (US West) [or closest to you]
   Plan: Free
   ```
3. Click **"Create Database"**
4. **Wait 2-3 minutes** for database to be ready
5. **Copy these details** (you'll need them):
   - Go to database → **"Connections"** tab
   - Copy: **Internal Database URL**
   - Copy: **Host**, **Port**, **Database**, **Username**, **Password**

### 3. Deploy Backend Service

1. Click **"New +"** → **"Web Service"**
2. Connect GitHub:
   - Click **"Connect account"** if needed
   - Select: **`DatabaseIntelligenceAi`**
   - Click **"Connect"**
3. Configure:
   ```
   Name: ai-database-intelligence-backend
   Region: Same as database
   Branch: main
   Root Directory: backend  ⚠️ IMPORTANT!
   Runtime: Java
   Build Command: ./mvnw clean package -DskipTests
   Start Command: java -jar target/*.jar
   Plan: Free
   ```
4. Click **"Create Web Service"**

### 4. Add Environment Variables

In your service → **"Environment"** tab → Click **"Add Environment Variable"**:

**Variable 1:**
```
Key: SPRING_PROFILES_ACTIVE
Value: production
```

**Variable 2:**
```
Key: OPENAI_API_KEY
Value: sk-your-actual-openai-key
```
*(Get from: https://platform.openai.com/api-keys)*

**Variable 3:**
```
Key: JWT_SECRET
Value: your-secret-key-at-least-32-characters-long
```
*(Generate a random string, min 32 chars)*

**Variable 4:**
```
Key: SPRING_DATASOURCE_URL
Value: jdbc:postgresql://your-db-host:5432/database_intelligence
```
*(From your database "Connections" tab - use Internal Database URL format)*

**Variable 5:**
```
Key: SPRING_DATASOURCE_USERNAME
Value: your-db-username
```
*(From database connections)*

**Variable 6:**
```
Key: SPRING_DATASOURCE_PASSWORD
Value: your-db-password
```
*(From database connections)*

**Variable 7:**
```
Key: CORS_ALLOWED_ORIGINS
Value: https://placeholder.netlify.app
```
*(We'll update this after frontend deploy)*

### 5. Wait for Deployment

- First build: **5-10 minutes**
- Watch **"Logs"** tab for progress
- Look for: `BUILD SUCCESS` and `Started Application`
- Your URL: `https://ai-database-intelligence-backend.onrender.com`

### 6. Test Backend

Open in browser:
```
https://ai-database-intelligence-backend.onrender.com/api/health
```

Should see:
```json
{"status":"UP","message":"AI Database Intelligence Platform"}
```

**✅ If you see this, backend is working!**

---

## 🎯 That's It!

Your backend is now live on Render! 

**Next**: Deploy frontend to Netlify (see `FRONTEND_NETLIFY_DEPLOY.md`)

