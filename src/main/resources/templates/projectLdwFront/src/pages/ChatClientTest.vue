<script setup>
import { ref, onMounted } from 'vue';
// Importe a biblioteca Stomp.js. Você precisa garantir que ela esteja instalada:
// npm install @stomp/stompjs sockjs-client
import { Client as StompClient } from '@stomp/stompjs';
import SockJS from 'sockjs-client'; // É necessário instalar 'sockjs-client' para a integração correta com o Spring Boot

// --- Estado Reativo ---
const isConnected = ref(false);
const username = ref('');
const messageInput = ref('');
const messages = ref([]);
const stompClient = ref(null);

// Defina a URL do endpoint HTTP do SockJS (sem o 'ws://' e sem o protocolo STOMP)
// Exemplo: 'http://localhost:8080/live-chat-websocket' se o Spring estiver na porta 8080
// Usamos window.location.host para manter a flexibilidade, mas mudamos de 'ws' para 'http'
// e removemos o protocolo do host, deixando o cliente STOMP adicionar o protocolo apropriado.

// Usaremos apenas o path do endpoint para garantir que o SockJS use o protocolo correto (http/https)
// Se você está desenvolvendo em localhost:5173 e o backend está em localhost:8080,
// você precisará ajustar o host. Assumindo que o Spring está rodando no mesmo host/porta
// (o que é improvável em uma aplicação Vue/Spring separada), a URL de base deve ser a do servidor Spring.
// Se você estiver rodando o Vue em http://localhost:5173 e o Spring em http://localhost:8080:
const backendBaseUrl = 'http://' + window.location.hostname + ':8081'; // AJUSTE A PORTA DO BACKEND AQUI
const endpointUrl = backendBaseUrl + '/live-chat-websocket';


// --- Funções do Stomp Client ---

/**
 * Lida com a conexão bem-sucedida do Stomp.
 */
const onConnect = (frame) => {
    isConnected.value = true;
    console.log('Connected: ' + frame);

    // 1. Assina o tópico de chat
    stompClient.value.subscribe('/topics/livechat', (message) => {
        // A mensagem STOMP vem em formato string, deve ser parseada para JSON.
        // O corpo da mensagem é um objeto JSON que contém a propriedade 'content'
        const chatOutput = JSON.parse(message.body);
        updateLiveChat(chatOutput.content);
    });
};

/**
 * Lida com erros de WebSocket.
 */
const onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

/**
 * Lida com erros do broker STOMP.
 */
const onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

// --- Ações da Aplicação ---

/**
 * Inicializa e ativa a conexão Stomp.
 */
const connect = () => {
    if (!username.value.trim()) {
        console.warn("Please enter a username before connecting.");
        return;
    }

    // A StompClient deve ser criada APENAS quando o usuário clica em conectar.
    // Isso garante que o SockJS seja usado corretamente.
    
    // Cria uma factory para o WebSocket usando SockJS
    const sockJsFactory = () => {
        return new SockJS(endpointUrl);
    };

    stompClient.value = new StompClient({
        webSocketFactory: sockJsFactory, // Usa a factory SockJS
        onConnect: onConnect,
        onWebSocketError: onWebSocketError,
        onStompError: onStompError,
        // Configuração de reconexão
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });
    
    stompClient.value.activate();
};

/**
 * Desativa a conexão Stomp.
 */
const disconnect = () => {
    if (stompClient.value) {
        stompClient.value.deactivate();
        isConnected.value = false;
        console.log("Disconnected");
    }
};

/**
 * Envia uma mensagem para o servidor.
 */
const sendMessage = () => {
    if (messageInput.value.trim() === '' || username.value.trim() === '') {
        return;
    }

    if (stompClient.value && isConnected.value) {
        const payload = {
            user: username.value,
            message: messageInput.value
        };

        stompClient.value.publish({
            destination: "/app/new-message",
            body: JSON.stringify(payload)
        });

        messageInput.value = "";
    } else {
        console.warn("Not connected to send message.");
    }
};

/**
 * Adiciona uma nova mensagem ao chat (chamado pelo subscriber).
 * @param {string} message - A mensagem completa (incluindo usuário e conteúdo).
 */
const updateLiveChat = (message) => {
    messages.value.push(message);
    // Rola para o final
    const chatHistory = document.getElementById('chat-history');
    if (chatHistory) {
        setTimeout(() => {
            chatHistory.scrollTop = chatHistory.scrollHeight;
        }, 0);
    }
};

// --- Configuração e Lifecycle ---
onMounted(() => {
    // A StompClient agora é inicializada dentro da função connect,
    // garantindo que a conexão SockJS seja estabelecida no momento correto.
    console.log("Component mounted. Ready to connect.");
});
</script>

