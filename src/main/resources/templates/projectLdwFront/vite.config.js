import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { nodePolyfills } from 'vite-plugin-node-polyfills';

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    nodePolyfills()
  ],
  define: {
    // ESSA LINHA É CRUCIAL para o sockjs-client
    global: 'window', 
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src/main/resources/templates/projectLdwFront/src', import.meta.url))
    },
  },
})
