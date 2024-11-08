package ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage;

import ke.co.myfuture.Myfuture.IbukaGPTs.GptChat.GptChat;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sender sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpt_chat_id", nullable = false)
    private GptChat gptChat;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime lastUpdateDate;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
        this.lastUpdateDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdateDate = LocalDateTime.now();
    }

    public enum Sender {
        USER,
        SYSTEM
    }
}
