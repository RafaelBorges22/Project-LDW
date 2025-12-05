<script setup>
import { ref } from 'vue';
import axios from 'axios';

// URL base da sua API (ajuste conforme necessário)
const API_BASE_URL = import.meta.env.VITE_API_URL_REQ;

const email = ref('');
const message = ref('');
const isError = ref(false);
const isLoading = ref(false);

const requestReset = async () => {
  if (!email.value) {
    message.value = 'Por favor, insira seu endereço de e-mail.';
    isError.value = true;
    return;
  }

  isLoading.value = true;
  message.value = '';
  isError.value = false;

  try {
    const response = await axios.post(API_BASE_URL, {
      email: email.value
    });

    message.value = response.data; // Ex: "Se o e-mail estiver cadastrado, um link será enviado."
    isError.value = false;
    email.value = ''; // Limpa o campo após o sucesso

  } catch (error) {
    // O backend retorna 200 OK mesmo que o email não exista por segurança,
    // então a maioria dos erros de rede ou servidor cairão aqui.
    console.error('Erro ao solicitar redefinição:', error);
    message.value = 'Ocorreu um erro ao tentar se conectar ao servidor. Tente novamente mais tarde.';
    isError.value = true;
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="reset-container">
    <h2>Solicitar Redefinição de Senha</h2>
    <p>Insira seu e-mail para receber um token de redefinição.</p>

    <form @submit.prevent="requestReset">
      <div class="form-group">
        <label for="email">E-mail:</label>
        <input
          id="email"
          type="email"
          v-model="email"
          required
          :disabled="isLoading"
        />
      </div>

      <button type="submit" :disabled="isLoading">
        <span v-if="isLoading">Enviando...</span>
        <span v-else>Enviar Token</span>
      </button>
    </form>

    <p v-if="message" :class="['message', { 'error': isError, 'success': !isError }]">
      {{ message }}
    </p>
  </div>
</template>

<style scoped>
.reset-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.form-group {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}
input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}
button:hover:not(:disabled) {
  background-color: #0056b3;
}
button:disabled {
  background-color: #a0c3e8;
  cursor: not-allowed;
}
.message {
  margin-top: 20px;
  padding: 10px;
  border-radius: 4px;
}
.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}
</style>