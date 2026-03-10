package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserPasswordRequest implements Serializable {
    private String email;
    private String password;
}
