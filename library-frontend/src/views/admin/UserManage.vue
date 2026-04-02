<template>
  <div class="user-manage-container">
    <el-card class="material-card search-card" shadow="never">
      <div class="toolbar">
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索学号、姓名或院系专业..."
            prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
            class="search-input"
          />
          <el-button type="primary" class="md-button" @click="handleSearch">检索</el-button>
        </div>
        <div class="action-area">
          <el-button type="success" icon="Plus" class="md-button" @click="handleAdd"
            >录入新读者</el-button
          >
        </div>
      </div>
    </el-card>

    <el-card class="material-card table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="studentData"
        row-key="studentId"
        class="material-table"
        style="width: 100%"
      >
        <el-table-column prop="studentId" label="学号 (账号)" width="140" sortable />

        <el-table-column label="姓名 (性别)" width="150">
          <template #default="scope">
            <span class="user-name">{{ scope.row.name }}</span>
            <span class="text-secondary" v-if="scope.row.gender"> ({{ scope.row.gender }})</span>
          </template>
        </el-table-column>

        <el-table-column label="所属院系 / 专业" min-width="200">
          <template #default="scope">
            <div class="dept-info">
              <span class="dept-name">{{ scope.row.department || '-' }}</span>
              <span class="major-name">{{ scope.row.major || '-' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="phone" label="联系方式" width="130" align="center" />

        <el-table-column label="借阅状况 (已借/总量)" width="160" align="center">
          <template #default="scope">
            <span :class="{ 'text-danger': scope.row.currentBorrow >= scope.row.maxBorrow }">
              {{ scope.row.currentBorrow }}
            </span>
            <span class="text-secondary"> / {{ scope.row.maxBorrow }} 本</span>
          </template>
        </el-table-column>

        <el-table-column prop="accountStatus" label="账户状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.accountStatus)" effect="plain" round>
              {{ scope.row.accountStatus || '正常' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑" placement="top">
              <el-button
                type="primary"
                icon="EditPen"
                class="md-action-btn"
                @click="handleEdit(scope.row)"
              />
            </el-tooltip>

            <el-tooltip
              :content="scope.row.accountStatus === '正常' ? '冻结账号' : '解冻账号'"
              placement="top"
            >
              <el-button
                :type="scope.row.accountStatus === '正常' ? 'warning' : 'success'"
                icon="Lock"
                class="md-action-btn"
                @click="handleToggleStatus(scope.row)"
              />
            </el-tooltip>

            <el-tooltip content="删除" placement="top">
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="780px"
      custom-class="material-dialog"
      destroy-on-close
    >
      <el-form :model="studentForm" :rules="rules" ref="formRef" label-position="top">
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="学号 (登录账号)" prop="studentId">
              <el-input
                v-model="studentForm.studentId"
                placeholder="请输入学号"
                :disabled="dialogTitle === '编辑读者信息'"
                maxlength="20"
                @input="studentForm.studentId = studentForm.studentId.replace(/[^\w]/g, '')"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="登录密码" prop="password">
              <el-input
                v-model="studentForm.password"
                type="password"
                show-password
                placeholder="请输入密码"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="账户状态" prop="accountStatus">
              <el-select v-model="studentForm.accountStatus" style="width: 100%">
                <el-option label="正常" value="正常" />
                <el-option label="挂失" value="挂失" />
                <el-option label="冻结" value="冻结" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-divider border-style="dashed" class="md-divider" />

          <el-col :span="8">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="studentForm.name" placeholder="真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="studentForm.gender">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大借阅量" prop="maxBorrow">
              <el-input-number
                v-model="studentForm.maxBorrow"
                :min="1"
                :max="50"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="院系">
              <el-input v-model="studentForm.department" placeholder="如: 计算机学院" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="专业">
              <el-input v-model="studentForm.major" placeholder="如: 软件工程" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="年级">
              <el-input-number
                v-model="studentForm.grade"
                :min="2000"
                :max="2099"
                :step="1"
                controls-position="right"
                style="width: 100%"
                placeholder="2024"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="班级">
              <el-input v-model="studentForm.class" placeholder="如: 软件2401" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="studentForm.phone" placeholder="手机号码" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电子邮箱">
              <el-input v-model="studentForm.email" placeholder="邮箱地址" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false" class="md-button text-btn">取消</el-button>
          <el-button type="primary" class="md-button" @click="submitForm" :loading="submitLoading"
            >保存修改</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const loading = ref(false)
const studentData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('录入新读者')
const submitLoading = ref(false)
const formRef = ref()

// 严格按照你提供的 SQL Schema 映射的表单模型
const studentForm = reactive({
  studentId: '',
  name: '',
  password: '123456', // 默认密码
  gender: '男',
  department: '',
  major: '',
  grade: new Date().getFullYear(), // 默认当前年份
  class: '',
  phone: '',
  email: '',
  accountStatus: '正常', // 对应数据库 account_status
  maxBorrow: 5, // 数据库 default 为 5
  currentBorrow: 0, // 数据库 default 为 0
})

const rules = {
  studentId: [{ required: true, message: '学号不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
  maxBorrow: [{ required: true, message: '必须设置最大借阅量', trigger: 'blur' }],
}

const loadStudents = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/api/students/list', {
      params: {
        current: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
      },
    })
    studentData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadStudents()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadStudents()
}
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadStudents()
}

// 动态获取状态 Tag 颜色
const getStatusTagType = (status: string) => {
  switch (status) {
    case '正常':
      return 'success'
    case '挂失':
      return 'warning'
    case '冻结':
      return 'danger'
    default:
      return 'info'
  }
}

const handleAdd = () => {
  dialogTitle.value = '录入新读者'
  Object.assign(studentForm, {
    studentId: '',
    name: '',
    password: '123456',
    gender: '男',
    department: '',
    major: '',
    grade: new Date().getFullYear(),
    class: '',
    phone: '',
    email: '',
    accountStatus: '正常',
    maxBorrow: 5,
    currentBorrow: 0,
  })
  dialogVisible.value = true
  if (formRef.value) formRef.value.clearValidate()
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑读者信息'
  Object.assign(studentForm, { ...row })
  dialogVisible.value = true
}

// 快捷冻结/解冻功能
const handleToggleStatus = async (row: any) => {
  // 如果是挂失状态，不允许快捷解冻，必须进表单修改
  if (row.accountStatus === '挂失') {
    ElMessage.warning('账号处于挂失状态，请在编辑表单中处理解挂。')
    return
  }

  const newStatus = row.accountStatus === '正常' ? '冻结' : '正常'
  const actionText = newStatus === '冻结' ? '冻结该账号，限制其借书' : '解除冻结，恢复其借阅权限'

  ElMessageBox.confirm(`确定要 ${actionText} 吗？`, '状态变更', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    customClass: 'material-message-box',
  })
    .then(async () => {
      // 提交到后端时，注意字段名 accountStatus
      await request.put('/api/students/update', { ...row, accountStatus: newStatus })
      ElMessage.success(`账号已${newStatus}`)
      loadStudents()
    })
    .catch(() => {})
}

const handleDelete = (row: any) => {
  if (row.currentBorrow > 0) {
    ElMessage.error(`该读者还有 ${row.currentBorrow} 本书未归还，无法删除账号！`)
    return
  }

  ElMessageBox.confirm(`确定要彻底删除学生 ${row.name} (${row.studentId}) 的档案吗？`, '危险操作', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'error',
    customClass: 'material-message-box',
  })
    .then(async () => {
      await request.delete(`/api/students/delete/${row.studentId}`)
      ElMessage.success('学生档案已删除')
      loadStudents()
    })
    .catch(() => {})
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogTitle.value === '编辑读者信息') {
          await request.put('/api/students/update', studentForm)
          ElMessage.success('档案更新成功')
        } else {
          await request.post('/api/students/add', studentForm)
          ElMessage.success('新读者录入成功')
        }
        dialogVisible.value = false
        loadStudents()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadStudents()
})
</script>

