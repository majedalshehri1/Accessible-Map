<script setup>
import FilterPlaces from '@/components/FilterPlaces.vue';
import Map from '@/components/Map.vue';
import PlaceDetailsDrawer from '@/components/PlaceDetailsDrawer.vue';

</script>

<template>
    
    <div class="relative w-full h-full">
        
        <FilterPlaces />
        <PlaceDetailsDrawer class="absolute inset-0 z-900 pointer-events-none" />
        <Map />
    </div>
</template>