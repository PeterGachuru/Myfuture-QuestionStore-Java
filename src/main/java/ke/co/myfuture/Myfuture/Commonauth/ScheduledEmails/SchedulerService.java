package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Pattern;

import java.time.LocalDateTime;

@Service
public class SchedulerService {
    @Autowired
    ScheduledEmailsRepo scheduledEmailsRepo;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    public Boolean persistScheduledEmail(String recipient, String subject, String body, String fromName,
                                         LocalDateTime scheduledTime, SenderService senderService) {
        return persistScheduledEmail(recipient, subject, body, fromName, scheduledTime,
                senderService == null ? null : senderService.name());
    }

    /**
     * Stores an email using a free-form category string. New lifecycle stages can
     * introduce new categories without changing a Java enum or the database schema.
     */
    public Boolean persistScheduledEmail(String recipient, String subject, String body, String fromName,
                                         LocalDateTime scheduledTime, String senderService) {
        if (!isValidEmail(recipient)) {
            System.out.println("Invalid email address: " + recipient);
            return false;
        }
        if (senderService == null || senderService.isBlank() || senderService.length() > 100) {
            System.out.println("Invalid email category: " + senderService);
            return false;
        }
        ScheduledEmails scheduledEmail = new ScheduledEmails();
        scheduledEmail.setRecipient(recipient.trim());
        scheduledEmail.setSubject(subject);
        scheduledEmail.setFromName(fromName);
        scheduledEmail.setBody(body);
        scheduledEmail.setSenderService(senderService.trim());
        scheduledEmail.setScheduledTime(Date.from(scheduledTime.plusSeconds(1).atZone(ZoneId.systemDefault()).toInstant()));
        scheduledEmail.setExpiresAfterSeconds(60L * 60 * 24 * 3);
        scheduledEmail.setSent(false);
        scheduledEmail.setLastAttemptStatus(LastStatus.PENDING);

        scheduledEmailsRepo.save(scheduledEmail);
        System.out.println("Email scheduled for recipient: " + recipient);
        return true;
    }

    public Page<ScheduledEmailsRepo.ScheduledEmailsProjection> getPaginatedEmails(Pageable pageable) {
        return scheduledEmailsRepo.findAllProjected(pageable);
    }

//    public void persistScheduledEmail(String toEmail, String subject, String emailContent, LocalDateTime localDateTime, SenderService senderService) {
//
//    }
}
