package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class SignupRequest {
    private Long sn;
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    private String roleFk;
//    @Password
    private String password;
    @NotBlank
    @Size(min = 3, max = 20)
    private String firstName;
    @NotBlank
    @Size(min = 3, max = 20)
    private String lastName;
    @NotBlank
    @Size(min = 10, max = 12)
    private String phoneNo;
    @NotBlank
    @Size(min=3, max = 6)
    private String solCode;
    @NotBlank
    @Size(min=3, max = 6)
    private String entityId;
    private String isTeller = "No";
    private String workclassFk;
    private String memberCode;
}