<template>
  <section class="budget-table">
    <h3 class="table-title">Orçamentos</h3>

    <!-- Estado: carregando -->
    <div v-if="loading" class="table-placeholder">
      <div class="spinner" />
      <p>Carregando orçamentos...</p>
    </div>

    <!-- Estado: erro -->
    <div v-else-if="error" class="table-error">
      <p>Erro ao carregar: {{ error }}</p>
      <button class="retry-btn" @click="fetchQuotes">Tentar novamente</button>
    </div>

    <!-- Estado: sucesso -->
    <div v-else>
      <table v-if="hasData" class="table">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Tamanho</th>
            <th>Parte do Corpo</th>
            <th>Custo Adicional</th>
            <th>Contato</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(quote, i) in quotes" :key="i"
          @click="goToQuoteDetails(quote.id)" class="clickable-row">
            <td><strong>{{ quote.client.name || '—' }}</strong></td>
            <td>{{ quote.size || '—' }}</td>
            <td>{{ quote.bodyPart || '—' }}</td>
            <td>{{ formatCurrency(quote.additionalCost) }}</td>
            <td>
              <div v-if="quote.client" class="client-info">
                <p class="muted">{{ quote.client.email || '—' }}</p>
                <p>{{ quote.client.phone || '—' }}</p>
              </div>
              <span v-else class="muted">Sem cliente</span>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="no-data">Nenhum orçamento encontrado.</div>
    </div>
  </section>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from "vue-router";
const router = useRouter();

type Client = {
  name?: string
  email?: string
  phone?: string
}

type Quote = {
  id?: string | number
  name?: string
  size?: string
  bodyPart?: string
  additionalCost?: number
  client?: Client
}

const API_URL = 'http://localhost:8081/quotes'

const quotes = ref<Quote[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

async function fetchQuotes() {
  loading.value = true
  error.value = null
  try {
    const response = await fetch(API_URL)
    if (!response.ok) throw new Error(`${response.status} ${response.statusText}`)
    const data = await response.json()
    quotes.value = normalizeQuotes(data)
  } catch (err: any) {
    error.value = err?.message || 'Erro desconhecido'
    quotes.value = []
  } finally {
    loading.value = false
  }
}

function normalizeQuotes(data: any): Quote[] {
  // tenta lidar com diferentes formatos de retorno da API
  if (Array.isArray(data)) return data
  if (Array.isArray(data?.content)) return data.content as Quote[]
  if (data && typeof data === 'object') return [data as Quote]
  return []
}

function formatCurrency(value: any): string {
  if (value == null || isNaN(value)) return '—'
  return Number(value).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  })
}

function goToQuoteDetails(id) {
  router.push(`/quotes/${id}`);
}

const hasData = computed(() => quotes.value.length > 0)

onMounted(fetchQuotes)
</script>

<style scoped>
@import '../../assets/budget/BudgetTable.scss';
</style>
