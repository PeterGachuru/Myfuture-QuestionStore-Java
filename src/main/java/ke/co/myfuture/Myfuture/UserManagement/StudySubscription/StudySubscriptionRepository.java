package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudySubscriptionRepository extends JpaRepository<StudySubscription, Long> {

    StudySubscription findByTransactionCode(String transactionCode);

    StudySubscription findByEmailAddress(String emailAddress);
}