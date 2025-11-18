package ldw.squad.project.Controller;


import ldw.squad.project.Controller.Domain.ChatInput;
import ldw.squad.project.Controller.Domain.ChatOutput;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class LiveChatController {

    //Recebe as msg e guarda na memória
    @MessageMapping("/new-message")
    @SendTo("/topics/livechat")
    public ChatOutput newMessage(ChatInput input){
        return new ChatOutput(HtmlUtils.htmlEscape(input.user() + ":" + input.message()));
    }
}
