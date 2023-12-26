package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.Session;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Activesession {
    private UUID uuid;
    private String email;
    private String username;
    private String Status;
    private String loginAt;
    private String address;
    private String os;
    private String browser;
}
