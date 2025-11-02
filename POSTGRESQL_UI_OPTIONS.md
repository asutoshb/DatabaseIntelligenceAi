# PostgreSQL UI Options - View Your Data Online

## 🌐 Option 1: pgAdmin 4 (Web-based, Recommended)

**pgAdmin** is a web-based PostgreSQL administration tool - runs in your browser!

### Installation:
```bash
brew install --cask pgadmin4
```

### Usage:
1. Open pgAdmin 4 from Applications
2. It opens in your browser automatically (usually `http://127.0.0.1:5050`)
3. Add server:
   - Name: `Local PostgreSQL`
   - Host: `localhost`
   - Port: `5432`
   - Username: `asutoshbhere`
   - Password: (leave empty)
   - Database: `database_intelligence`

### Features:
- ✅ Beautiful web interface
- ✅ Browse tables visually
- ✅ Run SQL queries
- ✅ See data in tables
- ✅ Export/import data

---

## 🌐 Option 2: Adminer (Lightweight Web Tool)

**Adminer** is a single PHP file that provides a web interface!

### Installation:
```bash
# Install PHP (if not installed)
brew install php

# Download Adminer
cd /tmp
curl -o adminer.php https://www.adminer.org/latest.php

# Start PHP server
php -S localhost:8081 adminer.php
```

### Usage:
1. Open browser: `http://localhost:8081`
2. Select: **PostgreSQL**
3. Enter:
   - System: `PostgreSQL`
   - Server: `localhost:5432`
   - Username: `asutoshbhere`
   - Password: (empty)
   - Database: `database_intelligence`

---

## 💻 Option 3: VS Code Extension (If you use VS Code)

**SQLTools** extension provides database UI in VS Code!

### Installation:
1. Open VS Code
2. Extensions → Search "SQLTools"
3. Install "SQLTools" + "SQLTools PostgreSQL"

### Usage:
1. Open SQLTools sidebar
2. Click "+" to add connection
3. Enter PostgreSQL details
4. Browse tables, run queries in VS Code!

---

## 🖥️ Option 4: DBeaver Community (Desktop, Free)

**DBeaver** is a powerful desktop database tool (free version available).

### Installation:
```bash
brew install --cask dbeaver-community
```

### Features:
- Beautiful interface
- Browse data visually
- Run SQL queries
- Export data

---

## 🚀 Quick Setup: pgAdmin 4 (Easiest)

Let me install it for you!

