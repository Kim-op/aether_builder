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

  /* 增强视觉深度的背景设置 */
  background-image: url('@/assets/background.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

/* 动态背景光效 - 增强视觉冲击力 */
#userRegisterPage::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 60% 30%, rgba(59, 130, 246, 0.25) 0%, transparent 40%),
    radial-gradient(circle at 20% 70%, rgba(139, 92, 246, 0.2) 0%, transparent 40%);
  z-index: 0;
  animation: backgroundShift 18s ease-in-out infinite alternate;
}

@keyframes backgroundShift {
  0% {
    transform: translate(0, 0);
  }

  100% {
    transform: translate(-10px, 10px);
  }
}

/* 背景遮罩层 - 提升整体可读性 */
.bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  z-index: 0;
}

/* 表单容器样式 - 增强玻璃态效果 */
.form-container {
  background-color: rgba(255, 255, 255, 0);
  width: 100%;
  max-width: 420px;
  padding: 40px 40px;
  border-radius: 20px;
  box-shadow: 0 15px 40px -5px rgba(0, 0, 0, 0.2);
  z-index: 1;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  transform-style: preserve-3d;
  transform: perspective(1000px) translateZ(0);
  max-height: calc(100vh - 80px);
  overflow-y: auto;
}

.form-container:hover {
  box-shadow: 0 20px 50px -5px rgba(0, 0, 0, 0.25);
  transform: perspective(1000px) translateZ(5px) translateY(-5px);
  background-color: rgba(255, 255, 255, 0.85);
}

/* 品牌标识样式 - 增强视觉焦点 */
.brand-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  position: relative;
}

.logo-container {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.3);
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.logo-container::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 50%;
  z-index: -1;
  animation: logoPulse 2s ease-in-out infinite alternate;
}

@keyframes logoPulse {
  0% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.7;
  }

  100% {
    transform: translate(-50%, -50%) scale(1.1);
    opacity: 0.9;
  }
}

.logo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 标题与描述样式 - 增强层次感 */
.title {
  text-align: center;
  margin-bottom: 12px;
  color: #1e293b;
  font-size: 28px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.8);
  position: relative;
  display: inline-block;
  left: 50%;
  transform: translateX(-50%);
}

.title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);
  border-radius: 2px;
}

.desc {
  text-align: center;
  color: #475569;
  margin-bottom: 36px;
  font-size: 15px;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  line-height: 1.5;
}

/* 表单样式 - 增强交互体验 */
.register-form {
  width: 100%;
}

.form-item {
  margin-bottom: 20px !important;
  position: relative;
}

.form-item::before {
  content: '';
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  z-index: 2;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.form-item:focus-within::before {
  opacity: 1;
}

.form-input {
  border-radius: 10px !important;
  border-color: rgba(229, 230, 235, 0.9) !important;
  background-color: rgba(255, 255, 255, 0.9) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  height: 52px !important;
  padding-left: 16px !important;
  font-size: 15px !important;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05) !important;
}

.form-input:focus {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2) !important;
  background-color: rgba(255, 255, 255, 1) !important;
  transform: translateY(-2px);
}

/* 验证码区域特殊样式 */
.captcha-group {
  display: flex;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-btn {
  width: 120px !important;
  height: 52px !important;
  border-radius: 10px !important;
  background-color: rgba(59, 130, 246, 0.1) !important;
  color: #3b82f6 !important;
  border-color: rgba(59, 130, 246, 0.2) !important;
  transition: all 0.3s ease !important;
  font-size: 14px !important;
}

.captcha-btn:hover {
  background-color: rgba(59, 130, 246, 0.2) !important;
  border-color: rgba(59, 130, 246, 0.3) !important;
}

.captcha-btn:disabled {
  opacity: 0.7 !important;
  cursor: not-allowed !important;
  transform: none !important;
  box-shadow: none !important;
}

/* 同意条款区域 */
.agree-terms {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 10px 0 24px;
  font-size: 14px;
  color: #64748b;
}

.agree-checkbox {
  width: 16px !important;
  height: 16px !important;
  border-color: #94a3b8 !important;
}

.agree-checkbox:checked {
  background-color: #3b82f6 !important;
  border-color: #3b82f6 !important;
}

.term-link {
  color: #3b82f6 !important;
  text-decoration: none !important;
  transition: all 0.2s ease;
}

.term-link:hover {
  color: #2563eb !important;
  text-decoration: underline !important;
}

/* 提示文字与链接样式 - 增强交互反馈 */
.tips {
  margin-bottom: 28px;
  color: #475569;
  font-size: 14px;
  text-align: right;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.8);
}

.link {
  color: #3b82f6 !important;
  text-decoration: none !important;
  font-weight: 600;
  transition: all 0.2s ease;
  position: relative;
  padding: 2px 0;
}

.link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #3b82f6;
  transition: width 0.2s ease;
}

.link:hover {
  color: #2563eb !important;
}

.link:hover::after {
  width: 100%;
}

/* 提交按钮样式 - 增强视觉吸引力 */
.submit-item {
  margin-bottom: 0 !important;
  margin-top: 12px !important;
}

.submit-btn {
  width: 100% !important;
  height: 54px !important;
  font-size: 16px !important;
  border-radius: 10px !important;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%) !important;
  border: none !important;
  color: white !important;
  font-weight: 600 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3) !important;
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s ease;
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:hover,
.submit-btn:focus {
  background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%) !important;
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4) !important;
}

.submit-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3) !important;
}

/* 切换登录选项 */
.switch-login {
  text-align: center;
  margin-top: 24px;
  color: #475569;
  font-size: 14px;
}

/* 页脚样式 - 增强品牌一致性 */
.page-footer {
  margin-top: 40px;
  color: #475569;
  font-size: 13px;
  z-index: 1;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
  text-align: center;
  line-height: 1.6;
}

/* 响应式调整 - 确保多设备体验一致 */
@media (max-width: 480px) {
  .form-container {
    padding: 32px 24px;
    margin: 0 16px;
    border-radius: 16px;
  }

  .title {
    font-size: 24px;
  }

  .desc {
    font-size: 14px;
    margin-bottom: 30px;
  }

  .form-input,
  .captcha-btn {
    height: 48px !important;
  }

  .captcha-btn {
    width: 100px !important;
    font-size: 13px !important;
  }

  .submit-btn {
    height: 50px !important;
    font-size: 15px !important;
  }

  .agree-terms {
    font-size: 13px;
  }
}
</style>
