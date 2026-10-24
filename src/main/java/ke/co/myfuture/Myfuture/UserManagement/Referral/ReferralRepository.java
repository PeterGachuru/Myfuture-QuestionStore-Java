package ke.co.myfuture.Myfuture.UserManagement.Referral;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findAllByOrderByIdDesc(Pageable pageable);

    List<Referral> findByReferrerStudentId(Long id);

    @Query("""
    SELECT DATE(e.createdAt), COUNT(e)
    FROM Referral e
    WHERE e.createdAt >= :startDate
    GROUP BY DATE(e.createdAt)
    ORDER BY DATE(e.createdAt)
""")
    List<Object[]> countPerDay(@Param("startDate") Date startDate);

    @Query(value = "SELECT CASE WHEN :hourly = true THEN DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00') ELSE DATE_FORMAT(created_at, '%Y-%m-%d') END AS bucket, COUNT(*) FROM referral WHERE created_at >= :startDate AND created_at < :endDate GROUP BY bucket ORDER BY bucket", nativeQuery = true)
    List<Object[]> dashboardReferrals(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("hourly") boolean hourly);
}
