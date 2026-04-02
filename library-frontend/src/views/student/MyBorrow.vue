<template>
  <div class="my-borrow-container">
    <div class="page-header">
      <h1 class="page-title">我的借阅柜</h1>
      <p class="page-subtitle">在这里追踪你的阅读进度与归还期限。</p>
    </div>

    <el-card class="material-card tabs-card" shadow="never">
      <el-tabs v-model="activeTab" class="material-tabs">
        <el-tab-pane label="当前借阅 (需归还)" name="active">
          <div v-if="activeRecords.length === 0" class="empty-state">
            <el-empty description="你当前没有在借的图书哦，去图书大厅看看吧！" />
          </div>

          <div v-else class="record-grid">
            <el-card
              v-for="record in activeRecords"
              :key="record.borrowId"
              class="record-card"
              shadow="hover"
            >
              <div class="record-content">
                <div class="book-info">
                  <h3 class="book-title">《{{ record.bookTitle }}》</h3>
                  <p class="book-meta">
                    流水号: {{ record.borrowId }} &nbsp;|&nbsp; ISBN: {{ record.bookId }}
                  </p>
                  <p class="time-info">借出日期: {{ formatDate(record.borrowDate) }}</p>
                  <p class="time-info text-primary">应还日期: {{ formatDate(record.dueDate) }}</p>
                </div>

                <div class="status-zone">
                  <div
                    v-if="record.status === '逾期' || getDaysLeft(record.dueDate) < 0"
                    class="countdown text-danger"
                  >
                    <el-icon><Warning /></el-icon> 已逾期
                  </div>
                  <div v-else-if="getDaysLeft(record.dueDate) <= 3" class="countdown text-warning">
                    <el-icon><Timer /></el-icon> 剩 {{ getDaysLeft(record.dueDate) }} 天
                  </div>
                  <div v-else class="countdown text-success">
                    <el-icon><Clock /></el-icon> 剩 {{ getDaysLeft(record.dueDate) }} 天
                  </div>
                  <el-tag
                    :type="record.status === '逾期' ? 'danger' : 'primary'"
                    effect="dark"
                    round
                    class="status-tag"
                  >
                    {{ record.status }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>

        <el-tab-pane label="借阅历史" name="history">
          <div v-if="historyRecords.length === 0" class="empty-state">
            <el-empty description="暂无借阅历史记录" />
          </div>

          <div v-else class="record-grid">
            <el-card
              v-for="record in historyRecords"
              :key="record.borrowId"
              class="record-card history-card"
              shadow="never"
            >
              <div class="record-content">
                <div class="book-info">
                  <h3 class="book-title text-muted">《{{ record.bookTitle }}》</h3>
                  <p class="book-meta">
                    流水号: {{ record.borrowId }} &nbsp;|&nbsp; ISBN: {{ record.bookId }}
                  </p>
                  <p class="time-info">借出日期: {{ formatDate(record.borrowDate) }}</p>
                  <p class="time-info">归还日期: {{ formatDate(record.returnDate) || '未知' }}</p>
                </div>

                <div class="status-zone">
                  <el-tag
                    :type="getHistoryTagType(record.status)"
                    effect="plain"
                    round
                    style="margin-bottom: 8px"
                  >
                    {{ record.status }}
                  </el-tag>
                  <el-button
                    v-if="record.status === '已还'"
                    size="small"
                    type="primary"
                    plain
                    round
                    @click="openReviewDialog(record)"
                  >
                    <el-icon><EditPen /></el-icon>写书评
                  </el-button>
                </div>
              </div>
            </el-card>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="reviewVisible"
      title="分享你的阅读心得"
      width="500px"
      custom-class="material-dialog"
    >
      <div style="text-align: center; margin-bottom: 20px">
        <h3>《{{ currentReviewBook.bookTitle }}》</h3>
      </div>
      <el-form :model="reviewForm" label-position="top">
        <el-form-item label="综合评分">
          <el-rate
            v-model="reviewForm.score"
            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            show-text
          />
        </el-form-item>
        <el-form-item label="书评内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="这本书对你有什么启发？写下来分享给大家吧..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview" :loading="submitLoading"
            >提交评价</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning, Timer, Clock, EditPen } from '@element-plus/icons-vue'
import request from '../../utils/request'

const activeTab = ref('active')
const allRecords = ref<any[]>([])
const studentId = ref(localStorage.getItem('userId') || '')

// --- 评价功能相关状态 ---
const reviewVisible = ref(false)
const submitLoading = ref(false)
const currentReviewBook = ref<any>({})
const reviewForm = ref({
  borrowId: null,
  bookId: '',
  score: 5,
  content: '',
})

