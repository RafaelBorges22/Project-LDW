<template>
  <section class="budget-table">
    <h3 class="table-title">Orçamentos</h3>

    <!-- Estado: carregando -->
    <div v-if="loading" class="table-placeholder">
      <div class="spinner"></div>
      <p>Carregando orçamentos...</p>
    </div>

    <!-- Estado: erro -->
    <div v-else-if="error" class="table-error">
      <p>Erro ao carregar: {{ error }}</p>
      <button class="retry-btn" @click="fetchQuotes">Tentar novamente</button>
    </div>

    <!-- Estado: sucesso -->
    <div v-else>
      <table v-if="hasData" class="styled-table">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Tamanho</th>
            <th>Parte do Corpo</th>
            <th>Valor Final</th>
            <th>Contato</th>
          </tr>
        </thead>
        <tbody>
  <tr
    v-for="(quote, i) in quotes"
    :key="i"
    @click="goToQuoteDetails(quote.id)"
    class="clickable-row"
  >
    <td><strong>{{ quote.clientName || "—" }}</strong></td>
    <td>{{ quote.size || "—" }}</td>
    <td>{{ quote.bodyPart || "—" }}</td>
    <td>{{ formatCurrency(quote.additionalCost + quote.finalValue)}}</td>

    <td>
      <div class="client-info">
        <p class="muted">{{ quote.clientEmail || "—" }}</p>
      </div>
    </td>
  </tr>
</tbody>

      </table>

      <div v-else class="no-data">Nenhum orçamento encontrado.</div>
    </div>
  </section>
</template>

<script lang="js" setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// ✔ Pegando a URL da API do .env
const API_URL = import.meta.env.VITE_API_URL_BUD;

const quotes = ref([]);
const loading = ref(true);
const error = ref(null);

async function fetchQuotes() {
  loading.value = true;
  error.value = null;

  try {
    const response = await fetch(API_URL);
    if (!response.ok) throw new Error(`${response.status} ${response.statusText}`);

    const data = await response.json();
    quotes.value = normalizeQuotes(data);
  } catch (err) {
    error.value = err?.message || "Erro desconhecido";
    quotes.value = [];
  } finally {
    loading.value = false;
  }
}

function normalizeQuotes(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  return [];
}

function formatCurrency(value) {
  if (!value) return "—";
  return Number(value).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

function goToQuoteDetails(id) {
  router.push(`/quotes/${id}`);
}

const hasData = computed(() => quotes.value.length > 0);

onMounted(fetchQuotes);
</script>

<style scoped>
@import "../../assets/budget/BudgetTable.scss";
</style>
