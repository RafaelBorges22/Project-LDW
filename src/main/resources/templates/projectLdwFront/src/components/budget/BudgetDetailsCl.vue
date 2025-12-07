<template>
  <div id="app">
    <Navbar />

    <section class="quote-details" v-if="!loading && !notFound">
      <div class="header">
        <div>
          <button class="back-btn" @click="goBack">Voltar</button>
        </div>
        <div class="status-badge">
          <strong :class="['status', `status-${quote?.state?.toLowerCase()}`]">
            {{ traduzirEstado(quote?.state) }}
          </strong>
        </div>
      </div>

      <div v-if="error" class="loading">
        <p>{{ error }}</p>
      </div>

      <div v-else class="details-container">
        <div class="image-box" v-if="quote?.imageUrl">
          <img :src="quote.imageUrl" alt="Imagem do orçamento" />
        </div>

        <div class="info-box main-info-box">
          <h3>Detalhes do Orçamento</h3>
          <ul>
            <li><strong>Descrição:</strong> {{ quote.description || '—' }}</li>
            <li><strong>Parte do Corpo:</strong> {{ traduzirParteCorpo(quote.bodyPart) }}</li>
            <li><strong>Tamanho:</strong> {{ traduzirTamanho(quote.size) }}</li>
            <li><strong>Colorido:</strong> {{ quote.isColored ? 'Sim' : 'Não' }}</li>
          </ul>
          <hr class="separator"/>
          <h4>Valores</h4>
          <ul class="values-list">
            <li><strong>Valor Estimado:</strong> <span class="value-highlight estimated">{{ formatCurrency(quote.finalValue - (quote.additionalCost || 0)) }}</span></li>
            <li><strong>Ajuste do Tatuador:</strong> <span class="value-highlight extra">{{ formatCurrency(quote.additionalCost) }}</span></li>
            <li class="final-value-row"><strong>Valor Final:</strong> <span class="value-highlight final">{{ formatCurrency(quote.finalValue) }}</span></li>
          </ul>
        </div>
      </div>
    </section>

    <div v-if="loading" class="loading">
      <div class="spinner" />
      <p>Carregando orçamento...</p>
    </div>

    <div v-if="notFound && !loading" class="loading">
      <p>Orçamento não encontrado.</p>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import Navbar from '../global/NavBar.vue';
import Footer from '../global/Footer.vue';

const route = useRoute();
const router = useRouter();

const API_URL_BUD = import.meta.env.VITE_API_URL_BUD;

const quote = ref(null);
const loading = ref(true);
const error = ref(null);
const notFound = ref(false);

/** Formatação **/
function formatCurrency(value) {
  if (value == null || value === '' || isNaN(Number(value))) return '—';
  return Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

/** Tradução enums **/
function traduzirEstado(estado) {
  const mapa = { WAITING: 'Pendente', AWNSERED: 'Respondido', PAID: 'Pago' };
  return mapa[estado] || estado;
}
function traduzirTamanho(tamanho) {
  const mapa = { SMALL: 'Pequeno', MEDIUM: 'Médio', LARGE: 'Grande' };
  return mapa[tamanho] || tamanho;
}
function traduzirParteCorpo(parte) {
  const mapa = {
    ARM: 'Braço', BACK: 'Costas', LEG: 'Perna', CHEST: 'Peito',
    RIB: 'Costela', NECK: 'Pescoço', HAND: 'Mão', HEAD: 'Cabeça',
    FOOT: 'Pé', OTHER: 'Outro'
  };
  return mapa[parte] || parte;
}

/** Navegação **/
function goBack() { router.back(); }

/** Busca orçamento **/
async function fetchQuoteAndValidate() {
  loading.value = true;
  error.value = null;
  notFound.value = false;

  const id = route.params.id;
  if (!id) { error.value = 'ID do orçamento ausente.'; loading.value = false; return; }

  try {
    const token = localStorage.getItem('jwtToken');
    const res = await axios.get(`${API_URL_BUD}/${encodeURIComponent(id)}`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });

    if (!res.data) { notFound.value = true; return; }
    quote.value = res.data;
  } catch (err) {
    if (err?.response?.status === 404) notFound.value = true;
    else error.value = err?.response?.data?.message || err?.message || 'Erro ao carregar orçamento.';
  } finally { loading.value = false; }
}

onMounted(() => fetchQuoteAndValidate());
</script>

<style>
@import "../../assets/Scss/budget/BudgetDetails.scss";
</style>
