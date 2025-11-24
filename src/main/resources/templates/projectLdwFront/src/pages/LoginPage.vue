<template>
  <section class="login">
    <div class="wrapper">
      <!-- Cabeçalho -->
      <div class="form-header" :class="{ 'largura-aumentada': modoRecuperacao }">
        <div class="titles">
          <div v-if="!modoCadastro && !modoRecuperacao" class="title-login">Login</div>
          <div v-else-if="modoCadastro" class="title-cadastro">Cadastro</div>
          <div v-else class="title-recuperacao">Recuperar Senha</div>
        </div>
      </div>

      <!-- Login -->
      <form
        v-if="!modoCadastro && !modoRecuperacao"
        class="form-container login-form"
        @submit.prevent="fazerLogin"
      >
        <div class="input-box" :class="{ filled: login.email }">
          <input type="email" class="input-field" v-model="login.email" required />
          <label class="label">Email</label>
          <i class="fi fi-rr-envelope" id="icon-login"></i>
        </div>
        <div class="input-box" :class="{ filled: login.password }">
          <input
            :type="mostrarSenhaLogin ? 'text' : 'password'"
            class="input-field"
            v-model="login.password"
            required
          />
          <label class="label">Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>
        <div class="form-cols">
          <div class="col-1">
            <input type="checkbox" id="mostrar-senha-login" v-model="mostrarSenhaLogin" />
            <label for="mostrar-senha-login">
              {{ mostrarSenhaLogin ? 'Ocultar Senha' : 'Mostrar Senha' }}
            </label>
          </div>
          <div class="col-2">
            <a href="#" @click.prevent="trocarFormulario('recuperacao')">Esqueceu a senha?</a>
          </div>
        </div>
        <div class="input-box">
          <button type="submit" class="btn-submit">
            Entrar <i class="bx bx-log-in"></i>
          </button>
        </div>
        <div class="swith-form">
          <span>Não tem uma conta?
            <a href="#" @click.prevent="trocarFormulario('cadastro')">Cadastre-se</a>
          </span>
        </div>
      </form>

      <!-- Cadastro -->
      <form
        v-else-if="modoCadastro"
        class="form-container cadastro-form"
        @submit.prevent="criarUsuario"
      >
        <div class="input-box" :class="{ filled: cadastro.nome }">
          <input type="text" class="input-field" v-model="cadastro.nome" required />
          <label class="label">Nome</label>
          <i class="fi fi-rs-user" id="icon-login"></i>
        </div>
        <div class="input-box" :class="{ filled: cadastro.email }">
          <input type="email" class="input-field" v-model="cadastro.email" required />
          <label class="label">Email</label>
          <i class="fi fi-rr-envelope" id="icon-login"></i>
        </div>
        <div class="input-box" :class="{ filled: cadastro.senha }">
          <input
            :type="mostrarSenhaCadastro ? 'text' : 'password'"
            class="input-field"
            v-model="cadastro.senha"
            required
          />
          <label class="label">Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>
        <div class="input-box" :class="{ filled: cadastro.confirmarSenha }">
          <input
            :type="mostrarSenhaCadastro ? 'text' : 'password'"
            class="input-field"
            v-model="cadastro.confirmarSenha"
            required
          />
          <label class="label" id="label-confirmar-senha">Confirmar Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>
        <div class="form-cols">
          <div class="col-1">
            <input type="checkbox" id="mostrar-senha-cadastro" v-model="mostrarSenhaCadastro" />
            <label for="mostrar-senha-cadastro">
              {{ mostrarSenhaCadastro ? 'Ocultar Senhas' : 'Mostrar Senhas' }}
            </label>
          </div>
        </div>
        <div class="input-box">
          <button type="submit" class="btn-submit">
            Cadastrar <i id="icon-cadastro" class="fi fi-ss-paw-claws"></i>
          </button>
        </div>
        <div class="swith-form">
          <span>Já tem uma conta?
            <a href="#" @click.prevent="trocarFormulario('login')">Faça o Login</a>
          </span>
        </div>
      </form>

      <!-- Recuperação de senha - ETAPA 1: solicitar e-mail -->
      <form
        v-else-if="modoRecuperacao && etapaRecuperacao === 1"
        class="form-container recuperacao-form"
        @submit.prevent="solicitarResetSenha"
      >
        <div class="input-box" :class="{ filled: reset.email }">
          <input type="email" class="input-field" v-model="reset.email" required />
          <label class="label">Email cadastrado</label>
          <i class="fi fi-rr-envelope" id="icon-login"></i>
        </div>

        <div class="input-box">
          <button type="submit" class="btn-submit">
            Enviar link de redefinição <i class="bx bx-mail-send"></i>
          </button>
        </div>

        <div class="swith-form">
          <span>Lembrou sua senha?
            <a href="#" @click.prevent="trocarFormulario('login')">Voltar ao Login</a>
          </span>
        </div>
      </form>

      <!-- Recuperação de senha - ETAPA 2: informar token + nova senha -->
      <form v-else class="form-container recuperacao-form" @submit.prevent="redefinirSenha">
        <div class="input-box" :class="{ filled: reset.token }">
          <input type="text" class="input-field" v-model="reset.token" required />
          <label class="label">Token recebido por e-mail</label>
          <i class="fi fi-rr-key" id="icon-login"></i>
        </div>

        <div class="input-box" :class="{ filled: reset.novaSenha }">
          <input
            :type="mostrarSenhaRecuperacao ? 'text' : 'password'"
            class="input-field"
            v-model="reset.novaSenha"
            required
          />
          <label class="label" id="label-nova-senha">Nova Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>

        <div class="input-box" :class="{ filled: reset.confirmarNovaSenha }">
          <input
            :type="mostrarSenhaRecuperacao ? 'text' : 'password'"
            class="input-field"
            v-model="reset.confirmarNovaSenha"
            required
          />
          <label class="label" id="label-confirmar-nova-senha">Confirmar Nova Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>

        <div class="form-cols">
          <div class="col-1">
            <input
              type="checkbox"
              id="mostrar-senha-recuperacao"
              v-model="mostrarSenhaRecuperacao"
            />
            <label for="mostrar-senha-recuperacao">
              {{ mostrarSenhaRecuperacao ? 'Ocultar Senhas' : 'Mostrar Senhas' }}
            </label>
          </div>
        </div>

        <div class="input-box">
          <button type="submit" class="btn-submit">
            Redefinir Senha <i class="bx bx-reset"></i>
          </button>
        </div>

        <div class="swith-form">
          <span>Lembrou sua senha?
            <a href="#" @click.prevent="trocarFormulario('login')">Voltar ao Login</a>
          </span>
        </div>
      </form>
    </div>

    <!-- pop-up fora da wrapper pra cobrir a tela toda -->
    <div v-if="showPopup" class="login-popup-overlay" @click="showPopup = false">
      <div class="login-popup-card" :class="popupType" @click.stop>
        <div class="login-popup-icon-wrap">
          <span class="login-popup-icon" v-if="popupType === 'error'">!</span>
          <span class="login-popup-icon" v-else>✅</span>
        </div>
        <h3 class="login-popup-title">
          {{ popupTitle }}
        </h3>
        <p class="login-popup-text">
          {{ popupMessage }}
        </p>
        <button
          v-if="popupType === 'success'"
          type="button"
          class="login-popup-button"
          @click="irParaLogin"
        >
          Ir para o Login
        </button>
        <button
          v-else
          type="button"
          class="login-popup-button"
          @click="showPopup = false"
        >
          Tentar novamente
        </button>
      </div>
    </div>
  </section>
