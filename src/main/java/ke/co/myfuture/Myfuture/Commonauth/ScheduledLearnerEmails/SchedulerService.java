package ke.co.myfuture.Myfuture.Commonauth.ScheduledLearnerEmails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public void persistScheduledEmail(String recipient, String subject, String body, LocalDateTime scheduledTime, SenderService senderService) {
        if (!isValidEmail(recipient)) {
            System.out.println("Invalid email address: " + recipient);
            return;
        }
        ScheduledEmails scheduledEmail = new ScheduledEmails();
        scheduledEmail.setRecipient(recipient);
        scheduledEmail.setSubject(subject);
        scheduledEmail.setBody(body);
        scheduledEmail.setSenderService(senderService);
        scheduledEmail.setScheduledTime(scheduledTime.plusSeconds(1)); // Schedule for the next second after the week's end
        scheduledEmail.setExpiresAfterSeconds((long) (60*60*24*3)); // Email expires after 3 days (in seconds)
        scheduledEmail.setSent(false);
        scheduledEmail.setLastAttemptStatus(LastStatus.PENDING);

        scheduledEmailsRepo.save(scheduledEmail);
        System.out.println("Email scheduled for recipient: " + recipient);
    }
}
