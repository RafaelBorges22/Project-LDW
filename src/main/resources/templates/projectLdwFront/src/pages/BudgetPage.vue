<template>
  <div id="app">
    <Navbar />

    <!-- FORMULÁRIO DE ORÇAMENTO -->
    <div class="form-container">
      <form class="budget-form" @submit.prevent="handleSubmit" enctype="multipart/form-data">
        
        <div class="form-row">
          <div class="form-group">
            <label for="description" class="form-label">
              DESCREVA SUA TATTOO <span class="required">*</span>
            </label>
            <input
              v-model="form.description"
              type="text"
              id="description"
              placeholder="Quero tatuar um Dragão cabreiro..."
              class="form-input"
              required
            />
          </div>

          <div class="form-group color-option">
            <label for="color" class="form-label">COLORIDA <span class="required">*</span></label>
            <div class="checkbox-wrapper">
              <input v-model="form.colored" type="checkbox" id="color" class="form-checkbox" />
              <span class="checkmark"></span>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="body-part" class="form-label">PARTE DO CORPO <span class="required">*</span></label>
            <select v-model="form.bodyPart" id="body-part" class="form-select" required>
              <option disabled value="">Selecione</option>
              <option value="ARM">BRAÇO</option>
              <option value="LEG">PERNA</option>
              <option value="BACK">COSTAS</option>
              <option value="CHEST">PEITO</option>
              <option value="RIB">COSTELA</option>
              <option value="NECK">PESCOÇO</option>
              <option value="HAND">MÃO</option>
              <option value="HEAD">CABEÇA</option>
              <option value="FOOT">PÉ</option>
              <option value="OTHER">OUTRO</option>
            </select>
          </div>

          <div class="form-group">
            <label for="size" class="form-label">TAMANHO <span class="required">*</span></label>
            <select v-model="form.size" id="size" class="form-select" required>
              <option disabled value="">Selecione</option>
              <option value="SMALL">Pequeno</option>
              <option value="MEDIUM">Médio</option>
              <option value="LARGE">Grande</option>
            </select>
          </div>
        </div>

        <div class="form-group upload-section">
          <label for="reference-image" class="form-label">IMAGEM DE REFERÊNCIA</label>

          <div class="upload-box">
            <input
              type="file"
              id="reference-image"
              class="file-input"
              @change="handleFileChange"
            />

            <div v-if="!imageUrl" class="upload-placeholder">
              <svg xmlns="http://www.w3.org/2000/svg" height="40px" viewBox="0 0 24 24" width="40px" fill="#5c3a33"><path d="M0 0h24v24H0V0z" fill="none"/><path d="M18 20H4V6h9V4H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2v-9h-2v9zm-7.79-3.17l-1.96-2.36L5.5 18h11l-3.54-4.71zM20 4V1h-2v3h-3c.01.01 0 2 0 2h3v2.99c.01.01 2 0 2 0V6h3V4h-3z"/></svg>
              <span>BUSCAR</span>
            </div>

            <div v-if="imageUrl" class="image-preview">
              <img :src="imageUrl" alt="Pré-visualização da imagem" />
            </div>
          </div>
        </div>

        <button type="submit" class="submit-button">ENVIAR</button>

        <p class="form-note">
          O pedido será enviado para o tatuador, que confirmará disponibilidade e valores.
        </p>
      </form>
    </div>

    <Footer />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import axios from 'axios';

import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';

export default defineComponent({
  name: 'BudgetPage',

  components: {
    Navbar,
    Footer
  },

  data() {
    return {
      form: {
        description: '',
        colored: false,
        bodyPart: '',
        size: ''
      },
      userId: 1, // depois substituímos pelo ID do usuário logado
      selectedFile: null,
      imageUrl: null
    };
  },

  methods: {
    handleFileChange(event: any) {
      if (this.imageUrl) URL.revokeObjectURL(this.imageUrl);

      const file = event.target.files[0];
      if (file) {
        this.selectedFile = file;
        this.imageUrl = URL.createObjectURL(file);
      } else {
        this.selectedFile = null;
        this.imageUrl = null;
      }
    },

    async handleSubmit() {
      if (!this.selectedFile) {
        alert('É necessário anexar uma imagem de referência.');
        return;
      }

      const formData = new FormData();

      const quoteData = {
        description: this.form.description,
        colored: this.form.colored,
        bodyPart: this.form.bodyPart,
        size: this.form.size
      };

      const quoteBlob = new Blob([JSON.stringify(quoteData)], {
        type: 'application/json'
      });

      formData.append('quote', quoteBlob);
      formData.append('image', this.selectedFile);

      try {
        const response = await axios.post(
          `http://localhost:8081/quotes/${this.userId}`,
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        );

        console.log('Resposta do servidor:', response.data);
      } catch (err) {
        console.error('Erro ao enviar o orçamento:', err);
      }
    }
  },

  beforeUnmount() {
    if (this.imageUrl) URL.revokeObjectURL(this.imageUrl);
  }
});
</script>

<style scoped>
@import '../assets/Scss/pages/BudgetForm.scss';
</style>
