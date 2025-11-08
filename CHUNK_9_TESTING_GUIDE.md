# Chunk 9: Frontend - React Setup & UI Components - Testing Guide

## 🚀 Quick Start

### 1. Start Backend
```bash
cd backend
./mvnw spring-boot:run
```

Wait for: `Started DatabaseAiApplication in X.XXX seconds`

### 2. Start Frontend
```bash
cd frontend
npm run dev
```

You should see:
```
  VITE v5.x.x  ready in XXX ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

### 3. Open Browser
Navigate to `http://localhost:5173` (or the port shown in terminal)

---

## ✅ What to Test

### 1. **Layout & Navigation**
- [ ] Header shows "🚀 AI Database Intelligence" logo
- [ ] Sidebar shows menu items: Home, NL to SQL, History, Settings
- [ ] Clicking logo navigates to home page
- [ ] Clicking sidebar items navigates to different pages
- [ ] Active page is highlighted in sidebar

### 2. **Home Page**
- [ ] Shows "Welcome to AI Database Intelligence Platform"
- [ ] Backend Status card shows connection status
- [ ] Click "Refresh Status" button - should update timestamp
- [ ] Quick action cards are visible (NL to SQL, Query History, Settings)
- [ ] Clicking "Get Started" on NL to SQL card navigates to `/nl-to-sql`

### 3. **Responsive Design**
- [ ] Resize browser window to mobile size (< 600px)
- [ ] Header shows hamburger menu button (☰)
- [ ] Clicking menu button opens/closes sidebar
- [ ] Sidebar closes after selecting a menu item (on mobile)

### 4. **WebSocket Connection**
- [ ] **Visual Check (Easiest):** Look at the "WebSocket Status" card on the HomePage
  - Should show green "Connected" chip
  - Should display: "✅ Real-time updates are active"
- [ ] **Console Check (Optional):**
  - Open browser DevTools (F12) **BEFORE** refreshing the page
  - Go to Console tab
  - Refresh the page (F5)
  - Look for: `[HH:MM:SS] ✅ WebSocket connected`
  - Should see: `✅ Subscribed to /topic/nl-to-sql`
  - Should see: `✅ Subscribed to /topic/query-execution`
  
**💡 Tip:** If you open console AFTER page loads, you'll miss the initial connection messages. Open DevTools first, then refresh!

### 5. **API Integration**
- [ ] Home page should show backend status
- [ ] If backend is running: Status = "UP", Message = "Backend is healthy"
- [ ] If backend is stopped: Status = "❌ Backend not connected"

### 6. **Page Navigation**
- [ ] Navigate to `/nl-to-sql` - shows placeholder page
- [ ] Navigate to `/history` - shows placeholder page
- [ ] Navigate to `/settings` - shows placeholder page
- [ ] Navigate back to `/` - shows home page
- [ ] Browser back/forward buttons work correctly

---

## 🔍 Browser Console Checks

### How to See Console Messages:
1. **Open DevTools FIRST** (F12) before refreshing
2. Go to Console tab
3. Refresh the page (F5)
4. You should see connection messages with timestamps

### Expected Console Messages:
```
[10:30:45 AM] 🔌 Attempting to connect to WebSocket: ws://localhost:8080/ws/websocket
[10:30:45 AM] ✅ WebSocket connected - You can see this in the UI too!
[10:30:45 AM] 📡 Subscribing to topics: /topic/nl-to-sql, /topic/query-execution
✅ Subscribed to /topic/nl-to-sql
✅ Subscribed to /topic/query-execution
[10:30:46 AM] 📊 NL to SQL Update: {...}  (when backend sends updates)
[10:30:46 AM] ⚡ Query Execution Update: {...}  (when backend sends updates)
```

**Note:** All messages now have timestamps `[HH:MM:SS]` for easier tracking!

### No Errors Should Appear:
- ❌ No TypeScript errors
- ❌ No React errors
- ❌ No WebSocket connection errors (if backend is running)

---

## 🐛 Troubleshooting

### Frontend won't start
- **Error:** `Port 5173 already in use`
  - **Solution:** Kill process using port or change port in `vite.config.ts`

### Backend Status shows "not connected"
- **Check:** Is backend running on `http://localhost:8080`?
- **Check:** Is CORS enabled in backend? (Should be `@CrossOrigin(origins = "*")`)

### WebSocket not connecting
- **Check:** Look at "WebSocket Status" card on HomePage - shows connection status visually
- **Check:** Backend WebSocket endpoint: `ws://localhost:8080/ws/websocket`
- **Check:** Browser console for connection errors (open DevTools BEFORE refreshing)
- **Check:** Backend logs for WebSocket handshake errors
- **Note:** If console is opened AFTER page loads, you'll miss connection messages. Open DevTools first!

### Console messages not showing
- **Solution:** Open DevTools (F12) **BEFORE** refreshing the page
- **Why:** WebSocket connects immediately on page load, so messages appear before console opens
- **Alternative:** Check the "WebSocket Status" card on HomePage - it shows connection status visually

### TypeScript errors
- **Run:** `npm run build` to see all errors
- **Fix:** All errors should be resolved (build should succeed)

---

## 📸 Visual Checklist

### Desktop View (> 600px)
- [ ] Sidebar always visible on left (240px wide)
- [ ] Header spans full width at top
- [ ] Main content area on right side
- [ ] Proper spacing and padding

### Mobile View (< 600px)
- [ ] Sidebar hidden by default
- [ ] Hamburger menu button visible in header
- [ ] Sidebar slides in from left when menu clicked
- [ ] Sidebar closes when clicking outside or selecting item

---

## ✅ Success Criteria

Chunk 9 is complete when:
- ✅ All pages load without errors
- ✅ Navigation works smoothly
- ✅ Backend status check works
- ✅ WebSocket connects successfully
- ✅ Responsive design works on mobile
- ✅ No console errors
- ✅ Build succeeds (`npm run build`)

---

## 🎯 Next Steps

Once Chunk 9 is tested and working:
- **Chunk 10:** Build the actual NL to SQL interface
- Add form for natural language input
- Add database selection dropdown
- Display real-time progress updates
- Show generated SQL preview

---

**Happy Testing! 🚀**

