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
            <img src="../../assets/base/LogoFooter.png" alt="Upload Icon" class="upload-icon" />
            <span>BUSCAR IMAGEM</span>
          </div>
          
          <div v-if="imageUrl" class="image-preview">
            <img :src="imageUrl" alt="Pré-visualização da imagem" />
          </div>
        </div>
      </div>

      <button type="submit" class="submit-button">ENVIAR</button>

      <p class="form-note">O pedido será enviado para o tatuador, que confirmará disponibilidade e valores.</p>
  <div v-if="showSuccess" class="success-popup">
    <div class="popup-content">
      <img src="../../assets/base/LogoFooter.png" alt="Sucesso" class="popup-icon" />
      <h3>Orçamento enviado! 🎉</h3>
      <p>Obrigado por confiar no nosso trabalho.  
      Nosso tatuador vai analisar sua ideia e te responder logo logo! 🖤✨</p>

      <button @click="showSuccess = false" class="popup-button">
        Fechar
      </button>
  </div>
</div>

    </form>
  </div>
</template>
<script lang="js">
import axios from "axios";

const API_URL_EMAIL = import.meta.env.VITE_API_URL_CLI + '/email';
const API_URL = import.meta.env.VITE_API_URL_BUD;

export default {
  data() {
    return {
      form: {
        description: "",
        colored: false,
        bodyPart: "",
        size: ""
      },

      client: {
        id: null,
        name: "",
        email: ""
      },
      showSuccess: false,
      selectedFile: null,
      imageUrl: null
    };
  },

  async created() {
    const email = localStorage.getItem("usuarioEmail");

    if (!email) {
      console.error("Nenhum email encontrado no localStorage.");
      return;
    }

    try {
      const response = await axios.get(`${API_URL_EMAIL}/${email}`);

      this.client = {
        id: response.data.id,
        name: response.data.name,
        email: response.data.email
      };

      localStorage.setItem("client_data", JSON.stringify(this.client));

      console.log("CLIENTE CARREGADO:", this.client);

    } catch (error) {
      console.error("Erro ao buscar cliente por email:", error);
      alert("Erro ao carregar dados do cliente.");
    }
  },

  methods: {
    handleFileChange(event) {
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
      if (!this.client.id) {
        alert("Erro: clientId não encontrado. Faça login novamente.");
        return;
      }

      if (!this.selectedFile) {
        alert("É necessário anexar uma imagem.");
        return;
      }

      const quoteData = {
        clientId: this.client.id,
        colored: this.form.colored,
        description: this.form.description,
        size: this.form.size,
        bodyPart: this.form.bodyPart,
        state: "WAITING"
      };

      const formData = new FormData();
      formData.append("quote", new Blob([JSON.stringify(quoteData)], { type: "application/json" }));
      formData.append("image", this.selectedFile);

      try {
        const response = await axios.post(API_URL, formData, {
          headers: { "Content-Type": "multipart/form-data" }
        });

        console.log("ORÇAMENTO ENVIADO:", response.data);
        this.form.description = "";
        this.form.bodyPart = "";
        this.form.size = "";
        this.form.colored = false;
        this.selectedFile = null;
        this.imageUrl = null;
        document.getElementById("reference-image").value = "";
        this.showSuccess = true;
      } catch (err) {
        console.error("Erro ao enviar orçamento:", err);
      }
    }
  }
};
</script>


<style scoped>
@import '../../assets/Scss/pages/BudgetForm.scss';
@import '../../assets/Scss/global/Popup.scss';
</style>
