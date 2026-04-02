import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router' //

const app = createApp(App)
app.use(ElementPlus)
app.use(router) // ✨ 必须加上这一行，路由才能生效！
app.mount('#app')
