<template>
  <div id="app">
    <Navbar />

    <div class="chat-wrapper">
      <!-- Lista de clientes (admin) -->
      <div v-if="isAdmin && !selectedUser" class="admin-list">
        <h2>Escolha um cliente</h2>
        <div class="admin-search-row">
          <input
            v-model="clientSearch"
            type="search"
            placeholder="Pesquisar cliente por nome ou e-mail..."
            class="search-input"
            aria-label="Pesquisar cliente"
          />
        </div>

        <div
          v-for="user in filteredContacts"
          :key="user.name + '__' + (user.email || '')"
          class="user-card"
          @click="openChatWith(user.name)"
        >
          <div class="user-left">
            <div class="avatar">{{ user.name.charAt(0).toUpperCase() }}</div>
          </div>
          <div class="user-body">
            <div class="user-name">{{ user.name }}</div>
            <div class="user-email">{{ user.email || 'sem e-mail' }}</div>
          </div>
          <div class="user-right">
            <button class="open-btn" @click.stop="openChatWith(user.name)">Abrir</button>
          </div>
        </div>

        <div v-if="!filteredContacts.length" class="no-results">
          Nenhum cliente encontrado.
        </div>
      </div>

      <!-- Área de chat -->
      <div id="chat-area" v-show="selectedUser">
        <div id="chat-header">
          <button @click="closeChat">✕</button>
          <div id="chat-title">
            <h2 class="contact-name">{{ selectedContact?.name || selectedUser }}</h2>
            <div class="chat-email" v-if="selectedContact?.email">{{ selectedContact.email }}</div>
          </div>
        </div>

        <div id="message-list" ref="messageList">
          <!-- Barra de boas-vindas fixa -->
          <div class="welcome-bar">{{ welcomeMessage }}</div>

          <!-- Mensagens reais -->
          <div
            v-for="(message, index) in displayedMessages"
            :key="index"
            :class="['message', message.sender === username ? 'sent' : 'received']"
          >
            <div class="sender-name">
              {{ message.sender === username ? 'Você' : message.sender }}
            </div>
            <div class="bubble">{{ message.content }}</div>
          </div>
        </div>

        <div id="message-input">
          <input
            ref="messageInput"
            v-model="messageText"
            type="text"
            placeholder="Digite uma mensagem..."
            @keyup.enter="sendMessage"
          />
          <button @click="sendMessage">Enviar</button>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script>
import { Client } from "@stomp/stompjs";
import Navbar from "../components/global/NavBar.vue";
import Footer from "../components/global/Footer.vue";

