import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import fs from 'fs'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    https: {
      key: fs.readFileSync('./key.pem'),
      cert: fs.readFileSync('./cert.pem')
    }, // https to enable encryption in transit, mitm attack
    host: true, // for docker to expose port
    port: 3443,
    proxy: {
      '/api': {
        target: 'http://backend:8080', // should be service name in compose.yaml
        changeOrigin: true,
        secure: false
      }
    }
  }
})
