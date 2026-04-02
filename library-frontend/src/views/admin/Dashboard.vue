<template>
  <div class="dashboard-container" v-loading="loading">
    <el-row :gutter="24">
      <el-col :span="6" v-for="(item, index) in statCards" :key="index">
        <el-card shadow="hover" class="material-card stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-header-row">
                <span class="stat-title">{{ item.title }}</span>
                <el-dropdown trigger="click" @command="handleCommand($event, item.route)">
                  <el-icon class="more-icon"><MoreFilled /></el-icon>
                  <template #dropdown>
                    <el-dropdown-menu class="material-dropdown">
                      <el-dropdown-item command="view"
                        ><el-icon><View /></el-icon>查看明细</el-dropdown-item
                      >
                      <el-dropdown-item command="export"
                        ><el-icon><Download /></el-icon>导出报表</el-dropdown-item
                      >
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
              <div class="stat-value">{{ item.value }}</div>
            </div>
            <div class="stat-icon-wrapper" :style="{ backgroundColor: item.bgColor }">
              <el-icon :style="{ color: item.color }" class="stat-icon">
                <component :is="item.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="content-row">
      <el-col :span="8">
        <el-card shadow="hover" class="material-card chart-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">资源状态分布</span>
            </div>
          </template>
          <div class="progress-list">
            <div class="progress-item">
              <div class="progress-label-row">
                <span>在馆可借 (Available)</span>
                <span>{{ dbData.bookStatus?.available || 0 }} 本</span>
              </div>
              <el-progress
                :text-inside="true"
                :stroke-width="20"
                :percentage="getPercent('available')"
                status="success"
              />
            </div>
            <div class="progress-item">
              <div class="progress-label-row">
                <span>已被借出 (Borrowed)</span>
                <span>{{ dbData.bookStatus?.borrowed || 0 }} 本</span>
              </div>
              <el-progress
                :text-inside="true"
                :stroke-width="20"
                :percentage="getPercent('borrowed')"
              />
            </div>
            <div class="progress-item">
              <div class="progress-label-row">
                <span>超期未还 (Overdue)</span>
                <span>{{ dbData.bookStatus?.overdue || 0 }} 本</span>
              </div>
              <el-progress
                :text-inside="true"
                :stroke-width="20"
                :percentage="getPercent('overdue')"
                status="exception"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="material-card alert-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">待办事项与预警</span>
            </div>
          </template>
          <div class="alert-list">
            <el-alert
              v-if="dbData.unpaidFines > 0"
              :title="`有 ${dbData.unpaidFines} 笔违约罚金待处理`"
              type="error"
              show-icon
              :closable="false"
              class="custom-alert"
              @click="$router.push('/admin/fines')"
            />
            <el-alert
              v-if="dbData.overdueAlerts > 0"
              :title="`发现 ${dbData.overdueAlerts} 本图书已超期`"
              type="warning"
              show-icon
              :closable="false"
              class="custom-alert"
              @click="$router.push('/admin/borrows')"
            />
            <el-alert
              title="暂无新的预约请求"
              type="info"
              show-icon
              :closable="false"
              class="custom-alert"
            />
            <div v-if="dbData.unpaidFines === 0 && dbData.overdueAlerts === 0" class="all-good">
              <el-icon class="good-icon"><CircleCheckFilled /></el-icon>
              <p>目前一切运转正常，无需处理事项</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="material-card timeline-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">最新借阅动态</span>
            </div>
          </template>
          <el-timeline v-if="dbData.activities?.length > 0">
            <el-timeline-item
              v-for="(activity, index) in dbData.activities"
              :key="index"
              :type="activity.status === '借出' ? 'primary' : 'success'"
              size="large"
              :timestamp="formatDate(activity.timestamp)"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无动态" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Notebook,
  User,
  DataAnalysis,
  Warning,
  MoreFilled,
  View,
  Download,
  CircleCheckFilled,
} from '@element-plus/icons-vue'
import request from '../../utils/request'

const router = useRouter()
const loading = ref(false)
const dbData = ref<any>({})

// 统计卡片的数据结构映射
const statCards = ref([
  {
    title: '馆藏图书总册数',
    value: 0,
    icon: Notebook,
    color: '#1a73e8',
    bgColor: '#e8f0fe',
    route: '/admin/books',
  },
  {
    title: '注册读者人数',
    value: 0,
    icon: User,
    color: '#34a853',
    bgColor: '#e6f4ea',
    route: '/admin/users',
  },
  {
    title: '累计借阅人次',
    value: 0,
    icon: DataAnalysis,
    color: '#fbbc04',
    bgColor: '#fef7e0',
    route: '/admin/borrows',
  },
  {
    title: '超期预警数',
    value: 0,
    icon: Warning,
    color: '#ea4335',
    bgColor: '#fce8e6',
    route: '/admin/fines',
  },
])

const loadDashboardData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/dashboard/statistics')
    dbData.value = res.data

    // 把真实数据注入到卡片中
    statCards.value[0].value = res.data.totalBooks
    statCards.value[1].value = res.data.totalStudents
    statCards.value[2].value = res.data.totalBorrows
    statCards.value[3].value = res.data.overdueAlerts
  } catch (error) {
    console.error('获取仪表盘数据失败', error)
  } finally {
    loading.value = false
  }
}

// 计算百分比（防止除以0）
const getPercent = (type: string) => {
  if (!dbData.value.totalBooks || dbData.value.totalBooks === 0) return 0
  const count = dbData.value.bookStatus?.[type] || 0
  return Math.round((count / dbData.value.totalBooks) * 100)
}

// 格式化时间
const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}月${d.getDate()}日 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 处理右上角下拉菜单点击
const handleCommand = (command: string, route: string) => {
  if (command === 'view') {
    router.push(route)
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard-container {
  padding: 8px;
}

/* Material 卡片统一风格 */
.material-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03) !important;
  transition: all 0.3s ease;
}
.material-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06) !important;
  transform: translateY(-2px);
}

/* 第一排：统计卡片 */
.stat-card {
  height: 120px;
}
.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}
.stat-info {
  display: flex;
  flex-direction: column;
  width: 100%;
}
.stat-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.stat-title {
  font-size: 14px;
  color: #5f6368;
  font-weight: 500;
}
.more-icon {
  cursor: pointer;
  color: #bdc3c7;
  font-size: 16px;
  transition: color 0.2s;
}
.more-icon:hover {
  color: #202124;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #202124;
}
.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  margin-left: 12px;
}
.stat-icon {
  font-size: 28px;
}

/* 第二排 */
.content-row {
  margin-top: 24px;
}
.chart-card,
.alert-card,
.timeline-card {
  height: 420px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #202124;
}

/* 进度条样式 */
.progress-list {
  display: flex;
  flex-direction: column;
  gap: 28px;
  padding-top: 10px;
}
.progress-label-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
  color: #5f6368;
  font-weight: 500;
}

/* 待办预警样式 */
.alert-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.custom-alert {
  cursor: pointer;
  transition: opacity 0.2s;
}
.custom-alert:hover {
  opacity: 0.8;
}
.all-good {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #5f6368;
}
.good-icon {
  font-size: 48px;
  color: #34a853;
  margin-bottom: 16px;
  opacity: 0.8;
}
</style>
