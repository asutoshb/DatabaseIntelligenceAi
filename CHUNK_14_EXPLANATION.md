# Chunk 14: Frontend Deployment - Technical Explanation

## Overview

This document explains the technical concepts and implementation details for deploying the React frontend to cloud platforms (Netlify/Vercel).

---

## 1. Frontend Deployment Concepts

### What is Frontend Deployment?

Frontend deployment means making your React application accessible on the internet via a public URL. Unlike backend deployment, frontend deployment is simpler because:

- **Static Files**: React apps are built into static HTML, CSS, and JavaScript files
- **No Server Required**: Static files can be served by CDN (Content Delivery Network)
- **Fast & Cheap**: CDNs are fast, scalable, and often free

### Build Process

When you run `npm run build`, Vite:

1. **Compiles TypeScript** → JavaScript
2. **Bundles Code** → Optimized JavaScript bundles
3. **Processes Assets** → Optimized images, CSS
4. **Generates HTML** → `index.html` with script tags
5. **Outputs to `dist/`** → Static files ready for deployment

### Environment Variables in Vite

Vite uses a special prefix for environment variables: `VITE_`

```env
# ✅ Works - prefixed with VITE_
VITE_API_BASE_URL=https://api.example.com

# ❌ Doesn't work - not prefixed
API_BASE_URL=https://api.example.com
```

**Why?** Security! Only variables prefixed with `VITE_` are exposed to the browser. This prevents accidentally exposing secrets.

**Important**: Environment variables are **embedded at build time**, not runtime. This means:
- You must rebuild after changing environment variables
- Different builds can have different environment variables
- Production builds use production environment variables

---

## 2. Netlify Deployment

### What is Netlify?

Netlify is a platform for deploying static websites and SPAs (Single Page Applications). It provides:
- **CDN**: Fast global content delivery
- **Automatic Deployments**: Deploys on every Git push
- **HTTPS**: Free SSL certificates
- **Custom Domains**: Easy domain configuration

### Netlify Configuration (`netlify.toml`)

```toml
[build]
  command = "npm run build"    # Command to build the app
  publish = "dist"              # Directory with built files

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

**Why the redirect?** React Router handles routing client-side. When you visit `/nl-to-sql` directly, the server doesn't have that file. The redirect tells Netlify: "For any route, serve `index.html` and let React Router handle it."

### Build Process on Netlify

1. **Git Push** → Netlify detects changes
2. **Install Dependencies** → `npm install`
3. **Build** → `npm run build` (uses environment variables)
4. **Deploy** → Upload `dist/` files to CDN
5. **Live** → Available at `https://your-app.netlify.app`

---

## 3. Vercel Deployment

### What is Vercel?

Vercel is a platform optimized for frontend frameworks (React, Next.js, etc.). It provides:
- **Edge Network**: Ultra-fast global CDN
- **Automatic Deployments**: Deploys on every Git push
- **Preview Deployments**: Deploy previews for every PR
- **HTTPS**: Free SSL certificates

### Vercel Configuration (`vercel.json`)

```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

**Rewrites vs Redirects**: Vercel uses "rewrites" (internal routing) instead of "redirects" (HTTP redirects). Both achieve the same result for SPAs.

### Build Process on Vercel

1. **Git Push** → Vercel detects changes
2. **Install Dependencies** → `npm install`
3. **Build** → `npm run build` (uses environment variables)
4. **Deploy to Edge** → Upload to global CDN
5. **Live** → Available at `https://your-app.vercel.app`

---

## 4. Environment Variables

### How They Work

**Development** (`.env.local`):
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=http://localhost:8080/api/ws
```

**Production** (Set in platform dashboard):
```env
VITE_API_BASE_URL=https://your-backend.onrender.com/api
VITE_WS_URL=https://your-backend.onrender.com/api/ws
```

### Accessing in Code

```typescript
// In api.ts
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// In useWebSocket.ts
const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8080/api/ws';
```

`import.meta.env` is Vite's way of accessing environment variables. It's replaced at build time with actual values.

### Why Different URLs?

- **Development**: `http://localhost:8080` (local backend)
- **Production**: `https://your-backend.onrender.com` (deployed backend)

**Important**: Use `https://` for production (not `http://`) because:
- Browsers block mixed content (HTTP on HTTPS page)
- Most deployment platforms use HTTPS

---

## 5. Single Page Application (SPA) Routing

### The Problem

