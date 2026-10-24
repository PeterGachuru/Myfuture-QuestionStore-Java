package ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ContestInviteeRepository extends JpaRepository<ContestInvitee, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE contest_invitee SET score = :score, attempted = 1, attempted_at = CURRENT_TIMESTAMP WHERE contest = :contestId AND invitee_id = :studentId ")
    void updateScore(@Param("contestId") Long contestId, @Param("studentId") Long studentId, @Param("score") Integer score);

    List<ContestInvitee> findByContestOrderByCreatedAtDesc(Long contestId);

    int countByContest(Long contestId);

    List<ContestInvitee> findByStudentaccount(IbukaStudentAccount student);

    Optional<ContestInvitee> findByContestAndStudentaccount(Long contest, IbukaStudentAccount student);

    @Query(value = """
        SELECT DATE(attempted_at), COUNT(*)
        FROM contest_invitee
        WHERE (attempted = true OR score > 0 )
        AND attempted_at >= :startDate
        GROUP BY DATE(attempted_at)
        ORDER BY DATE(attempted_at)
    """, nativeQuery = true)
    List<Object[]> countAttemptsPerDay(@Param("startDate") Date startDate);

    @Query(value = "SELECT CASE WHEN :hourly = true THEN DATE_FORMAT(attempted_at, '%Y-%m-%d %H:00:00') ELSE DATE_FORMAT(attempted_at, '%Y-%m-%d') END AS bucket, COUNT(*) FROM contest_invitee WHERE (attempted = true OR score > 0) AND attempted_at >= :startDate AND attempted_at < :endDate GROUP BY bucket ORDER BY bucket", nativeQuery = true)
    List<Object[]> dashboardContestAttempts(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("hourly") boolean hourly);
}
