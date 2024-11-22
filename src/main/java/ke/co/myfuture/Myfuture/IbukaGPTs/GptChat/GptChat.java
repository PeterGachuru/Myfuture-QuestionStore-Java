package ke.co.myfuture.Myfuture.IbukaGPTs.GptChat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ke.co.myfuture.Myfuture.IbukaGPTs.ChatMessage.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GptChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false,  columnDefinition = "CHAR(36)")
    private String uuid;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, updatable = false)
    private Date creationDate;

    @Column(nullable = false)
    private Date lastUpdateDate;

    @JsonIgnore
    @OneToMany(mappedBy = "gptChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages;

    @PrePersist
    public void prePersist() {
        this.uuid = String.valueOf(UUID.randomUUID());
        this.creationDate = new Date();
        this.lastUpdateDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdateDate = new Date();
    }
}