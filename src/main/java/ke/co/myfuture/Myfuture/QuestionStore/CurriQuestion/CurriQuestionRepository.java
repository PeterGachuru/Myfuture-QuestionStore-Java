package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface CurriQuestionRepository extends JpaRepository<CurriQuestion, Long> {

    @Query(value = "SELECT * FROM curri_question WHERE subtopic = :subtopicId ", nativeQuery = true)
    List<CurriQuestion> findBySubtopicId(@Param("subtopicId") Long subtopicId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE curri_question SET book_model = :bookModel WHERE book_model IS NULL")
    void setDefaultBookModel(@Param("bookModel") String bookModel);
}
