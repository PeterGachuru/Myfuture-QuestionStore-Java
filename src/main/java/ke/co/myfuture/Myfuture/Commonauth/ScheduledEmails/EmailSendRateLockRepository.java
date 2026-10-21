package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

public interface EmailSendRateLockRepository extends JpaRepository<EmailSendRateLock, Integer> {
    @Modifying
    @Query(value = "INSERT IGNORE INTO email_send_rate_lock (id) VALUES (1)", nativeQuery = true)
    void ensureSingletonRow();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lockRow FROM EmailSendRateLock lockRow WHERE lockRow.id = 1")
    EmailSendRateLock lockSingletonRow();
}
