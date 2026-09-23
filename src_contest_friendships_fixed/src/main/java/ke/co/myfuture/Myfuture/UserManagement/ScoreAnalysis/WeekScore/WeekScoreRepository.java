package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.WeekScore;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import java.util.List;

public interface WeekScoreRepository extends JpaRepository<WeekScore, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            insert into week_score(created_at, name, student_id, class_level_id, school, score) SELECT CURRENT_TIMESTAMP, k.name, k.id AS student_id, k.classlevel AS class_level_id, k.school, k.score FROM (SELECT ibuka_student_account.*, (ibuka_student_account.total_score - COALESCE(latest_score.total_score,0)) AS score FROM ibuka_student_account 
            LEFT JOIN (SELECT latest_update.date, ibuka_daily_score.total_score, latest_update.student_id FROM (SELECT MAX(date) AS date, student_id FROM ibuka_daily_score 
            WHERE date <= DATE_SUB(DATE(NOW()), INTERVAL DAYOFWEEK(NOW())-1 DAY) GROUP BY student_id) latest_update INNER JOIN ibuka_daily_score 
            ON ibuka_daily_score.student_id = latest_update.student_id AND ibuka_daily_score.date = latest_update.date) latest_score ON latest_score.student_id = ibuka_student_account.id 
            ORDER BY score DESC LIMIT 500) AS k WHERE score > 0;
            """)
    void analyzeWeekScore();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "TRUNCATE TABLE week_score")
    void clearEveryThing();

    List<WeekScore> findAllByOrderByScoreDesc(Pageable pageable);
}