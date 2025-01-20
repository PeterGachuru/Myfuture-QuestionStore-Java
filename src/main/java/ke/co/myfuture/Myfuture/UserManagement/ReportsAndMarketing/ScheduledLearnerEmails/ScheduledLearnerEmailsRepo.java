package ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.ScheduledLearnerEmails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledLearnerEmailsRepo extends JpaRepository<ScheduledLearnerEmails, Long> {

    @Query("SELECT e FROM ScheduledLearnerEmails e WHERE e.scheduledTime <= :now AND e.sent = false")
    List<ScheduledLearnerEmails> findPendingEmails(LocalDateTime now);
}
