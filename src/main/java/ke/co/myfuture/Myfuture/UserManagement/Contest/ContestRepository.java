package ke.co.myfuture.Myfuture.UserManagement.Contest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    @Query(value = "SELECT * FROM contest WHERE id > :contestId AND id IN (select contest from contest_invitee where invitee_id = :studentId) ", nativeQuery = true)
    List<Contest> contestsAfter(@Param("contestId") Long contestId, @Param("studentId") Long studentId);

    @Query(value = "SELECT * FROM contest WHERE id IN (select contest from contest_invitee where invitee_id = :studentId AND id > :latestInviteId) ", nativeQuery = true)
    List<Contest>  contestsAfterInvite(Long latestInviteId, Long studentId);
}
