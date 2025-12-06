<template>
  <div id="app">
    <!-- Header global -->
    <Navbar />

    <!-- Conteúdo do chat -->
    <div class="chat-wrapper">
      <!-- Debug -->
      <div class="chat-debug">{{ debugMessage }}</div>

      <!-- Lista de clientes (admin) -->
      <div v-if="isAdmin && !selectedUser" class="admin-list">
        <h2>Escolha um cliente</h2>
        <div
          v-for="user in contacts"
          :key="user"
          class="user-card"
          @click="openChatWith(user)"
        >
          {{ user }}
        </div>
      </div>

      <!-- Área do chat -->
      <div v-if="selectedUser" id="chat-area">
        <!-- Header do chat -->
        <div id="chat-header">
          <button @click="closeChat">✕</button>
          <h2 id="chat-with-name">{{ selectedUser }}</h2>
        </div>

        <!-- Lista de mensagens -->
        <div id="message-list" ref="messageList">
          <div
            v-for="(message, index) in displayedMessages"
            :key="index"
            :class="['message', message.sender === username ? 'sent' : 'received']"
          >
            <div class="sender-name">
              {{ message.sender === username ? "Você" : message.sender }}
            </div>
            <div class="bubble">{{ message.content }}</div>
          </div>
        </div>

        <!-- Input de mensagem -->
        <div id="message-input">
          <input
            ref="messageInput"
            type="text"
            placeholder="Digite uma mensagem..."
            @keyup.enter="sendMessage"
          />
          <button @click="sendMessage">Enviar</button>
        </div>
      </div>
    </div>

    <!-- Footer global -->
    <Footer />
  </div>
</template>

<script>
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import Navbar from '../components/global/NavBar.vue';
import Footer from '../components/global/Footer.vue';

