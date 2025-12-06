<template>
  <div id="app">
    <Navbar />
    <BudgetTableCL />
    <Footer />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';
import BudgetTableCL from '../components/budget/BudgetFromCL.vue';

export default defineComponent({
  name: 'App',
  components: {
    Navbar,
    Footer,
    BudgetTableCL
  }
});

</script>

<style scoped>
@import '../assets/Scss/pages/BudgetForm.scss';
</style>