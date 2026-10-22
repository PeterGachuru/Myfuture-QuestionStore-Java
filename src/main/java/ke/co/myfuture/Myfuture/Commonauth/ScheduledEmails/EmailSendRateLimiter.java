package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailSendRateLimiter {
    private final EmailSendRateLockRepository lockRepository;
    private final EmailSendRateAttemptRepository attemptRepository;

    @Value("${email.delivery.max-per-hour:180}")
    private int maxPerHour;

    public EmailSendRateLimiter(EmailSendRateLockRepository lockRepository,
                                EmailSendRateAttemptRepository attemptRepository) {
        this.lockRepository = lockRepository;
        this.attemptRepository = attemptRepository;
    }

    /** Reserves one slot per recipient, serialized across app instances using the database lock row. */
    @Transactional(transactionManager = "commonauthTransactionManager")
    public void reserveRecipients(int recipientCount) {
        if (recipientCount <= 0) return;

        lockRepository.ensureSingletonRow();
        lockRepository.lockSingletonRow();

        Date now = new Date();
        Date cutoff = new Date(now.getTime() - 60L * 60L * 1000L);
        attemptRepository.deleteBefore(cutoff);
        long usedInLastHour = attemptRepository.countSince(cutoff);
        if (usedInLastHour + recipientCount > maxPerHour) {
            throw new EmailRateLimitExceededException("Hourly email sending limit reached ("
                    + maxPerHour + " recipients per rolling hour).");
        }

        List<EmailSendRateAttempt> reservations = new ArrayList<>(recipientCount);
        for (int i = 0; i < recipientCount; i++) {
            reservations.add(new EmailSendRateAttempt(now));
        }
        attemptRepository.saveAll(reservations);
        attemptRepository.flush();
    }
}
