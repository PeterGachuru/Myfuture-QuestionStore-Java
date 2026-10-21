package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "email_send_rate_attempt")
public class EmailSendRateAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reserved_at", nullable = false)
    private Date reservedAt;

    protected EmailSendRateAttempt() {}

    public EmailSendRateAttempt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }
}
