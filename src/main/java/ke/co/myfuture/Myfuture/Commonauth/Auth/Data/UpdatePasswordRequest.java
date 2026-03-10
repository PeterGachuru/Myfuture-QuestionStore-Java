package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest implements Serializable {
    private String previousPassword;
    private String email;
    private String password;
}
