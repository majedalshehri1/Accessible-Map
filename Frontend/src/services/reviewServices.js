import axiosClient from "@/apis/axiosClient";

export default {
    getReviewsByPlaceId(placeId) {
        return axiosClient.get(`/reviews/place/${placeId}`);
    },
    createReview(reviewData) {
        return axiosClient.post("/reviews/create", reviewData);
    }
}