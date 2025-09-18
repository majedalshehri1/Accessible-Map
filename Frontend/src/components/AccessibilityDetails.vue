<script setup>
import { Badge } from '@/components/ui/badge'

import {
    CheckCircle,
    XCircle,
    Car,
    Building2,
    WashingMachine,
    Accessibility,
    Utensils,
    DoorOpen,
} from 'lucide-vue-next'

const props = defineProps({
    placeFeatures: {
        type: Array,
        required: true
    }
});

const placeFeatures = props.placeFeatures;

const accessibilityItems = [
    { key: 'ELEVATORS', label: 'مصاعد', icon: Building2, isAvalibale: false },
    { key: 'PARKING', label: 'مواقف مخصصة', icon: Car, isAvalibale: false },
    { key: 'DEDICATED_RESTROOMS', label: 'دورات مياه مخصصة', icon: WashingMachine, isAvalibale: false },
    { key: 'RAMPS', label: 'سلالم مخصصة', icon: Accessibility, isAvalibale: false },
    { key: 'DEDICATED_DINING_TABLES', label: 'طاولات طعام', icon: Utensils, isAvalibale: false },
    { key: 'AUTOMATIC_DOORS', label: 'أبواب أوتوماتيكية', icon: DoorOpen, isAvalibale: false },
]

const availableAccessibilityItems = accessibilityItems.map(pl => {
    if (placeFeatures.includes(pl.key)) {
        return { ...pl, isAvalibale: true }
    }

    return pl
})

</script>

<template>
    <div class="space-y-4">
        <h3 class="text-lg font-semibold">الخدمات المقدمة</h3>
        <div class="grid gap-3">
            <div v-for="item in availableAccessibilityItems" :key="item.key"
                class="flex items-center justify-between p-3 rounded-lg border">
                <div class="flex items-center gap-3">
                    <component :is="item.icon" class="h-5 w-5 text-zinc-600" />
                    <span class="font-medium">{{ item.label }}</span>
                </div>
                <Badge :variant="item.isAvalibale ? 'default' : 'secondary'" class="flex items-center gap-1"
                    :class="item.isAvalibale ? 'bg-blue-600 hover:bg-blue-700 ring-blue-300' : ''">
                    <CheckCircle v-if="item.isAvalibale" class="h-3 w-3" />
                    <XCircle v-else class="h-3 w-3" />
                    {{ item.isAvalibale ? 'متوفر' : 'غير متوفر' }}
                </Badge>
            </div>
        </div>
    </div>
</template>