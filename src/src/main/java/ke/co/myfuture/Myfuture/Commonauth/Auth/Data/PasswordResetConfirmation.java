package ke.co.myfuture.Myfuture.Commonauth.Auth.Data;


import lombok.Data;

@Data
public class PasswordResetConfirmation {
    String otp;
    String email;
    String newPassword;
}
