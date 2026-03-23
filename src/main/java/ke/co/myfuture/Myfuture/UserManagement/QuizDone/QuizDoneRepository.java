package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount.IbukaStudentAccount;
import ke.co.myfuture.Myfuture.UserManagement.Referral.Referral;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface QuizDoneRepository extends JpaRepository<QuizDone, Long> {
    @Query("SELECT q FROM QuizDone q WHERE q.student = :student AND q.endDate BETWEEN :startOfWeek AND :endOfWeek")
    List<QuizDone> findQuizzesByStudentAndWeek(IbukaStudentAccount student, Date startOfWeek, Date endOfWeek);

    @Query("SELECT DISTINCT q.student FROM QuizDone q")
    List<IbukaStudentAccount> findDistinctStudentIds();

    List<QuizDone> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<QuizDone> findByStudentOrderByCreatedAtDesc(IbukaStudentAccount student);

    @Query("""
    SELECT DATE(e.createdAt), COUNT(e)
    FROM QuizDone e
    WHERE e.createdAt >= :startDate
    GROUP BY DATE(e.createdAt)
    ORDER BY DATE(e.createdAt)
""")
    List<Object[]> countPerDay(@Param("startDate") Date startDate);
}