<template>
  <section class="login">
	<div class="wrapper">
      <div class="form-header">
        <div class="titles">
          <div v-if="!modoCadastro && !modoRecuperacao">Login</div>
          <div v-else-if="modoCadastro">Cadastro</div>
          <div v-else>Recuperar Senha</div>
        </div>
      </div>

      <form v-if="!modoCadastro && !modoRecuperacao" @submit.prevent="fazerLogin">
        <div>
          <label>Email</label>
          <input type="email" v-model="login.email" required />
        </div>
        <div>
          <label>Senha</label>
          <input :type="mostrarSenhaLogin ? 'text' : 'password'" v-model="login.password" required />
          <input type="checkbox" v-model="mostrarSenhaLogin" /> Mostrar senha
        </div>
        <button type="submit">Entrar</button>
        <p v-if="erroLogin">{{ erroLogin }}</p>
        <p>
          <a href="#" @click.prevent="trocarFormulario('recuperacao')">Esqueceu a senha?</a> |
          <a href="#" @click.prevent="trocarFormulario('cadastro')">Cadastrar</a>
        </p>
      </form>

      <form v-else-if="modoCadastro" @submit.prevent="criarUsuario">
        <div>
          <label>Nome</label>
          <input type="text" v-model="cadastro.nome" required />
        </div>
        <div>
          <label>Email</label>
          <input type="email" v-model="cadastro.email" required />
        </div>
        <div>
          <label>Senha</label>
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" v-model="cadastro.senha" required />
        </div>
        <div>
          <label>Confirmar senha</label>
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" v-model="cadastro.confirmarSenha" required />
          <input type="checkbox" v-model="mostrarSenhaCadastro" /> Mostrar senhas
        </div>
        <button type="submit">Cadastrar</button>
        <p v-if="erroCadastro">{{ erroCadastro }}</p>
        <p v-if="sucessoCriacao">{{ sucessoCriacao }}</p>
        <p><a href="#" @click.prevent="trocarFormulario('login')">Voltar ao login</a></p>
      </form>

      <form v-else @submit.prevent="recuperarSenha">
        <div>
          <label>Email</label>
          <input type="email" v-model="login.email" required />
        </div>
        <div>
          <label>Nova senha</label>
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" v-model="cadastro.senha" required />
        </div>
        <div>
          <label>Confirmar nova senha</label>
          <input :type="mostrarSenhaCadastro ? 'text' : 'password'" v-model="cadastro.confirmarSenha" required />
          <input type="checkbox" v-model="mostrarSenhaCadastro" /> Mostrar senhas
        </div>
        <button type="submit">Redefinir senha</button>
        <p v-if="erroRecuperacao">{{ erroRecuperacao }}</p>
        <p v-if="sucessoRecuperacao">{{ sucessoRecuperacao }}</p>
        <p><a href="#" @click.prevent="trocarFormulario('login')">Voltar ao login</a></p>
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
