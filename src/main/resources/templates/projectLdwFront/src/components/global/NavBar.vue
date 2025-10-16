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

    <div v-if="showLogoutConfirm" class="logout-confirm" @click="cancelLogout">
      <div class="confirm-box">
        <p>Deseja realmente sair?</p>
        <div class="confirm-actions">
          <button @click="logoutNow">Sim</button>
          <button @click="cancelLogout">Cancelar</button>
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
