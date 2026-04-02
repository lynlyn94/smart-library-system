<template>
  <div class="reservation-manage-container">
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
            placeholder="预约状态"
            clearable
            class="status-select"
            @change="handleSearch"
          >
            <el-option label="等待" value="等待" />
            <el-option label="可取" value="可取" />
            <el-option label="已完成" value="已完成" />
            <el-option label="取消" value="取消" />
            <el-option label="过期" value="过期" />
          </el-select>
          <el-button type="primary" class="md-button" @click="handleSearch">检索预约</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="material-card table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="reservationData"
        class="material-table"
        style="width: 100%"
      >
        <el-table-column prop="reservationId" label="预约单号" width="100" />

        <el-table-column label="预约图书" min-width="200">
          <template #default="scope">
            <div class="info-cell">
              <span class="main-text">《{{ scope.row.bookTitle || '未知图书' }}》</span>
              <span class="sub-text">ISBN: {{ scope.row.bookId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="预约读者" min-width="150">
          <template #default="scope">
            <div class="info-cell">
              <span class="main-text">{{ scope.row.studentName || '未知读者' }}</span>
              <span class="sub-text">学号: {{ scope.row.studentId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="预约日期" width="150" align="center">
          <template #default="scope">
            <span class="sub-text">{{ formatDate(scope.row.reservationDate) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="当前状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" effect="light" round>
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <template v-if="scope.row.status === '等待'">
              <el-tooltip content="通知读者来取书" placement="top">
                <el-button
                  type="primary"
                  icon="Bell"
                  class="md-action-btn"
                  @click="handleUpdateStatus(scope.row, '可取')"
                />
              </el-tooltip>
              <el-tooltip content="取消预约" placement="top">
                <el-button
                  type="danger"
                  icon="Close"
                  class="md-action-btn"
                  @click="handleUpdateStatus(scope.row, '取消')"
                />
              </el-tooltip>
            </template>

            <template v-else-if="scope.row.status === '可取'">
              <el-tooltip content="读者已取走，完成流转" placement="top">
                <el-button
                  type="success"
                  icon="Check"
                  class="md-action-btn"
                  @click="handleUpdateStatus(scope.row, '已完成')"
                />
              </el-tooltip>
              <el-tooltip content="读者逾期未取" placement="top">
                <el-button
                  type="warning"
                  icon="Clock"
                  class="md-action-btn"
                  @click="handleUpdateStatus(scope.row, '过期')"
                />
              </el-tooltip>
            </template>

            <span v-else class="sub-text">流转结束</span>
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
const reservationData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterStatus = ref('')

const loadReservations = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/reservations/list', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
        status: filterStatus.value,
      },
    })
    reservationData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadReservations()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadReservations()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadReservations()
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const getStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    等待: 'warning',
    可取: 'primary',
    已完成: 'success',
    取消: 'info',
    过期: 'danger',
  }
  return map[status] || 'info'
}

const handleUpdateStatus = (row: any, newStatus: string) => {
  let actionText = ''
  if (newStatus === '可取') actionText = '标记为“可取”，系统将可通知读者来取书'
  if (newStatus === '已完成') actionText = '标记为“已完成”，确认读者已取走该书'
  if (newStatus === '取消') actionText = '取消该预约'
  if (newStatus === '过期') actionText = '标记为“过期作废”，读者超时未取'

  ElMessageBox.confirm(`确定要对该预约单 ${actionText} 吗？`, '状态流转确认', {
    confirmButtonText: '确认操作',
    cancelButtonText: '暂不处理',
    type: newStatus === '已完成' || newStatus === '可取' ? 'success' : 'warning',
    customClass: 'material-message-box',
  })
    .then(async () => {
      try {
        await request.put(`/api/reservations/status/${row.reservationId}`, null, {
          params: { status: newStatus },
        })
        ElMessage.success(`状态已更新为：${newStatus}`)
        loadReservations()
      } catch (error) {}
    })
    .catch(() => {})
}

onMounted(() => {
  loadReservations()
})
</script>

<style scoped>
.reservation-manage-container {
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
.md-action-btn.el-button--primary {
  background-color: #e8f0fe;
  color: #1a73e8;
}
.md-action-btn.el-button--primary:hover {
  background-color: #1a73e8;
  color: #fff;
  box-shadow: 0 2px 6px rgba(26, 115, 232, 0.4);
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
.md-action-btn.el-button--warning {
  background-color: #fef7e0;
  color: #fbbc04;
}
.md-action-btn.el-button--warning:hover {
  background-color: #fbbc04;
  color: #fff;
  box-shadow: 0 2px 6px rgba(251, 188, 4, 0.4);
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
