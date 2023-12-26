package ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPasswordRepo extends JpaRepository<UserPassword, Long> {
    List<UserPassword> findAllByIsExpired(Boolean isExpired);
}
