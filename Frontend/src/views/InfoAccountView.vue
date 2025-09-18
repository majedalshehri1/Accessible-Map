<script setup>
// Vue
import { ref, computed, onMounted } from "vue";

// UI components
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import Footer from "@/components/Footer.vue";
import Survey from "@/components/Survey.vue";

import { useAuthStore } from "@/stores/authStore";

import reviewService from "@/services/reviewServices";

const auth = useAuthStore();

// For user section
const userProfile = ref({
  id: null,
  username: "",
  email: "",
});

const isEditingProfile = ref(false);
const isUpdatingProfile = ref(false);
const editProfileForm = ref({ username: "" });

const avatarInitial = computed(
  () => (userProfile.value.username || "").trim().charAt(0) || "؟"
);
const totalReviews = ref(0);

const loadUserFromStore = () => {
  const u = auth.user || {};
  userProfile.value.id = u.id ?? null;
  userProfile.value.username = u.username ?? "";
  userProfile.value.email = u.email ?? "";
};

onMounted(async () => {
  if (!auth.user) await auth.restore();
  loadUserFromStore();
  await fetchUserReviews();
});

// update username
const startEditingProfile = () => {
  editProfileForm.value.username = userProfile.value.username;
  isEditingProfile.value = true;
};
// Cancel editing profile
const cancelEditingProfile = () => {
  isEditingProfile.value = false;
  editProfileForm.value.username = "";
};

const saveProfile = async () => {
  const newName = editProfileForm.value.username?.trim();
  if (!newName) return;
  isUpdatingProfile.value = true;
  try {
    await auth.updateUsername(newName); // API call to update usernamE
    loadUserFromStore();
    isEditingProfile.value = false;
  } catch (e) {
    console.error("Error updating profile username:", e);
  } finally {
    isUpdatingProfile.value = false;
  }
};

// For review section
const userReviews = ref([]);
// { id, placeName, placeCategory, userName, description, rating }
const isReviewsLoading = ref(false);
const editingReviewId = ref(null);
const isEditingReview = ref(false);
const editReviewForm = ref({ comment: "", rating: 0 });

const showConfirmDialog = ref(false);
const reviewToDelete = ref(null);

// Categories mapping (the backend returns category as a string like "RESTAURANT", "HOSPITAL", etc.) it can be used to display category labels and colors for custom styling
const categoriesMap = {
  RESTAURANT: { label: "مطعم", color: "bg-orange-100 text-orange-700" },
  HOSPITAL: { label: "مستشفى", color: "bg-red-100 text-red-700" },
  COFFEE: { label: "كافيه", color: "bg-amber-100 text-amber-700" },
  MALL: { label: "مول", color: "bg-purple-100 text-purple-700" },
  PARK: { label: "حديقة", color: "bg-green-100 text-green-700" },
};
const getCategoryLabel = (v) => categoriesMap[v]?.label ?? v;
const getCategoryColor = (v) =>
  categoriesMap[v]?.color ?? "bg-gray-100 text-gray-700";

// ( GET /api/reviews/user/{user_id} )
const fetchUserReviews = async () => {
  if (!auth.user?.id) return;
  isReviewsLoading.value = true;
  try {
    const { data } = await reviewService.getReviewsByUserId(auth.user.id);

    userReviews.value = data.content;
    totalReviews.value = data.content.length;
    // debug
    console.log("Fetched user reviews:", data);
    console.log("Total reviews:", data.length);
  } catch (e) {
    console.error("Error fetching user reviews:", e);
    userReviews.value = [];
    totalReviews.value = 0;
  } finally {
    isReviewsLoading.value = false;
  }
};

const startEditingReview = (review) => {
  editingReviewId.value = review.id;
  editReviewForm.value.comment = review.description;
  editReviewForm.value.rating = review.rating ?? 0;
};

const cancelEditingReview = () => {
  editingReviewId.value = null;
  editReviewForm.value = { comment: "", rating: 0 };
};

