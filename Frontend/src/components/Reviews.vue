<script setup>
import { ref } from "vue";

import { Button } from "@/components/ui/button";
import { Star, Plus } from "lucide-vue-next";

import ReviewForm from "./ReviewForm.vue";
import { useAuthStore } from "@/stores/authStore";
import { storeToRefs } from "pinia";

defineProps({
  rating: {
    type: Number,
    required: true,
  },
  reviewsCount: {
    type: Number,
    required: true,
  },
  reviews: {
    type: Array,
    required: true,
  },
});

const authStore = useAuthStore();
const { user } = storeToRefs(authStore);

const showReviewForm = ref(false);

const renderStars = (rating) => {
  return Array.from({ length: 5 }, (_, i) => i < rating);
};

const formatArabicNumber = (number) => {
  const numberFormated = number.toFixed(1)
  return number.toLocaleString("ar-EG");
};
</script>

<template>
  <div class="space-y-6">
    <!-- Rating Overview -->
    <div class="text-center space-y-2">
      <div class="text-3xl font-bold text-blue-500">
        {{ formatArabicNumber(+rating.toFixed(1)) }}
      </div>
      <div class="flex justify-center gap-1">
        <Star
          v-for="(filled, index) in renderStars(Math.round(rating))"
          :key="index"
          :class="filled ? 'text-yellow-400 fill-current' : 'text-gray-300'"
          class="h-5 w-5"
        />
      </div>
      <p class="text-sm text-zinc-400">
        بناء على <span class="text-foreground font-medium text-base">{{ formatArabicNumber(reviewsCount) }}</span> تقييم
      </p>

      <Button
        v-if="!!user"
        @click="showReviewForm = true"
        class="mt-4 bg-blue-600 hover:bg-blue-700 ring-blue-300"
        size="sm"
      >
        <Plus class="h-4 w-4 mr-2" />
        اضف تقييم
      </Button>
    </div>

    <!-- Reviews List -->
    <div class="space-y-4" v-if="reviews.length !== 0">
      <h4 class="font-semibold">اخر التقييمات</h4>
      <div
        v-for="review in reviews"
        :key="review.id"
        class="border rounded-lg p-4 space-y-2"
      >
        <div class="flex items-center justify-between">
          <div class="font-medium">{{ review.userName }}</div>
          <div class="flex items-center gap-2">
            <div class="flex gap-1">
              <Star
                v-for="(filled, index) in renderStars(review.rating)"
                :key="index"
                :class="
                  filled ? 'text-yellow-400 fill-current' : 'text-zinc-200'
                "
                class="h-4 w-4"
              />
            </div>
            <!-- <span class="text-sm text-zinc-400">{{ formatDate(review.date) }}</span> -->
          </div>
        </div>
        <p class="text-zinc-700">{{ review.comment }}</p>
      </div>
    </div>

    <!-- Review Form Modal/Drawer -->
    <ReviewForm
      v-if="showReviewForm"
      @close="showReviewForm = false"
      @submit="showReviewForm = false"
    />
  </div>
</template>
