package ke.co.myfuture.Myfuture.Commonauth.Auth.User.PasswordReset;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetDTO, Long> {
    Optional<PasswordResetDTO> findByEmailAndOtp(String email, String otp);
}
