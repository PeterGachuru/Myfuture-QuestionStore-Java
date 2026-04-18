package ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage;

import ke.co.myfuture.Myfuture.IbukaGPTs.GptChat.GptChat;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

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

    private String model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpt_chat_id", nullable = false)
    private GptChat gptChat;

    @Column(nullable = false, updatable = false)
    private Date creationDate;

    @Column(nullable = false)
    private Date lastUpdateDate;

    @PrePersist
    public void prePersist() {
        this.creationDate = new Date();
        this.lastUpdateDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdateDate = new Date();
    }

    public enum Sender {
        USER,
        SYSTEM
    }
}
