<template>
  <div class="borrow-manage-container">
    <el-card class="material-card search-card" shadow="never">
      <div class="toolbar">
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学号、ISBN 或书名..."
            prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            class="search-input"
          />
          <el-select
            v-model="filterStatus"
            placeholder="借阅状态"
            clearable
            class="status-select"
            @change="handleSearch"
          >
            <el-option label="借出(含逾期)" value="借出" />
            <el-option label="已还" value="已还" />
            <el-option label="遗失" value="遗失" />
            <el-option label="损坏" value="损坏" />
          </el-select>
          <el-button type="primary" class="md-button" @click="handleSearch">检索记录</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="material-card table-card" shadow="never">
      <el-table v-loading="loading" :data="recordData" class="material-table" style="width: 100%">
        <el-table-column prop="borrowId" label="流水号" width="120" sortable />

        <el-table-column label="借阅图书" min-width="200">
          <template #default="scope">
            <div class="info-cell">
              <span class="main-text">《{{ scope.row.bookTitle || '未知图书' }}》</span>
              <span class="sub-text">ISBN: {{ scope.row.bookId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="借阅人" min-width="150">
          <template #default="scope">
            <div class="info-cell">
              <span class="main-text">{{ scope.row.studentName || '未知读者' }}</span>
              <span class="sub-text">学号: {{ scope.row.studentId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="借阅 / 应还日期" width="220" align="center">
          <template #default="scope">
            <div class="time-cell">
              <span class="borrow-time">借: {{ formatDate(scope.row.borrowDate) }}</span>
              <span
                class="due-time"
                :class="{
                  'text-danger':
                    isOverdue(scope.row.dueDate) &&
                    (scope.row.status === '借出' || scope.row.status === '逾期'),
                }"
              >
                还: {{ formatDate(scope.row.dueDate) }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="当前状态" width="120" align="center">
          <template #default="scope">
            <el-tag
              :type="getStatusTagType(scope.row.status, scope.row.dueDate)"
              effect="light"
              round
            >
              {{ getStatusText(scope.row.status, scope.row.dueDate) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip
              content="确认读者已将此书归还"
              placement="top"
              v-if="['借出', '逾期'].includes(scope.row.status)"
            >
              <el-button
                type="success"
                icon="Check"
                class="md-action-btn"
                @click="handleReturn(scope.row)"
              />
            </el-tooltip>

            <span v-else class="sub-text">
              {{
                scope.row.returnDate ? `已于 ${formatDate(scope.row.returnDate)} 归还` : '结束流转'
              }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const loading = ref(false)
const recordData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterStatus = ref('')

const loadRecords = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/borrow/list', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
        status: filterStatus.value,
      },
    })
    recordData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadRecords()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadRecords()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadRecords()
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const isOverdue = (dueDateStr: string) => {
  if (!dueDateStr) return false
  return new Date(dueDateStr).getTime() < new Date().getTime()
}

// 【核心修改】中文枚举状态解析
const getStatusText = (status: string, dueDate: string) => {
  if (status === '已还') return '已归还'
  if (status === '遗失') return '已遗失'
  if (status === '损坏') return '已损坏'
  if (status === '逾期' || (status === '借出' && isOverdue(dueDate))) return '已逾期'
  return '借阅中'
}

// 【核心修改】中文枚举颜色映射
const getStatusTagType = (status: string, dueDate: string) => {
  if (status === '已还') return 'info'
  if (status === '遗失' || status === '损坏') return 'warning'
  if (status === '逾期' || (status === '借出' && isOverdue(dueDate))) return 'danger'
  return 'primary'
}

const handleReturn = (row: any) => {
  ElMessageBox.confirm(`确认读者 ${row.studentName} 已将《${row.bookTitle}》归还？`, '还书确认', {
    confirmButtonText: '确认归还',
    cancelButtonText: '取消',
    type: 'success',
    customClass: 'material-message-box',
  })
    .then(async () => {
      try {
        await request.post(`/api/borrow/return/${row.borrowId}`)
        ElMessage.success('还书处理成功！')
        loadRecords()
      } catch (error) {}
    })
    .catch(() => {})
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.borrow-manage-container {
  padding: 8px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 84px);
}
.material-card {
  border-radius: 16px;
  border: none;
  background-color: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03) !important;
  margin-bottom: 24px;
}
.search-card {
  padding: 10px 20px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-area {
  display: flex;
  gap: 16px;
}
.search-input {
  width: 320px;
}
.status-select {
  width: 140px;
}
.search-input :deep(.el-input__wrapper),
.status-select :deep(.el-input__wrapper) {
  border-radius: 24px;
  padding-left: 16px;
}

.table-card {
  padding: 16px;
}

.info-cell {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.main-text {
  font-weight: 600;
  color: #202124;
  font-size: 14px;
  margin-bottom: 4px;
}
.sub-text {
  font-size: 12px;
  color: #80868b;
}

.time-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 13px;
  color: #5f6368;
}
.borrow-time {
  margin-bottom: 4px;
}
.due-time {
  font-weight: 500;
}
.text-danger {
  color: #ea4335;
  font-weight: bold;
}

.material-table :deep(th.el-table__cell) {
  background-color: #f8f9fa;
  color: #5f6368;
  font-weight: 600;
  height: 56px;
}
.material-table :deep(.el-table__row) {
  height: 72px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.md-button {
  border-radius: 20px;
  font-weight: 600;
  padding: 8px 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}
.md-button:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.md-action-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  margin: 0 6px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.md-action-btn.el-button--success {
  background-color: #e6f4ea;
  color: #34a853;
}
.md-action-btn.el-button--success:hover {
  background-color: #34a853;
  color: #fff;
  box-shadow: 0 2px 6px rgba(52, 168, 83, 0.4);
}
</style>
