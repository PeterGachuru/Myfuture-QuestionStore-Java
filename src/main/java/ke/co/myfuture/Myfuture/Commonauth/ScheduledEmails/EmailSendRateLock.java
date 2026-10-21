package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_send_rate_lock")
public class EmailSendRateLock {
    @Id
    private Integer id;

    protected EmailSendRateLock() {}

    public Integer getId() {
        return id;
    }
}
