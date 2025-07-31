<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
</script>

<template>
  <BasicLayout />
</template>

<style>
/* 全局暗黑科技风格样式 */
* {
  box-sizing: border-box;
}

:root {
  --primary-glow: 0 0 10px rgba(24, 144, 255, 0.5), 0 0 20px rgba(24, 144, 255, 0.3);
  --secondary-glow: 0 0 5px rgba(0, 230, 118, 0.5), 0 0 10px rgba(0, 230, 118, 0.3);
}

body {
  margin: 0;
  padding: 0;
  font-family:
    Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  overflow-x: hidden;
  background-color: #121212;
  color: #f5f5f5;
  line-height: 1.6;
  font-size: 16px;
  background-image:
    radial-gradient(circle at 25% 25%, rgba(24, 144, 255, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(0, 230, 118, 0.05) 0%, transparent 50%);
  background-size: 100% 100%;
  background-attachment: fixed;
}

html {
  overflow-x: hidden;
  scroll-behavior: smooth;
}

/* 科技感滚动条 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #1e1e1e;
}

::-webkit-scrollbar-thumb {
  background: #333;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #1890ff;
  box-shadow: var(--primary-glow);
}

/* 科技感选中效果 */
::selection {
  background: rgba(24, 144, 255, 0.3);
  color: #fff;
}
</style>
