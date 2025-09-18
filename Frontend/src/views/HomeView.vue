<script setup>
import FilterPlaces from '@/components/FilterPlaces.vue';
import Map from '@/components/Map.vue';
import PlaceDetailsDrawer from '@/components/PlaceDetailsDrawer.vue';
import { ref, watch } from 'vue';

const placeId = ref(null)

watch(placeId, () => console.log(placeId.value));

function updatePlaceId(newPlaceId) {
    console.log(newPlaceId);
    
    placeId.value = newPlaceId;
}

</script>

<template>
    <div class="relative w-full h-full">
        <FilterPlaces />
        <PlaceDetailsDrawer class="absolute inset-0 z-900 pointer-events-none" v-model:placeId="placeId"/>
        <Map @on-new-place-selected="updatePlaceId" />
    </div>
</template>