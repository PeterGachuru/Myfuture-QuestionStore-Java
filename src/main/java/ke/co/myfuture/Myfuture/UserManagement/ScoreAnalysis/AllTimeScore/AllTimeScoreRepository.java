package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.AllTimeScore;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface AllTimeScoreRepository extends JpaRepository<AllTimeScore, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
insert into all_time_score( name, student_id, class_level_id, school, score) SELECT k.name, k.id AS student_id,
 k.classlevel AS class_level_id, k.school, k.total_score FROM ibuka_student_account k WHERE `total_score` > 0 
 ORDER BY `total_score` DESC LIMIT 0, 500;
            """)
    void analyzeScore();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "TRUNCATE TABLE all_time_score")
    void clearEveryThing();


    List<AllTimeScore> findAllByOrderByScoreDesc(Pageable pageable);
}
