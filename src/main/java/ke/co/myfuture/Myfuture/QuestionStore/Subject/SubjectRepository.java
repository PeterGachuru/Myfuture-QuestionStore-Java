package ke.co.myfuture.Myfuture.QuestionStore.Subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query(value = "select * from subject where id in (select subject from curri_topic where curri_level = :level)", nativeQuery = true)
    List<Subject> subjectsByClassLevel(@Param("level") Long classLevel);
}
