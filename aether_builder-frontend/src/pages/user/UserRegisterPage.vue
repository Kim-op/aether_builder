<template>
  <div id="userRegisterPage">
    <div class="form-container">
      <h2 class="title">构建者苏苏 - 用户注册</h2>
      <div class="desc">不写一行代码，生成完整应用</div>
      <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
        <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
          <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
        </a-form-item>
        <a-form-item name="userPassword" :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码不能小于 8 位' },
        ]">
          <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
        </a-form-item>
        <a-form-item name="checkPassword" :rules="[
          { required: true, message: '请确认密码' },
          { min: 8, message: '密码不能小于 8 位' },
          { validator: validateCheckPassword },
        ]">
          <a-input-password v-model:value="formState.checkPassword" placeholder="请确认密码" />
        </a-form-item>
        <div class="tips">
          已有账号？
          <RouterLink to="/user/login">去登录</RouterLink>
        </div>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { userRegister } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import { reactive } from 'vue'

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
/* 设置 html 和 body 元素高度为 100%，确保没有默认边距 */
:deep(html),
:deep(body) {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
  /* 防止页面滚动 */
}

/* 外层容器设置，确保覆盖整个页面 */
:deep(#app) {
  height: 100%;
}

#userRegisterPage {
  /* 背景图片设置 - 修正为使用实际存在的 background.png */
  background-image: url('@/assets/background.png');
  background-size: cover;
  /* 覆盖整个容器 */
  background-position: center;
  /* 居中显示 */
  background-repeat: no-repeat;
  /* 不重复 */
  background-attachment: fixed;
  /* 固定背景图片，防止滚动时移动 */
  height: 100vh;
  /* 强制覆盖整个视口高度 */
  width: 100vw;
  /* 强制覆盖整个视口宽度 */
  display: flex;
  /* 使用flex布局 */
  flex-direction: column;
  /* 垂直排列 */
  justify-content: center;
  /* 垂直居中 */
  align-items: center;
  /* 水平居中 */
  padding: 24px;
  box-sizing: border-box;
  /* 确保padding不影响整体尺寸 */
  position: relative;
  /* 为内容容器提供定位参考 */
}

/* 表单容器，保持原有样式但增加半透明背景提高可读性 */
.form-container {
  background-color: rgba(255, 255, 255, 0.85);
  /* 白色半透明背景 */
  max-width: 720px;
  width: 100%;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 1;
  /* 确保在背景之上 */
}

.title {
  text-align: center;
  margin-bottom: 16px;
  color: #e91e63;
  /* 玫红色 */
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}

/* 覆盖 Ant Design 按钮的主色调 */
.ant-btn-primary {
  background-color: #e91e63 !important;
  border-color: #e91e63 !important;
}

.ant-btn-primary:hover {
  background-color: #d81b60 !important;
  border-color: #d81b60 !important;
}

/* 链接颜色改为玫红色 */
.tips a {
  color: #e91e63 !important;
}
</style>
