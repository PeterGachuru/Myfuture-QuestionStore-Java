package ke.co.myfuture.Myfuture.Commonauth.RememberMeToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RememberMeRepository extends JpaRepository<RememberMeToken, Long> {

    Optional<RememberMeToken> findByToken(String token);

    void deleteByUserId(Long userId);

//    void deleteByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM RememberMeToken t WHERE t.token = :token")
    void deleteByToken(@Param("token") String token);
}