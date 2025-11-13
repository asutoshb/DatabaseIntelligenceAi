# Chunk 13: Backend Deployment

## Goal
Deploy the Spring Boot backend to a cloud platform so it's accessible from anywhere via a public URL.

## What We'll Build

### 1. Railway Deployment Configuration
- `railway.json` or Railway-specific configuration
- Environment variables setup
- Build and start commands
- Database connection configuration

### 2. Render Deployment Configuration
- `render.yaml` configuration file
- Environment variables setup
- Build and start commands
- Health check configuration

### 3. Docker Configuration (Optional but Recommended)
- `Dockerfile` for containerization
- `.dockerignore` file
- Multi-stage build for optimization
- Production-ready configuration

### 4. Production Environment Variables
- Documentation for required environment variables
- Production vs development differences
- Security best practices

### 5. Enhanced Health Check
- Database connectivity check
- External service checks (OpenAI API)
- Detailed health status endpoint
- Ready for cloud platform health checks

### 6. Production Configuration
- `application-production.properties` profile
- CORS configuration for production domains
- Logging configuration for production
- Performance optimizations

## Technical Implementation

### Files to Create/Modify

1. **`backend/Dockerfile`** (NEW)
   - Multi-stage Docker build
   - Optimized for production
   - Java 17 base image

2. **`backend/.dockerignore`** (NEW)
   - Exclude unnecessary files from Docker build

3. **`backend/render.yaml`** (NEW)
   - Render.com deployment configuration
   - Service definition
   - Environment variables
   - Health check path

4. **`backend/railway.json`** (NEW - Optional)
   - Railway deployment configuration
   - Build and start commands

5. **`backend/src/main/resources/application-production.properties`** (NEW)
   - Production-specific configuration
   - Optimized settings
   - Production logging

6. **`backend/src/main/java/com/databaseai/controller/HealthController.java`** (MODIFY)
   - Enhanced health check with database connectivity
   - External service checks
   - Detailed status information

7. **`DEPLOYMENT_GUIDE.md`** (NEW)
   - Step-by-step deployment instructions
   - Platform-specific guides (Railway, Render)
   - Troubleshooting guide

## Implementation Steps

### Step 1: Create Dockerfile
- Use multi-stage build
- Java 17 base image
- Copy application files
- Expose port 8080
- Set entrypoint

### Step 2: Create Production Properties
- Separate production profile
- Optimized JPA settings
- Production logging
- CORS for production domains

### Step 3: Enhance Health Check
- Check database connectivity
- Check OpenAI API availability (optional)
- Return detailed status

### Step 4: Create Platform Configurations
- Render.yaml for Render.com
- Railway.json for Railway
- Environment variable documentation

### Step 5: Create Deployment Documentation
- Step-by-step guides
- Platform-specific instructions
- Troubleshooting tips

## Deployment Platforms

### Railway
- **Pros**: Easy setup, automatic deployments, PostgreSQL addon
- **Cons**: Free tier limitations
- **Best For**: Quick deployments, small projects

### Render
- **Pros**: Free tier, PostgreSQL support, easy setup
- **Cons**: Free tier has limitations
- **Best For**: Production deployments, medium projects

### Heroku
- **Pros**: Well-established, many addons
- **Cons**: No free tier anymore
- **Best For**: Enterprise projects

## Environment Variables Required

### Required for Production
- `DATABASE_URL` or individual DB config (host, port, name, username, password)
- `OPENAI_API_KEY` - OpenAI API key
- `JWT_SECRET` - JWT secret key (at least 32 characters)
- `PORT` - Server port (usually set by platform)

### Optional
- `JWT_EXPIRATION` - Token expiration (default: 86400000)
- `SPRING_PROFILES_ACTIVE` - Active profile (production)
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins

## Success Criteria

✅ Backend deployed to cloud platform
✅ Accessible via public URL
✅ Database connected and working
✅ Health check endpoint responds correctly
✅ Environment variables properly configured
✅ API endpoints working from anywhere
✅ CORS configured for frontend domain

## Tech Stack to Learn

- **Docker**: Containerization
- **Cloud Platforms**: Railway, Render, Heroku
- **Environment Variables**: Production configuration
- **Health Checks**: Application monitoring
- **CI/CD**: Continuous deployment basics

## Deployment Flow

1. **Prepare Application**
   - Ensure all environment variables are externalized
   - Test locally with production-like settings
   - Build and test Docker image locally (optional)

2. **Choose Platform**
   - Select Railway, Render, or Heroku
   - Create account if needed

3. **Deploy**
   - Connect GitHub repository
   - Configure environment variables
   - Set build and start commands
   - Deploy

4. **Verify**
   - Check health endpoint
   - Test API endpoints
   - Verify database connection
   - Test authentication

5. **Configure Frontend**
   - Update frontend API URL
   - Update CORS settings if needed
   - Test end-to-end

---

**Estimated Time**: 2-3 hours
**Difficulty**: Medium
**Prerequisites**: Chunk 1-12 (Backend and Frontend complete) ✅

