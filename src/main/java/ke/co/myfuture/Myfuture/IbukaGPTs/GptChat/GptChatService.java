package ke.co.myfuture.Myfuture.IbukaGPTs.GptChat;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Security.jwt.UserRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GptChatService {

    @Autowired
    private GptChatRepository gptChatRepository;

    public GptChat addGptChat(String model) {
        GptChat gptChat = GptChat.builder()
                .email(UserRequestContext.getCurrentUserName()) // Email is set to default or can be set based on your logic
                .model(model)
                .build();

        return gptChatRepository.save(gptChat);
    }
}