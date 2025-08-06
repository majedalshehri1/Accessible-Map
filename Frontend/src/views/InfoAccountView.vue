<script setup>
// Vue 3 Composition API imports
import { ref, computed, onMounted } from 'vue'

// shadcn-vue UI component imports
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"

// Footer component
import Footer from '@/components/Footer.vue'

// Categories data from backend (enum mapping)
const categories = ref([])

// Loading state for categories
const isCategoriesLoading = ref(false)

// User profile data
const userProfile = ref({
  id: 1,
  name: 'أحمد محمد',
  email: 'ahmed@example.com',
  joinDate: '2024-01-15'
})

// User reviews data
const userReviews = ref([
  {
    id: 1,
    placeName: 'مطعم الشرقي',
    placeCategory: 'RESTAURANT', // Backend enum value
    comment: 'مطعم رائع والطعام لذيذ جداً، الخدمة ممتازة والأسعار معقولة. أنصح بتجربة المشاوي.'
  },
  {
    id: 2,
    placeName: 'كافيه النخيل',
    placeCategory: 'COFFEE', // Backend enum value
    comment: 'كافيه هادئ ومناسب للعمل، القهوة جيدة لكن الأسعار مرتفعة قليلاً.'
  },
  {
    id: 3,
    placeName: 'مستشفى الملك فهد',
    placeCategory: 'HOSPITAL', // Backend enum value
    comment: 'مستشفى متميز، الأطباء محترفون والنظافة ممتازة، لكن أوقات الانتظار طويلة.'
  }
])

// Loading states
const isUpdatingProfile = ref(false)
const isEditingReview = ref(false)

// Dialog state for delete confirmation
const showConfirmDialog = ref(false)
const reviewToDelete = ref(null)

// Edit mode states
const isEditingProfile = ref(false)
const editingReviewId = ref(null)

// Form data
const editProfileForm = ref({ name: '' })
const editReviewForm = ref({ comment: '' })

// Computed property for total reviews
const totalReviews = computed(() => userReviews.value.length)

// API function to fetch categories from backend
const fetchCategories = async () => {
  isCategoriesLoading.value = true
  
  try {
    const response = await fetch('')
    // const data = await response.json()
    
    // Simulate API call - replace with actual backend call
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // Mock response that matches backend enum structure
    const mockCategories = [
      { value: 'RESTAURANT', label: 'مطعم', color: 'bg-orange-100 text-orange-700' },
      { value: 'MALL', label: 'مول', color: 'bg-purple-100 text-purple-700' },
      { value: 'COFFEE', label: 'كافيه', color: 'bg-amber-100 text-amber-700' },
      { value: 'HOSPITAL', label: 'مستشفى', color: 'bg-red-100 text-red-700' },
      { value: 'PARK', label: 'حديقة', color: 'bg-green-100 text-green-700' }
    ]
    
    categories.value = mockCategories
    
  } catch (error) {
    console.error('Error fetching categories:', error)
    
    categories.value = []
  } finally {
    isCategoriesLoading.value = false
  }
}

// Function to get category display name from enum value
const getCategoryLabel = (enumValue) => {
  const category = categories.value.find(cat => cat.value === enumValue)
  return category ? category.label : enumValue
}

// Function to get category color from enum value
const getCategoryColor = (enumValue) => {
  const category = categories.value.find(cat => cat.value === enumValue)
  return category ? category.color : 'bg-gray-100 text-gray-700'
}

// Initialize component - fetch categories on mount
onMounted(() => {
  fetchCategories()
})

// Profile editing functions
const startEditingProfile = () => {
  editProfileForm.value.name = userProfile.value.name
  isEditingProfile.value = true
}

const cancelEditingProfile = () => {
  isEditingProfile.value = false
  editProfileForm.value.name = ''
}

const saveProfile = async () => {
  if (!editProfileForm.value.name.trim()) return
  
  isUpdatingProfile.value = true
  
  try {
    // API call: await updateUserProfile(userProfile.value.id, { name: editProfileForm.value.name })
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    userProfile.value.name = editProfileForm.value.name
    isEditingProfile.value = false
    
  } catch (error) {
    console.error('Error updating profile:', error)
  } finally {
    isUpdatingProfile.value = false
  }
}

// Review editing functions
const startEditingReview = (review) => {
  editingReviewId.value = review.id
  editReviewForm.value.comment = review.comment
}

const cancelEditingReview = () => {
  editingReviewId.value = null
  editReviewForm.value.comment = ''
}

