# Chunk 3 Explanation: OpenAI API Integration

## 🎯 What We Built

We integrated OpenAI API to:
1. **Generate Embeddings** - Convert text to vector numbers
2. **Call GPT** - Generate text responses using AI
3. **Secure API Key Management** - Using environment variables

---

## 📚 Technologies Explained

### 1. WebClient - HTTP Client for REST APIs

**What is WebClient?**
- Spring's modern HTTP client (replaced RestTemplate)
- Reactive (non-blocking, async)
- Easy to use for calling external APIs

**Simple Explanation:**
- Like making HTTP requests from a browser, but from Java code
- `POST https://api.openai.com/v1/embeddings` → Get response

**Why WebClient?**
- Modern, reactive (faster)
- Better error handling
- Easier to use than RestTemplate

**Example:**
```java
WebClient webClient = WebClient.builder()
    .baseUrl("https://api.openai.com/v1")
    .defaultHeader("Authorization", "Bearer sk-...")
    .build();

// Make POST request
Response response = webClient.post()
    .uri("/embeddings")
    .bodyValue(request)
    .retrieve()
    .bodyToMono(Response.class)
    .block();
```

---

### 2. Embeddings - What Are They?

**Simple Explanation:**
- Embeddings = Converting text into a list of numbers
- Example: "Hello" → [0.1, 0.5, -0.3, 0.9, ..., 0.2] (1536 numbers)
- Similar texts have similar number lists

**Analogy:**
- Like converting words into a secret code of numbers
- "Dog" and "Puppy" have similar codes (they're related)
- "Dog" and "Car" have different codes (they're not related)

**Why We Need Embeddings:**
- In Chunk 4: Store schema descriptions as embeddings
- In Chunk 5: Find similar schemas using embeddings
- RAG: Retrieve relevant context before generating SQL

**Example:**
```
Text: "users table with id and name columns"
Embedding: [0.123, -0.456, 0.789, ..., 0.321] (1536 numbers)
```

**Real Use Case:**
1. User asks: "Show customers by revenue"
2. System finds similar schema descriptions using embeddings
3. Uses that context to generate accurate SQL

---

### 3. GPT (ChatGPT API) - Text Generation

**What is GPT?**
- GPT = Generative Pre-trained Transformer
- AI model that generates human-like text
- Can understand context and answer questions

**How It Works:**
1. You send a prompt (question/instruction)
2. GPT processes it
3. Returns generated text response

**Simple Example:**
```
Input: "What is SQL?"
Output: "SQL (Structured Query Language) is a programming language..."
```

**How We Use It:**
- Convert natural language to SQL (Chunk 5)
- Generate explanations
- Answer questions about databases

**API Call:**
```
POST https://api.openai.com/v1/chat/completions
Body: {
  "model": "gpt-3.5-turbo",
  "messages": [
    {"role": "user", "content": "What is SQL?"}
  ]
}
Response: {
  "choices": [{
    "message": {"content": "SQL is..."}
  }]
}
```

---

### 4. Environment Variables - Secure API Key Storage

**What are Environment Variables?**
- Variables stored outside your code
- Not committed to Git (secure!)
- Set in terminal or system settings

**Why Use Environment Variables?**
- ✅ API keys stay secret
- ✅ Different keys for dev/prod
- ✅ Not exposed in code

**How to Set (macOS/Linux):**
```bash
export OPENAI_API_KEY=sk-your-key-here
```

**How to Use in Code:**
```java
@Value("${OPENAI_API_KEY}")
private String apiKey;
```

**Spring Boot Priority:**
1. Environment variable (highest priority)
2. `application.properties`
3. Default value

---

## 🏗️ Architecture

### Request Flow for Embedding:

```
1. API Request: POST /api/embeddings/generate
   Body: {"text": "Hello world"}
        ↓
2. EmbeddingController receives request
        ↓
3. Calls EmbeddingService.generateEmbedding()
        ↓
4. EmbeddingService creates EmbeddingRequest DTO
        ↓
5. WebClient POST to OpenAI API
   POST https://api.openai.com/v1/embeddings
        ↓
6. OpenAI returns EmbeddingResponse
        ↓
7. Extract embedding vector (list of numbers)
        ↓
8. Return to client as JSON
```

### Request Flow for GPT:

```
1. API Request: POST /api/llm/generate
   Body: {"prompt": "What is SQL?"}
        ↓
2. LLMController receives request
        ↓
3. Calls LLMService.generateText()
        ↓
4. LLMService creates ChatRequest DTO
        ↓
5. WebClient POST to OpenAI API
   POST https://api.openai.com/v1/chat/completions
        ↓
6. OpenAI returns ChatResponse
        ↓
7. Extract text from response
        ↓
8. Return to client
```

---

## 📝 What Each Component Does

### 1. OpenAIConfig.java
- Configures WebClient for OpenAI API
- Sets base URL and headers
- Reads API key from environment

### 2. EmbeddingService.java
- Generates embeddings from text
- Calls OpenAI Embeddings API
- Returns vector (list of numbers)

### 3. LLMService.java
- Calls GPT for text generation
- Can generate SQL from natural language
- Handles errors gracefully

### 4. DTOs (Data Transfer Objects)
- `EmbeddingRequest` - What we send to OpenAI
- `EmbeddingResponse` - What OpenAI sends back
- `ChatRequest` - GPT request format
- `ChatResponse` - GPT response format

### 5. Controllers
- `EmbeddingController` - REST endpoints for embeddings
- `LLMController` - REST endpoints for GPT

---

## 🔐 Security Best Practices

### ✅ DO:
- Use environment variables for API keys
- Never commit API keys to Git
- Use `.gitignore` to exclude sensitive files
- Validate API keys before making calls

### ❌ DON'T:
- Hardcode API keys in code
- Commit keys to GitHub
- Share keys publicly
- Log API keys in console

---

## 🎓 Key Concepts Summary

| Concept | What It Is | Why We Need It |
|---------|------------|----------------|
| **WebClient** | HTTP client for REST APIs | Call OpenAI API from Java |
| **Embeddings** | Text as vector numbers | Find similar schemas (RAG) |
| **GPT** | AI text generation | Convert NL to SQL |
| **Environment Variables** | Secure config storage | Protect API keys |
| **DTO** | Data transfer objects | Structure API requests/responses |

---

## 🚀 Next Steps

Chunk 3 is complete! You can now:
- ✅ Generate embeddings from text
- ✅ Call GPT for text generation
- ✅ Preview SQL generation

**Chunk 4:** Store embeddings in pgvector
**Chunk 5:** Use embeddings + GPT for NL→SQL with RAG!

---

**You've integrated AI into your application! 🎉**

