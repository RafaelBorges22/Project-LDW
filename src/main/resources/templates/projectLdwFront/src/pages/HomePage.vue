<template>
  <div id="app">
    <Navbar />

    <div v-if="showWelcome" class="welcome-popup-overlay" @click="fecharWelcome" aria-modal="true" role="dialog">
      <div class="welcome-popup-card info" @click.stop>
        <div class="welcome-popup-logo-container">
          <span class="welcome-popup-logo">
            <img :src="LogoFooter" alt="Logo Footer" />
          </span>
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
          <li><strong>Falar com o tatuador</strong> no <em>chat</em> integrado, sem sair do site.</li>
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

    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">TATUE AQUI!</h1>
        <button class="hero-button" @click="goToBudget">AGENDE SEU HORÁRIO</button>
      </div>
    </section>

    <section class="about-section">
      <div class="about-image-container">
        <img :src="AboutImgLast" alt="Sobre Tatuagens" class="about-image" />
      </div>
      <div class="about-content">
        <h2 class="about-title">SOBRE TATUAGENS</h2>
        <p class="about-text">Tatuagem é uma forma de marcar quem você é. Cada desenho tem um motivo, um gosto ou uma
          história por trás. Aqui eu trabalho a tattoo do jeito que você imagina: algo que combine com sua
          personalidade, seu estilo e o que você quer passar. Não importa se é algo simbólico, estético ou só porque
          achou legal, o importante é que faça sentido para você e fique bonito na pele.</p>
      </div>
    </section>

    <section class="about-section reverse-layout section-dark">
      <div class="about-image-container">
        <img :src="AboutImgFirst" alt="Sobre Mim" class="about-image" />
      </div>
      <div class="about-content">
        <h2 class="about-title">SOBRE MIM</h2>
        <p class="about-text">Sou tatuador desde 2012 e sempre gostei de transformar ideias em arte. Meu estilo puxa
          mais para o sombrio, o diferente e o que foge do comum. Gosto de trabalhar com calma, explicar tudo e deixar
          você seguro do que vai levar na pele. Meu foco é fazer uma tattoo bonita, bem-feita e com a sua cara.</p>
      </div>
    </section>

    <section class="about-section">
      <div class="about-image-container">
        <img :src="AboutImgMiddle" alt="Meu Trabalho" class="about-image" />
      </div>
      <div class="about-content">
        <h2 class="about-title">MEU TRABALHO</h2>
        <p class="about-text">Tatudo desde 2012, sempre buscando criar peças marcantes e com personalidade. Meu foco são
          estilos não realistas, explorando temas sombrios, góticos e criaturas que fogem do comum. Gosto de trabalhar
          com artes que tenham atitude e deixem a tattoo única na pele.</p>
      </div>
    </section>

    <section class="about-section section-dark aftercare-section">
      <div class="about-content">
        <h2 class="about-title">CUIDADOS DEPOIS DA TATTOO</h2>

        <div class="aftercare-grid" role="list" aria-label="Cuidados após a tatuagem">
          <div class="aftercare-item" role="listitem">
            <span class="aftercare-icon" aria-hidden="true"><i class="fi fi-sr-gauze-bandage"></i></span>
            <p id="strong">Deixe o curativo</p>
            <p class="aftercare-text">Não retire antes do tempo informado.</p>
          </div>

          <div class="aftercare-item" role="listitem">
            <span class="aftercare-icon" aria-hidden="true"><i class="fi fi-ss-soap"></i></span>
            <p id="strong">Lave bem</p>
            <p class="aftercare-text">Use sabonete neutro 2x ao dia, sem
              esfregar.</p>
          </div>

          <div class="aftercare-item" role="listitem">
            <span class="aftercare-icon" aria-hidden="true"><i class="fi fi-ss-hand-paper"></i></span>
            <p id="strong">Não coce</p>
            <p class="aftercare-text">Não arranque casquinhas, deixe cair naturalmente.
            </p>
          </div>

          <div class="aftercare-item" role="listitem">
            <span class="aftercare-icon" aria-hidden="true"><i class="fi fi-br-cream"></i></span>
            <p id="strong">Use pomada</p>
            <p class="aftercare-text">Aplique a indicada pelo tatuador, sem excessos.
            </p>
          </div>

          <div class="aftercare-item" role="listitem">
            <span class="aftercare-icon" aria-hidden="true"><i class="fi fi-ss-apple-whole"></i></span>
            <p id="strong">Alimente bem</p>
            <p class="aftercare-text">Evite comidas muito gordurosas nas primeiras
              semanas.</p>
          </div>

          <div class="aftercare-item" role="listitem">
            <span class="aftercare-icon" aria-hidden="true"><i class="fi fi-ss-sun"></i></span>
            <p id="strong">Proteja-se do sol</p>
            <p class="aftercare-text">Evite exposição direta por pelo menos 30
              dias.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== GALERIA (com otimizações) ========== -->
    <div class="gallery-section">
      <h2 class="section-title">Galeria de Trabalhos</h2>

      <div class="gallery-container" ref="galleryContainer" @mouseenter="pauseAutoplay" @mouseleave="resumeAutoplay"
        @touchstart.passive="onTouchStart" @touchmove.passive="onTouchMove" @touchend="onTouchEnd">
        <button class="gallery-nav prev" @click="prev" aria-label="Imagem Anterior">
          <i class="fi fi-rr-angle-left"></i>
        </button>

        <div class="gallery-track" ref="galleryTrack" @transitionend="onTransitionEnd"
          :style="{ transform: `translate3d(-${activeIndex * 100}%, 0, 0)` }">
          <div v-for="(img, i) in galleryImages" :key="i" class="gallery-item">
            <img :src="img.src" :alt="img.alt" loading="lazy" decoding="async" />
          </div>
        </div>

        <button class="gallery-nav next" @click="next" aria-label="Próxima Imagem">
          <i class="fi fi-rr-angle-right"></i>
        </button>
      </div>

      <div class="gallery-dots">
        <button v-for="(_, i) in galleryImages" :key="'dot-' + i" class="dot"
          :class="{ 'dot-active': i === activeIndex }" @click="goTo(i)" aria-label="Ir para imagem"></button>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script>
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';

