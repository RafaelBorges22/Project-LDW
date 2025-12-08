<template>
  <div id="app">
    <Navbar />

    <div class="chat-wrapper">
      <div v-if="isAdmin && !selectedUser" class="admin-list">
        <h2>Escolha um cliente</h2>
        <div v-for="user in contacts" :key="user" class="user-card" @click="openChatWith(user)">
          {{ user }}
        </div>
      </div>

      <div v-if="selectedUser" id="chat-area">
        <div id="chat-header">
          <button @click="closeChat">✕</button>
          <h2 id="chat-with-name">{{ selectedUser }}</h2>
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
      contacts: [],
      messageText: "",
      welcomeMessage: "Olá! Obrigado por usar nosso site. Use esse chat para tirar dúvidas e marcar horários.",
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

    openChatWith(user) {
      this.selectedUser = user;
      const key = this.getChatKey(this.username, user);
      this.displayedMessages = this.chatMap[key] ? [...this.chatMap[key]] : [];
      this.subscribeRoom(key);

	  if (!this.chatMap[key] || this.chatMap[key].length === 0) {
	    if ((this.username === this.adminName && user) || 
	        (user === this.adminName)) {
	      this.sendAdminAutoMessage(user);
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
        this.contacts = data.map((c) => c.name).filter((n) => n && n !== this.username);
      } catch {}
    },

    subscribeOnlineUsers() {
      if (this.roomSubscriptions["__users_topic"]) return;

      const sub = this.stompClient.subscribe("/topic/users", (msg) => {
        try {
          const users = JSON.parse(msg.body);
          this.contacts = users.filter((u) => u !== this.username);
        } catch {}
      });

      this.roomSubscriptions["__users_topic"] = sub;
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