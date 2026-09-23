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
}
