package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.ValidationConstraints.Password;
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
