package ke.co.myfuture.Myfuture.Commonauth.CustomerExceptions;

import org.springframework.mail.MailException;

public class MailServiceException extends MailException {
    public MailServiceException(String message){
        super(message);
    }
}
