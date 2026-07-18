package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IbukaDailyScoreRepository
        extends JpaRepository<IbukaDailyScore, IbukaDailyScoreId> {

    List<IbukaDailyScore> findByStudentId(Long studentId);

    void deleteByStudentId(Long studentId);

}