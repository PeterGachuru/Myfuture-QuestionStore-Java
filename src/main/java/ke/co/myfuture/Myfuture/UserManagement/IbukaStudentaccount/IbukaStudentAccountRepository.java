package ke.co.myfuture.Myfuture.UserManagement.IbukaStudentaccount;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import jakarta.transaction.Transactional;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IbukaStudentAccountRepository extends JpaRepository<IbukaStudentAccount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM IbukaStudentAccount s WHERE s.id = :id")
    Optional<IbukaStudentAccount> findByIdForScoreUpdate(@Param("id") Long id);

    @Query(value = """
        SELECT * 
        FROM ibuka_student_account
        WHERE name LIKE CONCAT('%', :search, '%')
        AND classlevel IN (:classlevels)
        AND id <> :studentId
        ORDER BY id DESC
        LIMIT :count
        """, nativeQuery = true)
    List<IbukaStudentAccount> contestInvitees(
            @Param("search") String search,
            @Param("count") Integer count,
            @Param("classlevels") List<Long> classlevels,
            @Param("studentId") Long studentId);

    @Query(value = """
        SELECT *
        FROM ibuka_student_account
        WHERE name LIKE CONCAT('%', :search, '%')
          AND classlevel = :classlevel
          AND id <> :studentId
        ORDER BY id DESC
        LIMIT :count
        """, nativeQuery = true)
    List<IbukaStudentAccount> contestInviteesExactClass(
            @Param("search") String search,
            @Param("count") Integer count,
            @Param("classlevel") Long classlevel,
            @Param("studentId") Long studentId);
    List<IbukaStudentAccount> findByParent(Long parentId);

    @Query("SELECT COUNT(DISTINCT s.parent) FROM IbukaStudentAccount s WHERE s.parent IN :parentIds")
    long countDistinctParentsByParentIn(@Param("parentIds") List<Long> parentIds);
    Optional<IbukaStudentAccount> findByShareCode(String shareCode);

    List<IbukaStudentAccount> findTop200ByOrderByCreatedAtDesc();

    List<IbukaStudentAccount> findBySenderIsNullAndIdGreaterThanOrderByIdAsc(Long afterId, Pageable pageable);


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

    List<IbukaStudentAccount> findAllByOrderByIdDesc(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE IbukaStudentAccount s SET s.recentActivity = CURRENT_TIMESTAMP WHERE s.id = :id")
    int updateRecentActivity(@Param("id") Long id);

    @Query("""
        SELECT DATE(e.createdAt), COUNT(e)
        FROM IbukaStudentAccount e
        WHERE e.createdAt >= :startDate
        GROUP BY DATE(e.createdAt)
        ORDER BY DATE(e.createdAt)
    """)
    List<Object[]> countPerDay(@Param("startDate") Date startDate);

    @Query(value = "SELECT CASE WHEN :hourly = true THEN DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00') ELSE DATE_FORMAT(created_at, '%Y-%m-%d') END AS bucket, COUNT(*) FROM ibuka_student_account WHERE created_at >= :startDate AND created_at < :endDate GROUP BY bucket ORDER BY bucket", nativeQuery = true)
    List<Object[]> dashboardRegistrations(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("hourly") boolean hourly);

    List<IbukaStudentAccount> findByParentUsernameOrderByIdDesc(String parentUsername);

    boolean existsByParent(Long parent);
}
