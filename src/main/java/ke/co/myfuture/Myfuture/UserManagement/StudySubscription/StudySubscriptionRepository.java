package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StudySubscriptionRepository extends JpaRepository<StudySubscription, Long> {

    StudySubscription findByTransactionCode(String transactionCode);

    List<StudySubscription> findTop10ByEmailAddressOrderByCreatedAtDesc(String emailAddress);

    List<StudySubscription> findByEmailAddressAndEndDateAfterOrderByEndDateDesc(String emailAddress, Date now);

    List<StudySubscription> findByEmailAddressAndCreatedAtAfterOrderByCreatedAtDesc(String emailAddress, Date since);

    @Query("SELECT s FROM StudySubscription s ORDER BY s.createdAt DESC")
    List<StudySubscription> findLatestSubscriptions(Pageable pageable);

    // Optional helper method to simplify usage
    default List<StudySubscription> findLatest300() {
        return findLatestSubscriptions(PageRequest.of(0, 300));
    }

    List<StudySubscription> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = """
        SELECT DATE(created_at), COUNT(*)
        FROM study_subscription
        WHERE created_at >= :startDate
        GROUP BY DATE(created_at)
        ORDER BY DATE(created_at)
    """, nativeQuery = true)
    List<Object[]> countTransactionsPerDay(@Param("startDate") Date startDate);

    @Query(value = """
        SELECT DATE(created_at), SUM(pay_amount)
        FROM study_subscription
        WHERE created_at >= :startDate
        GROUP BY DATE(created_at)
        ORDER BY DATE(created_at)
    """, nativeQuery = true)
    List<Object[]> sumTransactionsPerDay(@Param("startDate") Date startDate);

    @Query(value = "SELECT CASE WHEN :hourly = true THEN DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00') ELSE DATE_FORMAT(created_at, '%Y-%m-%d') END AS bucket, COUNT(*) FROM study_subscription WHERE created_at >= :startDate AND created_at < :endDate GROUP BY bucket ORDER BY bucket", nativeQuery = true)
    List<Object[]> dashboardTransactions(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("hourly") boolean hourly);

    @Query(value = "SELECT CASE WHEN :hourly = true THEN DATE_FORMAT(created_at, '%Y-%m-%d %H:00:00') ELSE DATE_FORMAT(created_at, '%Y-%m-%d') END AS bucket, SUM(pay_amount) FROM study_subscription WHERE created_at >= :startDate AND created_at < :endDate GROUP BY bucket ORDER BY bucket", nativeQuery = true)
    List<Object[]> dashboardRevenue(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("hourly") boolean hourly);

}
