<template>
  <div id="app">
    <Navbar />

    <div class="chat-wrapper">
      <div v-if="isAdmin && !selectedUser" class="admin-list">
        <h2>Escolha um cliente</h2>

        <div class="admin-search-row">
          <input
            v-model="clientSearch"
            @input="onClientSearch"
            type="search"
            placeholder="Pesquisar cliente por nome ou e-mail..."
            class="search-input"
            aria-label="Pesquisar cliente"
          />
        </div>

        <div v-for="user in filteredContacts" :key="user.email || user.name" class="user-card" @click="openChatWith(user.name)">
          <div class="user-left">
            <div class="avatar">{{ user.name ? user.name.charAt(0).toUpperCase() : '?' }}</div>
          </div>
          <div class="user-body">
            <div class="user-name">{{ user.name }}</div>
            <div class="user-email" v-if="user.email">{{ user.email }}</div>
            <div class="user-email" v-else>sem e-mail</div>
          </div>
          <div class="user-right">
            <button class="open-btn" @click.stop="openChatWith(user.name)">Abrir</button>
          </div>
        </div>

        <div v-if="!filteredContacts.length" class="no-results">
          Nenhum cliente encontrado.
        </div>
      </div>

      <div v-if="selectedUser" id="chat-area">
        <div id="chat-header">
          <button @click="closeChat">✕</button>
          <div id="chat-title">
            <h2 id="chat-with-name">{{ selectedContact?.name || selectedUser }}</h2>
            <div class="chat-email" v-if="selectedContact?.email">{{ selectedContact.email }}</div>
          </div>
        </div>

        <div id="message-list" ref="messageList">
          <div
            v-for="(message, index) in displayedMessages"
            :key="index"
            :class="['message', message.sender === username ? 'sent' : 'received']">
            <div class="sender-name">
              {{ message.sender === username ? 'Você' : message.sender }}
            </div>
            <div class="bubble">
              {{ message.content }}
              <div v-if="message.type === 'AUTO'" class="auto-tag">
                Mensagem automática — as próximas respostas serão humanas.
              </div>
            </div>
          </div>
        </div>

        <div id="message-input">
          <input ref="messageInput" v-model="messageText" type="text" placeholder="Digite uma mensagem..." @keyup.enter="sendMessage" />
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
      username: null,
      selectedUser: null,
      displayedMessages: [],
      chatMap: {},
      roomSubscriptions: {},
      adminName: "Rafael Borges",
      adminEmail: "rafaelmascarenhasborges@gmail.com",
      isAdmin: false,
      contacts: [], // agora guarda objetos { name, email }
      messageText: "",
      welcomeMessage: "Olá! Obrigado por usar nosso site. Use esse chat para tirar dúvidas e marcar horários.",
      clientSearch: "", // termo de busca para filtro
    };
  },

  created() {
    this.username = localStorage.getItem("usuarioNome") || "";
    const email = localStorage.getItem("usuarioEmail") || "";
    this.isAdmin = email === this.adminEmail;
  },

  mounted() {
    this.connect();
  },

  beforeUnmount() {
    this.cleanup();
  },

  computed: {
    selectedContact() {
      return this.contacts.find(c => c.name === this.selectedUser) || null;
    },
    filteredContacts() {
      if (!this.clientSearch || !this.clientSearch.trim()) return this.contacts;
      const q = this.clientSearch.trim().toLowerCase();
      return this.contacts.filter(c => {
        const name = (c.name || "").toLowerCase();
        const email = (c.email || "").toLowerCase();
        return name.includes(q) || email.includes(q);
      });
    }
  },

  methods: {
    cleanup() {
      try {
        Object.values(this.roomSubscriptions).forEach((s) => s?.unsubscribe && s.unsubscribe());
        this.stompClient?.deactivate();
      } catch {}
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

        if (this.isAdmin) {
          this.subscribeOnlineUsers();
          this.loadContacts();
        }

        if (!this.isAdmin) {
          const room = this.getChatKey(this.username, this.adminName);
          this.subscribeRoom(room);
          setTimeout(() => this.openChatWith(this.adminName), 200);
        }
      };

      this.stompClient.activate();
    },

    getChatKey(a, b) {
      return [a, b].map((x) => x.toLowerCase()).sort().join("-");
    },

    subscribeRoom(roomId) {
      if (this.roomSubscriptions[roomId]) return;

      const sub = this.stompClient.subscribe(`/topic/room/${roomId}`, (msg) => {
        const m = JSON.parse(msg.body);
        this.handleChatMessage(m);
      });

      this.roomSubscriptions[roomId] = sub;
    },

    handleChatMessage(message) {
      const key = this.getChatKey(message.sender, message.recipient);
      if (!this.chatMap[key]) this.chatMap[key] = [];

      this.chatMap[key].push(message);

      const other = message.sender === this.username ? message.recipient : message.sender;

      if (this.selectedUser === other) {
        this.displayedMessages.push(message);
        this.$nextTick(() => {
          this.$refs.messageList.scrollTop = this.$refs.messageList.scrollHeight;
        });
      }
    },

    openChatWith(username) {
      this.selectedUser = username;
      const key = this.getChatKey(this.username, username);
      this.displayedMessages = this.chatMap[key] ? [...this.chatMap[key]] : [];
      this.subscribeRoom(key);

      if (!this.chatMap[key] || this.chatMap[key].length === 0) {
        if ((this.username === this.adminName && username) || (username === this.adminName)) {
          this.sendAdminAutoMessage(username);
        }
      }

      this.$nextTick(() => this.$refs.messageInput?.focus());
    },

    closeChat() {
      if (this.isAdmin) {
        this.selectedUser = null;
        this.displayedMessages = [];
        return;
      }
      this.selectedUser = null;
      this.$router.push("/");
    },

    sendMessage() {
      if (!this.messageText.trim()) return;

      const msg = {
        sender: this.username,
        recipient: this.selectedUser,
        content: this.messageText,
        type: "CHAT",
      };

      this.stompClient.publish({
        destination: "/app/chat.privateMessage",
        body: JSON.stringify(msg),
      });

      this.messageText = "";
    },

    sendAdminAutoMessage(user) {
      const msg = {
        sender: this.adminName,
        recipient: user,
        content: this.welcomeMessage,
        type: "AUTO",
      };

      const key = this.getChatKey(this.adminName, user);
      if (!this.chatMap[key]) this.chatMap[key] = [];
      this.chatMap[key].push(msg);

      if (this.selectedUser === user) {
        this.displayedMessages.push(msg);
      }

      this.stompClient.publish({
        destination: "/app/chat.privateMessage",
        body: JSON.stringify(msg),
      });
    },

    async loadContacts() {
      try {
        const token = localStorage.getItem("token");
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        const res = await fetch("http://localhost:8081/clients", { headers });
        if (!res.ok) return;
        const data = await res.json();

        const mapped = data.map((c) => {
          if (typeof c === "string") return { name: c, email: "" };
          return { name: c.name || c.username || "", email: c.email || "" };
        });

        this.contacts = mapped.filter((c) => c.name && c.name !== this.username && c.name !== this.adminName);
      } catch (err) {}
    },

    subscribeOnlineUsers() {
      if (this.roomSubscriptions["__users_topic"]) return;

      const sub = this.stompClient.subscribe("/topic/users", (msg) => {
        try {
          const users = JSON.parse(msg.body);
          const mapped = users.map((u) => {
            if (typeof u === "string") return { name: u, email: "" };
            return { name: u.name || u.username || "", email: u.email || "" };
          });
          this.contacts = mapped.filter((c) => c.name && c.name !== this.username && c.name !== this.adminName);
        } catch {}
      });

      this.roomSubscriptions["__users_topic"] = sub;
    },

    // evento chamado no input do filtro — hoje apenas passthrough
    onClientSearch() {
      // deixei aqui caso queira debounce ou analytics futuramente
    },
  },
};
</script>

<style scoped>
@import "../assets/Scss/pages/chat.scss";
.auto-tag {
  font-size: 10px;
  opacity: 0.7;
  margin-top: 4px;
}
</style>
