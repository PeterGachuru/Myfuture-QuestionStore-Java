package ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/chatmessage")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatMessage> createChatMessage(@RequestBody ChatMessageRequest chatMessageRequest) {
        ChatMessage createdChatMessage = chatService.addUserChatMessage(chatMessageRequest);
        return ResponseEntity.ok(createdChatMessage);
    }
}