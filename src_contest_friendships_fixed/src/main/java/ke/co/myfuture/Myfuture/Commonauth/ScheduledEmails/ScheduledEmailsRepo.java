package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ScheduledEmailsRepo extends JpaRepository<ScheduledEmails, Long> {

    @Query("""
    SELECT e FROM ScheduledEmails e 
    WHERE 
        e.scheduledTime <= :now 
        AND e.sent = false 
        AND FUNCTION('TIMESTAMPADD', SECOND, e.expiresAfterSeconds, e.createdAt) > :now
    """)
    List<ScheduledEmails> findPendingEmails(Date now);

    @Query(value = "SELECT s.id AS id, s.recipient AS recipient, s.subject AS subject, " +
            "s.scheduled_time AS scheduledTime, s.attempted_send_at AS attemptedSendAt, " +
            "s.time_sent AS timeSent, s.last_attempt_status AS lastAttemptStatus, " +
            "s.sender_service AS senderService, s.sent AS sent, " +
            "s.expires_after_seconds AS expiresAfterSeconds, " +
            "s.created_at AS createdAt, s.updated_at AS updatedAt " +
            "FROM scheduled_emails s order by created_at DESC",
            countQuery = "SELECT COUNT(*) FROM scheduled_emails",
            nativeQuery = true)
    Page<ScheduledEmailsProjection> findAllProjected(Pageable pageable);

    Set<String> findRecipientByLastAttemptStatus(LastStatus lastAttemptStatus);

    @Query("SELECT COUNT(s) FROM ScheduledEmails s " +
            "WHERE s.attemptedSendAt IS NOT NULL " +
            "AND s.attemptedSendAt >= :fromTime")
    long countAttemptsInLast50Minutes(@Param("fromTime") Date fromTime);

    public interface ScheduledEmailsProjection {
        Long getId();
        String getRecipient();
        String getSubject();
        Date getScheduledTime();
        Date getAttemptedSendAt();
        Date getTimeSent();
        String getLastAttemptStatus();
        String getSenderService();
        boolean isSent();
        Long getExpiresAfterSeconds();
        Date getCreatedAt();
        Date getUpdatedAt();
    }
}
