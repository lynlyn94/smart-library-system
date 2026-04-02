<template>
  <div class="book-list">
    <el-form :inline="true" :model="searchForm" class="search-bar">
      <el-form-item label="图书名称">
        <el-input v-model="searchForm.title" placeholder="输入书名搜索" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="bookData" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="bookId" label="编号" width="100" />
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'available' ? 'success' : 'danger'">
            {{ scope.row.status === 'available' ? '在馆' : '借出' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            :disabled="scope.row.status !== 'available'"
            @click="handleBorrow(scope.row)"
          >
            借阅
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      layout="total, prev, pager, next"
      :total="total"
      v-model:current-page="currentPage"
      :page-size="10"
      @current-change="loadBooks"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request' //

// 响应式数据
const bookData = ref([])
const total = ref(0)
const currentPage = ref(1)
const loading = ref(false)
const searchForm = reactive({
  title: '',
})

// 加载图书列表
const loadBooks = async () => {
  loading.ref = true
  try {
    const res: any = await request.get('/api/books/list', {
      params: {
        current: currentPage.value,
        size: 10,
        title: searchForm.title,
      },
    })
    bookData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取图书列表失败', error)
  } finally {
    loading.value = false
  }
}

// 查询按钮点击事件
const handleSearch = () => {
  currentPage.value = 1
  loadBooks()
}

// 借阅功能实现
const handleBorrow = (row: any) => {
  // 从 localStorage 获取登录时存入的学号和角色
  const studentId = localStorage.getItem('userId')
  const role = localStorage.getItem('role')

  if (role !== 'student') {
    ElMessage.warning('只有学生身份可以借阅图书')
    return
  }

  if (!studentId) {
    ElMessage.error('用户信息缺失，请重新登录')
    return
  }

  ElMessageBox.confirm(`确认借阅《${row.title}》吗？`, '借阅确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      try {
        // 调用后端借阅接口
        await request.post('/api/borrow/do', {
          studentId: studentId,
          bookId: row.bookId,
        })

        ElMessage.success('借阅成功！')
        // 刷新列表以更新图书状态
        loadBooks()
      } catch (error: any) {
        // 错误已由 request.ts 拦截器统一处理弹出提示
      }
    })
    .catch(() => {
      // 点击取消不做处理
    })
}

// 页面加载时初始化
onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.book-list {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}
.search-bar {
  margin-bottom: 20px;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
