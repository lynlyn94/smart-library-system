# 📚 智慧图书馆管理系统 (Smart Library System)

基于 Vue 3 + Spring Boot 构建的现代化企业级图书管理系统。

## ✨ 核心特性
- **UI/UX**：采用 Google Material Design 扁平化设计规范，响应式布局。
- **角色分离**：包含拥有炫酷仪表盘的管理员端 (Admin Workspace) 与沉浸式的学生端 (Student Portal)。
- **核心闭环**：借书扣减库存 ⇌ 学生额度增减 ⇌ 自动生成借阅流水。
- **高阶功能**：
  - 💰 **逾期罚款**：自动计算逾期记录，支持线下结清。
  - 📅 **图书预约**：热门图书无库存时支持排队预约及状态流转。
  - 💬 **评价审核**：基于真实借阅历史的 UGC 评价体系与后台违规审核。

## 🛠️ 技术栈
- **前端**：Vue 3 + Element Plus + Vite + Vue Router
- **后端**：Spring Boot 3 + MyBatis-Plus + MySQL 8 + RESTful API

## 🚀 快速启动
1. 将 `library.sql` 导入 MySQL 数据库。
2. 后端：修改 `application.yml` 数据库密码，使用 Maven 运行。
3. 前端：进入 `library-frontend` 目录，执行 `npm install` 后 `npm run dev`。
