package ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPasswordRepo extends JpaRepository<UserPassword, Long> {
    List<UserPassword> findAllByIsExpired(Boolean isExpired);

    @Query(nativeQuery = true, value = "SELECT * FROM user_password WHERE user_id = :userId and is_expired = 0")
    List<UserPassword> findByUserId(Long userId);
}
