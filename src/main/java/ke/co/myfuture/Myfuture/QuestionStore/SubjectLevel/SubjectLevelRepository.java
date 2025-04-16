package ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubjectLevelRepository extends JpaRepository<SubjectLevel, Long> {

    @Query(nativeQuery = true, value = "select * from subject_level where curri_level = :classlevelId AND subject = :subjectId")
    Optional<SubjectLevel> findByLevelAndSubject(Long classlevelId, Long subjectId);

    @Query(value = """
        SELECT CASE 
            WHEN COUNT(*) > 0 THEN TRUE 
            ELSE FALSE 
        END 
        FROM subject_level 
        WHERE subject = :subjectId 
        AND curri_level = :levelId 
        AND deleted_flag = TRUE
        """, nativeQuery = true)
    Integer subjectIsdeleted(@Param("subjectId") Long subjectId, @Param("levelId") Long levelId);
}
