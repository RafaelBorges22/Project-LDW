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

      <form v-if="!modoCadastro && !modoRecuperacao" class="form-container login-form" @submit.prevent="fazerLogin"
        autocomplete="on" novalidate>
        <div class="input-box" :class="{ filled: login.email }">
          <input id="login-email" name="username" type="email" class="input-field" v-model="login.email" required
            autocomplete="username" autocorrect="off" autocapitalize="off" spellcheck="false" />
          <label class="label">Email</label>
          <i class="fi fi-rr-envelope" id="icon-login"></i>
        </div>

        <div class="input-box" :class="{ filled: login.password }">
          <input id="login-password" name="current-password" :type="mostrarSenhaLogin ? 'text' : 'password'"
            class="input-field" v-model="login.password" required autocomplete="current-password" />
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

      <form v-else-if="modoCadastro" class="form-container cadastro-form" @submit.prevent="criarUsuario">
        <div class="input-box" :class="{ filled: cadastro.nome }">
          <input type="text" class="input-field" placeholder="Nome Completo" v-model="cadastro.nome" required />
          <label class="label">Nome</label>
          <i class="fi fi-rs-user" id="icon-login"></i>
        </div>

        <div class="input-box" :class="{ filled: cadastro.telefone }">
          <input id="phone" name="phone" type="tel" inputmode="tel" autocomplete="tel" class="input-field"
            v-model="cadastro.telefone" @input="formatarTelefone" maxlength="15" required placeholder="(11) 91234-5678"
            pattern="^\([0-9]{2}\) [0-9]{4,5}-[0-9]{4}$" title="Telefone no formato (11) 91234-5678" />
          <label class="label tel">Telefone</label>
          <i class="fi fi-rr-phone-call" id="icon-login"></i>
        </div>

        <div class="input-box" :class="{ filled: cadastro.endereco }">
          <input type="text" class="input-field" v-model="cadastro.endereco" required
            placeholder="Rua, Número, Bairro, Cidade, Estado" title="Endereço (Rua, Número, Bairro, Cidade, Estado)" />
          <label class="label">Endereço</label>
          <i class="fi fi-rr-map-pin" id="icon-login"></i>
        </div>

        <div class="input-box" :class="{ filled: cadastro.email }">
          <input id="email" name="email" type="email" class="input-field" placeholder="email@exemplo.com"
            v-model="cadastro.email" autocomplete="email" required />
          <label class="label">Email</label>
          <i class="fi fi-rr-envelope" id="icon-login"></i>
        </div>

        <div class="input-box campo-password" :class="{ filled: cadastro.senha }">
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" class="input-field" placeholder="Min. 8 caracteres"
            v-model="cadastro.senha" required />
          <label class="label">Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>

          <div class="pwd-compact" aria-hidden="false">
            <span class="pwd-chip" :class="{ ok: senhaCriteria.minLength }" title="Mínimo 8 caracteres">
              <i :class="senhaCriteria.minLength ? 'fi fi-rr-check' : 'fi fi-rr-circle'"></i>
              <small>8+</small>
            </span>

            <span class="pwd-chip" :class="{ ok: senhaCriteria.lower }" title="Possui letra minúscula">
              <i :class="senhaCriteria.lower ? 'fi fi-rr-check' : 'fi fi-rr-circle'"></i>
              <small>a</small>
            </span>

            <span class="pwd-chip" :class="{ ok: senhaCriteria.upper }" title="Possui letra maiúscula">
              <i :class="senhaCriteria.upper ? 'fi fi-rr-check' : 'fi fi-rr-circle'"></i>
              <small>A</small>
            </span>

            <span class="pwd-chip" :class="{ ok: senhaCriteria.number }" title="Possui número">
              <i :class="senhaCriteria.number ? 'fi fi-rr-check' : 'fi fi-rr-circle'"></i>
              <small>123</small>
            </span>

            <span class="pwd-chip" :class="{ ok: senhaCriteria.special }" title="Possui caractere especial">
              <i :class="senhaCriteria.special ? 'fi fi-rr-check' : 'fi fi-rr-circle'"></i>
              <small>#</small>
            </span>

            <span class="pwd-strength" :class="strengthClass">{{ strengthLabel }}</span>
          </div>
        </div>

        <div class="input-box" :class="{ filled: cadastro.confirmarSenha }">
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" class="input-field" placeholder="Confirme sua senha"
            v-model="cadastro.confirmarSenha" required />
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
      <form v-else-if="modoRecuperacao && etapaRecuperacao === 1" class="form-container recuperacao-form"
        @submit.prevent="solicitarResetSenha">
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
          <input :type="mostrarSenhaRecuperacao ? 'text' : 'password'" class="input-field" v-model="reset.novaSenha"
            required />
          <label class="label" id="label-nova-senha">Nova Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>

        <div class="input-box" :class="{ filled: reset.confirmarNovaSenha }">
          <input :type="mostrarSenhaRecuperacao ? 'text' : 'password'" class="input-field"
            v-model="reset.confirmarNovaSenha" required />
          <label class="label" id="label-confirmar-nova-senha">Confirmar Nova Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>

        <div class="form-cols">
          <div class="col-1">
            <input type="checkbox" id="mostrar-senha-recuperacao" v-model="mostrarSenhaRecuperacao" />
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
        <button v-if="popupType === 'success'" type="button" class="login-popup-button" @click="irParaLogin">
          Ir para o Login
        </button>
        <button v-else type="button" class="login-popup-button" @click="showPopup = false">
          Tentar novamente
        </button>
      </div>
    </div>
  </section>