</template>

<script>
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL_LOG;
const API_URL_CLI = import.meta.env.VITE_API_URL_CLI;

export default {
  data() {
    return {
      modoCadastro: false,
      modoRecuperacao: false,
      etapaRecuperacao: 1, // 1 = pedir email, 2 = token + nova senha

      reset: { email: '', token: '', novaSenha: '', confirmarNovaSenha: '' },

      login: { email: '', password: '' },
      cadastro: { nome: '', email: '', senha: '', confirmarSenha: '', permissao: 'CLIENT' },

      mostrarSenhaLogin: false,
      mostrarSenhaCadastro: false,
      mostrarSenhaRecuperacao: false,

      erroLogin: '',
      erroCadastro: '',
      erroRecuperacao: '',
      sucessoCriacao: '',
      sucessoRecuperacao: '',
      showPopup: false,
      popupTitle: '',
      popupMessage: '',
      popupType: 'info'
    };
  },

  mounted() {
    // Se vier um ?resetToken=XYZ na URL, entra direto na etapa 2 da recuperação
    const resetToken = this.$route.query.resetToken;
    if (resetToken) {
      this.modoCadastro = false;
      this.modoRecuperacao = true;
      this.etapaRecuperacao = 2;
      this.reset.token = String(resetToken);
    }
  },

  methods: {
    async fazerLogin() {
      this.erroLogin = '';
      try {
        const response = await axios.post(API_URL, this.login);
        const token = response.data.token;

        // salva token e e-mail
        localStorage.setItem('jwtToken', token);
        localStorage.setItem('usuarioEmail', this.login.email);

        // tenta obter o nome real: 1) body {name}, 2) claims do JWT
        let nome =
          typeof data.name === 'string' && data.name.trim() ? data.name.trim() : '';

        if (!nome) {
          const fromJwt = this.extractNameFromJwt(token);
          if (fromJwt) nome = fromJwt;
        }

        if (nome) {
          localStorage.setItem('usuarioNome', nome);
        } else {
          localStorage.removeItem('usuarioNome');
        }

        this.$router.push('/');
      } catch (error) {
        this.abrirPopup(
          'Erro de login',
          error.response?.data?.message ||
            error.response?.data?.error ||
            'Usuário ou senha inválidos. Verifique e tente novamente.',
          'error'
        );
      }
    },

    criarUsuario() {
      if (this.cadastro.senha !== this.cadastro.confirmarSenha) {
        this.abrirPopup('Senhas diferentes', 'As senhas informadas não coincidem.', 'error');
        return;
      }

      axios.post(API_URL_CLI, {
        name: this.cadastro.nome,
        email: this.cadastro.email,
        password: this.cadastro.senha,
		role: this.cadastro.permissao
      })
      .then(() => {
        this.sucessoCriacao = 'Usuário criado com sucesso!';
        this.erroCadastro = '';
        this.resetCadastro();
        setTimeout(() => {
          this.modoCadastro = false;
          this.sucessoCriacao = '';
        });
    },

    // ETAPA 1 - envia o pedido de reset (/clients/request)
    async solicitarResetSenha() {
      this.erroRecuperacao = '';
      try {
        await axios.post('http://localhost:8081/clients/request', {
          email: this.reset.email
        });

        this.abrirPopup(
          'Verifique seu e-mail',
          'Se o e-mail informado estiver cadastrado, você receberá um link para redefinir sua senha.',
          'info'
        );

        // avança para o segundo formulário (token + nova senha)
        this.etapaRecuperacao = 2;
      } catch (error) {
        this.abrirPopup(
          'Erro ao solicitar redefinição',
          error.response?.data?.message ||
            error.response?.data ||
            'Não foi possível enviar o pedido de redefinição. Tente novamente.',
          'error'
        );
      }
    },

    // ETAPA 2 - redefine a senha (/clients/reset)
    async redefinirSenha() {
      if (this.reset.novaSenha !== this.reset.confirmarNovaSenha) {
        this.abrirPopup(
          'Senhas diferentes',
          'As senhas informadas não coincidem. Tente novamente.',
          'error'
        );
        this.sucessoRecuperacao = '';
        return;
      }

      axios.post(API_URL_PASS, {
        email: this.login.email,
        novaSenha: this.cadastro.senha
      })
      .then(() => {
        this.sucessoRecuperacao = 'Senha redefinida com sucesso.';
        this.erroRecuperacao = '';
        this.resetRecuperacao();
        this.modoRecuperacao = false;
      } catch (error) {
        this.abrirPopup(
          'Erro ao redefinir senha',
          error.response?.data?.message ||
            error.response?.data ||
            'Não foi possível redefinir a senha. Verifique o token e tente novamente.',
          'error'
        );
        this.sucessoRecuperacao = '';
      }
    },

    trocarFormulario(modo) {
      this.modoCadastro = modo === 'cadastro';
      this.modoRecuperacao = modo === 'recuperacao';
      this.etapaRecuperacao = 1;

      this.erroCadastro = '';
      this.erroLogin = '';
      this.erroRecuperacao = '';
      this.sucessoCriacao = '';
      this.sucessoRecuperacao = '';

      this.login = { email: '', password: '' };
      this.resetCadastro();
      this.resetRecuperacao();
      this.resetSenhasVisiveis();
    },

    resetCadastro() {
      this.cadastro = {
        nome: '',
        email: '',
        senha: '',
        confirmarSenha: '',
        permissao: 'CLIENT'
      };
    },

    resetRecuperacao() {
      this.reset = { email: '', token: '', novaSenha: '', confirmarNovaSenha: '' };
      this.mostrarSenhaRecuperacao = false;
      this.etapaRecuperacao = 1;
    },

    resetSenhasVisiveis() {
      this.mostrarSenhaLogin = false;
      this.mostrarSenhaCadastro = false;
      this.mostrarSenhaRecuperacao = false;
    },

    abrirPopup(titulo, msg, tipo = 'info') {
      this.popupTitle = titulo;
      this.popupMessage = msg;
      this.popupType = tipo;
      this.showPopup = true;
    },

    irParaLogin() {
      this.showPopup = false;
      this.modoCadastro = false;
      this.modoRecuperacao = false;
      this.etapaRecuperacao = 1;
      this.resetRecuperacao();
    },

    // --------- Helpers JWT ---------

    extractNameFromJwt(token) {
      if (!token || token.split('.').length < 2) return null;
      try {
        const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
        const json = decodeURIComponent(
          atob(base64)
            .split('')
            .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
            .join('')
        );
        const payload = JSON.parse(json);

        const candidates = [
          payload.name,
          payload.nome,
          payload.fullName,
          payload.full_name,
          payload.given_name && payload.family_name
            ? `${payload.given_name} ${payload.family_name}`
            : null
        ].filter(Boolean);

        const full = candidates.length ? String(candidates[0]).trim() : '';
        if (!full) return null;

        return this.pickFirstAndSecond(full);
      } catch {
        return null;
      }
    },

    pickFirstAndSecond(full) {
      if (!full) return '';
      const tokens = full.replace(/\s+/g, ' ').trim().split(' ');
      if (!tokens.length) return '';
      const connectors = new Set(['da', 'de', 'do', 'das', 'dos', 'e']);

      const normalized = tokens.map((t, i) => {
        const lower = t.toLowerCase();
        return connectors.has(lower) && i !== 0 ? lower : this.titleCase(lower);
      });

      const first = normalized[0] || '';
      let second = '';
      for (let i = 1; i < normalized.length; i++) {
        if (!connectors.has(normalized[i].toLowerCase())) {
          second = normalized[i];
          break;
        }
      }
      if (!second && normalized[1]) second = normalized[1];

      return [first, second].filter(Boolean).slice(0, 2).join(' ');
    },

    titleCase(str) {
      if (!str) return '';
      return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
    }
  }
};
</script>

<style scoped>
@import '../assets/Scss/pages/login.scss';
</style>
