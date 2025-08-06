<template>
  <div class="relative flex-1">
    <SearchIcon class="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground w-4 h-4" />
    <Input
      v-model="searchValue"
      placeholder="بحث..."
      class="h-auto py-1.5 pl-8 pr-10"
      @input="handleInput"
    />
    <button
      v-if="showCloseButton"
      @click="handleClose"
      class="absolute left-2 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
    >
      <X class="w-4 h-4" />
    </button>
  </div>
</template>

<script setup>
import { Search as SearchIcon, X } from 'lucide-vue-next'
import { ref } from 'vue'
import Input from './ui/input/Input.vue'

const props = defineProps({
  showCloseButton: {
    type: Boolean,
    default: false
  },
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'close', 'search'])

const searchValue = ref(props.modelValue)

const handleInput = (event) => {
  searchValue.value = event.target.value
  emit('update:modelValue', searchValue.value)
  emit('search', searchValue.value)
}

const handleClose = () => {
  searchValue.value = ''
  emit('update:modelValue', '')
  emit('close')
}
</script>
