# 🗄️ Add Database Created in pgAdmin4 to UI

## ⚠️ Important Understanding

**Two Different Things:**
1. **Database in pgAdmin4** = The actual PostgreSQL database you created (e.g., `my_database`)
2. **Database in UI** = Database connection info stored in backend's `database_info` table

The UI doesn't automatically discover databases. You need to **register** the database connection.

---

## ✅ Step-by-Step: Add Your pgAdmin4 Database

### Step 1: Get Database Connection Details

From pgAdmin4, you need:
1. **Host**: Where PostgreSQL is running
   - If local: `localhost` or `127.0.0.1`
   - If Render: `dpg-xxx.oregon-postgres.render.com`
2. **Port**: Usually `5432` for PostgreSQL
3. **Database Name**: The name you created in pgAdmin4
4. **Username**: Your PostgreSQL username
5. **Password**: Your PostgreSQL password (if required)

### Step 2: Add Database in Settings UI

1. **Go to Settings page:**
   - Open: `https://database-intelligence.netlify.app/settings`
   - Or click **"Settings"** in the sidebar

2. **Click "Add Database" button**

3. **Fill in the form:**
   - **Database Name**: Any friendly name (e.g., "My Local Database" or "pgAdmin Database")
   - **Database Type**: `PostgreSQL`
   - **Host**: 
     - If local: `localhost`
     - If Render: Your Render database host
   - **Port**: `5432` (default PostgreSQL port)
   - **Database Name**: The exact name you created in pgAdmin4
   - **Username**: Your PostgreSQL username
   - **Password**: Your PostgreSQL password (optional if local database doesn't require it)

4. **Click "Add"**

5. **Refresh the NL to SQL page** - Your database should appear in the dropdown!

---

## 🔍 How to Find Connection Details in pgAdmin4

### Option 1: Check Server Properties
1. In pgAdmin4, right-click on your **server** (e.g., "PostgreSQL 18")
2. Click **"Properties"**
3. Go to **"Connection"** tab
4. You'll see:
   - **Host name/address**: Your host
   - **Port**: Usually 5432
   - **Maintenance database**: Usually `postgres`
   - **Username**: Your username

### Option 2: Check Database Properties
1. In pgAdmin4, expand your server
2. Expand **"Databases"**
3. Right-click on your database
4. Click **"Properties"**
5. Check the **"Name"** - this is your database name

### Option 3: Check Connection String
1. In pgAdmin4, right-click on your server
2. Click **"Properties"** → **"Connection"** tab
3. You can see the connection details there

---

## 📝 Example: Adding Local Database

If you created a database called `my_database` in pgAdmin4 on your local machine:

**Settings Form:**
- **Database Name**: `My Local Database`
- **Database Type**: `PostgreSQL`
- **Host**: `localhost`
- **Port**: `5432`
- **Database Name**: `my_database` (exact name from pgAdmin4)
- **Username**: `postgres` (or your username)
- **Password**: Leave empty if local database doesn't require password

---

## 📝 Example: Adding Render Database

If you're using Render database:

**Settings Form:**
- **Database Name**: `Render Database`
- **Database Type**: `PostgreSQL`
- **Host**: `dpg-xxx.oregon-postgres.render.com` (from Render)
- **Port**: `5432`
- **Database Name**: `database_intelligence` (or your database name)
- **Username**: `database_intelligence_user` (from Render)
- **Password**: Get from Render → Database → Connect → Internal Database URL

---

## 🎯 After Adding Database

1. Go to: `https://database-intelligence.netlify.app/nl-to-sql`
2. Select your database from the dropdown
3. Enter a query: `SELECT * FROM customers;`
4. Click **"CONVERT TO SQL"** → **"EXECUTE QUERY"**
5. You should see results from your pgAdmin4 database! 🎉

---

## 🐛 Troubleshooting

### Database not showing in dropdown?
- Make sure you clicked **"Add"** button
- Check if there are any errors in browser console (F12)
- Refresh the page
- Check backend logs on Render for errors

### Can't connect to database?
- **Verify host/port**: Make sure PostgreSQL is running and accessible
- **Check username/password**: Make sure credentials are correct
- **Check database name**: Must match exactly what you see in pgAdmin4
- **For local databases**: Make sure PostgreSQL allows connections from your backend server

### Getting "database does not exist" error?
- Double-check the **Database Name** field - must match exactly
- In pgAdmin4, check the exact name (case-sensitive in PostgreSQL)
- Try listing databases: `SELECT datname FROM pg_database;`

### Getting "connection refused" error?
- **For local databases**: Backend on Render can't connect to `localhost` on your machine
- **Solution**: Use Render database or set up port forwarding/tunneling
- **Alternative**: Use the Render database you already have

---

## 💡 Important Notes

1. **Local vs Cloud**: 
   - If your database is on your local machine (`localhost`), the backend on Render **cannot** connect to it
   - You need to use a cloud database (like Render) or run the backend locally

2. **Database Name vs Connection Name**:
   - **Database Name** in Settings form = Friendly name (can be anything)
   - **Database Name** field = Actual PostgreSQL database name (must match pgAdmin4)

3. **Multiple Databases**:
   - You can add multiple database connections
   - Each one appears in the dropdown
   - Select which one to query

---

## ✅ Quick Checklist

- [ ] Database created in pgAdmin4
- [ ] Got connection details (host, port, database name, username, password)
- [ ] Added database in Settings page
- [ ] Database appears in dropdown on NL to SQL page
- [ ] Can execute queries successfully

---

## 🎯 Next Steps

Once your database is added:
1. Test with a simple query: `SELECT 1;`
2. Try querying your tables: `SELECT * FROM customers;`
3. Use natural language: "show me top 5 customers by revenue"

Your database from pgAdmin4 should now be accessible in the UI! 🚀

