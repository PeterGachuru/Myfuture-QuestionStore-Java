package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.MailService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailDto {
    private String to;
    private String subject;
    private String message;
}
