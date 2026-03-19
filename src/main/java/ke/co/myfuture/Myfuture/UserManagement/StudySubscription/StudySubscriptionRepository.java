package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudySubscriptionRepository extends JpaRepository<StudySubscription, Long> {

    StudySubscription findByTransactionCode(String transactionCode);

    List<StudySubscription> findTop10ByEmailAddressOrderByCreatedAtDesc(String emailAddress);

    @Query("SELECT s FROM StudySubscription s ORDER BY s.createdAt DESC")
    List<StudySubscription> findLatestSubscriptions(Pageable pageable);

    // Optional helper method to simplify usage
    default List<StudySubscription> findLatest300() {
        return findLatestSubscriptions(PageRequest.of(0, 300));
    }

    List<StudySubscription> findAllByOrderByCreatedAtDesc(Pageable pageable);

}