# Step-by-Step Netlify Deployment Guide

Follow these steps **exactly** to deploy your frontend to Netlify and view it on a Netlify domain.

---

## ⚠️ STEP 0: Check Your Backend Status

**IMPORTANT:** Before deploying frontend, we need to know where your backend is running.

### Option A: Backend Already Deployed to Cloud (Render/Railway)

If you deployed your backend to Render or Railway in Chunk 13:

1. **Find your backend URL:**
   - **Render**: Go to [render.com](https://render.com) → Your service → Copy the URL (e.g., `https://ai-database-intelligence.onrender.com`)
   - **Railway**: Go to [railway.app](https://railway.app) → Your service → Copy the URL (e.g., `https://ai-database-intelligence.up.railway.app`)

2. **Test your backend:**
   ```bash
   curl https://your-backend-url.onrender.com/api/health
   ```
   Should return: `{"status":"UP",...}`

3. **✅ If backend is working:** Proceed to Step 1 below. You'll use this URL in Step 6.

### Option B: Backend Running Locally in Docker

If your backend is only running locally in Docker (like `localhost:8081`):

**⚠️ Problem:** Netlify frontend **CANNOT** access `localhost` - it needs a public URL.

**Solution:** You have 2 choices:

#### Choice 1: Deploy Backend to Cloud First (Recommended)

1. **Quick Deploy to Render (5-10 minutes):**
   - Go to [render.com](https://render.com)
   - Sign up/login with GitHub
   - Click "New +" → "Web Service"
   - Connect your GitHub repo: `DatabaseIntelligenceAi`
   - Configure:
     - **Name**: `ai-database-intelligence-backend`
     - **Root Directory**: `backend`
     - **Build Command**: `./mvnw clean package -DskipTests`
     - **Start Command**: `java -jar target/*.jar`
   - Add environment variables (see `DEPLOYMENT_GUIDE.md`)
   - Deploy and wait for URL

2. **Or Deploy to Railway:**
   - Go to [railway.app](https://railway.app)
   - Sign up with GitHub
   - Click "New" → "GitHub Repo" → Select your repo
   - Railway auto-detects Java project
   - Add environment variables
   - Deploy and wait for URL

3. **After backend is deployed:** Use the cloud URL in Step 6.

#### Choice 2: Use Localhost for Testing (Temporary)

If you want to test frontend deployment first (backend will not work from Netlify):

1. In Step 6, use:
   ```
   VITE_API_BASE_URL = http://localhost:8080/api
   VITE_WS_URL = http://localhost:8080/api/ws
   ```

2. **⚠️ Note:** This will only work if:
   - You're testing locally
   - You run backend locally while testing
   - Netlify frontend **WILL NOT** be able to connect to localhost

3. **Later:** Deploy backend and update environment variables.

**📖 Full Backend Deployment Guide:** See `DEPLOYMENT_GUIDE.md` for detailed steps.

---

## ✅ STEP 1: Verify Prerequisites

Before starting, make sure you have:

- [x] ✅ Frontend code committed and pushed to GitHub
- [ ] ⚠️ **Backend Status Check Required** (see Step 0 above)
- [x] ✅ GitHub account
- [x] ✅ Netlify account (we'll create one if needed)

**Current Status Check:**
```bash
# Your code is already committed and ready to push
# Run this to push to GitHub:
git push origin main
```

---

## ✅ STEP 2: Push Code to GitHub

**Action Required:**

1. Open terminal in your project directory
2. Run these commands:

```bash
cd /Users/asutoshbhere/DatabaseAI
git push origin main
```

**Expected Output:**
```
Enumerating objects: X, done.
Counting objects: 100% (X/X), done.
Writing objects: 100% (X/X), done.
To https://github.com/asutoshb/DatabaseIntelligenceAi.git
   main -> main
```

**✅ If successful:** Your code is now on GitHub and ready for deployment!

---

## ✅ STEP 3: Sign Up / Login to Netlify

**Action Required:**

1. **Open your web browser**
2. **Go to:** [https://www.netlify.com](https://www.netlify.com)
3. **Click:** "Sign up" (or "Log in" if you already have an account)
4. **Choose:** "Sign up with GitHub" (recommended - easiest way)
5. **Authorize Netlify** to access your GitHub account
6. **Complete** the sign-up process

**✅ Once logged in:** You'll see the Netlify dashboard

---

## ✅ STEP 4: Create New Site from GitHub

**Action Required:**

1. **In Netlify Dashboard:**
   - Click the **"Add new site"** button (top right, green button)
   - Select **"Import an existing project"**

2. **Connect to Git:**
   - Click **"GitHub"** (or "GitLab" / "Bitbucket" if you use those)
   - **Authorize** Netlify to access your repositories (if prompted)

3. **Select Repository:**
   - Search for: `DatabaseIntelligenceAi`
   - Click on your repository

4. **Configure Branch:**
   - **Branch to deploy:** `main` (should be selected by default)
   - Click **"Next"**

**✅ You should now see build settings**

---

## ✅ STEP 5: Configure Build Settings

**Action Required:**

**IMPORTANT:** Since your repo has both `frontend` and `backend` folders, you need to tell Netlify to build only the frontend.

1. **Base directory:**
   - Click **"Show advanced"** or look for "Base directory" field
   - Enter: `frontend`
   - This tells Netlify to look in the `frontend` folder

2. **Build command:**
   - Should auto-detect: `npm run build`
   - If not, enter: `npm run build`

3. **Publish directory:**
   - Should auto-detect: `dist`
   - If not, enter: `dist`

**Your settings should look like this:**
```
Base directory: frontend
Build command: npm run build
Publish directory: dist
```

4. **Click "Deploy site"** (or "Save" then "Deploy")

**✅ Netlify will now start building your site!**

---

## ✅ STEP 6: Wait for First Build

**What's Happening:**

Netlify is now:
1. Cloning your repository
2. Installing dependencies (`npm install`)
3. Building your app (`npm run build`)
4. Deploying to CDN

**Time:** Usually takes 2-5 minutes

**You'll see:**
- Build logs in real-time
- Progress indicators
- Success or error messages

**✅ Wait for:** "Site is live" message

---

## ✅ STEP 7: Set Environment Variables

**Action Required:**

**IMPORTANT:** You need to set environment variables so your frontend knows where your backend is.

1. **In Netlify Dashboard:**
   - Go to your site (click on the site name)
   - Click **"Site settings"** (top menu)
   - Click **"Environment variables"** (left sidebar)

2. **Add First Variable:**
   - Click **"Add variable"**
   - **Key:** `VITE_API_BASE_URL`
   - **Value:** 
     - If backend is deployed: `https://your-backend.onrender.com/api`
     - If testing locally: `http://localhost:8080/api` (for now)
   - **Scopes:** Select "All scopes" (Production, Deploy previews, Branch deploys)
   - Click **"Save"**

3. **Add Second Variable:**
   - Click **"Add variable"** again
   - **Key:** `VITE_WS_URL`
   - **Value:**
     - If backend is deployed: `https://your-backend.onrender.com/api/ws`
     - If testing locally: `http://localhost:8080/api/ws` (for now)
   - **Scopes:** Select "All scopes"
   - Click **"Save"**

**Example Values (if backend is on Render):**
```
VITE_API_BASE_URL = https://ai-database-intelligence.onrender.com/api
VITE_WS_URL = https://ai-database-intelligence.onrender.com/api/ws
```

**⚠️ Important:** 
- Replace `your-backend.onrender.com` with your actual backend URL
- Use `https://` (not `http://`) for production
- After adding variables, you MUST redeploy (see Step 8)

---

## ✅ STEP 8: Redeploy with Environment Variables

**Action Required:**

After adding environment variables, you need to trigger a new deployment:

1. **In Netlify Dashboard:**
   - Go to **"Deploys"** tab (top menu)
   - Click **"Trigger deploy"** → **"Deploy site"**
   - Or click the **"..."** menu on the latest deploy → **"Retry deploy"**

2. **Wait for rebuild:**
   - Netlify will rebuild with your environment variables
   - This takes 2-5 minutes

**✅ After rebuild:** Your site will use the new environment variables

---

## ✅ STEP 9: View Your Deployed Site

**Action Required:**

1. **In Netlify Dashboard:**
   - Go to **"Overview"** tab
   - Look for **"Production deploy"** section
   - You'll see a URL like: `https://random-name-123.netlify.app`

2. **Click the URL** or copy and paste it in your browser

3. **Your site should load!** 🎉

**Example URL Format:**
```
https://amazing-database-ai-12345.netlify.app
```

---

## ✅ STEP 10: Test Your Deployment

**Action Required:**

1. **Open your Netlify URL** in a browser

2. **Check Browser Console:**
   - Press `F12` (or right-click → Inspect)
   - Go to **Console** tab
   - Look for: `✅ WebSocket connected` (if backend is running)

3. **Test Features:**
   - ✅ Page loads correctly
   - ✅ No console errors
   - ✅ UI displays properly
   - ✅ Try submitting a query (if backend is connected)

4. **Check Network Tab:**
   - Go to **Network** tab in DevTools
   - Try loading databases
   - Verify API calls go to correct backend URL

**✅ If everything works:** Your deployment is successful!

---

## ✅ STEP 11: Update Backend CORS (If Backend is Deployed)

**Action Required:**

If your backend is already deployed, you need to allow your Netlify frontend URL:

1. **Get your Netlify URL:**
   - Copy your Netlify site URL (e.g., `https://amazing-database-ai-12345.netlify.app`)

2. **Update Backend CORS:**
   - Go to your backend deployment platform (Render/Railway)
   - Add environment variable:
     ```
     CORS_ALLOWED_ORIGINS = https://amazing-database-ai-12345.netlify.app
     ```
   - Or update `application-production.properties`:
     ```properties
     cors.allowed-origins=https://amazing-database-ai-12345.netlify.app
     ```
   - Redeploy backend

**✅ After backend redeploy:** Frontend and backend will communicate properly

---

## ✅ STEP 12: Custom Domain (Optional)

**Action Required (Optional):**

If you want a custom domain (e.g., `myapp.com`):

1. **In Netlify Dashboard:**
   - Go to **"Site settings"** → **"Domain management"**
   - Click **"Add custom domain"**
   - Enter your domain name
   - Follow DNS configuration instructions

2. **Update DNS:**
   - Add CNAME record in your domain provider
   - Point to your Netlify site

**✅ After DNS propagates:** Your site will be accessible via custom domain

---

## 🎉 Success Checklist

After completing all steps, verify:

- [x] ✅ Code pushed to GitHub
- [x] ✅ Netlify account created
- [x] ✅ Site created and connected to GitHub
- [x] ✅ Build settings configured (base directory: `frontend`)
- [x] ✅ Environment variables set (`VITE_API_BASE_URL`, `VITE_WS_URL`)
- [x] ✅ Site deployed successfully
- [x] ✅ Site accessible via Netlify URL
- [x] ✅ Frontend loads correctly
- [x] ✅ Backend CORS updated (if backend deployed)
- [x] ✅ All features working

---

## 🆘 Troubleshooting

### Issue: Build Fails

**Solution:**
- Check build logs in Netlify dashboard
- Verify `package.json` exists in `frontend` folder
- Check Node.js version (should be 18+)
- Ensure all dependencies are in `package.json`

### Issue: Site Shows 404

**Solution:**
- Check `netlify.toml` exists in `frontend` folder
- Verify redirect rule: `/* → /index.html`
- Check publish directory is `dist`

### Issue: API Calls Fail

**Solution:**
- Verify `VITE_API_BASE_URL` is set correctly
- Check backend is accessible
- Verify backend CORS includes Netlify URL
- Check browser console for CORS errors

### Issue: WebSocket Fails

**Solution:**
- Verify `VITE_WS_URL` is set correctly
- Ensure URL uses `https://` (not `http://`)
- Check backend WebSocket endpoint is accessible

---

## 📝 Quick Reference

**Netlify Dashboard:** [https://app.netlify.com](https://app.netlify.com)

**Your Site URL:** Check in Netlify dashboard → Overview tab

**Environment Variables:** Site settings → Environment variables

**Build Logs:** Deploys tab → Click on any deploy

**Redeploy:** Deploys tab → Trigger deploy

---

## 🎯 Next Steps

After successful deployment:

1. **Share your app** with the Netlify URL
2. **Monitor performance** using Netlify analytics
3. **Set up custom domain** (optional)
4. **Configure automatic deployments** (already enabled by default)
5. **Set up error tracking** (optional)

---

**Congratulations! Your frontend is now live on Netlify! 🚀**

If you encounter any issues, check the troubleshooting section or the main `FRONTEND_DEPLOYMENT_GUIDE.md` file.

