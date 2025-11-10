<template>
  <nav class="navbar">
    <div class="navbar-left">
      <img src="../../../public/images/Logo.png" alt="Kazu Tatoo Logo" class="logo-image" />
      <span class="logo-text">Kazu Tatoo</span>
    </div>
    <div class="navbar-right">
      <ul class="nav-links">
        <li><a href="#"><i class="fi fi-sr-home"></i></a></li>

        <li><a href="#"><i class="fi fi-sr-hand-holding-heart"></i></a></li>

        <li><a href="#"><i class="fi fi-ss-checklist-task-budget"></i></a></li>

        <li><a href="#"><i class="fi fi-sr-messages"></i></a></li>
      </ul>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'Navbar'
};
</script>

<style scoped>
@import '../../assets/Navbar.scss';
</style>
