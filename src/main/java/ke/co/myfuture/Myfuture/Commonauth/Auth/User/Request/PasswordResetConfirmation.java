package ke.co.myfuture.Myfuture.Commonauth.Auth.User.Request;


import lombok.Data;

@Data
public class PasswordResetConfirmation {
    String otp;
    String email;
    String newPassword;
}
