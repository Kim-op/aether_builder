<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        <a href="https://github.com/Kim-op/aether_builder" target="_blank" rel="noopener noreferrer"
          class="author-link">
          <GithubOutlined class="github-icon" />
          GitHub by Cammy
        </a>
      </p>
      <p class="rights">© 2025 构建者苏苏. 保留所有权利</p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
import { GithubOutlined } from '@ant-design/icons-vue'
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  text-align: center;
  padding: 30px 20px;
  margin-top: 60px;
  border-top: 1px solid rgba(59, 130, 246, 0.1);
  position: relative;
  overflow: hidden;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.2), transparent);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.copyright {
  margin: 0 0 12px;
  font-size: 14px;
  position: relative;
}

.author-link {
  color: #64748b;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px;
  border-radius: 16px;
  transition: all 0.3s ease;
}

.author-link:hover {
  color: #3b82f6;
  background-color: rgba(59, 130, 246, 0.05);
  transform: translateY(-2px);
}

.github-icon {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.author-link:hover .github-icon {
  transform: rotate(30deg) scale(1.2);
}

.rights {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
  opacity: 0.8;
  letter-spacing: 0.3px;
}

/* 装饰元素 */
.footer::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: linear-gradient(180deg, transparent, rgba(139, 92, 246, 0.03));
  pointer-events: none;
}

@media (max-width: 768px) {
  .footer {
    padding: 24px 16px;
    margin-top: 40px;
  }

  .copyright {
    font-size: 13px;
  }

  .rights {
    font-size: 12px;
  }
}
</style>
