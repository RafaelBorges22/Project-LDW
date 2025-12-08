package ldw.squad.project.chat.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketUserService userService;

    private final String ADMIN_NAME = "rafael borges";

    public WebSocketChatController(SimpMessagingTemplate messagingTemplate, WebSocketUserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
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

        String usernameLower = username.toLowerCase();
        String adminLower = ADMIN_NAME.toLowerCase();

        if (!usernameLower.equals(adminLower)) {

            ChatSocketMessage autoMsg = new ChatSocketMessage();
            autoMsg.setSender(ADMIN_NAME);
            autoMsg.setRecipient(username);
            autoMsg.setType("CHAT");
            autoMsg.setContent(
                    "Boa tarde! Seja bem-vindo ao KazuTattoo. " +
                    "Se precisar de qualquer coisa, me manda uma mensagem, " +
                    "eu respondo assim que der! (mensagem automatizada)"
            );

            String roomId = Stream.of(usernameLower, adminLower)
                    .map(String::trim)
                    .sorted()
                    .collect(Collectors.joining("-"));

            String destination = "/topic/room/" + roomId;

            System.out.println("🤖 Enviando mensagem automática para sala: " + destination);

            messagingTemplate.convertAndSend(destination, autoMsg);
        }
    }

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatSocketMessage message) {

        String sender = message.getSender().trim().toLowerCase();
        String recipient = message.getRecipient().trim().toLowerCase();

        String roomId = Stream.of(sender, recipient)
                .sorted()
                .collect(Collectors.joining("-"));

        System.out.println("✉️ [Sala: " + roomId + "] " + sender + " ➜ " + recipient + ": " + message.getContent());

        messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    }
}
