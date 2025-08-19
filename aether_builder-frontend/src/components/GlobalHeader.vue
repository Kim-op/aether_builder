<template>
  <a-layout-header class="header">
    <div class="header-container">
      <!-- 左侧：Logo和标题 -->
      <RouterLink to="/" class="logo-link">
        <div class="header-left">
          <div class="logo-container">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
          </div>
          <h1 class="site-title">构建者苏苏</h1>
        </div>
      </RouterLink>

      <!-- 中间：导航菜单 -->
      <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" :items="menuItems" @click="handleMenuClick"
        class="main-menu" />

      <!-- 右侧：用户操作区域 -->
      <div class="user-login-status">
        <div v-if="loginUserStore.loginUser.id" class="user-menu">
          <a-dropdown>
            <a-space class="user-info">
              <a-avatar :src="loginUserStore.loginUser.userAvatar" class="user-avatar" />
              <span class="user-name">{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
            </a-space>
            <template #overlay>
              <a-menu class="dropdown-menu">
                <a-menu-item @click="doLogout" class="menu-item">
                  <LogoutOutlined class="menu-icon" />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else>
          <a-button type="primary" href="/user/login" class="login-btn">登录</a-button>
        </div>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: '/admin/chatManage',
    label: '对话管理',
    title: '对话管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/Kim-op/aether_builder', target: '_blank' }, 'GitHub'),
    title: 'GitHub',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
.header {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: 0 32px;
  box-shadow: 0 2px 30px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
}

.header:hover {
  box-shadow: 0 4px 35px rgba(0, 0, 0, 0.08);
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.logo-link {
  text-decoration: none;
  display: flex;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s ease;
}

.header-left:hover {
  transform: translateY(-2px);
}

.logo-container {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo-container:hover {
  box-shadow: 0 6px 25px rgba(59, 130, 246, 0.25);
  transform: rotate(5deg) scale(1.05);
}

.logo {
  height: 100%;
  width: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.logo-container:hover .logo {
  transform: scale(1.1);
}

.logo-container::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, rgba(59, 130, 246, 0.2) 0%, transparent 70%);
  pointer-events: none;
}

.site-title {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 700;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 0.5px;
  position: relative;
}

.site-title::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);
  transition: width 0.3s ease;
}

.header-left:hover .site-title::after {
  width: 100%;
}

.main-menu {
  flex: 1;
  margin: 0 40px;
  justify-content: center;
}

/* 菜单样式优化 */
.ant-menu-horizontal {
  border-bottom: none !important;
  height: 100%;
  align-items: center;
}

.ant-menu-item {
  margin: 0 12px;
  padding: 0 20px;
  height: 44px;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  font-size: 15px;
  position: relative;
  overflow: hidden;
  color: #475569;
}

.ant-menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
  transform-origin: right center;
  z-index: -1;
}

.ant-menu-item:hover::before {
  transform: scaleX(1);
  transform-origin: left center;
}

.ant-menu-item:hover {
  color: #3b82f6 !important;
  transform: translateY(-3px);
}

.ant-menu-item-selected {
  color: #3b82f6 !important;
  font-weight: 600;
}

.ant-menu-item-selected::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 3px 3px 0 0;
}

/* 用户区域样式 */
.user-login-status {
  display: flex;
  align-items: center;
  margin-left: 15px;
}

.user-menu {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 24px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.user-info::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.user-info:hover::after {
  opacity: 1;
}

.user-avatar {
  width: 38px;
  height: 38px;
  border: 2px solid transparent;
  box-shadow: 0 2px 10px rgba(59, 130, 246, 0.1);
  transition: all 0.3s ease;
  background-color: #f1f5f9;
}

.user-info:hover .user-avatar {
  border-color: rgba(59, 130, 246, 0.2);
  transform: translateY(-3px);
}

/* 用户名样式 */
.user-name {
  margin-left: 10px;
  font-size: 15px;
  color: #7d449b;
  transition: color 0.3s ease;
  font-weight: 600;
  font-family: "Comic Sans MS", "Bubblegum Sans", "Marker Felt", "Arial Rounded MT Bold", sans-serif;
  -webkit-font-smoothing: antialiased;
}

.user-info:hover .user-name {
  color: #3b82f6;
}

/* 下拉菜单样式 */
.dropdown-menu {
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(226, 232, 240, 0.6);
  overflow: hidden;
  padding: 5px 0;
  margin-top: 8px !important;
  background-color: rgba(255, 255, 255, 0.95) !important;
  backdrop-filter: blur(10px);
}

.menu-item {
  transition: all 0.3s ease;
  padding: 10px 20px;
  font-size: 14px;
  color: #475569;
}

.menu-item:hover {
  background-color: rgba(59, 130, 246, 0.08) !important;
  color: #3b82f6 !important;
  padding-left: 25px;
}

.menu-icon {
  margin-right: 10px;
  font-size: 16px;
}

/* 登录按钮样式 - 解决文字居中居中问题 */
.login-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border: none;
  border-radius: 24px;
  padding: 0 24px; /* 左右内边距保持一致 */
  height: 44px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.25);
  color: white;

  /* 确保文字垂直居中 */
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1; /* 重置除行高影响 */
  text-align: center;
}

.login-btn:hover {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.35);
  color: white;
}

.login-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* 响应式设计 */
@media (max-width: 992px) {
  .main-menu {
    margin: 0 20px;
  }

  .ant-menu-item {
    margin: 0 8px;
    padding: 0 16px;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0 16px;
    height: 64px;
  }

  .header-container {
    height: 64px;
  }

  .main-menu {
    display: none;
  }

  .site-title {
    font-size: 1.2rem;
  }

  .logo-container {
    width: 42px;
    height: 42px;
  }

  .header-left {
    gap: 12px;
  }

  .login-btn {
    padding: 0 18px;
    height: 40px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .user-name {
    display: none;
  }
}
</style>
