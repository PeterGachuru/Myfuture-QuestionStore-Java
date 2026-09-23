package ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
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

    @GetMapping("all")
    public ResponseEntity<List<ChatMessage>> createChatMessage(@RequestParam("chatId") Long chatId) {
        List<ChatMessage> chatMessages = chatService.allForChatId(chatId);
        return ResponseEntity.ok(chatMessages);
    }

    @GetMapping("bychatuuid")
    public ResponseEntity<List<ChatMessage>> createChatMessage(@RequestParam("chatUuid") String chatUuid) {
        List<ChatMessage> chatMessages = chatService.allForChatUuid(chatUuid);
        return ResponseEntity.ok(chatMessages);
    }
}

