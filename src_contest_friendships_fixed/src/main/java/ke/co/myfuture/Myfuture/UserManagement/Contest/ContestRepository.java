package ke.co.myfuture.Myfuture.UserManagement.Contest;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.PageVisit.PageVisit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ContestRepository extends JpaRepository<Contest, Long> {
    @Query(value = "SELECT * FROM contest WHERE id > :contestId AND id IN (select contest from contest_invitee where invitee_id = :studentId) ", nativeQuery = true)
    List<Contest> contestsAfter(@Param("contestId") Long contestId, @Param("studentId") Long studentId);

    @Query(value = "SELECT * FROM contest WHERE id IN (select contest from contest_invitee where invitee_id = :studentId AND id > :latestInviteId) ", nativeQuery = true)
    List<Contest>  contestsAfterInvite(Long latestInviteId, Long studentId);

    List<Contest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
    SELECT DATE(e.createdAt), COUNT(e)
    FROM Contest e
    WHERE e.createdAt >= :startDate
    GROUP BY DATE(e.createdAt)
    ORDER BY DATE(e.createdAt)
""")
    List<Object[]> countPerDay(@Param("startDate") Date startDate);

    List<Contest> findByCreator(IbukaStudentAccount student);
}
