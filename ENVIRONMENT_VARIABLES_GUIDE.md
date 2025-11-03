# Environment Variables Guide

## 🔐 Why Use Environment Variables?

**Security Benefits:**
- ✅ API keys never committed to Git
- ✅ Different keys for dev/prod
- ✅ Easy to rotate keys
- ✅ No accidental exposure

---

## 🚀 How to Set Environment Variables

### Option 1: Set for Current Terminal Session

```bash
export OPENAI_API_KEY=sk-your-key-here
```

**Lasts:** Until you close the terminal

### Option 2: Set Permanently (Recommended)

**For macOS (zsh shell):**
```bash
# Add to ~/.zshrc
echo 'export OPENAI_API_KEY=sk-your-key-here' >> ~/.zshrc

# Reload shell
source ~/.zshrc
```

**For bash:**
```bash
# Add to ~/.bashrc or ~/.bash_profile
echo 'export OPENAI_API_KEY=sk-your-key-here' >> ~/.bashrc
source ~/.bashrc
```

**Lasts:** Permanent across terminal sessions

### Option 3: Set Only for Backend Command

```bash
# Run backend with environment variable
OPENAI_API_KEY=sk-your-key-here ./mvnw spring-boot:run
```

**Lasts:** Only for that command execution

---

## ✅ Verify Environment Variable is Set

```bash
# Check if set
echo $OPENAI_API_KEY

# Should output your API key (if set)
# If empty, you need to set it!
```

---

## 🧪 Testing

### Step 1: Set Environment Variable

```bash
export OPENAI_API_KEY=sk-example
```

### Step 2: Verify It's Set

```bash
echo $OPENAI_API_KEY
# Should show your key
```

### Step 3: Restart Backend

```bash
cd backend
./mvnw spring-boot:run
```

### Step 4: Test API Key Status

```bash
curl http://localhost:8080/api/embeddings/status
```

**Expected:**
```json
{
  "apiKeyConfigured": true,
  "message": "OpenAI API key is configured"
}
```

---

## 🔄 How Spring Boot Reads Environment Variables

**Priority Order:**

1. **Environment Variables** (highest priority)
   - `OPENAI_API_KEY` environment variable
   
2. **application.properties** (fallback)
   - `openai.api.key=...` in properties file

3. **Default** (if neither set)
   - Empty string (will fail)

**Current Configuration:**
```java
@Value("${OPENAI_API_KEY:${openai.api.key:}}")
```

**This means:**
- First try: `OPENAI_API_KEY` environment variable
- If not found: Try `openai.api.key` from properties
- If not found: Empty string

---

## 💡 Best Practices

### ✅ DO:
- ✅ Use environment variables for all secrets
- ✅ Add `.env` files to `.gitignore` (if using .env)
- ✅ Document required environment variables in README
- ✅ Set different keys for dev/prod

### ❌ DON'T:
- ❌ Commit API keys to Git
- ❌ Hardcode keys in source code
- ❌ Share keys in screenshots/documentation
- ❌ Use same key for dev and production

---

## 📝 Quick Setup Script

Create a script to set it up:

```bash
# Create setup script
cat > setup-env.sh << 'EOF'
#!/bin/bash
export OPENAI_API_KEY=sk-your-key-here
echo "✅ OPENAI_API_KEY set for this session"
echo "Run: source setup-env.sh to activate"
EOF

chmod +x setup-env.sh

# Use it
source setup-env.sh
```

---

## 🎯 Summary

**What Changed:**
- ✅ Removed hardcoded API key from `application.properties`
- ✅ Configuration now reads from `OPENAI_API_KEY` environment variable first
- ✅ More secure - key not in Git!

**How to Use:**
1. Set: `export OPENAI_API_KEY=sk-your-key-here`
2. Verify: `echo $OPENAI_API_KEY`
3. Run backend - it will use the environment variable!

---

**Your API key is now secure! 🔒**

