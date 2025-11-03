# 📝 Where to Add the Environment Variable

## ⚠️ IMPORTANT: This is NOT added to application.properties!

The `export OPENAI_API_KEY=...` command is run in your **TERMINAL**, not in the properties file!

---

## 🖥️ Step-by-Step: Where to Type This

### Method 1: In Your Terminal (Easiest - Just Copy & Paste!)

1. **Open your terminal** (Terminal app on macOS)

2. **Type or paste this command:**
   ```bash
   export OPENAI_API_KEY=sk-example
   ```

3. **Press Enter**

4. **Verify it worked:**
   ```bash
   echo $OPENAI_API_KEY
   ```
   (Should show your API key)

5. **Run your backend:**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

---

## 🎯 Visual Guide

```
┌─────────────────────────────────────────┐
│  Terminal Window                        │
├─────────────────────────────────────────┤
│  $ export OPENAI_API_KEY=sk-proj-...   │ ← Type this HERE
│  $ echo $OPENAI_API_KEY                 │ ← Verify
│  sk-proj-thmGPSdpgaqZee08EkdV...       │ ← Should show your key
│  $ cd backend                           │
│  $ ./mvnw spring-boot:run               │ ← Backend will use it
└─────────────────────────────────────────┘
```

**NOT in application.properties file!**

---

## 🔄 Make It Permanent (So You Don't Need to Export Every Time)

If you want the environment variable to be available every time you open a terminal:

### Step 1: Open your shell config file

```bash
# For macOS (zsh) - most common
nano ~/.zshrc

# OR if using bash
nano ~/.bashrc
```

### Step 2: Add this line at the end:

```bash
export OPENAI_API_KEY=sk-example
```

### Step 3: Save and exit
- Press `Ctrl + X`
- Press `Y` (to confirm)
- Press `Enter`

### Step 4: Reload your shell

```bash
source ~/.zshrc
# OR
source ~/.bashrc
```

---

## ✅ Quick Test

After setting the variable, test it:

```bash
# 1. Check if variable is set
echo $OPENAI_API_KEY

# 2. Test backend endpoint
curl http://localhost:8080/api/embeddings/status
```

**Expected response:**
```json
{
  "apiKeyConfigured": true,
  "message": "OpenAI API key is configured"
}
```

---

## 📍 Summary

- ❌ **NOT in:** `application.properties` file
- ✅ **YES in:** Terminal (command line)
- ✅ **OR in:** `~/.zshrc` or `~/.bashrc` (for permanent)

**Just run this in your terminal right now:**
```bash
export OPENAI_API_KEY=sk-example
```

That's it! 🎉

