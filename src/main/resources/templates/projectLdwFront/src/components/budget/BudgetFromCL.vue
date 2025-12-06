<template>
  <div class="budget-container">
    <h2>Meus Orçamentos</h2>

    <div v-if="loading">Carregando...</div>

    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="alreadySearched && quotes.length === 0">
      Nenhum orçamento encontrado.
    </div>

    <div v-for="q in quotes" :key="q.id" class="budget-card">
      <h3>{{ q.description }}</h3>
      <p><strong>Estado:</strong> {{ q.state }}</p>
      <p><strong>Valor Final:</strong> {{ formatCurrency(q.finalValue) }}</p>
      <p><strong>Custos Extras:</strong> {{ formatCurrency(q.additionalCost) }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const quotes = ref([]);
const loading = ref(false);
const error = ref(null);
const id = ref(null);
const alreadySearched = ref(false);

const API_URL_CLI = import.meta.env.VITE_API_URL_CLI;
const API_URL_BUD = import.meta.env.VITE_API_URL_BUD;

function getEmailFromToken() {
  const token = localStorage.getItem("jwtToken");
  if (!token) return null;

  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.sub || null;
  } catch {
    return null;
  }
}

async function fetchClientId(email) {
  const res = await axios.get(`${API_URL_CLI}/email/${email}`);
  return res.data.id;
}


async function fetchQuotes() {
  if (!id.value) return;

  loading.value = true;
  alreadySearched.value = true;

  try {
    const res = await axios.get(`${API_URL_BUD}/${id.value}/history`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
      },
    });

    quotes.value = res.data;
  } catch (err) {
    console.error(err);
    error.value = "Erro ao carregar histórico.";
  }

  loading.value = false;
}


function formatCurrency(value) {
  if (!value) return "—";
  return Number(value).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

onMounted(async () => {
  const email = getEmailFromToken();

  if (!email) {
    error.value = "Token inválido.";
    return;
  }

  try {
    id.value = await fetchClientId(email);
    await fetchQuotes();
  } catch (err) {
    console.error(err);
    error.value = "Erro ao obter ID do cliente.";
  }
});
</script>

<style scoped>
.error {
  color: red;
  margin-bottom: 10px;
}
.budget-card {
  border: 1px solid #ddd;
  padding: 12px;
  margin-bottom: 10px;
}
</style>
