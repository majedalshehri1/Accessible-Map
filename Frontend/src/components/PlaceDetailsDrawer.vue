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
import { usePlace } from "@/composables/place/usePlace";
import { toRef, watch } from "vue";
import { toast } from "vue-sonner";
import PlaceDetailsSkeleton from "./PlaceDetailsSkeleton.vue";
import Skeleton from "./ui/skeleton/Skeleton.vue";

const categories = {
  RESTAURANT: "المطاعم",
  COFFEE: "المقاهي",
  PARK: "المنتزهات",
  HOSPITAL: "المستشفيات",
  MALL: "أسواق",
};

const props = defineProps({
  placeId: {
    default: null
  }
});
const placeId = toRef(props, 'placeId');

// Debug: Watch placeId changes
watch(placeId, (newId) => {
  console.log('placeId updated:', newId, !!newId);
});

const { data: selectedPlace, isPending, error } = usePlace(placeId);

watch(error, (err) => {
  if (err) {
    toast.error("حدث خطأ اثناء تحميل الأماكن. يرجى اعادة التجربة");
  }
});

</script>

<template>
  <Drawer
    :open="!!placeId"
    @update:open="(val) => !val && $emit('update:placeId', null)"
  >
    <DrawerContent class="h-[80vh] z-900">
      <DrawerHeader class="border-b">
        <div class="flex items-center justify-between">
          <DrawerTitle>
            <!-- Skeleton header -->
            <template v-if="isPending">
              <Skeleton class="h-7 w-48 mb-2" />
              <Skeleton class="h-3 w-24" />
            </template>

            <!-- actual header -->
            <template v-else>
              <span class="text-3xl font-medium block text-blue-500">
                {{ selectedPlace?.data.placeName }}
              </span>
              <span class="text-xs text-zinc-400">
                {{ categories[selectedPlace?.data.placeCategory] || "" }}
              </span>
            </template>
          </DrawerTitle>
          <DrawerClose as-child>
            <Button variant="ghost" size="sm">
              <X class="h-4 w-4" @click="$emit('update:placeId', null)"/>
            </Button>
          </DrawerClose>
        </div>
      </DrawerHeader>

      <div class="overflow-y-auto flex-1 p-4">
        <!-- show skeleton when loading -->
        <PlaceDetailsSkeleton v-if="isPending" />

        <!-- actual content -->
        <div v-else class="space-y-6">
          <Carousel :images="selectedPlace?.data.images" />

          <Tabs default-value="accessibility" class="w-full" dir="rtl">
            <TabsList class="grid w-full grid-cols-2">
              <TabsTrigger value="accessibility">الخدمات</TabsTrigger>
              <TabsTrigger value="reviews">التقييمات</TabsTrigger>
            </TabsList>

            <TabsContent value="accessibility" class="mt-4" dir="rtl">
              <AccessibilityDetails
                :place-features="selectedPlace?.data.features"
              />
            </TabsContent>

            <TabsContent value="reviews" class="mt-4" dir="rtl">
              <Reviews
                :rating="selectedPlace.data.avgRating || 0"
                :reviews-count="selectedPlace.data.reviewsCount || 0"
                :reviews="selectedPlace.data.reviews || []"
              />
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </DrawerContent>
  </Drawer>
</template>
