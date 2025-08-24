import axiosClient from "@/apis/axiosClient";

export default {
  /** GET /api/reviews/user/{user_id} */
  getReviewsByUserId(userId) {
    return axiosClient.get(`/reviews/user/${userId}`);
  },

  /** GET /api/reviews/place/{placeId} */
  getReviewsByPlaceId(placeId) {
    return axiosClient.get(`/reviews/place/${placeId}`);
  },

  /** POST /api/reviews/create */
  createReview(reviewData) {
    // reviewData => { userId, placeId, description, rating }
    return axiosClient.post("/reviews/create", reviewData);
  },

  /** PUT /api/reviews/edit/{review_id} */
  editReview(reviewId, updatedData) {
    // updatedData => { description, rating }
    return axiosClient.put(`/reviews/edit/${reviewId}`, updatedData);
  },

  /** DELETE /api/reviews/delete/{review_id} */
  deleteReview(reviewId) {
    return axiosClient.delete(`/reviews/delete/${reviewId}`);
  }
};
