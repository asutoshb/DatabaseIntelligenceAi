# 🗄️ Add Database and Sample Tables

## Step 1: Get Render Database Connection Details

1. Go to **Render Dashboard**: https://dashboard.render.com
2. Open your database: `database-intelligence-db`
3. Click **"Connect"** button (top right)
4. Copy the **"Internal Database URL"** - it looks like:
   ```
   postgresql://username:password@hostname:port/database_name
   ```
5. Or get individual values:
   - **Host**: (from the connection string)
   - **Port**: Usually `5432` for PostgreSQL
   - **Database Name**: Usually the database name from the URL
   - **Username**: (from the connection string)
   - **Password**: (from the connection string)

---

## Step 2: Add Database via API

### Option A: Using Browser Console (Easiest)

1. Open your frontend: `https://database-intelligence.netlify.app`
2. Open **DevTools** (F12) → **Console** tab
3. Paste this code (replace with your Render database details):

```javascript
fetch('https://ai-database-intelligence-backend.onrender.com/api/databases', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    name: 'Render Database',
    databaseType: 'PostgreSQL',
    host: 'YOUR_HOST_HERE',  // e.g., 'dpg-d4blptripnbc73a6b7s0-a.oregon-postgres.render.com'
    port: 5432,
    databaseName: 'database_intelligence',  // or your actual database name
    username: 'YOUR_USERNAME_HERE'  // e.g., 'database_intelligence_db_user'
  })
})
.then(res => res.json())
.then(data => {
  console.log('✅ Database added!', data);
  alert('Database added successfully! Refresh the page.');
})
.catch(err => {
  console.error('❌ Error:', err);
  alert('Error: ' + err.message);
});
```

4. **Replace** `YOUR_HOST_HERE` and `YOUR_USERNAME_HERE` with your actual values
5. Press **Enter** to run
6. You should see: `✅ Database added!`
7. **Refresh** the page - the database should appear in the dropdown!

---

### Option B: Using curl (Terminal)

```bash
curl -X POST https://ai-database-intelligence-backend.onrender.com/api/databases \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Render Database",
    "databaseType": "PostgreSQL",
    "host": "YOUR_HOST_HERE",
    "port": 5432,
    "databaseName": "database_intelligence",
    "username": "YOUR_USERNAME_HERE"
  }'
```

---

## Step 3: Connect to Render Database and Create Sample Tables

1. Go to **Render Dashboard** → Your database → **"Connect"** button
2. Copy the **"psql"** command or use **"Connection Pooling"** URL
3. Connect using your preferred PostgreSQL client (psql, pgAdmin, DBeaver, etc.)

### Or use Render's Web Shell:

1. Go to **Render Dashboard** → Your database
2. Click **"Shell"** in the left sidebar
3. Run the SQL commands below

---

## Step 4: Create Customers Table with Sample Data

Run this SQL in your Render database:

```sql
-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    city VARCHAR(50),
    country VARCHAR(50),
    revenue DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO customers (name, email, city, country, revenue) VALUES
('John Smith', 'john.smith@email.com', 'New York', 'USA', 125000.00),
('Emma Johnson', 'emma.johnson@email.com', 'London', 'UK', 98000.50),
('Michael Chen', 'michael.chen@email.com', 'Tokyo', 'Japan', 156750.25),
('Sarah Williams', 'sarah.williams@email.com', 'Sydney', 'Australia', 112500.75),
('David Brown', 'david.brown@email.com', 'Toronto', 'Canada', 87500.00),
('Lisa Anderson', 'lisa.anderson@email.com', 'Berlin', 'Germany', 134200.50),
('Robert Taylor', 'robert.taylor@email.com', 'Paris', 'France', 98750.25),
('Jennifer Martinez', 'jennifer.martinez@email.com', 'Madrid', 'Spain', 145600.00),
('James Wilson', 'james.wilson@email.com', 'Mumbai', 'India', 76500.75),
('Maria Garcia', 'maria.garcia@email.com', 'São Paulo', 'Brazil', 118900.50);

-- Verify data
SELECT * FROM customers ORDER BY revenue DESC;
```

---

## Step 5: Test Your Query!

1. Go to your frontend: `https://database-intelligence.netlify.app/nl-to-sql`
2. Select **"Render Database"** from the dropdown
3. Enter: **"show me top 5 customers by revenue"**
4. Click **"CONVERT TO SQL"**
5. You should see SQL like:
   ```sql
   SELECT * FROM customers ORDER BY revenue DESC LIMIT 5;
   ```
6. Click **"EXECUTE QUERY"**
7. You should see the top 5 customers! 🎉

---

## 🎯 Quick Test Queries

Try these natural language queries:

- **"show me top 5 customers by revenue"**
- **"list all customers from USA"**
- **"what is the total revenue of all customers"**
- **"show me customers with revenue greater than 100000"**
- **"how many customers are there"**

---

## 🔧 Troubleshooting

### Database not showing in dropdown?
- Check browser console for errors
- Verify the API call succeeded (check Network tab in DevTools)
- Refresh the page

### Can't connect to database?
- Verify host, port, username are correct
- Check if database name exists
- Verify password is correct (if required)

### Query execution fails?
- Check if the table exists: `SELECT * FROM customers;`
- Verify table name matches (case-sensitive in PostgreSQL)
- Check backend logs on Render for detailed errors

