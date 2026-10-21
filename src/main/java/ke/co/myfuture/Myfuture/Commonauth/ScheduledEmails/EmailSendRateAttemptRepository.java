package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailSendRateAttemptRepository extends JpaRepository<EmailSendRateAttempt, Long> {
    @Query("SELECT COUNT(attempt) FROM EmailSendRateAttempt attempt WHERE attempt.reservedAt >= :cutoff")
    long countSince(@Param("cutoff") Date cutoff);

    @Modifying
    @Query("DELETE FROM EmailSendRateAttempt attempt WHERE attempt.reservedAt < :cutoff")
    int deleteBefore(@Param("cutoff") Date cutoff);
}
