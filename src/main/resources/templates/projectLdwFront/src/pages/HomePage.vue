<template>
  <div id="app">
    <Navbar />

    <!-- Pop-up de boas-vindas (mesmo layout dos pop-ups do Login) -->
<div
  v-if="showWelcome"
  class="welcome-popup-overlay"
  @click="fecharWelcome"
  aria-modal="true"
  role="dialog"
>
  <div class="welcome-popup-card info" @click.stop>
    <div class="welcome-popup-logo-container">
      <span class="welcome-popup-logo"><img src="../assets/base/LogoFooter.png" alt=""></span>
    </div>

    <h3 class="welcome-popup-title">
      Bem-vindo(a) {{ firstName }}!
    </h3>

    <p class="welcome-popup-description">
      Você está no lugar certo para transformar sua ideia em tatuagem. Aqui você pode:
    </p>

    <ul class="welcome-popup-features">
      <li><strong>Agendar sua tatuagem</strong> em poucos cliques, escolhendo data e horário.</li>
      <li><strong>Fazer um orçamento</strong> sem burocracia, direto pelo site.</li>
      <li><strong>Acompanhar o status</strong> do seu pedido em tempo real.</li>
      <li><strong>Falar com o tatuador</strong> no <em>chat</em> integrado — sem sair do site.</li>
    </ul>

    <div class="welcome-popup-buttons">
      <button type="button" class="welcome-popup-btn" @click="irParaAgendamento">
        Agende agora <i id="icon-welcome-paw" class="fi fi-ss-paw-claws"></i>
      </button>
      <button type="button" class="welcome-popup-btn outline" @click="fecharWelcome">
        Conheça o site
      </button>
    </div>
  </div>
</div>


    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">TATUE AQUI!</h1>
        <button class="hero-button" @click="goToBudget">AGENDE SEU HORÁRIO</button>
      </div>
    </section>

    <!-- About Section - TATUAGENS -->
    <section class="about-section">
      <div class="about-image-container">
        <img src="../assets/images/AboutImgLast.png" alt="Sobre Tatuagens" class="about-image" />
      </div>
      <div class="about-content">
        <h2 class="about-title">SOBRE TATUAGENS</h2>
        <p class="about-text">
          lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore
          et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
          aliquip ex ea commodo consequat.
        </p>
      </div>
    </section>

    <!-- About Section - SOBRE MIM -->
    <section class="about-section reverse-layout">
      <div class="about-image-container">
        <img src="../assets/images/AboutImgFirst.png" alt="Sobre Mim" class="about-image" />
      </div>
      <div class="about-content">
        <h2 class="about-title">SOBRE MIM</h2>
        <p class="about-text">
          lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore
          et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
          aliquip ex ea commodo consequat.
        </p>
      </div>
    </section>

    <Footer />
  </div>
</template>

<script>
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';

export default {
  name: 'HomePage',
  components: { Navbar, Footer },
  data() {
    return {
      showWelcome: false,
      firstName: ''
    };
  },
  mounted() {
    const email = (localStorage.getItem('usuarioEmail') || '').trim();
    const key = `welcomeShown:${email || 'anon'}`;

    // tenta pegar o nome completo salvo (ex.: "Maria Clara da Silva")
    const nomeBruto = (localStorage.getItem('usuarioNome') || '').trim();

    if (nomeBruto) {
      this.firstName = this.pickFirstAndSecond(nomeBruto);
    } else if (email.includes('@')) {
      // fallback: tenta gerar duas palavras a partir do e-mail
      this.firstName = this.deriveTwoFromEmail(email) || 'ao estúdio';
    } else {
      this.firstName = 'ao estúdio';
    }

    const jaViu = localStorage.getItem(key);
    if (!jaViu) {
      this.showWelcome = true;
      localStorage.setItem(key, 'true');
    }
  },
  methods: {
    goToBudget() {
      window.location.href = '/budget';
    },
    irParaAgendamento() {
      this.showWelcome = false;
      this.goToBudget();
    },
    fecharWelcome() {
      this.showWelcome = false;
    },

    // -------- Helpers para formatar nome --------
    pickFirstAndSecond(full) {
      const tokens = String(full).replace(/\s+/g, ' ').trim().split(' ');
      if (!tokens.length) return '';

      const connectors = new Set(['da', 'de', 'do', 'das', 'dos', 'e']);

      // Normaliza para Title Case, mas preserva conectores em minúsculo
      const normalized = tokens.map((t, i) => {
        const lower = t.toLowerCase();
        return connectors.has(lower) && i !== 0 ? lower : this.titleCase(lower);
      });

      const first = normalized[0] || '';
      let second = '';

      // procura o primeiro token que não seja conector após o primeiro
      for (let i = 1; i < normalized.length; i++) {
        if (!connectors.has(normalized[i].toLowerCase())) {
          second = normalized[i];
          break;
        }
      }

      // fallback: se não houver segundo nome
      return second ? `${first} ${second}` : first;
    },

    deriveTwoFromEmail(email) {
      if (!email || !email.includes('@')) return '';
      const user = email.split('@')[0];
      // separa por . _ - e remove números puros
      const parts = user.split(/[._-]+/).filter(Boolean).filter(p => /[A-Za-zÀ-ÿ]/.test(p));
      if (!parts.length) return '';
      const p1 = this.titleCase(parts[0]);
      const p2 = parts[1] ? this.titleCase(parts[1]) : '';
      return p2 ? `${p1} ${p2}` : p1;
    },

    titleCase(str) {
      if (!str) return '';
      return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
    }
  }
};
</script>

<style>
@import '../assets/Scss/global/Global.scss';
@import '../assets/Scss/pages/home.scss';
</style>
