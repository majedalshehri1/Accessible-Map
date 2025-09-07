<script setup>
import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Star, X } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import { usePlacesStore } from '@/stores/placeStore'
import { useAuthStore } from '@/stores/authStore'
import { storeToRefs } from 'pinia'

const placesStore = usePlacesStore();
const { selectedPlace } = storeToRefs(placesStore);
const { createPlaceReview } = placesStore;

const authStore = useAuthStore();
const { user } = storeToRefs(authStore);

const emit = defineEmits(['close', 'submit'])

const form = ref({
    rating: 0,
    comment: ''
})

const hoverRating = ref(0)
const isLoading = ref(false)

const setRating = (rating) => {
    form.value.rating = rating
}

const setHoverRating = (rating) => {
    hoverRating.value = rating
}

const submitReview = async () => {
    if (!form.value.comment || form.value.rating === 0) {
        toast.error('يرجى تعبئة جميع الحقول وتحديد التقييم')
        return
    }

    isLoading.value = true

    try {
        await createPlaceReview({
            placeId: selectedPlace.value.id,
            rating: form.value.rating,
            description: form.value.comment,
            userId: user.value.id
        })

        toast.success('ارسل التقييم بنجاح')

        // reset form
        form.value = {
            rating: 0,
            comment: ''
        }

        emit('submit')
    } catch (error) {
        console.log(error);
        toast.error('فشل في إرسال التقييم. حاول مرة أخرى.')
    } finally {
        isLoading.value = false
    }
}
</script>


<template>
    <div class="fixed inset-0 bg-zinc-950/80 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
            <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold">اضف تقييم</h3>
                <Button variant="ghost" size="sm" @click="emit('close')" :disabled="isLoading">
                    <X class="h-4 w-4" />
                </Button>
            </div>

            <form @submit.prevent="submitReview" class="space-y-4">
                <div>
                    <Label>التقييم</Label>
                    <div class="flex gap-1 mt-1.5">
                        <Star v-for="star in 5" :key="star" @click="setRating(star)" @mouseenter="setHoverRating(star)"
                            @mouseleave="setHoverRating(0)"
                            :class="star <= (hoverRating || form.rating) ? 'text-yellow-400 fill-current' : 'text-gray-300'"
                            class="h-6 w-6 cursor-pointer transition-colors" />
                    </div>
                </div>

                <div>
                    <Label for="comment">التعليق</Label>
                    <Textarea id="comment" v-model="form.comment" placeholder="شاركنا تجربتك..." rows="4" required
                        :disabled="isLoading" />
                </div>

                <div class="flex gap-2 pt-2">
                    <Button type="submit" :disabled="isLoading" class="bg-blue-600 hover:bg-blue-700 ring-blue-300">
                        <span v-if="isLoading">جاري الإرسال...</span>
                        <span v-else>ارسل التقييم</span>
                    </Button>
                    <Button type="button" variant="outline" @click="emit('close')" :disabled="isLoading">
                        الغاء
                    </Button>
                </div>
            </form>
        </div>
    </div>
</template>
