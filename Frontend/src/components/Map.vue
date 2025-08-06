<script setup>
import { onMounted, ref, watch } from 'vue';
import L from "leaflet"
import { useGeolocation } from '@vueuse/core';
import { usePlaces } from '@/hooks/usePlaces';

const { places, openPlaceDetails } = usePlaces()

const map = ref();
const mapContainer = ref();

const markers = ref([])

const { coords, error } = useGeolocation();

const handleMarkerClick = (place) => {
    // zoom to the marker if clicked
    map.value.setView([place.lat, place.lng], 16, {
        animate: true,
        duration: 0.5
    });

    openPlaceDetails(place)
}

const addMarkersToMap = () => {
    places.value.forEach(place => {
        const marker = L.marker([place.lat, place.lng])
            .addTo(map.value)
            .on('click', () => handleMarkerClick(place))

        markers.value.push(marker)
    })

}


onMounted(() => {
    map.value = L.map(mapContainer.value).setView([24.760547398165134, 46.71390262311927], 12);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map.value);

    addMarkersToMap()
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