# Troubleshooting: Database Connection Error

## Error Message
```
Database connection failed: The database user 'postgres' does not exist. 
Please check your database configuration.
```

## Step-by-Step Fix

### Step 1: Check Your PostgreSQL Users

First, let's see what users exist in your PostgreSQL database:

```bash
# Connect to PostgreSQL (use your actual PostgreSQL username)
psql -U your_username -d postgres

# Or if you're not sure of your username, try:
psql -d postgres
```

Once connected, run:
```sql
\du
```

This will list all database users/roles. Look for:
- `postgres` (the default superuser)
- Your macOS username (often used as a PostgreSQL user)
- Any other custom users

**Note down which users exist!**

---

### Step 2: Check Your DatabaseInfo Record

You need to see what username is stored in your `DatabaseInfo` table:

**Option A: Using psql**
```bash
# Connect to your application database (not the target database)
psql -U your_username -d databaseai  # or whatever your app database is called

# Check the database_info table
SELECT id, name, host, port, database_name, username, database_type 
FROM database_info;
```

**Option B: Using API (if available)**
```bash
curl http://localhost:8080/api/databases
```

Look at the `username` field - this is what's causing the error.

---

### Step 3: Fix the Issue

You have **3 options**:

#### Option A: Create the 'postgres' User (Recommended if you want to use 'postgres')

```bash
# Connect to PostgreSQL as a superuser
psql -U your_username -d postgres

# Create the postgres user
CREATE ROLE postgres WITH LOGIN PASSWORD 'your_secure_password';

# Grant necessary privileges
ALTER ROLE postgres CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE your_database_name TO postgres;
```

**Replace:**
- `your_secure_password` with a strong password
- `your_database_name` with your actual database name

#### Option B: Update DatabaseInfo to Use Existing User

If you have an existing PostgreSQL user (like your macOS username), update the `DatabaseInfo` record:

**Using psql:**
```sql
-- Connect to your application database
psql -U your_username -d databaseai

-- Update the username (replace 1 with your actual database_info id)
UPDATE database_info 
SET username = 'your_existing_username' 
WHERE id = 1;
```

**Using SQL directly:**
```sql
-- First, find your database_info id
SELECT id, name, username FROM database_info;

-- Then update it (replace 1 with the actual id, and 'your_username' with your PostgreSQL username)
UPDATE database_info 
SET username = 'your_username' 
WHERE id = 1;
```

#### Option C: Create a New User for This Application

```bash
# Connect to PostgreSQL
psql -U your_username -d postgres

# Create a new user for the application
CREATE ROLE dbai_user WITH LOGIN PASSWORD 'secure_password';
ALTER ROLE dbai_user CREATEDB;

# Grant access to your target database
GRANT CONNECT ON DATABASE your_database_name TO dbai_user;
GRANT USAGE ON SCHEMA public TO dbai_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO dbai_user;
```

Then update `DatabaseInfo`:
```sql
UPDATE database_info 
SET username = 'dbai_user' 
WHERE id = 1;
```

---

### Step 4: Verify Database Connection

Test if the connection works:

**Option A: Test from psql**
```bash
# Try connecting with the username you're using
psql -U the_username_you_set -d your_database_name -h localhost -p 5432
```

If this works, your database connection should work in the app.

**Option B: Test via API (if you have a test endpoint)**
```bash
curl http://localhost:8080/api/query-execution/test-connection/1
```

---

### Step 5: Check Database Password (If Required)

If your PostgreSQL user requires a password, you need to add password support:

**Current Issue:** The `QueryExecutionService` currently uses an empty password. If your PostgreSQL user requires a password, you need to:

1. **Add password field to DatabaseInfo model** (if not already there)
2. **Update QueryExecutionService** to use the password from DatabaseInfo
3. **Store password securely** (encrypted, not plain text)

**Quick Fix (Development Only):**
If you want to test quickly, you can temporarily modify the connection code, but **DO NOT commit this to production!**

---

### Step 6: Restart Backend

After making changes:

```bash
# Stop the backend (Ctrl+C)
# Then restart it
cd backend
./mvnw spring-boot:run
```

---

### Step 7: Test in Frontend

1. Refresh your frontend page
2. Try executing the query again
3. Check if the error is resolved

---

## Common Issues & Solutions

### Issue 1: "Connection refused"
**Solution:** PostgreSQL might not be running
```bash
# Check if PostgreSQL is running
brew services list  # On macOS with Homebrew
# Or
pg_isready

# Start PostgreSQL if needed
brew services start postgresql  # On macOS
```

### Issue 2: "Database does not exist"
**Solution:** The database name in `DatabaseInfo` is wrong
```sql
-- Check what databases exist
\l

-- Update DatabaseInfo with correct database name
UPDATE database_info 
SET database_name = 'correct_database_name' 
WHERE id = 1;
```

### Issue 3: "Password authentication failed"
**Solution:** You need to add password support (see Step 5)

### Issue 4: "Permission denied"
**Solution:** The user doesn't have SELECT permissions
```sql
-- Grant SELECT permissions
GRANT SELECT ON ALL TABLES IN SCHEMA public TO your_username;
```

---

## Quick Checklist

- [ ] PostgreSQL is running
- [ ] You know what PostgreSQL users exist (`\du` in psql)
- [ ] You've checked the `username` in `database_info` table
- [ ] You've either:
  - Created the 'postgres' user, OR
  - Updated `DatabaseInfo` to use an existing user
- [ ] You can connect via psql with that username
- [ ] Backend has been restarted
- [ ] Frontend has been refreshed

---

## Still Having Issues?

1. **Check backend logs** for detailed error messages:
   ```bash
   # Look at the terminal where backend is running
   # Or check logs/application.log
   ```

2. **Check PostgreSQL logs**:
   ```bash
   # On macOS with Homebrew
   tail -f /usr/local/var/log/postgres.log
   # Or
   tail -f ~/Library/Logs/Homebrew/postgresql*.log
   ```

3. **Verify connection details**:
   - Host: Should be `localhost` or `127.0.0.1` for local
   - Port: Usually `5432` for PostgreSQL
   - Database name: Must exist
   - Username: Must exist in PostgreSQL
   - Password: Required if user has a password

4. **Test connection manually**:
   ```bash
   psql -U username -d database_name -h host -p port
   ```

---

## Example: Complete Fix

Here's a complete example if you want to use your macOS username:

```bash
# 1. Check your PostgreSQL users
psql -d postgres -c "\du"

# 2. Note your username (e.g., "asutoshbhere")

# 3. Update DatabaseInfo
psql -U your_username -d databaseai << EOF
UPDATE database_info 
SET username = 'asutoshbhere' 
WHERE id = 1;
SELECT * FROM database_info;
EOF

# 4. Test connection
psql -U asutoshbhere -d your_database_name

# 5. Restart backend
cd backend
./mvnw spring-boot:run

# 6. Test in frontend
```

---

**Need more help?** Check the backend logs for the exact error message!

