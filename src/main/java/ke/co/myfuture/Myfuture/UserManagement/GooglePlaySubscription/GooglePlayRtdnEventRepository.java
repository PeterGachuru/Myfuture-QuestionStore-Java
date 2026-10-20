package ke.co.myfuture.Myfuture.UserManagement.GooglePlaySubscription;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GooglePlayRtdnEventRepository extends JpaRepository<GooglePlayRtdnEvent, Long> {
    boolean existsByMessageId(String messageId);
    List<GooglePlayRtdnEvent> findTop50ByStatusOrderByCreatedAtAsc(String status);
}
