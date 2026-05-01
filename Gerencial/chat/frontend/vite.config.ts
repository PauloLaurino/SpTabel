import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/chat/',
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/chat/api': {
        target: 'http://localhost:5000',
        changeOrigin: true,
      },
      '/chat/ws': {
        target: 'ws://localhost:5000',
        ws: true,
        changeOrigin: true,
      },
      '/chat': {
        target: 'http://localhost:5000',
        changeOrigin: true,
      },
    },
  },
  build: {
    // Build para o chat.war separado - arquivos vão para src/main/webapp do módulo chat
    outDir: '../src/main/webapp',
    sourcemap: false,
  },
})

