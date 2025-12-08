<template>
  <div id="app">
    <Navbar />

    <section class="quote-details" v-if="!loading && !notFound">
      <!-- Cabeçalho com botão voltar e status -->
      <div class="header">
        <div>
          <button class="back-btn" @click="goBack">← Voltar</button>
        </div>
        <div class="status-badge">
          <strong :class="['status', `status-${edited.state?.toLowerCase() || quote.state?.toLowerCase()}`]">
            {{ traduzirEstado(edited.state || quote.state) }}
          </strong>
        </div>
      </div>

      <!-- Erro -->
      <div v-if="error" class="loading">
        <p>{{ error }}</p>
      </div>

      <!-- Conteúdo principal -->
      <div v-else>
        <!-- Grid principal: imagem + detalhes do orçamento -->
        <div class="details-container">
          <!-- Imagem (clicável para expandir) -->
          <div class="image-box" v-if="quote?.imageUrl">
            <img
              :src="quote.imageUrl"
              alt="Imagem da solicitação"
              class="clickable-image"
              @click="openImage(quote.imageUrl)"
              tabindex="0"
              @keyup.enter="openImage(quote.imageUrl)"
            />
          </div>

          <!-- Informações do Orçamento -->
          <div class="info-box main-info-box">
            <h3>Detalhes do Orçamento</h3>
            <ul>
              <li>
                <strong>Descrição:</strong>
                <span>{{ quote.description || '—' }}</span>
              </li>
              <li>
                <strong>Parte do Corpo:</strong>
                <span>{{ traduzirParteCorpo(quote.bodyPart) }}</span>
              </li>
              <li>
                <strong>Tamanho:</strong>
                <span>{{ traduzirTamanho(quote.size) }}</span>
              </li>
              <li>
                <strong>Colorido:</strong>
                <span>{{ quote.colored ? 'Sim' : 'Não' }}</span>
              </li>
            </ul>

            <hr class="separator"/>
            <h4>Valores</h4>
            <ul class="values-list">
              <li>
                <strong>Valor Estimado:</strong>
                <template v-if="!isEditing">{{ formatCurrency(quote.finalValue - (quote.additionalCost || 0)) }}</template>
                <template v-else>
                  <input type="number" step="0.01" v-model.number="edited.finalValue" class="edit-input"/>
                </template>
              </li>
              <li>
                <strong>Ajuste do Tatuador:</strong>
                <template v-if="!isEditing">{{ formatCurrency(quote.additionalCost) }}</template>
                <template v-else>
                  <input type="number" step="0.01" v-model.number="edited.additionalCost" class="edit-input"/>
                </template>
              </li>
              <li>
                <strong>Estado:</strong>
                <template v-if="!isEditing">{{ traduzirEstado(quote.state) }}</template>
                <template v-else>
                  <select v-model="edited.state" class="edit-input">
                    <option value="WAITING">Pendente</option>
                    <option value="AWNSERED">Respondido</option>
                    <option value="PAID">Pago</option>
                  </select>
                </template>
              </li>
              <li class="final-value-row">
                <strong>Valor Final:</strong>
                <span class="value-highlight final">{{ formatCurrency(edited.finalValue || quote.finalValue) }}</span>
              </li>
            </ul>
          </div>
        </div>

        <!-- Grid separado para informações do cliente (apenas leitura) -->
        <div class="details-container client-container">
          <div class="client-box">
            <h3>Informações do Cliente</h3>
            <ul>
              <li><strong>Nome:</strong> <span>{{ quote.clientName }}</span></li>
              <li><strong>Email:</strong> <span>{{ quote.clientEmail }}</span></li>
              <li><strong>Telefone:</strong> <span>{{ formatPhone(quote.clientPhone) }}</span></li>
            </ul>
          </div>
        </div>

        <!-- Ações de edição do orçamento -->
        <div class="account-actions" v-if="isAdmin">
          <div class="right-actions">
            <button v-if="!isEditing" class="btn outline" @click="startEdit">Editar <i class="fi fi-sr-pencil"></i></button>
            <button v-if="isEditing" class="btn outline" @click="saveEdit">Salvar</button>
            <button v-if="isEditing" class="btn secondary" @click="cancelEdit">Cancelar</button>
          </div>
        </div>
      </div>
    </section>

    <!-- Loading -->
    <div v-if="loading" class="loading">
      <div class="spinner" />
      <p>Carregando orçamento...</p>
    </div>

    <!-- Não encontrado -->
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

    <!-- MINI TOAST COM TRANSITION -->
    <transition name="toast">
      <div
        v-if="toastVisible"
        :class="['message-toast', toastType === 'success' ? 'success' : 'error']"
        role="status"
        aria-live="polite"
      >
        <div class="message-icon" aria-hidden="true">
          <i v-if="toastType === 'success'" class="fi fi-rr-check"></i>
          <i v-else class="fi fi-rr-exclamation"></i>
        </div>
        <div class="message-text">{{ toastMessage }}</div>
      </div>
    </transition>

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

