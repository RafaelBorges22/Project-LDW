package ldw.squad.project.chat.websocket;

import ldw.squad.project.chat.history.ChatMessageDocument;
import ldw.squad.project.chat.history.ChatMessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketUserService userService;
    private final ChatMessageRepository chatRepository; // novo

    public WebSocketChatController(SimpMessagingTemplate messagingTemplate,
                                   WebSocketUserService userService,
                                   ChatMessageRepository chatRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.chatRepository = chatRepository; // novo
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatSocketMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {

        String username = chatMessage.getSender();

        if (username == null || username.isBlank()) {
            return;
        }

        username = username.trim();

        System.out.println("🟢 [addUser] Usuário conectado: " + username);

        // Registrar usuário
        userService.addUser(username);
        headerAccessor.getSessionAttributes().put("username", username);

        // Enviar lista de usuários para todos
        messagingTemplate.convertAndSend("/topic/users", userService.getAllUsers());

        // Enviar lista somente para o novo usuário
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/users",
                userService.getAllUsers()
        );
    }

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatSocketMessage message) {

        String sender = message.getSender().trim().toLowerCase();
        String recipient = message.getRecipient().trim().toLowerCase();

        String roomId = Stream.of(sender, recipient)
                .sorted()
                .collect(Collectors.joining("-"));

        System.out.println("✉️ [Sala: " + roomId + "] " + sender + " ➜ " + recipient + ": " + message.getContent());

        // Enviar via STOMP
        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);

        // Salvar no MongoDB apenas mensagens do tipo CHAT ou AUTO (você pode ajustar)
        if ("CHAT".equalsIgnoreCase(message.getType()) || "AUTO".equalsIgnoreCase(message.getType())) {
            ChatMessageDocument doc = new ChatMessageDocument();
            doc.setIdUsuarioRemetente(message.getSender());
            doc.setIdUsuarioDestinatario(message.getRecipient());
            doc.setMensagem(message.getContent());
            doc.setDataHora(new Date());

            chatRepository.save(doc);
        }
    }
}
