<template>
  <div id="app">
    <Navbar />

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">TATUE AQUI!</h1>
        <button class="hero-button" @click="goToBudget">AGENDE SEU HORÁRIO</button>
      </div>
    </section>

    <!-- About Section - TATUAGENS -->
    <section class="about-section">
      <div class="about-image-container">
        <img src="../assets/images/AboutImgLast.png" alt="Sobre Tatuagens" class="about-image" />
      </div>
      <div class="about-content">
        <h2 class="about-title">SOBRE TATUAGENS</h2>
        <p class="about-text">
          lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore
          et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
          aliquip ex ea commodo consequat.
        </p>
      </div>
    </section>

    <!-- About Section - SOBRE MIM -->
    <section class="about-section reverse-layout">
      <div class="about-image-container">
        <img src="../assets/images/AboutImgFirst.png" alt="Sobre Mim" class="about-image" />
      </div>
      <div class="about-content">
        <h2 class="about-title">SOBRE MIM</h2>
        <p class="about-text">
          lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore
          et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
          aliquip ex ea commodo consequat.
        </p>
      </div>
    </section>

    <Footer />
  </div>
</template>

<script>
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';

export default {
  name: 'HomePage',
  components: {
    Navbar,
    Footer
  },
  methods: {
    goToBudget() {
      window.location.href = '/budget';
    }
  }
};
</script>

<style>
@import '../assets/Scss/global/Global.scss';
@import '../assets/Scss/pages/home.scss';

</style>