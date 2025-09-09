<script setup>
import {
  Drawer,
  DrawerContent,
  DrawerHeader,
  DrawerTitle,
  DrawerClose,
} from "./ui/drawer";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { X } from "lucide-vue-next";
import Carousel from "./Carousel.vue";
import AccessibilityDetails from "./AccessibilityDetails.vue";
import Reviews from "./Reviews.vue";
import { usePlacesStore } from "@/stores/placeStore";
import { storeToRefs } from "pinia";

const placesStore = usePlacesStore();
const { selectedPlace, isDrawerOpen } = storeToRefs(placesStore);
const { closePlaceDetails } = placesStore;

const categories = {
  RESTAURANT: "المطاعم",
  COFFEE: "المقاهي",
  PARK: "المنتزهات",
  HOSPITAL: "المستشفيات",
  MALL: "أسواق",
};

console.log(selectedPlace.value);
</script>

<template>
  <Drawer
    :open="isDrawerOpen"
    @update:open="(val) => !val && closePlaceDetails()"
  >
    <DrawerContent class="h-[80vh] z-900">
      <DrawerHeader class="border-b">
        <div class="flex items-center justify-between">
          <DrawerTitle>
            <span class="text-3xl font-medium block text-blue-500">{{
              selectedPlace?.name
            }}</span>
            <span class="text-xs text-zinc-400">{{
              categories[selectedPlace?.placeCategory] || ""
            }}</span>
          </DrawerTitle>
          <DrawerClose as-child>
            <Button variant="ghost" size="sm">
              <X class="h-4 w-4" />
            </Button>
          </DrawerClose>
        </div>
      </DrawerHeader>

      <div class="overflow-y-auto flex-1 p-4">
        <div v-if="selectedPlace" class="space-y-6">
          <!-- Image Carousel -->
          <Carousel :images="selectedPlace.images" />

          <!-- Tabs for Details and Reviews -->
          <Tabs default-value="accessibility" class="w-full" dir="rtl">
            <TabsList class="grid w-full grid-cols-2">
              <TabsTrigger value="accessibility">الخدمات</TabsTrigger>
              <TabsTrigger value="reviews">التقييمات</TabsTrigger>
            </TabsList>

            <TabsContent value="accessibility" class="mt-4" dir="rtl">
              <AccessibilityDetails
                :place-features="selectedPlace.placeFeatures"
              />
            </TabsContent>

            <TabsContent value="reviews" class="mt-4" dir="rtl">
              <Reviews
                :rating="selectedPlace.rating"
                :reviews-count="selectedPlace.reviewsCount"
                :reviews="selectedPlace.reviews"
              />
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </DrawerContent>
  </Drawer>
</template>