const saveReview = async (reviewId) => {
  const description = editReviewForm.value.comment?.trim();
  const rating = Number(editReviewForm.value.rating ?? 0);
  if (!description) return;

  isEditingReview.value = true;
  try {
    const { data: updated } = await reviewService.editReview(reviewId, {
      description,
      rating,
    });
    const i = userReviews.value.findIndex((r) => r.id === reviewId);
    if (i !== -1) {
      userReviews.value[i] = {
        ...userReviews.value[i],
        description: updated.description ?? description,
        rating: updated.rating ?? rating,
        placeName: updated.placeName ?? userReviews.value[i].placeName,
        placeCategory:
          updated.placeCategory ?? userReviews.value[i].placeCategory,
        userName: updated.userName ?? userReviews.value[i].userName,
      };
    }
    cancelEditingReview();
  } catch (e) {
    console.error("Error updating review:", e);
  } finally {
    isEditingReview.value = false;
  }
};

const confirmDeleteReview = (reviewId) => {
  reviewToDelete.value = reviewId;
  showConfirmDialog.value = true;
};

// ( DELETE /api/reviews/delete/{id} )
const deleteReview = async () => {
  if (!reviewToDelete.value) return;
  try {
    await reviewService.deleteReview(reviewToDelete.value);
    const i = userReviews.value.findIndex((r) => r.id === reviewToDelete.value);
    if (i !== -1) userReviews.value.splice(i, 1);
    totalReviews.value = userReviews.value.length;
  } catch (e) {
    console.error("Error deleting review:", e);
  } finally {
    reviewToDelete.value = null;
    showConfirmDialog.value = false;
  }
};

const closeConfirmDialog = () => {
  reviewToDelete.value = null;
  showConfirmDialog.value = false;
};
</script>

