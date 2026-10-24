package ke.co.myfuture.Myfuture.Commonauth.Install;

import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscription;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface Install2Repository extends JpaRepository<Install, Long> {
    @Query("SELECT DISTINCT i.fcmToken FROM Install i WHERE LOWER(i.accountEmail) IN :emails AND i.platform = 'myfuture_android' AND i.fcmToken IS NOT NULL AND i.fcmToken <> ''")
    List<String> findAndroidFcmTokensByAccountEmails(@Param("emails") Set<String> normalizedEmails);

    @Modifying
    @Transactional(transactionManager = "commonauthTransactionManager")
    @Query("UPDATE Install i SET i.fcmToken = NULL WHERE i.fcmToken = :token")
    int clearFcmToken(@Param("token") String token);

    @Query("SELECT s FROM Install s ORDER BY s.id DESC")
    List<Install> findLatestInstall(Pageable pageable);

    // Optional helper method to simplify usage
    default List<Install> findLatest500() {
        return findLatestInstall(PageRequest.of(0, 500));
    }

    List<Install> findTop500ByPlatformOrderByCreatedAtDesc(String platform);

        @Query(value = """
        SELECT DATE(created_at), COUNT(*)
        FROM install
        WHERE created_at >= :startDate
        GROUP BY DATE(created_at)
        ORDER BY DATE(created_at)
    """, nativeQuery = true)
        List<Object[]> countInstallsPerDay(@Param("startDate") Date startDate);

    @Query(value = """
    SELECT DATE(created_at), COUNT(*)
    FROM install
    WHERE created_at >= :startDate
    AND account_id IS NULL
    GROUP BY DATE(created_at)
    ORDER BY DATE(created_at)
""", nativeQuery = true)
    List<Object[]> countAnonymousInstallsPerDay(@Param("startDate") Date startDate);

    @Query(value = """
    SELECT DATE(created_at), COUNT(*)
    FROM install
    WHERE created_at >= :startDate
    AND account_id IS NOT NULL
    GROUP BY DATE(created_at)
    ORDER BY DATE(created_at)
""", nativeQuery = true)
    List<Object[]> countInstallsWithAccountsPerDay(@Param("startDate") Date startDate);

    @Query(value = """
    SELECT DATE(account_added_at), COUNT(*)
    FROM install
    WHERE account_added_at >= :startDate
    AND account_id > 0 
    GROUP BY DATE(account_added_at)
    ORDER BY DATE(account_added_at)
""", nativeQuery = true)
    List<Object[]> accountsAddedPerDay(@Param("startDate") Date startDate);

    @Query(value = """
    SELECT DATE(created_at),
           (COUNT(CASE WHEN account_id IS NOT NULL THEN 1 END) * 100.0) / COUNT(*) as percentage
    FROM install
    WHERE created_at >= :startDate
    GROUP BY DATE(created_at)
    ORDER BY DATE(created_at)
""", nativeQuery = true)
    List<Object[]> accountConversionPerDay(@Param("startDate") Date startDate);
}
