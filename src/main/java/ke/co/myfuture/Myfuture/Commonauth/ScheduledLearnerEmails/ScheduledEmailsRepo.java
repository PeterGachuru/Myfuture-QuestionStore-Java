package ke.co.myfuture.Myfuture.Commonauth.ScheduledLearnerEmails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledEmailsRepo extends JpaRepository<ScheduledEmails, Long> {

    @Query("SELECT e FROM ScheduledEmails e WHERE e.scheduledTime <= :now AND e.sent = false")
    List<ScheduledEmails> findPendingEmails(LocalDateTime now);
}
