# Chunk 3: Testing Guide

## 🚀 Quick Start

### Step 1: Get OpenAI API Key

1. Go to: https://platform.openai.com/api-keys
2. Sign up / Login (if needed)
3. Click "Create new secret key"
4. Copy the key (starts with `sk-`)
5. **Save it somewhere safe!** (You won't see it again)
// secret key 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

### Step 2: Set API Key

**Option A: Environment Variable (Recommended)**

**macOS/Linux:**
```bash
export OPENAI_API_KEY=sk-your-key-here
```

**To make it permanent (add to ~/.zshrc or ~/.bashrc):**
```bash
echo 'export OPENAI_API_KEY=sk-your-key-here' >> ~/.zshrc
source ~/.zshrc
```

**Option B: application.properties (Not Recommended for Production)**

Edit `backend/src/main/resources/application.properties`:
```properties
openai.api.key=sk-your-key-here
```

### Step 3: Restart Backend

```bash
# Stop current backend (Ctrl+C or):
pkill -f "spring-boot:run"

# Start again
cd backend
./mvnw spring-boot:run
```

### Step 4: Verify API Key

```bash
curl http://localhost:8080/api/embeddings/status
```

**If configured correctly:**
```json
{
  "apiKeyConfigured": true,
  "message": "OpenAI API key is configured"
}
```

**If not configured:**
```json
{
  "apiKeyConfigured": false,
  "message": "OpenAI API key is not configured"
}
```

---

## 🧪 Test Embeddings

### Test 1: Generate Single Embedding

```bash
curl -X POST http://localhost:8080/api/embeddings/generate \
  -H "Content-Type: application/json" \
  -d '{"text": "Hello world"}'
```

**Expected Response:**
```json
{
  "text": "Hello world",
  "embedding": [0.001234, -0.004567, 0.008901, ...],
  "dimension": 1536
}
```

**What you see:**
- `embedding`: List of 1536 numbers (the vector!)
- `dimension`: Size of the vector (always 1536 for ada-002)

### Test 2: Generate Multiple Embeddings

```bash
curl -X POST http://localhost:8080/api/embeddings/generate-batch \
  -H "Content-Type: application/json" \
  -d '{"texts": ["Hello", "World", "Test"]}'
```

**Expected Response:**
```json
{
  "texts": ["Hello", "World", "Test"],
  "embeddings": [
    [0.001, -0.002, ...],
    [0.003, -0.001, ...],
    [0.002, -0.003, ...]
  ],
  "count": 3,
  "dimension": 1536
}
```

---

## 🤖 Test GPT/LLM

### Test 1: Simple Text Generation

```bash
curl -X POST http://localhost:8080/api/llm/generate \
  -H "Content-Type: application/json" \
  -d '{"prompt": "What is SQL in one sentence?"}'
```

**Expected Response:**
```json
{
  "prompt": "What is SQL in one sentence?",
  "response": "SQL (Structured Query Language) is a programming language used to manage and manipulate relational databases."
}
```

### Test 2: Generate SQL (Preview)

```bash
curl -X POST http://localhost:8080/api/llm/generate-sql \
  -H "Content-Type: application/json" \
  -d '{"query": "Show me all users"}'
```

**Expected Response:**
```json
{
  "naturalLanguage": "Show me all users",
  "sql": "SELECT * FROM users;"
}
```

**Note:** This is a simple preview. In Chunk 5, we'll enhance it with RAG (using embeddings to find relevant schema context)!

### Test 3: Complex Query

```bash
curl -X POST http://localhost:8080/api/llm/generate-sql \
  -H "Content-Type: application/json" \
  -d '{"query": "Find all customers who ordered more than 5 items"}'
```

**Expected:**
```json
{
  "naturalLanguage": "Find all customers who ordered more than 5 items",
  "sql": "SELECT * FROM customers WHERE order_count > 5;"
}
```

---

## 🐛 Troubleshooting

### Error: "OpenAI API key is not configured"

**Solution:**
1. Make sure you set the environment variable:
   ```bash
   echo $OPENAI_API_KEY  # Should show your key
   ```
2. If empty, set it:
   ```bash
   export OPENAI_API_KEY=sk-your-key-here
   ```
3. Restart backend

### Error: "OpenAI API key is invalid"

**Solution:**
1. Check your API key is correct
2. Make sure it starts with `sk-`
3. Verify on OpenAI dashboard: https://platform.openai.com/api-keys
4. Check if you have credits: https://platform.openai.com/account/billing

### Error: "Rate limit exceeded"

**Solution:**
- You've made too many requests
- Wait a few minutes
- Check your usage: https://platform.openai.com/usage

### Error: "Connection timeout"

**Solution:**
- Check internet connection
- OpenAI API might be down (check status)

---

## 📊 Expected Costs

**OpenAI Pricing (as of 2024):**

- **Embeddings (ada-002):** ~$0.0001 per 1K tokens
- **GPT-3.5-turbo:** ~$0.002 per 1K tokens
- **GPT-4:** ~$0.03 per 1K tokens

**Example Costs:**
- 1000 embedding requests ≈ $0.10
- 100 GPT-3.5 responses ≈ $0.20

**Check usage:** https://platform.openai.com/usage

---

## ✅ Testing Checklist

- [ ] API key obtained from OpenAI
- [ ] API key set as environment variable
- [ ] Backend restarted
- [ ] Status endpoint shows API key configured
- [ ] Can generate single embedding
- [ ] Can generate batch embeddings
- [ ] Can generate text with GPT
- [ ] Can generate SQL from natural language
- [ ] Understand what embeddings are (list of numbers)
- [ ] Understand embeddings dimension (1536)

---

## 🎯 What to Test

### Basic Tests
1. ✅ Status check
2. ✅ Single embedding
3. ✅ Batch embeddings
4. ✅ GPT text generation
5. ✅ SQL generation

### Advanced Tests
1. Test with different text lengths
2. Test SQL generation with various queries
3. Compare embeddings of similar texts (should be similar)
4. Check error handling (invalid API key, network errors)

---

## 💡 Tips

- **Start with small tests** - Don't test with huge text
- **Check API usage** - Monitor costs on OpenAI dashboard
- **Save API key safely** - Use environment variables
- **Rate limits** - Don't make too many requests too fast

---

**Happy Testing! 🚀**

