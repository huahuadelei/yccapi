import "babel-polyfill"


import Vue from 'vue'
import App from './App.vue'
import router from '@/router'
import store from '@/store'
import server from '@/server'
import ViewUI from 'view-design';
import Loading from 'vue-loading-easy'
import StorageHelper from '@/utils/StorageHelper'
import 'view-design/dist/styles/iview.css';


Vue.use(Loading)
Vue.use(ViewUI)

Vue.prototype.$server=server;
Vue.prototype.$bus=new Vue(); 
Vue.prototype.$bus.storage=StorageHelper;
Vue.config.productionTip = false

new Vue({
  store,
  router,
  render: h => h(App),
}).$mount('#app')
