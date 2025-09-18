<script setup>
import { RouterView } from 'vue-router';
import { Toaster } from '@/components/ui/sonner'
import 'vue-sonner/style.css' // vue-sonner v2 requires this import

</script>

<template>
  <RouterView />
  <Toaster dir="rtl" richColors position="top-right" :visibleToasts="1"/>
</template>
