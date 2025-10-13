package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints =
        {@UniqueConstraint(columnNames = {"inid", "installId"})})
public class StudySubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal payAmount;

    @Column(nullable = false)
    private int numberOfDays;
    private int appVersion;

    @Column(nullable = false)
    private Long installId;

    @Column(nullable = false)
    private Long inid;

    @Column(nullable = false)
    private String transactionCode;

    private String referralCode;

    @Column(nullable = false)
    private String emailAddress;

    @Column(nullable = false)
    private String subscriptionType;

    @Column(nullable = false)
    private String paymentProcessor;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    @CreationTimestamp
    public Date createdAt;
    @UpdateTimestamp
    public Date updatedAt;
}
