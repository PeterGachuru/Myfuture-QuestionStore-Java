package ke.co.myfuture.Myfuture.UserManagement.MailService;

import lombok.Data;

@Data
public class MailDto {
    private String to;
    private String subject;
    private String message;
}
