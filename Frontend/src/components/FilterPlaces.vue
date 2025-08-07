<script setup>
import { CheckCircle, Coffee, Hospital, ShoppingBag, Trees, Utensils } from 'lucide-vue-next';

import Button from './ui/button/Button.vue';
import { ref } from 'vue';
import { toast } from 'vue-sonner';
import { usePlaces } from '@/hooks/usePlaces';
import placeServiecs from '@/services/placeServiecs';

const formatPlace = (data) => {
    console.log(data);

    let rating = 0;
    let reviewsCount = data.reviews.length;

    const reviews = data.reviews.map(r => {
        rating += r.rating

        return {
            id: r.id,
            userName: r.user.userName,
            rating: r.rating,
            comment: r.description
        }
    });

    rating = reviewsCount !== 0 ? rating / reviewsCount : 0;

    const placeFeatures = [];

    data.accessibilityFeatures.forEach(pl => {
        if (pl.isAvaliable) {
            placeFeatures.push(pl.accessibillityType)
        }
    })

    return {
        id: data.id,
        name: data.placeName,
        lng: data.longitude,
        lat: data.latitude,
        placeCategory: data.placeCategory,
        images: [data.imageUrl],
        reviews,
        placeFeatures,
        rating,
        reviewsCount
    }
}

const { isLoadingPlaces, places, fetchAllPlaces } = usePlaces();

const categories = [
    {
        id: 0, label: "الكل", value: "all"
    },
    {
        id: 1, label: "المطاعم", icon: Utensils, value: "RESTAURANT"
    },
    {
        id: 2, label: "المقاهي", icon: Coffee, value: "COFFEE"
    },
    {
        id: 3, label: "المنتزهات", icon: Trees, value: "PARK"
    },
    {
        id: 4, label: "المستشفيات", icon: Hospital, value: "HOSPITAL"
    },
    {
        id: 5, label: "أسواق", icon: ShoppingBag, value: "MALL"
    },
]

const asyncFunc = async (category) => {
    const { data } = await placeServiecs.getPlacesByCategory(category.value);
    const formatedData = data.map(formatPlace);
    places.value = formatedData;
    currentSelectedFilter.value = category
};

const handleClick = async (category) => {
    if (category.value === "all") {
        const fetchedPlaces = await fetchAllPlaces();
        places.value = fetchedPlaces;
        currentSelectedFilter.value = categories[0];
        return;
    } else {
        await asyncFunc(category)
    }
}

const currentSelectedFilter = ref(categories[0])

</script>

<template>
    <div
        class="absolute top-6 left-1/2 -translate-x-1/2 flex flex-wrap gap-2 max-w-64 sm:max-w-72 md:max-w-lg mx-auto z-[800]">
        <Button v-for="category in categories" :key="category.id" @click="handleClick(category)"
            :variant="currentSelectedFilter.id === category.id ? '' : 'outline'" size="sm"
            class="relative flex-[1_1_0%]"
            :class="currentSelectedFilter.id === category.id && 'bg-blue-600 hover:bg-blue-700 ring-blue-300'">
            <CheckCircle v-if="currentSelectedFilter.id === category.id"
                class="absolute -top-1 right-0 translate-x-1/2 h-2 w-2 fill-zinc-100 stroke-blue-600 z-[801]" />
            <component v-if="category.icon" :is="category.icon" class="h-4 w-4 mr-2" />
            {{ category.label }}
        </Button>
    </div>
</template>