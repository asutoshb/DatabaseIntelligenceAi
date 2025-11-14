# Chunk 14: Frontend Deployment - Testing Guide

## 🧪 Testing Checklist

Use this guide to verify your frontend deployment is working correctly.

---

## Pre-Deployment Testing

### 1. Local Production Build Test

**Purpose**: Verify the production build works locally before deploying.

```bash
cd frontend

# Create production environment variables
echo "VITE_API_BASE_URL=http://localhost:8080/api" > .env.local
echo "VITE_WS_URL=http://localhost:8080/api/ws" >> .env.local

# Build for production
npm run build

# Preview production build
npm run preview
```

**Expected Result**:
- ✅ Build completes without errors
- ✅ Preview server starts on port 4173 (or similar)
- ✅ App loads in browser
- ✅ No console errors

**If Build Fails**:
- Check for TypeScript errors: `npm run build` shows errors
- Check for missing dependencies: `npm install`
- Check for syntax errors in code

---

### 2. Environment Variables Test

**Purpose**: Verify environment variables are accessible in code.

**Test in Browser Console** (after `npm run preview`):

```javascript
// Check if environment variables are accessible
console.log(import.meta.env.VITE_API_BASE_URL);
console.log(import.meta.env.VITE_WS_URL);
```

**Expected Result**:
- ✅ Variables are defined and have correct values
- ✅ Variables are not `undefined`

**If Variables are Undefined**:
- Check variable names (must start with `VITE_`)
- Rebuild after changing `.env.local`
- Check `.env.local` file exists and has correct format

---

### 3. Bundle Size Check

**Purpose**: Verify bundle size is reasonable (not too large).

```bash
cd frontend
npm run build

# Check dist folder size
du -sh dist/
```

**Expected Result**:
- ✅ Total size: 1-5 MB (uncompressed)
- ✅ Main bundle: 100-500 KB (gzipped)
- ✅ No unusually large files

**If Bundle is Too Large**:
- Check for large dependencies
- Consider code splitting
- Check for duplicate dependencies

---

## Post-Deployment Testing

### 4. Frontend Loads Test

**Purpose**: Verify the deployed frontend loads correctly.

**Steps**:
1. Open deployed URL in browser
2. Check browser console (F12 → Console)
3. Check Network tab

**Expected Result**:
- ✅ Page loads without errors
- ✅ No 404 errors in Network tab
- ✅ No CORS errors in console
- ✅ UI renders correctly

**If Page Doesn't Load**:
- Check deployment logs for build errors
- Verify build command is correct
- Check publish directory is `dist`
- Verify environment variables are set

---

### 5. API Connection Test

**Purpose**: Verify frontend can connect to backend API.

**Steps**:
1. Open deployed frontend
2. Open browser DevTools → Network tab
3. Try to load databases (should trigger API call)
4. Check API request URL

**Expected Result**:
- ✅ API calls go to correct backend URL
- ✅ API calls succeed (200 status)
- ✅ Data loads correctly (databases list appears)

**If API Calls Fail**:
- Check `VITE_API_BASE_URL` is set correctly
- Verify backend is accessible (try opening backend URL)
- Check backend CORS includes frontend URL
- Check Network tab for error details

---

### 6. WebSocket Connection Test

**Purpose**: Verify WebSocket connection works in production.

**Steps**:
1. Open deployed frontend
2. Open browser DevTools → Console
3. Look for WebSocket connection message
4. Try submitting a NL to SQL query

**Expected Result**:
- ✅ Console shows: `✅ WebSocket connected`
- ✅ Real-time updates work (progress indicators)
- ✅ No WebSocket errors in console

**If WebSocket Fails**:
- Check `VITE_WS_URL` is set correctly
- Verify WebSocket URL uses `https://` (not `http://`)
- Check backend WebSocket endpoint is accessible
- Check browser console for WebSocket errors

---

### 7. SPA Routing Test

**Purpose**: Verify client-side routing works (no 404s on refresh).

**Steps**:
1. Navigate to different pages (e.g., `/nl-to-sql`)
2. Refresh the page (F5)
3. Try direct URL access (paste URL in new tab)

**Expected Result**:
- ✅ Page loads correctly after refresh
- ✅ Direct URL access works
- ✅ No 404 errors

**If Routing Fails**:
- Check redirect/rewrite rules in `netlify.toml` or `vercel.json`
- Verify all routes redirect to `/index.html`
- Check deployment platform configuration

---

### 8. Feature Testing

**Purpose**: Verify all features work in production.

#### 8.1 Database Selection
- ✅ Database dropdown loads databases
- ✅ Can select a database
- ✅ Selected database is remembered

