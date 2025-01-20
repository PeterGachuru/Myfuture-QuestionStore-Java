package ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.ScheduledLearnerEmails;


import ke.co.myfuture.Myfuture.Commonauth.Utils.CustomMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LearnerEmailSchedulerService {
    @Autowired
    private ScheduledLearnerEmailsRepo emailRepository;

    @Autowired
    private CustomMailSender customMailSender; // External service for sending emails

    // Save a new scheduled email
    public ScheduledLearnerEmails scheduleEmail(ScheduledLearnerEmails email) {
        return emailRepository.save(email);
    }

    // Periodically check for pending emails to send
//    @Scheduled(fixedRate = 60000) // Runs every minute
    public void processPendingEmails() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledLearnerEmails> pendingEmails = emailRepository.findPendingEmails(now);

        for (ScheduledLearnerEmails email : pendingEmails) {
            try {
                email.setAttemptedSendAt( LocalDateTime.now());
                email.setLastAttemptStatus(null);
                emailRepository.save(email);
//                customMailSender.sendEmail(email.getRecipient(), email.getSubject(), email.getBody()); // External service call
                email.setSent(true);
                email.setLastAttemptStatus(LastStatus.SUCCESS);
                email.setTimeSent(LocalDateTime.now());
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
