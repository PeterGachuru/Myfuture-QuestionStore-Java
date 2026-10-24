package ke.co.myfuture.Myfuture.Commonauth.Auth.User;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Date;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@NonNull String e);

    List<User> findByStatus(@NonNull String s);

    @Query(nativeQuery = true, value = "select status, count(status) as `count` from user group by status")
    List<Analytics> getAnalytics();

    @Query(nativeQuery = true, value = "select email from user")
    List<String> getAllEmailAddresses();

    List<User> findByDeletedDateIsNullOrderByIdDesc(PageRequest of);

    @Query(value = "SELECT DATE(creation_date), COUNT(*) FROM user WHERE creation_date >= :startDate GROUP BY DATE(creation_date) ORDER BY DATE(creation_date)", nativeQuery = true)
    List<Object[]> countCreatedAccountsPerDay(@Param("startDate") Date startDate);

    @Query(value = "SELECT CASE WHEN :hourly = true THEN DATE_FORMAT(creation_date, '%Y-%m-%d %H:00:00') ELSE DATE_FORMAT(creation_date, '%Y-%m-%d') END AS bucket, COUNT(*) FROM user WHERE creation_date >= :startDate AND creation_date < :endDate GROUP BY bucket ORDER BY bucket", nativeQuery = true)
    List<Object[]> dashboardCreatedAccounts(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("hourly") boolean hourly);

    interface Analytics {
        String getStatus();
        Integer getCount();
    }
}
