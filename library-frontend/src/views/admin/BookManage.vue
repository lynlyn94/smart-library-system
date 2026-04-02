<template>
  <div class="book-manage-container">
    <el-card class="material-card search-card" shadow="never">
      <div class="toolbar">
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索书名、ISBN 或作者..."
            prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            class="search-input"
          />
          <el-button type="primary" class="md-button" @click="handleSearch">检索</el-button>
        </div>
        <div class="action-area">
          <el-button type="success" icon="Plus" class="md-button" @click="handleAdd"
            >录入新书</el-button
          >
        </div>
      </div>
    </el-card>

    <el-card class="material-card table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="bookData"
        row-key="bookId"
        class="material-table"
        style="width: 100%"
      >
        <el-table-column label="封面" width="80" align="center">
          <template #default="scope">
            <el-image
              class="table-cover"
              :src="scope.row.coverUrl"
              fit="cover"
              :preview-src-list="[scope.row.coverUrl]"
              preview-teleported
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>

        <el-table-column prop="bookId" label="ISBN (编号)" width="140" sortable />

        <el-table-column label="书名 / 作者" min-width="250">
          <template #default="scope">
            <div class="book-info-cell">
              <span class="book-title">{{ scope.row.title }}</span>
              <span class="book-author">{{ scope.row.author }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="location" label="馆藏位置" width="150" show-overflow-tooltip />

        <el-table-column label="库存 (可借/总)" width="130" align="center">
          <template #default="scope">
            <span :class="{ 'text-danger': scope.row.availableCount === 0 }">
              {{ scope.row.availableCount }}
            </span>
            <span class="text-secondary"> / {{ scope.row.totalCount }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="图书状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" effect="plain" round>
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" icon="EditPen" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" icon="Delete" @click="handleDelete(scope.row)">
              删除
            </el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="720px"
      custom-class="material-dialog"
      destroy-on-close
    >
      <el-form :model="bookForm" :rules="rules" ref="formRef" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="ISBN (图书编号)" prop="bookId">
              <el-input
                v-model="bookForm.bookId"
                placeholder="请输入纯数字 ISBN"
                :disabled="dialogTitle === '编辑图书信息'"
                maxlength="20"
                @input="bookForm.bookId = bookForm.bookId.replace(/[^\d]/g, '')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="书名" prop="title">
              <el-input v-model="bookForm.title" placeholder="请输入完整书名" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="bookForm.author" placeholder="请输入作者名字" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-input v-model="bookForm.category" placeholder="请输入分类号，如: TP311" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="出版社">
              <el-input v-model="bookForm.publisher" placeholder="请输入出版社" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="出版日期">
              <el-date-picker
                v-model="bookForm.publishDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="4">
            <el-form-item label="价格 (元)">
              <el-input-number
                v-model="bookForm.price"
                :precision="2"
                :step="0.1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="馆藏位置">
              <el-input v-model="bookForm.location" placeholder="如: 本部-计算中心201" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="总册数" prop="totalCount">
              <el-input-number
                v-model="bookForm.totalCount"
                :min="0"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="当前状态" prop="status">
              <el-select v-model="bookForm.status" style="width: 100%">
                <el-option label="正常" value="正常" />
                <el-option label="遗失" value="遗失" />
                <el-option label="损坏" value="损坏" />
                <el-option label="下架" value="下架" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="封面图片 URL">
              <el-input
                v-model="bookForm.coverUrl"
                placeholder="请输入 HTTP 开头的图片链接"
                prefix-icon="Link"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="图书简介">
              <el-input
                v-model="bookForm.description"
                type="textarea"
                :rows="3"
                placeholder="简要描述图书内容..."
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false" class="md-button text-btn">取消</el-button>
          <el-button type="primary" class="md-button" @click="submitForm" :loading="submitLoading"
            >保存</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, EditPen, Delete, Picture } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const bookData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('录入新书')
const submitLoading = ref(false)
const formRef = ref()

const bookForm = reactive({
  bookId: '',
  title: '',
  author: '',
  publisher: '',
  publishDate: null,
  price: 0.0,
  category: '',
  location: '',
  totalCount: 0,
  availableCount: 0,
  status: '正常',
  description: '',
  coverUrl: '',
})

// 【核心修改 3】增加对 ISBN 的严格正则校验
const rules = {
  bookId: [
    { required: true, message: 'ISBN 不能为空', trigger: 'blur' },
    { pattern: /^\d+$/, message: 'ISBN 只能由纯数字组成', trigger: 'blur' },
  ],
  title: [{ required: true, message: '书名不能为空', trigger: 'blur' }],
  author: [{ required: true, message: '作者不能为空', trigger: 'blur' }],
  totalCount: [{ required: true, message: '必须输入册数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

const loadBooks = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/books/list', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
      },
    })
    bookData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadBooks()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadBooks()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadBooks()
}

