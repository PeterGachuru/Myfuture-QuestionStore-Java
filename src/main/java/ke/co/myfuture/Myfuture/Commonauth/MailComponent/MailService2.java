package ke.co.myfuture.Myfuture.Commonauth.MailComponent;

import ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions.MailServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service @Slf4j
public class MailService2 {

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.sender}")
    private String fromEmail;

    @Async
    public void sendEmail(String to, String message, String subject) throws MailServiceException {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setSentDate(new Date());
            mailMessage.setText(message);
            javaMailSender.send(mailMessage);
            log.info("Email Sent successfully.");
        }catch (MailSendException e) {
            throw new MailServiceException("Error while sending email. Email provided is either invalid or mail service is down. Please contact your system admin for more assistance.");
        }catch (MailAuthenticationException ignored){
            throw new MailServiceException("Configured mail credentials are invalid. Please contact your system admin");
        }catch (MailParseException ignored){
            throw new MailServiceException("Mail content failed to parse or the email has an invalid format");
        }catch (MailPreparationException ignored) {
            throw new MailServiceException("Error in email preparation");
        }catch (MailException e) {
            throw new MailServiceException(Objects.requireNonNull(e.getMessage()).split(";")[0]);
        }
    }
}
