import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  define: {
    // Polyfill for 'global' - sockjs-client expects this Node.js global
    global: 'globalThis',
  },
  server: {
    port: 3000,
    proxy: {
      // Proxy API requests to backend
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})

