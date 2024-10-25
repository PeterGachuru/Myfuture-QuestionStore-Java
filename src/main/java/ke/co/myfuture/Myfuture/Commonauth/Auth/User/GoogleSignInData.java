package ke.co.myfuture.Myfuture.Commonauth.Auth.User;

import lombok.Data;

@Data
public class GoogleSignInData {
    String displayName;
    String givenName;
    String familyName;
    String email;
    String photoUrl;
    Long installId;
}
