<template>
  <el-container class="layout-container">
    <el-aside width="220px">
      <div class="logo">图书管理系统</div>
      <el-menu
        router
        :default-active="$route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>系统概览</span>
        </el-menu-item>

        <el-menu-item index="/books">
          <el-icon><Notebook /></el-icon>
          <span>图书查询</span>
        </el-menu-item>

        <el-menu-item v-if="role === 'admin'" index="/book-manage">
          <el-icon><Management /></el-icon>
          <span>图书管理</span>
        </el-menu-item>

        <el-menu-item index="/borrow">
          <el-icon><Collection /></el-icon>
          <span>借阅管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">欢迎回来，{{ role === 'admin' ? '管理员' : '学生' }}</div>
        <div class="header-right">
          <el-button type="danger" link @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-main> <router-view /> </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { Odometer, Notebook, Management, Collection } from '@element-plus/icons-vue'

const router = useRouter()
const role = localStorage.getItem('role') // 从本地读取角色

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示').then(() => {
    localStorage.clear() // 清除 Token 和角色信息
    router.push('/login')
  })
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.el-aside {
  background-color: #304156;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b2f3a;
}
.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
