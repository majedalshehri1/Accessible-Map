<script setup>
// Vue 3 Composition API imports
import { ref } from 'vue'

// shadcn-vue UI component imports
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { Button } from "@/components/ui/button"
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "@/components/ui/select"
import { Card, CardContent } from "@/components/ui/card"

// Form data reactive reference - Contains all form fields
const form = ref({
  name: '',
  category: '',
  location: { lat: null, lng: null },
  images: null,
  services: [], // Array to store selected services
  agree: false
})

// Available services list - Services that can be selected for the place
const availableServices = ref([
  { id: 'parking', name: 'مواقف المقعدين' },
  { id: 'water', name: 'دورات المياه' },
  { id: 'elevators', name: 'المنحدرات'},
  { id: 'lifts', name: 'المصاعد' },
  { id: 'auto_doors', name: 'أبواب أوتوماتيكية' },
  { id: 'food_tables', name: 'طاولات الطعام' }
])

// Loading state for form submission
const isLoading = ref(false)

// Form validation function - Check if required fields are filled
const validateForm = () => {
  if (!form.value.name.trim()) {
    alert('يرجى إدخال اسم المكان')
    return false
  }
  if (!form.value.category) {
    alert('يرجى اختيار فئة المكان')
    return false
  }
  if (!form.value.agree) {
    alert('يجب الموافقة على الشروط أولاً')
    return false
  }
  return true
}

// Toggle service selection - Add or remove service from selected list
const toggleService = (serviceId) => {
  const index = form.value.services.indexOf(serviceId)
  if (index > -1) {
    // Remove service if already selected
    form.value.services.splice(index, 1)
  } else {
    // Add service if not selected
    form.value.services.push(serviceId)
  }
}

// Check if service is selected - Return true if service is in selected list
const isServiceSelected = (serviceId) => {
  return form.value.services.includes(serviceId)
}
const handleFileChange = (event) => {
  const files = event.target.files
  if (files && files.length > 0) {
    form.value.images = files
  }
}

// Form submission handler - Prepare data for backend
const submitForm = async () => {
  // Validate form before submission
  if (!validateForm()) return

  // Set loading state
  isLoading.value = true

  try {
    // Create FormData object for file upload
    const formData = new FormData()
    
    // Append text fields
    formData.append('name', form.value.name)
    formData.append('category', form.value.category)
    
    // Append location data as JSON string
    formData.append('location', JSON.stringify(form.value.location))
    
    // Append selected services as JSON array
    formData.append('services', JSON.stringify(form.value.services))
    
    // Append images if selected
    if (form.value.images) {
      for (let i = 0; i < form.value.images.length; i++) {
        formData.append('images', form.value.images[i])
      }
    }
    
    // Append agreement status
    formData.append('agree', form.value.agree)

    // Log form data for debugging (remove in production)
    console.log("Form data prepared for backend:", {
      name: form.value.name,
      category: form.value.category,
      location: form.value.location,
      services: form.value.services,
      images: form.value.images ? form.value.images.length + ' files' : 'No files',
      agree: form.value.agree
    })

    // Here you would make the API call to your backend
    // Example: await axios.post('/api/places', formData)
    
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Success message
    alert('تم إرسال الطلب بنجاح!')
    
    // Reset form after successful submission
    resetForm()
    
  } catch (error) {
    console.error('Error submitting form:', error)
    alert('حدث خطأ أثناء إرسال الطلب. يرجى المحاولة مرة أخرى.')
  } finally {
    // Reset loading state
    isLoading.value = false
  }
}

// Reset form function - Clear all form fields
const resetForm = () => {
  form.value = {
    name: '',
    category: '',
    location: { lat: null, lng: null },
    images: null,
    services: [], // Reset services array
    agree: false
  }
  // Clear file input
  const fileInput = document.getElementById('picture')
  if (fileInput) fileInput.value = ''
}

// Handle map click - Update location coordinates
const handleMapClick = (event) => {
  // This will be implemented when you integrate with actual map
  console.log('Map clicked - implement with your map library')
  // Example: form.value.location = { lat: event.lat, lng: event.lng }
}

