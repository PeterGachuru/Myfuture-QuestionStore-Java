package ke.co.myfuture.Myfuture.IbukaGPTs.GptChat;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Security.jwt.UserRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/ai/gptchat")
public class GptChatController {

    @Autowired
    private GptChatService gptChatService;

    @PostMapping
    public ResponseEntity<GptChat> createGptChat(@RequestParam String model) {
        GptChat createdGptChat = gptChatService.addGptChat(model);
        return ResponseEntity.ok(createdGptChat);
    }

    @GetMapping("all")
    public ResponseEntity<List<GptChat>> allGpts() {
        List<GptChat> createdGptChat = gptChatService.allByEmail(UserRequestContext.getCurrentUserName());
        return ResponseEntity.ok(createdGptChat);
    }
}