<template>
  <!-- Top bar: back to home + breadcrumb -->
  <div
    class="sticky top-0 z-20 -mx-4 mb-4 bg-white/70 backdrop-blur supports-[backdrop-filter]:bg-white/50 border-b border-slate-200/60"
  >
    <div
      class="max-w-4xl mx-auto px-4 py-3 flex items-center justify-between"
      dir="rtl"
    >
      <!-- Back button -->
      <RouterLink
        to="/"
        class="inline-flex items-center gap-2 rounded-lg border border-slate-200 px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-50 transition"
      >
        <!-- Arrow (RTL-friendly) -->
        <svg
          class="w-4 h-4"
          viewBox="0 0 20 20"
          fill="currentColor"
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            d="M12.78 15.53a.75.75 0 01-1.06 0l-4.5-4.5a.75.75 0 010-1.06l4.5-4.5a.75.75 0 111.06 1.06L8.56 10l4.22 4.22a.75.75 0 010 1.06z"
            clip-rule="evenodd"
          />
        </svg>
        <span>رجوع للرئيسية</span>
      </RouterLink>

      <!-- Breadcrumb -->
      <nav class="text-xs text-slate-500">
        <ol class="flex items-center gap-1">
          <li>
            <RouterLink to="/" class="hover:text-slate-700"
              >الرئيسية</RouterLink
            >
          </li>
          <li class="px-1">/</li>
          <li class="text-slate-700 font-medium">الملف الشخصي</li>
        </ol>
      </nav>
    </div>
  </div>
  <!-- Main container -->
  <div dir="rtl" class="min-h-screen bg-slate-50 relative overflow-hidden">
    <!-- Background elements -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <div
        class="absolute -top-32 -right-32 w-96 h-96 bg-blue-100/20 rounded-full blur-3xl"
      ></div>
      <div
        class="absolute -bottom-24 -left-24 w-64 h-64 bg-green-100/30 rounded-full blur-2xl"
      ></div>
      <div
        class="absolute top-1/2 left-1/4 w-32 h-32 bg-purple-100/20 rounded-full blur-xl"
      ></div>
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
          <CardHeader
            class="pb-4 flex flex-col md:flex-row md:items-center md:justify-between gap-3"
          >
            <CardTitle class="text-xl font-bold text-slate-800"
              >المعلومات الشخصية</CardTitle
            >
            <Survey />
          </CardHeader>
          <CardContent class="p-6">
            <div
              class="flex flex-col md:flex-row items-start md:items-center gap-6"
            >
              <!-- User avatar -->
              <div class="flex flex-col items-center space-y-4">
                <div
                  class="w-24 h-24 bg-blue-100 rounded-full flex items-center justify-center text-2xl font-bold text-blue-700"
                >
                  {{ avatarInitial }}
                </div>
              </div>

              <!-- Profile information -->
              <div class="flex-1 space-y-6">
                <!-- Display mode -->
                <div v-if="!isEditingProfile" class="space-y-4">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <Label class="text-sm font-medium text-slate-600"
                        >الاسم</Label
                      >
                      <p class="text-lg font-semibold text-slate-800 mt-1">
                        {{ userProfile.username }}
                      </p>
                    </div>
                    <div>
                      <Label class="text-sm font-medium text-slate-600"
                        >البريد الإلكتروني</Label
                      >
                      <p class="text-lg text-slate-700 mt-1">
                        {{ userProfile.email }}
                      </p>
                    </div>
                  </div>

                  <!-- Statistics -->
                  <div
                    class="pt-4 border-t border-slate-200/60 flex items-center justify-between w-[65%] gap-6"
                  >
                    <!-- Edit button -->
                    <Button
                      @click="startEditingProfile"
                      class="bg-blue-600 hover:bg-blue-700"
                    >
                      تعديل الاسم
                    </Button>
                    <p class="text-sm text-slate-600">إجمالي التقييمات</p>
                    <p class="text-2xl font-bold text-blue-600">
                      {{ totalReviews }}
                    </p>
                  </div>
                </div>

                <!-- Edit mode -->
                <div v-if="isEditingProfile" class="space-y-4">
                  <div class="space-y-2">
                    <Label
                      for="edit-name"
                      class="text-sm font-medium text-slate-700"
                      >الاسم الجديد</Label
                    >
                    <Input
                      id="edit-name"
                      v-model="editProfileForm.username"
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
            <CardTitle class="text-xl font-bold text-slate-800"
              >تقييماتي</CardTitle
            >
            <p class="text-slate-600">جميع التقييمات التي قمت بإضافتها</p>
          </CardHeader>
          <CardContent class="p-6">
            <!-- Empty state -->
            <div v-if="userReviews.length === 0" class="text-center py-12">
              <div class="text-6xl mb-4">📝</div>
              <h3 class="text-xl font-semibold text-slate-700 mb-2">
                لا توجد تقييمات
              </h3>
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
                    <h3 class="text-lg font-semibold text-slate-800">
                      {{ review.placeName }}
                    </h3>
                    <Badge
                      :class="getCategoryColor(review.placeCategory)"
                      class="text-xs"
                    >
                      {{ getCategoryLabel(review.placeCategory) }}
                    </Badge>
                  </div>
                </div>

                <!-- Display mode -->
                <div v-if="editingReviewId !== review.id">
                  <p class="text-slate-700 mb-4 leading-relaxed">
                    {{ review.description }}
                  </p>

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
                    <Label
                      for="edit-comment"
                      class="text-sm font-medium text-slate-700"
                      >تعديل التعليق</Label
                    >
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
  <Footer />

  <!-- Delete Confirmation Dialog -->
  <Dialog v-model:open="showConfirmDialog">
    <DialogContent class="sm:max-w-md" dir="rtl">
      <DialogHeader>
        <DialogTitle
          class="text-center text-amber-600 flex items-center justify-center gap-2"
        >
          <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
              clip-rule="evenodd"
            />
          </svg>
          تأكيد الحذف
        </DialogTitle>
      </DialogHeader>
      <div class="text-center py-4">
        <p class="text-slate-700">
          هل أنت متأكد من حذف هذا التقييم؟ لا يمكن التراجع عن هذا الإجراء.
        </p>
      </div>
      <div class="flex justify-center gap-3">
        <Button @click="deleteReview" class="bg-red-600 hover:bg-red-700">
          تأكيد الحذف
        </Button>
        <Button @click="closeConfirmDialog" variant="outline"> إلغاء </Button>
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
