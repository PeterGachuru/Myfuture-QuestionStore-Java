package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginData implements Serializable {
    @Builder.Default
    private Long id = null;

    @Builder.Default
    private Boolean hasAcceptedTerms = false;

    @Builder.Default
    private String email = null;

    @Builder.Default
    private Integer firstLogin = null;

    @Builder.Default
    private String firstName = null;

    @Builder.Default
    private String lastName = null;

    @Builder.Default
    private List<UserRoleData> roles = null;

    @Builder.Default
    private String token = null;
}
