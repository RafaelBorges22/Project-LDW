<template>
  <div id="app">
    <Navbar />

    <section class="budget-table budget-table-client">
      <div class="header-section">
        <div class="title-block">
          <h3 class="table-title">Meus Orçamentos</h3>
          <p class="table-subtitle muted">Acompanhe o status dos seus pedidos e as propostas dos tatuadores.</p>
        </div>

        <div class="actions-block">
          <button class="btn primary new-quote-btn" @click="goToCreateQuote" :disabled="loading">Novo Orçamento</button>

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
          </div>
        </div>
      </div>

      <div v-if="hasData && !loading" class="stats-row">
        <div class="stat-card total-stat">
          <div class="stat-value">{{ quotes.length }}</div>
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
          <h4>{{ qSearch || stateFilter ? 'Nenhum orçamento encontrado' : 'Você ainda não tem orçamentos' }}</h4>
          <p class="muted">
            {{ qSearch || stateFilter ? 'Tente ajustar seus filtros ou pesquisa.' : 'Clique em "Novo Orçamento" para criar o primeiro.' }}
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
              :aria-label="`Ver detalhes do orçamento: ${q.description}`"
            >
              <td data-label="Orçamento" class="col-desc">
                <div class="desc">
                  <strong class="quote-title">{{ q.description || '—' }}</strong>
                  <div class="small-meta">
                    Tamanho: {{ translateSize(q.raw?.size) }} • Parte do Corpo: {{ translateBodyPart(q.raw?.bodyPart) }}
                    <span v-if="q.raw?.isColored"> • Colorida</span>
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
                <span class="final-value highlight-final">
                  {{ q.finalValue != null ? formatCurrency(q.finalValue) : '—' }}
                </span>
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
    O <em>Valor Estimado</em> é calculado pelo sistema. Dependendo da complexidade, o tatuador pode ajustar esse valor, aumentando ou dando descontos, mostrado em <em>Ajuste Tatuador</em>. 
    O <em>Valor Final</em> só aparece quando o orçamento é respondido (não <strong>Pendente</strong>).
  </span>
      </div>

    </section>

    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
import Navbar from '../global/NavBar.vue';
import Footer from '../global/Footer.vue';

const router = useRouter();
const quotes = ref([]);
const loading = ref(false);
const error = ref(null);
const id = ref(null);

const API_URL_CLI = import.meta.env.VITE_API_URL_CLI;
const API_URL_BUD = import.meta.env.VITE_API_URL_BUD;

const qSearch = ref("");
const stateFilter = ref("");

// ---------------- Token helpers ----------------
function getTokenSafe() {
  const raw = localStorage.getItem("jwtToken");
  if (!raw) return null;
  const t = String(raw).trim();
  if (!t || t.toLowerCase() === "null" || t.toLowerCase() === "undefined") return null;
  return t;
}
function decodeJwt(token) {
  try {
    const payload = token.split(".")[1];
    return JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
  } catch {
    return null;
  }
}
function getEmailFromToken() {
  const token = getTokenSafe();
  if (!token) return null;
  const p = decodeJwt(token);
  return p?.sub || p?.email || null;
}

// ---------------- Parsing robusto ----------------
function toNumber(v) {
  if (v == null) return null;
  if (typeof v === "number" && !isNaN(v)) return v;
  if (typeof v === "string") {
    let s = v.trim();
    if (s === "") return null;
    s = s.replace(/[^\d.,\-+]/g, "");
    const hasDot = s.includes(".");
    const hasComma = s.includes(",");
    if (hasComma && hasDot) s = s.replace(/\./g, "").replace(",", ".");
    else if (hasComma && !hasDot) s = s.replace(",", ".");
    const n = parseFloat(s);
    return isNaN(n) ? null : n;
  }
  return null;
}

