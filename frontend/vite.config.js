import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // for docker to expose port
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://backend:8080', // should be service name in compose.yaml
        changeOrigin: true,
        secure: false
      }
    }
  }
})
