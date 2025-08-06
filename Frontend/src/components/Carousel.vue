<script setup>
import {
    Carousel,
    CarouselContent,
    CarouselItem,
    CarouselNext,
    CarouselPrevious
} from "@/components/ui/carousel"

defineProps({
    images: {
        type: Array,
        required: true
    }
})
</script>

<template>
    <!-- Flex container to center the carousel -->
    <div class="flex justify-center">
        <div class="max-w-[80%] sm:max-w-[60%] md:max-w-[50%] h-[300px] md:h-[400px] lg:h-[450px]">
            <Carousel class="relative h-full w-full" dir="ltr">
                <CarouselContent class="">
                    <CarouselItem v-for="(image, index) in images" :key="index" class="h-full basis-full">
                        <div class="h-[300px] md:h-[400px] lg:h-[450px] overflow-hidden rounded-lg relative">
                            <img :src="image" :alt="`Image ${index + 1}`" class="h-full w-full object-cover" />
                        </div>
                    </CarouselItem>
                </CarouselContent>

                <!-- Position navigation buttons inside the image -->
                <CarouselPrevious
                    class="absolute left-2 top-1/2 -translate-y-1/2 z-10 bg-white/70 hover:bg-white p-1 rounded-full shadow" />
                <CarouselNext
                    class="absolute right-2 top-1/2 -translate-y-1/2 z-10 bg-white/70 hover:bg-white p-1 rounded-full shadow" />
            </Carousel>
        </div>
    </div>
</template>
