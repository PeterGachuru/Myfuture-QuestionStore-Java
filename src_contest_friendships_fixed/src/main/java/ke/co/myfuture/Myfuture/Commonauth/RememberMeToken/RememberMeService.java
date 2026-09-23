package ke.co.myfuture.Myfuture.Commonauth.RememberMeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RememberMeService {
    @Autowired
    private RememberMeRepository rememberMeRepo;

    // 30 days validity
    public static final long REMEMBER_ME_EXPIRY_DURATION =
            12* 30L * 24 * 60 * 60 * 1000;

    public String createToken(Long userId) {

        // optional: remove old tokens for same user (1 device rule)
        rememberMeRepo.deleteByUserId(userId);

        String token = UUID.randomUUID().toString();

        Date expiryDate = new Date(System.currentTimeMillis() + REMEMBER_ME_EXPIRY_DURATION);

        RememberMeToken entity = new RememberMeToken();
        entity.setUserId(userId);
        entity.setToken(token);
        entity.setExpiryDate(expiryDate);
        entity.setCreatedAt(new Date());

        rememberMeRepo.save(entity);

        return token;
    }

    public void addStudent(String token, Long studentId) {
        Optional<RememberMeToken> record = rememberMeRepo.findByToken(token);

        if (record.isEmpty()) return ;

        RememberMeToken t = record.get();
        t.setStudentId(studentId);
        rememberMeRepo.save(t);
    }

    public Optional<RememberMeToken> validateToken(String token) {

        Optional<RememberMeToken> record = rememberMeRepo.findByToken(token);

        if (record.isEmpty()) return Optional.empty();

        RememberMeToken t = record.get();

        // check expiry
        if (t.getExpiryDate().before(new Date())) {
            rememberMeRepo.deleteByToken(token);
            return Optional.empty();
        }

        return Optional.of(t);
    }

    public void revokeToken(String token) {
        rememberMeRepo.deleteByToken(token);
    }

    public void revokeUserTokens(Long userId) {
        rememberMeRepo.deleteByUserId(userId);
    }
}
