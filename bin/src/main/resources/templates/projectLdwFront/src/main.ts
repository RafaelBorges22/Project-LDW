import { createApp } from 'vue'
import App from './App.vue'
import router from './routers'
import './assets/Scss/global/Global.scss'

createApp(App)
    .use(router)
    .mount('#app');