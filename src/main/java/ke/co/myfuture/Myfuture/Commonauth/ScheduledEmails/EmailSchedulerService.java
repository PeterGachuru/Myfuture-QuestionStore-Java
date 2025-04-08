package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import ke.co.myfuture.Myfuture.Commonauth.Utils.CustomMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EmailSchedulerService {
    @Autowired
    private ScheduledEmailsRepo emailRepository;

    @Autowired
    private CustomMailSender customMailSender; // External service for sending emails

    // Save a new scheduled email
    public ScheduledEmails scheduleEmail(ScheduledEmails email) {
        return emailRepository.save(email);
    }

    // Periodically check for pending emails to send
//    @Scheduled(initialDelay = 0,fixedRate = 60000) // Runs every minute
    @Scheduled(initialDelay = 0,fixedRate = 2 * 60 * 1000) // Runs every 2 minute
    public void processPendingEmails() {
        System.out.println(" public void processPendingEmails() {");
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledEmails> pendingEmails = emailRepository.findPendingEmails(new Date());

        System.out.println("Found "+pendingEmails.size()+" emails to send");
        for (ScheduledEmails email : pendingEmails) {
            try {
                System.out.println(LocalDateTime.now()+": To send scheduled email");
                email.setAttemptedSendAt(new Date());
                email.setLastAttemptStatus(null);
                emailRepository.save(email);
                Boolean status = customMailSender.sendEmail(email.getSubject(),
                        email.getBody(),
                        new String[]{email.getRecipient()}, new String[]{}, new String[]{}, email.getFromName());
//                customMailSender.sendEmail(email.getSubject(),
//                        email.getBody(),
//                        new String[]{"ngangagachuru919@gmail.com"}, new String[]{}, new String[]{});
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
