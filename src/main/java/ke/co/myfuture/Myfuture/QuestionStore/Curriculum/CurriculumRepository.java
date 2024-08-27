package ke.co.myfuture.Myfuture.QuestionStore.Curriculum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    @Query(value = """ 
            SELECT * FROM curriculum 
            ORDER BY id 
            """, nativeQuery = true)
    List<Curriculum> getAllCurriculums();

    @Query(value = """ 
            SELECT * FROM curriculum WHERE 
                         id IN(SELECT curriculum FROM curri_level 
                                WHERE id IN(SELECT curri_level FROM curri_topic 
                                WHERE id IN(SELECT subtopic FROM curri_question 
                                WHERE reviewed = '0'))) 
            ORDER BY id 
            """, nativeQuery = true)
    List<Curriculum> getAllCurriculumsWithUnapprovedQuestions();
}
