<template>
  <section class="about-section" :class="{ 'reverse-layout': reverse }">
    <div class="about-image-container">
      <img :src="imageUrl" alt="About Image" class="about-image" />
    </div>
    <div class="about-content">
      <h2 class="about-title">{{ title }}</h2>
      <p class="about-text">{{ text }}</p>
    </div>
  </section>
</template>

<script>
export default {
  name: 'AboutSection',
  props: {
    title: {
      type: String,
      required: true
    },
    text: {
      type: String,
      required: true
    },
    imageUrl: {
      type: String,
      required: true
    },
    reverse: {
      type: Boolean,
      default: false
    }
  }
};
</script>

<style scoped>
@import '../../assets/home/AboutSection.css';
</style>