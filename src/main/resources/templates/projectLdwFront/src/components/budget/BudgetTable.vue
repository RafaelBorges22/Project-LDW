<template>
  <div id="app">
    <Navbar />

    <section class="budget-table budget-table-client">
      <div class="header-section">
        <div class="title-block">
          <h3 class="table-title">Orçamentos</h3>
          <p class="table-subtitle muted">Visualize todos os orçamentos cadastrados no sistema.</p>
        </div>

        <div class="actions-block">
          <div class="filters-row">
            <input
              v-model="qSearch"
              @input="onSearch"
              type="search"
              placeholder="Pesquisar descrição..."
              class="search-input"
              aria-label="Pesquisar orçamentos"
            />

            <select v-model="stateFilter" @change="onFilterChange" class="state-filter" aria-label="Filtrar por estado">
              <option value="">Todos os estados</option>
              <option value="WAITING">Pendente</option>
              <option value="AWNSERED">Respondido</option>
              <option value="PAID">Pago</option>
            </select>

            <input
              v-model="clientFilter"
              @input="onClientSearch"
              type="search"
              placeholder="Pesquisar nome ou e-mail"
              class="search-input"
              aria-label="Filtrar por cliente"
            />
          </div>
        </div>
      </div>

      <div v-if="hasData && !loading" class="stats-row">
        <div class="stat-card total-stat">
          <div class="stat-value">{{ filteredQuotes.length }}</div>
          <div class="stat-label">Total de Orçamentos</div>
        </div>
        <div class="stat-card pending-stat">
          <div class="stat-value">{{ countBy('Pendente') }}</div>
          <div class="stat-label">Pendentes</div>
        </div>
        <div class="stat-card paid-stat">
          <div class="stat-value">{{ countBy('Pago') }}</div>
          <div class="stat-label">Pagos</div>
        </div>
      </div>

      <div v-if="loading" class="table-placeholder">
        <div class="spinner" />
        <p>Carregando orçamentos...</p>
      </div>

      <div v-else-if="error" class="table-error">
        <p class="error-message">Erro ao carregar: {{ error }}</p>
        <div class="error-actions">
          <button class="retry-btn" @click="fetchQuotes" :disabled="loading">Tentar novamente</button>
        </div>
      </div>

      <div v-else-if="!filteredQuotes.length" class="no-data">
        <div class="empty-card">
          <h4>{{ qSearch || stateFilter || clientFilter ? 'Nenhum orçamento encontrado' : 'Não há orçamentos cadastrados' }}</h4>
          <p class="muted">
            {{ qSearch || stateFilter || clientFilter ? 'Tente ajustar seus filtros ou pesquisa.' : 'Orçamentos aparecerão aqui automaticamente.' }}
          </p>
        </div>
      </div>

      <div v-else class="table-container">
        <table class="styled-table">
          <thead>
            <tr>
              <th class="col-desc">Orçamento (Descrição & Detalhes)</th>
              <th class="col-state">Status</th>
              <th class="text-right col-initial">Valor Estimado</th>
              <th class="text-right col-adjust">Ajuste do Tatuador</th>
              <th class="text-right col-final">Valor Final</th>
              <th class="col-actions">Ações</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="(q, i) in filteredQuotes"
              :key="q.id || i"
              @click="goToQuoteDetails(q.id)"
              class="clickable-row"
              role="button"
            >
              <td data-label="Orçamento" class="col-desc">
                <div class="desc">
                  <strong class="quote-title">{{ q.description || '—' }}</strong>
                  <div class="small-meta">
                    Tamanho: {{ translateSize(q.raw?.size) }} • Parte do Corpo: {{ translateBodyPart(q.raw?.bodyPart) }}
                    <span v-if="q.raw?.isColored"> • Colorida</span>
                  </div>
                  <div class="small-meta" v-if="q.client">
                    Cliente: {{ q.client?.name || '—' }} • {{ q.client?.email || '—' }}
                  </div>
                </div>
              </td>

              <td data-label="Status" class="col-state">
                <span
                  class="status"
                  :class="statusClass(q.state)"
                  :title="translateState(q.state)"
                  @click.stop
                >
                  {{ translateState(q.state) }}
                </span>
              </td>

              <td data-label="Valor Estimado" class="text-right col-initial">
                <span v-if="q.initialValue != null">{{ formatCurrency(q.initialValue) }}</span>
                <span v-else class="muted">—</span>
              </td>

              <td data-label="Ajuste do Tatuador" class="text-right col-adjust">
                <template v-if="q.adjustment == null">
                  <span class="muted adjustment-await">Aguardando</span>
                </template>
                <template v-else>
                  <span :class="['adjustment', q.adjustment > 0 ? 'adjust-up' : q.adjustment < 0 ? 'adjust-down' : 'adjust-none']">
                    {{ formatSignedCurrency(q.adjustment) }}
                  </span>
                </template>
              </td>

              <td data-label="Valor Final" class="text-right col-final">
                <template v-if="q.computedFinal == null">
                  <span class="final-value-await">Aguardando revisão</span>
                </template>
                <template v-else>
                  <span class="final-value highlight-final">{{ formatCurrency(q.computedFinal) }}</span>
                </template>
              </td>

              <td data-label="Ações" class="col-actions">
                <button class="btn outline details-btn" @click.stop="goToQuoteDetails(q.id)">Ver Detalhes</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="hasData && !loading" class="explain-note" role="note" aria-live="polite">
        <strong>Nota:</strong>
        <span>
          O <em>Valor Estimado</em> é calculado pelo sistema. Dependendo da complexidade, o tatuador pode ajustar esse valor, aumentando ou dando descontos, mostrado em <em>Ajuste do Tatuador</em>. 
          O <em>Valor Final</em> só aparece quando o orçamento é respondido (não <strong>Pendente</strong>).
        </span>
      </div>
    </section>

    <Footer />
  </div>
