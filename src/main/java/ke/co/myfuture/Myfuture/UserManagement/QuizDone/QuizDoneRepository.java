package ke.co.myfuture.Myfuture.UserManagement.QuizDone;

import ke.co.myfuture.Myfuture.UserManagement.Studentaccount.IbukaStudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizDoneRepository extends JpaRepository<QuizDone, Long> {
    @Query("SELECT q FROM QuizDone q WHERE q.student = :student AND q.endDate BETWEEN :startOfWeek AND :endOfWeek")
    List<QuizDone> findQuizzesByStudentAndWeek(IbukaStudentAccount student, LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    @Query("SELECT DISTINCT q.student FROM QuizDone q")
    List<IbukaStudentAccount> findDistinctStudentIds();
}