// Go back function - Handle navigation
const goBack = () => {
  // Implement navigation logic here
  console.log('Going back...')
  // Example: router.go(-1) or router.push('/previous-page')
}
</script>

<template>
  <!-- Main container with RTL direction and modern styling -->
  <div dir="rtl" class="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 py-8 px-4">
    <div class="max-w-2xl mx-auto">
      
      <!-- Header section -->
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-slate-800 mb-2">طلب إضافة مكان جديد</h1>
        <p class="text-slate-600 text-lg">أضف مكانك التي تريد تقييمة</p>
      </div>

      <!-- Main form card -->
      <Card class="shadow-xl border-0 bg-white/80 backdrop-blur-sm">
        <CardContent class="p-8">
          <!-- Form element with submit prevention -->
          <form @submit.prevent="submitForm" class="space-y-8">
            
            <!-- Basic information section -->
            <div class="space-y-6">
              <!-- Place name and category row -->
              <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <!-- Place name input (takes 2 columns on large screens) -->
                <div class="lg:col-span-2 space-y-2">
                  <Label for="place-name" class="text-sm font-medium text-slate-700">
                    اسم المكان <span class="text-red-500">*</span>
                  </Label>
                  <Input 
                    id="place-name"
                    v-model="form.name" 
                    placeholder="ادخل اسم المكان" 
                    class="h-12 text-right border-slate-200 focus:border-blue-500 focus:ring-blue-500/20"
                    :disabled="isLoading"
                    required
                  />
                </div>

                <!-- Category selection -->
                <div class="space-y-2">
                  <Label for="category" class="text-sm font-medium text-slate-700">
                    فئة المكان <span class="text-red-500">*</span>
                  </Label>
                  <Select v-model="form.category" :disabled="isLoading">
                    <SelectTrigger class="w-full border-slate-200 focus:border-blue-500 cursor-pointer">
                      <SelectValue placeholder="اختر الفئة" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="restaurant">مطعم</SelectItem>
                      <SelectItem value="cafe">كافيه</SelectItem>
                      <SelectItem value="hospital">مستشفى</SelectItem>
                      <SelectItem value="mall">مول</SelectItem>
                      <SelectItem value="park">حديقة</SelectItem>
                      <SelectItem value="gas-station">محطة وقود</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>

            <!-- Services selection section -->
            <div class="space-y-6 border-t pt-6">
              <div class="space-y-4">
                <Label class="text-lg font-semibold text-slate-700">الخدمات المقدمة</Label>
                <p class="text-sm text-slate-500">اختر الخدمات المتوفرة في هذا المكان</p>
                
                <!-- Services grid -->
                <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
                  <div 
                    v-for="service in availableServices" 
                    :key="service.id"
                    @click="toggleService(service.id)"
                    class="relative group cursor-pointer transition-all duration-300 transform hover:scale-105"
                    :class="{ 'pointer-events-none opacity-50': isLoading }"
                  >
                    <!-- Service card -->
                    <div 
                      class="p-4 rounded-xl border-2 transition-all duration-300 text-center min-h-[100px] flex flex-col items-center justify-center space-y-2"
                      :class="isServiceSelected(service.id) 
                        ? 'border-blue-500 bg-blue-50 shadow-lg shadow-blue-100' 
                        : 'border-slate-200 bg-white hover:border-blue-300 hover:shadow-md'"
                    >
                      <!-- Service icon -->
                      <!-- <div class="text-3xl mb-1">{{ service.icon }}</div> -->
                      
                      <!-- Service name -->
                      <span 
                        class="text-sm font-medium transition-colors duration-300"
                        :class="isServiceSelected(service.id) ? 'text-blue-700' : 'text-slate-700'"
                      >
                        {{ service.name }}
                      </span>
                      
                      <!-- Selection indicator -->
                      <div 
                        v-if="isServiceSelected(service.id)"
                        class="absolute -top-2 -right-2 w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center shadow-lg"
                      >
                        <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                          <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- Selected services summary -->
                <div v-if="form.services.length > 0" class="mt-4 p-3 bg-blue-50 rounded-lg border border-blue-200">
                  <p class="text-sm text-blue-700 font-medium">
                    تم اختيار {{ form.services.length }} خدمة
                  </p>
                </div>
              </div>
            </div>

            <!-- Location and images section -->
            <div class="space-y-6 border-t pt-6">
              <!-- Map placeholder -->
              <div class="space-y-2">
                <Label class="text-sm font-medium text-slate-700">اختر الموقع على الخريطة</Label>
                <div 
                  @click="handleMapClick"
                  class="h-80 bg-gradient-to-br from-slate-100 to-slate-200 rounded-xl border-2 border-dashed border-slate-300 flex flex-col items-center justify-center text-slate-500 hover:border-blue-400 transition-colors duration-200 cursor-pointer"
                  :class="{ 'opacity-50 cursor-not-allowed': isLoading }"
                >
                  <div class="text-6xl mb-4">🗺️</div>
                  <p class="text-lg font-medium">خريطة تفاعلية</p>
                  <p class="text-sm">انقر لتحديد الموقع</p>
                  <!-- Display selected coordinates if available -->
                  <div v-if="form.location.lat && form.location.lng" class="mt-2 text-xs text-green-600">
                    الموقع المحدد: {{ form.location.lat.toFixed(6) }}, {{ form.location.lng.toFixed(6) }}
                  </div>
                </div>
              </div>

              <!-- Image upload -->
              <div class="space-y-2">
                <Label for="picture" class="text-sm font-medium text-slate-700">إضافة صور للمكان</Label>
                <div class="relative">
                  <!-- File upload input with change handler -->
                  <Input 
                    class="h-12 cursor-pointer text-blue-600" 
                    id="picture" 
                    type="file" 
                    multiple
                    accept="image/*"
                    @change="handleFileChange"
                    :disabled="isLoading"
                  />
                </div>
                <p class="text-xs text-slate-500">يمكنك اختيار عدة صور (أقصى حد 10 صور)</p>
                <!-- Display selected files count -->
                <div v-if="form.images && form.images.length > 0" class="text-xs text-green-600">
                  تم اختيار {{ form.images.length }} صورة
                </div>
              </div>
            </div>

            <!-- Terms and conditions -->
            <div class="border-t pt-6">
              <div class="flex items-start gap-3 p-4 bg-slate-50 rounded-lg">
                <Checkbox
                  v-model:checked="form.agree"
                  id="agree"
                  class="mt-1"
                  :disabled="isLoading"
                />
                <div class="space-y-1">
                  <Label for="agree" class="text-sm font-medium text-slate-700 cursor-pointer">
                    أوافق على الشروط والأحكام وسياسة الخصوصية
                  </Label>
                  <p class="text-xs text-slate-500">
                    بالموافقة، أنت تؤكد أن جميع المعلومات المقدمة صحيحة ودقيقة
                  </p>
                </div>
              </div>
            </div>

            <!-- Action buttons -->
            <div class="flex flex-col sm:flex-row gap-4 justify-between pt-6">
              <!-- Back button -->
              <Button 
                type="button"
                variant="outline" 
                size="lg"
                class="order-2 sm:order-1 h-12 px-8 border-slate-300 hover:bg-slate-50 cursor-pointer"
                @click="goBack"
                :disabled="isLoading"
              >
                الرجوع
              </Button>
              <!-- Submit button -->
              <Button 
                type="submit"
                size="lg"
                class="order-1 sm:order-2 h-12 px-8 bg-blue-600 hover:bg-blue-700 text-white shadow-lg hover:shadow-xl transition-all duration-200 cursor-pointer"
                :disabled="!form.agree || isLoading"
              >
                <!-- Show loading state or normal text -->
                <span v-if="isLoading">جاري الإرسال...</span>
                <span v-else>إرسال الطلب</span>
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
@import url("https://fonts.googleapis.com/css2?family=Cairo:wght@200;300;400;500;600;700;800;909&display=swap");

/* Global font family application */
* {
  font-family: "Cairo", sans-serif;
}

/* Body font family */
body {
  font-family: "Cairo", sans-serif;
}

/* Custom scrollbar for better UX */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>