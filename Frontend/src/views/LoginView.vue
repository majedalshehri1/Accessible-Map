<script setup>
import { ref, computed } from 'vue'
import router from '@/router'

// shadcn-vue UI component imports
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from 'vue-sonner'

import { useAuthStore } from '@/stores/authStore'

// state
const auth = useAuthStore()

const isLoggingIn = computed(() => auth.loading) // Update

const loginForm = ref({
  email: '',
  password: ''
})

// Login form validation
const validateLoginForm = () => {
  if (!loginForm.value.email.trim()) {
    toast.error("يرجى إدخال البريد الإلكتروني")
    return false
  }
  if (!loginForm.value.password.trim()) {
    toast.error("يرجى إدخال كلمة المرور")
    return false
  }
  if (!loginForm.value.email.includes('@')) {
    toast.error("يرجى إدخال بريد إلكتروني صحيح")
    return false
  }
  return true
}

// Handle login submission
const handleLogin = async () => {
  if (!validateLoginForm()) return

  try {
    const { email, password } = loginForm.value
    
    await auth.login({ email, password })

    
    toast.success("تم تسجيل الدخول بنجاح", {
      description: `مرحبًا ${auth.user?.username || ''}!` 
    })

    router.push('/')
  } catch (err) {
    console.error('Login error:', err) // Log the error for debugging
    toast.error("فشل تسجيل الدخول", {
      description: auth.error || "يرجى التحقق من بيانات الاعتماد الخاصة بك"
    })
  }
}

// Navigate to Register view
const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div dir="rtl" class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 flex items-center justify-center py-12 px-4">
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute top-10 right-10 w-72 h-72 bg-blue-200/30 rounded-full blur-3xl"></div>
      <div class="absolute bottom-10 left-10 w-80 h-80 bg-purple-200/30 rounded-full blur-3xl"></div>
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-pink-200/20 rounded-full blur-3xl"></div>
    </div>

    <div class="relative z-10 w-full max-w-md">
      <Card class="shadow-2xl border-0 bg-white/95 backdrop-blur-sm">
        <CardHeader class="text-center pb-6">
          <CardTitle class="text-2xl font-bold text-slate-800">
            تسجيل الدخول
          </CardTitle>
          <p class="text-slate-600 mt-2">
            أهلاً بك مرة أخرى
          </p>
        </CardHeader>

        <CardContent class="p-6">
          
          <!-- Login Form -->
          <form @submit.prevent="handleLogin" class="space-y-6">
            
            <!-- Email Input -->
            <div class="space-y-2">
              <Label for="login-email" class="text-sm font-medium text-slate-700">
                البريد الإلكتروني
              </Label>
              <Input
                id="login-email"
                type="email"
                v-model="loginForm.email"
                placeholder="أدخل بريدك الإلكتروني"
                class="h-12 text-right border-slate-200 focus:border-blue-500"
                :disabled="isLoggingIn"
                required
              />
            </div>

            <!-- Password Input -->
            <div class="space-y-2">
              <Label for="login-password" class="text-sm font-medium text-slate-700">
                كلمة المرور
              </Label>
              <Input
                id="login-password"
                type="password"
                v-model="loginForm.password"
                placeholder="أدخل كلمة المرور"
                class="h-12 text-right border-slate-200 focus:border-blue-500"
                :disabled="isLoggingIn"
                required
              />
            </div>

            <!-- Login Button -->
            <Button
              type="submit"
              class="w-full h-12 bg-blue-600 hover:bg-blue-700 cursor-pointer text-white font-medium"
              :disabled="isLoggingIn"
            >
              <span v-if="isLoggingIn">جاري تسجيل الدخول...</span>
              <span v-else>تسجيل الدخول</span>
            </Button>
            <!-- Switch to Register -->
            <div class="text-center pt-4 border-t border-slate-200">
              <p class="text-slate-600 mb-3">ليس لديك حساب؟</p>
              <Button
                type="button"
                variant="outline"
                @click="goToRegister"
                class="w-full h-12 border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer"
                :disabled="isLoggingIn"
              >
                إنشاء حساب جديد
              </Button>
            </div>
          </form>

        </CardContent>
      </Card>
      
    </div>
  </div>
</template>

<style scoped>
/* Arabic font import */
@import url("https://fonts.googleapis.com/css2?family=Cairo:wght@200;300;400;500;600;700;800;900&display=swap");

/* Global font family application */
* {
  font-family: "Cairo", sans-serif;
}

/* Smooth transitions */
form {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Custom input styling */
input::placeholder {
  color: #94a3b8;
}
</style>