const getStatusTagType = (status: string) => {
  switch (status) {
    case '正常':
      return 'success'
    case '遗失':
      return 'danger'
    case '损坏':
      return 'warning'
    case '下架':
      return 'info'
    default:
      return 'info'
  }
}

const handleAdd = () => {
  dialogTitle.value = '录入新书'
  Object.assign(bookForm, {
    bookId: '',
    title: '',
    author: '',
    publisher: '',
    publishDate: null,
    price: 0.0,
    category: '',
    location: '',
    totalCount: 0,
    availableCount: 0,
    status: '正常',
    description: '',
    coverUrl: '',
  })
  dialogVisible.value = true
  // 强制清除校验提示
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑图书信息'
  Object.assign(bookForm, { ...row })
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  if (row.totalCount !== row.availableCount) {
    ElMessage.warning(`还有 ${row.totalCount - row.availableCount} 本书在借，无法删除该 ISBN！`)
    return
  }

  ElMessageBox.confirm(
    `确定要彻底删除 ISBN 为《${row.bookId}》的图书记录吗？此操作不可逆！`,
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error',
    },
  )
    .then(async () => {
      try {
        await request.delete(`/api/books/delete/${row.bookId}`)
        ElMessage.success('图书记录已删除')
        loadBooks()
      } catch (error) {}
    })
    .catch(() => {})
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      if (!bookForm.availableCount && dialogTitle.value === '录入新书') {
        bookForm.availableCount = bookForm.totalCount
      }

      try {
        if (dialogTitle.value === '编辑图书信息') {
          await request.put('/api/books/update', bookForm)
          ElMessage.success('图书信息修改成功')
        } else {
          await request.post('/api/books/add', bookForm)
          ElMessage.success('新书录入成功')
        }
        dialogVisible.value = false
        loadBooks()
      } catch (error) {
        // 注：这里即使后端抛出 "该 ISBN 已存在"，request.ts 拦截器会自动显示 error，
        // 这里只是捕获异常防止崩溃，不用额外写重复提示逻辑。
        console.error('提交失败', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.book-manage-container {
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
.search-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  padding-left: 16px;
}

.table-card {
  padding: 16px;
}

.table-cover {
  width: 50px;
  height: 66px;
  border-radius: 4px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  vertical-align: middle;
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f1f3f4;
  color: #909399;
  font-size: 20px;
  border-radius: 4px;
}

.book-info-cell {
  display: flex;
  flex-direction: column;
}
.book-title {
  font-size: 15px;
  font-weight: 600;
  color: #202124;
  margin-bottom: 4px;
}
.book-author {
  font-size: 13px;
  color: #5f6368;
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

.text-secondary {
  color: #80868b;
  font-size: 12px;
}
.text-danger {
  color: #ea4335;
  font-weight: bold;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.md-button {
  border-radius: 20px;
  font-weight: 600;
  letter-spacing: 0.3px;
  padding: 8px 20px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.text-btn {
  color: #5f6368;
}
.text-btn:hover {
  background-color: #f1f3f4;
  color: #202124;
}

.material-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 10px;
  border-bottom: none;
}
.material-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
}
.material-dialog :deep(.el-dialog__body) {
  padding: 10px 24px 20px;
}
.material-dialog :deep(.el-form-item__label) {
  font-weight: 600;
  color: #202124;
  font-size: 13px;
  margin-bottom: 4px;
}
.material-dialog :deep(.el-input__wrapper) {
  border-radius: 8px;
}
</style>
