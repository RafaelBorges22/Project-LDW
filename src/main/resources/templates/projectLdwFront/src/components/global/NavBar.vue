<template>
  <nav class="navbar">
    <div class="navbar-left">
      <img src="../../../public/images/Logo.png" alt="Kazu Tatoo Logo" class="logo-image" />
      <span class="logo-text">Kazu Tatoo</span>
    </div>
    <div class="navbar-right">
      <ul class="nav-links">
        <li><a href="#"><img src="../../../public/images/icons/home_icon.png" alt="Home" class="nav-icon-img" /></a></li>

        <li><a href="#"><img src="../../../public/images/icons/perfil_icon.png" alt="Perfil" class="nav-icon-img" /></a></li>

        <li><a href="#"><img src="../../../public/images/icons/budget_icon.png" alt="Documentos" class="nav-icon-img" /></a></li>

        <li><a href="#"><img src="../../../public/images/icons/chat_icon.png" alt="Chat" class="nav-icon-img" /></a></li>
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
@import '../../assets/Navbar.css';
</style>
