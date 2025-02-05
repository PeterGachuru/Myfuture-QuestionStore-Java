package ke.co.myfuture.Myfuture.QuestionStore.SubjectLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubjectLevelRepository extends JpaRepository<SubjectLevel, Long> {

    @Query(nativeQuery = true, value = "select * from subject_level where curri_level = :classlevelId AND subject = :subjectId")
    Optional<SubjectLevel> findByLevelAndSubject(Long classlevelId, Long subjectId);
}
