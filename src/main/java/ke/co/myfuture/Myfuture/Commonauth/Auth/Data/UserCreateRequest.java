package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String county;
    private Long roleId;
    private Long installId;
    private String role;
    private String password;
}
