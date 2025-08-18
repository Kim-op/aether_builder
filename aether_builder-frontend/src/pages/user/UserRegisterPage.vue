<template>
  <div id="userRegisterPage">
    <!-- 半透明遮罩层，提升整体可读性 -->
    <div class="bg-overlay"></div>

    <div class="form-container">
      <!-- 品牌标识区域 - 使用指定路径的头像 -->
      <div class="brand-logo">
        <div class="logo-container">
          <!-- 这里可以替换为您指定的头像路径 -->
          <img :src="avatarPath" alt="构建者苏苏Logo" class="logo-image" />
        </div>
      </div>

      <h2 class="title">构建者苏苏 - 用户注册</h2>
      <div class="desc">不写一行代码，生成完整应用</div>

      <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit" class="register-form">
        <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]" class="form-item">
          <a-input v-model:value="formState.userAccount" placeholder="请输入账号" size="large" class="form-input"
            prefix-icon="user" />
        </a-form-item>

        <a-form-item name="userPassword" :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码不能小于 8 位' },
        ]" class="form-item">
          <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" size="large" class="form-input"
            prefix-icon="lock" />
        </a-form-item>

        <a-form-item name="checkPassword" :rules="[
          { required: true, message: '请确认密码' },
          { min: 8, message: '密码不能小于 8 位' },
          { validator: validateCheckPassword },
        ]" class="form-item">
          <a-input-password v-model:value="formState.checkPassword" placeholder="请确认密码" size="large" class="form-input"
            prefix-icon="check-circle" />
        </a-form-item>

        <div class="tips">
          已有账号？
          <RouterLink to="/user/login" class="link">去登录</RouterLink>
        </div>

        <a-form-item class="submit-item">
          <a-button type="primary" html-type="submit" size="large" class="submit-btn">
            注册
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 页脚信息 -->
    <div class="page-footer">
      <p>© 2025 构建者苏苏. 保留所有权利</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { userRegister } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import { reactive, ref } from 'vue'

// 头像路径 - 您可以在这里指定具体的头像路径
const avatarPath = ref('/src/assets/avatar.png') // 默认路径，可根据需要修改

const router = useRouter()

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

/**
 * 验证确认密码
 * @param rule
 * @param value
 * @param callback
 */
const validateCheckPassword = (rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value && value !== formState.userPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: API.UserRegisterRequest) => {
  const res = await userRegister(values)
  // 注册成功，跳转到登录页面
  if (res.data.code === 0) {
    message.success('注册成功')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('注册失败，' + res.data.message)
  }
}
</script>

<style scoped>
/* 基础样式重置与设置 */
:deep(html),
:deep(body) {
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

:deep(#app) {
  height: 100%;
}

#userRegisterPage {
  min-height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 24px;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;

  /* 保留原始背景图片设置 */
  background-image: url('@/assets/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

/* 背景遮罩层 - 提升整体可读性 */
.bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.2);
  z-index: 0;
}

/* 表单容器样式 - 调整为50%透明度 */
.form-container {
  background-color: rgba(255, 255, 255, 0.001);
  /* 50%透明度 */
  width: 100%;
  max-width: 420px;
  padding: 36px 40px;
  border-radius: 12px;
  box-shadow: 0 10px 30px -5px rgba(0, 0, 0, 0.25);
  /* 增强阴影 */
  z-index: 1;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  /* 增强模糊效果 */
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  /* 增加边框提升轮廓感 */
}

.form-container:hover {
  box-shadow: 0 15px 35px -5px rgba(0, 0, 0, 0.3);
  transform: translateY(-2px);
  background-color: rgba(255, 255, 255, 0.55);
  /*  hover时略微提高不透明度 */
}

/* 品牌标识样式 - 头像设置 */
.brand-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.logo-container {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 标题与描述样式 - 加深颜色确保可读性 */
.title {
  text-align: center;
  margin-bottom: 8px;
  color: #111;
  font-size: 24px;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

.desc {
  text-align: center;
  color: #222;
  margin-bottom: 32px;
  font-size: 14px;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.8);
}

/* 表单样式 - 提高输入框不透明度确保内容清晰 */
.register-form {
  width: 100%;
}

.form-item {
  margin-bottom: 16px !important;
}

.form-input {
  border-radius: 8px !important;
  border-color: rgba(229, 230, 235, 0.9) !important;
  background-color: rgba(255, 255, 255, 0.9) !important;
  /* 提高输入框不透明度 */
  transition: all 0.2s ease !important;
}

.form-input:focus {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2) !important;
  background-color: rgba(255, 255, 255, 0.95) !important;
}

/* 提示文字与链接样式 - 加深颜色 */
.tips {
  margin-bottom: 24px;
  color: #222;
  font-size: 14px;
  text-align: right;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.8);
}

.link {
  color: #1d4ed8 !important;
  text-decoration: none !important;
  font-weight: 600;
  transition: color 0.2s ease;
}

.link:hover {
  color: #1e40af !important;
  text-decoration: underline !important;
}

/* 提交按钮样式 */
.submit-item {
  margin-bottom: 0 !important;
}

.submit-btn {
  width: 100% !important;
  height: 48px !important;
  font-size: 16px !important;
  border-radius: 8px !important;
  background-color: rgba(59, 130, 246, 0.95) !important;
  /* 提高按钮不透明度 */
  border-color: rgba(59, 130, 246, 0.95) !important;
  transition: all 0.2s ease !important;
}

.submit-btn:hover,
.submit-btn:focus {
  background-color: #2563eb !important;
  border-color: #2563eb !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4) !important;
}

.submit-btn:active {
  transform: translateY(0);
}

/* 页脚样式 */
.page-footer {
  margin-top: 32px;
  color: #1a1a1a;
  font-size: 12px;
  z-index: 1;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

/* 响应式调整 */
@media (max-width: 480px) {
  .form-container {
    padding: 28px 24px;
    margin: 0 16px;
  }

  .title {
    font-size: 20px;
  }

  .submit-btn {
    height: 44px !important;
    font-size: 15px !important;
  }
}
</style>
