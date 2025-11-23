<template>
  <section class="hero-section">
    <div class="hero-content">
      <h1 class="hero-title">TATUE AQUI!</h1>
      <button class="hero-button" @click="goToBudget">AGENDE SEU HORÁRIO</button>
    </div>
  </section>
</template>

<script>
export default {
  name: 'HeroSection',
  methods: {
    goToBudget() {
      window.location.href = '/budget'; 
    }
  }
};
</script>

<style scoped>
@import '../../assets/home/HeroSection.scss'; 
</style>