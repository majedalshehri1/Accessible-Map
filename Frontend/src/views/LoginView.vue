<script setup>
// Vue 3 Composition API imports
import { ref } from 'vue'

// shadcn-vue UI component imports
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

// State to toggle between login and register
const isLoginMode = ref(true)

// Loading states
const isLoggingIn = ref(false)
const isRegistering = ref(false)

// Login form data
const loginForm = ref({
  email: '',
  password: ''
})

// Register form data
const registerForm = ref({
  name: '',
  email: '',
  password: ''
})

// Toggle between login and register modes
const toggleMode = () => {
  isLoginMode.value = !isLoginMode.value
  // Clear forms when switching
  loginForm.value = { email: '', password: '' }
  registerForm.value = { name: '', email: '', password: '' }
}

// Login form validation
const validateLoginForm = () => {
  if (!loginForm.value.email.trim()) {
    alert('يرجى إدخال البريد الإلكتروني')
    return false
  }
  if (!loginForm.value.password.trim()) {
    alert('يرجى إدخال كلمة المرور')
    return false
  }
  if (!loginForm.value.email.includes('@')) {
    alert('يرجى إدخال بريد إلكتروني صحيح')
    return false
  }
  return true
}

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

// Handle login submission
const handleLogin = async () => {
  if (!validateLoginForm()) return
  
  isLoggingIn.value = true
  
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Log form data for debugging
    console.log('Login data:', loginForm.value)
    
    // API call would be here
    // const response = await axios.post('/api/login', loginForm.value)
    
    alert('تم تسجيل الدخول بنجاح!')
    
    // Redirect user after successful login
    // router.push('/dashboard')
    
  } catch (error) {
    console.error('Login error:', error)
    alert('حدث خطأ في تسجيل الدخول')
  } finally {
    isLoggingIn.value = false
  }
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
    
    // Switch to login mode after successful registration
    isLoginMode.value = true
    
  } catch (error) {
    console.error('Register error:', error)
    alert('حدث خطأ في إنشاء الحساب')
  } finally {
    isRegistering.value = false
  }
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

    <!-- Auth Card -->
    <div class="relative z-10 w-full max-w-md">
      <Card class="shadow-2xl border-0 bg-white/95 backdrop-blur-sm">
        <CardHeader class="text-center pb-6">
          <div class="mx-auto w-16 h-16 bg-gradient-to-r from-blue-500 to-purple-600 rounded-full flex items-center justify-center mb-4">
            <svg class="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
            </svg>
          </div>
          <CardTitle class="text-2xl font-bold text-slate-800">
            {{ isLoginMode ? 'تسجيل الدخول' : 'إنشاء حساب جديد' }}
          </CardTitle>
          <p class="text-slate-600 mt-2">
            {{ isLoginMode ? 'أهلاً بك مرة أخرى' : 'انضم إلينا اليوم' }}
          </p>
        </CardHeader>

        <CardContent class="p-6">
          
          <!-- Login Form -->
          <form v-if="isLoginMode" @submit.prevent="handleLogin" class="space-y-6">
            
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

            <!-- Forgot Password Link -->
            <div class="text-left">
              <button type="button" class="text-sm text-blue-600 hover:text-blue-700 hover:underline">
                نسيت كلمة المرور؟
              </button>
            </div>

            <!-- Login Button -->
            <Button
              type="submit"
              class="w-full h-12 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white font-medium"
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
                @click="toggleMode"
                class="w-full h-12 border-slate-300 text-slate-700 hover:bg-slate-50"
                :disabled="isLoggingIn"
              >
                إنشاء حساب جديد
              </Button>
            </div>
          </form>

          <!-- Register Form -->
          <form v-if="!isLoginMode" @submit.prevent="handleRegister" class="space-y-6">
            
            <!-- Name Input -->
            <div class="space-y-2">
              <Label for="register-name" class="text-sm font-medium text-slate-700">
                الاسم الكامل
              </Label>
              <Input
                id="register-name"
                type="text"
                v-model="registerForm.name"
                placeholder="أدخل اسمك الكامل"
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
              <p class="text-xs text-slate-500">يجب أن تكون كلمة المرور 6 أحرف على الأقل</p>
            </div>

            <!-- Register Button -->
            <Button
              type="submit"
              class="w-full h-12 bg-gradient-to-r from-green-600 to-blue-600 hover:from-green-700 hover:to-blue-700 text-white font-medium"
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
                @click="toggleMode"
                class="w-full h-12 border-slate-300 text-slate-700 hover:bg-slate-50"
                :disabled="isRegistering"
              >
                تسجيل الدخول
              </Button>
            </div>
          </form>

        </CardContent>
      </Card>

      <!-- Footer -->
      <div class="text-center mt-8 text-slate-500 text-sm">
        <p>© 2024 جميع الحقوق محفوظة</p>
      </div>
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

/* Smooth transitions for form switching */
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