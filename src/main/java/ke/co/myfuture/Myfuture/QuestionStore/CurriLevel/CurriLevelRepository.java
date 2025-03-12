package ke.co.myfuture.Myfuture.QuestionStore.CurriLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
            SELECT * FROM curri_level WHERE curriculum = :curriculum 
            ORDER BY numbering 
            """, nativeQuery = true)
    List<CurriLevel> getAllByCurriculum(@Param("curriculum") Long curriculum);

    @Query(value = """ 
            SELECT name FROM curri_level WHERE id = :classlevel
            """, nativeQuery = true)
    String getName(Long classlevel);
}