export default {
  name: "ChatPage",
  components: { Navbar, Footer },
  data() {
    return {
      stompClient: null,
      username: localStorage.getItem("usuarioNome") || "",
      selectedUser: null,
      displayedMessages: [],
      chatMap: {},
      roomSubscriptions: {},
      adminName: "Rafael Borges",
      adminEmail: "rafaelmascarenhasborges@gmail.com",
      isAdmin: localStorage.getItem("usuarioEmail") === "rafaelmascarenhasborges@gmail.com",
      contacts: [],
      messageText: "",
      welcomeMessage:
        "Olá! Bem-vindo(a) ao chat! Use este espaço para tirar dúvidas e marcar horários.",
      clientSearch: "",
    };
  },
  mounted() {
    this.connect();
    if (!this.isAdmin) {
      setTimeout(() => {
        this.openChatWith(this.adminName);
      }, 200);
    }
  },
  beforeUnmount() {
    this.cleanup();
  },
  computed: {
    selectedContact() {
      return this.contacts.find((c) => c.name === this.selectedUser) || null;
    },
    filteredContacts() {
      if (!this.clientSearch.trim()) return this.contacts;
      const q = this.clientSearch.toLowerCase().trim();
      return this.contacts.filter(
        (c) =>
          c.name.toLowerCase().includes(q) ||
          (c.email && c.email.toLowerCase().includes(q))
      );
    },
  },
  methods: {
    cleanup() {
      Object.values(this.roomSubscriptions).forEach((s) => s?.unsubscribe?.());
      this.stompClient?.deactivate();
    },
    connect() {
      this.stompClient = new Client({
        webSocketFactory: () => new WebSocket("ws://localhost:8081/ws"),
        reconnectDelay: 5000,
      });

      this.stompClient.onConnect = () => {
        this.stompClient.publish({
          destination: "/app/chat.addUser",
          body: JSON.stringify({ sender: this.username }),
        });
        this.loadContacts();
      };

      this.stompClient.activate();
    },
    getChatKey(a, b) {
      return [a, b].map((x) => x.toLowerCase()).sort().join("-");
    },
	openChatWith(username) {
	  this.selectedUser = username;
	  const key = this.getChatKey(this.username, username);

	  if (!this.chatMap[key]) this.chatMap[key] = { messages: [] };

	  // Primeiro carrega histórico
	  fetch(`http://localhost:8081/mensagemchat/historico/${this.username}/${username}`)
	    .then((res) => res.json())
	    .then((history) => {
	      this.chatMap[key].messages = history.map((h) => ({
	        sender: h.idUsuarioRemetente,
	        recipient: h.idUsuarioDestinatario,
	        content: h.mensagem,
	      }));
	      this.displayedMessages = [...this.chatMap[key].messages];

	      // Depois que histórico carregou, se inscreve no STOMP
	      this.subscribeRoom(key);

	      this.$nextTick(() => this.scrollToBottom());
	    })
	    .catch((err) => console.error(err));
	},
    subscribeRoom(roomId) {
      if (this.roomSubscriptions[roomId]) return;
      const sub = this.stompClient.subscribe(`/topic/room/${roomId}`, (msg) => {
        const message = JSON.parse(msg.body);
        const key = this.getChatKey(message.sender, message.recipient);
        if (!this.chatMap[key]) this.chatMap[key] = { messages: [] };

        this.chatMap[key].messages.push(message);

        if (
          this.selectedUser === message.sender ||
          this.selectedUser === message.recipient
        ) {
          this.displayedMessages = [...this.chatMap[key].messages];
          this.$nextTick(() => this.scrollToBottom());
        }
      });
      this.roomSubscriptions[roomId] = sub;
    },
	closeChat() {
	  this.selectedUser = null;
	  this.displayedMessages = [];

	  if (!this.isAdmin) {
	    this.$router.push("/");
	  }
	},
	sendMessage() {
	  if (!this.messageText.trim()) return;

	  const msg = {
	    sender: this.username,
	    recipient: this.selectedUser,
	    content: this.messageText,
	  };

	  // Envia via WebSocket
	  this.stompClient.publish({
	    destination: "/app/chat.privateMessage",
	    body: JSON.stringify(msg),
	  });

	  // Salva no banco
	  fetch("http://localhost:8081/mensagemchat", {
	    method: "POST",
	    headers: { "Content-Type": "application/json" },
	    body: JSON.stringify({
	      idUsuarioRemetente: msg.sender,
	      idUsuarioDestinatario: msg.recipient,
	      mensagem: msg.content,
	    }),
	  }).catch((err) => console.error("Erro ao salvar mensagem:", err));

	  // Limpa input
	  this.messageText = "";
	  this.$nextTick(() => this.scrollToBottom());
	},
    loadContacts() {
      fetch("http://localhost:8081/clients")
        .then((res) => res.json())
        .then((data) => {
          this.contacts = data
            .map((c) => ({ name: c.name || "", email: c.email || "" }))
            .filter(
              (c) => c.name && c.name !== this.username && c.name !== this.adminName
            );
        });
    },
  },
};
</script>

<style lang="scss">
@import "../assets/Scss/pages/chat.scss";

.welcome-bar {
  background-color: rgb(255, 255, 255);
  color: rgb(0, 0, 0);
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  font-weight: bold;
  margin-bottom: 12px;
}

.message-list {
  max-height: 400px;
  overflow-y: auto;
}

.contact-name {
  color: white;
}
</style>
