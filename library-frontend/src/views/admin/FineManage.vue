<template>
  <div class="fine-manage-container">
    <el-card class="material-card search-card" shadow="never">
      <div class="toolbar">
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学号或姓名..."
            prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            class="search-input"
          />
          <el-select
            v-model="filterStatus"
            placeholder="缴纳状态"
            clearable
            class="status-select"
            @change="handleSearch"
          >
            <el-option label="未缴纳" value="未缴纳" />
            <el-option label="已缴纳" value="已缴纳" />
          </el-select>
          <el-button type="primary" class="md-button" @click="handleSearch">检索违约记录</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="material-card table-card" shadow="never">
      <el-table v-loading="loading" :data="fineData" class="material-table" style="width: 100%">
        <el-table-column prop="fineId" label="罚单编号" width="100" sortable />

        <el-table-column label="违约读者" min-width="150">
          <template #default="scope">
            <div class="info-cell">
              <span class="main-text">{{ scope.row.studentName || '未知读者' }}</span>
              <span class="sub-text">学号: {{ scope.row.studentId }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="reason" label="产生原因" min-width="180" />

        <el-table-column label="罚金 (元)" width="120" align="center">
          <template #default="scope">
            <span class="money-text">¥ {{ scope.row.amount }}</span>
          </template>
        </el-table-column>

        <el-table-column label="产生日期" width="150" align="center">
          <template #default="scope">
            <span class="sub-text">{{ formatDate(scope.row.generateDate) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="120" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === '未缴纳' ? 'danger' : 'success'"
              effect="light"
              round
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作 / 处理人" width="160" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip
              content="确认已收到读者罚金并结清"
              placement="top"
              v-if="scope.row.status === '未缴纳'"
            >
              <el-button
                type="success"
                icon="Check"
                class="md-action-btn"
                @click="handlePay(scope.row)"
              />
            </el-tooltip>
            <span v-else class="sub-text">
              经办人: {{ scope.row.handlerName || scope.row.handlerId || '系统' }}
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
const fineData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterStatus = ref('')

// 获取当前登录的管理员ID
const adminId = ref(localStorage.getItem('userId') || '')

const loadFines = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/fines/list', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
        status: filterStatus.value,
      },
    })
    fineData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadFines()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadFines()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadFines()
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const handlePay = (row: any) => {
  ElMessageBox.confirm(
    `确认已收到读者 ${row.studentName} 的罚金 ¥${row.amount}，并结清该罚单吗？`,
    '结清确认',
    {
      confirmButtonText: '确认结清',
      cancelButtonText: '取消',
      type: 'warning',
      customClass: 'material-message-box',
    },
  )
    .then(async () => {
      try {
        // 提交时带上当前管理员的工号作为经办人
        await request.put(`/api/fines/pay/${row.fineId}`, null, {
          params: { adminId: adminId.value },
        })
        ElMessage.success('罚单结清成功！')
        loadFines()
      } catch (error) {}
    })
    .catch(() => {})
}

onMounted(() => {
  loadFines()
})
</script>

<style scoped>
.fine-manage-container {
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
.money-text {
  font-size: 16px;
  font-weight: bold;
  color: #ea4335;
} /* 罚金用红色醒目 */

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
