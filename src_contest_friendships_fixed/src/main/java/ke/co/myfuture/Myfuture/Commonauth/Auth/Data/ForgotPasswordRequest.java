package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest implements Serializable {
    private String email;
}
