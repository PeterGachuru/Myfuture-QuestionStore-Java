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

    @Query(value = "SELECT DATE(created_at), COUNT(*) FROM scheduled_emails WHERE created_at >= :startDate GROUP BY DATE(created_at) ORDER BY DATE(created_at)", nativeQuery = true)
    List<Object[]> countScheduledPerDay(@Param("startDate") Date startDate);

    @Query(value = "SELECT DATE(time_sent), COUNT(*) FROM scheduled_emails WHERE time_sent >= :startDate AND sent = 1 GROUP BY DATE(time_sent) ORDER BY DATE(time_sent)", nativeQuery = true)
    List<Object[]> countSentPerDay(@Param("startDate") Date startDate);

    @Query(value = "SELECT CASE WHEN :hourly = true THEN DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00') ELSE DATE_FORMAT(created_at, '%Y-%m-%d') END AS bucket, COUNT(*) FROM scheduled_emails WHERE created_at >= :startDate AND created_at < :endDate GROUP BY bucket ORDER BY bucket", nativeQuery = true)
    List<Object[]> dashboardEmailsScheduled(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("hourly") boolean hourly);

    @Query(value = "SELECT CASE WHEN :hourly = true THEN DATE_FORMAT(time_sent, '%Y-%m-%d %H:00:00') ELSE DATE_FORMAT(time_sent, '%Y-%m-%d') END AS bucket, COUNT(*) FROM scheduled_emails WHERE time_sent >= :startDate AND time_sent < :endDate AND sent = 1 GROUP BY bucket ORDER BY bucket", nativeQuery = true)
    List<Object[]> dashboardEmailsSent(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("hourly") boolean hourly);

    boolean existsByRecipientAndSubjectAndCreatedAtAfter(String recipient, String subject, Date createdAt);

    long countByRecipientAndCreatedAtAfter(String recipient, Date createdAt);

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

    @Query(value = "SELECT DISTINCT sender_service FROM scheduled_emails WHERE sender_service IS NOT NULL ORDER BY sender_service", nativeQuery = true)
    List<String> findDistinctSenderServices();

    Set<String> findRecipientByLastAttemptStatus(LastStatus lastAttemptStatus);

    @Query(
            value = "SELECT s.id AS id, " +
                    "s.recipient AS recipient, " +
                    "s.subject AS subject, " +
                    "s.scheduled_time AS scheduledTime, " +
                    "s.attempted_send_at AS attemptedSendAt, " +
                    "s.time_sent AS timeSent, " +
                    "s.last_attempt_status AS lastAttemptStatus, " +
                    "s.sender_service AS senderService, " +
                    "s.sent AS sent, " +
                    "s.created_at AS createdAt, " +
                    "s.updated_at AS updatedAt " +
                    "FROM scheduled_emails s " +
                    "WHERE (:category IS NULL OR s.sender_service = :category) " +
                    "AND (:sent IS NULL OR s.sent = :sent) " +
                    "AND (:email IS NULL OR " +
                    "LOWER(s.recipient) LIKE LOWER(CONCAT('%', :email, '%'))) " +
                    "ORDER BY s.created_at DESC, s.id DESC",
            countQuery = "SELECT COUNT(*) FROM scheduled_emails s " +
                    "WHERE (:category IS NULL OR s.sender_service = :category) " +
                    "AND (:sent IS NULL OR s.sent = :sent) " +
                    "AND (:email IS NULL OR " +
                    "LOWER(s.recipient) LIKE LOWER(CONCAT('%', :email, '%')))",
            nativeQuery = true
    )
    Page<ScheduledEmailsProjection> findAdminEmails(
            @Param("category") String category,
            @Param("sent") Boolean sent,
            @Param("email") String email,
            Pageable pageable
    );

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
