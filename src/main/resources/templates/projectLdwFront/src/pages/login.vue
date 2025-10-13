<template>
  <section class="login">
    <div class="wrapper">
      <!-- Cabeçalho -->
      <div class="form-header" :class="{'largura-aumentada': modoRecuperacao}">
        <div class="titles">
          <div v-if="!modoCadastro && !modoRecuperacao" class="title-login">Login</div>
          <div v-else-if="modoCadastro" class="title-cadastro">Cadastro</div>
          <div v-else class="title-recuperacao">Recuperar Senha</div>
        </div>
      </div>

      <!-- Login -->
      <form v-if="!modoCadastro && !modoRecuperacao" class="form-container login-form" @submit.prevent="fazerLogin">
        <div class="input-box" :class="{ filled: login.email }">
          <input type="email" class="input-field" v-model="login.email" required />
          <label class="label">Email</label>
          <i class="fi fi-rr-envelope" id="icon-login"></i>
        </div>
        <div class="input-box" :class="{ filled: login.password }">
          <input :type="mostrarSenhaLogin ? 'text' : 'password'" class="input-field" v-model="login.password" required />
          <label class="label">Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>
        <div class="form-cols">
          <div class="col-1">
            <input type="checkbox" id="mostrar-senha-login" v-model="mostrarSenhaLogin" />
            <label for="mostrar-senha-login">{{ mostrarSenhaLogin ? 'Ocultar Senha' : 'Mostrar Senha' }}</label>
          </div>
          <div class="col-2">
            <a href="#" @click.prevent="trocarFormulario('recuperacao')">Esqueceu a senha?</a>
          </div>
        </div>
        <div class="input-box">
          <button type="submit" class="btn-submit">Entrar <i class='bx bx-log-in'></i></button>
        </div>
        <p id="msg-erro" v-if="erroLogin">{{ erroLogin }}</p>
        <div class="swith-form">
          <span>Não tem uma conta? <a href="#" @click.prevent="trocarFormulario('cadastro')">Cadastre-se</a></span>
        </div>
      </form>

      <!-- Cadastro -->
      <form v-else-if="modoCadastro" class="form-container cadastro-form" @submit.prevent="criarUsuario">
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
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" class="input-field" v-model="cadastro.senha" required />
          <label class="label">Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>
        <div class="input-box" :class="{ filled: cadastro.confirmarSenha }">
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" class="input-field" v-model="cadastro.confirmarSenha" required />
          <label class="label" id="label-confirmar-senha">Confirmar Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>
        <div class="form-cols">
          <div class="col-1">
            <input type="checkbox" id="mostrar-senha-cadastro" v-model="mostrarSenhaCadastro" />
            <label for="mostrar-senha-cadastro">{{ mostrarSenhaCadastro ? 'Ocultar Senhas' : 'Mostrar Senhas' }}</label>
          </div>
        </div>
        <div class="input-box">
          <button type="submit" class="btn-submit">Cadastrar <i id="icon-cadastro" class="fi fi-ss-paw-claws"></i></button>
        </div>
        <p id="msg-erro" v-if="erroCadastro">{{ erroCadastro }}</p>
        <p id="msg-sucesso" v-if="sucessoCriacao">{{ sucessoCriacao }}</p>
        <div class="swith-form">
          <span>Já tem uma conta? <a href="#" @click.prevent="trocarFormulario('login')">Faça o Login</a></span>
        </div>
      </form>

      <!-- Recuperação de senha -->
      <form v-else class="form-container recuperacao-form" @submit.prevent="recuperarSenha">
        <div class="input-box" :class="{ filled: login.email }">
          <input type="email" class="input-field" v-model="login.email" required />
          <label class="label">Email</label>
          <i class="fi fi-rr-envelope" id="icon-login"></i>
        </div>
        <div class="input-box" :class="{ filled: cadastro.senha }">
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" class="input-field" v-model="cadastro.senha" required />
          <label class="label" id="label-nova-senha">Nova Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>
        <div class="input-box" :class="{ filled: cadastro.confirmarSenha }">
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" class="input-field" v-model="cadastro.confirmarSenha" required />
          <label class="label" id="label-confirmar-nova-senha">Confirmar Nova Senha</label>
          <i class="fi fi-rr-lock" id="icon-login"></i>
        </div>
        <div class="form-cols">
          <div class="col-1">
            <input type="checkbox" id="mostrar-senha-recuperacao" v-model="mostrarSenhaCadastro" />
            <label for="mostrar-senha-recuperacao">{{ mostrarSenhaCadastro ? 'Ocultar Senhas' : 'Mostrar Senhas' }}</label>
          </div>
        </div>
        <div class="input-box">
          <button type="submit" class="btn-submit">Redefinir Senha <i class='bx bx-reset'></i></button>
        </div>
        <p id="msg-erro" v-if="erroRecuperacao">{{ erroRecuperacao }}</p>
        <p id="msg-sucesso" v-if="sucessoRecuperacao">{{ sucessoRecuperacao }}</p>
        <div class="swith-form">
          <span>Lembrou sua senha? <a href="#" @click.prevent="trocarFormulario('login')">Voltar ao Login</a></span>
        </div>
      </form>
    </div>
  </section>
