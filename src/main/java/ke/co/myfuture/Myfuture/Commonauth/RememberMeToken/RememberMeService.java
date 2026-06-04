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
    private static final long EXPIRY_DURATION =
            30L * 24 * 60 * 60 * 1000;

    public String createToken(Long userId) {

        // optional: remove old tokens for same user (1 device rule)
        rememberMeRepo.deleteByUserId(userId);

        String token = UUID.randomUUID().toString();

        Date expiryDate = new Date(System.currentTimeMillis() + EXPIRY_DURATION);

        RememberMeToken entity = new RememberMeToken();
        entity.setUserId(userId);
        entity.setToken(token);
        entity.setExpiryDate(expiryDate);
        entity.setCreatedAt(new Date());

        rememberMeRepo.save(entity);

        return token;
    }

    public Optional<Long> validateToken(String token) {

        Optional<RememberMeToken> record = rememberMeRepo.findByToken(token);

        if (record.isEmpty()) return Optional.empty();

        RememberMeToken t = record.get();

        // check expiry
        if (t.getExpiryDate().before(new Date())) {
            rememberMeRepo.deleteByToken(token);
            return Optional.empty();
        }

        return Optional.of(t.getUserId());
    }

    public void revokeToken(String token) {
        rememberMeRepo.deleteByToken(token);
    }

    public void revokeUserTokens(Long userId) {
        rememberMeRepo.deleteByUserId(userId);
    }
}