</template>

<script>
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL_AUTH;
const API_URL_CLI = import.meta.env.VITE_API_URL_CLI;
const API_URL_REQ = import.meta.env.VITE_API_URL_REQ;
const API_URL_RES = import.meta.env.VITE_API_URL_RES;

export default {
  data() {
    return {
      modoCadastro: false,
      modoRecuperacao: false,
      etapaRecuperacao: 1, // 1 = pedir email, 2 = token + nova senha

      reset: { email: '', token: '', novaSenha: '', confirmarNovaSenha: '' },

      login: { email: '', password: '' },
      cadastro: {
        nome: '',
        telefone: '',
        endereco: '',
        email: '',
        senha: '',
        confirmarSenha: '',
        permissao: 'CLIENT'
      },

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
    const resetToken = this.$route.query.resetToken;
    if (resetToken) {
      this.modoCadastro = false;
      this.modoRecuperacao = true;
      this.etapaRecuperacao = 2;
      this.reset.token = String(resetToken);
    }
  },

  computed: {
    senhaCriteria() {
      const s = this.cadastro.senha || '';
      return {
        minLength: s.length >= 8,
        lower: /[a-z]/.test(s),
        upper: /[A-Z]/.test(s),
        number: /[0-9]/.test(s),
        special: /[!@#\$%\^&\*\(\)\-_\+=\[\]{};:'",.<>\/\\\?|`~]/.test(s)
      };
    },

    senhaScore() {
      const c = this.senhaCriteria;
      return Object.values(c).filter(Boolean).length;
    },

    senhaIsStrong() {
      return this.senhaScore === 5;
    },

    strengthLabel() {
      if (this.senhaScore <= 2) return 'Fraca';
      if (this.senhaScore === 3 || this.senhaScore === 4) return 'Média';
      return 'Forte';
    },

    strengthClass() {
      if (this.senhaScore <= 2) return 'weak';
      if (this.senhaScore === 3 || this.senhaScore === 4) return 'medium';
      return 'strong';
    }
  },

  methods: {
    async fazerLogin() {
      this.erroLogin = '';
      try {
        // 1) Login principal
        const { data } = await axios.post(API_URL, this.login);
        const token = data.token;

        // 2) Salva token e email
        localStorage.setItem('jwtToken', token);
        localStorage.setItem('usuarioEmail', this.login.email);

        // 3) nome (resposta ou JWT)
        let nome = typeof data.name === 'string' && data.name.trim() ? data.name.trim() : '';
        if (!nome) {
          const fromJwt = this.extractNameFromJwt(token);
          if (fromJwt) nome = fromJwt;
        }
        if (nome) localStorage.setItem('usuarioNome', nome);

        // 4) se o login retornou id, salva
        if (data.id) {
          localStorage.setItem('usuarioId', String(data.id));
        }

        // 5) Busca dados completos do usuário (id, phone, address) e garante salvar usuarioId
        try {
          const resUser = await axios.get(
            `${API_URL_CLI}/email/${encodeURIComponent(this.login.email)}`
          );

          const user = resUser.data;
          if (user) {
            if (user.id) localStorage.setItem('usuarioId', String(user.id));
            if (user.name && !nome) localStorage.setItem('usuarioNome', user.name);
            localStorage.setItem('usuarioTelefone', user.phone || '');
            localStorage.setItem('usuarioEndereco', user.address || '');
          }
        } catch (err) {
          console.warn('Não foi possível obter telefone/endereço do usuário:', err);
          // garantir chaves existentes (evita undefined)
          if (!localStorage.getItem('usuarioTelefone')) localStorage.setItem('usuarioTelefone', '');
          if (!localStorage.getItem('usuarioEndereco')) localStorage.setItem('usuarioEndereco', '');
        }

        // 6) Redireciona
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

    formatarTelefone() {
      let digits = (this.cadastro.telefone || '').replace(/\D/g, '').slice(0, 11);
      if (digits.length === 0) {
        this.cadastro.telefone = '';
        return;
      }
      if (digits.length <= 2) {
        this.cadastro.telefone = `(${digits}`;
        return;
      }
      if (digits.length <= 6) {
        this.cadastro.telefone = `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
        return;
      }
      if (digits.length <= 10) {
        this.cadastro.telefone = `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
        return;
      }
      this.cadastro.telefone = `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
    },

    criarUsuario() {
      if (this.cadastro.senha !== this.cadastro.confirmarSenha) {
        this.abrirPopup('Senhas diferentes', 'As senhas informadas não coincidem.', 'error');
        return;
      }

      if (!this.senhaIsStrong) {
        const faltam = [];
        const crit = this.senhaCriteria;
        if (!crit.minLength) faltam.push('mínimo 8 caracteres');
        if (!crit.lower) faltam.push('uma letra minúscula');
        if (!crit.upper) faltam.push('uma letra maiúscula');
        if (!crit.number) faltam.push('um número');
        if (!crit.special) faltam.push('um caractere especial');
        this.abrirPopup(
          'Senha fraca',
          `Sua senha precisa conter: ${faltam.join(', ')}.`,
          'error'
        );
        return;
      }

      const payload = {
        name: this.cadastro.nome,
        email: this.cadastro.email,
        password: this.cadastro.senha,
        address: this.cadastro.endereco || '',
        phone: (this.cadastro.telefone || '').replace(/\D/g, ''),
        role: this.cadastro.permissao
      };

      axios
        .post(API_URL_CLI, payload)
        .then(() => {
          this.abrirPopup(
            'Conta criada',
            'Sua conta foi criada com sucesso! Faça login para continuar.',
            'success'
          );
          this.erroCadastro = '';
          this.resetCadastro();
        })
        .catch(error => {
          if (
            error.response?.status === 400 &&
            typeof error.response.data === 'string' &&
            error.response.data.includes('Já existe um usuário')
          ) {
            this.abrirPopup(
              'E-mail já cadastrado',
              'Este e-mail já está cadastrado. Faça login ou use outro e-mail.',
              'error'
            );
          } else {
            this.abrirPopup(
              'Erro ao criar conta',
              error.response?.data?.message ||
              error.response?.data ||
              'Não foi possível criar o usuário. Tente novamente.',
              'error'
            );
          }
          this.sucessoCriacao = '';
        });
    },

    async solicitarResetSenha() {
      this.erroRecuperacao = '';
      try {
        await axios.post(API_URL_REQ, { email: this.reset.email });

        this.abrirPopup(
          'Verifique seu e-mail',
          'Se o e-mail informado estiver cadastrado, você receberá um link para redefinir sua senha.',
          'info'
        );

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

      try {
        await axios.post(API_URL_RES, {
          token: this.reset.token,
          newPassword: this.reset.novaSenha
        });

        this.abrirPopup(
          'Senha redefinida',
          'Sua senha foi redefinida com sucesso! Você já pode fazer login.',
          'success'
        );
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
        telefone: '',
        endereco: '',
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