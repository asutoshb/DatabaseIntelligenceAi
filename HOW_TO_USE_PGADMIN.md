# How to Use pgAdmin 4 - Step by Step Guide

## 🚀 Starting pgAdmin

1. **Open pgAdmin 4** from Applications (or Spotlight search)
2. It will open in your **default web browser**
3. You'll see the pgAdmin interface running at `http://127.0.0.1:5050` (or similar)

---

## 📝 First-Time Setup

### Step 1: Set Master Password
- When you first open pgAdmin, it will ask for a **master password**
- This protects your saved database passwords
- Enter any password you want (remember it!)
- Click **OK**

### Step 2: Add Server Connection

1. In the left sidebar, **right-click** on "Servers"
2. Select **"Register" → "Server..."**

3. **General Tab:**
   - Name: `Local DatabaseAI` (or any name you want)

4. **Connection Tab:**
   - **Host name/address:** `localhost`
   - **Port:** `5432`
   - **Maintenance database:** `database_intelligence`
   - **Username:** `asutoshbhere`
   - **Password:** (leave empty, or enter if you set one)

5. Click **"Save"**

---

## 👀 Viewing Your Data

### Step 1: Expand Server
- Click the **arrow** next to "Local DatabaseAI" to expand

### Step 2: Expand Databases
- Expand **"Databases"**
- You'll see `database_intelligence`

### Step 3: View Tables
- Expand `database_intelligence`
- Expand **"Schemas"**
- Expand **"public"**
- Expand **"Tables"**
- You'll see: `users` and `database_info`

### Step 4: View Data
- Right-click on **"users"** table
- Select **"View/Edit Data" → "All Rows"**
- Your data appears in a nice table! 📊

---

## 🔍 Running SQL Queries

### Option 1: Query Tool
1. Right-click on **"database_intelligence"**
2. Select **"Query Tool"**
3. Type SQL:
   ```sql
   SELECT * FROM users;
   ```
4. Click **▶ (Play)** button or press `F5`

### Option 2: SQL Editor
- Click **"Tools" → "Query Tool"** from menu

---

## 📋 Common Operations

### View Table Data
```
Right-click table → View/Edit Data → All Rows
```

### View Table Structure
```
Right-click table → Properties → Columns
```

### Run SQL Query
```
Right-click database → Query Tool → Type SQL → Execute
```

### Refresh Data
- Right-click → **Refresh**

---

## 🎯 Quick Test

1. **Create data via API:**
   ```bash
   curl -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{"username": "john", "email": "john@example.com"}'
   ```

2. **In pgAdmin:**
   - Refresh the `users` table
   - View data → See your new user!

---

## 🌐 Accessing pgAdmin Web Interface

pgAdmin runs a local web server. You can access it at:
- `http://127.0.0.1:5050`
- Or the URL shown when you open pgAdmin

**This is essentially "online" - it's a web interface running in your browser!**

---

## 💡 Tips

- **Auto-refresh:** Data doesn't auto-update, click refresh after API calls
- **Query Tool:** Use it for complex SQL queries
- **Export Data:** Right-click table → Export/Import
- **Filter Data:** In View Data, you can filter and search

---

## 🔧 Troubleshooting

### "Connection refused"
- Make sure PostgreSQL is running:
  ```bash
  brew services list | grep postgresql
  ```

### "Password required"
- If you set a password, enter it
- If not, leave password empty

### "pgAdmin won't open"
- Try launching from Applications folder directly
- Or run: `open -a "pgAdmin 4"`

---

**That's it! You now have a beautiful web-based UI to view your PostgreSQL data!** 🎉

