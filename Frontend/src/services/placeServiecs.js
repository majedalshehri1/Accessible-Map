import axiosClient from "@/apis/axiosClient";

export default {
    getAllPlaces() {
        return axiosClient.get("/place/all");
    },
    getPlaceById(id) {
        return axiosClient.get(`/place/${id}`);
    },
    createPlace(placeData) {
        return axiosClient.post("/place", placeData);
    },
    deletePlace(id) {
        return axiosClient.delete(`/place/${id}`);
    }
}