package ke.co.myfuture.Myfuture.Commonauth.Auth.User.PasswordReset;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Otp.OtpService;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.User;
import ke.co.myfuture.Myfuture.Commonauth.Auth.User.UserRepository;
import ke.co.myfuture.Myfuture.Commonauth.Auth.UserPasswords.UserPassword;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Utilities.PasswordUtil;
import ke.co.myfuture.Myfuture.Commonauth.Utils.CustomMailSender;
import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails.SchedulerService.isValidEmail;

@Service
@AllArgsConstructor
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;

    private final UserRepository userRepository;

    private final PasswordUtil passwordUtil;

    private final OtpService otpService;

    @Autowired
    private CustomMailSender customMailSender;


    public boolean validateResetOtp(String email, String otp) {
        Optional<PasswordResetDTO> reset = passwordResetRepository.findByEmailAndOtp(email, otp);
        if (reset.isPresent()) {
            Timestamp creationTime = reset.get().getCreationDate();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            long diffInMillis = now.getTime() - creationTime.getTime();
            long diffInMinutes = diffInMillis / (60 * 1000);

            return diffInMinutes <= 10; // Valid for 10 minutes
        }
        return false;
    }


    public AuthEntityResponse updateUserPassword(@NonNull String email, @NonNull String previousPassword,
                                                 @NonNull String password) {
        AtomicReference<AuthEntityResponse> response = new AtomicReference<>();

        this.userRepository.findByEmail(email).ifPresentOrElse(userData -> {
            if (Objects.equals(userData.getStatus(), "Active")) {

                if (passwordUtil.matches(previousPassword.trim(),
                        userData.getPasswords().get(userData.getPasswords().size() - 1).getPassword())) {

                    List<UserPassword> passwords = userData.getPasswords();
                    String encodedPassword = passwordUtil.encode(password);

                    boolean passwordExists =
                            passwords.stream().anyMatch(userPassword -> userPassword.getPassword().equals(encodedPassword));

                    if (passwordExists) {
                        response.set(AuthEntityResponse.builder().message("New password cannot equal old password!").statusCode(HttpStatus.FORBIDDEN.value()).build());
                        return;
                    } else {
                        UserPassword userPassword = new UserPassword();
                        userPassword.setPassword(encodedPassword);

                        if (passwords.size() == 12) {
                            passwords.remove(0);
                        }

                        passwords.add(userPassword);
                        userData.setPasswords(passwords);
                    }

                    userData.setFirstLogin(0);
                    userRepository.save(userData);

                    response.set(AuthEntityResponse.builder().message("Password updated successfully !").statusCode(HttpStatus.OK.value()).build());
//                    audit.log("USERS ACCOUNT", "Updating own password");
                } else {
                    response.set(AuthEntityResponse.builder().message("The previous  password you provided is " +
                            "incorrect !").statusCode(HttpStatus.BAD_REQUEST.value()).build());
                }

            } else {


                response.set(AuthEntityResponse.builder().message(String.format("Account with the email %s is not " +
                        "active ", email)).statusCode(HttpStatus.BAD_REQUEST.value()).build());

            }
        }, () -> {
            /* todo:: User not found  */
            response.set(AuthEntityResponse.builder().message(String.format("Account with the email %s not found ",
                    email)).statusCode(HttpStatus.BAD_REQUEST.value()).build());
        });

        return response.get();
    }

    public UniversalResponse resetPasswordWithOtp(String email, String otp, String newPassword) {

        Optional<PasswordResetDTO> resetOpt = passwordResetRepository.findByEmailAndOtp(email, otp);

        if (resetOpt.isEmpty()) {
            return new UniversalResponse(400, "Invalid OTP");
        }

        PasswordResetDTO reset = resetOpt.get();

        // Check expiry
        long diffInMillis = System.currentTimeMillis() - reset.getCreationDate().getTime();
        long diffInMinutes = diffInMillis / (60 * 1000);

        if (diffInMinutes > 10) {
            return new UniversalResponse(400, "OTP expired");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new UniversalResponse(400, "User not found");
        }

        User user = userOpt.get();

        if (!"Active".equals(user.getStatus())) {
            return new UniversalResponse(400, "Account not active");
        }

        // Encode new password
        String encodedPassword = passwordUtil.encode(newPassword);

        List<UserPassword> passwords = user.getPasswords();

        boolean exists = passwords.stream()
                .anyMatch(p -> p.getPassword().equals(encodedPassword));

        if (exists) {
            return new UniversalResponse(400, "Cannot reuse old password");
        }

        UserPassword newPass = new UserPassword();
        newPass.setUser(user);
        newPass.setPassword(encodedPassword);

        if (passwords.size() == 12) {
            passwords.remove(0);
        }

        passwords.add(newPass);
        user.setPasswords(passwords);

        userRepository.save(user);

        // OPTIONAL: delete OTP after use
        passwordResetRepository.delete(reset);

        return new UniversalResponse(200, "Password reset successful");
    }

    public UniversalResponse passwordResetRequest(PasswordResetDTO passwordResetDTO) {

        if (!isValidEmail(passwordResetDTO.getEmail())) return null;
        String resetCode = otpService.generateOTP();
        passwordResetDTO.setOtp(resetCode);

        String subject = "Password Reset Code - Ibuka Technologies";
        String htmlContent = generatePasswordResetEmailContent(resetCode);

        System.out.println("OTP: "+resetCode);


        passwordResetRepository.save(passwordResetDTO);

        Boolean status = customMailSender.sendEmail(subject,
                htmlContent,
                new String[]{passwordResetDTO.getEmail()}, new String[]{}, new String[]{}, "Ibuka Technologies");

        if (status) {
            return new UniversalResponse(200, "Sent password reset email");
        }
        return new UniversalResponse(500, "Server error");
    }

    private String generatePasswordResetEmailContent(String resetCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
                ".container { background: white; padding: 20px; border-radius: 5px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); }" +
                "h2 { color: #2C3E50; }" +
                "p { font-size: 16px; }" +
                ".code { font-size: 24px; font-weight: bold; color: #e74c3c; background: #f8d7da; padding: 10px; border-radius: 5px; display: inline-block; }" +
                "</style></head>" +
                "<body>" +
                "<div class='container'>" +
                "<h2>Password Reset Request</h2>" +
                "<p>Hello,</p>" +
                "<p>We received a request to reset your password for your Ibuka Technologies account. Use the following code to reset your password:</p>" +
                "<p class='code'>" + resetCode + "</p>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>Thank you,</p>" +
                "<p><strong>Ibuka Technologies Team</strong></p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
