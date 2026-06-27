import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5000,
    allowedHosts: true,
  },
  // Configuración de Vitest: entorno jsdom simula el DOM del navegador
  test: {
    environment: 'jsdom',
    globals: true,
  },
})
