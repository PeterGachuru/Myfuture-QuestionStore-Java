package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriTopicRepository extends JpaRepository<CurriTopic, Long> {

    @Query(value = "SELECT * FROM curri_topic  WHERE parent is null AND subject = :subject AND curri_level = :classLevel AND deleted = 0 ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findBySubjectAndClass(@Param("subject") Long subject, @Param("classLevel")  Long classLevel);
    @Query(value = "SELECT * FROM curri_topic  WHERE parent = :parentId ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findByParent(@Param("parentId") Long parentId);

    @Query(value = "SELECT * FROM curri_topic  WHERE id IN(select parent from curri_topic where length(content) > 40) ORDER BY id ASC", nativeQuery = true)
    List<CurriTopic> findParentsWithContent();

    @Query(value = "SELECT * FROM curri_topic  WHERE length(content) > 40 ORDER BY id ASC", nativeQuery = true)
    List<CurriTopic> findChildrenWithContent();

    @Query(value = "SELECT * FROM curri_topic  WHERE parent IS NOT NULL AND id NOT IN (SELECT subtopic_id FROM aiquery where query_purpose = :purpose) ORDER BY numbering ASC", nativeQuery = true)

    List<CurriTopic> findSubtopicsWithoutAI(@Param("purpose") String purpose);
    @Query(value = "SELECT * FROM curri_topic  WHERE parent IS NOT NULL AND id NOT IN (select subtopic from curri_question where book_model = 'chatGpt3_5') ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findSubtopicsWithoutAIQuestions();

    @Query(value = "SELECT * FROM curri_topic  WHERE parent IS NOT NULL AND id IN (select subtopic from (select count(subtopic) as count, subtopic from curri_question where book_model = 'gpt-3.5-turbo-0125' group by subtopic) k where count < 13) ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findSubtopicsWithLessAIQuestions();
}