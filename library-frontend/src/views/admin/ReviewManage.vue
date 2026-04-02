<template>
  <div class="review-manage-container">
    <el-card class="material-card search-card" shadow="never">
      <div class="toolbar">
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索书名、学号或评论内容..."
            prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            class="search-input"
          />
          <el-select
            v-model="filterScore"
            placeholder="评分筛选"
            clearable
            class="status-select"
            @change="handleSearch"
          >
            <el-option label="5星 (好评)" :value="5" />
            <el-option label="4星 (良好)" :value="4" />
            <el-option label="3星 (中评)" :value="3" />
            <el-option label="2星 (差评)" :value="2" />
            <el-option label="1星 (极差)" :value="1" />
          </el-select>
          <el-button type="primary" class="md-button" @click="handleSearch">检索评价</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="material-card table-card" shadow="never">
      <el-table v-loading="loading" :data="reviewData" class="material-table" style="width: 100%">
        <el-table-column prop="reviewId" label="ID" width="80" />

        <el-table-column label="评价图书" min-width="180">
          <template #default="scope">
            <div class="info-cell">
              <span class="main-text">《{{ scope.row.bookTitle || '未知图书' }}》</span>
              <span class="sub-text">ISBN: {{ scope.row.bookId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="评价读者" min-width="120">
          <template #default="scope">
            <div class="info-cell">
              <span class="main-text">{{ scope.row.studentName || '未知读者' }}</span>
              <span class="sub-text">{{ scope.row.studentId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="评分" width="160" align="center">
          <template #default="scope">
            <el-rate v-model="scope.row.score" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>

        <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />

        <el-table-column label="评价时间" width="160" align="center">
          <template #default="scope">
            <span class="sub-text">{{ formatDateTime(scope.row.reviewDate) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="删除违规评论" placement="top">
              <el-button
                type="danger"
                icon="Delete"
                class="md-action-btn"
                @click="handleDelete(scope.row)"
              />
            </el-tooltip>
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
const reviewData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterScore = ref<number | ''>('')

const loadReviews = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/reviews/list', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
        score: filterScore.value,
      },
    })
    reviewData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadReviews()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadReviews()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadReviews()
}

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除读者 ${row.studentName} 对《${row.bookTitle}》的评价吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error',
      customClass: 'material-message-box',
    },
  )
    .then(async () => {
      try {
        await request.delete(`/api/reviews/${row.reviewId}`)
        ElMessage.success('评价已删除')
        loadReviews()
      } catch (error) {}
    })
    .catch(() => {})
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.review-manage-container {
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
.md-action-btn.el-button--danger {
  background-color: #fce8e6;
  color: #ea4335;
}
.md-action-btn.el-button--danger:hover {
  background-color: #ea4335;
  color: #fff;
  box-shadow: 0 2px 6px rgba(234, 67, 53, 0.4);
}
</style>
