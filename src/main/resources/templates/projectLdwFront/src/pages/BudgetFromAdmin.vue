<template>
  <div id="app">
    <Navbar />
    <router-view />
    <Footer />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';

export default defineComponent({
  name: 'BudgetFromAdmin',
  components: {
    Navbar,
    Footer
  }
});
</script>