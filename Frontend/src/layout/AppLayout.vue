<script setup>
import Header from '@/components/Header.vue';

import { RouterView } from 'vue-router';

</script>

<template>
    <Header />
    <main class="flex-1 relative">
        <RouterView />
    </main>
</template>