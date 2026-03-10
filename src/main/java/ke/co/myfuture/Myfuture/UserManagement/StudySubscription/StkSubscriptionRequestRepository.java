package ke.co.myfuture.Myfuture.UserManagement.StudySubscription;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StkSubscriptionRequestRepository extends JpaRepository<StkSubscriptionRequest, Long> {
    @Query("SELECT s FROM StkSubscriptionRequest s ORDER BY s.createdAt DESC")
    List<StkSubscriptionRequest> findLatestStkRequests(Pageable pageable);

    // Optional helper method to simplify usage
    default List<StkSubscriptionRequest> findLatest300() {
        return findLatestStkRequests(PageRequest.of(0, 300));
    }

    List<StkSubscriptionRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
