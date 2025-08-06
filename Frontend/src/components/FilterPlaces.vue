<script setup>
import { CheckCircle, Coffee, Hospital, Trees, Utensils } from 'lucide-vue-next';

import Button from './ui/button/Button.vue';
import { ref } from 'vue';

const currentSelectedFilter = ref(1)

const categories = [
    {
        id: 0, label: "الكل", value: "all"
    },
    {
        id: 1, label: "المطاعم", icon: Utensils, value: "resturant"
    },
    {
        id: 2, label: "المقاهي", icon: Coffee, value: "coffe"
    },
    {
        id: 3, label: "المنتزهات", icon: Trees, value: "parks"
    },
    {
        id: 4, label: "المستشفيات", icon: Hospital, value: "hospita;"
    },
]

</script>

<template>
    <div
        class="absolute top-6 left-1/2 -translate-x-1/2 flex flex-wrap gap-2 max-w-64 sm:max-w-72 md:max-w-lg mx-auto z-[800]">
        <Button v-for="{ id, label, icon, value } in categories" :key="id" @click="currentSelectedFilter = id"
            :variant="currentSelectedFilter === id ? '' : 'outline'" size="sm" class="relative flex-[1_1_0%]">
            <CheckCircle v-if="currentSelectedFilter === id"
                class="absolute -top-1 right-0 translate-x-1/2 h-2 w-2 fill-zinc-100 stroke-zinc-900 z-[801]" />
            <component v-if="icon" :is="icon" class="h-4 w-4 mr-2" />
            {{ label }}
        </Button>
    </div>
</template>