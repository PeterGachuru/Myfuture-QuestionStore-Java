package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Requests;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OTPCode {
    public Integer otp;
    public String username;
    public String email;
}