</template>



<script>
import axios from 'axios';

export default {
  data() {
    return {
      modoCadastro: false,
      modoRecuperacao: false,
      login: { email: '', password: '' },
      cadastro: { nome: '', email: '', senha: '', confirmarSenha: '', permissao: 'CLIENT' },
      mostrarSenhaLogin: false,
      mostrarSenhaCadastro: false,
      erroLogin: '',
      erroCadastro: '',
      erroRecuperacao: '',
      sucessoCriacao: '',
      sucessoRecuperacao: ''
    };
  },
  methods: {
    async fazerLogin() {
      this.erroLogin = '';
      try {
        const response = await axios.post('http://localhost:8081/auth/login', this.login);
        const token = response.data.token;

        localStorage.setItem('jwtToken', token);
        localStorage.setItem('usuarioEmail', this.login.email);

        this.$router.push('/');
      } catch (error) {
        this.erroLogin = error.response?.data?.message || 'Usuário ou senha inválidos.';
      }
    },
    criarUsuario() {
      if (this.cadastro.senha !== this.cadastro.confirmarSenha) {
        this.erroCadastro = 'As senhas devem ser iguais.';
        return;
      }

      axios.post('http://localhost:8081/clients', {
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
        }, 2500);
      })
      .catch(error => {
        this.erroCadastro = error.response?.data?.message || 'Erro ao criar usuário';
        this.sucessoCriacao = '';
      });
    },
    recuperarSenha() {
      if (this.cadastro.senha !== this.cadastro.confirmarSenha) {
        this.erroRecuperacao = 'As senhas devem ser iguais.';
        this.sucessoRecuperacao = '';
        return;
      }

      axios.post('http://localhost:8081/usuario/recuperar-senha', {
        email: this.login.email,
        novaSenha: this.cadastro.senha
      })
      .then(() => {
        this.sucessoRecuperacao = 'Senha redefinida com sucesso.';
        this.erroRecuperacao = '';
        this.resetRecuperacao();
        setTimeout(() => {
          this.modoRecuperacao = false;
          this.sucessoRecuperacao = '';
        }, 2500);
      })
      .catch(error => {
        this.erroRecuperacao = error.response?.data?.message || 'Erro ao redefinir a senha';
        this.sucessoRecuperacao = '';
      });
    },
    trocarFormulario(modo) {
      this.modoCadastro = modo === 'cadastro';
      this.modoRecuperacao = modo === 'recuperacao';
      this.erroCadastro = '';
      this.erroLogin = '';
      this.erroRecuperacao = '';
      this.sucessoCriacao = '';
      this.sucessoRecuperacao = '';
      this.login = { email: '', password: '' };
      this.resetCadastro();
      this.resetSenhasVisiveis();
    },
    resetCadastro() {
      this.cadastro = { nome: '', email: '', senha: '', confirmarSenha: '', permissao: 'Aluno' };
    },
    resetRecuperacao() {
      this.login.email = '';
      this.cadastro.senha = '';
      this.cadastro.confirmarSenha = '';
      this.mostrarSenhaCadastro = false;
    },
    resetSenhasVisiveis() {
      this.mostrarSenhaLogin = false;
      this.mostrarSenhaCadastro = false;
      this.mostrarSenhaRecuperacao = false;
    }
  }
};
</script>

<style scoped>
@import '../assets/login/login.scss';
</style>
