package ke.co.myfuture.Myfuture.UserManagement.Contest.ContestInvitee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface ContestInviteeRepository extends JpaRepository<ContestInvitee, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE contest_invitee SET score = :score, attempted_at = CURRENT_TIMESTAMP WHERE contest = :contestId AND invitee_id = :studentId ")
    void updateScore(@Param("contestId") Long contestId, @Param("studentId") Long studentId, @Param("score") Integer score);
}
