# 🚀 Quick Start Guide - Chunk 1

## Step 1: Install Prerequisites

### Check if you have everything:

```bash
# Check Java (need 17+)
java -version

# Check Node.js (need 18+)
node -version

# Check npm (or yarn)
npm -version
# OR
yarn -version
```

**If missing:**
- Java: Download from https://adoptium.net/
- Node.js: Download from https://nodejs.org/ (includes npm)
- Yarn: Install with `npm install -g yarn` (if you prefer yarn)

---

## Step 2: Start Backend

```bash
# Navigate to backend folder
cd backend

# Use Maven wrapper (recommended)
./mvnw spring-boot:run

# OR if you have Maven installed
mvn spring-boot:run
```

**You should see:**
```
🚀 AI Database Intelligence Platform is running!
📍 API available at: http://localhost:8080/api
```

**Test it:**
```bash
# In another terminal
curl http://localhost:8080/api/health
```

Or open in browser: http://localhost:8080/api/health

---

## Step 3: Start Frontend

**Open a NEW terminal window/tab:**

```bash
# Navigate to frontend folder
cd frontend

# Install dependencies (first time only - takes 1-2 minutes)
# Using npm:
npm install
# OR using yarn:
yarn install

# Start development server
# Using npm:
npm run dev
# OR using yarn:
yarn dev
```

**You should see:**
```
  VITE v5.0.8  ready in 500 ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

**Open browser:**
```
http://localhost:3000
```

---

## Step 4: Test the Connection

1. **Backend running?** ✅ 
   - Check: http://localhost:8080/api/health
   
2. **Frontend running?** ✅
   - Check: http://localhost:3000
   
3. **Frontend connected to backend?** ✅
   - On frontend page, you should see "✅ Connected!"
   - If not, check backend is running

---

## 🐛 Troubleshooting

### Backend Issues

**Problem: Port 8080 already in use**
```bash
# Find what's using port 8080
lsof -i :8080

# Kill it
kill -9 <PID>

# Or change port in application.properties:
# server.port=8081
```

**Problem: Maven not found**
```bash
# Install Maven
# macOS:
brew install maven

# Or use Maven wrapper (already included)
./mvnw spring-boot:run
```

**Problem: Java version wrong**
```bash
# Check Java version (need 17+)
java -version

# If wrong version, install Java 17 from:
# https://adoptium.net/
```

---

### Frontend Issues

**Problem: npm/yarn install fails**
```bash
# If using npm:
npm cache clean --force
npm install

# If using yarn:
yarn cache clean
yarn install
```

**Problem: Port 3000 already in use**
```bash
# Vite will automatically use next available port (3001, 3002, etc.)
# Or specify port in vite.config.ts
```

**Problem: Can't connect to backend**
- ✅ Check backend is running on port 8080
- ✅ Check CORS config in backend
- ✅ Check browser console for errors
- ✅ Try refreshing frontend page

---

## ✅ Success Checklist

- [ ] Backend starts without errors
- [ ] http://localhost:8080/api/health returns JSON
- [ ] Frontend starts without errors
- [ ] http://localhost:3000 shows the app
- [ ] Frontend shows "✅ Connected!" when backend is running

---

## 📚 What to Read Next

1. **CHUNK_1_EXPLANATION.md** - Detailed explanation of tech stack
2. **PROJECT_PLAN.md** - Overview of all chunks
3. **backend/README.md** - Backend details
4. **frontend/README.md** - Frontend details

---

## 🎯 Next Steps

Once everything is running:
1. ✅ Play around with the UI
2. ✅ Check browser console (F12) - see network requests
3. ✅ Try the "Refresh Status" button
4. ✅ Ready for Chunk 2!

---

**Happy Coding! 🚀**

