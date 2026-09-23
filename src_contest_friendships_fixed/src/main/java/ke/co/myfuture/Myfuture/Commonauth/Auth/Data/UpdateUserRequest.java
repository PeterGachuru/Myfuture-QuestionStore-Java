package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String county;
    private String phoneNumber;
}
