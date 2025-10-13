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
        <li><a href="#" @click.prevent="showLogoutConfirm = true"><i class="fi fi-ss-user-logout"></i>
          </a>
        </li>
      </ul>
    </div>

    <!-- Confirmação de logout -->
    <div v-if="showLogoutConfirm" class="logout-confirm">
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
@import '../../assets/Navbar.scss';
</style>
