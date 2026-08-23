package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import org.springframework.stereotype.Service;

@Service
public class GooglePlaySubscriptionService {

    private final GooglePlaySubscriptionRepository repository;

    public GooglePlaySubscriptionService(GooglePlaySubscriptionRepository repository) {
        this.repository = repository;
    }

    public GooglePlaySubscription saveSubscription(GooglePlaySubscription subscription) {
        // Optional: check for duplicates before saving
        if (repository.existsByPurchaseToken(subscription.getPurchaseToken())) {
            throw new IllegalArgumentException("Duplicate subscription detected");
        }
        GooglePlaySubscription savedGooglePlaySubscription = repository.save(subscription);

        return savedGooglePlaySubscription;
    }
}