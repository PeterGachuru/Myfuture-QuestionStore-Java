package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public interface CurriQuestionRepository extends JpaRepository<CurriQuestion, Long> {

    int countBySubtopicAndDeleted(CurriTopic subtopic, Boolean deleted);

    int countBySubtopicAndReviewedAndDeleted(CurriTopic subtopic, Boolean reviewed, Boolean deleted);

    @Query(value = "SELECT * FROM curri_question WHERE subtopic = :subtopicId ", nativeQuery = true)
    List<CurriQuestion> findBySubtopicId(@Param("subtopicId") Long subtopicId);
    @Query(value = "SELECT * FROM curri_question WHERE subtopic = :subtopicId AND  reviewed = '0'", nativeQuery = true)
    List<CurriQuestion> findBySubtopicIdUnapproved(@Param("subtopicId") Long subtopicId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE curri_question SET book_model = :bookModel WHERE book_model IS NULL")
    void setDefaultBookModel(@Param("bookModel") String bookModel);

    @Query(value = "SELECT * FROM curri_question WHERE update_id >= :lastUpdateId AND deleted = 0 AND subtopic IN (SELECT id FROM curri_topic WHERE curri_level IN(SELECT id FROM curri_level WHERE curriculum = :curriculum)) ORDER BY update_id ASC ", nativeQuery = true)
    Page<CurriQuestion> findByBookModel(Pageable paging, @Param("lastUpdateId") String lastUpdateId, Long curriculum);

    @Query(value = "SELECT * FROM curri_question WHERE book_model = :model AND update_id >= :lastUpdateId AND deleted = 0 AND subtopic IN (SELECT id FROM curri_topic WHERE curri_level = :level AND subject = :subject AND curri_level IN(SELECT id FROM curri_level WHERE curriculum = :curriculum)) ORDER BY update_id ASC ", nativeQuery = true)
    Page<CurriQuestion> findByBookModel(Pageable paging, @Param("model") String model, @Param("lastUpdateId") String lastUpdateId, Long curriculum, Long level, Long subject);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE curri_question SET explanation = :explanation, update_id = :questionsUpdateId WHERE string LIKE CONCAT('%', :question, '%') ")
    void updateExplanation(@Param("explanation") String explanation, @Param("question") String question, @Param("questionsUpdateId") long questionsUpdateId);

    @Query(value = "select cq.* from (select * from curri_question where id in (:ids)) cq join curri_topic ct ON ct.id = cq.subtopic group by ct.curri_level, ct.parent", nativeQuery = true)
    List<CurriQuestion> forContestDownload(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM curri_question WHERE book_model = :bookModel AND reviewed = :reviewed AND subtopic IN(SELECT id FROM curri_topic WHERE subject = :subjectId)", nativeQuery = true)
    List<CurriQuestion> findBySubjectAndBookModelAndReviewed(Long subjectId, String bookModel, Integer reviewed);

    @Query(value = "SELECT * FROM curri_question WHERE book_model = :bookModel AND subtopic IN(SELECT id FROM curri_topic WHERE subject = :subjectId)", nativeQuery = true)
    List<CurriQuestion> findBySubjectAndBookModel(Long subjectId, String bookModel);


    @Query(value = "SELECT * FROM curri_question WHERE subtopic = :subtopicId AND  reviewed = '0' limit :limit", nativeQuery = true)
    List<CurriQuestion> findUnapprovedQuestionsBySubtopic(Long subtopicId, Integer limit);


    @Query("""
        SELECT q FROM CurriQuestion q
        WHERE q.subtopic.id = :subtopicId
        AND q.deleted = false 
        ORDER BY q.id ASC
    """)
    List<CurriQuestion> findBySubtopic(@Param("subtopicId") Long subtopicId);

}