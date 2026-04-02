import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
    {
      path: '/',
      redirect: '/login', // 根目录直接重定向到登录页
    },
    // ================= 管理员端专属路由 =================
    {
      path: '/admin',
      component: () => import('../views/AdminLayout.vue'), // 管理员专属布局
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('../views/admin/Dashboard.vue'),
        },
        {
          path: 'books',
          name: 'AdminBookManage',
          component: () => import('../views/admin/BookManage.vue'),
        },
        {
          path: 'users',
          name: 'AdminUserManage',
          component: () => import('../views/admin/UserManage.vue'),
        },
        {
          path: 'borrows',
          name: 'AdminBorrowManage',
          component: () => import('../views/admin/BorrowManage.vue'),
        },
        {
          path: 'fines',
          name: 'AdminFineManage',
          component: () => import('../views/admin/FineManage.vue'),
        },
        {
          path: 'reservations',
          name: 'AdminReservationManage',
          component: () => import('../views/admin/ReservationManage.vue'),
        },
        {
          path: 'reviews',
          name: 'AdminReviewManage',
          component: () => import('../views/admin/ReviewManage.vue'),
        },
        // 以后管理员的其他页面都在这里继续加...
      ],
    },
    // ================= 学生端专属路由 (暂不开发，仅占位) =================
    {
      path: '/student',
      component: () => import('../views/StudentLayout.vue'),
      redirect: '/student/home',
      children: [
        {
          path: 'home',
          name: 'StudentHome',
          component: () => import('../views/student/Home.vue'), // 图书大厅
        },
        {
          path: 'borrows',
          name: 'StudentBorrows',
          component: () => import('../views/student/MyBorrow.vue'), // 我的借阅
        },
        {
          path: 'profile',
          name: 'StudentProfile',
          component: () => import('../views/student/Profile.vue'), // 个人中心
        },
      ],
    },
  ],
})

// 路由守卫：未登录拦截 & 越权拦截
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.path !== '/login' && !token) {
    // 没登录，一律踢回登录页
    next('/login')
  } else if (to.path.startsWith('/admin') && role !== 'admin') {
    // 不是管理员想进后台，踢回登录页
    next('/login')
  } else {
    next()
  }
})

export default router