// ---------------- Helpers para tradução de Enums ----------------
function translateSize(s) { if(!s) return "—"; const n=String(s).toUpperCase(); return n==="SMALL"?"Pequeno":n==="MEDIUM"?"Médio":n==="LARGE"?"Grande":s; }
function translateBodyPart(s) { if(!s) return "—"; const n=String(s).toUpperCase(); switch(n){case "ARM":return "Braço";case "BACK":return "Costas";case "LEG":return "Perna";case "CHEST":return "Peito";case "RIB":return "Costela";case "NECK":return "Pescoço";case "HAND":return "Mão";case "HEAD":return "Cabeça";case "FOOT":return "Pé";case "OTHER":return "Outra";default:return String(s);} }
function isPendingState(s){return String(s).toUpperCase()==="WAITING";}
function translateState(s){if(!s)return "—";const n=String(s).toUpperCase();if(n==="WAITING")return "Pendente";if(n==="AWNSERED")return "Respondido";if(n==="PAID")return "Pago";return String(s);}
function statusClass(s){const t=String(translateState(s)).toLowerCase();if(t.includes("pendente"))return "status-pending";if(t.includes("pago"))return "status-paid";return "";}

// ---------------- Fetch ----------------
async function fetchClientId(email){const res=await axios.get(`${API_URL_CLI}/email/${encodeURIComponent(email)}`);return res.data.id;}
async function fetchQuotes(){
  if(!id.value) return;
  loading.value = true; error.value = null;
  try{
    const res=await axios.get(`${API_URL_BUD}/${id.value}/history`,{headers:{Authorization:`Bearer ${getTokenSafe()}`}});

    const data=Array.isArray(res.data)?res.data:(res.data?.content||(res.data?[res.data]:[]));
    quotes.value = data.map(q => {
  const additional = q.additionalCost != null ? toNumber(q.additionalCost) : null;
  const backendFinal = q.finalValue != null ? toNumber(q.finalValue) : null;

  let estimated =
    q.estimatedValue ??
    q.initialValue ??
    q.estimated ??
    q.calculatedValue ??
    q.basePrice ??
    null;
  let finalValue = backendFinal;

  if (finalValue == null && estimated != null && additional != null) {
    finalValue = Number((estimated + additional).toFixed(2));
  }

  return {
    id: q.id || q._id || null,
    description: q.description || "Sem descrição",
    state: q.state,
    initialValue: estimated != null ? Number(estimated) : null,
    adjustment: additional != null ? Number(additional) : null,
    finalValue: finalValue != null ? Number(finalValue) : null,
    raw: q
  };
});

  }catch(err){console.error(err);error.value=err?.response?.data?.message||err?.message||"Erro ao carregar histórico.";quotes.value=[];}
  finally{loading.value=false;}
}

// ---------------- Helpers UI ----------------
function formatCurrency(value){return value!=null?Number(value).toLocaleString("pt-BR",{style:"currency",currency:"BRL"}):"—";}
function formatSignedCurrency(value){if(value==null||isNaN(Number(value)))return "—";const n=Number(value);if(n===0)return formatCurrency(0);const sign=n>0?"+":"-";return `${sign}${formatCurrency(Math.abs(n))}`;}

// ---------------- Navigation ----------------
function goToQuoteDetails(quoteId){if(!quoteId) return;router.push({name:"quote-details-cl",params:{id:quoteId}});}
function goToCreateQuote(){router.push({name:"Budget"});}

// ---------------- Search & filter ----------------
const filteredQuotes=computed(()=>{let list=quotes.value.slice();if(qSearch.value){const s=qSearch.value.toLowerCase();list=list.filter(q=>(q.description||"").toLowerCase().includes(s));}if(stateFilter.value){const f=String(stateFilter.value).toUpperCase();list=list.filter(q=>(String(q.state||"")).toUpperCase()===f);}return list;});
function onSearch(){}
function onFilterChange(){}
function countBy(label){const t=String(label||"").toLowerCase();return quotes.value.filter(q=>translateState(q.state).toLowerCase().includes(t)).length;}
const hasData=computed(()=>quotes.value.length>0);

// ---------------- Init ----------------
onMounted(async () => {
  const email = getEmailFromToken();
  if (!email) {
    error.value = "Token inválido.";
    return;
  }

  try {
    id.value = await fetchClientId(email);

    await fetchQuotes(); 
    if (router.currentRoute.value.query.refresh) {
      await fetchQuotes();
    }

  } catch {
    error.value = "Erro ao obter ID do cliente.";
  }
});

</script>

<style scoped>
@import "../../assets/Scss/budget/BudgetTable.scss";
</style>
