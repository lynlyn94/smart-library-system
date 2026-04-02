<template>
  <el-container class="admin-layout">
    <el-aside width="260px" class="material-aside">
      <div class="logo-container">
        <div class="logo-icon">
          <el-icon><Reading /></el-icon>
        </div>
        <span class="logo-text">Admin Workspace</span>
      </div>

      <el-menu :default-active="$route.path" router class="material-menu" :collapse="false">
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>系统概览</span>
        </el-menu-item>

        <el-menu-item index="/admin/books">
          <el-icon><Notebook /></el-icon>
          <span>图书管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>读者管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/borrows">
          <el-icon><List /></el-icon>
          <span>借阅流转</span>
        </el-menu-item>

        <el-menu-item index="/admin/fines">
          <el-icon><Money /></el-icon>
          <span>罚款管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/reservations">
          <el-icon><AlarmClock /></el-icon>
          <span>预约管理</span>
        </el-menu-item>

        <el-menu-item index="/admin/reviews">
          <el-icon><ChatDotSquare /></el-icon>
          <span>评价审核</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <el-header class="material-header">
        <div class="header-left"></div>

        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-profile">
              <el-avatar :size="36" class="avatar">A</el-avatar>
              <div class="user-info">
                <span class="user-name">超级管理员</span>
                <span class="user-role">Admin</span>
              </div>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="material-dropdown">
                <el-dropdown-item command="profile">
                  <el-icon><Setting /></el-icon>个人设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" class="logout-item">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="material-main">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
// 【核心修复】：在这里补全了所有需要的图标组件引入！
import {
  Reading,
  DataLine,
  Notebook,
  User,
  List,
  Money,
  AlarmClock,
  ChatDotSquare,
  ArrowDown,
  Setting,
  SwitchButton,
} from '@element-plus/icons-vue'

const router = useRouter()

const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出当前管理员账号吗？', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'material-message-box',
    })
      .then(() => {
        localStorage.clear()
        router.push('/login')
      })
      .catch(() => {})
  }
}
</script>

<style scoped>
/* =========== 核心布局变量 =========== */
:root {
  --md-primary: #1a73e8; /* Google 蓝 */
  --md-surface: #ffffff;
  --md-background: #f8f9fa; /* 极浅的灰，用于主背景 */
  --md-text-primary: #202124;
  --md-text-secondary: #5f6368;
}

.admin-layout {
  height: 100vh;
  background-color: #f8f9fa;
  font-family: 'Roboto', 'Helvetica Neue', Arial, sans-serif;
}

/* =========== 侧边栏 (MD Navigation Drawer) =========== */
.material-aside {
  background-color: #ffffff;
  border-right: none;
  box-shadow: 1px 0 10px rgba(0, 0, 0, 0.03); /* 极弱的右侧阴影 */
  z-index: 10;
  display: flex;
  flex-direction: column;
}

.logo-container {
  height: 80px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 12px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background-color: #e8f0fe;
  color: #1a73e8;
  border-radius: 8px; /* 谷歌偏爱的圆角 */
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #202124;
  letter-spacing: 0.5px;
}

/* 侧边菜单魔改：药丸形高亮 (Pill Shape) */
.material-menu {
  border-right: none;
  padding: 12px;
}

.material-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  border-radius: 25px; /* 药丸形的核心 */
  margin-bottom: 8px;
  color: #5f6368;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.material-menu :deep(.el-menu-item:hover) {
  background-color: #f1f3f4;
  color: #202124;
}

.material-menu :deep(.el-menu-item.is-active) {
  background-color: #e8f0fe; /* 极浅的蓝色底 */
  color: #1a73e8; /* 谷歌蓝字 */
  font-weight: 600;
}

.material-menu :deep(.el-icon) {
  font-size: 20px;
  margin-right: 16px;
}

/* =========== 顶栏 (MD App Bar) =========== */
.material-header {
  background-color: #ffffff;
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05); /* 底部微阴影 */
  z-index: 9;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 24px;
  transition: background-color 0.2s;
}

.user-profile:hover {
  background-color: #f1f3f4;
}

.avatar {
  background-color: #1a73e8;
  color: white;
  font-weight: bold;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #202124;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #80868b;
}

/* =========== 内容主画板 =========== */
.material-main {
  padding: 24px 32px;
  overflow-x: hidden;
}

.content-wrapper {
  height: 100%;
}

/* =========== 路由切换丝滑动画 =========== */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(15px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(-15px);
}

/* 退出登录标红 */
.logout-item {
  color: #d93025 !important;
}
</style>
