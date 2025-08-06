<script setup>
import Button from './ui/button/Button.vue';
import Search from './Search.vue';

</script>

<template>
  <header class="sticky top-0 z-40 border-b border-zinc-200 bg-zinc-50/90 pt-1 backdrop-blur">
    <div class="container px-4">
      <div class="relative flex h-14 items-center gap-2 sm:justify-between sm:gap-4">
        <div>LOGO</div>

        <Search />

        <div class="">
          <Button>تسجيل الدخول</Button>
        </div>
      </div>
    </div>
  </header>
</template>