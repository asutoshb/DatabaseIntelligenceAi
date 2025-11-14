# Fix Railway Build Error

## Problem
Railway is trying to build from the root directory, but your `railway.json` is in the `backend/` folder. Railway can't find the build configuration.

## Solution: Set Root Directory in Railway

### Step 1: Go to Railway Dashboard
1. Open your Railway project: [railway.app](https://railway.app)
2. Click on the **"DatabaseIntelligenceAi"** service (the one that failed)

### Step 2: Set Root Directory
1. Go to **"Settings"** tab (top navigation)
2. Scroll down to **"Source"** section
3. Find **"Root Directory"** field
4. Enter: `backend`
5. Click **"Save"** or **"Update"**

### Step 3: Redeploy
1. Go to **"Deploys"** tab
2. Click **"Redeploy"** or **"Deploy"** button
3. Wait for build to complete (5-10 minutes)

---

## Alternative: Create Root-Level railway.json

If setting root directory doesn't work, create a root-level `railway.json`:

### Option A: Root-Level Config (Points to Backend)

Create `railway.json` in the root directory:

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "cd backend && ./mvnw clean package -DskipTests"
  },
  "deploy": {
    "startCommand": "cd backend && java -jar target/*.jar",
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

### Option B: Use Railway Service Settings

Instead of `railway.json`, configure directly in Railway:

1. Go to **Settings** → **Build & Deploy**
2. Set **Root Directory**: `backend`
3. Set **Build Command**: `./mvnw clean package -DskipTests`
4. Set **Start Command**: `java -jar target/*.jar`

---

## Verify Configuration

After setting root directory, Railway should:
1. ✅ Detect Java project (finds `backend/pom.xml`)
2. ✅ Use Maven wrapper (`backend/mvnw`)
3. ✅ Build successfully
4. ✅ Start the Spring Boot application

---

## Common Issues

### Issue: Still can't find build command
**Solution:** Make sure `backend/mvnw` file exists and is executable. Railway should auto-detect it.

### Issue: Build fails with Maven errors
**Solution:** Check environment variables are set correctly in Railway dashboard.

### Issue: Can't find `target/*.jar`
**Solution:** Verify build command completed successfully. Check build logs.

---

## Quick Fix Steps Summary

1. **Railway Dashboard** → Your Service → **Settings**
2. **Root Directory** → Set to: `backend`
3. **Save**
4. **Redeploy**

That's it! Railway will now look in the `backend/` folder for `pom.xml` and build commands.

