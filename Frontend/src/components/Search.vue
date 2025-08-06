<template>
  <div class="flex items-center flex-1 sm:max-w-lg">
    <!-- Mobile Search Overlay -->
    <div 
      v-show="isOpen"
      class="fixed inset-0 z-50 bg-white flex items-start pt-4 px-4 sm:hidden"
    >
      <SearchInput 
        v-model="searchQuery"
        :showCloseButton="true"
        @close="closeSearch"
        @search="handleSearch"
      />
    </div>
    
    <!-- Desktop Search (always visible on sm+) -->
    <div class="hidden sm:flex flex-1">
      <SearchInput 
        v-model="searchQuery"
        @search="handleSearch"
      />
    </div>
    
    <!-- Mobile Search Icon -->
    <Button 
      variant="ghost" 
      size="icon" 
      class="sm:hidden"
      @click="openSearch"
    >
      <SearchIcon class="w-5 h-5" />
    </Button>
  </div>
</template>

<script setup>
import { Search as SearchIcon } from 'lucide-vue-next'
import { ref } from 'vue'
import SearchInput from './SearchInput.vue'
import Button from './ui/button/Button.vue'

const isOpen = ref(false)
const searchQuery = ref('')

const openSearch = () => {
  isOpen.value = true
}

const closeSearch = () => {
  isOpen.value = false
}

const handleSearch = (query) => {
  console.log('Searching for:', query)
  // Add your search logic here
}
</script>
