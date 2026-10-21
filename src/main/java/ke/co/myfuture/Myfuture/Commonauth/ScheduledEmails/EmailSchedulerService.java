package ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails;

import ke.co.myfuture.Myfuture.Commonauth.Utils.CustomMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EmailSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(EmailSchedulerService.class);
    @Autowired
    private ScheduledEmailsRepo emailRepository;

    @Value("${production:false}")
    private boolean inProd;

    @Value("${email.delivery.enabled:true}")
    private boolean emailDeliveryEnabled;

    @Autowired
    private CustomMailSender customMailSender; // External service for sending emails

    // Save a new scheduled email
    public ScheduledEmails scheduleEmail(ScheduledEmails email) {
        return emailRepository.save(email);
    }

    // Periodically check for pending emails to send
//    @Scheduled(initialDelay = 0,fixedRate = 60000) // Runs every minute
    @Scheduled(initialDelay = 0, fixedRateString = "${email.delivery.scan-delay-ms:60000}")
    public void processPendingEmails() {
        log.info("Scheduled email delivery scan START: enabled={}, production={}", emailDeliveryEnabled, inProd);
        if (!emailDeliveryEnabled) {
            log.info("Scheduled email delivery disabled by email.delivery.enabled=false; queued emails will remain pending");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<ScheduledEmails> pendingEmails = emailRepository.findPendingEmails(new Date());
        log.info("Scheduled email delivery scan found {} pending emails at {}", pendingEmails.size(), now);
        for (ScheduledEmails email : pendingEmails) {
            try {
                log.info("Attempting scheduled email id={}, senderService={}, recipient={}", email.getId(), email.getSenderService(), maskEmail(email.getRecipient()));
                email.setAttemptedSendAt(new Date());
                email.setLastAttemptStatus(null);
                emailRepository.save(email);
//                Boolean status = customMailSender.sendEmail(email.getSubject(),
//                        email.getBody(),
//                        new String[]{email.getRecipient()}, new String[]{}, lifecycleBccList(email), new String[]{}, email.getFromName());

                Boolean status = customMailSender.sendEmail(email.getSubject(),
                        email.getBody(),
                        new String[]{email.getRecipient()}, new String[]{}, lifecycleBccList(email), new String[]{}, email.getFromName());

                email.setSent(status);
                email.setLastAttemptStatus(status? LastStatus.SUCCESS: LastStatus.FAILED);
                email.setTimeSent(new Date());
                emailRepository.save(email);
                log.info("Scheduled email id={} send result={}", email.getId(), status);
            } catch (EmailRateLimitExceededException e) {
                email.setAttemptedSendAt(null);
                email.setLastAttemptStatus(LastStatus.PENDING);
                emailRepository.save(email);
                log.warn("Hourly email limit reached; leaving scheduled email id={} pending", email.getId());
                break;
            } catch (Exception e) {
                // Handle email sending failure (e.g., logging)
                email.setLastAttemptStatus(LastStatus.FAILED);
                emailRepository.save(email);
                log.error("Failed to send scheduled email id={} recipient={}", email.getId(), maskEmail(email.getRecipient()), e);
            }
        }
        log.info("Scheduled email delivery scan COMPLETE");
    }

    private static String[] lifecycleBccList(ScheduledEmails email) {
        String category = email.getSenderService();
        if (category != null && (category.startsWith("Lifecycle") || category.equals("StudentLifecycle"))) {
            return new String[]{"ngangagachuru@gmail.com"};
        }
        return new String[0];
    }

    private static String maskEmail(String email) {
        if (email == null) return "<null>";
        int at = email.indexOf('@');
        if (at <= 1) return "***" + (at >= 0 ? email.substring(at) : "");
        return email.substring(0, 1) + "***" + email.substring(at);
    }
}
