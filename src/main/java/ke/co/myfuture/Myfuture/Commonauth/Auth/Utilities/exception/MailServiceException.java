package ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.exception;

import org.springframework.mail.MailException;

public class MailServiceException extends MailException {
    public MailServiceException(String message){
        super(message);
    }
}
