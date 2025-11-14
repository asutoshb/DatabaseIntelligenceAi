# Chunk 14: Frontend Deployment

## Goal
Deploy the React frontend to a cloud platform (Netlify/Vercel) so it's accessible from anywhere via a public URL and connected to the deployed backend.

## What We'll Build

### 1. Netlify Deployment Configuration
- `netlify.toml` configuration file
- Build settings and redirects
- Environment variables setup
- Production build optimization

### 2. Vercel Deployment Configuration
- `vercel.json` configuration file
- Build and output settings
- Environment variables setup
- Rewrite rules for SPA routing

### 3. Environment Variables Configuration
- `.env.example` file for reference
- Documentation for required variables
- Production vs development differences
- API URL configuration

### 4. Production Build Optimization
- Verify Vite build configuration
- Optimize bundle size
- Configure asset optimization
- Ensure proper environment variable handling

### 5. CORS Configuration
- Ensure backend CORS allows frontend domain
- Handle preflight requests
- Configure for production domains

### 6. Deployment Documentation
- Step-by-step deployment instructions
- Platform-specific guides (Netlify, Vercel)
- Environment variable setup
- Troubleshooting guide

## Technical Implementation

### Files to Create/Modify

1. **`frontend/netlify.toml`** (NEW)
   - Netlify deployment configuration
   - Build command and publish directory
   - Redirect rules for SPA
   - Environment variables

2. **`frontend/vercel.json`** (NEW)
   - Vercel deployment configuration
   - Build settings
   - Rewrite rules for SPA routing
   - Headers configuration

3. **`frontend/.env.example`** (NEW)
   - Example environment variables
   - Documentation for each variable
   - Production vs development values

4. **`frontend/vite.config.ts`** (MODIFY if needed)
   - Ensure production build optimization
   - Configure base path if needed
   - Optimize asset handling

5. **`FRONTEND_DEPLOYMENT_GUIDE.md`** (NEW)
   - Step-by-step deployment instructions
   - Platform-specific guides (Netlify, Vercel)
   - Environment variable setup
   - Troubleshooting guide

## Implementation Steps

### Step 1: Create Netlify Configuration
- Define build command (`npm run build`)
- Set publish directory (`dist`)
- Configure redirects for SPA routing
- Set up environment variables

### Step 2: Create Vercel Configuration
- Define build command
- Set output directory
- Configure rewrites for SPA
- Set up headers

### Step 3: Create Environment Variables Template
- Document `VITE_API_BASE_URL`
- Document `VITE_WS_URL`
- Provide example values
- Explain production vs development

### Step 4: Verify Build Configuration
- Test production build locally
- Verify environment variables work
- Check bundle size
- Ensure all assets load correctly

### Step 5: Create Deployment Documentation
- Step-by-step guides for each platform
- Environment variable setup instructions
- Troubleshooting tips
- Post-deployment verification steps

## Deployment Platforms

### Netlify
- **Pros**: Free tier, easy setup, automatic deployments, great for SPAs
- **Cons**: Free tier has limitations
- **Best For**: Quick deployments, static sites, SPAs

### Vercel
- **Pros**: Free tier, excellent performance, automatic deployments, great DX
- **Cons**: Free tier has limitations
- **Best For**: React apps, Next.js, modern frontends

### GitHub Pages (Alternative)
- **Pros**: Free, simple
- **Cons**: Limited features, no server-side features
- **Best For**: Simple static sites

## Environment Variables Required

### Required for Production
- `VITE_API_BASE_URL` - Backend API URL (e.g., `https://your-backend.onrender.com/api`)
- `VITE_WS_URL` - WebSocket URL (e.g., `https://your-backend.onrender.com/api/ws`)

### Optional
- `VITE_APP_NAME` - Application name
- `VITE_APP_VERSION` - Application version

## Success Criteria

✅ Frontend deployed to cloud platform
✅ Accessible via public URL
✅ Connected to deployed backend
✅ Environment variables properly configured
✅ All features working (NL to SQL, query execution, charts)
✅ WebSocket connection working
✅ CORS configured correctly
✅ SPA routing working (no 404s on refresh)

## Tech Stack to Learn

- **Netlify/Vercel**: Frontend deployment platforms
- **Environment Variables**: Production configuration
- **Vite Build**: Production optimization
- **CORS**: Cross-origin resource sharing
- **SPA Routing**: Single-page application routing

## Deployment Flow

1. **Prepare Application**
   - Ensure all API URLs use environment variables
   - Test production build locally
   - Verify environment variables work
   - Check bundle size and optimization

2. **Choose Platform**
   - Select Netlify or Vercel
   - Create account if needed

3. **Deploy**
   - Connect GitHub repository
   - Configure build settings
   - Set environment variables
   - Deploy

4. **Verify**
   - Check frontend loads correctly
   - Test API connection
   - Verify WebSocket connection
   - Test all features
   - Check SPA routing

5. **Update Backend CORS**
   - Add frontend URL to backend CORS allowed origins
   - Redeploy backend if needed
   - Test end-to-end

---

**Estimated Time**: 1-2 hours
**Difficulty**: Easy
**Prerequisites**: Chunk 13 (Backend Deployment) ✅

