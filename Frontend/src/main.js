import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'

import { createPinia } from 'pinia'
import { useAuthStore } from '@/stores/authStore'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'

const app = createApp(App)


const pinia = createPinia()
app.use(pinia)

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // global retries on failure
      retry: 2, 
    },
  },
});

app.use(VueQueryPlugin, { queryClient })

const auth = useAuthStore()

await auth.refreshProfile()

app.use(router)
app.mount('#app')
