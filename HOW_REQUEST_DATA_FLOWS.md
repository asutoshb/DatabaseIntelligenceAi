# How Request Data Flows in Spring Boot

## 🔄 Complete Data Flow

### Step-by-Step Process

```
1. Client (Postman/curl/Frontend)
   ↓
   Sends HTTP POST with JSON body
   ↓
2. Spring Boot receives HTTP request
   ↓
3. @RequestBody annotation triggers JSON deserialization
   ↓
4. Jackson (JSON library) converts JSON → Java object
   ↓
5. NLToSQLRequest object is created with data
   ↓
6. Controller method receives the object
   ↓
7. You can access data via getters!
```

---

## 📡 Step 1: Client Sends Request

**Who sends the data?**
- Frontend application (React)
- Postman (API testing tool)
- curl command (terminal)
- Any HTTP client

**Example using curl:**
```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "Show me top 5 customers",
    "topK": 5
  }'
```

**The JSON in the request body:**
```json
{
  "databaseInfoId": 1,
  "naturalLanguageQuery": "Show me top 5 customers",
  "topK": 5
}
```

---

## 🎯 Step 2: Spring Boot Receives Request

**HTTP Request arrives at:**
```
POST /api/nl-to-sql/convert
Content-Type: application/json

{
  "databaseInfoId": 1,
  "naturalLanguageQuery": "Show me top 5 customers",
  "topK": 5
}
```

**Spring Boot routing:**
- `@PostMapping("/convert")` matches the URL
- Method `convertToSQL()` is called

---

## 🔧 Step 3: @RequestBody Annotation

**The magic happens here:**

```java
@PostMapping("/convert")
public ResponseEntity<NLToSQLResponse> convertToSQL(
        @Valid @RequestBody NLToSQLRequest request  // ← HERE!
)
```

**What `@RequestBody` does:**
1. Tells Spring Boot: "Read the HTTP request body"
2. Expects JSON format (because of `Content-Type: application/json`)
3. Automatically converts JSON → Java object
4. Uses Jackson library (built into Spring Boot)

---

## 📦 Step 4: Jackson Converts JSON → Java Object

**Jackson (JSON library) does the conversion:**

**JSON (from HTTP request):**
```json
{
  "databaseInfoId": 1,
  "naturalLanguageQuery": "Show me top 5 customers",
  "topK": 5
}
```

**Jackson automatically:**
1. Creates new `NLToSQLRequest` object
2. Calls setters:
   - `setDatabaseInfoId(1)`
   - `setNaturalLanguageQuery("Show me top 5 customers")`
   - `setTopK(5)`
3. Returns the populated object

**Java Object (after conversion):**
```java
NLToSQLRequest request = new NLToSQLRequest();
request.setDatabaseInfoId(1);
request.setNaturalLanguageQuery("Show me top 5 customers");
request.setTopK(5);
```

---

## ✅ Step 5: Controller Method Receives Object

**Now the `request` object has all the data!**

```java
@PostMapping("/convert")
public ResponseEntity<NLToSQLResponse> convertToSQL(
        @Valid @RequestBody NLToSQLRequest request  // ← Object is ready!
) {
    // Now you can use:
    Long dbId = request.getDatabaseInfoId();           // → 1
    String query = request.getNaturalLanguageQuery();   // → "Show me top 5 customers"
    Integer topK = request.getTopK();                  // → 5
    
    // Use the data...
}
```

---

## 🔍 How It Works: Field Mapping

**Jackson matches JSON keys to Java fields:**

| JSON Key | Java Field | How It Works |
|----------|------------|--------------|
| `"databaseInfoId"` | `databaseInfoId` | Jackson finds field with same name |
| `"naturalLanguageQuery"` | `naturalLanguageQuery` | Matches field name |
| `"topK"` | `topK` | Matches field name |

**Jackson calls setters:**
- `request.setDatabaseInfoId(1)`
- `request.setNaturalLanguageQuery("Show me top 5 customers")`
- `request.setTopK(5)`

---

## 📝 Complete Example

### 1. Client Sends Request

```bash
curl -X POST http://localhost:8080/api/nl-to-sql/convert \
  -H "Content-Type: application/json" \
  -d '{
    "databaseInfoId": 1,
    "naturalLanguageQuery": "Show me top 5 customers"
  }'
```

### 2. Spring Boot Receives

