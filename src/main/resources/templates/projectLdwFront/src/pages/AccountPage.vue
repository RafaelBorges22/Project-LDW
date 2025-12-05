<template>
  <div id="app" class="account-page-root">
    <Navbar />

    <main class="account-container">
      <section class="account-card">
        <div class="account-header">
          <div class="avatar" aria-hidden="true">
            <div class="avatar-icon">
              <i class="fi fi-ss-user"></i>
            </div>
          </div>

          <div class="header-info">
            <h2 class="title">Minha Conta</h2>
            <p class="subtitle">{{ isEditing ? 'Editar informações do perfil' : 'Informações do perfil' }}</p>
          </div>
        </div>

        <!-- ===== VIEW MODE ===== -->
        <div v-if="!isEditing" class="info-grid" role="list">
          <div class="info-item" role="listitem">
            <label>Nome</label>
            <p class="info-value">{{ userNameDisplay }}</p>
          </div>

          <div class="info-item" role="listitem">
            <label>Email</label>
            <p class="info-value">{{ userEmailDisplay }}</p>
          </div>

          <div class="info-item" role="listitem">
            <label>Telefone</label>
            <p class="info-value">
              <i class="fi fi-br-phone-call contact-icon"></i>
              <span>{{ userPhoneDisplay }}</span>
            </p>
          </div>

          <div class="info-item" role="listitem">
            <label>Endereço</label>
            <p class="info-value">
              <i class="fi fi-rr-marker contact-icon" aria-hidden="true"></i>
              <span>{{ userAddressDisplay }}</span>
            </p>
          </div>
        </div>

        <!-- ===== EDIT MODE ===== -->
        <div v-else class="edit-grid">
          <div class="edit-item">
            <label>Nome</label>
            <input type="text" v-model="userNameRaw" />
          </div>

          <div class="edit-item">
            <label>Email</label>
            <input type="email" v-model="userEmailRaw" disabled />
          </div>

          <div class="edit-item">
            <label>Telefone</label>
            <!-- máscara em tempo real -->
            <input
              type="text"
              :value="userPhoneRaw"
              @input="onPhoneInput($event)"
              inputmode="tel"
              placeholder="(11) 91234-5678"
            />
          </div>

          <div class="edit-item">
            <label>Endereço</label>
            <input type="text" v-model="userAddressRaw" placeholder="Rua, Número, Bairro, Cidade, Estado"/>
          </div>
        </div>

        <div class="account-actions">
          <button class="btn secondary" @click="isEditing ? cancelEdit() : goBack()">
            ← {{ isEditing ? 'Cancelar' : 'Voltar' }}
          </button>

          <div class="right-actions">
            <button v-if="isLoggedIn && !isEditing" class="btn outline" @click="startEdit" title="Editar perfil">
              Editar <i class="fi fi-sr-pencil"></i>
            </button>

            <button v-if="isLoggedIn && isEditing" class="btn outline" @click="saveEdit" title="Salvar alterações">
              Salvar <i class="fi fi-sr-check"></i>
            </button>

            <button v-if="isLoggedIn" class="btn danger" @click="showLogoutConfirm = true">
              Sair <i class="fi fi-ss-user-logout" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </section>
    </main>

    <!-- Logout popup (sem alterações) -->
    <div v-if="showLogoutConfirm" class="logout-popup-overlay" @click="cancelLogout" aria-modal="true" role="dialog">
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
          <button type="button" class="logout-popup-btn outline" @click="cancelLogout">
            Cancelar
          </button>
        </div>
      </div>
    </div>

    <!-- MINI TOAST -->
    <div
      v-if="toastVisible"
      :class="['message-toast', toastType === 'success' ? 'success' : 'error']"
      role="status"
      aria-live="polite"
    >
      <div class="message-icon" aria-hidden="true">
        <i v-if="toastType === 'success'" class="fi fi-rr-check"></i>
        <i v-else class="fi fi-rr-exclamation"></i>
      </div>
      <div class="message-text">{{ toastMessage }}</div>
    </div>

    <Footer />
  </div>
</template>

<script>
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';
import axios from 'axios';

