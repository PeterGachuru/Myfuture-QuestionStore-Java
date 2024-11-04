package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StudySubscriptionService {

    @Autowired
    private StudySubscriptionRepository studySubscriptionRepository;

    public StudySubscription createSubscription(StudySubscription subscription) {
        if (subscription.getId() != null && subscription.getId() > 0)
            return null;
        return studySubscriptionRepository.save(subscription);
    }

    public List<StudySubscription> getAllSubscriptions() {
        return studySubscriptionRepository.findAll();
    }

    public StudySubscription getSubscriptionById(Long id) {
        return studySubscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }

    public StudySubscription getSubscriptionByTransactionCode(String transactionCode) {
        return studySubscriptionRepository.findByTransactionCode(transactionCode);
    }

    public StudySubscription getSubscriptionByEmailAddress(String emailAddress) {
        return studySubscriptionRepository.findByEmailAddress(emailAddress);
    }

    public StudySubscription updateSubscription(Long id, BigDecimal payAmount, int numberOfDays, String subscriptionType) {
        StudySubscription subscription = getSubscriptionById(id);
        subscription.setPayAmount(payAmount);
        subscription.setNumberOfDays(numberOfDays);
        subscription.setSubscriptionType(subscriptionType);
        return studySubscriptionRepository.save(subscription);
    }

    public void deleteSubscription(Long id) {
        studySubscriptionRepository.deleteById(id);
    }
}
