package ke.co.myfuture.Myfuture.Commonauth.Auth.Data.User;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserData implements Serializable {
    @Builder.Default
    private Long id = null;
    @Builder.Default
    private String loggedSystem = null;
    @Builder.Default
    private String loggedDomain = null;

    @Builder.Default
    private String email = null;
    @Builder.Default
    private String phoneNumber = null;

    @Builder.Default
    private String fullName = null;
    @Builder.Default
    private String pictureUrl = null;

    @Builder.Default
    private String firstName = null;
    @Builder.Default
    private String county = null;

    @Builder.Default
    private String lastName = null;

    @Builder.Default
    private String password = null;

    @Builder.Default
    private String status = null;

    @Builder.Default
    private Integer firstLogin = null;

    @Builder.Default
    private Timestamp creationDate = null;

    @Builder.Default
    private Timestamp updateDate = null;

    @Builder.Default
    private Timestamp deletedDate = null;

    @Builder.Default
    private Integer isLoggedIn = null;

    @Builder.Default
    private Boolean hasAcceptedTerms = false;

    @Builder.Default
    private String resetPasswordToken = null;

    @Builder.Default
    private Timestamp resetPasswordTokenExpire = null;

    @Builder.Default
    private List<UserRoleData> roles = null;

    public String getUsername() {
        return this.email;
    }

    public void setUsername(String username) {
        this.email = username;
    }
}
