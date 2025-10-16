<template>
  <div id="app">
    <BudgetTable />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import BudgetTable from '../components/budget/BudgetTable.vue';

export default defineComponent({
  name: 'App',
  components: {
    BudgetTable
  }
});

</script>