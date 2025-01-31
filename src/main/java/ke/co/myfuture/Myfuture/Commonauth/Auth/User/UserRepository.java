package ke.co.myfuture.Myfuture.Commonauth.Auth.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@NonNull String e);

    List<User> findByStatus(@NonNull String s);

    @Query(nativeQuery = true, value = "select status, count(status) as `count` from user group by status")
    List<Analytics> getAnalytics();

    @Query(nativeQuery = true, value = "select email from user")
    List<String> getAllEmailAddresses();

    interface Analytics {
        String getStatus();
        Integer getCount();
    }
}
