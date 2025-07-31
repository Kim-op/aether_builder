<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 科技感数据可视化示例
const dataPoints = ref([
  { name: '系统A', value: 65 },
  { name: '系统B', value: 78 },
  { name: '系统C', value: 90 },
  { name: '系统D', value: 45 },
  { name: '系统E', value: 60 },
])

// 动态脉冲效果
const pulseScale = ref(1)
const pulseOpacity = ref(0.7)

onMounted(() => {
  const pulseInterval = setInterval(() => {
    pulseScale.value = 1.05
    pulseOpacity.value = 1
    setTimeout(() => {
      pulseScale.value = 1
      pulseOpacity.value = 0.7
    }, 1000)
  }, 2000)

  return () => clearInterval(pulseInterval)
})
</script>

<template>
  <main class="home-container">
    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="pulse-background" :style="{ transform: `scale(${pulseScale})`, opacity: pulseOpacity }"></div>
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="highlight">苏苏 AI 零代码应用生成平台</span>
          <span class="subtitle">科技驱动创新</span>
        </h1>
        <p class="hero-description">
          打造下一代智能系统，融合前沿技术与创新思维
        </p>
        <div class="hero-actions">
          <button class="action-button primary">开始探索</button>
          <button class="action-button secondary">了解更多</button>
        </div>
      </div>
    </section>

    <!-- 功能卡片区域 -->
    <section class="features-section">
      <h2 class="section-title">核心功能</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="card-icon">⚡</div>
          <h3 class="card-title">智能分析</h3>
          <p class="card-description">利用先进算法进行深度数据分析，提供精准洞察</p>
        </div>
        <div class="feature-card">
          <div class="card-icon">🔒</div>
          <h3 class="card-title">安全防护</h3>
          <p class="card-description">多层级安全架构，确保系统和数据的绝对安全</p>
        </div>
        <div class="feature-card">
          <div class="card-icon">🚀</div>
          <h3 class="card-title">高性能</h3>
          <p class="card-description">优化的架构设计，提供卓越的系统性能和响应速度</p>
        </div>
        <div class="feature-card">
          <div class="card-icon">🔄</div>
          <h3 class="card-title">实时更新</h3>
          <p class="card-description">持续的系统更新和功能迭代，保持技术领先优势</p>
        </div>
      </div>
    </section>

    <!-- 数据可视化区域 -->
    <section class="data-section">
      <h2 class="section-title">系统状态</h2>
      <div class="data-grid">
        <div v-for="item in dataPoints" :key="item.name" class="data-card">
          <div class="data-label">{{ item.name }}</div>
          <div class="data-bar-container">
            <div class="data-bar" :style="{ width: `${item.value}%` }"></div>
          </div>
          <div class="data-value">{{ item.value }}%</div>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
/* 主页容器 */
.home-container {
  padding: 40px 0;
  min-height: calc(100vh - 180px);
  position: relative;
  overflow: hidden;
}

/* 英雄区域 */
.hero-section {
  position: relative;
  padding: 80px 20px;
  text-align: center;
  margin-bottom: 60px;
}

.pulse-background {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(1);
  width: 800px;
  height: 800px;
  background: radial-gradient(circle, rgba(24, 144, 255, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  transition: transform 1s ease, opacity 1s ease;
  z-index: 0;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 48px;
  margin-bottom: 16px;
  color: #f5f5f5;
  text-shadow: 0 0 20px rgba(24, 144, 255, 0.5);
  font-weight: 700;
}

.highlight {
  background: linear-gradient(90deg, #1890ff, #00e676);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  display: block;
  font-size: 24px;
  margin-top: 10px;
  color: #b0b0b0;
}

.hero-description {
  font-size: 18px;
  color: #b0b0b0;
  margin-bottom: 32px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.action-button {
  padding: 12px 24px;
  border-radius: 4px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.primary {
  background-color: #1890ff;
  border: 1px solid #1890ff;
  color: white;
  box-shadow: 0 0 15px rgba(24, 144, 255, 0.5);
}

.primary:hover {
  background-color: #40a9ff;
  box-shadow: 0 0 20px rgba(24, 144, 255, 0.7);
  transform: translateY(-2px);
}

.secondary {
  background-color: transparent;
  border: 1px solid #333;
  color: #e0e0e0;
}

.secondary:hover {
  border-color: #1890ff;
  color: #1890ff;
  box-shadow: 0 0 15px rgba(24, 144, 255, 0.3);
  transform: translateY(-2px);
}

/* 功能区域 */
.features-section {
  max-width: 1200px;
  margin: 0 auto 80px;
  padding: 0 20px;
}

.section-title {
  font-size: 32px;
  margin-bottom: 40px;
  color: #f5f5f5;
  text-align: center;
  position: relative;
  font-weight: 600;
}

.section-title::after {
  content: '';
  display: block;
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #1890ff, #00e676);
  margin: 16px auto 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.feature-card {
  background: #1e1e1e;
  border: 1px solid #333;
  border-radius: 8px;
  padding: 32px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #1890ff, transparent);
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  border-color: #1890ff;
}

.card-icon {
  font-size: 40px;
  margin-bottom: 16px;
  color: #1890ff;
  text-shadow: 0 0 10px rgba(24, 144, 255, 0.5);
}

.card-title {
  font-size: 20px;
  margin-bottom: 12px;
  color: #f0f0f0;
  font-weight: 600;
}

.card-description {
  color: #b0b0b0;
  font-size: 16px;
}

/* 数据可视化区域 */
.data-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.data-card {
  background: #1e1e1e;
  border: 1px solid #333;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
}

.data-card:hover {
  border-color: #1890ff;
  box-shadow: 0 5px 15px rgba(24, 144, 255, 0.2);
}

.data-label {
  color: #b0b0b0;
  margin-bottom: 10px;
  font-size: 14px;
}

.data-bar-container {
  height: 8px;
  background: #252525;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.data-bar {
  height: 100%;
  background: linear-gradient(90deg, #1890ff, #00e676);
  border-radius: 4px;
  transition: width 1s ease;
}

.data-value {
  color: #f0f0f0;
  font-weight: bold;
  font-size: 18px;
}
</style>
