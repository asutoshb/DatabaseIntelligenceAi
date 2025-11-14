# Simple Deployment Guide - Backend (Render) + Frontend (Netlify)

Complete step-by-step guide to deploy both backend and frontend in the simplest way possible.

---

## 🎯 Overview

- **Backend**: Deploy to **Render.com** (easiest for Spring Boot)
- **Frontend**: Deploy to **Netlify.com** (perfect for React)

**Time Required**: 15-20 minutes total

---

## 📦 Part 1: Deploy Backend to Render

### Step 1: Create Render Account
1. Go to **[render.com](https://render.com)**
2. Click **"Get Started for Free"**
3. Sign up with **GitHub** (easiest way)
4. Authorize Render to access your repositories

### Step 2: Create PostgreSQL Database
1. In Render dashboard, click **"New +"** → **"PostgreSQL"**
2. Configure:
   - **Name**: `database-intelligence-db`
   - **Database**: `database_intelligence`
   - **User**: Leave default or set your own
   - **Region**: Choose closest to you (e.g., `Oregon (US West)`)
   - **PostgreSQL Version**: `16` (or latest)
   - **Plan**: **Free**
3. Click **"Create Database"**
4. **⚠️ IMPORTANT**: Wait 2-3 minutes for database to be ready
5. **Save these details** (you'll need them):
   - Internal Database URL
   - Host
   - Port (usually 5432)
   - Database name
   - Username
   - Password

### Step 3: Deploy Backend Service
1. In Render dashboard, click **"New +"** → **"Web Service"**
2. Connect your GitHub repository:
   - Click **"Connect account"** if not connected
   - Select repository: **`DatabaseIntelligenceAi`**
   - Click **"Connect"**
3. Configure the service:
   - **Name**: `ai-database-intelligence-backend`
   - **Region**: Same as database (e.g., `Oregon (US West)`)
   - **Branch**: `main`
   - **Root Directory**: `backend` ⚠️ **IMPORTANT!**
   - **Runtime**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Plan**: **Free**
4. Click **"Create Web Service"**

### Step 4: Set Environment Variables
1. In your service page, go to **"Environment"** tab
2. Click **"Add Environment Variable"** and add these one by one:

```
SPRING_PROFILES_ACTIVE = production
```

```
OPENAI_API_KEY = sk-your-openai-key-here
```
*(Get from: https://platform.openai.com/api-keys)*

```
JWT_SECRET = your-secret-key-at-least-32-characters-long-for-production
```
*(Generate a random string, at least 32 characters)*

```
SPRING_DATASOURCE_URL = jdbc:postgresql://your-db-host:5432/database_intelligence
```
*(Get from your PostgreSQL database page → "Internal Database URL")*

```
SPRING_DATASOURCE_USERNAME = your-db-username
```
*(From your PostgreSQL database)*

```
SPRING_DATASOURCE_PASSWORD = your-db-password
```
*(From your PostgreSQL database)*

```
CORS_ALLOWED_ORIGINS = https://your-frontend-name.netlify.app
```
*(We'll update this after frontend is deployed - use placeholder for now: `https://placeholder.netlify.app`)*

3. **Save** each variable

### Step 5: Wait for Deployment
1. Render will automatically start building
2. **First build takes 5-10 minutes**
3. Watch the build logs:
   - Click **"Logs"** tab to see progress
   - Look for: `BUILD SUCCESS` and `Started Application`
4. **Your backend URL will be**: `https://ai-database-intelligence-backend.onrender.com`
   *(or whatever name you chose)*

### Step 6: Verify Backend is Working
1. Open your backend URL in browser: `https://ai-database-intelligence-backend.onrender.com/api/health`
2. You should see:
   ```json
   {
     "status": "UP",
     "message": "AI Database Intelligence Platform",
     "checks": {
       "application": {"status": "UP"},
       "database": {"status": "UP"}
     }
   }
   ```
3. **✅ If you see this, backend is deployed!**

---

## 🎨 Part 2: Deploy Frontend to Netlify

### Step 1: Create Netlify Account
1. Go to **[netlify.com](https://www.netlify.com)**
2. Click **"Sign up"**
3. Choose **"Sign up with GitHub"**
4. Authorize Netlify to access your repositories

### Step 2: Create New Site
1. In Netlify dashboard, click **"Add new site"** → **"Import an existing project"**
2. Click **"GitHub"** (or connect if not connected)
3. Select repository: **`DatabaseIntelligenceAi`**
4. Click **"Next"**

### Step 3: Configure Build Settings
**⚠️ IMPORTANT**: Since your repo has both `frontend` and `backend`:

1. Click **"Show advanced"** or look for **"Base directory"**
2. Set **Base directory**: `frontend`
3. **Build command**: `npm run build` (should auto-detect)
4. **Publish directory**: `dist` (should auto-detect)

Your settings should look like:
```
Base directory: frontend
Build command: npm run build
Publish directory: dist
```

5. Click **"Deploy site"**

### Step 4: Set Environment Variables
1. Wait for first deployment to complete (2-3 minutes)
2. Go to **"Site settings"** → **"Environment variables"**
3. Click **"Add variable"** and add:

```
VITE_API_BASE_URL = https://ai-database-intelligence-backend.onrender.com/api
```
*(Replace with your actual Render backend URL)*

```
VITE_WS_URL = https://ai-database-intelligence-backend.onrender.com/api/ws
```
*(Same backend URL, but with `/api/ws`)*

4. **Save** each variable

### Step 5: Redeploy with Environment Variables
1. Go to **"Deploys"** tab
2. Click **"Trigger deploy"** → **"Deploy site"**
3. Wait for rebuild (2-3 minutes)
4. **Your frontend URL will be**: `https://random-name-123.netlify.app`
   *(Netlify generates a random name)*

### Step 6: Update Backend CORS
1. Go back to **Render** dashboard
2. Open your backend service
3. Go to **"Environment"** tab
4. Find `CORS_ALLOWED_ORIGINS` variable
5. Click **"Edit"**
6. Update value to your Netlify URL:
   ```
   https://your-frontend-name.netlify.app
   ```
7. **Save**
8. Render will automatically redeploy (takes 2-3 minutes)

---

## ✅ Part 3: Verify Everything Works

### Test Backend
```bash
curl https://ai-database-intelligence-backend.onrender.com/api/health
```
Should return: `{"status":"UP",...}`

### Test Frontend
1. Open your Netlify URL in browser
2. Open browser DevTools (F12) → Console
3. You should see: `✅ WebSocket connected`
4. Try submitting a query
5. Check if it connects to backend

### Test Full Flow
1. Select a database
2. Enter a natural language query
3. Click "Convert to SQL"
4. Verify it generates SQL
5. Click "Execute Query"
6. Verify results appear

---

## 🎉 Success Checklist

- [ ] ✅ Backend deployed to Render
- [ ] ✅ Backend health check works
- [ ] ✅ Frontend deployed to Netlify
- [ ] ✅ Frontend loads correctly
- [ ] ✅ Environment variables set in both
- [ ] ✅ Backend CORS updated with Netlify URL
- [ ] ✅ WebSocket connection works
- [ ] ✅ Full flow works (NL → SQL → Results)

---

## 🆘 Troubleshooting

### Backend Build Fails
- Check **Root Directory** is set to `backend`
- Verify `backend/pom.xml` exists
- Check build logs in Render dashboard

### Backend Can't Connect to Database
- Verify database is running (green status in Render)
- Check database credentials are correct
- Ensure `SPRING_DATASOURCE_URL` uses internal database URL

### Frontend Can't Connect to Backend
- Verify `VITE_API_BASE_URL` is correct
- Check backend is running (test health endpoint)
- Verify CORS is set correctly in backend

### WebSocket Fails
- Ensure `VITE_WS_URL` uses `https://` (not `http://`)
- Check backend WebSocket endpoint is accessible
- Verify CORS allows Netlify origin

---

## 📝 Quick Reference

**Backend URL**: `https://ai-database-intelligence-backend.onrender.com`  
**Frontend URL**: `https://your-name.netlify.app`  
**Backend Health**: `https://your-backend.onrender.com/api/health`

**Render Dashboard**: [render.com](https://render.com)  
**Netlify Dashboard**: [app.netlify.com](https://app.netlify.com)

---

**That's it! Your app is now live! 🚀**