#### 8.2 NL to SQL Conversion
- ✅ Can enter natural language query
- ✅ Can submit query
- ✅ See real-time progress updates
- ✅ Generated SQL appears
- ✅ Validation status shows correctly

#### 8.3 Query Execution
- ✅ Can execute generated SQL
- ✅ See execution progress
- ✅ Results table displays
- ✅ Can see row count

#### 8.4 Data Visualization
- ✅ Charts render correctly
- ✅ Can switch between table/chart/both views
- ✅ Chart type auto-detected correctly

#### 8.5 Export Functionality
- ✅ Can export to CSV
- ✅ Can export to JSON
- ✅ Files download correctly

#### 8.6 Query History
- ✅ Query history loads
- ✅ Can click to reuse previous query
- ✅ History persists

---

### 9. CORS Test

**Purpose**: Verify CORS is configured correctly.

**Steps**:
1. Open deployed frontend
2. Open browser DevTools → Console
3. Try to make an API call
4. Check for CORS errors

**Expected Result**:
- ✅ No CORS errors in console
- ✅ API calls succeed
- ✅ Preflight requests (OPTIONS) succeed

**If CORS Errors**:
- Add frontend URL to backend CORS allowed origins
- Update `application-production.properties`:
  ```properties
  cors.allowed-origins=https://your-frontend.netlify.app
  ```
- Redeploy backend
- Clear browser cache

---

### 10. Performance Test

**Purpose**: Verify page loads quickly.

**Steps**:
1. Open deployed frontend
2. Open browser DevTools → Network tab
3. Reload page (Ctrl+R / Cmd+R)
4. Check load times

**Expected Result**:
- ✅ Initial load: < 3 seconds
- ✅ Time to interactive: < 5 seconds
- ✅ Assets load quickly

**If Performance is Poor**:
- Check bundle size (should be < 1 MB gzipped)
- Check for large images (optimize if needed)
- Check for unnecessary dependencies
- Consider lazy loading

---

### 11. HTTPS Test

**Purpose**: Verify HTTPS is enabled.

**Steps**:
1. Check URL starts with `https://`
2. Check browser shows lock icon
3. Check certificate is valid

**Expected Result**:
- ✅ URL uses `https://`
- ✅ Lock icon shows in browser
- ✅ No certificate warnings

**If HTTPS Issues**:
- Netlify/Vercel automatically provides HTTPS
- Check custom domain DNS configuration
- Wait for SSL certificate provisioning (can take a few minutes)

---

### 12. Mobile Responsiveness Test

**Purpose**: Verify app works on mobile devices.

**Steps**:
1. Open deployed frontend on mobile device
2. Or use browser DevTools → Device toolbar
3. Test all features

**Expected Result**:
- ✅ UI is responsive
- ✅ All features work on mobile
- ✅ Touch interactions work
- ✅ Text is readable

**If Mobile Issues**:
- Check Material-UI responsive breakpoints
- Test on different screen sizes
- Check for overflow issues

---

## Common Issues & Solutions

### Issue: "Failed to fetch" or Network Error

**Solution**:
1. Check `VITE_API_BASE_URL` is set correctly
2. Verify backend is accessible
3. Check backend CORS settings
4. Check browser console for specific error

### Issue: WebSocket Connection Fails

**Solution**:
1. Check `VITE_WS_URL` is set correctly
2. Verify WebSocket URL uses `https://`
3. Check backend WebSocket endpoint
4. Check browser console for errors

### Issue: 404 on Page Refresh

**Solution**:
1. Check redirect/rewrite rules in config
2. Verify all routes → `/index.html`
3. Check deployment platform settings

### Issue: Environment Variables Not Working

**Solution**:
1. Verify variables start with `VITE_`
2. Rebuild after changing variables
3. Check deployment logs
4. Verify variables are set in platform dashboard

### Issue: CORS Errors

**Solution**:
1. Add frontend URL to backend CORS
2. Update backend configuration
3. Redeploy backend
4. Clear browser cache

---

## Testing Checklist Summary

### Pre-Deployment
- [ ] Local production build works
- [ ] Environment variables accessible
- [ ] Bundle size is reasonable

### Post-Deployment
- [ ] Frontend loads correctly
- [ ] API connection works
- [ ] WebSocket connection works
- [ ] SPA routing works
- [ ] All features work
- [ ] CORS configured correctly
- [ ] Performance is good
- [ ] HTTPS enabled
- [ ] Mobile responsive

---

## Next Steps

After all tests pass:

1. **Share your app** with the deployed URL
2. **Monitor performance** using platform analytics
3. **Set up custom domain** (optional)
4. **Configure error tracking** (optional)
5. **Optimize further** if needed

---

**Congratulations! Your frontend is deployed and tested! 🎉**

