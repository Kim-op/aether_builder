<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
import { getDeployUrl } from '@/config/env'
import AppCard from '@/components/AppCard.vue'
const router = useRouter()
const loginUserStore = useLoginUserStore()

// 用户提示词
const userPrompt = ref('')
const creating = ref(false)

// 我的应用数据
const myApps = ref<API.AppVO[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 精选应用数据
const featuredApps = ref<API.AppVO[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 设置提示词
const setPrompt = (prompt: string) => {
  userPrompt.value = prompt
}

// 优化提示词功能已移除

// 创建应用
const createApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }

  creating.value = true
  try {
    const res = await addApp({
      initPrompt: userPrompt.value.trim(),
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      // 跳转到对话页面，确保ID是字符串类型
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (error) {
    console.error('创建应用失败：', error)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

// 加载我的应用
const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) {
    return
  }

  try {
    const res = await listMyAppVoByPage({
      pageNum: myAppsPage.current,
      pageSize: myAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      myAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  }
}

// 加载精选应用
const loadFeaturedApps = async () => {
  try {
    const res = await listGoodAppVoByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      featuredApps.value = res.data.data.records || []
      featuredAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  }
}

// 查看对话
const viewChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}?view=1`)
  }
}

// 查看作品
const viewWork = (app: API.AppVO) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

// 格式化时间函数已移除，不再需要显示创建时间

// 页面加载时获取数据
onMounted(() => {
  loadMyApps()
  loadFeaturedApps()

  // 鼠标跟随光效
  const handleMouseMove = (e: MouseEvent) => {
    const { clientX, clientY } = e
    const { innerWidth, innerHeight } = window
    const x = (clientX / innerWidth) * 100
    const y = (clientY / innerHeight) * 100

    document.documentElement.style.setProperty('--mouse-x', `${x}%`)
    document.documentElement.style.setProperty('--mouse-y', `${y}%`)
  }

  document.addEventListener('mousemove', handleMouseMove)

  // 清理事件监听器
  return () => {
    document.removeEventListener('mousemove', handleMouseMove)
  }
})
</script>

<template>
  <div id="homePage">
    <div class="container">
      <!-- 网站标题和描述 -->
      <div class="hero-section">
        <h1 class="hero-title">构建者苏苏</h1>
        <p class="hero-description">一句话轻松创建网站应用</p>
      </div>

      <!-- 用户提示词输入框 -->
      <div class="input-section">
        <a-textarea v-model:value="userPrompt" placeholder="帮我创建个人博客网站" :rows="4" :maxlength="1000"
          class="prompt-input" />
        <div class="input-actions">
          <a-button type="primary" size="large" @click="createApp" :loading="creating">
            <template #icon>
              <span>↵</span>
            </template>
          </a-button>
        </div>
      </div>

      <!-- 快捷按钮 -->
      <div class="quick-actions">
        <a-button type="default" @click="
          setPrompt(
            '创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。',
          )
          ">个人博客网站</a-button>
        <a-button type="default" @click="
          setPrompt(
            '设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。',
          )
          ">企业官网</a-button>
        <a-button type="default" @click="
          setPrompt(
            '构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。',
          )
          ">在线商城</a-button>
        <a-button type="default" @click="
          setPrompt(
            '制作一个精美的作品展示网站，适合设计师、摄影师、艺术家等创作者。包含作品画廊、项目详情页、个人简历、联系方式等模块。采用瀑布流或网格布局展示作品，支持图片放大预览和作品分类筛选。',
          )
          ">作品展示网站</a-button>
      </div>

      <!-- 我的作品 -->
      <div class="section">
        <h2 class="section-title">我的作品</h2>
        <div class="app-grid">
          <AppCard v-for="app in myApps" :key="app.id" :app="app" @view-chat="viewChat" @view-work="viewWork" />
        </div>
        <div class="pagination-wrapper">
          <a-pagination v-model:current="myAppsPage.current" v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total" :show-size-changer="false" :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps" />
        </div>
      </div>

      <!-- 精选案例 -->
      <div class="section">
        <h2 class="section-title">精选案例</h2>
        <div class="featured-grid">
          <AppCard v-for="app in featuredApps" :key="app.id" :app="app" :featured="true" @view-chat="viewChat"
            @view-work="viewWork" />
        </div>
        <div class="pagination-wrapper">
          <a-pagination v-model:current="featuredAppsPage.current" v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total" :show-size-changer="false" :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  /* 恢复原始蓝紫色系背景 */
  background:
    linear-gradient(180deg, #f8fafc 0%, #f1f5f9 8%, #e2e8f0 20%, #cbd5e1 100%),
    radial-gradient(circle at 20% 80%, rgba(59, 130, 246, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(139, 92, 246, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(16, 185, 129, 0.08) 0%, transparent 50%);
  position: relative;
  overflow: hidden;
}

/* 科技感网格背景 - 增强层次感 */
#homePage::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    /* 恢复蓝紫色网格线条 */
    linear-gradient(rgba(59, 130, 246, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(59, 130, 246, 0.05) 1px, transparent 1px),
    linear-gradient(rgba(139, 92, 246, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(139, 92, 246, 0.04) 1px, transparent 1px);
  background-size:
    100px 100px,
    100px 100px,
    20px 20px,
    20px 20px;
  pointer-events: none;
  animation: gridFloat 15s ease-in-out infinite;
  opacity: 0.9;
}

/* 动态光效 - 增强互动感 */
#homePage::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    /* 恢复蓝紫色光效 */
    radial-gradient(800px circle at var(--mouse-x, 50%) var(--mouse-y, 50%),
      rgba(59, 130, 246, 0.15) 0%,
      rgba(139, 92, 246, 0.12) 40%,
      transparent 80%),
    linear-gradient(45deg, transparent 30%, rgba(59, 130, 246, 0.08) 50%, transparent 70%),
    linear-gradient(-45deg, transparent 30%, rgba(139, 92, 246, 0.08) 50%, transparent 70%);
  pointer-events: none;
  animation: lightPulse 4s ease-in-out infinite alternate;
}

@keyframes gridFloat {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(10px, 10px); }
}

@keyframes lightPulse {
  0% { opacity: 0.5; }
  100% { opacity: 0.9; }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  z-index: 2;
  width: 100%;
  box-sizing: border-box;
}

/* 英雄区域 - 增强视觉冲击力 */
.hero-section {
  text-align: center;
  padding: 120px 0 80px;
  margin-bottom: 40px;
  color: #1e293b;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    /* 恢复蓝紫色径向渐变 */
    radial-gradient(ellipse 900px 500px at center, rgba(59, 130, 246, 0.15) 0%, transparent 70%),
    linear-gradient(45deg, transparent 30%, rgba(139, 92, 246, 0.1) 50%, transparent 70%),
    linear-gradient(-45deg, transparent 30%, rgba(16, 185, 129, 0.08) 50%, transparent 70%);
  animation: heroGlow 6s ease-in-out infinite alternate;
}

@keyframes heroGlow {
  0% {
    opacity: 0.7;
    transform: scale(1);
  }
  100% {
    opacity: 1;
    transform: scale(1.05);
  }
}

.hero-title {
  font-size: clamp(2.8rem, 7vw, 4rem);
  font-weight: 800;
  margin: 0 0 25px;
  line-height: 1.1;
  /* 恢复蓝紫色文字渐变 */
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 50%, #10b981 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.8px;
  position: relative;
  z-index: 2;
  animation: titleShimmer 3s ease-in-out infinite;
  text-shadow: 0 3px 15px rgba(59, 130, 246, 0.2);
}

@keyframes titleShimmer {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.hero-description {
  font-size: clamp(1.1rem, 2.5vw, 1.35rem);
  margin: 0 auto 20px;
  max-width: 700px;
  opacity: 0.9;
  color: #64748b;
  position: relative;
  z-index: 2;
  line-height: 1.7;
}

/* 输入区域 - 增强深度感 */
.input-section {
  position: relative;
  margin: 0 auto 40px;
  max-width: 850px;
  transition: all 0.3s ease;
  transform: translateZ(0);
}

.prompt-input {
  border-radius: 12px;
  /* 恢复蓝紫色边框 */
  border: 2px solid rgba(59, 130, 246, 0.15);
  font-size: 17px;
  padding: 25px 80px 25px 28px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  /* 恢复蓝紫色阴影 */
  box-shadow: 0 15px 50px rgba(59, 130, 246, 0.12);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  resize: none;
}

.prompt-input:focus {
  background: #fff;
  /* 恢复蓝紫色聚焦阴影 */
  box-shadow: 0 20px 60px rgba(59, 130, 246, 0.18);
  transform: translateY(-5px);
  outline: none;
  /* 恢复蓝紫色边框 */
  border-color: rgba(59, 130, 246, 0.3);
}

.prompt-input:hover:not(:focus) {
  /* 恢复蓝紫色悬停阴影 */
  box-shadow: 0 18px 55px rgba(59, 130, 246, 0.15);
  transform: translateY(-2px);
}

.input-actions {
  position: absolute;
  bottom: 18px;
  right: 18px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.input-actions .ant-btn {
  border-radius: 50%;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 恢复蓝紫色按钮渐变 */
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border: none;
  transition: all 0.3s ease;
  /* 恢复蓝紫色按钮阴影 */
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

.input-actions .ant-btn:hover {
  transform: translateY(-3px) scale(1.08);
  /* 恢复蓝紫色按钮悬停阴影 */
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.5);
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
}

.input-actions .ant-btn:active {
  transform: translateY(0) scale(0.95);
}

/* 快捷按钮 - 增强交互体验 */
.quick-actions {
  display: flex;
  gap: 18px;
  justify-content: center;
  margin-bottom: 90px;
  flex-wrap: wrap;
  padding: 0 15px;
}

.quick-actions .ant-btn {
  border-radius: 32px;
  padding: 12px 28px;
  height: auto;
  background: #fff;
  /* 恢复蓝紫色边框 */
  border: 1px solid rgba(59, 130, 246, 0.2);
  color: #475569;
  backdrop-filter: blur(15px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  font-size: 15px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.06);
  font-weight: 500;
}

.quick-actions .ant-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  /* 恢复蓝紫色光效 */
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.2), transparent);
  transition: left 0.5s ease;
}

.quick-actions .ant-btn:hover::before {
  left: 100%;
}

.quick-actions .ant-btn:hover {
  background: #fff;
  /* 恢复蓝紫色边框 */
  border-color: rgba(59, 130, 246, 0.4);
  /* 恢复蓝紫色文字 */
  color: #3b82f6;
  transform: translateY(-5px);
  /* 恢复蓝紫色阴影 */
  box-shadow: 0 12px 30px rgba(59, 130, 246, 0.25);
}

.quick-actions .ant-btn:active {
  transform: translateY(-2px);
}

/* 区域标题 - 增强视觉层次 */
.section {
  margin-bottom: 100px;
  position: relative;
  padding-top: 20px;
}

.section-title {
  font-size: clamp(1.8rem, 4vw, 2.3rem);
  font-weight: 700;
  margin-bottom: 40px;
  color: #1e293b;
  position: relative;
  display: inline-block;
  padding-bottom: 15px;
}

.section-title::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 70px;
  height: 5px;
  /* 恢复蓝紫色下划线 */
  background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 3px;
}

/* 应用网格 - 增强卡片布局 */
.app-grid, .featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 35px;
  margin-bottom: 50px;
}

/* 为卡片添加进入动画容器 */
.app-grid, .featured-grid {
  perspective: 1200px;
}

/* 分页样式优化 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 50px;
}

.custom-pagination .ant-pagination-item {
  border-radius: 8px;
  margin: 0 6px;
  transition: all 0.2s ease;
  /* 恢复蓝紫色边框 */
  border-color: rgba(59, 130, 246, 0.2);
}

.custom-pagination .ant-pagination-item-active {
  /* 恢复蓝紫色激活态 */
  background-color: #3b82f6;
  border-color: #3b82f6;
}

.custom-pagination .ant-pagination-item:hover:not(.ant-pagination-item-active) {
  transform: translateY(-3px);
  /* 恢复蓝紫色边框 */
  border-color: #3b82f6;
}

/* 响应式设计增强 */
@media (max-width: 1024px) {
  .app-grid, .featured-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 30px;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 100px 0 60px;
  }

  .hero-title {
    font-size: clamp(2.3rem, 6vw, 3.2rem);
  }

  .hero-description {
    font-size: 16px;
    padding: 0 15px;
  }

  .app-grid, .featured-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .quick-actions {
    justify-content: center;
    gap: 12px;
    margin-bottom: 70px;
  }

  .quick-actions .ant-btn {
    padding: 10px 22px;
    font-size: 14px;
  }

  .prompt-input {
    padding: 22px 70px 22px 24px;
    border-radius: 12px;
    font-size: 16px;
  }

  .input-actions .ant-btn {
    width: 48px;
    height: 48px;
  }

  .section {
    margin-bottom: 80px;
  }

  .section-title {
    font-size: 2rem;
    margin-bottom: 30px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 15px;
  }

  .hero-section {
    padding: 80px 0 50px;
  }

  .hero-title {
    font-size: 2.5rem;
  }

  .quick-actions {
    gap: 10px;
    margin-bottom: 60px;
  }

  .quick-actions .ant-btn {
    padding: 8px 18px;
    font-size: 13px;
  }

  .input-section {
    margin-bottom: 30px;
  }

  .prompt-input {
    padding: 20px 60px 20px 20px;
    font-size: 15px;
    border-radius: 10px;
  }

  .section-title {
    font-size: 1.8rem;
  }

  .section-title::after {
    width: 60px;
    height: 4px;
    background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);
  }

  /* 卡片样式补充 - 增强视觉层次 */
  .app-card {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(59, 130, 246, 0.1);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    transform-style: preserve-3d;
    transform: translateZ(0);
  }

  .app-card:hover {
    transform: translateY(-10px) rotateX(5deg);
    box-shadow: 0 20px 40px rgba(59, 130, 246, 0.18);
  }

  .app-card .card-image {
    height: 180px;
    overflow: hidden;
    position: relative;
  }

  .app-card .card-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s ease;
  }

  .app-card:hover .card-image img {
    transform: scale(1.08);
  }

  .app-card .card-content {
    padding: 24px;
  }

  .app-card .card-title {
    font-size: 1.25rem;
    font-weight: 600;
    margin-bottom: 12px;
    color: #1e293b;
    transition: color 0.3s ease;
  }

  .app-card:hover .card-title {
    color: #3b82f6;
  }

  .app-card .card-desc {
    color: #64748b;
    font-size: 0.95rem;
    line-height: 1.6;
    margin-bottom: 18px;
  }

  .app-card .card-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.85rem;
    color: #94a3b8;
  }

  .app-card .meta-tag {
    background: rgba(59, 130, 246, 0.1);
    color: #3b82f6;
    padding: 4px 10px;
    border-radius: 12px;
    font-weight: 500;
  }

  .app-card .card-actions {
    padding: 0 24px 24px;
  }

  .app-card .use-btn {
    width: 100%;
    padding: 12px 0;
    border-radius: 10px;
    background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
    border: none;
    color: white;
    font-weight: 500;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
  }

  .app-card .use-btn:hover {
    background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
  }

  /* 精选卡片特殊样式 */
  .featured-card .card-badge {
    position: absolute;
    top: 16px;
    right: 16px;
    background: #10b981;
    color: white;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: 500;
    z-index: 2;
    box-shadow: 0 3px 10px rgba(16, 185, 129, 0.3);
  }

  /* 功能特点区域样式 */
  .features-section {
    padding: 40px 0;
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 30px;
  }

  .feature-card {
    background: rgba(255, 255, 255, 0.9);
    border-radius: 16px;
    padding: 30px;
    box-shadow: 0 10px 30px rgba(59, 130, 246, 0.08);
    transition: all 0.3s ease;
  }

  .feature-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 15px 35px rgba(59, 130, 246, 0.15);
  }

  .feature-icon {
    width: 60px;
    height: 60px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    font-size: 1.5rem;
    color: white;
  }

  .feature-title {
    font-size: 1.25rem;
    font-weight: 600;
    margin-bottom: 12px;
    color: #1e293b;
  }

  .feature-desc {
    color: #64748b;
    line-height: 1.6;
    font-size: 0.95rem;
  }

  /* 行动召唤区域 */
  .cta-section {
    background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
    border-radius: 30px;
    padding: 80px 40px;
    margin: 60px 0 100px;
    text-align: center;
    position: relative;
    overflow: hidden;
  }

  .cta-section::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image:
      radial-gradient(circle at 20% 30%, rgba(255,255,255,0.1) 0%, transparent 40%),
      radial-gradient(circle at 80% 70%, rgba(255,255,255,0.1) 0%, transparent 40%);
  }

  .cta-content {
    position: relative;
    z-index: 2;
    max-width: 700px;
    margin: 0 auto;
  }

  .cta-title {
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    color: white;
    margin-bottom: 20px;
    font-weight: 700;
  }

  .cta-desc {
    font-size: 1.1rem;
    color: rgba(255, 255, 255, 0.85);
    margin-bottom: 30px;
    line-height: 1.6;
  }

  .cta-actions {
    display: flex;
    gap: 20px;
    justify-content: center;
    flex-wrap: wrap;
  }

  .cta-actions .start-btn {
    padding: 14px 36px;
    font-size: 1.1rem;
    border-radius: 12px;
    background: white;
    color: #3b82f6;
    font-weight: 600;
    border: none;
    transition: all 0.3s ease;
  }

  .cta-actions .start-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
    background: white;
    color: #3b82f6;
  }

  .cta-actions .demo-btn {
    padding: 14px 36px;
    font-size: 1.1rem;
    border-radius: 12px;
    background: transparent;
    color: white;
    font-weight: 600;
    border: 2px solid white;
    transition: all 0.3s ease;
  }

  .cta-actions .demo-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    background: rgba(255, 255, 255, 0.1);
    color: white;
  }

  /* 页脚样式 */
  .footer {
    margin-top: 120px;
    padding: 60px 0 30px;
    border-top: 1px solid rgba(59, 130, 246, 0.1);
  }

  .footer-top {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 40px;
    margin-bottom: 60px;
  }

  .footer-title {
    font-size: 1.5rem;
    font-weight: 700;
    background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 15px;
  }

  .footer-desc {
    color: #64748b;
    line-height: 1.6;
  }

  .footer-subtitle {
    font-size: 1rem;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 20px;
  }

  .footer-links {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .footer-links li {
    margin-bottom: 12px;
  }

  .footer-links a {
    color: #64748b;
    text-decoration: none;
    transition: color 0.3s ease;
  }

  .footer-links a:hover {
    color: #3b82f6;
  }

  .footer-bottom {
    text-align: center;
    color: #94a3b8;
    padding-top: 30px;
    border-top: 1px solid rgba(59, 130, 246, 0.05);
  }
}
</style>
