package ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage;

import ke.co.myfuture.Myfuture.IbukaGPTs.GptChat.GptChat;
import ke.co.myfuture.Myfuture.IbukaGPTs.GptChat.GptChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private GptChatRepository gptChatRepository;

    public ChatMessage addChatMessage(ChatMessageRequest chatMessageRequest) {
        // Find the related GptChat by ID
        Optional<GptChat> gptChatOptional = gptChatRepository.findById(chatMessageRequest.getGptChatId());

        if (gptChatOptional.isEmpty()) {
            throw new IllegalArgumentException("GptChat with id " + chatMessageRequest.getGptChatId() + " not found");
        }

        GptChat gptChat = gptChatOptional.get();

        // Build and save the new ChatMessage
        ChatMessage chatMessage = ChatMessage.builder()
                .message(chatMessageRequest.getMessage())
                .sender(chatMessageRequest.getSender())
                .gptChat(gptChat)
                .build();

        return chatMessageRepository.save(chatMessage);
    }
}