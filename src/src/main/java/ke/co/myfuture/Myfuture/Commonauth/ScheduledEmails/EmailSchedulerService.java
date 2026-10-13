package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import ke.co.myfuture.Myfuture.Commonauth.Utils.CustomMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EmailSchedulerService {
    @Autowired
    private ScheduledEmailsRepo emailRepository;

    @Value("${production:false}")
    private boolean inProd;

    // Allows UAT to create queued emails while preventing real delivery.
    // Override explicitly only when email delivery is intended.
    @Value("${email.delivery.enabled:${production:false}}")
    private boolean emailDeliveryEnabled;
    @Autowired
    private CustomMailSender customMailSender; // External service for sending emails

    // Save a new scheduled email
    public ScheduledEmails scheduleEmail(ScheduledEmails email) {
        return emailRepository.save(email);
    }

    // Process queued email promptly. Lifecycle campaigns queue on their own scan;
    // this worker sends due emails once per minute.
    @Scheduled(initialDelay = 10000, fixedDelay = 60000) // Runs every minute
    public void processPendingEmails() {
        if (!emailDeliveryEnabled) {
            System.out.println("[EmailSchedulerService] Email delivery disabled; queued emails are retained.");
            return;
        }
        System.out.println("[EmailSchedulerService] Checking pending emails at " + new Date());

        if (emailRepository.countAttemptsInLast50Minutes(new Date()) > 150) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<ScheduledEmails> pendingEmails = emailRepository.findPendingEmails(new Date());

        System.out.println("Found "+pendingEmails.size()+" emails to send");
        for (ScheduledEmails email : pendingEmails) {
            try {
                System.out.println(LocalDateTime.now()+": To send scheduled email");
                email.setAttemptedSendAt(new Date());
                email.setLastAttemptStatus(null);
                emailRepository.save(email);
//                Boolean status = customMailSender.sendEmail(email.getSubject(),
//                        email.getBody(),
//                        new String[]{email.getRecipient()}, new String[]{}, new String[]{}, email.getFromName());

                Boolean status = customMailSender.sendEmail(email.getSubject(),
                        email.getBody(),
                        new String[]{email.getRecipient()}, new String[]{}, new String[]{}, email.getFromName());

                email.setSent(status);
                email.setLastAttemptStatus(status? LastStatus.SUCCESS: LastStatus.FAILED);
                email.setTimeSent(new Date());
                emailRepository.save(email);
            } catch (Exception e) {
                // Handle email sending failure (e.g., logging)
                email.setLastAttemptStatus(LastStatus.FAILED);
                emailRepository.save(email);
                System.err.println("Failed to send email to: " + email.getRecipient());
            }
        }
    }
}