export default {
  name: "AccountPage",
  components: { Navbar, Footer },

  data() {
    return {
      userNameRaw: null,
      userEmailRaw: null,
      userPhoneRaw: null,
      userAddressRaw: null,

      showLogoutConfirm: false,
      isLoggedIn: !!localStorage.getItem('jwtToken'),
      isEditing: false,

      toastVisible: false,
      toastMessage: '',
      toastType: 'success',
      _toastTimer: null
    };
  },

  computed: {
    userNameDisplay() { return this.userNameRaw || 'Não informado'; },
    userEmailDisplay() { return this.userEmailRaw || 'Não informado'; },
    userPhoneDisplay() { return this.userPhoneRaw || 'Não informado'; },
    userAddressDisplay() { return this.userAddressRaw || 'Não informado'; }
  },

  created() {
    this.loadFromLocalStorage();
    window.addEventListener('storage', this.onStorage);
  },

  beforeUnmount() {
    window.removeEventListener('storage', this.onStorage);
    if (this._toastTimer) clearTimeout(this._toastTimer);
  },

  methods: {
    startEdit() { this.isEditing = true; },
    cancelEdit() { this.loadFromLocalStorage(); this.isEditing = false; },

    onPhoneInput(event) {
      const raw = event.target.value || '';
      this.userPhoneRaw = this.formatPhoneForInput(raw);
    },

    formatPhoneForInput(value) {
      // aceita qualquer string, retorna formatada
      const digits = String(value || '').replace(/\D/g, '').slice(0, 11);
      if (!digits) return '';
      if (digits.length <= 2) {
        return `(${digits}`;
      }
      if (digits.length <= 6) {
        return `(${digits.slice(0,2)}) ${digits.slice(2)}`;
      }
      if (digits.length <= 10) {
        // (xx) xxxx-xxxx
        return `(${digits.slice(0,2)}) ${digits.slice(2,6)}-${digits.slice(6)}`;
      }
      // 11 dígitos -> (xx) xxxxx-xxxx
      return `(${digits.slice(0,2)}) ${digits.slice(2,7)}-${digits.slice(7)}`;
    },

    phoneDigits(value) {
      if (!value) return '';
      return String(value).replace(/\D/g, '');
    },

    async saveEdit() {
      try {
        const name = (this.userNameRaw || '').toString().trim();
        const address = (this.userAddressRaw || '').toString().trim();
        const phoneDigits = this.phoneDigits(this.userPhoneRaw || '');

        if (!name) {
          this.showToast('error', 'Preencha o campo Nome.');
          return;
        }

        if (!phoneDigits || (phoneDigits.length < 10)) {
          this.showToast('error', 'Informe um telefone válido (mínimo 10 dígitos).');
          return;
        }

        if (!address) {
          this.showToast('error', 'Preencha o campo Endereço.');
          return;
        }

        let id = localStorage.getItem('usuarioId');

        if (!id) {
          const email = localStorage.getItem('usuarioEmail');
          if (email) {
            try {
              const res = await axios.get(`http://localhost:8081/clients/email/${encodeURIComponent(email)}`);
              const user = res.data;
              if (user && (user.id || user._id)) {
                id = user.id || user._id;
                localStorage.setItem('usuarioId', String(id));
              }
            } catch (err) {
              console.warn('Não foi possível recuperar id por email:', err);
            }
          }
        }

        if (!id) {
          this.showToast('error', 'ID do usuário não encontrado. Faça login novamente.');
          return;
        }

        // monta payload com phone em dígitos
        const token = localStorage.getItem('jwtToken');
        const headers = token ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };

        const payload = {
          name,
          address,
          phone: phoneDigits
        };

        await axios.put(`http://localhost:8081/clients/${encodeURIComponent(id)}`, payload, { headers });

        // atualiza localStorage (salva telefone formatado para exibição)
        localStorage.setItem('usuarioNome', name);
        localStorage.setItem('usuarioTelefone', this.formatPhoneForInput(phoneDigits));
        localStorage.setItem('usuarioEndereco', address);

        // recarrega valores no UI a partir do localStorage
        this.loadFromLocalStorage();

        this.isEditing = false;
        this.showToast('success', 'Perfil atualizado com sucesso!');
      } catch (err) {
        console.error('Erro ao salvar perfil:', err);
        const msg = err.response?.data?.message || err.response?.data || err.message || String(err);
        this.showToast('error', 'Não foi possível salvar: ' + (typeof msg === 'string' ? msg : JSON.stringify(msg)));
      }
    },

    showToast(type = 'success', message = '', duration = 2200) {
      this.toastType = type === 'error' ? 'error' : 'success';
      this.toastMessage = message || (type === 'error' ? 'Ocorreu um erro' : 'Operação concluída');
      this.toastVisible = true;
      if (this._toastTimer) clearTimeout(this._toastTimer);
      this._toastTimer = setTimeout(() => {
        this.toastVisible = false;
        this._toastTimer = null;
      }, duration);
    },

    loadFromLocalStorage() {
      this.userNameRaw = localStorage.getItem('usuarioNome') || '';
      this.userEmailRaw = localStorage.getItem('usuarioEmail') || '';
      const phoneRaw = localStorage.getItem('usuarioTelefone') || '';
      if (phoneRaw && /^\d{8,11}$/.test(phoneRaw)) {
        this.userPhoneRaw = this.formatPhoneForInput(phoneRaw);
      } else {
        this.userPhoneRaw = phoneRaw;
      }
      this.userAddressRaw = localStorage.getItem('usuarioEndereco') || '';
    },

    onStorage(event) {
      if (['jwtToken', 'usuarioNome', 'usuarioEmail', 'usuarioTelefone', 'usuarioEndereco'].includes(event.key)) {
        this.isLoggedIn = !!localStorage.getItem('jwtToken');
        this.loadFromLocalStorage();
      }
    },

    goBack() { this.$router.push("/"); },
    logoutNow() {
      ['jwtToken', 'usuarioEmail', 'usuarioId', 'usuarioNome', 'usuarioTelefone', 'usuarioEndereco', 'permissao'].forEach(k => localStorage.removeItem(k));
      this.isLoggedIn = false;
      this.showLogoutConfirm = false;
      this.$router.push('/');
    },
    cancelLogout() { this.showLogoutConfirm = false; }
  }
};
</script>

<style scoped>
@import '../assets/Scss/pages/AccountPage.scss';
</style>