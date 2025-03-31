package ke.co.myfuture.Myfuture.Commonauth.ScheduledLearnerEmails;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class ScheduledEmails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String body;


    @Column(nullable = false)
    private Date scheduledTime;
    private Date attemptedSendAt;
    private Date timeSent;

    @Enumerated(EnumType.STRING)
    private LastStatus lastAttemptStatus;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderService SenderService;


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
