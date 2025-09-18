import axiosClient from "@/apis/axiosClient";

export default {
    getPlacesWithinBounds(bounds, query, category) {
        const searchParams = new URLSearchParams();
        const { north, south, east, west, zoom } = bounds
        searchParams.set("n", north);
        searchParams.set("s", south);
        searchParams.set("e", east);
        searchParams.set("w", west);
        searchParams.set("zoom", zoom);

        if (query) {
            searchParams.set("query", query);
        }

        if (category) {
            searchParams.set("category", category)
        }

        return axiosClient.get(`/place/within-bounds?${searchParams.toString()}`);
    },
    getPlaceById(id) {
        return axiosClient.get(`/place/${id}`);
    },
    createPlace(placeData) {
        return axiosClient.post("/place/create", placeData);
    },
    deletePlace(id) {
        return axiosClient.delete(`/place/${id}`);
    },
    getPlacesByCategory(category) {
        return axiosClient.get(`/place/category?category=${encodeURIComponent(category)}`)
    },
    getPlacesByQuery(query, config = {}) {
        return axiosClient.get(`/place/search?search=${query}`, config)
    }
}