When you visit `https://your-app.netlify.app/nl-to-sql`:
- Browser requests `/nl-to-sql` from server
- Server doesn't have `/nl-to-sql/index.html`
- Server returns 404

### The Solution

**Redirect/Rewrite Rule**: All routes → `/index.html`

```
User visits: /nl-to-sql
Server serves: /index.html (React app)
React Router sees: /nl-to-sql (in URL)
React Router renders: NLToSQLPage component
```

### How It Works

1. **Initial Load**: Browser requests `/nl-to-sql`
2. **Server Redirect**: Server serves `/index.html` (React app)
3. **React Router**: Reads URL (`/nl-to-sql`) and renders correct component
4. **Navigation**: Future navigation is client-side (no server requests)

---

## 6. CORS (Cross-Origin Resource Sharing)

### What is CORS?

CORS is a browser security feature that controls which websites can make requests to your backend.

### The Problem

- Frontend: `https://your-frontend.netlify.app`
- Backend: `https://your-backend.onrender.com`
- **Different origins** → Browser blocks requests by default

### The Solution

Backend must allow frontend origin:

```properties
# application-production.properties
cors.allowed-origins=https://your-frontend.netlify.app
```

### How It Works

1. **Browser sends preflight request** (OPTIONS) to backend
2. **Backend responds** with allowed origins
3. **Browser checks** if frontend origin is allowed
4. **If allowed**, browser sends actual request
5. **If not allowed**, browser blocks request (CORS error)

---

## 7. WebSocket in Production

### The Challenge

WebSocket connections need special handling in production:

1. **Protocol**: Must match page protocol (HTTPS → WSS)
2. **URL**: Must point to deployed backend
3. **CORS**: Backend must allow WebSocket connections from frontend

### Configuration

```typescript
// Development
const wsUrl = 'http://localhost:8080/api/ws';

// Production
const wsUrl = 'https://your-backend.onrender.com/api/ws';
```

**Note**: SockJS automatically upgrades HTTP to WebSocket, so you use `https://` (not `wss://`).

---

## 8. Build Optimization

### What Vite Does

When you run `npm run build`, Vite:

1. **Code Splitting**: Splits code into smaller chunks
2. **Tree Shaking**: Removes unused code
3. **Minification**: Compresses JavaScript/CSS
4. **Asset Optimization**: Optimizes images
5. **Source Maps**: Generates source maps for debugging

### Bundle Size

Typical React app bundle sizes:
- **Initial bundle**: 100-300 KB (gzipped)
- **Vendor chunks**: 200-500 KB (gzipped)
- **Total**: 300-800 KB (gzipped)

**Why it matters**: Smaller bundles = faster page loads

---

## 9. Security Headers

### Why They Matter

Security headers protect your app from common attacks:
- **XSS Protection**: Prevents cross-site scripting
- **Frame Options**: Prevents clickjacking
- **Content Type**: Prevents MIME type sniffing

### Configuration

**Netlify** (`netlify.toml`):
```toml
[[headers]]
  for = "/*"
  [headers.values]
    X-Frame-Options = "DENY"
    X-Content-Type-Options = "nosniff"
```

**Vercel** (`vercel.json`):
```json
{
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        {
          "key": "X-Frame-Options",
          "value": "DENY"
        }
      ]
    }
  ]
}
```

---

## 10. Cache Control

### Static Assets

Cache static assets (JS, CSS, images) for a long time:

```toml
# Netlify
Cache-Control = "public, max-age=31536000, immutable"
```

**Why?** Assets have hashed filenames (`index-abc123.js`). If the file changes, the hash changes, so it's safe to cache forever.

### HTML

Don't cache HTML (always get latest version):

```toml
# Netlify
Cache-Control = "public, max-age=0, must-revalidate"
```

**Why?** HTML might change (new features, bug fixes). Always fetch the latest version.

---

## Summary

**Frontend Deployment** is about:
1. **Building** static files from React code
2. **Configuring** environment variables
3. **Deploying** to CDN (Netlify/Vercel)
4. **Configuring** SPA routing
5. **Setting up** CORS with backend
6. **Testing** everything works

**Key Concepts**:
- Environment variables are embedded at build time
- SPAs need redirect/rewrite rules for routing
- CORS must be configured on backend
- WebSocket URLs must match backend protocol
- Security headers protect your app

**Result**: Your frontend is accessible worldwide via a public URL! 🌍

