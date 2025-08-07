import { ref } from "vue";

import placeServiecs from "@/services/placeServiecs";

const selectedPlace = ref(null)
const isDrawerOpen = ref(false);

const isLoadingPlaces = ref(false);

const formatPlace = (data) => {
    console.log(data);

    let rating = 0;
    let reviewsCount = data.reviews.length;

    const reviews = data.reviews.map(r => {
        rating += r.rating

        return {
            id: r.id,
            userName: r.user.userName,
            rating: r.rating,
            comment: r.description
        }
    });

    rating = reviewsCount !== 0 ? rating / reviewsCount : 0;

    const placeFeatures = [];

    data.placeFeatures.forEach(pl => {
        if (pl.isAvaliable) {
            placeFeatures.push(pl.accessibillityType)
        }
    })

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
    }
}

const places = ref([]);

const fetchAllPlaces = async () => {
    try {
        isLoadingPlaces.value = true;
        console.log(isLoadingPlaces);
        
        const { data } = await placeServiecs.getAllPlaces();

        const formatedData = data.map(formatPlace)

        return formatedData;
    } catch (error) {
        console.log(error);
    } finally {
        isLoadingPlaces.value = false;
    }
}

const fetchPlacesByCategory = async (category) => {
    try {
        isLoadingPlaces.value = true;
        const { data } = await placeServiecs.getPlacesByCategory(category);

        const formatedData = data.map(formatPlace)

        return formatedData;
    } catch (error) {
        console.log(error);
    } finally {
        isLoadingPlaces.value = false;
    }
}

const openPlaceDetails = (place) => {
    console.log(place);
    
    selectedPlace.value = place
    isDrawerOpen.value = true;
    console.log("this is showen");

}

const closePlaceDetails = () => {
    isDrawerOpen.value = false
    selectedPlace.value = null
}

export const usePlaces = () => {
    return {
        places,
        selectedPlace,
        isDrawerOpen,
        isLoadingPlaces,
        fetchAllPlaces,
        openPlaceDetails,
        closePlaceDetails
    }
}