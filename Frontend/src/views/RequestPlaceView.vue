<script setup>
// Vue 3 Composition API imports
import { ref, watch } from "vue";
import router from "@/router";
import placeServiecs from "@/services/placeServiecs";
import { uploadImages } from "@/services/uploadService";

// shadcn-vue UI component imports
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectTrigger,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent } from "@/components/ui/card";

//Component imports
import Footer from "@/components/Footer.vue";
import InputMap from "@/components/InputMap.vue";
import { toast } from "vue-sonner";

// Form data reactive reference - Contains all form fields
const form = ref({
  name: "",
  category: "",
  services: [],
  location: { lat: "", lng: "" },
  imageUrls: [],
  agree: false,
});

const availableServices = ref([
  { id: "PARKING", name: "مواقف" },
  { id: "DEDICATED_RESTROOMS", name: "دورات المياه" },
  { id: "RAMPS", name: "المنحدرات" },
  { id: "ELEVATORS", name: "المصاعد" },
  { id: "AUTOMATIC_DOORS", name: "أبواب أوتوماتيكية" },
  { id: "DEDICATED_DINING_TABLES", name: "طاولات الطعام" },
]);

// refrences for inputs
const isLoading = ref(false);
// const fileInputRef = ref(null)

// Form validation function
const validateForm = () => {
  if (!form.value.name.trim()) {
    toast.error("يرجى إدخال اسم المكان");
    return false;
  }
  if (!form.value.category) {
    toast.error("يرجى اختيار فئة المكان");
    return false;
  }
  if (!form.value.agree) {
    toast.error("يجب الموافقة على الشروط والأحكام أولاً");
    return false;
  }

  if (selectedFiles.value.length > maxImages) {
    toast.error("يمكنك رفع " + maxImages + " صور كحد أقصى");
    return false;
  }
  return true;
};

const toggleService = (serviceId) => {
  const index = form.value.services.indexOf(serviceId);
  if (index > -1) {
    form.value.services.splice(index, 1);
  } else {
    form.value.services.push(serviceId);
  }
};

const isServiceSelected = (serviceId) => {
  return form.value.services.includes(serviceId);
};

// sync marker location when a new marker is added
const handleAddMarker = (newLocation) => {
  form.value.location = newLocation;
};

const maxImages = 3;
const selectedFiles = ref([]);
const imagesPreview = ref([]);

// selectedFiles.value make it urls

const handleFileChange = async (event) => {
  const files = Array.from(event.target.files).filter((file) =>
    file.type.startsWith("image/")
  );

  if (files.length + selectedFiles.value.length > maxImages) {
    toast.error(`يمكنك رفع ${maxImages} صور كحد أقصى`);
    return;
  }

  selectedFiles.value = [...files, ...selectedFiles.value];

  console.log(selectedFiles.value);

  const promises = selectedFiles.value.map((file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        resolve(e.target.result);
      };
      reader.onerror = (error) => reject(error);
      reader.readAsDataURL(file);
    });
  });

  Promise.all(promises)
    .then((urls) => {
      imagesPreview.value = urls;
      console.log(imagesPreview.value);
      console.log(
        Array.isArray(imagesPreview.value),
        imagesPreview.value.length
      );
    })
    .catch((error) => {
      console.error("Error processing images:", error);
    });
};
const submitForm = async () => {
  if (!validateForm()) return;

  isLoading.value = true;

  try {
    // upload imgs to cdn
    const urls = await uploadImages(selectedFiles.value);
    // Prepare the request payload
    const requestPayload = {
      placeName: form.value.name,
      longitude: form.value.location.lng.toString(),
      latitude: form.value.location.lat.toString(),
      category: form.value.category,
      accessibilityFeatures: form.value.services,
      imageUrls: urls,
    };

    console.log("Request payload:", JSON.stringify(requestPayload, null, 2));

    // send the request to the backend
    const res = await placeServiecs.createPlace(requestPayload);
    console.log("Form submitted successfully:", res.data);

    toast.success("تم إرسال طلب إضافة المكان بنجاح!");
    router.push("/");
    resetForm();
  } catch (error) {
    console.error("Error submitting form:", error);
    toast.error("حدث خطأ أثناء إرسال الطلب. يرجى المحاولة مرة أخرى.");
  } finally {
    isLoading.value = false;
  }
};
const removeImage = (index) => {
  // delete from selectedFiles and imagesPreview as well
  selectedFiles.value.splice(index, 1);
  imagesPreview.value.splice(index, 1);
  console.log(selectedFiles.value, imagesPreview.value);
};