const saveReview = async (reviewId) => {
  if (!editReviewForm.value.comment.trim()) return
  
  isEditingReview.value = true
  
  try {
    // API call: await updateReviewComment(reviewId, editReviewForm.value.comment)
    await new Promise(resolve => setTimeout(resolve, 800))
    
    const reviewIndex = userReviews.value.findIndex(r => r.id === reviewId)
    if (reviewIndex !== -1) {
      userReviews.value[reviewIndex].comment = editReviewForm.value.comment
    }
    
    cancelEditingReview()
    
  } catch (error) {
    console.error('Error updating review:', error)
  } finally {
    isEditingReview.value = false
  }
}

// Delete review functions
const confirmDeleteReview = (reviewId) => {
  reviewToDelete.value = reviewId
  showConfirmDialog.value = true
}

const deleteReview = async () => {
  showConfirmDialog.value = false
  
  try {
    // API call: await deleteUserReview(reviewToDelete.value)
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const reviewIndex = userReviews.value.findIndex(r => r.id === reviewToDelete.value)
    if (reviewIndex !== -1) {
      userReviews.value.splice(reviewIndex, 1)
    }
    
  } catch (error) {
    console.error('Error deleting review:', error)
  } finally {
    reviewToDelete.value = null
  }
}

const closeConfirmDialog = () => {
  showConfirmDialog.value = false
  reviewToDelete.value = null
}
</script>

