# Backend Deployment Guide

Complete guide for deploying the AI Database Intelligence Platform backend to cloud platforms.

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Variables](#environment-variables)
3. [Deploy to Render](#deploy-to-render)
4. [Deploy to Railway](#deploy-to-railway)
5. [Deploy with Docker](#deploy-with-docker)
6. [Post-Deployment Verification](#post-deployment-verification)
7. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before deploying, ensure you have:

- ✅ Backend code pushed to GitHub
- ✅ PostgreSQL database (cloud-hosted or platform-provided)
- ✅ OpenAI API key
- ✅ JWT secret key (at least 32 characters)

---

## Environment Variables

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `OPENAI_API_KEY` | Your OpenAI API key | `sk-...` |
| `JWT_SECRET` | Secret key for JWT tokens (min 32 chars) | `mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong` |
| `SPRING_DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://host:5432/dbname` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `password123` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `production` |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_EXPIRATION` | JWT token expiration (ms) | `86400000` (24 hours) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:3000` |
| `PORT` | Server port | `8080` (usually set by platform) |
| `LOGGING_LEVEL_ROOT` | Root logging level | `INFO` |

### Platform-Specific Variables

Some platforms provide `DATABASE_URL` in format: `postgresql://user:pass@host:port/dbname`

If your platform provides this, you can use it directly:
```properties
spring.datasource.url=${DATABASE_URL}
```

---

## Deploy to Render

### Step 1: Create Render Account

1. Go to [render.com](https://render.com)
2. Sign up for a free account
3. Connect your GitHub account

### Step 2: Create PostgreSQL Database

1. In Render dashboard, click **"New +"** → **"PostgreSQL"**
2. Configure:
   - **Name**: `database-intelligence-db`
   - **Database**: `database_intelligence`
   - **User**: `database_user` (or auto-generated)
   - **Region**: Choose closest to you
   - **Plan**: Free (or paid for production)
3. Click **"Create Database"**
4. **Save the connection details** (you'll need them)

### Step 3: Deploy Backend Service

1. In Render dashboard, click **"New +"** → **"Web Service"**
2. Connect your GitHub repository
3. Configure:
   - **Name**: `ai-database-intelligence-backend`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Plan**: Free (or paid for production)
4. Click **"Create Web Service"**

### Step 4: Configure Environment Variables

In your service settings, add these environment variables:

```
SPRING_PROFILES_ACTIVE=production
OPENAI_API_KEY=sk-your-openai-key-here
JWT_SECRET=your-jwt-secret-at-least-32-characters-long
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/database_intelligence
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app
```

**Or use Render's PostgreSQL connection:**

If you created a PostgreSQL database in Render, you can use the **Internal Database URL**:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-xxxxx-a/database_intelligence
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
```

### Step 5: Deploy

1. Render will automatically build and deploy
2. Wait for deployment to complete (5-10 minutes)
3. Your backend will be available at: `https://your-service-name.onrender.com`

### Step 6: Verify Deployment

```bash
curl https://your-service-name.onrender.com/api/health
```

Expected response:
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

## Deploy to Railway

### Step 1: Create Railway Account

1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub
3. Create a new project

### Step 2: Add PostgreSQL Database

1. Click **"New"** → **"Database"** → **"Add PostgreSQL"**
2. Railway will automatically create the database
3. **Save the connection details** from the Variables tab

### Step 3: Deploy Backend

1. Click **"New"** → **"GitHub Repo"**
2. Select your repository
3. Railway will auto-detect it's a Java project
4. Add environment variables (see below)

### Step 4: Configure Environment Variables

In Railway dashboard, go to your service → **Variables** tab, add:

```
SPRING_PROFILES_ACTIVE=production
OPENAI_API_KEY=sk-your-openai-key-here
JWT_SECRET=your-jwt-secret-at-least-32-characters-long
```

Railway automatically provides:
- `DATABASE_URL` (if you added PostgreSQL)
- `PORT` (automatically set)

### Step 5: Configure Database Connection

Railway provides `DATABASE_URL` in format: `postgresql://user:pass@host:port/dbname`

You can either:
1. **Use DATABASE_URL directly** (update `application.properties` to parse it)
2. **Or extract individual components** and set:
   ```
   SPRING_DATASOURCE_URL=${DATABASE_URL}
   ```

### Step 6: Deploy

1. Railway will automatically build and deploy
2. Wait for deployment (3-5 minutes)
3. Your backend will be available at: `https://your-service-name.up.railway.app`

### Step 7: Verify Deployment

```bash
curl https://your-service-name.up.railway.app/api/health
```

---

## Deploy with Docker

### Step 1: Build Docker Image

```bash
cd backend
docker build -t ai-database-intelligence-backend .
```

### Step 2: Run Locally (for testing)

```bash
docker run -p 8080:8080 \
  -e OPENAI_API_KEY=sk-your-key \
  -e JWT_SECRET=your-secret \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/dbname \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=pass \
  -e SPRING_PROFILES_ACTIVE=production \
  ai-database-intelligence-backend
```

### Step 3: Deploy to Docker Hub

```bash
# Tag image
docker tag ai-database-intelligence-backend your-username/ai-database-intelligence-backend:latest

# Push to Docker Hub
docker push your-username/ai-database-intelligence-backend:latest
```

### Step 4: Deploy to Cloud Platform

Most platforms support Docker:
- **Render**: Use Dockerfile automatically
- **Railway**: Use Dockerfile automatically
- **Heroku**: Use `heroku.yml` or container registry
- **AWS ECS**: Use container registry
- **Google Cloud Run**: Use container registry

---

## Post-Deployment Verification

### 1. Health Check

```bash
curl https://your-backend-url.com/api/health
```

Should return:
```json
{
  "status": "UP",
  "checks": {
    "application": {"status": "UP"},
    "database": {"status": "UP"}
  }
}
```

### 2. Test API Endpoints

```bash
# Test authentication
curl -X POST https://your-backend-url.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"test123"}'

# Test NL to SQL (if not requiring auth)
curl -X POST https://your-backend-url.com/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{"query":"test query","databaseInfoId":1}'
```

### 3. Check Logs

Most platforms provide logs in their dashboard:
- **Render**: Service → Logs tab
- **Railway**: Service → Deployments → View Logs

Look for:
- ✅ "AI Database Intelligence Platform is running!"
- ✅ Database connection successful
- ❌ Any errors or exceptions

### 4. Update Frontend

Update your frontend `.env` file:

```env
VITE_API_URL=https://your-backend-url.com/api
VITE_WS_URL=https://your-backend-url.com/api/ws
```

---

## Troubleshooting

### Issue 1: Build Fails

**Symptoms:**
- Build command fails
- Maven errors

**Solutions:**
1. Check Java version (needs Java 17+)
2. Verify `pom.xml` is correct
3. Check build logs for specific errors
4. Try building locally first: `./mvnw clean package`

### Issue 2: Application Won't Start

**Symptoms:**
- Deployment succeeds but service is down
- Health check returns 503

**Solutions:**
1. Check environment variables are set correctly
2. Verify `JWT_SECRET` is at least 32 characters
3. Check database connection string
4. Review application logs

### Issue 3: Database Connection Fails

**Symptoms:**
- Health check shows database DOWN
- Connection timeout errors

**Solutions:**
1. Verify database credentials
2. Check database is accessible from platform
3. For Render: Use Internal Database URL (not external)
4. For Railway: Check DATABASE_URL format
5. Verify database is running and accessible

### Issue 4: CORS Errors

**Symptoms:**
- Frontend can't call backend
- CORS policy errors in browser

**Solutions:**
1. Set `CORS_ALLOWED_ORIGINS` environment variable
2. Include your frontend URL (e.g., `https://your-frontend.vercel.app`)
3. Restart backend service
4. Check CORS configuration in `application-production.properties`

### Issue 5: Environment Variables Not Loading

**Symptoms:**
- Application starts but can't find API keys
- Null pointer exceptions

**Solutions:**
1. Verify environment variables are set in platform dashboard
2. Check variable names match exactly (case-sensitive)
3. Restart service after adding variables
4. Check logs for missing variable warnings

### Issue 6: Port Issues

**Symptoms:**
- Service won't start
- Port already in use

**Solutions:**
1. Most platforms set `PORT` automatically
2. Don't hardcode port in code
3. Use `${PORT:8080}` in application.properties
4. Check platform documentation for port configuration

---

## Security Best Practices

1. **Never commit secrets** to Git
2. **Use environment variables** for all sensitive data
3. **Rotate JWT_SECRET** regularly in production
4. **Use HTTPS** (most platforms provide this automatically)
5. **Limit CORS origins** to your frontend domain only
6. **Enable database SSL** if available
7. **Use strong passwords** for database
8. **Monitor logs** for suspicious activity

---

## Next Steps

After backend is deployed:

1. ✅ Update frontend API URL
2. ✅ Test end-to-end functionality
3. ✅ Set up monitoring (optional)
4. ✅ Configure custom domain (optional)
5. ✅ Set up CI/CD for automatic deployments (optional)

---

## Support

If you encounter issues:

1. Check platform documentation
2. Review application logs
3. Test locally with production-like settings
4. Verify all environment variables are set
5. Check database connectivity

---

**Deployment Status**: Ready for deployment
**Estimated Time**: 30-60 minutes
**Difficulty**: Medium

