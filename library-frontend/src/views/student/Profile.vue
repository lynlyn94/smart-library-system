<template>
  <div class="profile-container" v-loading="loading">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-subtitle">管理你的个人档案与账号安全。</p>
    </div>

    <el-row :gutter="24">
      <el-col :xs="24" :md="8">
        <el-card class="material-card profile-card" shadow="never">
          <div class="avatar-section">
            <el-avatar :size="80" class="main-avatar">
              {{ userInfo.name ? userInfo.name.charAt(0) : 'U' }}
            </el-avatar>
            <h2 class="user-name">{{ userInfo.name || '未知用户' }}</h2>
            <p class="user-id">学号: {{ userInfo.studentId }}</p>
            <el-tag
              :type="userInfo.accountStatus === '正常' ? 'success' : 'danger'"
              effect="dark"
              round
              class="status-tag"
            >
              {{ userInfo.accountStatus || '正常' }}
            </el-tag>
          </div>

          <el-divider class="md-divider" />

          <div class="quota-section">
            <div class="quota-header">
              <span class="quota-title">借阅额度</span>
              <span class="quota-numbers">
                <strong :class="{ 'text-danger': userInfo.currentBorrow >= userInfo.maxBorrow }">
                  {{ userInfo.currentBorrow }}
                </strong>
                / {{ userInfo.maxBorrow }} 本
              </span>
            </div>
            <el-progress
              :percentage="borrowPercentage"
              :color="customColors"
              :stroke-width="10"
              :show-text="false"
            />
            <p class="quota-hint" v-if="userInfo.currentBorrow >= userInfo.maxBorrow">
              您的借阅额度已满，请先归还部分图书。
            </p>
            <p class="quota-hint" v-else>
              您还可以借阅 {{ userInfo.maxBorrow - userInfo.currentBorrow }} 本图书。
            </p>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="16">
        <el-card class="material-card form-card" shadow="never">
          <h3 class="form-title">基本信息与设置</h3>

          <el-form :model="userInfo" :rules="rules" ref="formRef" label-position="top">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="院系">
                  <el-input v-model="userInfo.department" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="专业">
                  <el-input v-model="userInfo.major" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年级">
                  <el-input v-model="userInfo.grade" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="班级">
                  <el-input v-model="userInfo.class" disabled />
                </el-form-item>
              </el-col>

              <el-divider class="md-divider" border-style="dashed" />

              <el-col :span="12">
                <el-form-item label="联系电话" prop="phone">
                  <el-input
                    v-model="userInfo.phone"
                    placeholder="请输入最新手机号"
                    prefix-icon="Iphone"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="电子邮箱" prop="email">
                  <el-input
                    v-model="userInfo.email"
                    placeholder="请输入常用邮箱"
                    prefix-icon="Message"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="登录密码" prop="password">
                  <el-input
                    v-model="userInfo.password"
                    type="password"
                    show-password
                    placeholder="输入新密码即可修改"
                    prefix-icon="Lock"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <div class="form-actions">
              <el-button
                type="primary"
                class="md-button"
                @click="handleSave"
                :loading="saveLoading"
              >
                保存修改
              </el-button>
            </div>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Iphone, Message, Lock } from '@element-plus/icons-vue'
import request from '../../utils/request'

const studentId = ref(localStorage.getItem('userId') || '')
const loading = ref(false)
const saveLoading = ref(false)
const formRef = ref()

// 个人信息数据模型
const userInfo = reactive({
  studentId: '',
  name: '',
  gender: '',
  department: '',
  major: '',
  grade: '',
  class: '',
  phone: '',
  email: '',
  password: '',
  accountStatus: '正常',
  currentBorrow: 0,
  maxBorrow: 5,
})

// 简单的表单校验
const rules = {
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号码', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
}

// 计算借阅百分比用于进度条
const borrowPercentage = computed(() => {
  if (userInfo.maxBorrow === 0) return 100
  const pct = (userInfo.currentBorrow / userInfo.maxBorrow) * 100
  return pct > 100 ? 100 : pct
})

// 进度条颜色渐变：越接近满额颜色越红
const customColors = [
  { color: '#34a853', percentage: 50 },
  { color: '#fbbc04', percentage: 80 },
  { color: '#ea4335', percentage: 100 },
]

// 获取当前登录学生的数据
const loadProfile = async () => {
  if (!studentId.value) return
  loading.value = true
  try {
    // 巧妙复用后端的 list 接口，通过关键字精准查找自己的信息
    const res: any = await request.get('/api/students/list', {
      params: { current: 1, size: 10, keyword: studentId.value },
    })

    // 找到完全匹配自己学号的那条数据
    const myData = res.data.records.find((u: any) => u.studentId === studentId.value)
    if (myData) {
      Object.assign(userInfo, myData)
    } else {
      ElMessage.error('获取个人信息失败，请联系管理员')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 提交修改
const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      saveLoading.value = true
      try {
        // 调用管理员写好的更新接口
        await request.put('/api/students/update', userInfo)
        ElMessage.success('个人资料保存成功！')
        loadProfile() // 重新拉取最新数据
      } catch (error) {
        // error 由拦截器处理
      } finally {
        saveLoading.value = false
      }
    }
  })
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-container {
  padding-bottom: 40px;
}
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

/* Material 卡片基础样式 */
.material-card {
  border-radius: 16px;
  border: none;
  background-color: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03) !important;
  margin-bottom: 20px;
}
.md-divider {
  margin: 24px 0;
  opacity: 0.6;
}

/* ======== 左侧名片区 ======== */
.profile-card {
  padding: 10px;
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px 0 10px;
}
.main-avatar {
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(135deg, #1a73e8, #4285f4);
  color: #fff;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(26, 115, 232, 0.3);
}
.user-name {
  font-size: 20px;
  color: #202124;
  margin: 0 0 4px 0;
  font-weight: 600;
}
.user-id {
  font-size: 14px;
  color: #5f6368;
  margin: 0 0 12px 0;
}
.status-tag {
  padding: 0 20px;
}

/* 额度进度条 */
.quota-section {
  padding: 10px;
}
.quota-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 12px;
}
.quota-title {
  font-weight: 600;
  color: #202124;
}
.quota-numbers {
  font-size: 14px;
  color: #5f6368;
}
.text-danger {
  color: #ea4335;
  font-size: 18px;
}
.quota-hint {
  font-size: 12px;
  color: #80868b;
  margin-top: 12px;
  text-align: center;
}

/* ======== 右侧表单区 ======== */
.form-card {
  padding: 20px;
}
.form-title {
  font-size: 18px;
  color: #202124;
  margin: 0 0 24px 0;
  font-weight: 600;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
.md-button {
  border-radius: 20px;
  font-weight: 600;
  padding: 8px 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}
.md-button:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}
.el-form-item__label {
  font-weight: 600;
  color: #3c4043;
}
</style>
