<template>
  <div class="form-container">
    <form class="budget-form" @submit.prevent="handleSubmit" enctype="multipart/form-data">
      <div class="form-row">
        <div class="form-group">
          <label for="description" class="form-label">DESCREVA SUA TATTOO <span class="required">*</span></label>
          <input v-model="form.description" type="text" id="description" placeholder="Quero tatuar um Dragão cabreiro..." class="form-input" required />
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
          <input type="file" id="reference-image" class="file-input" @change="handleFileChange" />
          
          <div v-if="!imageUrl" class="upload-placeholder">
            <img src="../../../public/images/icons/upload_icon.png" alt="Upload Icon" class="upload-icon" />
            <span>BUSCAR</span>
          </div>
          
          <div v-if="imageUrl" class="image-preview">
            <img :src="imageUrl" alt="Pré-visualização da imagem" />
          </div>
        </div>
      </div>

      <button type="submit" class="submit-button">ENVIAR</button>

      <p class="form-note">O pedido será enviado para o tatuador, que confirmará disponibilidade e valores.</p>
    </form>
  </div>
</template>
<script lang="js">
import axios from 'axios';

export default {
  data() {
    return {
      form: {
        description: '',
        colored: false,
        bodyPart: '',
        size: ''
      },
      userId: 1,
      selectedFile: null,
      imageUrl: null 
    };
  },

  methods: {
    handleFileChange(event) {
      if (this.imageUrl) {
        URL.revokeObjectURL(this.imageUrl);
      }
      
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
      const formData = new FormData();
      const quoteData = {
        description: this.form.description,
        colored: this.form.colored,
        bodyPart: this.form.bodyPart,
        size: this.form.size,
      };

      const quoteBlob = new Blob([JSON.stringify(quoteData)], { type: 'application/json' });
      formData.append('quote', quoteBlob);

      if (this.selectedFile) {
        formData.append('image', this.selectedFile);
      } else {
        alert('É necessário anexar uma imagem de referência.');
        return;
      }

      try {
        const clientId = this.userId;
        const response = await axios.post(`http://localhost:8081/quotes/${clientId}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        console.log('Resposta do servidor:', response.data);
      } catch (err) {
        console.error('Erro ao enviar o orçamento:', err);
        console.log('Dados do formulário:', quoteData);
      }
    }
  },

  beforeDestroy() {
    if (this.imageUrl) {
      URL.revokeObjectURL(this.imageUrl);
    }
  }
};
</script>

<style scoped>
@import '../../assets/budget/BudgetForm.scss';
</style>
