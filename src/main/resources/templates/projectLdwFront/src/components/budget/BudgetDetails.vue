<template>
  <section class="quote-details" v-if="quote">
    <header class="header">
      <button class="back-btn" @click="$router.push('/quotes')">← Voltar</button>
      <h2>Detalhes da Solicitação #{{ quote.id }}</h2>
    </header>

    <div class="details-container">
      <div class="image-box" v-if="quote.imageUrl">
        <img :src="quote.imageUrl" alt="Imagem da solicitação" />
      </div>

      <div class="info-box">
        <h3>Informações da Solicitação</h3>
        <ul>
          <li><strong>Descrição:</strong> {{ quote.description }}</li>
          <li><strong>Tamanho:</strong> {{ quote.size }}</li>
          <li><strong>Parte do corpo:</strong> {{ quote.bodyPart }}</li>
          <li><strong>Colorido:</strong> {{ quote.colored ? "Sim" : "Não" }}</li>
          <li><strong>Valor adicional:</strong> {{ quote.additionalCost ?? "—" }}</li>
          <li><strong>Valor final:</strong> R$ {{ formatMoney(quote.finalValue) }}</li>
          <li><strong>Estado:</strong> {{ quote.state ?? "—" }}</li>
        </ul>
      </div>

      <div class="client-box" v-if="quote.client">
        <h3>Informações do Cliente</h3>
        <ul>
          <li><strong>Nome:</strong> {{ quote.client.name }}</li>
          <li><strong>Email:</strong> {{ quote.client.email }}</li>
          <li><strong>Telefone:</strong> {{ quote.client.phone }}</li>
          <li><strong>Endereço:</strong> {{ quote.client.address }}</li>
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

function formatMoney(value) {
  return Number(value).toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
  });
}

onMounted(async () => {
  const id = route.params.id;
  try {
    const { data } = await axios.get(`http://localhost:8081/quotes/${id}`);
    quote.value = data;
  } catch (err) {
    console.error("Erro ao carregar solicitação:", err);
  }
});
</script>
<style scoped>
@import "../../assets/budget/BudgetDetails.scss";
</style>