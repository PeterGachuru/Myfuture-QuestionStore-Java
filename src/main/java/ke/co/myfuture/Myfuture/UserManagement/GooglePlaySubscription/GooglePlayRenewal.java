package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "google_play_renewals", uniqueConstraints =
        @UniqueConstraint(name = "uk_google_play_package_order", columnNames = {"package_name", "order_id"}))
@Data
public class GooglePlayRenewal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "package_name", nullable = false, length = 100)
    private String packageName;
    @Column(name = "order_id", nullable = false, length = 200)
    private String orderId;
    @Column(nullable = false, length = 4000)
    private String purchaseToken;
    @Column(nullable = false, length = 200)
    private String productId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private LocalDateTime verifiedAt;
    @Column(nullable = false)
    private LocalDateTime verifiedExpiryTime;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
