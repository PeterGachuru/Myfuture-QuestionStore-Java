package ke.co.myfuture.Myfuture.IbukaGPTs.GptChat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GptChatRepository extends JpaRepository<GptChat, Long> {
    Optional<GptChat> findByUuid(String uuid);

    List<GptChat> findByEmail(String email);
}