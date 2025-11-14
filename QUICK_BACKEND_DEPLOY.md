# Quick Backend Deployment Guide

If your backend is only running locally in Docker, you need to deploy it to a cloud platform so your Netlify frontend can access it.

---

## 🚀 Quick Deploy to Render (Recommended - Easiest)

### Step 1: Create Render Account
1. Go to [render.com](https://render.com)
2. Sign up/login with GitHub
3. Authorize Render to access your repositories

### Step 2: Create PostgreSQL Database
1. In Render dashboard, click **"New +"** → **"PostgreSQL"**
2. Configure:
   - **Name**: `database-intelligence-db`
   - **Database**: `database_intelligence`
   - **Region**: Choose closest to you
   - **Plan**: Free
3. Click **"Create Database"**
4. **Save the connection details** (Internal Database URL, Username, Password)

### Step 3: Deploy Backend Service
1. In Render dashboard, click **"New +"** → **"Web Service"**
2. Connect your GitHub repository: `DatabaseIntelligenceAi`
3. Configure:
   - **Name**: `ai-database-intelligence-backend`
   - **Root Directory**: `backend`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Plan**: Free
4. Click **"Create Web Service"**

### Step 4: Configure Environment Variables
In your service settings → **Environment** tab, add:

```
SPRING_PROFILES_ACTIVE=production
OPENAI_API_KEY=sk-your-openai-key-here
JWT_SECRET=your-jwt-secret-at-least-32-characters-long
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/database_intelligence
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
CORS_ALLOWED_ORIGINS=https://your-frontend.netlify.app
```

**Get Database Connection Details:**
- Go to your PostgreSQL database in Render
- Copy the **Internal Database URL** (format: `postgresql://dpg-xxxxx-a/database_intelligence`)
- Extract host, username, password from the URL

**Example:**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-abc123-a.oregon-postgres.render.com:5432/database_intelligence
SPRING_DATASOURCE_USERNAME=database_user
SPRING_DATASOURCE_PASSWORD=your-password-here
```

### Step 5: Deploy
1. Render will automatically build and deploy
2. Wait 5-10 minutes for deployment
3. Your backend URL will be: `https://ai-database-intelligence-backend.onrender.com`

### Step 6: Verify Deployment
```bash
curl https://ai-database-intelligence-backend.onrender.com/api/health
```

Should return:
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

### Step 7: Update CORS (After Frontend Deploy)
After deploying frontend to Netlify, update the `CORS_ALLOWED_ORIGINS` environment variable:
```
CORS_ALLOWED_ORIGINS=https://your-frontend-name.netlify.app
```

Then redeploy the backend.

---

## 🚂 Alternative: Deploy to Railway

### Step 1: Create Railway Account
1. Go to [railway.app](https://railway.app)
2. Sign up with GitHub
3. Create a new project

### Step 2: Add PostgreSQL Database
1. Click **"New"** → **"Database"** → **"Add PostgreSQL"**
2. Railway automatically creates the database
3. Go to **Variables** tab → Copy connection details

### Step 3: Deploy Backend
1. Click **"New"** → **"GitHub Repo"**
2. Select your repository: `DatabaseIntelligenceAi`
3. Railway auto-detects Java project
4. Set **Root Directory**: `backend`

### Step 4: Configure Environment Variables
In Railway dashboard → Your service → **Variables** tab, add:

```
SPRING_PROFILES_ACTIVE=production
OPENAI_API_KEY=sk-your-openai-key-here
JWT_SECRET=your-jwt-secret-at-least-32-characters-long
```

Railway automatically provides:
- `DATABASE_URL` (PostgreSQL connection)
- `PORT` (automatically set)

**Note:** You may need to parse `DATABASE_URL` or set individual database variables.

### Step 5: Deploy
1. Railway automatically builds and deploys
2. Wait 3-5 minutes
3. Your backend URL will be: `https://your-service-name.up.railway.app`

### Step 6: Verify
```bash
curl https://your-service-name.up.railway.app/api/health
```

---

## 📝 Required Environment Variables Summary

| Variable | Description | Where to Get |
|----------|-------------|--------------|
| `OPENAI_API_KEY` | Your OpenAI API key | [platform.openai.com](https://platform.openai.com) |
| `JWT_SECRET` | Secret key (min 32 chars) | Generate a random string |
| `SPRING_DATASOURCE_URL` | Database connection URL | From Render/Railway database |
| `SPRING_DATASOURCE_USERNAME` | Database username | From Render/Railway database |
| `SPRING_DATASOURCE_PASSWORD` | Database password | From Render/Railway database |
| `SPRING_PROFILES_ACTIVE` | Spring profile | Set to `production` |
| `CORS_ALLOWED_ORIGINS` | Frontend URL | Your Netlify URL (after frontend deploy) |

---

## ⚠️ Important Notes

1. **Database Setup:** You need a PostgreSQL database. Both Render and Railway provide free PostgreSQL databases.

2. **Build Time:** First deployment takes 5-10 minutes. Subsequent deployments are faster.

3. **Free Tier Limits:**
   - **Render**: Free tier spins down after 15 minutes of inactivity
   - **Railway**: Free tier has usage limits
   - Both are fine for development/testing

4. **CORS:** Update `CORS_ALLOWED_ORIGINS` after deploying frontend to allow your Netlify URL.

5. **Health Check:** Always verify deployment with the health endpoint before proceeding.

---

## 🆘 Troubleshooting

### Build Fails
- Check build logs in Render/Railway dashboard
- Verify `backend/pom.xml` exists
- Check Maven wrapper (`mvnw`) is present

### Database Connection Fails
- Verify database credentials are correct
- Check database is running (Render/Railway dashboard)
- Ensure database name matches

### Service Not Starting
- Check logs in dashboard
- Verify all environment variables are set
- Check `SPRING_PROFILES_ACTIVE=production`

---

## ✅ After Backend is Deployed

1. **Copy your backend URL** (e.g., `https://ai-database-intelligence.onrender.com`)
2. **Go back to `NETLIFY_DEPLOYMENT_STEPS.md`**
3. **Use your backend URL in Step 6** when setting environment variables:
   ```
   VITE_API_BASE_URL = https://ai-database-intelligence.onrender.com/api
   VITE_WS_URL = https://ai-database-intelligence.onrender.com/api/ws
   ```

---

**Full detailed guide:** See `DEPLOYMENT_GUIDE.md` for more information.

