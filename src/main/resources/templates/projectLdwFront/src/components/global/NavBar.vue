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

    <!-- painel direito -->
    <div class="navbar-right" :class="{ open: isMenuOpen }" ref="panel">
      <ul class="nav-links" role="menubar">

        <!-- Início -->
        <li role="none">
          <router-link to="/" @click="closeMenu" role="menuitem">
            <i class="fi fi-sr-home" aria-hidden="true"></i>
            <span class="link-label">Início</span>
            <span class="icon-tooltip">Página inicial</span>
          </router-link>
        </li>

        <!-- rota geral para qualquer usuário logado -->
        <li v-if="isLoggedIn" role="none">
          <router-link to="/budget" @click="closeMenu" role="menuitem">
            <i class="fi fi-ss-checklist-task-budget" aria-hidden="true"></i>
            <span class="link-label">Orçamento</span>
            <span class="icon-tooltip">Novo orçamento</span>
          </router-link>
        </li>

        <!-- rota apenas para usuário comum (cliente) -->
        <li v-if="isClient" role="none">
          <router-link to="/budget-table-cl" @click="closeMenu" role="menuitem">
            <i class="fi fi-ss-folder-check" aria-hidden="true"></i>
            <span class="link-label">Meus Orçamentos</span>
            <span class="icon-tooltip">Meus orçamentos</span>
          </router-link>
        </li>

        <!-- rota apenas para admin -->
        <li v-if="isAdmin" role="none">
          <router-link to="/budget-table" @click="closeMenu" role="menuitem">
            <i class="fi fi-ss-folder-check" aria-hidden="true"></i>
            <span class="link-label">Painel Admin</span>
            <span class="icon-tooltip">Área administrativa</span>
          </router-link>
        </li>

        <!-- mensagens (somente logado) -->
        <li v-if="isLoggedIn" role="none">
          <router-link to="/chat" @click="closeMenu" role="menuitem">
            <i class="fi fi-sr-messages" aria-hidden="true"></i>
            <span class="link-label">Mensagens</span>
            <span class="icon-tooltip">Chat</span>
          </router-link>
        </li>

        <!-- login quando não está logado -->
        <li v-if="!isLoggedIn" role="none">
          <router-link to="/login" @click="closeMenu" role="menuitem">
            <i class="fi fi-ss-user-add" aria-hidden="true"></i>
            <span class="link-label">Entrar</span>
            <span class="icon-tooltip">Login / Cadastro</span>
          </router-link>
        </li>

        <!-- conta quando logado -->
        <li v-if="isLoggedIn" role="none">
          <router-link to="/account" @click="closeMenu" role="menuitem">
            <i class="fi fi-ss-user" aria-hidden="true"></i>
            <span class="link-label">Conta</span>
            <span class="icon-tooltip">Minha conta</span>
          </router-link>
        </li>

      </ul>
    </div>

    <!-- backdrop mobile -->
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
function safeGetToken() {
  const raw = localStorage.getItem("jwtToken");
  if (!raw) return null;
  const trimmed = String(raw).trim();
  if (!trimmed || trimmed.toLowerCase() === "null" || trimmed.toLowerCase() === "undefined") return null;
  return trimmed;
}

function decodeJwt(token) {
  try {
    const payload = token.split(".")[1];
    const padded = payload.replace(/-/g, "+").replace(/_/g, "/");
    const decoded = decodeURIComponent(
      Array.prototype.map.call(atob(padded), function(c) {
        return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
      }).join("")
    );
    return JSON.parse(decoded);
  } catch (e) {
    try { return JSON.parse(atob(token.split(".")[1])); }
    catch (err) { console.warn("Erro ao decodificar JWT:", err); return null; }
  }
}

function extractRolesFromDecoded(decoded) {
  if (!decoded) return [];
  const roles = new Set();
  const pushCandidate = (r) => {
    if (!r && r !== 0) return;
    if (typeof r === "object") {
      if (Array.isArray(r)) r.forEach(pushCandidate);
      else if (r.authority) pushCandidate(r.authority);
      else if (r.role) pushCandidate(r.role);
      return;
    }
    if (typeof r === "string") {
      r.split(/[, ]+/).map(s => s.trim()).forEach(s => {
        if (!s) return;
        const normalized = s.replace(/^ROLE_/i, "").toLowerCase();
        roles.add(normalized);
      });
    }
  };
  const candidates = [decoded.role, decoded.roles, decoded.authorities, decoded.authority, decoded.userRole];
  candidates.forEach(pushCandidate);
  return Array.from(roles);
}

export default {
  name: "Navbar",
  data() {
    return {
      isLoggedIn: false,
      isMenuOpen: false,
      userRoles: [],
    };
  },
  computed: {
    isAdmin() { return this.userRoles.includes("admin"); },
    isClient() { return this.isLoggedIn && this.userRoles.includes("client"); }
  },
  created() {
    this.updateAuthState();
    this.$watch(() => this.$route.fullPath, () => { 
      this.updateAuthState(); 
      this.closeMenu(); 
    });
    window.addEventListener("storage", this.onStorage);
  },
  mounted() {
    document.addEventListener("keydown", this.onKeyDown);
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
      this.$nextTick(() => {
        if (this.isMenuOpen && this.$refs.panel) {
          this.$refs.panel.setAttribute("tabindex", "-1");
          this.$refs.panel.focus();
        }
      });
    },
    closeMenu() {
      this.isMenuOpen = false;
      this.$nextTick(() => { if (this.$refs.hamburgerBtn) this.$refs.hamburgerBtn.focus(); });
    },
    updateAuthState() {
      const token = safeGetToken();
      this.isLoggedIn = !!token;
      if (!token) { 
        this.userRoles = []; 
        return; 
      }
      const decoded = decodeJwt(token);
      const roles = extractRolesFromDecoded(decoded);
      this.userRoles = roles.length ? roles : decoded?.role ? [decoded.role.toLowerCase()] : [];
    },
    onStorage(event) { if (event.key === "jwtToken") this.updateAuthState(); },
    onKeyDown(e) { if (e.key === "Escape" && this.isMenuOpen) this.closeMenu(); },
    onDocumentClick(e) {
      if (!this.isMenuOpen) return;
      const panel = this.$refs.panel, hamburger = this.$refs.hamburgerBtn, backdrop = this.$refs.backdrop;
      if (!(panel && panel.contains(e.target)) && !(hamburger && hamburger.contains(e.target)) && !(backdrop && backdrop.contains(e.target))) this.closeMenu();
    }
  },
};
</script>

<style scoped>
@import "../../assets/Scss/global/Navbar.scss";
</style>
