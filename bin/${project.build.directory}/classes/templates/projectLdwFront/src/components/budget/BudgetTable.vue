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
            <th v-for="col in columns" :key="col">{{ col }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(quote, i) in quotes" :key="i">
            <td v-for="col in columns" :key="col + '-' + i">
              <span v-html="formatCell(quote[col])"></span>
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

type Quote = Record<string, unknown>

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
  if (Array.isArray(data)) return data
  if (Array.isArray(data?.content)) return data.content as Quote[]
  if (data && typeof data === 'object') {
    const values = Object.values(data).filter(v => typeof v === 'object')
    return values.length
      ? values.map(v => ({ ...(v as object) } as Quote))
      : [{ ...(data as object) } as Quote]
  }
  return []
}

function escapeHtml(str: string): string {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

function formatCell(value: unknown): string {
  if (value == null) return '<span class="muted">—</span>'

  if (typeof value === 'string' && /^\d{4}-\d{2}-\d{2}T/.test(value)) {
    return escapeHtml(new Date(value).toLocaleString())
  }

  if (typeof value === 'number') {
    return escapeHtml(
      value.toLocaleString(undefined, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      })
    )
  }

  if (typeof value === 'object') {
    try {
      return escapeHtml(JSON.stringify(value))
    } catch {
      return escapeHtml(String(value))
    }
  }

  return escapeHtml(String(value))
}

const hasData = computed(() => quotes.value.length > 0)

const columns = computed(() => {
  if (!hasData.value) return []

  const first = quotes.value[0]
  const preferredOrder = [
    'id', 'client', 'name', 'description',
    'amount', 'price', 'total', 'status',
    'date', 'createdAt', 'updatedAt',
  ]

  const keys = Object.keys(first)
  return keys.sort((a, b) => {
    const ai = preferredOrder.indexOf(a)
    const bi = preferredOrder.indexOf(b)
    if (ai === -1 && bi === -1) return 0
    if (ai === -1) return 1
    if (bi === -1) return -1
    return ai - bi
  })
})

onMounted(fetchQuotes)
</script>

<style scoped>
@import '../../assets/budget/BudgetTable.scss'
</style>
