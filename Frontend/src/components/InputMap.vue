<script setup>
import { onMounted, onUnmounted, ref, watch } from 'vue';
import L from "leaflet"
import { useGeolocation } from '@vueuse/core';

const map = ref();
const mapContainer = ref();

const marker = ref(null)

const { coords } = useGeolocation();

const emit = defineEmits(["onAddMarker"])

const addMarkerToMap = (e) => {
    const { lat, lng } = e.latlng

    if (marker.value) {
        marker.value.remove()
    }

    const markerPoint = L.marker([lat, lng]).addTo(map.value);
    marker.value = markerPoint;

    console.log(`marker add at lat: ${lat}, lng: ${lng}`);
    emit("onAddMarker", {lat, lng})
}


onMounted(() => {
    map.value = L.map(mapContainer.value).setView([24.760547398165134, 46.71390262311927], 12);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map.value);

    map.value.on("click", addMarkerToMap);
});

onUnmounted(() => {
    if (map.value && onMapClick) {
        map.value.off('click', onMapClick);
    }
})

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