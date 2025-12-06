<template>
  <div id="app">
    <Navbar />

    <main class="page-content">
      <section class="budget-table">
        <div class="header-row">
          <h3 class="table-title">Meus Orçamentos</h3>

          <div class="header-actions">
            <button class="btn primary" @click="onCreateClick">Fazer Orçamento</button>
          </div>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="table-placeholder">
          <div class="spinner" />
          <p>Carregando seus orçamentos...</p>
        </div>

        <!-- Error -->
        <div v-else-if="error" class="table-error">
          <p>Erro ao carregar: {{ error }}</p>
          <div class="error-actions">
            <button class="retry-btn" @click="fetchQuotes">Tentar novamente</button>
            <button class="btn outline" @click="onCreateClick">Fazer Orçamento</button>
          </div>
        </div>

        <!-- Success with data -->
        <div v-else-if="hasData">
          <table class="styled-table" aria-live="polite">
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
                :key="quote.id || i"
                class="clickable-row"
                @click="goToQuoteDetails(quote.id)"
              >
                <td data-label="Nome"><strong>{{ quote.clientName || quote.client?.name || '—' }}</strong></td>
                <td data-label="Tamanho">{{ quote.size || '—' }}</td>
                <td data-label="Parte do Corpo">{{ humanizeBodyPart(quote.bodyPart) || '—' }}</td>
                <td data-label="Valor Final">{{ formatCurrency(calcFinalValue(quote)) }}</td>
                <td data-label="Contato">
                  <div class="client-info">
                    <p class="muted">{{ quote.clientEmail || quote.client?.email || '—' }}</p>
                    <p>{{ formatPhone(quote.client?.phone || quote.clientPhone || quote.phone) }}</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="no-data">
          <div class="empty-card">
            <svg class="empty-icon" viewBox="0 0 24 24" aria-hidden="true"><path fill="currentColor" d="M12 2a7 7 0 0 0-7 7v3a3 3 0 0 0 3 3h1v2a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2v-2h1a3 3 0 0 0 3-3V9a7 7 0 0 0-7-7z"/></svg>
            <h4>Você ainda não tem orçamentos</h4>
            <p class="muted">Faça um orçamento para que eu possa avaliar e retornar com valor e disponibilidade.</p>
            <div class="empty-actions">
              <button class="btn primary" @click="onCreateClick">Fazer Orçamento</button>
            </div>
          </div>
        </div>
      </section>
    </main>

    <Footer />
  </div>
</template>

<script>
import Navbar from '../global/NavBar.vue';
import Footer from '../global/Footer.vue';
const BASE = import.meta.env.VITE_API_URL_BUD || '/quotes';

