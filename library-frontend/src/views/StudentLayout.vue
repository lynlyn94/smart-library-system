<template>
  <div class="student-layout">
    <el-header class="material-top-bar">
      <div class="nav-container">
        <div class="brand-area" @click="router.push('/student/home')">
          <div class="logo-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <span class="brand-text">智慧图书馆 <span class="badge">Reader</span></span>
        </div>

        <el-menu
          :default-active="$route.path"
          mode="horizontal"
          router
          class="material-nav-menu"
          :ellipsis="false"
        >
          <el-menu-item index="/student/home">
            <el-icon><Compass /></el-icon>图书大厅
          </el-menu-item>
          <el-menu-item index="/student/borrows">
            <el-icon><Collection /></el-icon>我的借阅
          </el-menu-item>
        </el-menu>

        <div class="user-area">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-profile">
              <el-avatar :size="36" class="avatar">{{ userId.charAt(0).toUpperCase() }}</el-avatar>
              <span class="user-id">学号: {{ userId }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="material-dropdown">
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" class="logout-item">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-main class="material-main">
      <div class="main-content-wrapper">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Reading,
  Compass,
  Collection,
  User,
  ArrowDown,
  SwitchButton,
} from '@element-plus/icons-vue'

const router = useRouter()
const userId = ref('User')

onMounted(() => {
  // 从本地存储获取登录时保存的学号
  const storedId = localStorage.getItem('userId')
  if (storedId) {
    userId.value = storedId
  }
})

const handleCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/student/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
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
.student-layout {
  min-height: 100vh;
  background-color: #f8f9fa; /* 极浅的灰色背景，让白色卡片更突出 */
  display: flex;
  flex-direction: column;
}

/* =========== 顶部导航栏 =========== */
.material-top-bar {
  background-color: #ffffff;
  height: 64px;
  padding: 0;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

/* Logo 区域 */
.brand-area {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #1a73e8, #4285f4);
  color: white;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  box-shadow: 0 2px 4px rgba(26, 115, 232, 0.3);
}
.brand-text {
  font-size: 18px;
  font-weight: 700;
  color: #202124;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.badge {
  background-color: #e8f0fe;
  color: #1a73e8;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 600;
}

/* 菜单导航 */
.material-nav-menu {
  flex-grow: 1;
  justify-content: center;
  border-bottom: none;
  height: 64px;
  background-color: transparent;
}
.material-nav-menu :deep(.el-menu-item) {
  font-size: 15px;
  font-weight: 500;
  color: #5f6368;
  padding: 0 24px;
  transition: all 0.3s ease;
}
.material-nav-menu :deep(.el-menu-item:hover) {
  color: #1a73e8;
  background-color: transparent;
}
.material-nav-menu :deep(.el-menu-item.is-active) {
  color: #1a73e8;
  border-bottom: 3px solid #1a73e8;
  background-color: transparent !important;
}

/* 用户区域 */
.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 24px;
  transition: background-color 0.2s;
}
.user-profile:hover {
  background-color: #f1f3f4;
}
.avatar {
  background-color: #1a73e8;
  font-weight: bold;
  font-size: 16px;
}
.user-id {
  font-size: 14px;
  font-weight: 500;
  color: #3c4043;
}
.arrow-icon {
  color: #80868b;
  font-size: 12px;
}

/* =========== 主内容区 =========== */
.material-main {
  padding: 32px 20px;
  flex-grow: 1;
}
.main-content-wrapper {
  max-width: 1200px; /* 限制最大宽度，保证在大屏幕上也不会显得太空旷 */
  margin: 0 auto;
  width: 100%;
}

/* 退出登录标红 */
.logout-item {
  color: #ea4335 !important;
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(15px);
}
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-15px);
}
</style>
