<template>
  <nav class="navbar">
    <div class="navbar-left">
      <router-link to="/"><img src="../../assets/base/Logo.png" alt="Kazu Tatoo Logo" class="logo-image" /></router-link>
      <span class="logo-text">Kazu Tatoo</span>
    </div>

    <div class="navbar-right">
      <ul class="nav-links">
        <li>
          <router-link to="/"><i class="fi fi-sr-home"></i></router-link>
        </li>
        <li>
          <router-link to="/"><i class="fi fi-sr-hand-holding-heart"></i></router-link>
        </li>
        <li>
          <router-link to="/budget"><i class="fi fi-ss-checklist-task-budget"></i></router-link>
        </li>
        <li>
          <router-link to="/"><i class="fi fi-sr-messages"></i></router-link>
        </li>
        <li>
          <a href="#" @click.prevent="showLogoutConfirm = true"><i class="fi fi-ss-user-logout"></i></a>
        </li>
      </ul>
    </div>

<div
  v-if="showLogoutConfirm"
  class="logout-popup-overlay"
  @click="cancelLogout"
  aria-modal="true"
  role="dialog"
>
  <div class="logout-popup-card info" @click.stop>
    <div class="logout-popup-logo-container">
      <span class="logout-popup-logo">
        <i class="fi fi-ss-exit"></i>
      </span>
    </div>

    <h3 class="logout-popup-title">Deseja realmente sair?</h3>

    <p class="logout-popup-description">
      Você está prestes a sair. Deseja continuar?
    </p>

    <div class="logout-popup-actions">
      <button type="button" class="logout-popup-btn" @click="logoutNow">
        Sim, sair <i class="fi fi-ss-exit"></i>
      </button>
      <button
        type="button"
        class="logout-popup-btn outline"
        @click="cancelLogout"
      >
        Cancelar
      </button>
    </div>
  </div>
</div>

  </nav>
</template>

<script>
export default {
  name: 'Navbar',
  data() {
    return {
      showLogoutConfirm: false
    };
  },
  methods: {
    logoutNow() {
      localStorage.removeItem("jwtToken");
      localStorage.removeItem("usuarioEmail");
      localStorage.removeItem("usuarioId");
      localStorage.removeItem("usuarioNome");
      localStorage.removeItem("permissao");
      this.showLogoutConfirm = false;
      this.$router.push('/login');
    },
    cancelLogout() {
      this.showLogoutConfirm = false;
    }
  }
};
</script>

<style scoped>
@import '../../assets/Scss/global/Navbar.scss';
</style>
