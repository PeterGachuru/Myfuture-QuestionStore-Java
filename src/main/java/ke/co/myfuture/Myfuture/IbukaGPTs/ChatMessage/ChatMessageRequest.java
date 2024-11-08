package ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private String message;
    private ChatMessage.Sender sender;
    private Long gptChatId;
}