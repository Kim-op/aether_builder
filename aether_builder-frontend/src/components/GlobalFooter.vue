<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        <a href="https://github.com/Kim-op" target="_blank" rel="noopener noreferrer" class="author-link">
          github by 程序员cammy
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: #1e1e1e;
  text-align: center;
  padding: 20px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  border-top: 1px solid #333;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.3);
  position: relative;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg, transparent, #1890ff, transparent);
}

.copyright {
  margin: 0;
  color: #b0b0b0;
  font-size: 14px;
}

.author-link {
  color: #1890ff;
  text-decoration: none;
  transition: all 0.3s ease;
  text-shadow: 0 0 5px rgba(24, 144, 255, 0.3);
}

.author-link:hover {
  color: #40a9ff;
  text-shadow: 0 0 10px rgba(24, 144, 255, 0.7);
}
</style>
