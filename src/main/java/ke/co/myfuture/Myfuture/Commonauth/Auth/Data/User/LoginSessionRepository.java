package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginSessionRepository extends JpaRepository<LoginSession, Long> {
    Optional<LoginSession> findByRefreshToken(String refreshToken);
}
