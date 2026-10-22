// Configuração do Vite (servidor de desenvolvimento e build).
// O plugin do React habilita JSX e o recarregamento rápido (Fast Refresh).
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
})
