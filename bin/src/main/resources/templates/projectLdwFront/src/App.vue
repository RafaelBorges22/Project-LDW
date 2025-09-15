<template>
  <div id="app">
    <Navbar />
    <HeroSection />

    <AboutSection
      title="SOBRE TATUAGENS"
      text="lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
      imageUrl="../../../public/images/photos/AboutImgLast.png" />

    <AboutSection
      title="SOBRE MIM"
      text="lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
      imageUrl="../../../public/images/photos/AboutImgFirst.png" reverse
    />
    
    <Footer />

  </div>
</template>

<script>
import Navbar from './components/global/Navbar.vue';
import HeroSection from './components/home/HeroSection.vue';
import AboutSection from './components/home/AboutSection.vue';
import Footer from './components/global/Footer.vue';

export default {
  name: 'App',
  components: {
    Navbar,
    HeroSection,
    AboutSection, 
    Footer
  }
};
</script>

<style>
@import './assets/Global.css';
</style>