const quote = ref(null);
const loading = ref(true);
const error = ref(null);
const notFound = ref(false);
const isEditing = ref(false);
const isAdmin = ref(true);

const edited = ref({
  finalValue: 0,
  additionalCost: 0,
  state: '',
});

const toastVisible = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
let _toastTimer = null;

// Lightbox (imagem expandida)
const expandedImage = ref(null);

function openImage(url) {
  expandedImage.value = url;
  // evita scroll atrás
  document.body.style.overflow = 'hidden';
}

function closeImage() {
  expandedImage.value = null;
  document.body.style.overflow = '';
}

function onKeyDown(e) {
  if (e.key === 'Escape' && expandedImage.value) {
    closeImage();
  }
}

// Formatação
function formatCurrency(value) {
  if (value == null || isNaN(Number(value))) return '—';
  return Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}
function traduzirEstado(estado) { const map = { WAITING: 'Pendente', AWNSERED: 'Respondido', PAID: 'Pago' }; return map[estado] || estado; }
function traduzirTamanho(tamanho) { const map = { SMALL: 'Pequeno', MEDIUM: 'Médio', LARGE: 'Grande' }; return map[tamanho] || tamanho; }
function traduzirParteCorpo(parte) { const map = { ARM: 'Braço', BACK: 'Costas', LEG: 'Perna', CHEST: 'Peito', RIB: 'Costela', NECK: 'Pescoço', HAND: 'Mão', HEAD: 'Cabeça', FOOT: 'Pé', OTHER: 'Outro' }; return map[parte] || parte; }
function formatPhone(phone) { if (!phone) return '—'; const digits = phone.replace(/\D/g, ''); if (digits.length === 11) return `(${digits.slice(0,2)}) ${digits.slice(2,7)}-${digits.slice(7)}`; if (digits.length === 10) return `(${digits.slice(0,2)}) ${digits.slice(2,6)}-${digits.slice(6)}`; return phone; }

// Navegação
function goBack() { router.back(); }

// Edição
function startEdit() {
  if (!quote.value) return;
  edited.value.finalValue = quote.value.finalValue;
  edited.value.additionalCost = quote.value.additionalCost;
  edited.value.state = quote.value.state;
  isEditing.value = true;
}

function cancelEdit() {
  if (!quote.value) return;
  edited.value.finalValue = quote.value.finalValue;
  edited.value.additionalCost = quote.value.additionalCost;
  edited.value.state = quote.value.state;
  isEditing.value = false;
}

async function saveEdit() {
  try {
    const payload = {
      finalValue: Number(edited.value.finalValue),
      additionalCost: Number(edited.value.additionalCost),
      state: edited.value.state,
    };

    await axios.put(`${import.meta.env.VITE_API_URL_BUD}/${quote.value.id}`, payload, {
      headers: { "Content-Type": "application/json" }
    });

    showToast('success', 'Orçamento atualizado com sucesso!');

    isEditing.value = false;
    setTimeout(() => {
      router.push("/budget-table");
    }, 600);

  } catch(err) {
    console.error(err);
    showToast('error', 'Erro ao atualizar orçamento.');
  }
}

// Toast
function showToast(type = 'success', message = '', duration = 2200) {
  toastType.value = type === 'error' ? 'error' : 'success';
  toastMessage.value = message || (type === 'error' ? 'Ocorreu um erro' : 'Operação concluída');
  toastVisible.value = true;

  if (_toastTimer) clearTimeout(_toastTimer);
  _toastTimer = setTimeout(() => {
    toastVisible.value = false;
    _toastTimer = null;
  }, duration);
}

// Busca orçamento
async function fetchQuote() {
  loading.value = true;
  const id = route.params.id;
  try {
    const { data } = await axios.get(`${import.meta.env.VITE_API_URL_BUD}/${id}`);
    if (!data) notFound.value = true;
    else quote.value = data;
  } catch(err) {
    console.error(err);
    error.value = 'Erro ao carregar orçamento.';
  } finally { loading.value = false; }
}

onMounted(() => {
  fetchQuote();
  window.addEventListener('keydown', onKeyDown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', onKeyDown);
  // garante restaurar overflow caso componente desmontado com lightbox aberto
  document.body.style.overflow = '';
});
</script>

<style scoped>
</style>