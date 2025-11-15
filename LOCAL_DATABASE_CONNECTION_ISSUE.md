# 🔧 Local Database Connection Issue

## ⚠️ The Problem

**Your backend is deployed on Render (cloud), but your database is on `localhost` (your local machine).**

- ✅ **Works locally**: When you run backend locally, it can connect to `localhost:5432`
- ❌ **Doesn't work on Render**: Render backend cannot reach `localhost` on your machine

**Why?**
- `localhost` on Render = Render's own server (not your computer)
- Your database is on your local machine, not accessible from the internet
- Render backend needs a publicly accessible database

---

## ✅ Solution 1: Use Render Database (Easiest)

You already have a Render database set up! Use that instead:

1. **Go to Settings**: `https://database-intelligence.netlify.app/settings`
2. **Check if "Render Database" exists** - If yes, use that
3. **If not, add it:**
   - Database Name: `Render Database`
   - Host: `dpg-d4blptripnbc73a6b7s0-a.oregon-postgres.render.com` (from Render)
   - Port: `5432`
   - Database Name: `database_intelligence`
   - Username: `database_intelligence_user`
   - Password: Get from Render → Database → Connect

4. **Copy your local database data to Render:**
   - Export from pgAdmin4 (local)
   - Import to Render database

---

## ✅ Solution 2: Run Backend Locally (For Development)

If you want to use your local database, run the backend locally:

### Step 1: Stop Using Render Backend
- Use local backend URL: `http://localhost:8080`

### Step 2: Update Frontend Environment
1. Create/update `.env` file in `frontend/`:
   ```env
   VITE_API_BASE_URL=http://localhost:8080/api
   VITE_WS_URL=ws://localhost:8080/api/ws
   ```

2. Restart frontend:
   ```bash
   cd frontend
   npm run dev
   ```

### Step 3: Run Backend Locally
```bash
cd backend
./mvnw spring-boot:run
```

### Step 4: Add Local Database in Settings
- Host: `localhost`
- Port: `5432`
- Database Name: Your local database name
- Username: `postgres` (or your username)
- Password: (if required)

Now it will work because backend and database are both local!

---

## ✅ Solution 3: Make Local Database Accessible (Advanced)

### Option A: Use ngrok (Tunnel)
1. Install ngrok: https://ngrok.com/
2. Create tunnel:
   ```bash
   ngrok tcp 5432
   ```
3. Use the ngrok URL as host in Settings
4. **Note**: This exposes your local database to the internet (security risk!)

### Option B: Use Cloud Database
- Use Render database (recommended)
- Or use another cloud PostgreSQL (AWS RDS, Google Cloud SQL, etc.)

---

## 🎯 Recommended Approach

**For Production/Deployment:**
- ✅ Use Render database (already set up)
- ✅ Backend on Render can connect to Render database
- ✅ Everything works in the cloud

**For Local Development:**
- ✅ Run backend locally (`./mvnw spring-boot:run`)
- ✅ Connect to local database (`localhost:5432`)
- ✅ Update frontend to use `http://localhost:8080`

---

## 📝 Quick Checklist

**If using Render database:**
- [ ] Database added in Settings with Render connection details
- [ ] Password set correctly
- [ ] Database appears in dropdown
- [ ] Queries work

**If using local database:**
- [ ] Backend running locally (`localhost:8080`)
- [ ] Frontend configured to use local backend
- [ ] Database added in Settings with `localhost` as host
- [ ] PostgreSQL running and accessible

---

## 🔍 How to Check What's Happening

### Check Backend Logs on Render:
1. Render Dashboard → Backend service → Logs
2. Look for connection errors:
   - `Connection refused` = Can't reach database
   - `password authentication failed` = Wrong password
   - `database does not exist` = Wrong database name

### Test Connection:
1. Try a simple query: `SELECT 1;`
2. Check error message
3. If it says "Connection refused" → Database not accessible from Render

---

## 💡 Summary

**The Issue:**
- Backend on Render (cloud) cannot connect to `localhost` (your machine)
- `localhost` on Render = Render's server, not your computer

**Solutions:**
1. **Best**: Use Render database (cloud-to-cloud connection)
2. **For dev**: Run backend locally to connect to local database
3. **Advanced**: Use tunnel (not recommended for production)

**Which one do you want to use?** I can help you set it up! 🚀