/* import explícito para o bundler */
import LogoFooter from '../assets/base/LogoFooter.png';
import AboutImgLast from '../assets/images/AboutImgLast.png';
import AboutImgFirst from '../assets/images/AboutImgFirst.png';
import AboutImgMiddle from '../assets/images/AboutImgMiddle.jpg';
import AboutImgAftercare from '../assets/images/skincare.png';
import Img1 from '../assets/images/tatuagem.jpg';
import Img2 from '../assets/images/tatuagem2.jpg';
import Img3 from '../assets/images/tatuagem3.jpg';
import Img4 from '../assets/images/tatuagem4.jpg';
import Img5 from '../assets/images/tatuagem5.jpg';
import Img6 from '../assets/images/tatuagem6.jpg';

export default {
  name: 'HomePage',
  components: { Navbar, Footer },
  data() {
    return {
      showWelcome: false,
      firstName: '',
      activeIndex: 0,
      autoplayInterval: null,
      autoplayDelay: 4000, // ms
      touchStartX: 0,
      touchEndX: 0,
      isTransitioning: false,

      LogoFooter,
      AboutImgLast,
      AboutImgFirst,
      AboutImgMiddle,
      AboutImgAftercare,

      galleryImages: [
        { src: Img1, alt: "Imagem 1 da galeria" },
        { src: Img2, alt: "Imagem 2 da galeria" },
        { src: Img3, alt: "Imagem 3 da galeria" },
        { src: Img4, alt: "Imagem 4 da galeria" },
        { src: Img5, alt: "Imagem 5 da galeria" },
        { src: Img6, alt: "Imagem 6 da galeria" }
      ]
    };
  },
  mounted() {
    // nome / welcome logic (mantido)
    const email = (localStorage.getItem('usuarioEmail') || '').trim();
    const key = `welcomeShown:${email || 'anon'}`;
    const nomeBruto = (localStorage.getItem('usuarioNome') || '').trim();

    if (nomeBruto) {
      this.firstName = this.pickFirstAndSecond(nomeBruto);
    } else if (email.includes('@')) {
      this.firstName = this.deriveTwoFromEmail(email) || 'ao estúdio';
    } else {
      this.firstName = 'ao estúdio';
    }

    const jaViu = localStorage.getItem(key);
    if (!jaViu) {
      this.showWelcome = true;
      localStorage.setItem(key, 'true');
    }

    // inicia autoplay do carrossel
    this.startAutoplay();

    // pausar autoplay quando aba estiver oculta
    document.addEventListener('visibilitychange', this.onVisibilityChange);
  },
  beforeUnmount() {
    this.stopAutoplay();
    document.removeEventListener('visibilitychange', this.onVisibilityChange);
  },
  methods: {
    // navegação do carrossel (respeita isTransitioning)
    next() {
      if (this.isTransitioning) return;
      this.isTransitioning = true;
      this.activeIndex = (this.activeIndex + 1) % this.galleryImages.length;
    },
    prev() {
      if (this.isTransitioning) return;
      this.isTransitioning = true;
      this.activeIndex = (this.activeIndex - 1 + this.galleryImages.length) % this.galleryImages.length;
    },
    goTo(i) {
      if (this.isTransitioning || i === this.activeIndex) return;
      this.isTransitioning = true;
      this.activeIndex = i;
    },

    // autoplay
    startAutoplay() {
      this.stopAutoplay();
      this.autoplayInterval = setInterval(() => {
        if (!this.isTransitioning) this.next();
      }, this.autoplayDelay);
    },
    stopAutoplay() {
      if (this.autoplayInterval) {
        clearInterval(this.autoplayInterval);
        this.autoplayInterval = null;
      }
    },
    pauseAutoplay() {
      this.stopAutoplay();
    },
    resumeAutoplay() {
      this.startAutoplay();
    },

    // visibilidade da aba
    onVisibilityChange() {
      if (document.hidden) {
        this.pauseAutoplay();
      } else {
        this.resumeAutoplay();
      }
    },

    // transition end handler: libera a navegação
    onTransitionEnd(e) {
      if (e.target === this.$refs.galleryTrack) {
        // margem de segurança para garantir repaint completo
        setTimeout(() => {
          this.isTransitioning = false;
        }, 0);
      }
    },

    // touch handlers (swipe)
    onTouchStart(e) {
      this.pauseAutoplay();
      this.touchStartX = e.touches ? e.touches[0].clientX : e.clientX;
      this.touchEndX = this.touchStartX;
    },
    onTouchMove(e) {
      this.touchEndX = e.touches ? e.touches[0].clientX : e.clientX;
    },
    onTouchEnd() {
      const dx = this.touchEndX - this.touchStartX;
      const threshold = 40; // pixels
      if (dx > threshold) {
        this.prev();
      } else if (dx < -threshold) {
        this.next();
      }
      this.resumeAutoplay();
    },

    // navegação (exemplo)
    goToBudget() {
      this.$router.push('/budget');
    },
    irParaAgendamento() {
      this.showWelcome = false;
      this.goToBudget();
    },
    fecharWelcome() {
      this.showWelcome = false;
    },

    /* helpers de nome (mantidos) */
    pickFirstAndSecond(full) {
      const tokens = String(full).replace(/\s+/g, ' ').trim().split(' ');
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

      return second ? `${first} ${second}` : first;
    },

    deriveTwoFromEmail(email) {
      if (!email || !email.includes('@')) return '';
      const user = email.split('@')[0];
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
