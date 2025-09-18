<script setup>
import Skeleton from './ui/skeleton/Skeleton.vue';

</script>

<template>
    <div class="space-y-6">
        <div class="w-full aspect-video rounded-xl bg-muted">
            <Skeleton class="w-full h-full rounded-xl" />
        </div>

        <div>
            <div class="grid w-full grid-cols-2 gap-2">
                <Skeleton class="h-9 w-full" />
                <Skeleton class="h-9 w-full" />
            </div>

            <div class="mt-4 space-y-3">
                <Skeleton class="h-4 w-3/4" />
                <Skeleton class="h-4 w-2/3" />
                <Skeleton class="h-4 w-5/6" />
                <Skeleton class="h-4 w-1/2" />
            </div>
        </div>
    </div>
</template>
