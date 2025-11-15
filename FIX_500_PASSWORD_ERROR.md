# 🔧 Fix: 500 Error - Password Authentication

## ✅ What I Fixed

1. **Added password field** to `DatabaseInfo` model
2. **Updated QueryExecutionService** to use password from database info
3. **Updated Settings page** to allow password input

## 📝 How to Fix Your Database Connection

### Step 1: Get Your Render Database Password

1. Go to **Render Dashboard**: https://dashboard.render.com
2. Open: `database-intelligence-db`
3. Click **"Connect"** button (top right)
4. Copy the **"Internal Database URL"**
   - Format: `postgresql://username:password@host:port/database_name`
   - Example: `postgresql://db_user:abc123xyz@dpg-xxx.oregon-postgres.render.com:5432/database_intelligence`
5. **Extract the password** from the connection string (the part after `:` and before `@`)

### Step 2: Update Your Database in Settings

1. Go to: `https://database-intelligence.netlify.app/settings`
2. Find your **"Render Database"** card
3. Click the **Edit** icon (pencil)
4. **Enter the password** in the "Password" field
5. Click **"Update"**

### Step 3: Test Again

1. Go to: `https://database-intelligence.netlify.app/nl-to-sql`
2. Select **"Render Database"**
3. Enter: **"show me top 5 customers by revenue"**
4. Click **"CONVERT TO SQL"** → **"EXECUTE QUERY"**
5. ✅ Should work now!

---

## ⚠️ Important Notes

- **Password is optional** - Only needed if your database requires authentication
- **Render databases require passwords** - You must get it from the connection string
- **Password is stored in database** - In production, this should be encrypted (future improvement)

---

## 🐛 If Still Getting Errors

### Check the SQL Query
The generated SQL might be querying the wrong table. If you see:
```sql
SELECT ... FROM orders ...
```
But you only have a `customers` table, try:
- **"show me top 5 customers by revenue"** (should use `customers` table)
- Or create an `orders` table if needed

### Check Backend Logs
1. Go to Render → Your backend service → **"Logs"** tab
2. Look for error messages
3. Common errors:
   - **"password authentication failed"** → Wrong password
   - **"Connection refused"** → Wrong host/port
   - **"database does not exist"** → Wrong database name

---

## 🔄 After Code Deploy

The code changes are pushed. Wait for:
1. **Render backend** to redeploy (1-2 minutes)
2. **Netlify frontend** to redeploy (1-2 minutes)

Then update your database with the password!