<template>
  <!-- Main container -->
  <div dir="rtl" class="min-h-screen bg-slate-50 relative overflow-hidden">
    
    <!-- Background elements -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div class="absolute -top-32 -right-32 w-96 h-96 bg-blue-100/20 rounded-full blur-3xl"></div>
      <div class="absolute -bottom-24 -left-24 w-64 h-64 bg-green-100/30 rounded-full blur-2xl"></div>
      <div class="absolute top-1/2 left-1/4 w-32 h-32 bg-purple-100/20 rounded-full blur-xl"></div>
    </div>

    <!-- Content wrapper -->
    <div class="relative z-10 py-8 px-4">
      <div class="max-w-4xl mx-auto space-y-8">
        
        <!-- Page header -->
        <div class="text-center mb-8">
          <h1 class="text-4xl font-bold text-slate-800 mb-2">الملف الشخصي</h1>
          <p class="text-slate-600 text-lg">إدارة معلوماتك الشخصية وتقييماتك</p>
        </div>

        <!-- Profile section -->
        <Card class="shadow-2xl border-0 bg-white/95 backdrop-blur-sm">
          <CardHeader class="pb-4">
            <CardTitle class="text-xl font-bold text-slate-800">المعلومات الشخصية</CardTitle>
          </CardHeader>
          <CardContent class="p-6">
            <div class="flex flex-col md:flex-row items-start md:items-center gap-6">
              
              <!-- User avatar -->
              <div class="flex flex-col items-center space-y-4">
                <div class="w-24 h-24 bg-blue-100 rounded-full flex items-center justify-center text-2xl font-bold text-blue-700">
                  {{ userProfile.name.charAt(0) }}
                </div>
              </div>

              <!-- Profile information -->
              <div class="flex-1 space-y-6">
                
                <!-- Display mode -->
                <div v-if="!isEditingProfile" class="space-y-4">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <Label class="text-sm font-medium text-slate-600">الاسم</Label>
                      <p class="text-lg font-semibold text-slate-800 mt-1">{{ userProfile.name }}</p>
                    </div>
                    <div>
                      <Label class="text-sm font-medium text-slate-600">البريد الإلكتروني</Label>
                      <p class="text-lg text-slate-700 mt-1">{{ userProfile.email }}</p>
                    </div>
                  </div>
                  
                  <!-- Statistics -->
                  <div class="pt-4 border-t border-slate-200/60 text-center">
                    <p class="text-2xl font-bold text-blue-600">{{ totalReviews }}</p>
                    <p class="text-sm text-slate-600">إجمالي التقييمات</p>
                  </div>
                  
                  <!-- Edit button -->
                  <Button @click="startEditingProfile" class="bg-blue-600 hover:bg-blue-700">
                    تعديل الاسم
                  </Button>
                </div>

                <!-- Edit mode -->
                <div v-if="isEditingProfile" class="space-y-4">
                  <div class="space-y-2">
                    <Label for="edit-name" class="text-sm font-medium text-slate-700">الاسم الجديد</Label>
                    <Input 
                      id="edit-name"
                      v-model="editProfileForm.name"
                      placeholder="أدخل اسمك الجديد"
                      class="text-right"
                      :disabled="isUpdatingProfile"
                    />
                  </div>
                  
                  <div class="flex gap-3">
                    <Button 
                      @click="saveProfile" 
                      :disabled="isUpdatingProfile"
                      class="bg-green-600 hover:bg-green-700"
                    >
                      <span v-if="isUpdatingProfile">جاري الحفظ...</span>
                      <span v-else>حفظ</span>
                    </Button>
                    <Button 
                      @click="cancelEditingProfile" 
                      variant="outline"
                      :disabled="isUpdatingProfile"
                    >
                      إلغاء
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Reviews section -->
        <Card class="shadow-2xl border-0 bg-white/95 backdrop-blur-sm">
          <CardHeader class="pb-4">
            <CardTitle class="text-xl font-bold text-slate-800">تقييماتي</CardTitle>
            <p class="text-slate-600">جميع التقييمات التي قمت بإضافتها</p>
          </CardHeader>
          <CardContent class="p-6">
            
            <!-- Empty state -->
            <div v-if="userReviews.length === 0" class="text-center py-12">
              <div class="text-6xl mb-4">📝</div>
              <h3 class="text-xl font-semibold text-slate-700 mb-2">لا توجد تقييمات</h3>
              <p class="text-slate-500">لم تقم بإضافة أي تقييمات بعد</p>
            </div>

            <!-- Reviews list -->
            <div v-else class="space-y-6">
              <div 
                v-for="review in userReviews" 
                :key="review.id"
                class="border border-slate-200/60 rounded-xl p-6 bg-white/80 hover:shadow-lg transition-all duration-200"
              >
                
                <!-- Review header -->
                <div class="flex items-center justify-between gap-4 mb-4">
                  <div class="flex items-center gap-3">
                    <h3 class="text-lg font-semibold text-slate-800">{{ review.placeName }}</h3>
                    <Badge :class="getCategoryColor(review.placeCategory)" class="text-xs">
                      {{ getCategoryLabel(review.placeCategory) }}
                    </Badge>
                  </div>
                </div>

                <!-- Display mode -->
                <div v-if="editingReviewId !== review.id">
                  <p class="text-slate-700 mb-4 leading-relaxed">{{ review.comment }}</p>

                  <div class="flex gap-2">
                    <Button 
                      @click="startEditingReview(review)" 
                      variant="outline" 
                      size="sm"
                      class="text-blue-600 border-blue-200 hover:bg-blue-50"
                    >
                      تعديل
                    </Button>
                    
                    <Button 
                      @click="confirmDeleteReview(review.id)"
                      variant="outline" 
                      size="sm"
                      class="text-red-600 border-red-200 hover:bg-red-50"
                    >
                      حذف
                    </Button>
                  </div>
                </div>

                <!-- Edit mode -->
                <div v-if="editingReviewId === review.id" class="space-y-4">
                  <div class="space-y-2">
                    <Label for="edit-comment" class="text-sm font-medium text-slate-700">تعديل التعليق</Label>
                    <textarea
                      id="edit-comment"
                      v-model="editReviewForm.comment"
                      placeholder="اكتب تعليقك هنا..."
                      class="w-full text-right min-h-[100px] p-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-1 focus:ring-blue-500 resize-none"
                      :disabled="isEditingReview"
                    ></textarea>
                  </div>

                  <div class="flex gap-2">
                    <Button 
                      @click="saveReview(review.id)" 
                      :disabled="isEditingReview"
                      class="bg-green-600 hover:bg-green-700"
                      size="sm"
                    >
                      <span v-if="isEditingReview">جاري الحفظ...</span>
                      <span v-else>حفظ</span>
                    </Button>
                    <Button 
                      @click="cancelEditingReview" 
                      variant="outline"
                      :disabled="isEditingReview"
                      size="sm"
                    >
                      إلغاء
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </div>
  
  <!-- Footer -->
  <Footer/>

  <!-- Delete Confirmation Dialog -->
  <Dialog v-model:open="showConfirmDialog">
    <DialogContent class="sm:max-w-md" dir="rtl">
      <DialogHeader>
        <DialogTitle class="text-center text-amber-600 flex items-center justify-center gap-2">
          <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
          تأكيد الحذف
        </DialogTitle>
      </DialogHeader>
      <div class="text-center py-4">
        <p class="text-slate-700">هل أنت متأكد من حذف هذا التقييم؟ لا يمكن التراجع عن هذا الإجراء.</p>
      </div>
      <div class="flex justify-center gap-3">
        <Button @click="deleteReview" class="bg-red-600 hover:bg-red-700">
          تأكيد الحذف
        </Button>
        <Button @click="closeConfirmDialog" variant="outline">
          إلغاء
        </Button>
      </div>
    </DialogContent>
  </Dialog>
</template>

<style scoped>
/* Arabic font */
@import url("https://fonts.googleapis.com/css2?family=Cairo:wght@200;300;400;500;600;700;800;900&display=swap");

* {
  font-family: "Cairo", sans-serif;
}

/* Custom scrollbar */
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