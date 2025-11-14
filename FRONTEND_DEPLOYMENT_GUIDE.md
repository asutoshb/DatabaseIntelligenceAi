# Frontend Deployment Guide

Complete guide for deploying the AI Database Intelligence Platform frontend to cloud platforms.

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Variables](#environment-variables)
3. [Deploy to Netlify](#deploy-to-netlify)
4. [Deploy to Vercel](#deploy-to-vercel)
5. [Post-Deployment Verification](#post-deployment-verification)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before deploying, ensure you have:

- ✅ Frontend code pushed to GitHub
- ✅ Backend deployed and accessible (from Chunk 13)
- ✅ Backend URL and WebSocket URL ready
- ✅ GitHub account
- ✅ Netlify or Vercel account (free tier works)

---

## Environment Variables

### Required Variables

| Variable | Description | Example (Development) | Example (Production) |
|----------|-------------|---------------------|---------------------|
| `VITE_API_BASE_URL` | Backend API base URL | `http://localhost:8080/api` | `https://your-backend.onrender.com/api` |
| `VITE_WS_URL` | WebSocket URL for real-time updates | `http://localhost:8080/api/ws` | `https://your-backend.onrender.com/api/ws` |

### Important Notes

- **All Vite environment variables must be prefixed with `VITE_`**
- These variables are **embedded at build time** (not runtime)
- After changing environment variables, you **must rebuild** the application
- Use `https://` for production URLs (not `http://`)

### Creating `.env.local` for Local Development

Create a file `frontend/.env.local` (this file is git-ignored):

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=http://localhost:8080/api/ws
```

---

## Deploy to Netlify

### Option 1: Deploy via Netlify Dashboard (Recommended)

1. **Sign up/Login to Netlify**
   - Go to [netlify.com](https://www.netlify.com)
   - Sign up or log in with GitHub

2. **Create New Site**
   - Click "Add new site" → "Import an existing project"
   - Select "GitHub" and authorize Netlify
   - Select your repository: `DatabaseIntelligenceAi`
   - Select branch: `main`

3. **Configure Build Settings**
   - **Base directory**: `frontend` (if your repo has both frontend and backend)
   - **Build command**: `npm run build`
   - **Publish directory**: `dist`

4. **Set Environment Variables**
   - Go to Site settings → Environment variables
   - Add the following:
     ```
     VITE_API_BASE_URL = https://your-backend.onrender.com/api
     VITE_WS_URL = https://your-backend.onrender.com/api/ws
     ```
   - Replace `your-backend.onrender.com` with your actual backend URL

5. **Deploy**
   - Click "Deploy site"
   - Wait for build to complete (usually 2-3 minutes)
   - Your site will be live at `https://random-name-123.netlify.app`

6. **Custom Domain (Optional)**
   - Go to Site settings → Domain management
   - Add your custom domain
   - Follow DNS configuration instructions

### Option 2: Deploy via Netlify CLI

```bash
# Install Netlify CLI
npm install -g netlify-cli

# Login to Netlify
netlify login

# Navigate to frontend directory
cd frontend

# Initialize and deploy
netlify init
netlify deploy --prod
```

### Netlify Configuration

The `netlify.toml` file is already configured with:
- Build command: `npm run build`
- Publish directory: `dist`
- SPA redirect rules (all routes → index.html)
- Security headers
- Cache control headers

---

## Deploy to Vercel

### Option 1: Deploy via Vercel Dashboard (Recommended)

1. **Sign up/Login to Vercel**
   - Go to [vercel.com](https://www.vercel.com)
   - Sign up or log in with GitHub

2. **Create New Project**
   - Click "Add New..." → "Project"
   - Import your repository: `DatabaseIntelligenceAi`
   - Select branch: `main`

3. **Configure Project Settings**
   - **Framework Preset**: Vite
   - **Root Directory**: `frontend` (if your repo has both frontend and backend)
   - **Build Command**: `npm run build` (auto-detected)
   - **Output Directory**: `dist` (auto-detected)
   - **Install Command**: `npm install` (auto-detected)

4. **Set Environment Variables**
   - Go to Project settings → Environment Variables
   - Add the following:
     ```
     VITE_API_BASE_URL = https://your-backend.onrender.com/api
     VITE_WS_URL = https://your-backend.onrender.com/api/ws
     ```
   - Replace `your-backend.onrender.com` with your actual backend URL
   - Make sure to add them for **Production**, **Preview**, and **Development** environments

5. **Deploy**
   - Click "Deploy"
   - Wait for build to complete (usually 1-2 minutes)
   - Your site will be live at `https://your-project.vercel.app`

6. **Custom Domain (Optional)**
   - Go to Project settings → Domains
   - Add your custom domain
   - Follow DNS configuration instructions

### Option 2: Deploy via Vercel CLI

```bash
# Install Vercel CLI
npm install -g vercel

# Navigate to frontend directory
cd frontend

# Login and deploy
vercel login
vercel --prod
```

### Vercel Configuration

The `vercel.json` file is already configured with:
- Build settings
- SPA rewrite rules
- Security headers
- Cache control headers

---

## Post-Deployment Verification

After deployment, verify everything works:

### 1. **Check Frontend Loads**
   - Open your deployed URL
   - Check browser console for errors
   - Verify the UI loads correctly

### 2. **Test API Connection**
   - Try to load databases (should see database list)
   - Check browser Network tab for API calls
   - Verify API calls go to correct backend URL

### 3. **Test WebSocket Connection**
   - Open browser DevTools → Console
   - You should see: `✅ WebSocket connected`
   - Try submitting a NL to SQL query
   - Verify real-time updates work

### 4. **Test All Features**
   - ✅ Database selection
   - ✅ NL to SQL conversion
   - ✅ Query execution
   - ✅ Results display
   - ✅ Charts and visualization
   - ✅ Export functionality

### 5. **Test SPA Routing**
   - Navigate to different pages
   - Refresh the page (should not show 404)
   - Direct URL access should work

### 6. **Update Backend CORS**
   - Add your frontend URL to backend CORS allowed origins
   - Example: `https://your-frontend.netlify.app`
   - Redeploy backend if needed

---

## Troubleshooting

### Issue: Frontend shows "Network Error" or "Failed to fetch"

**Solution:**
- Check `VITE_API_BASE_URL` is set correctly in deployment platform
- Verify backend is accessible (try opening backend URL in browser)
- Check backend CORS settings include your frontend URL
- Verify backend is running and healthy

### Issue: WebSocket connection fails

**Solution:**
- Check `VITE_WS_URL` is set correctly
- Ensure WebSocket URL uses `https://` (not `http://`) for production
- Verify backend WebSocket endpoint is accessible
- Check browser console for WebSocket errors

### Issue: 404 errors when refreshing page

**Solution:**
- Verify SPA redirect rules are configured (should be in `netlify.toml` or `vercel.json`)
- For Netlify: Check redirect rule `/* → /index.html` exists
- For Vercel: Check rewrite rule in `vercel.json`

### Issue: Environment variables not working

**Solution:**
- Ensure variables are prefixed with `VITE_`
- Rebuild the application after changing environment variables
- Check deployment logs for build errors
- Verify variables are set in correct environment (Production/Preview/Development)

### Issue: Build fails

**Solution:**
- Check build logs for specific errors
- Verify `package.json` has correct build script
- Ensure all dependencies are installed
- Check Node.js version (should be 18+)

### Issue: Assets not loading (404 for CSS/JS files)

**Solution:**
- Check `vite.config.ts` base path configuration
- Verify build output directory is `dist`
- Check asset paths in `index.html`
- Clear browser cache and try again

### Issue: CORS errors in browser console

**Solution:**
- Add frontend URL to backend CORS allowed origins
- Update backend `application-production.properties`:
  ```properties
  cors.allowed-origins=https://your-frontend.netlify.app,https://your-frontend.vercel.app
  ```
- Redeploy backend
- Clear browser cache

---

## Updating Deployment

### After Code Changes

1. **Push to GitHub**
   ```bash
   git add .
   git commit -m "Update frontend"
   git push origin main
   ```

2. **Automatic Deployment**
   - Netlify/Vercel will automatically detect changes
   - New deployment will start automatically
   - Wait for build to complete

3. **Manual Deployment (if needed)**
   - Netlify: Go to Deploys → Trigger deploy
   - Vercel: Go to Deployments → Redeploy

### After Environment Variable Changes

1. **Update in Platform Dashboard**
   - Netlify: Site settings → Environment variables
   - Vercel: Project settings → Environment variables

2. **Redeploy**
   - Trigger a new deployment
   - Environment variables are embedded at build time, so rebuild is required

---

## Production Checklist

Before going live, ensure:

- ✅ All environment variables are set correctly
- ✅ Backend is deployed and accessible
- ✅ Backend CORS includes frontend URL
- ✅ WebSocket connection works
- ✅ All features tested and working
- ✅ SPA routing works (no 404s on refresh)
- ✅ HTTPS enabled (automatic on Netlify/Vercel)
- ✅ Custom domain configured (optional)
- ✅ Error handling works correctly
- ✅ Loading states display properly

---

## Next Steps

After successful deployment:

1. **Share your application** with the deployed URL
2. **Monitor performance** using platform analytics
3. **Set up custom domain** (optional)
4. **Configure CI/CD** for automatic deployments
5. **Set up error tracking** (e.g., Sentry)
6. **Optimize performance** (lazy loading, code splitting)

---

**Congratulations! Your frontend is now live! 🎉**

