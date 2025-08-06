<script setup>
import Header from '@/components/Header.vue';

import { RouterView } from 'vue-router';

import { useRoute } from 'vue-router'; 

const route = useRoute();

</script>

<template>
    <Header v-if="route.path === '/'" />
    <main class="flex-1 relative">
        <RouterView />
    </main>
</template>