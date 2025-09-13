package ke.co.myfuture.Myfuture.Commonauth.Auth.User.PasswordReset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class PasswordResetService {
    @Autowired
    PasswordResetRepository passwordResetRepository;


    public boolean validateResetOtp(String email, String otp) {
        Optional<PasswordReset> reset = passwordResetRepository.findByEmailAndOtp(email, otp);
        if (reset.isPresent()) {
            Timestamp creationTime = reset.get().getCreationDate();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            long diffInMillis = now.getTime() - creationTime.getTime();
            long diffInMinutes = diffInMillis / (60 * 1000);

            return diffInMinutes <= 10; // Valid for 10 minutes
        }
        return false;
    }

}
