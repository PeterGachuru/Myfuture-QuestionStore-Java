package ke.co.myfuture.Myfuture.UserManagement.ScoreAnalysis.TodayScore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface TodayScoreRepository extends JpaRepository<TodayScore, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            insert into today_score( name, student_id, class_level_id, school, score) SELECT k.name, k.id AS student_id, k.classlevel AS class_level_id, k.school, k.score FROM (SELECT ibuka_student_account.*, (ibuka_student_account.total_score - COALESCE(latest_score.total_score,0)) AS score\s
            FROM ibuka_student_account LEFT JOIN (SELECT latest_update.date, ibuka_daily_score.total_score, latest_update.student_id FROM (SELECT MAX(date) AS date, student_id\s
            FROM ibuka_daily_score  GROUP BY student_id) latest_update INNER JOIN ibuka_daily_score ON ibuka_daily_score.student_id = latest_update.student_id AND ibuka_daily_score.date = latest_update.date) latest_score\s
            ON latest_score.student_id = ibuka_student_account.id ORDER BY score DESC LIMIT 500) AS k WHERE score > 0;
            """)
    void analyzeScore();

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "TRUNCATE TABLE today_score")
    void clearEveryThing();
}
