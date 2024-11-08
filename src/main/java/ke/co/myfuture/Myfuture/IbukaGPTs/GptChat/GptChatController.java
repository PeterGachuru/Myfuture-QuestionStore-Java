package ke.co.myfuture.Myfuture.IbukaGPTs.GptChat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/gptchat")
public class GptChatController {

    @Autowired
    private GptChatService gptChatService;

    @PostMapping
    public ResponseEntity<GptChat> createGptChat(@RequestParam String model) {
        GptChat createdGptChat = gptChatService.addGptChat(model);
        return ResponseEntity.ok(createdGptChat);
    }
}