package ke.co.myfuture.Myfuture.UserManagement.Contest.Contestquestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestQuestionRepository extends JpaRepository<ContestQuestion, Long> {
    List<ContestQuestion> findByContestOrderByIdAsc(Long contest);

    int countByContest(Long contest);
}
