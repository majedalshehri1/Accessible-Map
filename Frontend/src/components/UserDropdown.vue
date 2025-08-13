<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Home as HomeIcon, Plus as PlusIcon, User as UserIcon, LogOut as LogOutIcon } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/authStore'

const props = defineProps({
  user: {
    type: Object,
    required: false,
    default: null
  }
})

const emit = defineEmits(['logout', 'menu-click'])

const auth = useAuthStore()

const currentUser = computed(() => auth.user || props.user || null)

const userInitial = computed(() => {
  const nameOrEmail = currentUser.value?.username || currentUser.value?.name || currentUser.value?.email || 'U'
  return String(nameOrEmail).charAt(0).toUpperCase()
})

const displayName = computed(() => currentUser.value?.username || currentUser.value?.name || 'مستخدم')
const displayEmail = computed(() => currentUser.value?.email || '')

const isOpen = ref(false)
const dropdownRef = ref(null)

const toggleDropdown = () => {
  if (!currentUser.value) return
  isOpen.value = !isOpen.value
}
const closeDropdown = () => { isOpen.value = false }

const handleLogout = () => {
  closeDropdown()
  auth.logout()
  emit('logout')
}

const handleMenuClick = (action) => {
  closeDropdown()
  emit('menu-click', action)
}

// Close dropdown on outside click
const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    closeDropdown()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  if (!auth.user) auth.restore?.()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="relative" ref="dropdownRef">
    <!-- User Avatar Button -->
    <button
      @click="toggleDropdown"
      class="w-10 h-10 flex items-center justify-center bg-blue-100 text-blue-700 rounded-full font-semibold hover:bg-blue-200 transition-colors disabled:opacity-50"
      :disabled="!currentUser"
      :title="currentUser ? displayName : 'سجّل الدخول'"
    >
      {{ userInitial }}
    </button>

    <!-- Dropdown Menu -->
    <div
      v-if="isOpen && currentUser"
      class="absolute left-0 mt-2 w-72 bg-white rounded-lg shadow-lg border border-gray-200 py-2 z-[1001]"
    >
      <div class="px-4 py-3 border-b border-gray-100">
        <div class="flex items-center gap-3">
          <div class="w-12 h-12 flex items-center justify-center bg-blue-100 text-blue-700 rounded-full font-bold text-lg relative">
            {{ userInitial }}
            <!-- Online Status -->
            <div class="absolute -bottom-1 -right-1 w-4 h-4 bg-green-500 rounded-full border-2 border-white"></div>
          </div>
          <div class="flex-1 min-w-0">
            <p class="font-semibold text-gray-900 truncate">{{ displayName }}</p>
            <p class="text-sm text-gray-500 truncate" v-if="displayEmail">{{ displayEmail }}</p>
          </div>
        </div>
      </div>

      <!-- Menu Items -->
      <div class="py-1">
        <!-- Home -->
        <button
          @click="handleMenuClick('home')"
          class="flex items-center w-full px-4 py-3 text-right hover:bg-gray-50 transition-colors group"
        >
          <div class="w-8 h-8 flex items-center justify-center bg-blue-50 rounded-lg ml-3 group-hover:bg-blue-100 transition-colors">
            <HomeIcon class="w-4 h-4 text-blue-600" />
          </div>
          <span class="text-gray-700 font-medium">الرئيسية</span>
        </button>

        <!-- Profile -->
        <button
          @click="handleMenuClick('profile')"
          class="flex items-center w-full px-4 py-3 text-right hover:bg-gray-50 transition-colors group"
        >
          <div class="w-8 h-8 flex items-center justify-center bg-purple-50 rounded-lg ml-3 group-hover:bg-purple-100 transition-colors">
            <UserIcon class="w-4 h-4 text-purple-600" />
          </div>
          <span class="text-gray-700 font-medium">الملف الشخصي</span>
        </button>

        <!-- Add New Location -->
        <button
          @click="handleMenuClick('add-location')"
          class="flex items-center w-full px-4 py-3 text-right hover:bg-gray-50 transition-colors group"
        >
          <div class="w-8 h-8 flex items-center justify-center bg-green-50 rounded-lg ml-3 group-hover:bg-green-100 transition-colors">
            <PlusIcon class="w-4 h-4 text-green-600" />
          </div>
          <span class="text-gray-700 font-medium">إضافة مكان جديد</span>
        </button>

        <!-- Logout -->
        <button
          @click="handleLogout"
          class="flex items-center w-full px-4 py-3 text-right hover:bg-red-50 transition-colors group"
        >
          <div class="w-8 h-8 flex items-center justify-center bg-red-50 rounded-lg ml-3 group-hover:bg-red-100 transition-colors">
            <LogOutIcon class="w-4 h-4 text-red-600" />
          </div>
          <span class="text-red-600 font-medium">تسجيل الخروج</span>
        </button>
      </div>
    </div>

    <!-- Backdrop -->
    <div v-if="isOpen" @click="closeDropdown" class="fixed inset-0 z-[999]"></div>
  </div>
</template>
