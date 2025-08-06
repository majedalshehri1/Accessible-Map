    <script setup>
// Vue 3 Composition API imports
import { ref, onMounted } from 'vue'

// Current year reactive reference
const currentYear = ref(new Date().getFullYear())

// Update year on component mount
onMounted(() => {
  currentYear.value = new Date().getFullYear()
})
</script>

<template>
  <!-- Simple footer with centered content -->
  <footer class="py-6 sticky top-0 z-40 border-b border-zinc-200 bg-zinc-50/60 pt-1 backdrop-blur">
    <div class="container mx-auto px-4">
      <div class="text-center">
        <!-- Copyright text with current year -->
        <p class="text-sm">
          جميع الحقوق محفوظة © {{ currentYear }}
        </p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
/* Arabic font import */
@import url("https://fonts.googleapis.com/css2?family=Cairo:wght@200;300;400;500;600;700;800;900&display=swap");

/* Global font family application */
* {
  font-family: "Cairo", sans-serif;
}
</style>