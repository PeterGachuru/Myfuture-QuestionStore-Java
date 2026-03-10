package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequest implements Serializable {
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "password")
    private String password;
}
