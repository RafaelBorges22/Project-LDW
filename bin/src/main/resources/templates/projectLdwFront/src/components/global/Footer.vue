<template>
  <footer class="main-footer">
    <div class="footer-top">
      <div class="footer-column">
        <div class="footer-logo">
          <img src="../../../public/images/LogoFooter.png" alt="Kazu Tattoo Logo" class="logo-image-footer" />
        </div>
      </div>
      
      <div class="footer-column">
        <h3 class="footer-title">Links</h3>
        <ul class="footer-links">
          <li><a href="#">Home</a></li>
          <li><a href="#">Orçamento</a></li>
          <li><a href="#">Pós-Tattoo</a></li>
          <li><a href="#">Meu Trabalho</a></li>
        </ul>
      </div>

      <div class="footer-column">
        <h3 class="footer-title">Contatos</h3>
        <ul class="footer-social">
          <li>
            <a href="#" class="social-link">
              <img src="../../../public/images/icons/whatsapp_icon.png" alt="WhatsApp" class="social-icon" />
              <span class="social-text">Whatsapp</span>
            </a>
          </li>
          <li>
            <a href="#" class="social-link">
              <img src="../../../public/images/icons/instagram_icon.png" alt="Instagram" class="social-icon" />
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
@import '../../assets/Footer.scss';
</style>