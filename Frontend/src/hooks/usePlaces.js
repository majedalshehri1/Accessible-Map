import { ref } from "vue";

const selectedPlace = ref(null)
const isDrawerOpen = ref(false);

const places = ref([
    {
        id: 1,
        name: "Riyadh Mall",
        lat: 24.960547398165134,
        lng: 46.71390262311927,
        images: [
            "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400",
            "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400",
            "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400",
        ],
        accessibility: {
            elevator: true,
            parking: true,
            toilets: true,
            ramps: true,
            brailleSignage: false
        },
        rating: 4.2,
        reviewsCount: 156,
        reviews: [
            {
                id: 1,
                userName: "Ahmed Al-Salem",
                rating: 5,
                comment: "Great accessibility features, very wheelchair friendly!",
                date: "2024-01-15"
            },
            {
                id: 2,
                userName: "Sara Mohammed",
                rating: 4,
                comment: "Good facilities but could use more disabled parking spots.",
                date: "2024-01-10"
            }
        ]
    },
    // Add more places...
]);

const openPlaceDetails = (place) => {
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
        openPlaceDetails,
        closePlaceDetails
    }
}