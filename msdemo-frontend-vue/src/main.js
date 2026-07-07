import { createApp } from 'vue'
import App from './App.vue'

import router from './router';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';

// 链式调用
createApp(App).use(Antd).use(router).mount('#app')