export default {
  name: 'BudgetTablePage',
  components: { Navbar, Footer },

  data() {
    return {
      BASE: BASE,
      quotes: [],
      loading: true,
      error: null,
      userId: null,
      userEmail: null
    };
  },

  computed: {
    hasData() {
      return Array.isArray(this.quotes) && this.quotes.length > 0;
    }
  },

  mounted() {
    this.fetchQuotes();
  },

  methods: {
    isLoggedIn() {
      const raw = localStorage.getItem('jwtToken');
      return !!(raw && String(raw).trim() && raw.toLowerCase() !== 'null' && raw.toLowerCase() !== 'undefined');
    },

    async fetchQuotes() {
  loading.value = true;
  error.value = null;
  quotes.value = [];

  // lê identificadores locais (trim para evitar espaços)
  userId.value = (localStorage.getItem('usuarioId') || '').trim() || null;
  userEmail.value = (localStorage.getItem('usuarioEmail') || '').trim() || null;

  console.debug('[BudgetTable] userId from localStorage:', userId.value);
  console.debug('[BudgetTable] userEmail from localStorage:', userEmail.value);

  const tryUrls = [];

  // 1) tenta endpoint explícito que o backend tem: /quotes/{clientId}/history
  if (userId.value) tryUrls.push(`${BASE}/${encodeURIComponent(userId.value)}/history`);

  // 2) tentativas adicionais (fallbacks)
  if (userId.value) {
    tryUrls.push(`${BASE}/client/${encodeURIComponent(userId.value)}`);
    tryUrls.push(`${BASE}/clients/${encodeURIComponent(userId.value)}`);
    tryUrls.push(`${BASE}?clientId=${encodeURIComponent(userId.value)}`);
    tryUrls.push(`${BASE}/user/${encodeURIComponent(userId.value)}`);
  }

  if (userEmail.value) {
    tryUrls.push(`${BASE}?email=${encodeURIComponent(userEmail.value)}`);
    tryUrls.push(`${BASE}/by-email/${encodeURIComponent(userEmail.value)}`);
  }

  // finally, lista geral
  tryUrls.push(`${BASE}`);
  tryUrls.push(`${BASE}/all`);

  // função utilitária para fetch com/sem credentials - retorna array | null
  const doFetch = async (url, useCredentials = false) => {
    try {
      console.debug('[BudgetTable] tentando URL:', url, 'credentials=', useCredentials);
      const opts = useCredentials ? { credentials: 'include' } : {};
      const res = await fetch(url, opts);
      if (!res.ok) throw new Error(`HTTP ${res.status} ${res.statusText}`);
      const data = await res.json();
      // normaliza em array
      if (Array.isArray(data)) return data;
      if (Array.isArray(data?.content)) return data.content;
      if (Array.isArray(data?.items)) return data.items;
      if (data && typeof data === 'object' && (data.quotes || data.items)) return data.quotes || data.items;
      if (data && typeof data === 'object' && (data.id || data.client)) return [data];
      return [];
    } catch (err) {
      console.warn('[BudgetTable] fetch falhou:', url, err);
      return null;
    }
  };

  // 1ª passagem: tenta each URL sem credentials
  let resArr = null;
  for (const url of tryUrls) {
    resArr = await doFetch(url, false);
    if (resArr !== null) {
      // aceita mesmo array vazio (endpoint correto, sem dados)
      quotes.value = resArr;
      break;
    }
  }

  // 2ª passagem: se tudo deu null (problema CORS ou cookies), tenta novamente com credentials
  if ((resArr === null || resArr === undefined) && quotes.value.length === 0) {
    for (const url of tryUrls) {
      resArr = await doFetch(url, true);
      if (resArr !== null) {
        quotes.value = resArr;
        break;
      }
    }
  }

  // Se ainda sem id e sem resultados, tente obter id via /clients/email/{email}
  if ((!userId.value || quotes.value.length === 0) && userEmail.value) {
    try {
      console.debug('[BudgetTable] tentando recuperar cliente por email para obter id...');
      const res = await fetch(`${BASE.replace(/\/+$/,'')}/../clients/email/${encodeURIComponent(userEmail.value)}`);
      // Observação: usando caminho relativo hacky se BASE for /quotes — ajuste se necessário
      if (res.ok) {
        const clientObj = await res.json();
        const gotId = clientObj?.id || clientObj?._id || clientObj?.uuid;
        if (gotId) {
          try { localStorage.setItem('usuarioId', String(gotId)); }
          catch (e) { console.warn('Não foi possível salvar usuarioId recuperado por email:', e); }
          userId.value = String(gotId);
          // agora tenta o endpoint correto com id
          const urlHistory = `${BASE}/${encodeURIComponent(userId.value)}/history`;
          const historyData = await doFetch(urlHistory, false) ?? await doFetch(urlHistory, true);
          if (historyData != null) quotes.value = historyData;
        }
      }
    } catch (err) {
      console.warn('[BudgetTable] falha ao buscar cliente por email para id:', err);
    }
  }

  // normaliza cada quote para o formato esperado
  if (Array.isArray(quotes.value)) {
    quotes.value = quotes.value.map(normalizeQuote);
  } else {
    quotes.value = [];
  }

  loading.value = false;
  if (!Array.isArray(quotes.value)) quotes.value = [];
  console.debug('[BudgetTable] final quotes count=', quotes.value.length);
},

    normalizeQuote(q) {
      if (!q) return {};
      return {
        id: q.id ?? q._id ?? q.quoteId ?? null,
        size: q.size ?? q.tamanho ?? null,
        bodyPart: q.bodyPart ?? q.part ?? q.body_part ?? null,
        colored: q.colored ?? q.colorido ?? false,
        additionalCost: Number(q.additionalCost ?? q.additional_cost ?? 0) || 0,
        finalValue: Number(q.finalValue ?? q.final_value ?? q.price ?? 0) || 0,
        client: q.client ?? q.user ?? null,
        clientName: q.client?.name ?? q.clientName ?? q.client_name ?? null,
        clientEmail: q.client?.email ?? q.clientEmail ?? q.client_email ?? null,
        clientPhone: q.client?.phone ?? q.clientPhone ?? q.client_phone ?? null,
        phone: q.phone ?? null
      };
    },

    calcFinalValue(q) {
      if (!q) return 0;
      const add = Number(q.additionalCost ?? q.additional_cost ?? 0) || 0;
      const base = Number(q.finalValue ?? q.final_value ?? q.price ?? 0) || 0;
      return add + base;
    },

    formatCurrency(value) {
      if (value == null || isNaN(Number(value))) return '—';
      return Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    },

    formatPhone(raw) {
      if (!raw) return '—';
      const digits = String(raw).replace(/\D/g, '');
      if (digits.length === 11) {
        return `(${digits.slice(0,2)}) ${digits.slice(2,7)}-${digits.slice(7)}`;
      }
      if (digits.length === 10) {
        return `(${digits.slice(0,2)}) ${digits.slice(2,6)}-${digits.slice(6)}`;
      }
      return raw;
    },

    humanizeBodyPart(code) {
      if (!code) return null;
      const map = {
        ARM: 'Braço',
        LEG: 'Perna',
        BACK: 'Costas',
        CHEST: 'Peito',
        RIB: 'Costela',
        NECK: 'Pescoço',
        HAND: 'Mão',
        HEAD: 'Cabeça',
        FOOT: 'Pé',
        OTHER: 'Outro',
        SMALL: 'Pequeno',
        MEDIUM: 'Médio',
        LARGE: 'Grande'
      };
      return map[String(code).toUpperCase()] ?? code;
    },

    goToQuoteDetails(id) {
      if (!id) return;
      this.$router.push(`/quotes/${id}`);
    },

    onCreateClick() {
      if (!this.isLoggedIn()) {
        try { localStorage.setItem('postLoginRedirect', '/budget'); } catch(e) {}
        this.$router.push('/login');
        return;
      }
      this.$router.push('/budget');
    }
  }
};
</script>

<style scoped>
@import "../../assets/Scss/budget/BudgetTable.scss";
</style>
