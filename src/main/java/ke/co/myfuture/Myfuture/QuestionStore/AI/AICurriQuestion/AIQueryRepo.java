package ke.co.myfuture.Myfuture.QuestionStore.AI.AICurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AIQueryRepo extends JpaRepository<AIQuery, Long> {

    @Query(value = "SELECT * FROM aiquery WHERE aimodel = 'gpt-3.5-turbo-0125' AND  query_purpose = 'curri_question' ", nativeQuery = true)
    Page<AIQuery> findAllForQuestions(Pageable paging);
}
