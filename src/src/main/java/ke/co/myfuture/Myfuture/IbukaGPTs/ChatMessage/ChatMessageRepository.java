package ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage;

import ke.co.myfuture.Myfuture.IbukaGPTs.GptChat.GptChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Custom method to find all chat messages for a given GptChat
    List<ChatMessage> findByGptChat(GptChat gptChat);
}