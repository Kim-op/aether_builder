import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  css: {
    preprocessorOptions: {
      less: {
        modifyVars: {
          // 暗黑主题基础变量 - 优化对比度
          '@primary-color': '#1890ff',
          '@background-color': '#121212',
          '@text-color': '#f5f5f5',
          '@border-color': '#333333',
          '@card-background': '#1e1e1e',
          '@menu-dark-bg': '#1e1e1e',
          '@header-dark-bg': '#1e1e1e',
          '@footer-dark-bg': '#1e1e1e',

          // 科技感强调色
          '@success-color': '#00e676',
          '@warning-color': '#ffea00',
          '@error-color': '#ff1744',

          // 文本和背景对比度 - 增强可读性
          '@text-color-secondary': '#b0b0b0',
          '@text-color-tertiary': '#808080',
          '@background-color-light': '#252525',
          '@background-color-lighter': '#303030',

          // 字体设置
          '@font-family': 'Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif',
        },
        javascriptEnabled: true,
      },
    },
  },
})
