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

    @Value("${production}")
    private boolean inProd;
    @Autowired
    private CustomMailSender customMailSender; // External service for sending emails

    // Save a new scheduled email
    public ScheduledEmails scheduleEmail(ScheduledEmails email) {
        return emailRepository.save(email);
    }

    // Periodically check for pending emails to send
//    @Scheduled(initialDelay = 0,fixedRate = 60000) // Runs every minute
    @Scheduled(initialDelay = 0,fixedRate = 30 * 1000) // Runs every 30 seconds
    public void processPendingEmails() {
        System.out.println(" public void processPendingEmails() {");

//        if (!inProd) {
//            System.out.println("====Can't send scheduled emails because we are not in PROD=========");
//            return;
//        }

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
                        new String[]{"ngangagachuru001@gmail.com"}, new String[]{}, new String[]{}, email.getFromName());

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
