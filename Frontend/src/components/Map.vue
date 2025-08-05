<script setup>
import { onMounted, ref, watch } from 'vue';
import L from "leaflet"
import { useGeolocation } from '@vueuse/core';

const map = ref();
const mapContainer = ref();

const { coords, error } = useGeolocation();


onMounted(() => {
    map.value = L.map(mapContainer.value).setView([24.760547398165134, 46.71390262311927], 12);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map.value);
});

watch(coords, () => {
    if (
        coords.value.latitude !== Number.POSITIVE_INFINITY &&
        coords.value.longitude !== Number.POSITIVE_INFINITY
    ) {
        map.value.setView([coords.value.latitude, coords.value.longitude], 12)
    }
}, { once: true }
)

</script>

<template>
    <div ref="mapContainer" class="w-full h-full"></div>
</template>