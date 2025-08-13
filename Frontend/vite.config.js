import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'


export default defineConfig({
  plugins: [vue(),
  tailwindcss(),
  ],
  server: {
    proxy: {
      '/api': { target: 'http://localhost:8081', changeOrigin: true }
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  extensions: ['.js', '.vue', '.json'],
})