// 获取该学生的所有借阅记录
const loadMyRecords = async () => {
  if (!studentId.value) return
  try {
    const res: any = await request.get('/api/borrow/list', {
      params: {
        current: 1,
        size: 100,
        keyword: studentId.value,
      },
    })

    // 二次过滤，确保只显示自己的记录（防止学号刚好是某本书的书名）
    allRecords.value = res.data.records.filter((r: any) => r.studentId === studentId.value)
  } catch (error) {
    console.error('拉取记录失败', error)
  }
}

// 计算属性：当前借阅 (借出、逾期)
const activeRecords = computed(() => {
  return allRecords.value.filter((r) => r.status === '借出' || r.status === '逾期')
})

// 计算属性：历史借阅 (已还、遗失、损坏)
const historyRecords = computed(() => {
  return allRecords.value.filter(
    (r) => r.status === '已还' || r.status === '遗失' || r.status === '损坏',
  )
})

// 日期格式化
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 计算剩余天数
const getDaysLeft = (dueDateStr: string) => {
  if (!dueDateStr) return 0
  const due = new Date(dueDateStr).setHours(23, 59, 59, 999) // 截止到当天的最后一毫秒
  const now = new Date().getTime()
  const diff = due - now
  return Math.ceil(diff / (1000 * 3600 * 24))
}

const getHistoryTagType = (status: string) => {
  if (status === '已还') return 'info'
  if (status === '遗失' || status === '损坏') return 'warning'
  return 'info'
}

// --- 评价功能相关方法 ---

// 打开弹窗
const openReviewDialog = (record: any) => {
  currentReviewBook.value = record
  reviewForm.value = {
    borrowId: record.borrowId,
    bookId: record.bookId,
    score: 5, // 默认给5星
    content: '',
  }
  reviewVisible.value = true
}

// 提交评价
const submitReview = async () => {
  if (!reviewForm.value.content.trim()) {
    ElMessage.warning('书评内容不能为空哦！')
    return
  }
  submitLoading.value = true
  try {
    // 组装数据，加上当前登录的学生ID
    const payload = {
      ...reviewForm.value,
      studentId: studentId.value,
    }
    await request.post('/api/reviews/add', payload)
    ElMessage.success('评价发布成功！感谢你的分享。')
    reviewVisible.value = false
  } catch (error: any) {
    // 如果已经评价过，拦截器会提示后端返回的错误
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadMyRecords()
})
</script>

<style scoped>
.my-borrow-container {
  padding-bottom: 40px;
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}
.page-title {
  font-size: 28px;
  color: #202124;
  margin: 0 0 8px 0;
  font-weight: 700;
}
.page-subtitle {
  font-size: 15px;
  color: #5f6368;
  margin: 0;
}

/* 卡片与 Tab 样式 */
.material-card {
  border-radius: 16px;
  border: none;
  background-color: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03) !important;
}
.tabs-card {
  padding: 8px 24px 24px;
}
.material-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  color: #5f6368;
  height: 50px;
  line-height: 50px;
}
.material-tabs :deep(.el-tabs__item.is-active) {
  color: #1a73e8;
  font-weight: 600;
}

.empty-state {
  padding: 60px 0;
}

/* 网格布局 */
.record-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
  margin-top: 16px;
}

/* 单个记录卡片 */
.record-card {
  border-radius: 12px;
  border: 1px solid #f1f3f4;
  transition: all 0.3s;
}
.record-card:hover {
  border-color: #d2e3fc;
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.08) !important;
}
.history-card {
  background-color: #f8f9fa;
  opacity: 0.85;
}
.history-card:hover {
  border-color: #f1f3f4;
  box-shadow: none !important;
  opacity: 1;
}

.record-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.book-info {
  flex-grow: 1;
}
.book-title {
  font-size: 16px;
  color: #202124;
  margin: 0 0 8px 0;
  font-weight: 600;
}
.text-muted {
  color: #5f6368;
}
.book-meta {
  font-size: 12px;
  color: #9aa0a6;
  margin: 0 0 12px 0;
}
.time-info {
  font-size: 13px;
  color: #5f6368;
  margin: 0 0 4px 0;
}

/* 状态区域 */
.status-zone {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 90px;
}
.countdown {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.text-success {
  color: #34a853;
}
.text-warning {
  color: #fbbc04;
}
.text-danger {
  color: #ea4335;
}
.text-primary {
  color: #1a73e8;
  font-weight: 500;
}

.status-tag {
  padding: 0 16px;
}
</style>
