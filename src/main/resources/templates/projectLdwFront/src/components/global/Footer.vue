<template>
  <footer class="main-footer">
    <div class="footer-top">
      <div class="footer-column">
        <div class="footer-logo">
          <img src="../../assets/base/LogoFooter.png" alt="Kazu Tattoo Logo" class="logo-image-footer" />
        </div>
      </div>

      <div class="footer-column">
        <h3 class="footer-title">Links</h3>
        <ul class="footer-links">
          <router-link to="/">
            <li><p>Home</p></li>
          </router-link>
          <router-link to="/">
            <li><p>Orçamento</p></li>
          </router-link>
          <router-link to="/">
            <li><p>Pós-Tattoo</p></li>
          </router-link>
          <router-link to="/">
            <li><p>Meu Trabalho</p></li>
          </router-link>
        </ul>
      </div>

      <div class="footer-column">
        <h3 class="footer-title">Contatos</h3>
        <ul class="footer-social">
          <li>
            <a href="https://wa.me/5511959895643?text=Ol%C3%A1,%20gostaria%20de%20fazer%20um%20or%C3%A7amento."
              class="social-link" target="_blank">
              <i class="fi fi-brands-whatsapp"></i>
              <span class="social-text">Whatsapp</span>
            </a>
          </li>
          <li>
            <a href="https://www.instagram.com/kazutattoo_/?igsh=eXQ4MHI5dXZ4amY%3D#" class="social-link"
              target="_blank">
              <i class="fi fi-brands-instagram"></i>
              <span class="social-text">Instagram</span>
            </a>
          </li>
        </ul>
      </div>
    </div>

    <div class="footer-bottom">
      <p class="copyright-text">© 2025 Kazu Tattoo. Todos os direitos reservados</p>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer'
};
</script>

<style scoped>
@import '../../assets/Scss/global/Footer.scss';
</style>