</template>

<script lang="js" setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from "vue-router";
import Navbar from '../global/NavBar.vue';
import Footer from '../global/Footer.vue';

const router = useRouter();
const API_URL = import.meta.env.VITE_API_URL_BUD;

const quotes = ref([]);
const loading = ref(false);
const error = ref(null);

const qSearch = ref("");
const stateFilter = ref("");
const clientFilter = ref("");

// ---------------- Fetch ----------------
async function fetchQuotes() {
  loading.value = true;
  error.value = null;

  try {
    const res = await fetch(API_URL);
    if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
    const data = await res.json();
    const normalized = Array.isArray(data) ? data : data?.content || (data ? [data] : []);

    quotes.value = normalized.map(q => {
      const adjustment = Number(q.additionalCost) || 0;
      const backendFinal = Number(q.finalValue) || null;
      let initial = q.estimatedValue ?? q.initialValue ?? q.value ?? null;
      if (initial == null && backendFinal != null) initial = backendFinal - adjustment;

      const pending = String(q.state||"").toUpperCase() === "WAITING";
      let computedFinal = backendFinal != null ? backendFinal : null;
      if (computedFinal == null && initial != null && adjustment != null && !pending) computedFinal = initial + adjustment;
      if (pending) computedFinal = null;

      return {
        id: q.id || q._id || null,
        description: q.description || q.title || "Sem descrição",
        state: q.state || q.status || null,
        initialValue: initial,
        adjustment: adjustment,
        computedFinal: computedFinal,
        raw: q,
        client: {
          id: q.clientId || null,
          // É crucial que o endpoint de orçamentos retorne clientName e clientEmail
          name: q.clientName || null,
          email: q.clientEmail || null
        }
      };
    });
  } catch (err) {
    console.error(err);
    error.value = err?.message || "Erro desconhecido";
    quotes.value = [];
  } finally {
    loading.value = false;
  }
}

// ---------------- UI Helpers ----------------
function formatCurrency(value) {
  if (value == null) return '—';
  return Number(value).toLocaleString('pt-BR',{style:'currency',currency:'BRL'});
}
function formatSignedCurrency(value){
  if(value==null||isNaN(Number(value))) return '—';
  const n=Number(value);
  if(n===0) return formatCurrency(0);
  return (n>0?"+":"-")+formatCurrency(Math.abs(n));
}
function goToQuoteDetails(id){ if(id) router.push(`/quotes/${id}`); }
function translateSize(s){if(!s)return "—"; const n=String(s).toUpperCase(); return n==="SMALL"?"Pequeno":n==="MEDIUM"?"Médio":n==="LARGE"?"Grande":s;}
function translateBodyPart(s){if(!s)return "—"; const n=String(s).toUpperCase(); switch(n){case "ARM":return "Braço";case "BACK":return "Costas";case "LEG":return "Perna";case "CHEST":return "Peito";case "RIB":return "Costela";case "NECK":return "Pescoço";case "HAND":return "Mão";case "HEAD":return "Cabeça";case "FOOT":return "Pé";case "OTHER":return "Outra";default:return s;}}
function translateState(s){if(!s)return "—"; const n=String(s).toUpperCase(); if(n==="WAITING")return "Pendente"; if(n==="AWNSERED")return "Respondido"; if(n==="PAID")return "Pago"; return s;}
function statusClass(s){const t=String(translateState(s)).toLowerCase(); if(t.includes("pendente"))return "status-pending"; if(t.includes("pago"))return "status-paid"; return "";}

// ---------------- Filtros ----------------
const filteredQuotes = computed(() => {
  let list = quotes.value.slice();
  
  // 1. Filtro por Descrição
  if(qSearch.value) {
    list = list.filter(q => (q.description||"").toLowerCase().includes(qSearch.value.toLowerCase()));
  }

  // 2. Filtro por Estado
  if(stateFilter.value) {
    list = list.filter(q => String(q.state||"").toUpperCase() === stateFilter.value.toUpperCase());
  }

  // 3. Filtro por Cliente (Nome ou E-mail)
  if(clientFilter.value) {
    list = list.filter(q => {
      const val = clientFilter.value.toLowerCase();
      // Garante que 'name' e 'email' sejam strings vazias se forem null, prevenindo erros de .includes()
      const name = q.client?.name?.toLowerCase() || '';
      const email = q.client?.email?.toLowerCase() || '';
      return name.includes(val) || email.includes(val);
    });
  }

  return list;
});

function onSearch(){}
function onFilterChange(){}
function onClientSearch(){}
function countBy(label){return quotes.value.filter(q=>translateState(q.state).toLowerCase().includes(label.toLowerCase())).length;}
const hasData = computed(()=>quotes.value.length>0);

// ---------------- Init ----------------
onMounted(fetchQuotes);
</script>

<style scoped>
@import "../../assets/Scss/budget/BudgetTable.scss";
</style>