export default {
  name: "ChatPage",

  components: {
    Navbar,
    Footer,
  },

  data() {
    return {
      stompClient: null,
      username: null,
      selectedUser: null,
      displayedMessages: [],
      chatMap: {},
      roomSubscriptions: {},

      adminName: "Rafael Borges",
      adminEmail: "rafaelmascarenhasborges@gmail.com",
      isAdmin: false,
      contacts: [],

      debugMessage: "Conectando ao chat..."
    };
  },

  created() {
    this.username = localStorage.getItem("usuarioNome");
    const email = localStorage.getItem("usuarioEmail");
    this.isAdmin = email === this.adminEmail;
  },

  mounted() {
    this.connect();
  },

  methods: {
    connect() {
      this.debugMessage = "Abrindo conexão WebSocket...";

      // SockJS sem query param, username será enviado via publish
      const socket = new SockJS("http://localhost:8081/ws");

      this.stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000, // reconexão a cada 5s
        debug: (msg) => console.log("STOMP DEBUG:", msg),
      });

      this.stompClient.onConnect = () => {
        this.debugMessage = "Conectado ao chat!";
        console.log("STOMP conectado!");

        // Registrar usuário
        this.stompClient.publish({
          destination: "/app/chat.addUser",
          body: JSON.stringify({ sender: this.username }),
        });

        // Admin → receber lista de usuários
        if (this.isAdmin) {
          this.subscribeOnlineUsers();
          this.stompClient.publish({ destination: "/app/chat.getOnlineUsers", body: "{}" });
          return;
        }

        // Usuário normal → abrir chat com admin
        const roomId = this.getChatKey(this.username, this.adminName);
        if (!this.roomSubscriptions[roomId]) this.subscribeRoom(roomId);

        // Abrir chat e enviar mensagem de boas-vindas automática
        setTimeout(() => {
          this.openChatWith(this.adminName);
          this.sendAutomatedWelcome();
        }, 200);
      };

      this.stompClient.onStompError = (err) => {
        console.error("STOMP ERROR:", err);
        this.debugMessage = "Erro na conexão STOMP: " + JSON.stringify(err);
      };

      this.stompClient.activate();
    },

    async loadContacts() {
      try {
        this.debugMessage = "Carregando lista de clientes...";
        const res = await fetch("http://localhost:8081/clients");
        const data = await res.json();
        this.contacts = data.map(c => c.name).filter(n => n !== this.username);
        this.debugMessage = "Lista de clientes carregada!";
      } catch (err) {
        console.error("Erro ao carregar clientes:", err);
        this.debugMessage = "Erro ao carregar clientes: " + err.message;
      }
    },

    getChatKey(a, b) {
      return [a, b].sort().join("-");
    },

    subscribeRoom(roomId) {
      if (!this.stompClient || !this.stompClient.connected) {
        this.debugMessage = "STOMP ainda não conectado, não é possível assinar a sala.";
        return;
      }

      if (this.roomSubscriptions[roomId]) return;

      this.stompClient.subscribe("/topic/room/" + roomId, (msg) => {
        const message = JSON.parse(msg.body);
        const chatKey = this.getChatKey(message.sender, message.recipient);

        if (!this.chatMap[chatKey]) this.chatMap[chatKey] = [];
        this.chatMap[chatKey].push(message);

        if (this.selectedUser === message.sender || this.selectedUser === message.recipient) {
          this.appendMessage(message);
        }
      });

      this.roomSubscriptions[roomId] = true;
      this.debugMessage = "Assinando sala: " + roomId;
    },

    openChatWith(user) {
      this.selectedUser = user;
      this.displayedMessages = [];

      const roomId = this.getChatKey(this.username, user);
      this.subscribeRoom(roomId);

      const chatKey = this.getChatKey(this.username, user);
      if (this.chatMap[chatKey]) this.displayedMessages = [...this.chatMap[chatKey]];

      this.$nextTick(() => this.$refs.messageInput?.focus());
    },

    closeChat() {
      this.selectedUser = null;
      this.displayedMessages = [];
    },

    appendMessage(msg) {
      this.displayedMessages.push(msg);
      this.$nextTick(() => {
        this.$refs.messageList.scrollTop = this.$refs.messageList.scrollHeight;
      });
    },

    sendMessage() {
      if (!this.stompClient || !this.stompClient.connected) {
        this.debugMessage = "STOMP não conectado, não é possível enviar mensagem.";
        return;
      }

      const input = this.$refs.messageInput;
      const content = input.value.trim();
      if (!content) return;

      const msg = {
        sender: this.username,
        recipient: this.selectedUser,
        content,
        type: "CHAT",
      };

      this.stompClient.publish({
        destination: "/app/chat.privateMessage",
        body: JSON.stringify(msg),
      });

      const chatKey = this.getChatKey(this.username, this.selectedUser);
      if (!this.chatMap[chatKey]) this.chatMap[chatKey] = [];
      this.chatMap[chatKey].push(msg);

      this.appendMessage(msg);
      input.value = "";
    },

    sendAutomatedWelcome() {
      if (!this.selectedUser) return;

      const msg = {
        sender: this.adminName,
        recipient: this.username,
        content: "Boa tarde! Seja bem-vindo ao KazuTattoo. Se precisar de qualquer coisa, me manda uma mensagem, eu respondo assim que der! (essa mensagem foi automatizada)",
        type: "CHAT",
      };

      const chatKey = this.getChatKey(this.username, this.adminName);
      if (!this.chatMap[chatKey]) this.chatMap[chatKey] = [];
      this.chatMap[chatKey].push(msg);

      this.appendMessage(msg);
    },

    subscribeOnlineUsers() {
      if (!this.stompClient || !this.stompClient.connected) return;

      this.stompClient.subscribe("/topic/users", (msg) => {
        const users = JSON.parse(msg.body).filter(u => u !== this.username);
        this.contacts = users;
        this.debugMessage = "Lista de clientes atualizada!";
      });

      this.loadContacts();
    },
  },
};
</script>

<style scoped>
@import "../assets/Scss/pages/chat.scss";
</style>
