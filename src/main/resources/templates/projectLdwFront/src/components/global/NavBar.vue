<template>
  <nav class="navbar" role="navigation" aria-label="Main navigation" ref="navbarRoot">
    <div class="navbar-left">
      <router-link to="/" class="brand" @click="closeMenu">
        <img src="../../assets/base/Logo.png" alt="Kazu Tatoo Logo" class="logo-image" />
      </router-link>
      <span class="logo-text">Kazu Tatoo</span>
    </div>

    <!-- hamburger (mobile) -->
    <button
      class="hamburger"
      @click="toggleMenu"
      :aria-expanded="isMenuOpen.toString()"
      aria-label="Abrir menu"
      ref="hamburgerBtn"
    >
      <span></span>
      <span></span>
      <span></span>
    </button>

    <!-- painel direito (desktop = apenas ícones; mobile = painel deslizante com legendas) -->
    <div class="navbar-right" :class="{ open: isMenuOpen }" ref="panel">
      <ul class="nav-links" role="menubar">
        <li role="none">
          <router-link to="/" @click="closeMenu" role="menuitem">
            <i class="fi fi-sr-home" aria-hidden="true"></i>
            <span class="link-label">Início</span>
          </router-link>
        </li>

        <li v-if="isLoggedIn" role="none">
          <router-link to="/budget" @click="closeMenu" role="menuitem">
            <i class="fi fi-ss-checklist-task-budget" aria-hidden="true"></i>
            <span class="link-label">Orçamento</span>
          </router-link>
        </li>

        <li role="none">
          <router-link to="/chat" @click="closeMenu" role="menuitem">
            <i class="fi fi-sr-messages" aria-hidden="true"></i>
            <span class="link-label">Mensagens</span>
          </router-link>
        </li>

        <li v-if="!isloggedComputed" role="none">
          <router-link to="/login" @click="closeMenu" role="menuitem">
            <i class="fi fi-ss-user-add" aria-hidden="true"></i>
            <span class="link-label">Entrar</span>
          </router-link>
        </li>

        <li v-if="isLoggedIn" role="none">
          <router-link to="/account" @click="closeMenu" role="menuitem">
            <i class="fi fi-ss-user" aria-hidden="true"></i>
            <span class="link-label">Conta</span>
          </router-link>
        </li>
      </ul>
    </div>

    <!-- backdrop (aparece só no mobile quando o menu está aberto) -->
    <div
      v-if="isMenuOpen"
      class="mobile-backdrop"
      @click="closeMenu"
      aria-hidden="true"
      ref="backdrop"
    ></div>
  </nav>
</template>

<script>
export default {
  name: "Navbar",
  data() {
    return {
      isLoggedIn: !!localStorage.getItem("jwtToken"),
      isMenuOpen: false,
    };
  },

  computed: {
    // algumas builds minificam/alteram localStorage; keep a computed alias if quiser
    isloggedComputed() {
      return this.isLoggedIn;
    }
  },

  created() {
    this.$watch(
      () => this.$route.fullPath,
      () => {
        this.updateAuthState();
        this.closeMenu();
      }
    );

    window.addEventListener("storage", this.onStorage);
  },

  mounted() {
    // fecha ao apertar Esc
    document.addEventListener("keydown", this.onKeyDown);
    // fecha se clicar fora do painel (fallback se backdrop não capturar)
    document.addEventListener("click", this.onDocumentClick, true);
  },

  beforeUnmount() {
    window.removeEventListener("storage", this.onStorage);
    document.removeEventListener("keydown", this.onKeyDown);
    document.removeEventListener("click", this.onDocumentClick, true);
  },

  methods: {
    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
      // quando abrir, move foco para o painel para acessibilidade
      this.$nextTick(() => {
        if (this.isMenuOpen && this.$refs.panel) {
          this.$refs.panel.setAttribute("tabindex", "-1");
          this.$refs.panel.focus();
        }
      });
    },

    closeMenu() {
      this.isMenuOpen = false;
      // devolve foco para o botão hamburger
      this.$nextTick(() => {
        if (this.$refs.hamburgerBtn) this.$refs.hamburgerBtn.focus();
      });
    },

    updateAuthState() {
      const raw = localStorage.getItem("jwtToken");
      const token =
        raw && String(raw).trim() && raw.toLowerCase() !== "null" && raw.toLowerCase() !== "undefined"
          ? raw
          : null;
      this.isLoggedIn = !!token;
    },

    onStorage(event) {
      if (event.key === "jwtToken") {
        this.updateAuthState();
      }
    },

    onKeyDown(e) {
      if (e.key === "Escape" && this.isMenuOpen) {
        this.closeMenu();
      }
    },

    onDocumentClick(e) {
      // Se menu fechado, ignora
      if (!this.isMenuOpen) return;

      const panel = this.$refs.panel;
      const hamburger = this.$refs.hamburgerBtn;
      const backdrop = this.$refs.backdrop;

      // Se existe backdrop e o alvo é dentro dele -> já fecha via @click no backdrop.
      // Caso o clique seja fora do painel e fora do hambuger, fechamos como fallback.
      const clickedInsidePanel = panel && panel.contains(e.target);
      const clickedHamburger = hamburger && hamburger.contains(e.target);
      const clickedBackdrop = backdrop && backdrop.contains(e.target);

      if (!clickedInsidePanel && !clickedHamburger && !clickedBackdrop) {
        this.closeMenu();
      }
    },
  },
};
</script>

<style scoped>
@import "../../assets/Scss/global/Navbar.scss";
</style>
