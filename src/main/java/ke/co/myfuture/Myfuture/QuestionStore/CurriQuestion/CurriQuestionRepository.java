package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurriQuestionRepository extends JpaRepository<CurriQuestion, Long> {

    @Query(value = "SELECT * FROM curri_question WHERE subtopic = :subtopicId ", nativeQuery = true)
    List<CurriQuestion> findBySubtopicId(@Param("subtopicId") Long subtopicId);
}
