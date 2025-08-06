<script setup>
import { Badge } from '@/components/ui/badge'

import {
    CheckCircle,
    XCircle,
    Car,
    Building2,
    WashingMachine,
} from 'lucide-vue-next'

defineProps({
    accessibility: {
        type: Object,
        required: true
    }
})

const accessibilityItems = [
    { key: 'elevator', label: 'مصاعد', icon: Building2 },
    { key: 'parking', label: 'مواقف مخصصة', icon: Car },
    { key: 'toilets', label: 'دورات مياه مخصصة', icon: WashingMachine },
]
</script>

<template>
    <div class="space-y-4">
        <h3 class="text-lg font-semibold">الخدمات المقدمة</h3>
        <div class="grid gap-3">
            <div v-for="item in accessibilityItems" :key="item.key"
                class="flex items-center justify-between p-3 rounded-lg border">
                <div class="flex items-center gap-3">
                    <component :is="item.icon" class="h-5 w-5 text-zinc-600" />
                    <span class="font-medium">{{ item.label }}</span>
                </div>
                <Badge :variant="accessibility[item.key] ? 'default' : 'secondary'" class="flex items-center gap-1">
                    <CheckCircle v-if="accessibility[item.key]" class="h-3 w-3" />
                    <XCircle v-else class="h-3 w-3" />
                    {{ accessibility[item.key] ? 'متوفر' : 'غير متوفر' }}
                </Badge>
            </div>
        </div>
    </div>
</template>