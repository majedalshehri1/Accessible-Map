import { defineStore } from "pinia";
import { ref } from "vue";

export const usePlaceQueryParams = defineStore('placeQueryParams', () => {
    const bounds = ref({
        north: null,
        south: null,
        east: null,
        west: null,
        zoom: null
    });

    const query = ref('');

    const category = ref('');

    const updateBounds = (newBounds) => {
        if (!(newBounds?.north || newBounds?.south || newBounds?.east || newBounds?.west || newBounds?.zoom)) {
            throw new Error("invalid bounds")
        }

        bounds.value = newBounds;
    }

    const updateQuery = (newQuery) => query.value = newQuery;

    const updateCategory = (newCategory) => category.value = newCategory;

    return { bounds, query, category, updateBounds, updateCategory, updateQuery }
})