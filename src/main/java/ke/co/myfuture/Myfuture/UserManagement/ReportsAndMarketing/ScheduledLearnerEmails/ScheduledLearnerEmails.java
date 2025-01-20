package ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.ScheduledLearnerEmails;


import ke.co.myfuture.Myfuture.Treasury.Account.AccountOwnershipType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data

public class ScheduledLearnerEmails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(length = 5000, nullable = false)
    private String body;


    @Column(nullable = false)
    private LocalDateTime scheduledTime;
    private LocalDateTime attemptedSendAt;
    private LocalDateTime timeSent;

    @Enumerated(EnumType.STRING)
    private LastStatus lastAttemptStatus;


    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean sent;

    @Column(nullable = false)
    private Long expiresAfterSeconds;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    public Date createdAt;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    public Date updatedAt = new Date();
}