// Reset form fields to initial state
const resetForm = () => {
  form.value = {
    name: "",
    category: "",
    services: [],
    location: { lat: "", lng: "" },
    imageUrls: [],
    agree: false,
  };
};

const goBack = () => {
  console.log("Going back...");
  router.push("/");
};
</script>

<template>
  <div
    dir="rtl"
    class="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 py-8 px-4"
  >
    <div class="max-w-2xl mx-auto">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-slate-800 mb-2">
          طلب إضافة مكان جديد
        </h1>
        <p class="text-slate-600 text-lg">أضف مكانك التي تريد تقييمة</p>
      </div>

      <Card class="shadow-xl border-0 bg-white/80 backdrop-blur-sm">
        <CardContent class="p-8">
          <!-- Form element with submit prevention -->
          <form @submit.prevent="submitForm" class="space-y-8">
            <!-- Basic information section -->
            <div class="space-y-6">
              <!-- Place name and category row -->
              <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <!-- Place name input (takes 2 columns on large screens) -->
                <div class="lg:col-span-2 space-y-2">
                  <Label
                    for="place-name"
                    class="text-sm font-medium text-slate-700"
                  >
                    اسم المكان <span class="text-red-500">*</span>
                  </Label>
                  <Input
                    id="place-name"
                    v-model="form.name"
                    placeholder="ادخل اسم المكان"
                    class="h-12 text-right border-slate-200 focus:border-blue-500 focus:ring-blue-500/20"
                    :disabled="isLoading"
                    required
                  />
                </div>

                <!-- Category selection -->
                <div class="space-y-2">
                  <Label
                    for="category"
                    class="text-sm font-medium text-slate-700"
                  >
                    فئة المكان <span class="text-red-500">*</span>
                  </Label>
                  <Select v-model="form.category" :disabled="isLoading">
                    <SelectTrigger
                      class="w-full border-slate-200 focus:border-blue-500 cursor-pointer"
                    >
                      <SelectValue placeholder="اختر الفئة" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="RESTAURANT">مطعم</SelectItem>
                      <SelectItem value="COFFEE">كافيه</SelectItem>
                      <SelectItem value="HOSPITAL">مستشفى</SelectItem>
                      <SelectItem value="MALL">مول</SelectItem>
                      <SelectItem value="PARK">حديقة</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </div>

            <!-- Services selection section -->
            <div class="space-y-6 border-t pt-6">
              <div class="space-y-4">
                <Label class="text-lg font-semibold text-slate-700"
                  >الخدمات المقدمة</Label
                >
                <p class="text-sm text-slate-500">
                  اختر الخدمات المتوفرة في هذا المكان
                </p>

                <!-- Services grid -->
                <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
                  <div
                    v-for="service in availableServices"
                    :key="service.id"
                    @click="toggleService(service.id)"
                    class="relative group cursor-pointer transition-all duration-300 transform hover:scale-105"
                    :class="{ 'pointer-events-none opacity-50': isLoading }"
                  >
                    <!-- Service card -->
                    <div
                      class="p-4 rounded-xl border-2 transition-all duration-300 text-center min-h-[100px] flex flex-col items-center justify-center space-y-2"
                      :class="
                        isServiceSelected(service.id)
                          ? 'border-blue-500 bg-blue-50 shadow-lg shadow-blue-100'
                          : 'border-slate-200 bg-white hover:border-blue-300 hover:shadow-md'
                      "
                    >
                      <!-- Service name -->
                      <span
                        class="text-sm font-medium transition-colors duration-300"
                        :class="
                          isServiceSelected(service.id)
                            ? 'text-blue-700'
                            : 'text-slate-700'
                        "
                      >
                        {{ service.name }}
                      </span>

                      <!-- Selection indicator -->
                      <div
                        v-if="isServiceSelected(service.id)"
                        class="absolute -top-2 -right-2 w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center shadow-lg"
                      >
                        <svg
                          class="w-4 h-4 text-white"
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path
                            fill-rule="evenodd"
                            d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                            clip-rule="evenodd"
                          />
                        </svg>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Selected services summary -->
                <div
                  v-if="form.services.length > 0"
                  class="mt-4 p-3 bg-blue-50 rounded-lg border border-blue-200"
                >
                  <p class="text-sm text-blue-700 font-medium">
                    تم اختيار {{ form.services.length }} خدمة
                  </p>
                </div>
              </div>
            </div>

            <!-- Location and images section -->
            <div class="space-y-6 border-t pt-6">
              <!-- Map placeholder -->
              <div class="space-y-2">
                <Label class="text-sm font-medium text-slate-700"
                  >اختر الموقع على الخريطة</Label
                >
                <div
                  class="h-80 bg-gradient-to-br from-slate-100 to-slate-200 rounded-xl border-2 border-dashed border-slate-300 flex flex-col items-center justify-center text-slate-500 hover:border-blue-400 transition-colors duration-200 cursor-pointer"
                  :class="{ 'opacity-50 cursor-not-allowed': isLoading }"
                >
                  <InputMap @on-add-marker="handleAddMarker" />
                </div>
              </div>

              <!-- Image upload section -->
              <div class="space-y-2">
                <Label for="images" class="text-sm font-medium text-slate-700">
                  صور المكان (حد أقصى ٣)
                </Label>
                <input
                  id="images"
                  type="file"
                  accept="image/*"
                  multiple
                  class="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                  :disabled="isLoading || form.imageUrls.length >= maxImages"
                  @change="handleFileChange"
                />

                <!-- Preview selected images -->
                <div class="flex gap-3 mt-3 flex-wrap">
                  <div
                    v-for="(url, index) in imagesPreview"
                    :key="index"
                    class="relative w-24 h-24 rounded-lg overflow-hidden border shadow group"
                  >
                    <img :src="url" class="w-full h-full object-cover" />

                    <!-- Remove button -->
                    <button
                      type="button"
                      @click="removeImage(index)"
                      class="absolute top-1 right-1 bg-red-500 text-white text-xs px-1.5 py-0.5 rounded-full opacity-80 group-hover:opacity-100 transition"
                    >
                      ✕
                    </button>
                  </div>
                </div>

                <p class="text-xs text-slate-500">يمكنك رفع حتى ٣ صور فقط</p>
              </div>
            </div>

            <!-- Terms and conditions -->
            <div class="border-t pt-6">
              <div class="flex items-start gap-3 p-4 bg-slate-50 rounded-lg">
                <Checkbox
                  v-model="form.agree"
                  id="agree"
                  class="mt-1"
                  :disabled="isLoading"
                />
                <div class="space-y-1">
                  <Label
                    for="agree"
                    class="text-sm font-medium text-slate-700 cursor-pointer"
                  >
                    أوافق على الشروط والأحكام وسياسة الخصوصية
                  </Label>
                  <p class="text-xs text-slate-500">
                    بالموافقة، أنت تؤكد أن جميع المعلومات المقدمة صحيحة ودقيقة
                  </p>
                </div>
              </div>
            </div>

            <!-- Action buttons -->
            <div class="flex flex-col sm:flex-row gap-4 justify-between pt-6">
              <!-- Back button -->
              <Button
                type="button"
                variant="outline"
                size="lg"
                class="order-2 sm:order-1 h-12 px-8 border-slate-300 hover:bg-slate-50 cursor-pointer"
                @click="goBack"
                :disabled="isLoading"
              >
                الرجوع
              </Button>
              <!-- Submit button -->
              <Button
                type="submit"
                size="lg"
                class="order-1 sm:order-2 h-12 px-8 bg-blue-600 hover:bg-blue-700 text-white shadow-lg hover:shadow-xl transition-all duration-200 cursor-pointer"
                :disabled="!form.agree || isLoading"
              >
                <!-- Show loading state or normal text -->
                <span v-if="isLoading">جاري الإرسال...</span>
                <span v-else>إرسال الطلب</span>
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  </div>
  <Footer />
</template>

<style scoped>
/* Arabic font import */
@import url("https://fonts.googleapis.com/css2?family=Cairo:wght@200;300;400;500;600;700;800;909&display=swap");

/* Global font family application */
* {
  font-family: "Cairo", sans-serif;
}

/* Body font family */
body {
  font-family: "Cairo", sans-serif;
}

/* Custom scrollbar for better UX */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
