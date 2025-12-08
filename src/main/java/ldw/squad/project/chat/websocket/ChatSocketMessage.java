package ldw.squad.project.chat.websocket;

public class ChatSocketMessage {
	private String sender;
    private String recipient;
    private String content;
    private String type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    public String getType() {
        return type;
    }

    public void setType(String string) {
        this.type = string;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}