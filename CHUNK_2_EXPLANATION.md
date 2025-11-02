# Chunk 2 Explanation: Database Setup & Spring Data JPA

## 🎯 What We're Building

We're connecting our Spring Boot application to a real database (PostgreSQL) and creating REST API endpoints that can Create, Read, Update, and Delete (CRUD) data.

---

## 📚 Technologies Explained

### 1. PostgreSQL - What is it?

**PostgreSQL** is a powerful, open-source relational database management system (RDBMS).

**Simple Explanation:**
- Think of it as an Excel spreadsheet, but much more powerful
- Stores data in tables (rows and columns)
- Can handle millions of rows
- Supports advanced features (like pgvector for AI later)

**Why PostgreSQL?**
- Industry standard
- Supports advanced features (pgvector for vector storage)
- Free and open-source
- Very reliable and fast

**Example:**
```
Table: users
| id | name      | email           |
|----|-----------|-----------------|
| 1  | John      | john@email.com  |
| 2  | Jane      | jane@email.com  |
```

---

### 2. Spring Data JPA - What is it?

**Spring Data JPA** is a framework that makes database access SUPER EASY.

**Simple Explanation:**
- Without JPA: Write SQL queries manually (complex, error-prone)
- With JPA: Just create Java classes, JPA handles SQL automatically!

**Example:**

**Without JPA (traditional way):**
```java
String sql = "SELECT * FROM users WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setInt(1, userId);
ResultSet rs = stmt.executeQuery();
// ... lots of code to process results
```

**With JPA (our way):**
```java
User user = userRepository.findById(userId);
// Done! JPA wrote and executed the SQL for us!
```

**Benefits:**
- ✅ No SQL writing needed (mostly)
- ✅ Type-safe (compiler catches errors)
- ✅ Less code
- ✅ Automatic table creation

---

### 3. @Entity Annotation - What is it?

**@Entity** marks a Java class as a database table.

**Simple Explanation:**
- You create a Java class
- Add @Entity annotation
- JPA automatically creates a table in the database
- Each field becomes a column

**Example:**

```java
@Entity  // ← Tells JPA: "Create a table for this class"
@Table(name = "users")  // Table name
public class User {
    @Id  // Primary key
    @GeneratedValue  // Auto-increment
    private Long id;
    
    private String name;  // Column: name (VARCHAR)
    private String email; // Column: email (VARCHAR)
}
```

**JPA automatically creates:**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);
```

**You don't write this SQL - JPA does it!**

---

### 4. @Repository - What is it?

**@Repository** marks an interface as a data access layer.

**Simple Explanation:**
- You create an interface (not a class!)
- Spring automatically implements it
- Gives you methods like `findById()`, `save()`, `delete()`, etc.

**Example:**

```java
@Repository  // ← Spring manages this
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring automatically provides:
    // - findById(id)
    // - save(user)
    // - findAll()
    // - deleteById(id)
    // - etc.
}
```

**What Spring Data JPA gives you automatically:**
- `findById(id)` - Find by ID
- `findAll()` - Get all records
- `save(entity)` - Create or update
- `deleteById(id)` - Delete by ID
- `count()` - Count records
- And many more!

**You don't implement these - Spring does it automatically!**

---

### 5. application.properties - Database Configuration

**What we configured:**

```properties
# PostgreSQL Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/database_intelligence
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
```

**Explanation:**

1. **`spring.datasource.url`**
   - `jdbc:postgresql://` - Protocol (how to connect)
   - `localhost:5432` - Database server (local machine, port 5432)
   - `database_intelligence` - Database name

2. **`spring.jpa.hibernate.ddl-auto=update`**
   - `update` - Automatically create/update tables when app starts
   - Other options: `create`, `validate`, `none`
   - This means: If table doesn't exist, create it. If it exists, update it.

3. **`spring.jpa.show-sql=true`**
   - Shows SQL queries in console (great for learning!)

---

## 🏗️ Architecture Flow

```
User makes API request
    ↓
Controller receives request
    ↓
Controller calls Repository
    ↓
Repository uses JPA
    ↓
JPA converts to SQL
    ↓
PostgreSQL executes SQL
    ↓
Results converted back to Java objects
    ↓
Controller returns JSON response
```

---

## 📝 CRUD Operations Explained

**CRUD = Create, Read, Update, Delete**

### Create (POST)
```java
POST /api/users
Body: { "name": "John", "email": "john@email.com" }
→ Creates new user in database
```

### Read (GET)
```java
GET /api/users/1
→ Returns user with ID 1
```

### Update (PUT)
```java
PUT /api/users/1
Body: { "name": "John Updated" }
→ Updates user with ID 1
```

### Delete (DELETE)
```java
DELETE /api/users/1
→ Deletes user with ID 1
```

---

## 🔄 How JPA Works (Step by Step)

### Step 1: You Define Entity
```java
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}
```

### Step 2: You Create Repository
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

### Step 3: You Use Repository
```java
@Autowired
private UserRepository userRepository;

User user = userRepository.findById(1L);
```

### Step 4: JPA Magic Happens!
1. JPA sees `findById(1L)`
2. JPA knows User entity has `@Id Long id`
3. JPA generates SQL: `SELECT * FROM users WHERE id = 1`
4. Executes SQL on PostgreSQL
5. Converts result to User object
6. Returns User object to you

**You never wrote SQL - JPA did it all!**

---

## 🎓 Key Concepts

### 1. Entity = Database Table
- Each @Entity class = one table
- Each field = one column

### 2. Repository = Data Access Layer
- Interface, not class
- Spring implements it automatically
- Provides CRUD methods

### 3. Controller = API Endpoint
- Handles HTTP requests
- Uses Repository to get/save data
- Returns JSON responses

### 4. Service Layer (Optional but recommended)
- Business logic goes here
- Controllers call Services
- Services call Repositories

---

## 🚀 Next Steps

Now let's build:
1. User Entity (database table)
2. UserRepository (data access)
3. UserController (REST API endpoints)
4. Test everything!

---

## 📚 Additional Resources

- **PostgreSQL Docs**: https://www.postgresql.org/docs/
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **JPA Tutorial**: https://www.baeldung.com/jpa-entities

