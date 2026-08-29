package ke.co.myfuture.Myfuture.UserManagement.QuizDone.QuizQuestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByQuizOrderByPositionAsc(Long quiz);
    Optional<QuizQuestion> findByQuizAndQuestionId(
            Long quiz,
            Long questionId
    );

    List<QuizQuestion> findByQuizOrderByIdAsc(Long quizId);
}
