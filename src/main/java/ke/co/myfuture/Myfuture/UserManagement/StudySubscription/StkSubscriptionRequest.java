package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StkSubscriptionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal payAmount;

    @Column(nullable = false)
    private int numberOfDays;
    private int appVersion;

    @Column(nullable = false)
    private String subscriptionType;


    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Long installId;

    @ManyToOne
    private StudySubscription subscription;

    private String emailAddress;

    private String transactionCode;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt;

    public Date callbackAt;
}