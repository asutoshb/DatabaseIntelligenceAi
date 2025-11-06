# 🔐 Environment Variables Setup with .env File

## ✅ What's Been Set Up

Your project now supports `.env` file for easy local development!

**Files Created:**
- ✅ `backend/.env.example` - Template file (safe to commit)
- ✅ `backend/.env` - Your actual API key (NOT committed to Git)

**Configuration:**
- ✅ Added `dotenv-java` library to `pom.xml`
- ✅ Created `DotEnvConfig.java` to load `.env` file
- ✅ Updated `.gitignore` to exclude `.env` files
- ✅ Spring Boot automatically reads from `.env` file!

---

## 🚀 How to Use

### Step 1: Create `.env` File

In the `backend/` directory, create a file named `.env`:

```bash
cd backend
touch .env
```

### Step 2: Add Your Environment Variables

Open `backend/.env` and add:

```env
# OpenAI API Key (Chunk 3)
OPENAI_API_KEY=sk-your-openai-api-key-here

# JWT Secret Key (Chunk 7)
# Must be at least 32 characters (256 bits) for security
JWT_SECRET=mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLongForProductionUse

# JWT Expiration (optional, defaults to 24 hours)
JWT_EXPIRATION=86400000
```

**File location:** `backend/.env` (same directory as `pom.xml`)

**⚠️ Important:** 
- `JWT_SECRET` is **REQUIRED** - application will fail to start without it
- Must be at least 32 characters long
- Never commit this file to Git!

---

## 📁 File Structure

```
backend/
├── .env                    ← Your API key goes here (NOT in Git)
├── .env.example            ← Template (safe to commit)
├── pom.xml
├── src/
└── ...
```

---

## ✅ How It Works

**Priority Order (what Spring Boot reads first):**

1. **System Environment Variable** (production)
   - `OPENAI_API_KEY`
   - `JWT_SECRET`
   - `JWT_EXPIRATION`
2. **.env file** (local development) ✅ **What you're using!**
   - `OPENAI_API_KEY`
   - `JWT_SECRET` (REQUIRED)
   - `JWT_EXPIRATION` (optional)
3. **application.properties** (fallback, not recommended for secrets)

**Flow:**
1. `DotEnvConfig` loads `.env` file when app starts
2. Sets `OPENAI_API_KEY` and `JWT_SECRET` as system properties
3. `OpenAIConfig` reads `OPENAI_API_KEY` via `@Value("${OPENAI_API_KEY:...}")`
4. `JwtUtil` reads `JWT_SECRET` via `@Value("${jwt.secret}")`
5. Backend uses them automatically! 🎉

---

## 🧪 Test It

### Step 1: Make Sure `.env` Exists

```bash
cd backend
ls -la .env
# Should show the file
```

### Step 2: Verify It Has Your Key

```bash
cat .env
# Should show: OPENAI_API_KEY=sk-proj-...
```

### Step 3: Restart Backend

```bash
cd backend
./mvnw spring-boot:run
```

**Look for this message:**
```
✅ .env file loaded successfully
✅ OPENAI_API_KEY loaded from .env file
✅ JWT_SECRET loaded from .env file
✅ JWT_EXPIRATION loaded from .env file
```

**⚠️ If you see:**
```
⚠️  WARNING: JWT_SECRET not found in .env file!
```
**Action:** Add `JWT_SECRET=...` to your `.env` file and restart!

### Step 4: Test API

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

## 🔒 Security

**✅ DO:**
- ✅ Use `.env` file for local development
- ✅ Add `.env` to `.gitignore` (already done!)
- ✅ Commit `.env.example` as a template
- ✅ Use system environment variables in production

**❌ DON'T:**
- ❌ Never commit `.env` to Git
- ❌ Never share your API key
- ❌ Never hardcode keys in source code

---

## 🎯 Summary

**Before:** Had to set `export OPENAI_API_KEY=...` every time  
**Now:** Just create `backend/.env` file once, and it works! ✨

**Next Steps:**
1. Create `backend/.env` file
2. Add `OPENAI_API_KEY=sk-your-key-here`
3. Add `JWT_SECRET=your`
4. Restart backend
5. Done! 🎉

---

## 💡 Pro Tips

**For Team Development:**
- Share `.env.example` with your team
- Each developer creates their own `.env` file
- No need to share actual API keys!

**For Production:**
- Don't use `.env` file
- Set `OPENAI_API_KEY` as environment variable
- Platform will inject it automatically

**Multiple Environments:**
```env
# .env.local (for local development)
OPENAI_API_KEY=sk-local-key

# .env.dev (for dev server)
OPENAI_API_KEY=sk-dev-key

# Production uses system env var
OPENAI_API_KEY=sk-prod-key
```

---

**Your API key management is now super easy! 🔐✨**

