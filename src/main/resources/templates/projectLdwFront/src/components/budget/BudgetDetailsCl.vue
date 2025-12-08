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
          <!-- IMAGEM CLICÁVEL (abre lightbox) -->
          <img
            :src="quote.imageUrl"
            alt="Imagem do orçamento"
            class="clickable-image"
            @click="openImage(quote.imageUrl)"
            tabindex="0"
            @keyup.enter="openImage(quote.imageUrl)"
          />
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

    <!-- LIGHTBOX: exibida quando expandedImage tem valor -->
    <div
      v-if="expandedImage"
      class="image-lightbox-backdrop"
      @click.self="closeImage"
      role="dialog"
      aria-modal="true"
      aria-label="Visualização da imagem"
    >
      <div class="image-lightbox-content">
        <button class="lightbox-close" @click="closeImage" aria-label="Fechar">✕</button>
        <img :src="expandedImage" alt="Visualização ampliada" class="lightbox-image" />
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
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

/** Lightbox: expanded image **/
const expandedImage = ref(null);
function openImage(url) {
  expandedImage.value = url;
  document.body.style.overflow = 'hidden';
}
function closeImage() {
  expandedImage.value = null;
  document.body.style.overflow = '';
}
function onKeyDown(e) {
  if (e.key === 'Escape' && expandedImage.value) closeImage();
}

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

onMounted(() => {
  fetchQuoteAndValidate();
  window.addEventListener('keydown', onKeyDown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', onKeyDown);
  // garante restaurar overflow caso o componente seja desmontado com lightbox aberto
  document.body.style.overflow = '';
});
</script>

<style>
@import "../../assets/Scss/budget/BudgetDetails.scss";

/* ========== LIGHTBOX & CLICKABLE IMAGE ========== */

/* imagem clicável */
.image-box .clickable-image {
  cursor: zoom-in;
  border-radius: 10px;
  max-width: 100%;
  display: block;
  transition: transform 180ms ease, box-shadow 180ms ease;
}
.image-box .clickable-image:focus {
  outline: 3px solid rgba(92,58,51,0.12);
}

/* lightbox backdrop */
.image-lightbox-backdrop {
  position: fixed;
  inset: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(180deg, rgba(2,6,10,0.65), rgba(2,6,10,0.8));
  z-index: 1300;
  padding: 18px;
  backdrop-filter: blur(4px);
}

/* content (centra e limita tamanho) */
.image-lightbox-content {
  position: relative;
  max-width: 95%;
  max-height: 95%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  box-sizing: border-box;
  padding: 6px;
}

/* imagem ampliada responsiva */
.lightbox-image {
  max-width: 100%;
  max-height: 100%;
  border-radius: 10px;
  box-shadow: 0 18px 48px rgba(0,0,0,0.6);
  transform-origin: center center;
  animation: zoomIn 240ms cubic-bezier(.2,.9,.3,1) both;
}

/* botão fechar (canto superior direito da lightbox content) */
.lightbox-close {
  position: absolute;
  top: -8px;
  right: -8px;
  background: rgba(0,0,0,0.5);
  color: #fff;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  font-weight: 800;
  cursor: pointer;
  display: grid;
  place-items: center;
  z-index: 10;
  transition: transform 120ms ease, background 120ms ease;
}
.lightbox-close:hover { transform: translateY(-2px); background: rgba(0,0,0,0.65); }

/* animação suave */
@keyframes zoomIn {
  from { transform: scale(0.96); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

/* behavior mobile: ajustar botão fechar para dentro do viewport */
@media (max-width: 520px) {
  .lightbox-close { top: 8px; right: 8px; width: 40px; height: 40px; border-radius: 8px; }
  .image-lightbox-content { padding: 8px; }
}
</style>
