package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GooglePlayRenewalRepository extends JpaRepository<GooglePlayRenewal, Long> {
    boolean existsByPackageNameAndOrderId(String packageName, String orderId);
}
