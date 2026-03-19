package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriTopicRepository extends JpaRepository<CurriTopic, Long> {

    @Query(value = "SELECT * FROM curri_topic  WHERE parent is null AND subject = :subject AND curri_level = :classLevel AND deleted = 0 ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findBySubjectAndClass(@Param("subject") Long subject, @Param("classLevel")  Long classLevel);
    @Query(value = "SELECT * FROM curri_topic  WHERE parent = :parentId AND deleted = 0 ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findByParent(@Param("parentId") Long parentId);

    @Query(value = "SELECT * FROM curri_topic  WHERE id IN(select parent from curri_topic where length(content) > 40) ORDER BY id ASC", nativeQuery = true)
    List<CurriTopic> findParentsWithContent();

    @Query(value = "SELECT * FROM curri_topic  WHERE length(content) > 40 ORDER BY id ASC", nativeQuery = true)
    List<CurriTopic> findChildrenWithContent();

    @Query(value = "SELECT * FROM curri_topic  WHERE parent IS NOT NULL AND id NOT IN (SELECT subtopic_id FROM aiquery where query_purpose = :purpose) ORDER BY numbering ASC", nativeQuery = true)

    List<CurriTopic> findSubtopicsWithoutAI(@Param("purpose") String purpose);
    @Query(value = "SELECT * FROM curri_topic  WHERE parent IS NOT NULL AND id NOT IN (select subtopic from curri_question where book_model = 'chatGpt3_5') ORDER BY numbering ASC", nativeQuery = true)
    List<CurriTopic> findSubtopicsWithoutAIQuestions();

    @Query(value = """
SELECT * FROM curri_topic  WHERE parent IS NOT NULL AND id IN
 (select subtopic from (select count(subtopic) as count, subtopic 
 from curri_question group by subtopic) k where count < 30)
  ORDER BY numbering ASC
""", nativeQuery = true)
    List<CurriTopic> findSubtopicsWithLessAIQuestions();

    @Query(value = """
            SELECT * FROM curri_topic  WHERE parent is null AND subject = :subject AND curri_level = :classLevel 
            AND deleted = 0 AND id IN (SELECT parent FROM curri_topic WHERE id IN(SELECT subtopic FROM curri_question 
            WHERE reviewed = '0')) ORDER BY numbering ASC, id ASC
            """, nativeQuery = true)
    List<CurriTopic> getAllWithUnapprovedQuestions(@Param("subject") Long subject, @Param("classLevel")  Long classLevel);

    @Query(value = """
            SELECT * FROM curri_topic  WHERE parent = :parent AND subject = :subject AND curri_level = :classLevel 
            AND deleted = 0 AND id IN(SELECT subtopic FROM curri_question 
            WHERE reviewed = '0') ORDER BY numbering ASC, id ASC
            """, nativeQuery = true)
    List<CurriTopic> getAllWithUnapprovedQuestions(@Param("parent") Long parent, @Param("subject") Long subject, @Param("classLevel")  Long classLevel);

    @Query(nativeQuery = true, value = """
    SELECT 
        id AS id,
        parent AS parent,
        name AS name,
        curri_level AS curriLevel,
        subject AS subject,
        created_at AS createdAt,
        numbering AS numbering,
        deleted AS deleted,
        required AS required,
        updated_at AS updatedAt,
        content AS content,
        created_by AS createdBy,
        deleted_flag AS deletedFlag,
        deleted_at AS deletedAt,
        deleted_by AS deletedBy,
        instructions_on_generation_of_notes AS instructionsOnGenerationOfNotes,
        instructions_on_generation_of_questions AS instructionsOnGenerationOfQuestions,
        percentage_of_rejected_questions AS percentageOfRejectedQuestions,
        total_number_of_approved_questions AS totalNumberOfApprovedQuestions,
        total_number_of_unverified_questions AS totalNumberOfUnverifiedQuestions,
        is_parent AS isParent
    FROM curri_topic
    WHERE  curri_level IN (
               SELECT id FROM curri_level WHERE curriculum = :curriculum
           )
""")
    List<CurriTopicView> findAllTopicsByCurriculum(@Param("curriculum") Long curriculum);


    @Query(nativeQuery = true, value = """
    SELECT 
        id AS id,
        parent AS parent,
        name AS name,
        slug AS slug,
        curri_level AS curriLevel,
        subject AS subject,
        created_at AS createdAt,
        numbering AS numbering,
        deleted AS deleted,
        required AS required,
        updated_at AS updatedAt,
        content AS content,
        created_by AS createdBy,
        deleted_flag AS deletedFlag,
        deleted_at AS deletedAt,
        deleted_by AS deletedBy,
        instructions_on_generation_of_notes AS instructionsOnGenerationOfNotes,
        instructions_on_generation_of_questions AS instructionsOnGenerationOfQuestions,
        percentage_of_rejected_questions AS percentageOfRejectedQuestions,
        total_number_of_approved_questions AS totalNumberOfApprovedQuestions,
        total_number_of_unverified_questions AS totalNumberOfUnverifiedQuestions,
        is_parent AS isParent
    FROM curri_topic
    WHERE  curri_level = :classlevel 
""")
    List<CurriTopicView> findAllTopicsByClassLevel(@Param("classlevel") Long classlevel);

    // Find topics/subtopics with null or empty slug
    List<CurriTopic> findBySlugIsNullOrSlugIs(String emptySlug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query(value = "SELECT * FROM curri_topic WHERE slug = :slug LIMIT 1", nativeQuery = true)
    Optional<CurriTopic> findBySlug(@Param("slug") String slug);
}