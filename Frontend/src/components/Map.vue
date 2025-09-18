<script setup>
import { onMounted, ref, watch, onUnmounted } from "vue";

import L from "leaflet";
import { useGeolocation, useDebounceFn } from "@vueuse/core";
import { toast } from "vue-sonner";

import { usePlaceQueryParams } from "@/stores/placeQueryParamsStore";
import { usePlaces } from "@/composables/place/usePlaces";
import { getMarkerHtml } from "@/utils/customMarkerFactory";

const { updateBounds } = usePlaceQueryParams();
const {data: places, isPending, error} = usePlaces()

console.log(places, isPending, error);

const emit = defineEmits(["onNewPlaceSelected"])

const map = ref();
const mapContainer = ref();

const markers = ref([]);

const { coords } = useGeolocation();

const handleMarkerClick = (place) => {
  console.log(place);
  
  map.value.setView([place.latitude, place.longitude], 16, {
    animate: true,
    duration: 0.5,
  });
  emit("onNewPlaceSelected", place.id)
};

const clearMarkers = () => {
  markers.value.forEach((marker) => marker.remove());
  markers.value = [];
};

const addMarkersToMap = () => {
  clearMarkers(); // clear existing markers

  places.value.data.forEach(async (place) => {
    const markerHtml = await getMarkerHtml(place.category);
    const icon = L.divIcon({
      className: "custom-marker",
      html: markerHtml,
      iconSize: [40, 40],
      iconAnchor: [20, 40],
    })

    const marker = L.marker([place.latitude, place.longitude], { icon })
      .addTo(map.value)
      .on("click", () => handleMarkerClick(place));

    markers.value.push(marker);
  });
};

const handleMapChange = useDebounceFn(() => {
    const bounds = map.value.getBounds();
    const newBounds = {
      north: bounds.getNorth(),
      south: bounds.getSouth(),
      east: bounds.getEast(),
      west: bounds.getWest(),
      zoom: map.value.getZoom()
    };

    updateBounds(newBounds)

    console.log(newBounds);
    
  }, 300);

onMounted(async () => {
  map.value = L.map(mapContainer.value).setView(
    [24.760547398165134, 46.71390262311927],
    12
  );

  L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution:
      '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  }).addTo(map.value);

  // toast.promise(fetchAllPlaces, {
  //   loading: "تحميل...",
  //   error: (err) => "حدث خطأ اثناء تحميل الأماكن. يرجى اعادة التجربة",
  // });

  map.value.on("zoomend moveend", handleMapChange)
});

watch(places, () => {
  if (map.value) {
    addMarkersToMap();
  }
});

watch(
  coords,
  () => {
    if (
      coords.value.latitude !== Number.POSITIVE_INFINITY &&
      coords.value.longitude !== Number.POSITIVE_INFINITY
    ) {
      map.value.setView([coords.value.latitude, coords.value.longitude], 12);
    }
  },
  { once: true }
);

watch(
  isPending,
  (pending) => {
    if (pending) {
      toast('تحميل...', { id: 'loading-places' });
    } else {
      toast.dismiss('loading-places');
    }
  },
  { immediate: true },
);

watch(error, (err) => {
  if (err) {
    toast.error("حدث خطأ اثناء تحميل الأماكن. يرجى اعادة التجربة");
  }
});

onUnmounted(() => {
  map.value.off("zoomend moveend", handleMapChange)
})
</script>

<template>
  <div ref="mapContainer" class="w-full h-full"></div>
</template>

<style lang="css">

  .custom-marker svg {
    stroke-width: 2px;
  }

  .custom-marker > div::before {
    content: "";
    display: inline-block;
    position: absolute;
    bottom: -48%;
    left: 50%;
    transform: translateX(-50%);
    border: 6px solid transparent;
    border-top-color: white;
  }
</style>
