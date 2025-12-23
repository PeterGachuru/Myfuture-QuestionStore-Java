package ke.co.myfuture.Myfuture.Commonauth.Install;

import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscription;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Install2Repository extends JpaRepository<Install, Long> {
    @Query("SELECT s FROM Install s ORDER BY s.id DESC")
    List<Install> findLatestInstall(Pageable pageable);

    // Optional helper method to simplify usage
    default List<Install> findLatest300() {
        return findLatestInstall(PageRequest.of(0, 300));
    }
}
