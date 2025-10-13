package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GooglePlaySubscriptionRepository extends JpaRepository<GooglePlaySubscription, Long> {
    Optional<GooglePlaySubscription> findByPurchaseToken(String purchaseToken);
    boolean existsByPurchaseToken(String purchaseToken);
}