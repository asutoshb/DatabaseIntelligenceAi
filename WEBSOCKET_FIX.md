# WebSocket Connection Fix

## Problem
- Error: `Uncaught ReferenceError: global is not defined` from `sockjs-client`
- WebSocket connection failing

## Root Cause
The `sockjs-client` library expects a `global` object (Node.js environment), but browsers don't have it by default.

## Solution
Added a polyfill in `vite.config.ts`:

```typescript
define: {
  // Polyfill for 'global' - sockjs-client expects this Node.js global
  global: 'globalThis',
}
```

## How to Apply the Fix

1. **Stop the dev server** (if running) - Press `Ctrl+C` in the terminal
2. **Restart the dev server:**
   ```bash
   cd frontend
   npm run dev
   ```
3. **Refresh the browser** - The error should be gone

## Why This Works

- `global` is a Node.js global object
- Browsers use `globalThis` (or `window`)
- The `define` option in Vite replaces all instances of `global` with `globalThis` at build time
- This makes `sockjs-client` work in the browser environment

## Verification

After restarting, you should see:
- ✅ No `global is not defined` error
- ✅ WebSocket Status card shows "Connected" (green)
- ✅ Console shows: `[HH:MM:SS] ✅ WebSocket connected`

