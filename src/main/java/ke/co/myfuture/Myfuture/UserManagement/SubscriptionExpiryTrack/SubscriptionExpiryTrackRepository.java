package ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionExpiryTrackRepository extends JpaRepository<SubscriptionExpiryTrack, Long> {
    Optional<SubscriptionExpiryTrack> findByParent(Long parent);

    Optional<SubscriptionExpiryTrack> findByParentUsername(String parentUsername);

    Optional<SubscriptionExpiryTrack> findByInstallId(Long installId);

    List<SubscriptionExpiryTrack> findAllByOrderByExpiryDateDesc(Pageable pageable);
}

