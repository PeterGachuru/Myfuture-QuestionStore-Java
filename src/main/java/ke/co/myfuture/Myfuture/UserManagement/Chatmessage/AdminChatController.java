package ke.co.myfuture.Myfuture.UserManagement.Chatmessage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/chatmessages")
public class AdminChatController {

    private final ChatmessageService chatmessageService;

    public AdminChatController(ChatmessageService chatmessageService) {
        this.chatmessageService = chatmessageService;
    }

    @GetMapping
    public String chatmessages(Model model) {

        model.addAttribute("messages", chatmessageService.getLatestMessages());

        return "admin/chatmessages";
    }

}