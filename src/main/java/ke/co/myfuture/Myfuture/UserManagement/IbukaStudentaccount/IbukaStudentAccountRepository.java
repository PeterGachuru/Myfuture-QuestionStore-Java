package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface IbukaStudentAccountRepository extends JpaRepository<IbukaStudentAccount, Long> {
    @Query(value = "SELECT * FROM(SELECT * FROM ibuka_student_account WHERE name LIKE CONCAT('%',:search,'%')  AND classlevel BETWEEN (:classlevel - 1) AND (:classlevel + 1) " +
            "  AND id <> :studentId ORDER BY id DESC) AS m LIMIT :count", nativeQuery = true)
    List<IbukaStudentAccount> contestInvitees(@Param("search") String search, @Param("count")  Integer count,
                                              @Param("classlevel") Long classlevel, @Param("studentId")  Long studentId);
    List<IbukaStudentAccount> findByParent(Long parentId);
    Optional<IbukaStudentAccount> findByShareCode(String shareCode);

    List<IbukaStudentAccount> findTop200ByOrderByCreatedAtDesc();

    List<IbukaStudentAccount> findBySenderIsNull();


//    @Query(value = """
//            SELECT * FROM(SELECT * FROM student_account WHERE name LIKE CONCATE('%',:search,'%') AND classlevel = :classlevel
//            AND id <> :student_id ORDER BY id DESC) AS m LIMIT :count
//            """, nativeQuery = true)
//    List<IbukaStudentAccount> findForContest(Long parentId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
             INSERT INTO ibuka_daily_score(student_id, total_score, date) SELECT id, total_score, CURDATE()
                 FROM (SELECT * FROM ibuka_student_account 
                 WHERE id NOT IN(SELECT ibuka_daily_score.student_id FROM ibuka_daily_score INNER JOIN ibuka_student_account ON ibuka_student_account.id = ibuka_daily_score.student_id WHERE date = CURDATE() )
                  AND id NOT IN( SELECT ibuka_student_account.id FROM (SELECT latest_update.date, ibuka_daily_score.total_score, latest_update.student_id FROM (SELECT MAX(date) AS date, student_id FROM ibuka_daily_score GROUP BY student_id) latest_update
                           INNER JOIN ibuka_daily_score ON ibuka_daily_score.student_id = latest_update.student_id AND ibuka_daily_score.date = latest_update.date) latest_score
             INNER JOIN ibuka_student_account ON ibuka_student_account.id = latest_score.student_id AND   ibuka_student_account.total_score = latest_score.total_score)) AS scores 
            """)
    void analyzeScores();
}