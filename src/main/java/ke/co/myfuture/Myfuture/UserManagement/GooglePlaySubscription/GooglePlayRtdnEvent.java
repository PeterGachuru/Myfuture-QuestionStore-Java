package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "google_play_rtdn_events")
@Data
public class GooglePlayRtdnEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String messageId;

    @Column(nullable = false, length = 40)
    private String status = "PENDING";

    @Column(length = 100)
    private String packageName;

    @Column(length = 60)
    private String eventType;

    @Column(length = 4000)
    private String purchaseToken;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String notificationJson;

    @Column(length = 1000)
    private String lastError;

    private Integer attempts = 0;
    private LocalDateTime processedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
