<template>
  <nav class="navbar" role="navigation" aria-label="Main navigation">
    <div class="navbar-left">
      <router-link to="/" class="brand">
        <img src="../../assets/base/Logo.png" alt="Kazu Tatoo Logo" class="logo-image" />
      </router-link>
      <span class="logo-text">Kazu Tatoo</span>
    </div>

    <div class="navbar-right">
      <ul class="nav-links" role="menubar">

        <li role="none">
          <router-link to="/">
            <i class="fi fi-sr-home" aria-hidden="true"></i>
          </router-link>
        </li>

        <li v-if="isLoggedIn" role="none">
          <router-link to="/budget">
            <i class="fi fi-ss-checklist-task-budget" aria-hidden="true"></i>
          </router-link>
        </li>

        <li role="none">
          <router-link to="/">
            <i class="fi fi-sr-messages" aria-hidden="true"></i>
          </router-link>
        </li>

        <li v-if="!isLoggedIn" role="none">
          <router-link to="/login">
            <i class="fi fi-ss-user-add"></i>
          </router-link>
        </li>

        <li v-if="isLoggedIn" role="none">
          <router-link to="/account">
            <i class="fi fi-ss-user"></i>
          </router-link>
        </li>
        
      </ul>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'Navbar',

  data() {
    return {
      isLoggedIn: !!localStorage.getItem('jwtToken') // estado reativo inicial
    };
  },

  created() {
    // Atualiza quando a rota muda (útil após login/logout que navega para Home)
    this.$watch(
      () => this.$route.fullPath,
      () => {
        this.updateAuthState();
      }
    );

    // Escuta mudanças de localStorage vindas de outras abas
    window.addEventListener('storage', this.onStorage);
  },

  beforeUnmount() {
    window.removeEventListener('storage', this.onStorage);
  },

  methods: {
    updateAuthState() {
      // considera strings vazias/null/undefined como não logado
      const raw = localStorage.getItem('jwtToken');
      const token = raw && String(raw).trim() && raw.toLowerCase() !== 'null' && raw.toLowerCase() !== 'undefined'
        ? raw
        : null;
      this.isLoggedIn = !!token;
    },

    onStorage(event) {
      if (event.key === 'jwtToken') {
        // sincroniza estado entre abas
        this.updateAuthState();
      }
    }
  }
};
</script>

<style scoped>
@import '../../assets/Scss/global/Navbar.scss';

/* estilo local do botão removido (não mais usado aqui) mantido mínimo caso queira reativar algo */
.logout-btn {
  display: none;
}
</style>
