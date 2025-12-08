<template>
  <div class="form-container">
    <form class="budget-form" @submit.prevent="handleSubmit" enctype="multipart/form-data">

      <div class="form-header">
        <h2 class="title">SOLICITAR ORÇAMENTO DE TATUAGEM</h2>
        <p class="instruction">
          Descreva sua ideia em detalhes e inclua uma imagem de referência. Após o envio, o tatuador avaliará o pedido e entrará em contato com você via <strong>Mensagens</strong> para definir o valor e a disponibilidade.
        </p>
      </div>
      
      <hr class="separator"/>

      <div class="form-row">
        <div class="form-group">
          <label for="description" class="form-label">DESCREVA SUA TATTOO <span class="required">*</span></label>
          <input v-model="form.description" type="text" id="description" placeholder="Quero um dragão imponente em preto e cinza, com estilo..." class="form-input" required />
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
            <option value="ARM">Braço</option>
            <option value="LEG">Perna</option>
            <option value="BACK">Costas</option>
            <option value="CHEST">Peito</option>
            <option value="RIB">Costela</option>
            <option value="NECK">Pescoço</option>
            <option value="HAND">Mão</option>
            <option value="HEAD">Cabeça</option>
            <option value="FOOT">Pé</option>
            <option value="OTHER">Outro</option>
          </select>
        </div>

        <div class="form-group">
          <label for="size" class="form-label">TAMANHO (ESTIMADO) <span class="required">*</span></label>
          <select v-model="form.size" id="size" class="form-select" required>
            <option disabled value="">Selecione</option>
            <option value="SMALL">Pequeno (5 a 10 cm)</option>
            <option value="MEDIUM">Médio (10 a 20 cm)</option>
            <option value="LARGE">Grande (acima de 20 cm)</option>
          </select>
        </div>
      </div>

      <div class="form-group upload-section">
        <label for="reference-image" class="form-label">IMAGEM DE REFERÊNCIA <span class="required">*</span></label>
        <p class="upload-tip">Ajude o tatuador com uma imagem do estilo ou desenho desejado. (Obrigatório)</p>
        <div class="upload-box">
          <input type="file" id="reference-image" class="file-input" @change="handleFileChange" accept="image/*" />
          
          <div v-if="!imageUrl" class="upload-placeholder">
            <i class="fi fi-rr-cloud-upload upload-icon" aria-hidden="true"></i>
            <span>CLIQUE PARA BUSCAR IMAGEM</span>
          </div>
          
          <div v-if="imageUrl" class="image-preview">
            <img :src="imageUrl" alt="Pré-visualização da imagem" />
          </div>
        </div>
      </div>

      <button type="submit" class="submit-button" :disabled="isLoading" :aria-disabled="isLoading">ENVIAR ORÇAMENTO</button>
      <p class="form-note">Todos os campos marcados com <span class="required">*</span> são obrigatórios.</p>
    </form>
  </div>
  
  <div v-if="showSuccess" class="success-modal-backdrop" @click.self="showSuccess = false">
    <div class="success-modal-content">
      <div class="modal-header">
        <i class="fi fi-ss-check-circle modal-icon" aria-hidden="true"></i>
        <h3 class="modal-title">ORÇAMENTO ENVIADO COM SUCESSO!</h3>
      </div>
      <div class="modal-body">
        <p>Obrigado por confiar no nosso trabalho, <strong>{{ client.name }}</strong>!</p>
        <p>Nossa equipe já recebeu sua ideia e a imagem de referência.</p>
        <p>
          O tatuador irá analisar e te responder o mais breve possível. Fique de olho na aba
          <router-link to="/chat" class="link-chat">Mensagens</router-link> e
          <router-link :to="budgetLink" class="link-chat">Meus Orçamentos</router-link> para o retorno!
        </p>
      </div>
      <div class="modal-footer">
        <button @click="showSuccess = false" class="modal-button">
          FECHAR E CONTINUAR
        </button>
      </div>
    </div>
  </div>

  <div
    v-if="isLoading"
    class="global-loading-overlay"
    role="status"
    aria-live="polite"
    aria-label="Carregando"
  >
    <div class="global-loading-box">
      <div class="spinner" aria-hidden="true"></div>
      <div class="loading-text">{{ loadingMessage || 'Processando...' }}</div>
    </div>
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
      imageUrl: null,

      isLoading: false,
      loadingMessage: '',
      loadingAction: '' 
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

  computed: {
    budgetLink() {
      try {
        const raw = localStorage.getItem('jwtToken');
        if (!raw) return { name: 'BudgetTableCL' };

        const token = String(raw).trim();
        if (!token || token.toLowerCase() === 'null' || token.toLowerCase() === 'undefined') {
          return { name: 'BudgetTableCL' };
        }

        const parts = token.split('.');
        if (parts.length < 2) return { name: 'BudgetTableCL' };

        const payload = parts[1];

        let decoded = null;
        try {
          const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
          decoded = JSON.parse(json);
        } catch (e) {
          console.warn('Falha ao decodificar JWT no budgetLink:', e);
          return { name: 'BudgetTableCL' };
        }

        const roleValue = decoded?.role ?? decoded?.roles ?? decoded?.authorities ?? null;

        let roleStr = '';
        if (Array.isArray(roleValue)) {
          if (roleValue.length > 0 && typeof roleValue[0] === 'object' && roleValue[0] !== null) {
            roleStr = roleValue.map(r => r.authority || r.role || JSON.stringify(r)).join(',');
          } else {
            roleStr = roleValue.join(',');
          }
        } else {
          roleStr = String(roleValue || '');
        }

        if (roleStr.toUpperCase().includes('ADMIN')) {
          return { name: 'BudgetTable' };
        } else {
          return { name: 'BudgetTableCL' };
        }
      } catch (err) {
        console.error('Erro ao obter budgetLink:', err);
        return { name: 'BudgetTableCL' };
      }
    }
  },

  methods: {
    startLoading(action = '', message = '') {
      this.loadingAction = action;
      this.loadingMessage = message || '';
      this.isLoading = true;
    },
    stopLoading() {
      this.isLoading = false;
      this.loadingMessage = '';
      this.loadingAction = '';
    },

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
      
      // Validação adicionada, pois o upload agora é obrigatório
      if (!this.selectedFile) {
        alert("É necessário anexar uma imagem de referência.");
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

      this.startLoading('sendQuote', 'Enviando orçamento…');

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
      } finally {

        this.stopLoading();
      }
    }
  }
};
</script>

<style scoped>
@import '../../assets/Scss/pages/BudgetForm.scss';
</style>