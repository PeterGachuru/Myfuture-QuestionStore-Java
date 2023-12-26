package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.User;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest implements Serializable {
    private String firstName;
    private String lastName;
}
