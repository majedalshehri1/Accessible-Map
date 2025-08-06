<script setup>
// Vue 3 Composition API imports
import { ref } from 'vue'
import router from '@/router'

// shadcn-vue UI component imports
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

// Loading state
const isRegistering = ref(false)

// Register form data
const registerForm = ref({
  name: '',
  email: '',
  password: ''
})

// Register form validation
const validateRegisterForm = () => {
  if (!registerForm.value.name.trim()) {
    alert('يرجى إدخال الاسم')
    return false
  }
  if (!registerForm.value.email.trim()) {
    alert('يرجى إدخال البريد الإلكتروني')
    return false
  }
  if (!registerForm.value.password.trim()) {
    alert('يرجى إدخال كلمة المرور')
    return false
  }
  if (!registerForm.value.email.includes('@')) {
    alert('يرجى إدخال بريد إلكتروني صحيح')
    return false
  }
  if (registerForm.value.password.length < 8) {
    alert('كلمة المرور يجب أن تكون 8 أحرف على الأقل')
    return false
  }
  return true
}

// Handle register submission
const handleRegister = async () => {
  if (!validateRegisterForm()) return
  
  isRegistering.value = true
  
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Log form data for debugging
    console.log('Register data:', registerForm.value)
    
    // API call would be here
    // const response = await axios.post('/api/register', registerForm.value)
    
    alert('تم إنشاء الحساب بنجاح!')
    
    // Redirect to login page after successful registration
    router.push('/login')
    
  } catch (error) {
    console.error('Register error:', error)
    alert('حدث خطأ في إنشاء الحساب')
  } finally {
    isRegistering.value = false
  }
}

// Navigate to login page
const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <!-- Main container with RTL direction -->
  <div dir="rtl" class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 flex items-center justify-center py-12 px-4">
    
    <!-- Background decorative elements -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute top-10 right-10 w-72 h-72 bg-blue-200/30 rounded-full blur-3xl"></div>
      <div class="absolute bottom-10 left-10 w-80 h-80 bg-purple-200/30 rounded-full blur-3xl"></div>
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-96 h-96 bg-pink-200/20 rounded-full blur-3xl"></div>
    </div>

    <!-- Register Card -->
    <div class="relative z-10 w-full max-w-md">
      <Card class="shadow-2xl border-0 bg-white/95 backdrop-blur-sm">
        <CardHeader class="text-center pb-6">
          <CardTitle class="text-2xl font-bold text-slate-800">
            إنشاء حساب جديد
          </CardTitle>
          <p class="text-slate-600 mt-2">
            انضم إلينا اليوم
          </p>
        </CardHeader>

        <CardContent class="p-6">
          
          <!-- Register Form -->
          <form @submit.prevent="handleRegister" class="space-y-6">
            
            <!-- Name Input -->
            <div class="space-y-2">
              <Label for="register-name" class="text-sm font-medium text-slate-700">
                الاسم
              </Label>
              <Input
                id="register-name"
                type="text"
                v-model="registerForm.name"
                placeholder="أدخل اسمك"
                class="h-12 text-right border-slate-200 focus:border-blue-500"
                :disabled="isRegistering"
                required
              />
            </div>

            <!-- Email Input -->
            <div class="space-y-2">
              <Label for="register-email" class="text-sm font-medium text-slate-700">
                البريد الإلكتروني
              </Label>
              <Input
                id="register-email"
                type="email"
                v-model="registerForm.email"
                placeholder="أدخل بريدك الإلكتروني"
                class="h-12 text-right border-slate-200 focus:border-blue-500"
                :disabled="isRegistering"
                required
              />
            </div>

            <!-- Password Input -->
            <div class="space-y-2">
              <Label for="register-password" class="text-sm font-medium text-slate-700">
                كلمة المرور
              </Label>
              <Input
                id="register-password"
                type="password"
                v-model="registerForm.password"
                placeholder="أدخل كلمة مرور قوية"
                class="h-12 text-right border-slate-200 focus:border-blue-500"
                :disabled="isRegistering"
                required
              />
              <p class="text-xs text-slate-500">يجب أن تكون كلمة المرور 8 أحرف على الأقل</p>
            </div>

            <!-- Register Button -->
            <Button
              type="submit"
              class="w-full h-12 bg-blue-600 hover:bg-blue-700 cursor-pointer text-white font-medium"
              :disabled="isRegistering"
            >
              <span v-if="isRegistering">جاري إنشاء الحساب...</span>
              <span v-else>إنشاء الحساب</span>
            </Button>

            <!-- Switch to Login -->
            <div class="text-center pt-4 border-t border-slate-200">
              <p class="text-slate-600 mb-3">لديك حساب بالفعل؟</p>
              <Button
                type="button"
                variant="outline"
                @click="goToLogin"
                class="w-full h-12 border-slate-300 text-slate-700 hover:bg-slate-50 cursor-pointer"
                :disabled="isRegistering"
              >
                تسجيل الدخول
              </Button>
            </div>
          </form>

        </CardContent>
      </Card>

      <!-- Footer -->
      
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

/* Form animation */
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