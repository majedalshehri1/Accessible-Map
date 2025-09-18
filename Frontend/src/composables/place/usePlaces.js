import { computed } from "vue";

import { keepPreviousData, useQuery } from "@tanstack/vue-query";
import { storeToRefs } from "pinia";

import placeServiecs from "@/services/placeServiecs";
import { usePlaceQueryParams } from "@/stores/placeQueryParamsStore";

const store = usePlaceQueryParams();

const { bounds, category, query } = storeToRefs(store);

const queryKey = computed(() => [
    'places',
    { bounds: bounds.value, query: query.value, category: category.value },
]);

const queryFn = ({ signal }) => {
    const { bounds, query, category } = queryKey.value[1]
    console.log(bounds, query, category);
    return placeServiecs.getPlacesWithinBounds(bounds, query, category, signal);
}

const enabled = computed(() => !!(bounds.value.east && bounds.value.north && bounds.value.west && bounds.value.south && bounds.value.zoom));

export function usePlaces() {
    return useQuery({
        queryKey,
        queryFn,
        enabled,
        placeholderData: keepPreviousData,
        // set global stale time of 2 minutes
        staleTime: 1000 * 60 * 2,
    })
}