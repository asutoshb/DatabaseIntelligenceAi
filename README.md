# AI Database Intelligence Platform

🚀 An AI-powered platform that converts natural language queries to SQL using RAG (Retrieval-Augmented Generation).

## 🌐 Live Demo

- **Frontend**: [https://database-intelligence.netlify.app/nl-to-sql](https://database-intelligence.netlify.app/nl-to-sql)
- **Backend API Health**: [https://ai-database-intelligence-backend.onrender.com/api/health](https://ai-database-intelligence-backend.onrender.com/api/health)

## 📋 Project Overview

This project enables users to:
- Query databases using natural language (e.g., "Show me top 5 customers by revenue")
- Automatically generate optimized SQL queries
- Get intelligent insights from query results
- Visualize data automatically

## 🏗️ Architecture

- **Backend**: Java Spring Boot (REST API)
- **Frontend**: React + TypeScript
- **Database**: PostgreSQL + pgvector
- **AI**: OpenAI API (GPT-4, Embeddings)
- **Deployment**: Backend (Railway/Render) + Frontend (Netlify/Vercel)

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.6+
- PostgreSQL (will be set up in Chunk 2)

### Setup

1. **Clone the repository:**
   ```bash
   cd DatabaseAI
   ```

2. **Start Backend:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   # Backend runs on http://localhost:8080
   ```

3. **Start Frontend:**
   ```bash
   cd frontend
   yarn install  # or npm install
   yarn dev       # or npm run dev
   # Frontend runs on http://localhost:3000
   ```

4. **Open browser:**
   ```
   http://localhost:3000
   ```

## 📚 Development Chunks

We're building this project in 14 manageable chunks:

- ✅ **Chunk 1**: Project Setup (Complete)
- ⏳ **Chunk 2**: Database Setup
- ⏳ **Chunk 3**: OpenAI Integration
- ⏳ **Chunk 4**: pgvector Setup
- ⏳ **Chunk 5**: NL→SQL with RAG
- ⏳ **Chunk 6**: Query Execution
- ⏳ **Chunk 7**: Authentication
- ⏳ **Chunk 8**: WebSocket
- ⏳ **Chunk 9**: Frontend Setup
- ⏳ **Chunk 10**: Query Interface
- ⏳ **Chunk 11**: Results Display
- ⏳ **Chunk 12**: Visualization
- ⏳ **Chunk 13**: Backend Deployment
- ⏳ **Chunk 14**: Frontend Deployment

## 📁 Project Structure

```
DatabaseAI/
├── backend/              # Spring Boot backend
│   ├── src/
│   │   └── main/
│   │       ├── java/     # Java source code
│   │       └── resources/ # Configuration files
│   └── pom.xml          # Maven dependencies
├── frontend/             # React frontend
│   ├── src/             # React source code
│   └── package.json     # npm dependencies
└── README.md
```

## 🔧 Tech Stack

### Backend
- Java 17
- Spring Boot 3.2
- PostgreSQL + pgvector
- Spring Security
- WebSocket

### Frontend
- React 18
- TypeScript
- Material-UI
- Vite
- Axios

## 📝 License

MIT License

## 🤝 Contributing

This is a learning project. Feel free to fork and customize!

---

**Current Status**: ✅ Chunk 1 Complete - Project Setup

