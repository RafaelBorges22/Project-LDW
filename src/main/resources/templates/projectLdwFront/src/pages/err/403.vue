<template>
  <div class="error-403-container">
    <div class="error-card">
      <h1>403</h1>
      <h2>Acesso Negado</h2>
      <p>Você não tem permissão para acessar esta página.</p>

      <router-link to="/" class="back-button">Voltar para o início</router-link>
    </div>
  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
$brown-color: #5C3A33;
$brown-color-white: #664039;
$black-color: #1a1a1a;
$gray-color: #bdc3c7;
$background-color: #F8F5EE;
$text-color-black: #333;
$text-color-white: #f8f8f8;
$error-color: #b32323;
$success-color: #2d8031;
$transition: 0.3s ease-in-out;

.error-403-container {
  width: 100%;
  height: 100vh;
  background-color: $background-color;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1rem;
}

.error-card {
  background: lighten($background-color, 2%);
  padding: 2.5rem 3.5rem;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  border: 2px solid $brown-color-white;
  animation: fadeIn 0.5s $transition;

  h1 {
    font-size: 6rem;
    color: $error-color;
    margin-bottom: 0.5rem;
    font-weight: 900;
  }

  h2 {
    font-size: 1.8rem;
    color: $brown-color;
    margin-bottom: 1rem;
    font-weight: 600;
  }

  p {
    color: $text-color-black;
    font-size: 1.1rem;
    margin-bottom: 2rem;
  }

  .back-button {
    display: inline-block;
    padding: 0.8rem 1.6rem;
    background: $brown-color;
    color: $text-color-white;
    border-radius: 8px;
    transition: background $transition, transform $transition;
    font-weight: 600;

    &:hover {
      background: $brown-color-white;
      transform: scale(1.05);
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .error-card {
    padding: 2rem;
    
    h1 {
      font-size: 4rem;
    }

    h2 {
      font-size: 1.4rem;
    }
  }
}
</style>