<template>
    <div class="min-h-screen bg-gray-900 flex flex-col items-center p-4 sm:p-8 font-sans">
        <!-- Título -->
        <header class="w-full max-w-4xl mb-8">
            <h1 class="text-3xl sm:text-4xl font-extrabold text-white text-center tracking-wider">
                <span class="text-indigo-400">Build & Run</span> Live Chat
            </h1>
        </header>

        <main class="w-full max-w-4xl bg-gray-800 p-6 rounded-xl shadow-2xl border border-gray-700">

            <!-- Controles de Conexão e Usuário -->
            <div class="flex flex-col md:flex-row gap-4 mb-6 items-center justify-between">
                <div class="flex flex-col sm:flex-row gap-4 w-full md:w-auto">
                    <!-- Campo Usuário -->
                    <div class="w-full">
                        <label for="user" class="block text-sm font-medium text-gray-400 mb-1">Username:</label>
                        <input
                            id="user"
                            v-model="username"
                            type="text"
                            placeholder="Enter your username"
                            :disabled="isConnected"
                            class="w-full p-2.5 bg-gray-700 text-white border border-gray-600 rounded-lg focus:ring-indigo-500 focus:border-indigo-500 disabled:opacity-60 disabled:cursor-not-allowed"
                        >
                    </div>
                </div>

                <!-- Botões de Conexão -->
                <div class="flex space-x-3 w-full md:w-auto mt-4 md:mt-0 justify-end">
                    <button
                        @click="connect"
                        :disabled="isConnected || !username.trim()"
                        class="px-5 py-2.5 font-semibold rounded-lg transition-all duration-200"
                        :class="[isConnected ? 'bg-gray-500 text-gray-300 cursor-not-allowed' : 'bg-indigo-600 hover:bg-indigo-700 text-white shadow-lg shadow-indigo-500/50']"
                    >
                        Connect
                    </button>
                    <button
                        @click="disconnect"
                        :disabled="!isConnected"
                        class="px-5 py-2.5 font-semibold rounded-lg bg-red-600 hover:bg-red-700 text-white transition-all duration-200 disabled:bg-gray-500 disabled:text-gray-300 disabled:cursor-not-allowed"
                    >
                        Disconnect
                    </button>
                </div>
            </div>

            <!-- Formulário de Mensagem (Visível apenas quando conectado) -->
            <div v-if="isConnected" class="mb-8 p-4 bg-gray-700 rounded-lg shadow-inner">
                <form @submit.prevent="sendMessage" class="flex flex-col sm:flex-row gap-3 items-end">
                    <div class="flex-grow w-full">
                        <label for="message" class="block text-sm font-medium text-gray-400 mb-1">Message:</label>
                        <input
                            id="message"
                            v-model="messageInput"
                            type="text"
                            placeholder="Type your message..."
                            required
                            class="w-full p-2.5 bg-gray-800 text-white border border-gray-600 rounded-lg focus:ring-green-500 focus:border-green-500"
                        >
                    </div>
                    <button
                        type="submit"
                        :disabled="messageInput.trim() === ''"
                        class="w-full sm:w-auto px-6 py-2.5 font-bold rounded-lg bg-green-500 hover:bg-green-600 text-white transition-all duration-200 disabled:bg-gray-500 disabled:cursor-not-allowed"
                    >
                        Send
                    </button>
                </form>
            </div>

            <!-- Tabela de Conversa (Visível apenas quando conectado) -->
            <div v-if="isConnected" id="conversation" class="mt-6 border border-gray-700 rounded-lg overflow-hidden">
                <div class="bg-gray-700 p-3">
                    <h3 class="text-xl font-semibold text-white">Live Chat History</h3>
                </div>
                <div id="chat-history" class="max-h-96 overflow-y-auto bg-gray-800">
                    <table class="min-w-full divide-y divide-gray-700">
                        <tbody id="livechat" class="bg-gray-800 divide-y divide-gray-700">
                            <tr v-if="messages.length === 0">
                                <td class="px-4 py-3 text-center text-gray-400 italic">
                                    Aguardando mensagens...
                                </td>
                            </tr>
                            <tr v-for="(message, index) in messages" :key="index">
                                <td class="px-4 py-3 whitespace-pre-wrap text-white">
                                    <!-- A mensagem já vem formatada do servidor (ex: "User: Content") -->
                                    {{ message }}
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Mensagem de Desconexão -->
            <div v-else class="text-center p-12 bg-gray-700/50 rounded-xl mt-8">
                <p class="text-xl text-gray-300">
                    You are currently <span class="font-bold text-red-400">disconnected</span>. Please enter a username and click **Connect** to join the live chat.
                </p>
            </div>

        </main>
    </div>
</template>