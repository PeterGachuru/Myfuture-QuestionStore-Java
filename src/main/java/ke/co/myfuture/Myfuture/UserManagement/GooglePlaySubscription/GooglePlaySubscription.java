package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;


import com.google.api.client.util.DateTime;
import lombok.Data;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


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

    private Double amount;

    private String currency;

    private Integer numberOfDays;

    private String billingPeriod;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String originalJson;

    @CreationTimestamp
    private DateTime createdAt;
}