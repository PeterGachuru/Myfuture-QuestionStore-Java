package ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectLevelRepository extends JpaRepository<SubjectLevel, Long> {

    @Query(nativeQuery = true, value = "select * from subject_level where curri_level = :classlevelId AND subject = :subjectId")
    Optional<SubjectLevel> findByLevelAndSubject(Long classlevelId, Long subjectId);

    @Query(nativeQuery = true,
            value = """
           SELECT 
               id AS id,
               curri_level AS curriLevel,
               subject AS subject,
               created_by AS createdBy,
               created_at AS createdAt,
               deleted_at AS deletedAt,
               deleted_by AS deletedBy,
               deleted_flag AS deletedFlag,
               updated_at AS updatedAt
           FROM subject_level 
           WHERE curri_level IN (
               SELECT id FROM curri_level WHERE curriculum = :curriculum
           )
       """)
    List<SubjectLevelView> findByCurriculum(@Param("curriculum") Long curriculum);


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

    public interface SubjectLevelView {
        Integer getId();
        Integer getCurriLevel();
        Integer getSubject();
        String getCreatedBy();
        java.sql.Timestamp getCreatedAt();
        java.util.Date getDeletedAt();
        String getDeletedBy();
        Boolean getDeletedFlag();
        java.util.Date getUpdatedAt();
    }

}
