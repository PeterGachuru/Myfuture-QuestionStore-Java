package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

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
