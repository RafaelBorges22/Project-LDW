<template>
  <div id="app">
    <Navbar />
    <BudgetForm />
    <Footer />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';
import BudgetForm from '../components/budget/BudgetForm.vue';

export default defineComponent({
  name: 'App',
  components: {
    Navbar,
    Footer,
    BudgetForm
  }
});

</script>