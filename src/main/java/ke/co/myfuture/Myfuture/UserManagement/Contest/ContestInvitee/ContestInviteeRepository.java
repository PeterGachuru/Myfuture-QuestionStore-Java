package ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface ContestInviteeRepository extends JpaRepository<ContestInvitee, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE contest_invitee SET score = :score, attempted = 1, attempted_at = CURRENT_TIMESTAMP WHERE contest = :contestId AND invitee_id = :studentId ")
    void updateScore(@Param("contestId") Long contestId, @Param("studentId") Long studentId, @Param("score") Integer score);

    List<ContestInvitee> findByContestOrderByCreatedAtDesc(Long contestId);

    List<ContestInvitee>  findByStudentaccount(IbukaStudentAccount student);

    @Query(value = """
        SELECT DATE(attempted_at), COUNT(*)
        FROM contest_invitee
        WHERE (attempted = true OR score > 0 )
        AND attempted_at >= :startDate
        GROUP BY DATE(attempted_at)
        ORDER BY DATE(attempted_at)
    """, nativeQuery = true)
    List<Object[]> countAttemptsPerDay(@Param("startDate") Date startDate);
}