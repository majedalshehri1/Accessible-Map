<script setup>
import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Star, X } from 'lucide-vue-next'
import { toast } from 'vue-sonner'

const emit = defineEmits(['close', 'submit'])

const form = ref({
    userName: '',
    rating: 0,
    comment: ''
})

const hoverRating = ref(0)

const setRating = (rating) => {
    form.value.rating = rating
}

const setHoverRating = (rating) => {
    hoverRating.value = rating
}

const submitReview = () => {
    // Here you would typically send the review to your API
    console.log('Submitting review:', form.value)

    toast.success("ارسل التقييم بنجاح", {
        description: "اختبار لتقييم......................."
    })

    // Reset form
    form.value = { rating: 0, comment: '' }

    emit('submit')
}
</script>

<template>
    <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
            <div class="flex items-center justify-between mb-4">
                <h3 class="text-lg font-semibold">اضف تقييم</h3>
                <Button variant="ghost" size="sm" @click="emit('close')">
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
                    <Textarea id="comment" v-model="form.comment" placeholder="شاركنا تجربتك..." rows="4" required />
                </div>

                <div class="flex gap-2 pt-2">
                    <Button type="submit" :disabled="!form.rating">
                        ارسل التقييم
                    </Button>
                    <Button type="button" variant="outline" @click="emit('close')">
                        الغاء
                    </Button>
                </div>
            </form>
        </div>
    </div>
</template>