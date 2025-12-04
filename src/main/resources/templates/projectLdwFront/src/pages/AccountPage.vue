<template>
  <div id="app">
    <Navbar />
    <div class="account-card">

      <h2 class="title">Minha Conta</h2>

      <div class="info-group">
        <p><strong>Nome:</strong> {{ userName }}</p>
        <p><strong>Email:</strong> {{ userEmail }}</p>
        <p><strong>Telefone:</strong> {{ userPhone }}</p>
        <p><strong>Endereço:</strong> {{ userAddress }}</p>
      </div>

      <div class="account-actions">
        <button class="btn secondary" @click="goBack">
          Voltar para Home
        </button>

        <!-- Botão de logout visível apenas quando logado -->
        <button v-if="isLoggedIn" class="btn danger" @click="showLogoutConfirm = true">
          Sair <i class="fi fi-ss-user-logout" aria-hidden="true"></i>
        </button>
      </div>

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
          <span class="logout-popup-logo" aria-hidden="true">
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

  </div>
  <Footer />
</template>

<script>
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';

export default {
  name: "AccountPage",
  components: { Navbar, Footer },
  
  data() {
    return {
      userName: localStorage.getItem("usuarioNome"),
      userEmail: localStorage.getItem("usuarioEmail"),
      userPhone: localStorage.getItem("usuarioTelefone") || "Não informado",
      userAddress: localStorage.getItem("usuarioEndereco") || "Não informado",
      showLogoutConfirm: false,
      isLoggedIn: !!localStorage.getItem('jwtToken')
    };
  },

  created() {
    // atualizar se o localStorage mudar em outra aba
    window.addEventListener('storage', this.onStorage);
  },

  beforeUnmount() {
    window.removeEventListener('storage', this.onStorage);
  },

  methods: {
    goBack() {
      this.$router.push("/");
    },

    onStorage(event) {
      if (event.key === 'jwtToken') {
        const raw = localStorage.getItem('jwtToken');
        const token = raw && String(raw).trim() && raw.toLowerCase() !== 'null' && raw.toLowerCase() !== 'undefined'
          ? raw
          : null;
        this.isLoggedIn = !!token;
      }
    },

    logoutNow() {
      // remove dados do usuário
      localStorage.removeItem('jwtToken');
      localStorage.removeItem('usuarioEmail');
      localStorage.removeItem('usuarioId');
      localStorage.removeItem('usuarioNome');
      localStorage.removeItem('permissao');

      // atualiza estado local
      this.isLoggedIn = false;
      this.showLogoutConfirm = false;

      // navegar para a Home (tela de login)
      this.$router.push('/');
    },

    cancelLogout() {
      this.showLogoutConfirm = false;
    }
  }
};
</script>

<style scoped>
@import '../assets/Scss/pages/AccountPage.scss';
</style>