```java
@PostMapping("/convert")
public ResponseEntity<NLToSQLResponse> convertToSQL(
        @Valid @RequestBody NLToSQLRequest request
) {
    // request object is already populated!
    // No need to manually parse JSON!
}
```

### 3. Access Data

```java
Long id = request.getDatabaseInfoId();              // → 1
String query = request.getNaturalLanguageQuery();   // → "Show me top 5 customers"
Integer topK = request.getTopK();                   // → 5 (default value)
```

### 4. Use Data

```java
NLToSQLResponse response = nlToSQLService.convertToSQL(
    request.getDatabaseInfoId(),        // Pass the data
    request.getNaturalLanguageQuery(),
    request.getTopK() != null ? request.getTopK() : 5
);
```

---

## 🎓 Key Concepts

### 1. **@RequestBody Annotation**
- Tells Spring: "Read HTTP request body"
- Automatically converts JSON → Java object
- Uses Jackson library

### 2. **Jackson (JSON Library)**
- Built into Spring Boot
- Converts JSON ↔ Java objects
- Uses getters/setters or field names

### 3. **Automatic Conversion**
- No manual JSON parsing needed!
- Spring Boot handles everything
- Just use `@RequestBody` annotation

### 4. **Field Mapping**
- JSON keys match Java field names
- Jackson calls setters automatically
- Type conversion is automatic (String → String, Number → Long/Integer)

---

## 🔧 What Happens Behind the Scenes

**Spring Boot's internal process:**

1. **HTTP Request arrives**
   ```
   POST /api/nl-to-sql/convert
   Content-Type: application/json
   Body: {"databaseInfoId": 1, ...}
   ```

2. **Spring routes to controller**
   - Matches `@PostMapping("/convert")`
   - Finds `convertToSQL()` method

3. **@RequestBody processing**
   - Spring reads HTTP body
   - Detects JSON format
   - Calls Jackson to deserialize

4. **Jackson conversion**
   ```java
   // Pseudo-code of what Jackson does:
   NLToSQLRequest request = new NLToSQLRequest();
   request.setDatabaseInfoId(1);
   request.setNaturalLanguageQuery("Show me top 5 customers");
   request.setTopK(5);
   ```

5. **Validation (@Valid)**
   - Checks `@NotNull`, `@NotBlank` annotations
   - Returns error if validation fails

6. **Method execution**
   - Your code runs with populated `request` object

---

## 💡 Visual Flow

```
┌─────────────────┐
│   Client        │
│  (Postman/curl) │
└────────┬────────┘
         │
         │ HTTP POST
         │ JSON body
         ▼
┌─────────────────┐
│  Spring Boot    │
│  (Controller)   │
└────────┬────────┘
         │
         │ @RequestBody
         │ triggers
         ▼
┌─────────────────┐
│    Jackson      │
│  (JSON Library) │
└────────┬────────┘
         │
         │ Converts
         │ JSON → Java
         ▼
┌─────────────────┐
│ NLToSQLRequest  │
│  (Java Object)  │
│  - dbId: 1      │
│  - query: "..." │
│  - topK: 5      │
└────────┬────────┘
         │
         │ Use in code
         ▼
┌─────────────────┐
│ Your Code       │
│ request.get...()│
└─────────────────┘
```

---

## 🎯 Summary

**Where does the data come from?**
1. **Client sends HTTP POST** with JSON body
2. **Spring Boot receives** the HTTP request
3. **@RequestBody annotation** tells Spring to read the body
4. **Jackson library** converts JSON → Java object
5. **Setters are called** automatically
6. **Request object is populated** with data
7. **You access it** via getters (`request.getDatabaseInfoId()`)

**Key Point:**
- You don't manually parse JSON!
- Spring Boot + Jackson do it automatically!
- Just use `@RequestBody` annotation!

---

## ✅ Example: Complete Flow

**Request:**
```bash
POST /api/nl-to-sql/convert
Content-Type: application/json

{
  "databaseInfoId": 1,
  "naturalLanguageQuery": "Show me top 5 customers"
}
```

**Spring Boot automatically:**
```java
NLToSQLRequest request = new NLToSQLRequest();
request.setDatabaseInfoId(1);
request.setNaturalLanguageQuery("Show me top 5 customers");
request.setTopK(5);  // Default value
```

**Your code:**
```java
Long id = request.getDatabaseInfoId();  // → 1
String query = request.getNaturalLanguageQuery();  // → "Show me top 5 customers"
```

**That's it! Spring Boot handles everything! 🎉**

