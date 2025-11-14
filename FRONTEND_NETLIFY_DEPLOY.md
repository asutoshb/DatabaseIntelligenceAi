# Frontend Deployment to Netlify - Step by Step

Simplest way to deploy your React frontend.

---

## 🚀 Step-by-Step Instructions

### 1. Create Netlify Account
- Go to: https://www.netlify.com
- Click "Sign up"
- Choose "Sign up with GitHub"
- Authorize Netlify

### 2. Create New Site

1. Click **"Add new site"** → **"Import an existing project"**
2. Click **"GitHub"** (connect if needed)
3. Select repository: **`DatabaseIntelligenceAi`**
4. Click **"Next"**

### 3. Configure Build Settings

**⚠️ IMPORTANT**: Your repo has both `frontend` and `backend` folders!

1. Click **"Show advanced"** or find **"Base directory"**
2. Set:
   ```
   Base directory: frontend
   Build command: npm run build
   Publish directory: dist
   ```
3. Click **"Deploy site"**

### 4. Wait for First Build

- Takes **2-3 minutes**
- Watch build logs
- Your site URL: `https://random-name-123.netlify.app`

### 5. Set Environment Variables

1. Go to **"Site settings"** → **"Environment variables"**
2. Click **"Add variable"**

**Variable 1:**
```
Key: VITE_API_BASE_URL
Value: https://ai-database-intelligence-backend.onrender.com/api
```
*(Replace with your actual Render backend URL)*

**Variable 2:**
```
Key: VITE_WS_URL
Value: https://ai-database-intelligence-backend.onrender.com/api/ws
```
*(Same backend URL, but with `/api/ws`)*

3. **Save** each variable

### 6. Redeploy with Variables

1. Go to **"Deploys"** tab
2. Click **"Trigger deploy"** → **"Deploy site"**
3. Wait **2-3 minutes** for rebuild

### 7. Update Backend CORS

1. Go to **Render** dashboard
2. Open your backend service
3. Go to **"Environment"** tab
4. Find `CORS_ALLOWED_ORIGINS`
5. Click **"Edit"**
6. Update to your Netlify URL:
   ```
   https://your-frontend-name.netlify.app
   ```
7. **Save** (Render auto-redeploys)

### 8. Test Your Site

1. Open your Netlify URL
2. Open browser DevTools (F12) → Console
3. Should see: `✅ WebSocket connected`
4. Try submitting a query
5. **✅ Everything should work!**

---

## 🎯 That's It!

Your frontend is now live on Netlify!

**Your app is fully deployed! 🎉**

---

## 📝 Quick Checklist

- [ ] ✅ Site created on Netlify
- [ ] ✅ Base directory set to `frontend`
- [ ] ✅ Environment variables added
- [ ] ✅ Site redeployed
- [ ] ✅ Backend CORS updated
- [ ] ✅ Frontend loads correctly
- [ ] ✅ WebSocket connects
- [ ] ✅ Full flow works

