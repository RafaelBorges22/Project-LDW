<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router'; // Requer vue-router
import axios from 'axios';

// URL base da sua API (ajuste conforme necessário)
const API_BASE_URL = import.meta.env.VITE_API_URL_RES;

// Usar o router para pegar o token da URL (Ex: /reset?token=XYZ)
const route = useRoute(); 

const token = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
const message = ref('');
const isError = ref(false);
const isLoading = ref(false);

onMounted(() => {
  // Pega o token da URL ao carregar o componente
  token.value = route.query.token || ''; 

  if (!token.value) {
    message.value = 'Token de redefinição não encontrado na URL.';
    isError.value = true;
  }
});

const resetPassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    message.value = 'As senhas não coincidem.';
    isError.value = true;
    return;
  }

  if (newPassword.value.length < 6) { // Adicione suas regras de validação de senha
    message.value = 'A nova senha deve ter pelo menos 6 caracteres.';
    isError.value = true;
    return;
  }

  isLoading.value = true;
  message.value = '';
  isError.value = false;

  try {
    const response = await axios.post(API_BASE_URL, {
      token: token.value,
      newPassword: newPassword.value
    });

    message.value = response.data; // Ex: "Senha redefinida com sucesso!"
    isError.value = false;
    
  } catch (error) {
    console.error('Erro ao redefinir senha:', error);
    // Erro do Backend, geralmente 400 Bad Request
    const errorMessage = error.response?.data || 'Erro ao redefinir senha. O token pode ser inválido ou ter expirado.';
    message.value = errorMessage;
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="reset-container">
    <h2>Definir Nova Senha</h2>
    <p v-if="token">Token encontrado. Insira sua nova senha.</p>
    <p v-else class="error">
        ⚠️ {{ message }}
    </p>

    <form v-if="token" @submit.prevent="resetPassword">
      <div class="form-group">
        <label for="newPassword">Nova Senha:</label>
        <input
          id="newPassword"
          type="password"
          v-model="newPassword"
          required
          :disabled="isLoading"
        />
      </div>

      <div class="form-group">
        <label for="confirmPassword">Confirme a Nova Senha:</label>
        <input
          id="confirmPassword"
          type="password"
          v-model="confirmPassword"
          required
          :disabled="isLoading"
        />
      </div>

      <button type="submit" :disabled="isLoading || !token">
        <span v-if="isLoading">Redefinindo...</span>
        <span v-else>Redefinir Senha</span>
      </button>
    </form>

    <p v-if="message && !isError" class="message success">
      {{ message }}
    </p>
    <p v-if="message && isError" class="message error">
      {{ message }}
    </p>
  </div>
</template>

<style scoped>
/* Use o mesmo estilo do componente anterior (RequestReset.vue) */
.reset-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
/* ... (Inclua o restante do CSS separe os estilos) ... */
</style>