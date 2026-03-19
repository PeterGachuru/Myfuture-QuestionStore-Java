package ke.co.myfuture.Myfuture.QuestionStore.CurriLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CurriLevelRepository extends JpaRepository<CurriLevel, Long> {
    @Query(value = """ 
            SELECT * FROM curri_level 
                                WHERE curriculum = :curriculum AND id IN(SELECT curri_level FROM curri_topic 
                                WHERE id IN(SELECT subtopic FROM curri_question 
                                WHERE reviewed = '0')) 
            ORDER BY id 
            """, nativeQuery = true)
    List<CurriLevel> getAllWithUnapprovedQuestions(@Param("curriculum") Long curriculum);


    @Query(value = """ 
            SELECT name FROM curri_level WHERE id = :classlevel
            """, nativeQuery = true)
    String getName(Long classlevel);

    List<CurriLevel> findBySlugIsNullOrSlug(String slug);
    boolean existsBySlug(String slug);

    Optional<CurriLevel> findBySlug(String s);

    @Query(value = """ 
            SELECT * FROM curri_level WHERE curriculum = :curriculum ORDER BY numbering
             """, nativeQuery = true)
    List<CurriLevel> getAllByCurriculum(@Param("curriculum") Long curriculum);

    @Query(value = """
        SELECT cl.id
        FROM curri_level cl
        WHERE cl.curriculum = (SELECT curriculum FROM curri_level WHERE id = :classlevel)
        AND cl.numbering BETWEEN 
            (SELECT numbering FROM curri_level WHERE id = :classlevel) - 1
            AND
            (SELECT numbering FROM curri_level WHERE id = :classlevel) + 1
        ORDER BY cl.numbering
        """, nativeQuery = true)
    List<Long> classesAround(Long classlevel);
}
