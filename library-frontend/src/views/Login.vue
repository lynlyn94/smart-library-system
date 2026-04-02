<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">智慧图书馆管理系统</h2>
      </template>

      <el-form :model="loginForm" :rules="rules" ref="loginRef">
        <el-form-item>
          <el-radio-group v-model="loginForm.role" class="role-group">
            <el-radio-button label="student">学生登录</el-radio-button>
            <el-radio-button label="admin">管理员登录</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入学号/工号" prefix-icon="User" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-button type="primary" class="login-button" @click="handleLogin"> 立即登录 </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '../utils/request' // 引入我们之前写的拦截器

const router = useRouter()
const loginRef = ref()

const loginForm = reactive({
  username: '',
  password: '',
  role: 'student',
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  await loginRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        // 调用后端 AuthController 的 /api/auth/login 接口
        const res: any = await request.post('/api/auth/login', loginForm)

        // 登录成功，保存 Token 到本地
        localStorage.setItem('token', res.data)
        // 保存角色，用于权限判断
        localStorage.setItem('role', loginForm.role)
        // 保存用户ID(学号/工号)，用于后续借书等需要身份标识的操作
        localStorage.setItem('userId', loginForm.username)

        ElMessage.success('登录成功！')

        // 【核心修改】根据角色分别跳转到不同的专属通道
        if (loginForm.role === 'admin') {
          router.push('/admin/dashboard')
        } else {
          router.push('/student/home')
        }
      } catch (error) {
        console.error('登录失败', error)
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  border-radius: 12px;
}
.login-title {
  text-align: center;
  color: #409eff;
  margin: 0;
}
.role-group {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
}
.login-button {
  width: 100%;
  margin-top: 20px;
}
</style>