<style scoped>
/* =========== Material 风格延续 =========== */
.user-manage-container {
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

/* 单元格信息布局 */
.user-name {
  font-weight: 600;
  color: #202124;
  font-size: 15px;
}
.dept-info {
  display: flex;
  flex-direction: column;
}
.dept-name {
  font-size: 14px;
  color: #3c4043;
  font-weight: 500;
  margin-bottom: 2px;
}
.major-name {
  font-size: 13px;
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

.text-secondary {
  color: #80868b;
  font-size: 13px;
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

/* Material 按钮优化 */
.md-button {
  border-radius: 20px;
  font-weight: 600;
  padding: 8px 20px;
  display: inline-flex; /* 开启 Flex 布局 */
  align-items: center; /* 图标和文字垂直居中 */
  justify-content: center; /* 图标和文字水平居中 */
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 基础海拔阴影 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.md-button:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15); /* 悬浮时阴影加深 */
  transform: translateY(-1px); /* 悬浮时微小上浮 */
}
.text-btn {
  color: #5f6368;
  box-shadow: none;
  background: transparent;
}
.text-btn:hover {
  background-color: #f1f3f4;
  color: #202124;
  box-shadow: none;
  transform: none;
}

/* 【操作列图标按钮】 */
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

/* 蓝色：编辑 */
.md-action-btn.el-button--primary {
  background-color: #e8f0fe;
  color: #1a73e8;
}
.md-action-btn.el-button--primary:hover {
  background-color: #1a73e8;
  color: #fff;
  box-shadow: 0 2px 6px rgba(26, 115, 232, 0.4);
}

/* 红色：删除 */
.md-action-btn.el-button--danger {
  background-color: #fce8e6;
  color: #ea4335;
}
.md-action-btn.el-button--danger:hover {
  background-color: #ea4335;
  color: #fff;
  box-shadow: 0 2px 6px rgba(234, 67, 53, 0.4);
}

/* 黄色：冻结 */
.md-action-btn.el-button--warning {
  background-color: #fef7e0;
  color: #fbbc04;
}
.md-action-btn.el-button--warning:hover {
  background-color: #fbbc04;
  color: #fff;
  box-shadow: 0 2px 6px rgba(251, 188, 4, 0.4);
}

/* 绿色：解冻 */
.md-action-btn.el-button--success {
  background-color: #e6f4ea;
  color: #34a853;
}
.md-action-btn.el-button--success:hover {
  background-color: #34a853;
  color: #fff;
  box-shadow: 0 2px 6px rgba(52, 168, 83, 0.4);
}npm
.md-action-btn:hover {
  background-color: #e8f0fe;
  border-radius: 50%;
}
.md-action-btn.el-button--danger:hover {
  background-color: #fce8e6;
}
.md-action-btn.el-button--warning:hover {
  background-color: #fef7e0;
}
.md-action-btn.el-button--success:hover {
  background-color: #e6f4ea;
}

/* 弹窗样式 */
.material-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 10px;
  border-bottom: none;
}
.material-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
}
.material-dialog :deep(.el-dialog__body) {
  padding: 10px 24px 20px;
}
.material-dialog :deep(.el-form-item__label) {
  font-weight: 600;
  color: #202124;
  margin-bottom: 4px;
  font-size: 13px;
}
.material-dialog :deep(.el-input__wrapper) {
  border-radius: 8px;
}
.md-divider {
  margin: 12px 0 24px;
  opacity: 0.6;
}
</style>
