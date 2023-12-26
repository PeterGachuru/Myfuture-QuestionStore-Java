package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Responses;


import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Roles.Role;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class JwtResponse {
    private boolean otpEnabled = false;
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Set<Role> roles;
    private String solCode;
    private String entityId;
    private Character firstLogin;
    private UUID uuid;
    private String Status;
    private String loginAt;
    private String address;
    private String os;
    private String browser;
    private String tellerAc;
    private String memberCode;
    private Character isSystemGenPassword;
}
