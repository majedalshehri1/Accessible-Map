<script setup>
import { Search as SearchIcon } from 'lucide-vue-next'
import { ref, watch, onBeforeUnmount } from 'vue'
import { toast } from 'vue-sonner'
import SearchInput from './SearchInput.vue'
import Button from './ui/button/Button.vue'
import { usePlacesStore } from '@/stores/placeStore'
import { storeToRefs } from 'pinia'

const placesStore = usePlacesStore();
const { places, isLoadingPlaces } = storeToRefs(placesStore);
const { fetchAllPlaces, searchPlaceByQuery } = placesStore;

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

const handleSearch = (query) => {
    if (query.length === 0) {
        if (controller) {
            controller.abort();
        }
        return fetchAllPlaces();
    }

    if (controller) {
        controller.abort();
    }

    controller = new AbortController();

    return searchPlaceByQuery(query, controller);
}

watch(searchQuery, (newQuery) => {
    if (debounceTimeout) {
        clearTimeout(debounceTimeout);
    }

    debounceTimeout = setTimeout(() => {
        toast.promise(() => handleSearch(newQuery), {
            loading: "تحميل...",
            success: 'تم البحث بنجاح!',
            error: (err) => {
                if (err.name !== 'AbortError') {
                    return "حدث خطأ اثناء تحميل الأماكن. يرجى اعادة التجربة";
                }
                return;
            },
        });
    }, debounceDelay);
});

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
                @close="closeSearch" />
        </div>

        <!-- Desktop Search -->
        <div class="hidden sm:flex flex-1">
            <SearchInput :is-disabled="isLoadingPlaces" v-model="searchQuery" />
        </div>

        <!-- Mobile Search Icon -->
        <Button variant="ghost" size="icon" class="sm:hidden" @click="openSearch">
            <SearchIcon class="w-5 h-5" />
        </Button>
    </div>
</template>
