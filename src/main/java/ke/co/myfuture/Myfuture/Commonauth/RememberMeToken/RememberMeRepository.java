package ke.co.myfuture.Myfuture.Commonauth.RememberMeToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RememberMeRepository extends JpaRepository<RememberMeToken, Long> {

    Optional<RememberMeToken> findByToken(String token);

    void deleteByUserId(Long userId);

    void deleteByToken(String token);
}