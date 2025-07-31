<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <h1 class="site-title">以太构建者</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" :items="menuItems" @click="handleMenuClick" />
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { MenuProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { LogoutOutlined } from '@ant-design/icons-vue'
import { userLogout } from '@/api/userController.ts'

// 获取登录用户状态
const loginUserStore = useLoginUserStore()

const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    label: '首页',
    title: '首页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/Kim-op', target: '_blank' }, 'GitHub by Cammy'),
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
    message.error('退出登录失败，' + res.data.msg)
  }
}
</script>

<style scoped>
.header {
  background: #1e1e1e;
  padding: 0 24px;
  border-bottom: 1px solid #333333;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  position: relative;
}

.header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg, transparent, #1890ff, transparent);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 48px;
  width: 48px;
  filter: drop-shadow(0 0 5px rgba(24, 144, 255, 0.5));
}

.site-title {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  color: transparent;
  background-image: linear-gradient(90deg, #1890ff, #00e676);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 15px rgba(24, 144, 255, 0.7);
}

.ant-menu-horizontal {
  border-bottom: none !important;
  background: transparent !important;
}

/* 菜单项样式增强 - 现代科技风格 */
:deep(.ant-menu-horizontal .ant-menu-item) {
  color: #e0e0e0 !important;
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 0.5px;
  text-shadow: 0 0 5px rgba(224, 224, 224, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.ant-menu-title-content) {
  color: inherit !important;
}

:deep(.ant-menu-item:hover) {
  color: #61dafb !important;
  text-shadow: 0 0 10px rgba(97, 218, 251, 0.7);
  transform: translateY(-1px);
}

:deep(.ant-menu-item-selected) {
  color: #1890ff !important;
  background-color: rgba(24, 144, 255, 0.1) !important;
  text-shadow: 0 0 15px rgba(24, 144, 255, 0.9);
  font-weight: 700;
  border-bottom: 2px solid #1890ff;
}

/* 登录按钮样式 */
.ant-btn-primary {
  background-color: #1890ff !important;
  border-color: #1890ff !important;
  box-shadow: 0 0 10px rgba(24, 144, 255, 0.5) !important;
  transition: all 0.3s ease;
}

.ant-btn-primary:hover {
  background-color: #40a9ff !important;
  border-color: #40a9ff !important;
  box-shadow: 0 0 15px rgba(64, 169, 255, 0.7) !important;
}

/* 用户名称样式 */
.ant-space {
  color: #e0e0e0 !important;
  font-weight: 600;
  transition: all 0.3s ease;
}

.ant-space:hover {
  color: #61dafb !important;
  text-shadow: 0 0 10px rgba(97, 218, 251, 0.7);
}
</style>
