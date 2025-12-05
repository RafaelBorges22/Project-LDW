<template>
  <section class="quote-details" v-if="quote">
    <header class="header">
      <button class="back-btn" @click="$router.push('/quotes')">← Voltar</button>
      <h2>Detalhes da Solicitação #{{ quote.id }}</h2>
    </header>

    <div class="details-container">

      <!-- Imagem -->
      <div class="image-box" v-if="quote.imageUrl">
        <img :src="quote.imageUrl" alt="Imagem da solicitação" />
      </div>

      <!-- Informações -->
      <div class="info-box">
        <h3>Informações da Solicitação</h3>
        <ul>
          <li><strong>Descrição:</strong> {{ quote.description }}</li>
          <li><strong>Tamanho:</strong> {{ quote.size }}</li>
          <li><strong>Parte do corpo:</strong> {{ quote.bodyPart }}</li>
          <li><strong>Colorido:</strong> {{ quote.colored ? "Sim" : "Não" }}</li>

          <!-- EDITAR: Valor adicional -->
          <li>
            <strong>Valor adicional:</strong>
            <input
            type="number"
            v-model.number="edited.additionalCost"
          />

          </li>

          <!-- EDITAR: Valor final -->
          <li>
            <strong>Valor final (R$):</strong>
            <input
              type="number"
              step="0.01"
              v-model="edited.finalValue"
              class="edit-input"
            />
          </li>

          <!-- EDITAR: Estado -->
          <li>
            <strong>Estado:</strong>
            <select v-model="edited.state" class="edit-input">
              <option value="WAITING">Aguardando</option>
              <option value="AWNSERED">Respondido</option>
              <option value="PAID">Pago</option>
            </select>
          </li>
        </ul>

        <!-- Botão SALVAR -->
        <button class="save-btn" @click="updateQuote">
          Salvar alterações
        </button>
      </div>

      <!-- Informações do Cliente -->
      <div class="client-box">
        <h3>Informações do Cliente</h3>
        <ul>
          <li><strong>Nome:</strong> {{ quote.clientName }}</li>
          <li><strong>Email:</strong> {{ quote.clientEmail }}</li>
        </ul>
      </div>
    </div>
  </section>

  <div v-else class="loading">
    <p>Carregando detalhes da solicitação...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

const route = useRoute();
const quote = ref(null);

// Campos editáveis
const edited = ref({
  finalValue: null,
  additionalCost: null,
  state: null,
});

// Formatar moeda
function formatMoney(value) {
  return Number(value).toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
  });
}

// Carregar os dados do orçamento
onMounted(async () => {
  const id = route.params.id;

  try {
    const { data } = await axios.get(`http://localhost:8081/quotes/${id}`);
    quote.value = data;

    // Preencher dados editáveis
    edited.value.finalValue = data.finalValue;
    edited.value.additionalCost = data.additionalCost;
    edited.value.state = data.state;
  } catch (err) {
    console.error("Erro ao carregar solicitação:", err);
  }
});

// Atualizar orçamento
async function updateQuote() {
  try {
    const payload = {
      finalValue:
        edited.value.finalValue !== "" && edited.value.finalValue !== null
          ? Number(edited.value.finalValue)
          : null,

      additionalCost:
        edited.value.additionalCost !== "" && edited.value.additionalCost !== null
          ? Number(edited.value.additionalCost)
          : null,

      state: edited.value.state,
    };

    await axios.put(`${import.meta.env.VITE_API_URL_BUD}/${quote.value.id}`, payload, {
      headers: { "Content-Type": "application/json" },
    });

    alert("Atualizado com sucesso!");

  } catch (err) {
    console.error("Erro ao atualizar:", err);
    alert("Falha ao atualizar.");
  }
}

</script>

<style scoped>
@import "../../assets/budget/BudgetDetails.scss";

/* Inputs */
.edit-input {
  padding: 4px 8px;
  margin-left: 10px;
  border-radius: 6px;
  border: 1px solid #b2b2b2;
}

/* Botão de salvar */
.save-btn {
  margin-top: 15px;
  padding: 10px 14px;
  background: #1e88e5;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
}

.save-btn:hover {
  background: #1565c0;
}
</style>

