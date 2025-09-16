<script setup>
import { ref, onMounted } from "vue";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import surveyService from "@/services/surveyService";
import { useAuthStore } from "@/stores/authStore";
import { toast } from "vue-sonner";

const isOpen = ref(false);
const rating = ref(null);
const feedback = ref("");
const isSubmitting = ref(false);
const hasSubmittedSurvey = ref(null);

const authStore = useAuthStore();

onMounted(async () => {
  const userId = authStore.user?.id;
  if (!userId) {
    console.warn("⚠️ No userId in authStore");
    hasSubmittedSurvey.value = null;
    return;
  }

  const result = await surveyService.getIsExisting(userId);
  console.log("🔍 [Survey.vue] getIsExisting result:", result);

  if (result === null) {
    toast.error("حدث خطأ أثناء التحقق من حالة الاستبيان");
    hasSubmittedSurvey.value = false;
  } else {
    hasSubmittedSurvey.value = result;
  }
});

const submitSurvey = async () => {
  if (rating.value === null) {
    toast.error("الرجاء اختيار تقييم");
    return;
  }

  isSubmitting.value = true;

  try {
    await surveyService.postSurveyResponses({
      rating: rating.value,
      description: feedback.value,
      userId: authStore.user?.id,
    });

    toast.success("تم إرسال الاستبيان بنجاح");
    rating.value = null;
    feedback.value = "";
    isOpen.value = false;
    hasSubmittedSurvey.value = true;
  } catch (err) {
    if (err.response?.status === 409) {
      toast.error("لقد قمت بالفعل بإرسال استبيان سابقًا");
      hasSubmittedSurvey.value = true;
    } else {
      toast.error("خطأ في إرسال الاستبيان");
    }
    console.error("Error submitting survey:", err);
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<template>
  <Button
    v-if="hasSubmittedSurvey === false"
    @click="isOpen = true"
    variant="outline"
  >
    أضف تجربتك معنا
  </Button>

  <Dialog v-model:open="isOpen">
    <DialogContent dir="rtl" class="sm:max-w-lg">
      <DialogHeader class="space-y-3">
        <DialogTitle class="text-center text-xl font-bold text-slate-800">
          استبيان التوصية
        </DialogTitle>
        <DialogDescription class="text-right text-slate-600 leading-relaxed">
          على مقياس من 0 إلى 10، ما مدى احتمالية أن توصي بمنصتنا لأصدقائك أو
          معارفك؟
        </DialogDescription>
      </DialogHeader>

      <div class="grid gap-6 py-4">
        <div class="grid gap-2">
          <Label for="rating">اختر التقييم</Label>
          <Select v-model="rating">
            <SelectTrigger
              id="rating"
              class="w-full justify-between text-right"
            >
              <SelectValue placeholder="اختر من 0 إلى 10" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem v-for="n in 11" :key="n - 1" :value="n - 1">
                {{ n - 1 }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div class="grid gap-2">
          <Label for="feedback">اكتب وصفك / اقتراحاتك</Label>
          <textarea
            id="feedback"
            v-model="feedback"
            placeholder="شاركنا برأيك هنا..."
            class="w-full text-right min-h-[100px] p-3 border border-slate-200 rounded-lg focus:border-blue-500 focus:ring-1 focus:ring-blue-500 resize-none"
          ></textarea>
        </div>
      </div>

      <DialogFooter>
        <Button
          @click="submitSurvey"
          :disabled="isSubmitting"
          class="bg-green-600 hover:bg-green-700"
        >
          <span v-if="isSubmitting">جاري الإرسال...</span>
          <span v-else>إرسال</span>
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
