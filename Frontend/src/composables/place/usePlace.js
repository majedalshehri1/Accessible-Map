import placeServiecs from "@/services/placeServiecs";
import { useQuery } from "@tanstack/vue-query";
import { computed } from "vue";

export function usePlace(placeId) { // Expect placeId to be a ref
  // Ensure placeId is reactive in queryKey
  const queryKey = computed(() => ['place', placeId?.value]);

  // Ensure enabled is reactive
  const enabled = computed(() => !!placeId?.value);

  console.log('usePlace:', enabled.value, placeId?.value);

  return useQuery({
    queryKey,
    queryFn: () => placeServiecs.getPlaceById(placeId.value), // Pass value to queryFn
    enabled,
  });
}