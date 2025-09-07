import { ref, computed } from "vue";
import { defineStore } from "pinia";
import placeServiecs from "@/services/placeServiecs";
import reviewServices from "@/services/reviewServices";

// private helper function not exposed by the store
const formatPlace = (data) => {
  let rating = 0;
  const reviewsCount = data.reviews.length;
  const reviews = data.reviews.map(r => {
    rating += r.rating;
    return {
      id: r.id,
      userName: r.user?.userName || r.userName,
      rating: r.rating,
      comment: r.description
    };
  });
  rating = reviewsCount !== 0 ? rating / reviewsCount : 0;
  const placeFeatures = data.placeFeatures || data.accessibilityFeatures || data.reviews;
  
  return {
    id: data.id,
    name: data.placeName,
    lng: data.longitude,
    lat: data.latitude,
    placeCategory: data.placeCategory,
    images: [data.imageUrl],
    reviews,
    placeFeatures,
    rating,
    reviewsCount
  };
};

export const usePlacesStore = defineStore('places', () => {
  const selectedPlace = ref(null);
  const isLoadingPlaces = ref(false);
  const places = ref([]);

  const isDrawerOpen = computed(() => !!selectedPlace.value);

  const fetchAllPlaces = async () => {
    isLoadingPlaces.value = true;
    try {
      const { data } = await placeServiecs.getAllPlaces();
      places.value = data.map(formatPlace);
    } catch (error) {
      console.error("Error fetching all places:", error);
      throw error; // re-throw error to make components responsible of handling UI logic when req failed
    } finally {
      isLoadingPlaces.value = false;
    }
  };

  const fetchPlacesByCategory = async (category) => {
    isLoadingPlaces.value = true;
    try {
      const { data } = await placeServiecs.getPlacesByCategory(category);
      places.value = data.map(formatPlace);
    } catch (error) {
      console.error("Error fetching places by category:", error);
      throw error; // re-throw error to make components responsible of handling UI logic when req failed
    } finally {
      isLoadingPlaces.value = false;
    }
  };

  const createPlaceReview = async (review) => {
    try {
      await reviewServices.createReview(review);
    } catch (error) {
      console.error('Fetch error:', error);
      throw error; // re-throw error to make components responsible of handling UI logic when req failed
    }
  };

  const searchPlaceByQuery = async (query, controller) => {
    isLoadingPlaces.value = true;
    try {
      const { data } = await placeServiecs.getPlacesByQuery(query, { signal: controller?.signal });
      const formatedData = data.map(formatPlace);
      places.value = formatedData;
    } catch (error) {
      throw error;
    } finally {
      isLoadingPlaces.value = false;
    }
  };

  const openPlaceDetails = (place) => {
    selectedPlace.value = place;
  };

  const closePlaceDetails = () => {
    selectedPlace.value = null;
  };

  return {
    places,
    selectedPlace,
    isLoadingPlaces,
    isDrawerOpen,
    fetchAllPlaces,
    fetchPlacesByCategory,
    openPlaceDetails,
    closePlaceDetails,
    createPlaceReview,
    searchPlaceByQuery
  };
});