# AI Database Intelligence Platform - Backend

Spring Boot backend for converting natural language to SQL queries using AI.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- (PostgreSQL will be added in Chunk 2)

### Run the Application

1. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

2. **Run with Maven wrapper (recommended):**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Or if you have Maven installed:
   ```bash
   mvn spring-boot:run
   ```

3. **Test the API:**
   ```bash
   curl http://localhost:8080/api/health
   ```
   
   You should see:
   ```json
   {
     "status": "UP",
     "message": "AI Database Intelligence Platform is running!",
     "timestamp": "2024-01-01T12:00:00"
   }
   ```

## 📁 Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/databaseai/
│   │   │   ├── controller/     # REST API endpoints
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # Database access
│   │   │   ├── model/           # Data models
│   │   │   └── config/          # Configuration classes
│   │   └── resources/
│   │       └── application.properties  # App configuration
│   └── test/                     # Unit tests
├── pom.xml                       # Maven dependencies
└── README.md
```

## 🔧 Configuration

All configuration is in `src/main/resources/application.properties`

## 📚 Tech Stack (Chunk 1)

- **Spring Boot 3.2.0**: Framework that makes building Java apps easy
- **Java 17**: Programming language
- **Maven**: Dependency management and build tool
- **Spring Web**: For REST APIs
- **H2 Database**: In-memory database for testing (will switch to PostgreSQL later)

## 🧪 Testing

```bash
# Run tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📝 API Endpoints

### Health Check
- **GET** `/api/health`
- Returns server status

More endpoints will be added in future chunks!

