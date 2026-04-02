<template>
  <div class="student-home">
    <div class="hero-banner">
      <h1 class="hero-title">探索知识的海洋</h1>
      <p class="hero-subtitle">在这里发现你的下一本好书，一键借阅，畅游书海。</p>
      <div class="hero-search">
        <el-input
          v-model="searchKeyword"
          placeholder="输入书名、作者、ISBN 或分类搜索..."
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button icon="Search" class="search-btn" @click="handleSearch" />
          </template>
        </el-input>
      </div>
    </div>

    <div class="book-grid" v-loading="loading">
      <el-card
        v-for="book in bookList"
        :key="book.bookId"
        class="book-card"
        shadow="hover"
        :body-style="{ padding: '0px' }"
      >
        <div class="book-cover-wrapper">
          <el-image :src="book.coverUrl" class="book-cover" fit="cover">
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="status-badge" :class="{ 'out-of-stock': book.availableCount <= 0 }">
            {{ book.availableCount > 0 ? '可借阅' : '已借空' }}
          </div>
        </div>

        <div class="book-info">
          <h3 class="book-title" :title="book.title">{{ book.title }}</h3>
          <p class="book-author">{{ book.author }}</p>

          <div class="book-meta">
            <el-tag size="small" type="info" effect="plain" round>{{
              book.category || '综合'
            }}</el-tag>
            <span class="stock-info">馆藏: {{ book.location }}</span>
          </div>

          <el-button
            type="primary"
            class="borrow-btn"
            :disabled="book.availableCount <= 0 || book.status !== '正常'"
            @click="handleBorrowClick(book)"
          >
            {{ getButtonText(book) }}
          </el-button>
        </div>
      </el-card>
    </div>

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        v-model:current-page="currentPage"
        :page-size="pageSize"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Picture } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const bookList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12) // 网格布局，一页12本比较美观
const searchKeyword = ref('')

// 当前登录的学生ID
const studentId = ref(localStorage.getItem('userId') || '')

// 加载图书大厅数据
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
    bookList.value = res.data.records
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

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadBooks()
}

// 动态计算按钮文本
const getButtonText = (book: any) => {
  if (book.status !== '正常') return book.status // 显示遗失、下架等
  if (book.availableCount <= 0) return '暂无库存'
  return '立即借阅'
}

// 点击借阅按钮
const handleBorrowClick = (book: any) => {
  if (!studentId.value) {
    ElMessage.warning('请先重新登录以获取身份信息')
    return
  }

  ElMessageBox.confirm(`确定要借阅《${book.title}》吗？借阅期限通常为30天。`, '借阅确认', {
    confirmButtonText: '确认借阅',
    cancelButtonText: '取消',
    type: 'info',
    customClass: 'material-message-box',
  })
    .then(async () => {
      try {
        // 组装我们在后端定义好的 BorrowDTO 数据结构
        const borrowDTO = {
          studentId: studentId.value,
          bookId: book.bookId,
        }
        // 调用后端的借书接口
        await request.post('/api/borrow/do', borrowDTO)
        ElMessage.success('借阅成功！请在规定时间内前往图书馆前台取书。')

        // 刷新列表，扣减刚借出的库存
        loadBooks()
      } catch (error) {
        // 这里的 error 拦截器会处理，比如提示“账号冻结”或“超额”
      }
    })
    .catch(() => {})
}

onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.student-home {
  padding-bottom: 40px;
}

/* =========== 1. 巨幕横幅 =========== */
.hero-banner {
  background: linear-gradient(135deg, #e8f0fe 0%, #f1f3f4 100%);
  border-radius: 24px;
  padding: 60px 40px;
  text-align: center;
  margin-bottom: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.02);
}
.hero-title {
  font-size: 36px;
  color: #1a73e8;
  margin: 0 0 16px 0;
  font-weight: 800;
  letter-spacing: 2px;
}
.hero-subtitle {
  font-size: 16px;
  color: #5f6368;
  margin: 0 0 32px 0;
}
.hero-search {
  max-width: 600px;
  margin: 0 auto;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 24px 0 0 24px;
  padding-left: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.search-input :deep(.el-input-group__append) {
  border-radius: 0 24px 24px 0;
  background-color: #1a73e8;
  color: white;
  border: none;
  padding: 0 24px;
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.2);
}
.search-btn {
  font-size: 18px;
}

/* =========== 2. CSS Grid 图书网格 =========== */
.book-grid {
  display: grid;
  /* 核心魔法：自动填充，最小宽度220px，最大1等分 */
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.book-card {
  border: none;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  background-color: #fff;
}
.book-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important;
}

/* 封面区域 */
.book-cover-wrapper {
  position: relative;
  width: 100%;
  padding-top: 133%; /* 保持大约 3:4 的封面比例 */
  background-color: #f8f9fa;
  overflow: hidden;
}
.book-cover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}
.book-card:hover .book-cover {
  transform: scale(1.05); /* 悬浮时图片微放大 */
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #bdc3c7;
  font-size: 40px;
}

/* 角标 */
.status-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background-color: rgba(52, 168, 83, 0.9);
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  backdrop-filter: blur(4px);
}
.status-badge.out-of-stock {
  background-color: rgba(234, 67, 53, 0.9);
}

/* 信息区域 */
.book-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
}
.book-title {
  margin: 0 0 6px 0;
  font-size: 16px;
  color: #202124;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; /* 文字过长显示省略号 */
}
.book-author {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #5f6368;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.book-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.stock-info {
  font-size: 12px;
  color: #80868b;
}

/* 借阅按钮 */
.borrow-btn {
  width: 100%;
  border-radius: 20px;
  font-weight: bold;
  padding: 10px 0;
}

/* =========== 3. 分页 =========== */
.pagination-wrapper {
  display: flex;
  justify-content: center;
}
</style>
