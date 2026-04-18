package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;


import lombok.Data;

import jakarta.persistence.*;


@Entity
@Table(name = "google_play_subscriptions")
@Data
public class GooglePlaySubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false, unique = true)
    private String purchaseToken;

    @Column(nullable = true, unique = true)
    private String orderId;

    private Long purchaseTime;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String originalJson;
}