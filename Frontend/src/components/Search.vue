<script setup>
import { Search as SearchIcon } from 'lucide-vue-next'
import { ref, watch, onBeforeUnmount } from 'vue'
import { toast } from 'vue-sonner'
import SearchInput from './SearchInput.vue'
import Button from './ui/button/Button.vue'
import placeServiecs from "../services/placeServiecs"
import { usePlaces } from '@/hooks/usePlaces'

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

    data.reviews.forEach(pl => {
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

const { isLoadingPlaces, places, fetchAllPlaces } = usePlaces()

const isOpen = ref(false)
const searchQuery = ref('')
const debounceDelay = 500
let debounceTimeout = null
let controller = null

const openSearch = () => {
    isOpen.value = true
}

const closeSearch = () => {
    isOpen.value = false
}

const handleSearch = async (query) => {
    if (query.length <= 2) {
        const fetchedPlaces = await fetchAllPlaces();
        places.value = fetchedPlaces;
        return
    }

    // Cancel any previous request
    if (controller) {
        controller.abort()
    }

    controller = new AbortController()

    try {
        const { data } = await placeServiecs.getPlacesByQuery(query, { signal: controller.signal });
        const formatedData = data.map(formatPlace);
        places.value = formatedData;
    } catch (err) {
        if (err.name !== 'AbortError') {
            console.error('Fetch error:', err)
            toast.error("حدث خطأ اثناء تحميل البيانات")
        }
    }
}

// Debounce search input
watch(searchQuery, (newQuery) => {
    if (debounceTimeout) {
        clearTimeout(debounceTimeout)
    }

    debounceTimeout = setTimeout(() => {
        handleSearch(newQuery)
    }, debounceDelay)
})

// Cleanup on unmount
onBeforeUnmount(() => {
    if (debounceTimeout) clearTimeout(debounceTimeout)
    if (controller) controller.abort()
})
</script>

<template>
    <div class="flex items-center flex-1 sm:max-w-lg">
        <!-- Mobile Search Overlay -->
        <div v-show="isOpen" class="fixed inset-0 z-50 bg-white flex items-start pt-4 px-4 sm:hidden">
            <SearchInput :is-disabled="isLoadingPlaces" v-model="searchQuery" :showCloseButton="true"
                @close="closeSearch" @search="handleSearch" />
        </div>

        <!-- Desktop Search -->
        <div class="hidden sm:flex flex-1">
            <SearchInput :is-disabled="isLoadingPlaces" v-model="searchQuery" @search="handleSearch" />
        </div>

        <!-- Mobile Search Icon -->
        <Button variant="ghost" size="icon" class="sm:hidden" @click="openSearch">
            <SearchIcon class="w-5 h-5" />
        </Button>
    </div>
</template>
