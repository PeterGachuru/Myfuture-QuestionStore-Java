package ke.co.myfuture.Myfuture.QuestionStore.Subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query(value = "select * from subject where id in (select subject from curri_topic where curri_level = :level)", nativeQuery = true)
    List<Subject> subjectsWithTopicsByClassLevel(@Param("level") Long classLevel);

    @Query(value = "select * from subject where id in (select subject from subject_level where curri_level = :level AND deleted_flag = 0) order by name", nativeQuery = true)
    List<Subject> subjectsByClassLevel(@Param("level") Long classLevel);

    @Query(value = """
            select * from subject where id in (SELECT subject FROM curri_topic 
                                WHERE id IN(SELECT subtopic FROM curri_question 
                                WHERE reviewed = '0')) ORDER BY id 
            """, nativeQuery = true)
    List<Subject> getAllWithUnapprovedQuestions(@Param("level") Long level);

    @Query(value = """
            select name from subject where id = :subject
            """, nativeQuery = true)
    String getName(Long subject);

    Optional<Subject> findByName(String name);

    List<Subject> findAllByOrderByNameAsc();
}