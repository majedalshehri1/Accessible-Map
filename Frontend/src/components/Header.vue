<script setup>
import Button from './ui/button/Button.vue'
import Search from './Search.vue'
import UserDropdown from './UserDropdown.vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const auth = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  auth.logout()
  router.push('/')
}

const handleMenuClick = (action) => {
  switch (action) {
    case 'home':
      router.push('/')
      break
    case 'profile':
      router.push('/InfoAccount')
      break
    case 'add-location':
      router.push('/newPlace')
      break
    default:
      console.log('Unknown action:', action)
  }
}
</script>

<template>
  <header class="sticky top-0 z-[1000] border-b border-gray-200 bg-white/95 backdrop-blur-sm">
    <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div class="flex h-16 items-center justify-between">
        
        
        <div class="flex items-center flex-shrink-0 w-1/3">
          <div class="flex items-center">
            <span class="text-2xl font-bold text-blue-700">يسر</span>
          </div>
        </div>

        <!-- === Search === -->
        <div class="flex-1 max-w-lg mx-8">
          <Search />
        </div>
        <!-- === Search === -->
        
        <div class="flex items-center justify-end w-1/3">
          
          <!-- Logged In User -->
          <div v-if="auth.user" class="flex items-center gap-4">
            
            <!-- Welcome Message (Desktop Only) -->
            <div class="hidden lg:block text-sm text-gray-600">
              مرحبًا، <span class="font-semibold text-gray-900">{{ auth.user.username }}</span>
            </div>

            <!-- User Dropdown -->
            <UserDropdown 
              :user="auth.user" 
              @logout="handleLogout"
              @menu-click="handleMenuClick"
            />
          </div>

          <!-- Not Logged In -->
          <div v-else>
            <router-link to="/login">
              <Button class="bg-blue-600 hover:bg-blue-700 text-white font-medium px-6 py-2 rounded-lg transition-colors">
                تسجيل الدخول
              </Button>
            </router-link>
          </div>

        </div>
      </div>
    </div>
  </header>
</template>