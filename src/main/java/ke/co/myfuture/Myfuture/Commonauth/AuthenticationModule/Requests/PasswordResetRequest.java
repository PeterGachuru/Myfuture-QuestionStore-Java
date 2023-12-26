package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Requests;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.ValidationConstraints.Password;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordResetRequest {
    private String emailAddress;
    @Password
    private String password;
    private String confirmPassword;
}
